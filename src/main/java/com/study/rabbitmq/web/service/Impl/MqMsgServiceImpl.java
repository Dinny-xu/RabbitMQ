package com.study.rabbitmq.web.service.Impl;

import com.study.rabbitmq.web.bean.entity.MqMsg;
import com.study.rabbitmq.web.mapper.MqMsgMapper;
import com.study.rabbitmq.web.service.MqMsgService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;

@Service
public class MqMsgServiceImpl implements MqMsgService {

    @Resource
    private MqMsgMapper mqMsgMapper;


}
