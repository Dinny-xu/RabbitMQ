package com.study.rabbitmq.web.bean.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Table;


@Getter
@Setter
@ToString
@Table(name = "mq_msg")
public class MqMsg {

    private Integer id;

    private String context;
}