package org.demo.security.authentication.handler.resourceapi.openapi1;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.demo.security.authentication.service.JwtService;
import org.demo.security.authentication.handler.login.UserLoginInfo;
import org.demo.security.common.web.exception.ExceptionTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

public class MyJwtAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(MyJwtAuthenticationFilter.class);

  private JwtService jwtService;

  public MyJwtAuthenticationFilter(JwtService jwtService) {
    this.jwtService = jwtService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    logger.debug("Use OpenApi1AuthenticationFilter");

    String jwtToken = request.getHeader("Authorization");
    if (StringUtils.isEmpty(jwtToken)) {
     ExceptionTool.throwException("JWT token is missing!", "miss.token");
    }
    if (jwtToken.startsWith("Bearer ")) {
      jwtToken = jwtToken.substring(7);
    }


    try {
      UserLoginInfo userLoginInfo = jwtService.verifyJwt(jwtToken, UserLoginInfo.class);

      MyJwtAuthentication authentication = new MyJwtAuthentication();
      authentication.setJwtToken(jwtToken);
      authentication.setAuthenticated(true); // 设置true，认证通过。
      authentication.setCurrentUser(userLoginInfo);
      // 认证通过后，一定要设置到SecurityContextHolder里面去。
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }catch (ExpiredJwtException e) {
      // 转换异常，指定code，让前端知道时token过期，去调刷新token接口
      ExceptionTool.throwException("jwt过期", HttpStatus.UNAUTHORIZED, "token.expired");
    } catch (Exception e) {
      ExceptionTool.throwException("jwt无效", HttpStatus.UNAUTHORIZED, "token.invalid");
    }
    // 放行
    filterChain.doFilter(request, response);
  }
}
