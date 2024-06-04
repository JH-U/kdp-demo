package org.demo.security.authentication.handler.login.gitee;

import java.util.HashMap;
import org.demo.security.authentication.handler.login.UserLoginInfo;
import org.demo.security.authentication.service.UserService;
import org.demo.security.common.web.exception.BaseException;
import org.demo.security.common.web.model.User;
import org.demo.security.common.web.util.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
public class GiteeAuthenticationProvider implements AuthenticationProvider {

  @Autowired
  private UserService userService;

  @Autowired
  private GiteeApiClient giteeApiClient;

  public static final String PLATFORM = "gitee";

  @Override
  public Authentication authenticate(Authentication authentication) throws AuthenticationException {
    String code = authentication.getCredentials().toString();
    try {
      String token = giteeApiClient.getTokenByCode(code);
      String openId = giteeApiClient.getUserOpenId(token);
      User user = userService.getUserByOpenId(openId, PLATFORM);
      boolean notBindAccount = user == null; // gitee账号没有绑定我们系统的用户

      if (notBindAccount) {
        // 如果是第一次使用gitee登录，可能需要创建一个新用户
        user = new User();
        userService.createUserWithOpenId(user, openId, PLATFORM);
        notBindAccount = true;
      }

      GiteeAuthentication successAuth = new GiteeAuthentication();
      successAuth.setCurrentUser(JSON.convert(user, UserLoginInfo.class));
      successAuth.setAuthenticated(true); // 认证通过，一定要设成true

      HashMap<String, Object> loginDetail = new HashMap<>();
      // 可能需要返回给前端，当用户没有绑定账号是，可能会需要告知前端跳转到绑定账号页面或初始化账号页面
      loginDetail.put("notBindAccount", notBindAccount);
      successAuth.setDetails(loginDetail);
      return successAuth;
    } catch (BaseException e) {
      // 已知异常，将异常内容返回给前端
      throw new BadCredentialsException(e.getMessage());
    } catch (Exception e) {
      // 未知异常
      throw new BadCredentialsException("Gitee Authentication Failed");
    }
  }

  @Override
  public boolean supports(Class<?> authentication) {
    return GiteeAuthentication.class.isAssignableFrom(authentication);
  }

}