package com.chenxin.mq.seckill;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chenxin.common.CommConfig;
import com.chenxin.entity.CxOrder;
import com.chenxin.enums.OrderStatusEnum;
import com.chenxin.service.IOrderService;
import com.chenxin.util.RedisUtils;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Component
public class Consumer {
    private final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Autowired
    private IOrderService orderService;

    @Autowired
    @Qualifier(value = "jedisTemplate")
    private Jedis jedis;

    @Autowired
    private Producer producer;

    @Autowired
    private RedisUtils redisUtils;

    /**
     * 下单
     *
     * @param message
     * @param channel
     */
    @RabbitListener(queues = {MQConfig.TOPIC_QUEUE})
    public void createOrder(Message message, Channel channel) {
        try {
            CxOrder cxOrder = json2Order(message);
            //查mysql库存-创建订单-减库存 一个事物中执行
            boolean created = orderService.createOrder(cxOrder);
            logger.info("MQ 创建订单结果：{}, username：{}", created, cxOrder.getUsername());
            if (created) {
                //自动取消订单死信队列
                producer.buildAutoCancelOrder(cxOrder);
                //将用户订单信息存redis，供用户查询
                String orderKey = new StringBuilder("seckill：")
                        .append(cxOrder.getUsername())
                        .append(cxOrder.getTelphone()).toString();
                redisUtils.sadd(orderKey, JSON.toJSONString(cxOrder));
            }

            /**
             * 第一个参数 deliveryTag：就是接受的消息的deliveryTag,可以通过msg.getMessageProperties().getDeliveryTag()获得
             * 第二个参数 multiple：如果为true，确认之前接受到的消息；如果为false，只确认当前消息。
             * 如果为true就表示连续取得多条消息才发会确认，和计算机网络的中tcp协议接受分组的累积确认十分相似，
             * 能够提高效率。
             *
             * 同样的，如果要nack或者拒绝消息（reject）的时候，
             * 也是调用channel里面的basicXXX方法就可以了（要指定tagId）。
             *
             * 注意：如果抛异常或nack（并且requeue为true），消息会重新入队列，
             * 并且会造成消费者不断从队列中读取同一条消息的假象。
             */
            // 确认消息
            // 如果 channel.basicAck   channel.basicNack  channel.basicReject 这三个方法都不执行，消息也会被确认
            // 所以，正常情况下一般不需要执行 channel.basicAck
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);//删除消费的消息
        } catch (Exception e) {
            logger.error("MQ 创建订单失败message：{} , error：{}", message, e);
            // 处理消息失败，将消息重新放回队列
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    /**
     * 定时取消订单
     *
     * @param message
     * @param channel
     */
    @RabbitListener(queues = {MQConfig.DEAD_LETTER_QUEUE})
    public void cancelOrder(Message message, Channel channel) {
        CxOrder cxOrder = null;
        try {
            cxOrder = json2Order(message);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        logger.info("定时取消订单：{}", cxOrder.getUsername());
        try {
            CxOrder oldOrder = new CxOrder();
            BeanUtils.copyProperties(cxOrder, oldOrder);

            cxOrder.setStatus(OrderStatusEnum.CANCEL.getStatus());
            orderService.saveOrUpdate(cxOrder);
            //更新用户订单信息，供用户查询
            String orderKey = new StringBuilder("seckill：")
                    .append(cxOrder.getUsername())
                    .append(cxOrder.getTelphone()).toString();
            redisUtils.srem(orderKey, JSON.toJSONString(oldOrder));
            redisUtils.sadd(orderKey, JSON.toJSONString(cxOrder));
            //向redis加入数据
//            jedisCluster.lpush("order_list", cxOrder.toString());

            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);//删除消费的消息
        } catch (Exception e) {
            //订单处理异常，消息重新入队
            try {
                channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
            } catch (IOException e1) {
                logger.error("定时取消订单异常，消息入队错误", e1);
            }
            logger.error("定时取消订单异常", e);
        }
    }

    public CxOrder json2Order(Message message) throws UnsupportedEncodingException {
        String body = new String(message.getBody(), "UTF-8");
        CxOrder cxOrder = JSONObject.parseObject(body, CxOrder.class);
        return cxOrder;
    }
}
