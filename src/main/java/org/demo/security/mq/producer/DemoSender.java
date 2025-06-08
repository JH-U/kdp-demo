package org.demo.security.mq.producer;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author klein
 */
@Component
public class DemoSender {

    private final RabbitTemplate secondaryRabbitTemplate;

    public DemoSender(@Qualifier("secondaryRabbitTemplate") RabbitTemplate secondaryRabbitTemplate) {
        this.secondaryRabbitTemplate = secondaryRabbitTemplate;
    }

    public void sendAssetMessage(Object message) {
        secondaryRabbitTemplate.convertAndSend("secondary.exchange", "secondary.routing.key", message);
        System.out.println("Sent message: " + message + " with key: " + "secondary.routing.key");
    }
}