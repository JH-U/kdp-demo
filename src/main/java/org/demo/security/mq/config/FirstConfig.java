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
public class FirstConfig {

    // 主要实例的队列和交换机配置
    @Bean
    public Queue primaryQueue(@Qualifier("primaryRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Queue queue = new Queue("primary.queue", true);
        rabbitAdmin.declareQueue(queue);
        return queue;
    }

    @Bean
    public TopicExchange primaryExchange(@Qualifier("primaryRabbitAdmin") RabbitAdmin rabbitAdmin) {
        TopicExchange exchange = new TopicExchange("primary.exchange");
        rabbitAdmin.declareExchange(exchange);
        return exchange;
    }

    @Bean
    public Binding primaryBinding(@Qualifier("primaryRabbitAdmin") RabbitAdmin rabbitAdmin) {
        Binding binding = BindingBuilder
                .bind(primaryQueue(rabbitAdmin))
                .to(primaryExchange(rabbitAdmin))
                .with("primary.routing.key");
        rabbitAdmin.declareBinding(binding);
        return binding;
    }
}
