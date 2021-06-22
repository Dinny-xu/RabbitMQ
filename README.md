## RabbitMQ 多源配置

在本地安装RabbitMQ服务或者任意个人服务器安装server

#### 以Centos7为例

- 系统环境

    - JDK1.8
    - Centos7-64
    - Erlang-OTP 23
    - RabbitMQ-3.8.5



#### 安装Erlang

- 通过rpm 安装Erlang

  ```sh
  curl -s https://packagecloud.io/install/repositories/rabbitmq/erlang/script.rpm.sh | sudo bash
  ```

- 安装Erlang

  ```sh
  yum install -y erlang
  ```

- 查看erl版本号

  ```sh
  erl
  ```

![](https://cdn.xycloud.site/iShot2021-06-18%2010.56.23.png)

- Erlang 安装完成

#### 安装RabbitMQ

- 导入key

```sh
rpm --import https://packagecloud.io/rabbitmq/rabbitmq-server/gpgkey
rpm --import https://packagecloud.io/gpg.key
```

- 设置RabbitMQ 前置条件

```sh
curl -s https://packagecloud.io/install/repositories/rabbitmq/rabbitmq-server/script.rpm.sh | sudo bash
```

- 下载RabbitMQ

```sh
https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.8.5/rabbitmq-server-3.8.5-1.el7.noarch.rpm
```

- 访问链接下载后，将rpm包上传至服务器-导入key

```sh
rpm --import https://www.rabbitmq.com/rabbitmq-release-signing-key.asc
```

- 安装socat

```sh
yum -y install epel-release
yum -y install socat
```

- 安装RabbitMQ rpm 文件

```sh
rpm -ivh rabbitmq-server-3.8.5-1.el7.noarch.rpm
```

- 启用管理平台插件，启用插件后，可以可视化管理RabbitMQ

```sh
rabbitmq-plugins enable rabbitmq_management
```

- 启动MQ

```sh
systemctl start rabbitmq-server
```

- 访问控制图形化界面 -> IP:15672

![](https://cdn.xycloud.site/iShot2021-06-18%2011.06.28.png)

- MQ 默认账号guest 密码 guest
- 创建专属账号进行赋权使用-> 账号:admin 密码:admin

```sh
rabbitmqctl add_user admin admin
```

- 设置admin为超级管理员

```sh
rabbitmqctl set_user_tags admin administrator
```

- 授权远程访问（也可以登录后，可视化配置）

```sh
rabbitmqctl set_permissions -p / admin "." "." ".*"
```

- 创建完成后，重启RabbitMQ

```sh
systemctl restart rabbitmq-server
```

## 代码配置

#### 引入依赖

```java
<!-- rabbit mq--><dependency>    <groupId>org.springframework.boot</groupId>    <artifactId>spring-boot-starter-amqp</artifactId>    <version>2.2.10.RELEASE</version></dependency>
```

#### application.yml 配置

```yaml
spring:  port: 8088  rabbitmq:    first:      host: 42.156.222.164      port: 5672      username: admin      password: admin        #消费端配置      listener:        simple:          concurrency: 10  #消费端          max-concurrency: 20 #最大消费端数          acknowledge-mode: auto #自动签收auto  手动 manual          prefetch: 1 #限流（海量数据，同时只能过来一条）    second:      host: localhost      port: 5672      username: admin      password: admin
```

#### 程序启动类

```java
package com.study;import org.springframework.boot.SpringApplication;import org.springframework.boot.autoconfigure.SpringBootApplication;@SpringBootApplicationpublic class MqApplication {    public static void main(String[] args) {        SpringApplication.run(MqApplication.class, args);    }}
```

#### RabbitMQ 配置类

```java
package com.study.mq.rabbitmqConfig;import org.springframework.amqp.core.Binding;import org.springframework.amqp.core.BindingBuilder;import org.springframework.amqp.core.DirectExchange;import org.springframework.amqp.core.Queue;import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;import org.springframework.amqp.rabbit.connection.ConnectionFactory;import org.springframework.amqp.rabbit.core.RabbitTemplate;import org.springframework.beans.factory.annotation.Qualifier;import org.springframework.beans.factory.annotation.Value;import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;import org.springframework.context.annotation.Bean;import org.springframework.context.annotation.Configuration;import org.springframework.context.annotation.Primary;@Configurationpublic class RabbitConfig {    @Bean(name = "firstConnectionFactory")    @Primary    public ConnectionFactory firstConnectionFactory(            @Value("${spring.rabbitmq.first.host}") String host,            @Value("${spring.rabbitmq.first.port}") int port,            @Value("${spring.rabbitmq.first.username}") String username,            @Value("${spring.rabbitmq.first.password}") String password    ) {        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();        connectionFactory.setHost(host);        connectionFactory.setPort(port);        connectionFactory.setUsername(username);        connectionFactory.setPassword(password);        return connectionFactory;    }    @Bean(name = "secondConnectionFactory")    public ConnectionFactory secondConnectionFactory(            @Value("${spring.rabbitmq.second.host}") String host,            @Value("${spring.rabbitmq.second.port}") int port,            @Value("${spring.rabbitmq.second.username}") String username,            @Value("${spring.rabbitmq.second.password}") String password    ) {        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();        connectionFactory.setHost(host);        connectionFactory.setPort(port);        connectionFactory.setUsername(username);        connectionFactory.setPassword(password);        return connectionFactory;    }    @Bean(name = "firstRabbitTemplate")    @Primary    public RabbitTemplate firstRabbitTemplate(            @Qualifier("firstConnectionFactory") ConnectionFactory connectionFactory    ) {        return new RabbitTemplate(connectionFactory);    }    @Bean(name = "secondRabbitTemplate")    public RabbitTemplate secondRabbitTemplate(            @Qualifier("secondConnectionFactory") ConnectionFactory connectionFactory    ) {        return new RabbitTemplate(connectionFactory);    }    @Bean(name = "firstFactory")    public SimpleRabbitListenerContainerFactory firstFactory(            SimpleRabbitListenerContainerFactoryConfigurer configurer,            @Qualifier("firstConnectionFactory") ConnectionFactory connectionFactory    ) {        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();        configurer.configure(factory, connectionFactory);        return factory;    }    @Bean(name = "secondFactory")    public SimpleRabbitListenerContainerFactory secondFactory(            SimpleRabbitListenerContainerFactoryConfigurer configurer,            @Qualifier("secondConnectionFactory") ConnectionFactory connectionFactory    ) {        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();        configurer.configure(factory, connectionFactory);        return factory;    }}
```

#### 消费者1

```java
package com.study.mq.Receiver;import cn.hutool.json.JSONObject;import cn.hutool.json.JSONUtil;import com.study.mq.web.bean.entity.MqMsg;import com.study.mq.web.mapper.MqMsgMapper;import org.springframework.amqp.rabbit.annotation.RabbitHandler;import org.springframework.amqp.rabbit.annotation.RabbitListener;import org.springframework.stereotype.Component;import javax.annotation.Resource;/** * RabbitMQ中的消费者，接收first RabbitMQ中的队列first的数据 */@Componentpublic class Receiver {    @Resource    private MqMsgMapper mapper;    @RabbitListener(queues = "first", containerFactory = "firstFactory")    @RabbitHandler    public void process(String msg) {        final JSONObject object = JSONUtil.parseObj(msg);        final String context = object.getStr("context");        System.out.println("Receiver : " + context);        final MqMsg mqMsg = new MqMsg();        mqMsg.setContext(context);        mapper.insert(mqMsg);    }}
```

#### 消防者2

```java
package com.study.mq.Receiver;import org.springframework.amqp.rabbit.annotation.RabbitHandler;import org.springframework.amqp.rabbit.annotation.RabbitListener;import org.springframework.stereotype.Component;/** * RabbitMQ中的消费者，接收second RabbitMQ中的队列second的数据 */@Componentpublic class Receiver2 {    @RabbitListener(queues = "second", containerFactory = "secondFactory")    @RabbitHandler    public void process(String msg) {        System.out.println("Receiver : " + msg);    }}
```

#### 生产者1

```java
package com.study.mq.sender;import java.util.Date;import java.util.HashMap;import javax.annotation.Resource;import cn.hutool.core.map.MapUtil;import cn.hutool.json.JSONUtil;import org.springframework.amqp.rabbit.core.RabbitTemplate;import org.springframework.stereotype.Component;/** * RabbitMQ中的生产者，发送消息到RabbitMQ中first队列 */@Componentpublic class FirstSender {    @Resource(name="firstRabbitTemplate")    private RabbitTemplate firstRabbitTemplate;    public void send1() {        final HashMap<Object, Object> map = MapUtil.newHashMap();        map.put("context", "一条消息");        final String msg = JSONUtil.toJsonStr(map);        this.firstRabbitTemplate.convertAndSend("first","firstDirectRouting", msg);    }}
```

#### 生产者2

```java
package com.study.mq.sender;import org.springframework.amqp.rabbit.core.RabbitTemplate;import org.springframework.web.bind.annotation.GetMapping;import org.springframework.web.bind.annotation.RestController;import javax.annotation.Resource;import java.util.Date;/** * RabbitMQ中的生产者，发送消息到RabbitMQ中的second队列 *///@Component@RestControllerpublic class SecondSender {    @Resource(name = "secondRabbitTemplate")    private RabbitTemplate secondRabbitTemplate;    public void send1() {        String context = "第一次发送 " + new Date();        System.out.println("Sender : " + context);        this.secondRabbitTemplate.convertAndSend("second","secondRoutingKey", context);    }}
```

#### 程序测试

```java
package com.study.mq;import com.study.mq.sender.FirstSender;import com.study.mq.sender.SecondSender;import org.springframework.web.bind.annotation.GetMapping;import org.springframework.web.bind.annotation.RestController;import javax.annotation.Resource;@RestControllerpublic class TestDemo {    @Resource    private FirstSender firstSender;    @Resource    private SecondSender secondSender;    @GetMapping("/firstSend")    public void firstSend() throws Exception {        firstSender.send1();    }    @GetMapping("/secondSend")    public void secondSend() throws Exception {        secondSender.send1();    }}
```



- 启动项目, 调用 /firstSend 接口向第一个first 源发送一条消息

![](https://cdn.xycloud.site/iShot2021-06-18%2011.32.17.png)

- 测试项目时先注释消费者 @RabbitListener 监听注解，这样发送消息后不至于被马上消费
- 消息发送成功后，再放开@RabbitListener注释，重新启动项目可以查看到消息已被消费