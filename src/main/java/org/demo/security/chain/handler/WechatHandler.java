package org.demo.security.chain.handler;

import org.demo.security.chain.MessageHandler;
import org.demo.security.chain.MessageRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(3)
@Component
public class WechatHandler extends MessageHandler {
    @Override
    protected boolean canHandle(MessageRequest request) {
        return true;
    }

    @Override
    protected void process(MessageRequest request) {
        System.out.println("执行微信操作");
        throw new RuntimeException("微信出错了");
    }
}
