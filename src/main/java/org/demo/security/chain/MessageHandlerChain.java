package org.demo.security.chain;

import org.apache.logging.log4j.message.Message;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

public class MessageHandlerChain {

    private final List<MessageHandler> handlers;

    public MessageHandlerChain(List<MessageHandler> handlers) {
        this.handlers = handlers;
    }

    public void handleMessage(MessageRequest message) {
        if (!CollectionUtils.isEmpty(handlers)) {
            handlers.get(0).handle(message);
        }
    }


}
