package org.demo.security.mq;

import lombok.RequiredArgsConstructor;
import org.demo.security.mq.producer.DemoSender;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author klein
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/mq")
public class MqTest {

    private final DemoSender sender;
    @PostMapping("test")
    public String mqTest(@RequestParam String msg) {
        sender.sendAssetMessage(msg);
        return msg;
    }
}
