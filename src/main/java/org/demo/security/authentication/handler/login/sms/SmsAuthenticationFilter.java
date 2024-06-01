package org.demo.security.authentication.handler.login.sms;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;
import org.demo.security.common.web.util.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

public class SmsAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

  private static final Logger logger = LoggerFactory.getLogger(SmsAuthenticationFilter.class);

  public SmsAuthenticationFilter(AntPathRequestMatcher pathRequestMatcher,
      AuthenticationManager authenticationManager,
      AuthenticationSuccessHandler authenticationSuccessHandler,
      AuthenticationFailureHandler authenticationFailureHandler) {
    super(pathRequestMatcher);
    setAuthenticationManager(authenticationManager);
    setAuthenticationSuccessHandler(authenticationSuccessHandler);
    setAuthenticationFailureHandler(authenticationFailureHandler);
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request,
      HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
    logger.debug("user SmsCodeAuthenticationFilter");

    // 提取请求数据
    String requestJsonData = request.getReader().lines()
        .collect(Collectors.joining(System.lineSeparator()));
    Map<String, Object> requestMapData = JSON.parseToMap(requestJsonData);
    String phoneNumber = requestMapData.get("phone").toString();
    String smsCode = requestMapData.get("captcha").toString();

    SmsAuthentication authentication = new SmsAuthentication();
    authentication.setPhone(phoneNumber);
    authentication.setSmsCode(smsCode);
    authentication.setAuthenticated(false); // 提取参数阶段，authenticated一定是false
    return this.getAuthenticationManager().authenticate(authentication);
  }

}