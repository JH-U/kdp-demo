package org.demo.security.test;

import java.util.HashMap;
import org.demo.security.common.web.model.Result;
import org.demo.security.common.web.model.ResultBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/public/login")
public class LoginController {

  @Value("${login.gitee.clientId}")
  private String giteeClientId;

  @Value("${login.gitee.redirectUri}")
  private String giteeCallbackEndpoint;

  @GetMapping("/config")
  public Result getA() {
    HashMap<String, Object> config = new HashMap<>();
    config.put("clientId", giteeClientId);
    config.put("redirectUri", giteeCallbackEndpoint);
    return ResultBuilder.aResult()
        .code(Result.SUCCESS_CODE)
        .data(config)
        .msg("SUCCESS")
        .build();
  }

}
