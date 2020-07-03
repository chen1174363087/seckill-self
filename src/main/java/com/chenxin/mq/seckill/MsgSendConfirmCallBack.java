package com.chenxin.mq.seckill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MsgSendConfirmCallBack implements RabbitTemplate.ConfirmCallback {
    private final Logger logger = LoggerFactory.getLogger(MsgSendConfirmCallBack.class);

    /**
     * 当消息发送到交换机（exchange）时，该方法被调用.
     * 1.如果消息没有到exchange,则 ack=false
     * 2.如果消息到达exchange,则 ack=true
     *
     * @param correlationData
     * @param ack
     * @param cause
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        if (ack) {
            logger.info("消息发送到exchange成功,orderId {} ", correlationData.getId());
            // TODO 删除 msgId 与 Message 的关系
        } else {
            logger.error("消息发送到exchange失败 {} ", correlationData.getId());
            // TODO 消息发送到exchange失败 ， 重新发送
        }
    }
}
