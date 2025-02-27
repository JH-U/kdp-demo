package org.demo.security.chain.config;

import org.demo.security.chain.MessageHandler;
import org.demo.security.chain.MessageHandlerChain;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.Collections;
import java.util.List;

@Configuration
public class MessageHandlerConfig {
    @Bean
    public MessageHandlerChain handlerChain(List<MessageHandler> handlerProvider) {
        // 按 @Order 注解值排序（支持动态增删处理器）
        handlerProvider.sort(AnnotationAwareOrderComparator.INSTANCE);
        for (int i = 0; i < handlerProvider.size() - 1; i++) {
            handlerProvider.get(i).setNext(handlerProvider.get(i + 1));
        }
        return new MessageHandlerChain(handlerProvider);
    }

    // 可选：条件装配（当存在至少一个处理器时才注册链）
    @ConditionalOnMissingBean(MessageHandler.class)
    @Bean
    public MessageHandlerChain defaultHandlerChain() {
        return new MessageHandlerChain(Collections.emptyList());
    }
}
