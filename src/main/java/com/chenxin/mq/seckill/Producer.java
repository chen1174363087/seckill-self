package com.chenxin.mq.seckill;

import com.alibaba.fastjson.JSON;
import com.chenxin.common.CommConfig;
import com.chenxin.entity.CxOrder;
import com.chenxin.service.impl.OrderServiceImpl;
import com.chenxin.util.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

@Component
public class Producer {
    private final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private RedisUtils redisUtils;


    private static final Logger LOGGER = LoggerFactory.getLogger(OrderServiceImpl.class);

    /**
     * 另一种方式
     */
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public String buildCreateOrder(CxOrder cxOrder) {
        String msg = "排队失败，稍后重试！";
        //库存
        String stockValue = redisUtils.get(CommConfig.SEC_KILL_STOCK);
        LOGGER.info("秒杀库存是：{}", stockValue);
        int stock = Integer.valueOf(stockValue);
        if (StringUtils.isEmpty(stock)) {
            return msg;
        }

        if (stock <= 0) {
            msg = "该轮已售罄，敬请期待！";
            LOGGER.error("{}", msg);
            return msg;
        }
        /**
         * 构建Message ,主要是使用 orderId 将 message 和 CorrelationData 关联起来。
         * 这样当消息发送到交换机失败的时候，在 MsgSendConfirmCallBack 中就可以根据
         * correlationData.getId()即 orderId,知道具体是哪个message发送失败,进而进行处理。
         */
        /*将 msgId和 message绑定*/
        String id = UUID.randomUUID().toString();
        Message message = MessageBuilder.withBody(JSON.toJSONString(cxOrder).getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setCorrelationId(id).build();

        /* 将 orderId和 CorrelationData绑定
         *  以便确定该order是否发送成功
         */
        CorrelationData correlationData = new CorrelationData(id);
        rabbitTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE, MQConfig.TOPIC_KEY, message, correlationData);
        try {
            logger.info("MQ send message {} ", new String(message.getBody(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        msg = "申请成功，订单生成中！";

        return msg;
    }

    public void buildAutoCancelOrder(CxOrder cxOrder) {
        /**
         * 构建Message ,主要是使用 orderId 将 message 和 CorrelationData 关联起来。
         * 这样当消息发送到交换机失败的时候，在 MsgSendConfirmCallBack 中就可以根据
         * correlationData.getId()即 orderId,知道具体是哪个message发送失败,进而进行处理。
         */
        /*将 msgId和 message绑定*/
        String id = UUID.randomUUID().toString();
        Message message = MessageBuilder.withBody(JSON.toJSONString(cxOrder).getBytes())
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .setCorrelationId(id).build();

        /* 将 orderId和 CorrelationData绑定
         *  以便确定该order是否发送成功
         */
        CorrelationData correlationData = new CorrelationData(id);
        rabbitTemplate.convertAndSend(MQConfig.ORDER_CANCEL_TOPIC_EXCHANGE, MQConfig.ORDER_CANCEL_TOPIC_KEY, message, correlationData);
        try {
            logger.info("MQ send message {} ", new String(message.getBody(), "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
