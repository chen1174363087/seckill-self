package com.chenxin.mq.seckill;

import com.alibaba.fastjson.JSON;
import com.chenxin.entity.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Producer {
    private final Logger logger = LoggerFactory.getLogger(Producer.class);

    /**
     * 另一种方式
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void buildCreateOrder(Order order) {
        /**
         * 构建Message ,主要是使用 orderId 将 message 和 CorrelationData 关联起来。
         * 这样当消息发送到交换机失败的时候，在 MsgSendConfirmCallBack 中就可以根据
         * correlationData.getId()即 orderId,知道具体是哪个message发送失败,进而进行处理。
         */
        /*将 msgId和 message绑定*/
        String id = UUID.randomUUID().toString();
        Message message = MessageBuilder.withBody(JSON.toJSONString(order).getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setCorrelationId(id).build();

        /* 将 orderId和 CorrelationData绑定
         *  以便确定该order是否发送成功
         */
        CorrelationData correlationData = new CorrelationData(id);
        rabbitTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.TOPIC_KEY, message, correlationData);
        logger.info("MQ send message {} ", new String(message.getBody()));
    }

    public void buildAutoCancelOrder(Order order) {
        /**
         * 构建Message ,主要是使用 orderId 将 message 和 CorrelationData 关联起来。
         * 这样当消息发送到交换机失败的时候，在 MsgSendConfirmCallBack 中就可以根据
         * correlationData.getId()即 orderId,知道具体是哪个message发送失败,进而进行处理。
         */
        /*将 msgId和 message绑定*/
        String id = UUID.randomUUID().toString();
        Message message = MessageBuilder.withBody(JSON.toJSONString(order).getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setCorrelationId(id).build();

        /* 将 orderId和 CorrelationData绑定
         *  以便确定该order是否发送成功
         */
        CorrelationData correlationData = new CorrelationData(id);
        rabbitTemplate.convertAndSend(MQConfig.ORDER_CANCEL_TOPIC_EXCHANGE, MQConfig.ORDER_CANCEL_TOPIC_KEY, message, correlationData);
        logger.info("MQ send message {} ", new String(message.getBody()));
    }
}
