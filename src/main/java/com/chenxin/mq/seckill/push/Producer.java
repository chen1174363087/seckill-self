package com.chenxin.mq.seckill.push;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Objects;


/**
 * @Auther chenxin4
 * @Date 2020/7/20
 * Description
 */
@Component
public class Producer {
    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

    @Autowired
    @Qualifier("xianTransitPlaceRabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    /**
     * @param luggageOrderDto
     * @param orderAction     1:创建行程 2:退票3:改签 4:旅客取消
     */
    public void produceLuggage(Objects luggageOrderDto, Integer orderAction) {
        if (Objects.isNull(luggageOrderDto)) {
            return;
        }
        CorrelationData data = new CorrelationData("");
        Message message = null;
        try {
            message = MessageBuilder.withBody(JSON.toJSONString(luggageOrderDto).getBytes("UTF-8"))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setCorrelationId("").build();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        switch (orderAction) {
            case 1:
                rabbitTemplate.convertAndSend(
                        RabbitXIanTransitPlaceDataPushConfig.XIAN_TRAN_PLA_LUG_ORDER_EXCHANGE,
                        RabbitXIanTransitPlaceDataPushConfig.XIAN_TRAN_PLA_LUG_ORDER_ROUTING_KEY,
                        message, data);
                break;
            case 2:
                rabbitTemplate.convertAndSend(
                        RabbitXIanTransitPlaceDataPushConfig.XIAN_TRAN_PLA_LUG_REFUND_EXCHANGE,
                        RabbitXIanTransitPlaceDataPushConfig.XIAN_TRAN_PLA_LUG_REFUND_ROUTING_KEY,
                        message, data);
                break;
            default:
                break;
        }

    }
}
