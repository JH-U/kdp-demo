package org.demo.security.authentication.handler.resourceapi.openapi2;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.demo.security.common.web.exception.ExceptionTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class OpenApi2AuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(OpenApi2AuthenticationFilter.class);

  public OpenApi2AuthenticationFilter() {
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    logger.debug("Use OpenApi2AuthenticationFilter...");

    String appId = request.getHeader("x-app-id");
    if (StringUtils.isEmpty(appId)) {
     ExceptionTool.throwException("x-app-id token is missing!", "miss.appId");
    }

    // 认证开始前，按SpringSecurity设计，要将Authentication设置到SecurityContext里面去。
    System.out.println("appId认证通过...");

    OpenApi2Authentication authentication = new OpenApi2Authentication();

    OpenApi2LoginInfo userLoginInfo = new OpenApi2LoginInfo();
    userLoginInfo.setAppId(appId);
    userLoginInfo.setMerchantName("三方系统商户名称");

    authentication.setAuthenticated(true); // 设置true，认证通过。
    authentication.setCurrentUser(userLoginInfo);
    SecurityContextHolder.getContext().setAuthentication(authentication); // 一定要设置到ThreadLocal

    // 放行
    filterChain.doFilter(request, response);
  }
}
