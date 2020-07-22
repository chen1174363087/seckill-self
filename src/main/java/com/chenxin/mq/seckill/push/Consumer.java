package com.chenxin.mq.seckill.push;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @Auther chenxin4
 * @Date 2020/7/20
 * Description
 */
@Component
public class Consumer {
    @Value("${xian.transit_place.address:https://mptest.cwagpss.com}")
    private String xianTransitPlaceAddress;

    @Value("${xian.transit_place.appCode:AS504}")
    private String xianTransitPlaceAppCode;

    @Value("${xian.transit_place.secretKey:cwagtest}")
    private String xianTransitPlaceSecretKey;

    @Value("${xian.transit_place.sKey:pco2naLPhSkkfdko}")
    private String xianTransitPlaceSKey;

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

    @RabbitListener(queues = RabbitXIanTransitPlaceDataPushConfig.XIAN_TRAN_PLA_LUG_ORDER_QUEUE, concurrency = "1",
            containerFactory = "xianRabbitListenerContainerFactory")
    public void consumeLuggageMessage(Message message, Channel channel) {
        int resCode = 0;
        try {
            // 确认消息
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);//删除消费的消息

        } catch (Exception e) {
            LOGGER.error("MQ 推送西安机场行李下单失败 message：{} , error：{}", message, e);
            // 处理消息失败，将消息重新放回队列
            try {
                if (resCode == 0) {
                    channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
