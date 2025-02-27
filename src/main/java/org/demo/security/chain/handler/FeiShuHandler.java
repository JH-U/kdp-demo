package org.demo.security.chain.handler;

import org.demo.security.chain.MessageHandler;
import org.demo.security.chain.MessageRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(2)
@Component
public class FeiShuHandler extends MessageHandler {
    @Override
    protected boolean canHandle(MessageRequest request) {
        return true;
    }

    @Override
    protected void process(MessageRequest request) {
        System.out.println("执行飞书操作");
    }
}
