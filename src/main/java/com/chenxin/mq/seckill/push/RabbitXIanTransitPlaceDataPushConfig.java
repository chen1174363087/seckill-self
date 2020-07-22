package com.chenxin.mq.seckill.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Auther chenxin4
 * @Date 2020/7/20
 * Description 这种配置要好一点
 */
@Configuration
public class RabbitXIanTransitPlaceDataPushConfig {
    public static final String XIAN_TRAN_PLA_LUG_ORDER_QUEUE = "com.ticket.xianTranPlaLugOrder.queue";
    public static final String XIAN_TRAN_PLA_LUG_ORDER_EXCHANGE = "com.ticket.xianTranPlaLugOrder.exchange";
    public static final String XIAN_TRAN_PLA_LUG_ORDER_ROUTING_KEY = "com.ticket.xianTranPlaLugOrder.routing.key";

    //这个队列会存下单，和下单成功的数据（重复下单相当于查询）
    public static final String XIAN_TRAN_PLA_LUG_REFUND_QUEUE = "com.ticket.xianTranPlaLugRefund.queue";
    public static final String XIAN_TRAN_PLA_LUG_REFUND_EXCHANGE = "com.ticket.xianTranPlaLugRefund.exchange";
    public static final String XIAN_TRAN_PLA_LUG_REFUND_ROUTING_KEY = "com.ticket.xianTranPlaLugRefund.routing.key";

    public static final String XIAN_TRANSIT_PLACE_BIGGIFT_QUEUE = "com.ticket.xianTransitPlaceBigGift.queue";
    public static final String XIAN_TRANSIT_PLACE_BIGGIFT_EXCHANGE = "com.ticket.xianTransitPlaceBigGift.exchange";
    public static final String XIAN_TRANSIT_PLACE_BIGGIFT_ROUTING_KEY = "com.ticket.xianTransitPlaceBigGift.routing.key";

    private static final Logger LOGGER = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private CachingConnectionFactory connectionFactory;

    @Bean
    public DirectExchange luggageExchange() {
        return new DirectExchange(XIAN_TRAN_PLA_LUG_ORDER_EXCHANGE);
    }

    @Bean
    public Queue luggageQueue() {
        return new Queue(XIAN_TRAN_PLA_LUG_ORDER_QUEUE);
    }

    @Bean
    public Binding bindingLuggageExchangeMessage() {
        return BindingBuilder.bind(luggageQueue()).to(luggageExchange()).with(XIAN_TRAN_PLA_LUG_ORDER_ROUTING_KEY);
    }

    @Bean
    public DirectExchange luggageRefundExchange() {
        return new DirectExchange(XIAN_TRAN_PLA_LUG_REFUND_EXCHANGE);
    }

    @Bean
    public Queue luggageRefundQueue() {
        return new Queue(XIAN_TRAN_PLA_LUG_REFUND_QUEUE);
    }

    @Bean
    public Binding bindingLuggageExchangeRefundMessage() {
        return BindingBuilder.bind(luggageRefundQueue()).to(luggageRefundExchange()).with(XIAN_TRAN_PLA_LUG_REFUND_ROUTING_KEY);
    }

    @Bean
    public DirectExchange bigGiftExchange() {
        return new DirectExchange(XIAN_TRANSIT_PLACE_BIGGIFT_EXCHANGE);
    }

    @Bean
    public Queue bigGiftQueue() {
        return new Queue(XIAN_TRANSIT_PLACE_BIGGIFT_QUEUE);
    }

    @Bean
    public Binding bindingBigGiftExchangeMessage() {
        return BindingBuilder.bind(bigGiftQueue()).to(bigGiftExchange()).with(XIAN_TRANSIT_PLACE_BIGGIFT_ROUTING_KEY);
    }

    @Bean("xianRabbitListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        connectionFactory.setPublisherReturns(true);
        connectionFactory.setPublisherConfirms(true);
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        factory.setConcurrentConsumers(1);
        factory.setPrefetchCount(1);
        factory.setMaxConcurrentConsumers(1);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL); //手动确认消息消费状态
        return factory;
    }

    @Bean(value = "xianTransitPlaceRabbitTemplate")
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true);
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if (!ack) { //推送到exchange失败
                LOGGER.error("中转地为西安，推送数据到exchange失败，correlationDat：【{}】", correlationData.toString());
            }
        });
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() { //推送到Queue失败才会回调
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                LOGGER.error("中转地为西安，推送数据，消息丢失:exchange：【{}】,route：【{}】,replyCode：【{}】,replyText：【{}】,message:【{}】", exchange, routingKey, replyCode, replyText, message);
            }
        });
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }


}
