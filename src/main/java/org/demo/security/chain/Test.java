package org.demo.security.chain;

import org.demo.security.authentication.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {

    @Autowired
    private MessageHandlerChain messageHandlerChain;

    @GetMapping("test")
    public void abc(){
        MessageRequest request = new MessageRequest();
        messageHandlerChain.handleMessage(request);
    }
}
