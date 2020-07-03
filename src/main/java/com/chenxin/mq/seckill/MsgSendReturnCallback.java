package com.chenxin.mq.seckill;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MsgSendReturnCallback implements RabbitTemplate.ReturnCallback {
    private final Logger logger = LoggerFactory.getLogger(MsgSendReturnCallback.class);

    /**
     * 当消息从交换机到队列失败时，该方法被调用。（若成功，则不调用）
     * 需要注意的是：该方法调用后，MsgSendConfirmCallBack中的confirm方法也会被调用，且ack = true
     *
     * @param message
     * @param replyCode
     * @param replyText
     * @param exchange
     * @param routingKey
     */
    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        logger.error("MsgSendReturnCallback [消息从交换机到队列失败]  message：{}，exchange：{}", message, exchange);

        // TODO 消息从交换机到队列失败，重新发送

    }
}
