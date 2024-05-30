package org.demo.security.test;

import org.demo.security.common.web.model.Result;
import org.demo.security.common.web.model.ResultBuilder;
import org.demo.security.authentication.handler.login.UserLoginInfo;
import org.demo.security.common.web.util.JSON;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/open-api")
public class TestDemoController {

  @GetMapping("/business-1")
  public Result getA() {
    UserLoginInfo userLoginInfo = (UserLoginInfo)SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getPrincipal();
    System.out.println("登录信息：" + JSON.stringify(userLoginInfo));
    return ResultBuilder.aResult()
        .msg("${test.message.a:测试国际化消息 A}")
        .build();
  }

  @GetMapping("/business-2")
  public Result getB() {
    UserLoginInfo userLoginInfo = (UserLoginInfo)SecurityContextHolder
        .getContext()
        .getAuthentication()
        .getPrincipal();
    System.out.println("登录信息：" + JSON.stringify(userLoginInfo));
    return ResultBuilder.aResult()
        .msg("SUCCESS B")
        .build();
  }

  @GetMapping("/business-3")
  public Result getC() {
    Authentication authentication = SecurityContextHolder
        .getContext()
        .getAuthentication();
    System.out.println("登录信息：" + JSON.stringify(authentication));
    return ResultBuilder.aResult()
        .msg("匿名接口，所有人可公开访问")
        .build();
  }

}
