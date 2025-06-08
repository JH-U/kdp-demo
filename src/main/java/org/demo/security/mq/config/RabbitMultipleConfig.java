package org.demo.security.mq.config;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * TODO
 *
 * @author marin
 */
@Configuration
public class RabbitMultipleConfig {

    // 主要RabbitMQ配置
    @Primary
    @Bean("primaryConnectionFactory")
    public ConnectionFactory primaryConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("192.168.1.110");
        factory.setPort(10053);
        factory.setUsername("subway_assets");
        factory.setPassword("cNoXmUf2Dw8R");
        factory.setVirtualHost("rdp");
        return factory;
    }

    @Primary
    @Bean("primaryRabbitTemplate")
    public RabbitTemplate primaryRabbitTemplate(@Qualifier("primaryConnectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Primary
    @Bean("primaryRabbitAdmin")
    public RabbitAdmin primaryRabbitAdmin(@Qualifier("primaryConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        //禁止自动声明，防止扫描到的Bean自动创建队列或者交换机，这里只针对被rabbitAdmin注册的才创建
        rabbitAdmin.setAutoStartup(false);
        return rabbitAdmin;
    }

    @Primary
    @Bean("primaryRabbitListenerContainerFactory")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> primaryRabbitListenerContainerFactory(
            @Qualifier("primaryConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        // 高级配置
        factory.setPrefetchCount(10);                           // 预取数量
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);     // 手动确认
        factory.setConcurrentConsumers(5);                      // 初始消费者数量
        factory.setMaxConcurrentConsumers(10);                  // 最大消费者数量
        factory.setStartConsumerMinInterval(10000L);             // 启动消费者最小间隔
        factory.setStopConsumerMinInterval(60000L);              // 停止消费者最小间隔

        // 重试配置
        factory.setDefaultRequeueRejected(false);               // 拒绝消息时是否重新入队
        return factory;
    }

    // 第二个RabbitMQ配置
    @Bean("secondaryConnectionFactory")
    public ConnectionFactory secondaryConnectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setPort(5672);
        factory.setUsername("guest");
        factory.setPassword("guest");
        factory.setVirtualHost("/");
        return factory;
    }

    @Bean("secondaryRabbitTemplate")
    public RabbitTemplate secondaryRabbitTemplate(@Qualifier("secondaryConnectionFactory") ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean("secondaryRabbitAdmin")
    public RabbitAdmin secondaryRabbitAdmin(@Qualifier("secondaryConnectionFactory") ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(false);
        return rabbitAdmin;
    }

    @Bean("secondaryRabbitListenerContainerFactory")
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> secondaryRabbitListenerContainerFactory(
            @Qualifier("secondaryConnectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }

}
