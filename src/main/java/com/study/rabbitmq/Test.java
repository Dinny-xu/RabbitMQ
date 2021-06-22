package com.study.rabbitmq;

import com.study.rabbitmq.sender.FirstSender;
import com.study.rabbitmq.sender.SecondSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
public class Test {

    @Resource
    private FirstSender firstSender;

    @Resource
    private SecondSender secondSender;


    @GetMapping("/firstSend")
    public void firstSend() throws Exception {
        firstSender.send1();
    }

    @GetMapping("/secondSend")
    public void secondSend() throws Exception {
        secondSender.send1();
    }
}