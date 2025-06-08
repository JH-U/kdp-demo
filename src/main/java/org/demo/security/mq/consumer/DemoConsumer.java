package org.demo.security.mq.consumer;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author klein
 */
@Component
@Slf4j
public class DemoConsumer {

    @RabbitListener(queues = "STORE.INFO.ASSETS", containerFactory = "primaryRabbitListenerContainerFactory")
    public void receive(String body, Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        System.out.println(body);
        channel.basicAck(tag, false);
    }
}
