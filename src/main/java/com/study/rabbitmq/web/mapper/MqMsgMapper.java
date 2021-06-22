package com.study.rabbitmq.web.mapper;

import com.study.rabbitmq.web.bean.entity.MqMsg;
import org.apache.ibatis.annotations.Mapper;
import tk.mybatis.mapper.common.BaseMapper;

@Mapper
public interface MqMsgMapper extends BaseMapper<MqMsg> {

}