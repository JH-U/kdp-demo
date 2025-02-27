package org.demo.security.chain.handler;

import org.demo.security.chain.MessageHandler;
import org.demo.security.chain.MessageRequest;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
public class LogHandler extends MessageHandler {
    @Override
    protected boolean canHandle(MessageRequest request) {
        return true;
    }

    @Override
    protected void process(MessageRequest request) {
        System.out.println("执行日志操作");
        try {
            super.next(request);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("记录错误数据");
        }
    }

    @Override
    public void next(MessageRequest request) {
    }
}
