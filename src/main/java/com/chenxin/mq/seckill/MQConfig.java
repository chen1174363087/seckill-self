package com.chenxin.mq.seckill;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MQConfig {
    public static final String TOPIC_KEY = "com.chenxin.seckill";
    public static final String TOPIC_QUEUE = "com.chenxin.seckill.queue";
    public static final String TOPIC_EXCHANGE = "com.chenxin.seckill.exchange";

    public static final String ORDER_CANCEL_TOPIC_KEY = "com.chenxin.seckill.orderCancel";
    public static final String ORDER_CANCEL_TOPIC_QUEUE = "com.chenxin.seckill.orderCancel.queue";
    public static final String ORDER_CANCEL_TOPIC_EXCHANGE = "com.chenxin.seckill.orderCancel.exchange";

    public static final String DEAD_LETTER_EXCHANGER = "com.chenxin.orderCancel.exchange";
    public static final String DEAD_LETTER_QUEUE = "com.chenxin.orderCancel.queue";
    public static final String DEAD_LETTER_ROUTINGKEY = "com.chenxin.orderCancel";

    @Value("${order.expire-time}")
    private long orderExpireTime;

    @Autowired
    private ConnectionFactory connectionFactory;

    @Autowired
    private MsgSendConfirmCallBack msgSendConfirmCallBack;

    @Autowired
    private MsgSendReturnCallback msgSendReturnCallback;

    /**
     * 死信exchange
     *
     * @return
     */
    @Bean
    public FanoutExchange dlxOrder() {
        return new FanoutExchange(MQConfig.DEAD_LETTER_EXCHANGER);
    }

    /**
     * 死信queue
     *
     * @return
     */
    @Bean
    public Queue dlxQueueOrder() {
        return new Queue(MQConfig.DEAD_LETTER_QUEUE);
    }

    //绑定
    @Bean
    public Binding dlxQueueBindOrder() {
        return BindingBuilder.bind(dlxQueueOrder()).to(dlxOrder());
    }


    /**
     * Topic模式 交换机Exchange
     */
    @Bean
    public TopicExchange topicExchangeOrder() {
        return new TopicExchange(MQConfig.ORDER_CANCEL_TOPIC_EXCHANGE, true, false);
    }

    /**
     * 死信队列，延迟处理（定时取消订单）
     *
     * @return
     */
    @Bean
    public Queue queueOrder() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 20000);
        args.put("x-dead-letter-exchange", MQConfig.DEAD_LETTER_EXCHANGER);
        args.put("x-dead-letter-routing-key", MQConfig.DEAD_LETTER_ROUTINGKEY);
        return new Queue(MQConfig.ORDER_CANCEL_TOPIC_QUEUE, true, false, false, args);
    }

    //绑定
    @Bean
    public Binding topicBinding() {
        return BindingBuilder.bind(queueOrder()).to(topicExchangeOrder()).with(MQConfig.ORDER_CANCEL_TOPIC_KEY);
    }

    /**
     * 创建订单消息 Topic模式 交换机Exchange
     */
    @Bean
    public TopicExchange createOrderExchange() {
        return new TopicExchange(MQConfig.TOPIC_EXCHANGE, true, false);
    }

    @Bean
    public Queue createOrderQueue() {
        return new Queue(MQConfig.TOPIC_QUEUE);
    }

    //绑定
    @Bean
    public Binding createOrderQueueBind() {
        return BindingBuilder.bind(createOrderQueue()).to(createOrderExchange()).with(TOPIC_KEY);
    }

//    /**
//     * 处理订单
//     */
//    @Bean
//    public TopicExchange orderCancelExchange() {
//        return new TopicExchange(MQConfig.ORDER_CANCEL_TOPIC_EXCHANGE, true, false);
//    }
//
//    /**
//     * 死信队列，延迟处理（定时取消订单）
//     *
//     * @return
//     */
//    @Bean
//    public Queue orderCancelQueue() {
//        Map<String, Object> args = new HashMap<>();
//        args.put("x-message-ttl", orderExpireTime);
//        args.put("x-dead-letter-exchange", MQConfig.DEAD_LETTER_EXCHANGER);
//        args.put("x-dead-letter-routing-key", MQConfig.DEAD_LETTER_ROUTINGKEY);
//        return new Queue(ORDER_CANCEL_TOPIC_QUEUE, true, false, false, args);
//    }
//
//    //绑定
//    @Bean
//    public Binding orderCancelQueueBind() {
//        return BindingBuilder.bind(createOrderQueue()).to(createOrderExchange()).with(ORDER_CANCEL_TOPIC_KEY);
//    }
//
//    /**
//     * 死信exchange
//     *
//     * @return
//     */
//    @Bean
//    public FanoutExchange orderDlx() {
//        return new FanoutExchange(MQConfig.DEAD_LETTER_EXCHANGER);
//    }
//
//    /**
//     * 死信queue
//     *
//     * @return
//     */
//    @Bean
//    public Queue orderDlxQueue() {
//        return new Queue(MQConfig.DEAD_LETTER_QUEUE);
//    }
//
//    //绑定
//    @Bean
//    public Binding orderDlxQueueBind() {
//        return BindingBuilder.bind(orderDlxQueue()).to(orderDlx());
//    }


    /**
     * 定义rabbit template用于数据的接收和发送
     * 可以设置消息确认机制和回调
     *
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
//         template.setMessageConverter(); 可以自定义消息转换器  默认使用的JDK的，所以消息对象需要实现Serializable
        template.setMessageConverter(new Jackson2JsonMessageConverter());

        /**若使用confirm-callback或return-callback，
         * 必须要配置publisherConfirms或publisherReturns为true
         * 每个rabbitTemplate只能有一个confirm-callback和return-callback
         */
        template.setConfirmCallback(msgSendConfirmCallBack);

        /**
         * 使用return-callback时必须设置mandatory为true，或者在配置中设置mandatory-expression的值为true，
         * 可针对每次请求的消息去确定’mandatory’的boolean值，
         * 只能在提供’return -callback’时使用，与mandatory互斥
         */
        template.setReturnCallback(msgSendReturnCallback);
        template.setMandatory(true);
        return template;
    }
    /**
     * 关于 msgSendConfirmCallBack 和 msgSendReturnCallback 的回调说明：
     * 1.如果消息没有到exchange,则confirm回调,ack=false
     * 2.如果消息到达exchange,则confirm回调,ack=true
     * 3.exchange到queue成功,则不回调return
     * 4.exchange到queue失败,则回调return(需设置mandatory=true,否则不回调,消息就丢了)
     */

    /**
     * 消息确认机制
     * Confirms给客户端一种轻量级的方式，能够跟踪哪些消息被broker处理，
     * 哪些可能因为broker宕掉或者网络失败的情况而重新发布。
     * 确认并且保证消息被送达，提供了两种方式：发布确认和事务。(两者不可同时使用)
     * 在channel为事务时，不可引入确认模式；同样channel为确认模式下，不可使用事务。
     * @return
     */

}
