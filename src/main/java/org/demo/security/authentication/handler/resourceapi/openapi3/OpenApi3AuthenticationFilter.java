package org.demo.security.authentication.handler.resourceapi.openapi3;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.web.filter.OncePerRequestFilter;

public class OpenApi3AuthenticationFilter extends OncePerRequestFilter {


  public OpenApi3AuthenticationFilter() {
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    System.out.println("这里是默认过滤链...");
    // 放行
    filterChain.doFilter(request, response);
  }
}
