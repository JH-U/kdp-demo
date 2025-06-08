package org.demo.security.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author klein
 */
@Configuration
public class SecondConfig {

    // 第二个实例的队列和交换机配置
    @Bean
    public Queue secondaryQueue(@Qualifier("secondaryRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue("secondary.queue", true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    @Bean
    public TopicExchange secondaryExchange(@Qualifier("secondaryRabbitAdmin") RabbitAdmin rabbitAdmin) {
        TopicExchange exchange = new TopicExchange("secondary.exchange");
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }

    @Bean
    public Binding secondaryBinding(@Qualifier("secondaryRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder
                .bind(secondaryQueue(rabbitAdmin))
                .to(secondaryExchange(rabbitAdmin))
                .with("secondary.routing.key");
        rabbitAdmin.declareBinding(binding);
        return binding;
    }
}
