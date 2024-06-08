package org.demo.security.authentication.handler.resourceapi.openapi3;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class OpenApi3AuthenticationFilter extends OncePerRequestFilter {


  public OpenApi3AuthenticationFilter() {
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    System.out.println("这里是默认过滤链...");
    // 随便给个默认身份
    Authentication authentication = new TestingAuthenticationToken("username", "password", "ROLE_USER");
    SecurityContextHolder.getContext().setAuthentication(authentication);
    // 放行
    filterChain.doFilter(request, response);
  }
}
