package org.demo.security.authentication.config;

import jakarta.servlet.Filter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.demo.security.authentication.handler.exception.CustomAuthenticationExceptionHandler;
import org.demo.security.authentication.handler.exception.CustomAuthorizationExceptionHandler;
import org.demo.security.authentication.handler.exception.CustomSecurityExceptionHandler;
import org.demo.security.authentication.handler.login.LoginFailHandler;
import org.demo.security.authentication.handler.login.LoginSuccessHandler;
import org.demo.security.authentication.handler.login.sms.SmsCodeAuthenticationFilter;
import org.demo.security.authentication.handler.login.username.UsernameAuthenticationFilter;
import org.demo.security.authentication.handler.resourceapi.openapi1.OpenApi1AuthenticationFilter;
import org.demo.security.authentication.handler.resourceapi.openapi2.OpenApi2AuthenticationFilter;
import org.demo.security.authentication.service.JwtService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;

@Configuration
@EnableWebSecurity
public class CustomWebSecurityConfig {

  private final ApplicationContext applicationContext;

  public CustomWebSecurityConfig(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  private final AuthenticationEntryPoint authenticationExceptionHandler = new CustomAuthenticationExceptionHandler();
  private final AccessDeniedHandler authorizationExceptionHandler = new CustomAuthorizationExceptionHandler();
  private final Filter globalSpringSecurityExceptionHandler = new CustomSecurityExceptionHandler();
  /** 禁用不必要的默认filter，处理异常响应内容 */
  private void commonHttpSetting(HttpSecurity http) throws Exception {
    // 禁用SpringSecurity默认filter。这些filter都是非前后端分离项目的产物，用不上.
    // yml配置文件将日志设置DEBUG模式，就能看到加载了哪些filter
    // logging:
    //    level:
    //       org.springframework.security: DEBUG
    // 表单登录/登出、session管理、csrf防护等默认配置，如果不disable。会默认创建默认filter
    http.formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .sessionManagement(AbstractHttpConfigurer::disable)
        .csrf(AbstractHttpConfigurer::disable)
        // requestCache用于重定向，前后端分析项目无需重定向，requestCache也用不上
        .requestCache(cache -> cache
            .requestCache(new NullRequestCache())
        )
        // 无需给用户一个匿名身份
        .anonymous(AbstractHttpConfigurer::disable);

    // 处理 SpringSecurity 异常响应结果。响应数据的结构，改成业务统一的JSON结构。不要框架默认的响应结构
    http.exceptionHandling(exceptionHandling ->
        exceptionHandling
            // 认证失败异常
            .authenticationEntryPoint(authenticationExceptionHandler)
            // 鉴权失败异常
            .accessDeniedHandler(authorizationExceptionHandler)
    );
    // 其他未知异常. 尽量提前加载。
    http.addFilterBefore(globalSpringSecurityExceptionHandler, SecurityContextHolderFilter.class);
  }

  /**
   * 密码加密使用的编码器
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /** 不鉴权的api */
  @Bean
  public SecurityFilterChain publicApiFilterChain(HttpSecurity http) throws Exception {
    commonHttpSetting(http);
    http
        // 使用securityMatcher限定当前配置作用的路径
        .securityMatcher("/open-api/business-3")
        .authorizeHttpRequests(authorize -> authorize.anyRequest().permitAll());
    return http.build();
  }

  private static final String USERNAME_LOGIN_PATH = "/user/login/username";
  private static final String SMS_LOGIN_PATH = "/user/login/sms";
  /** 登录api */
  @Bean
  public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {
    // 使用securityMatcher限定当前配置作用的路径
    http.securityMatcher(USERNAME_LOGIN_PATH, SMS_LOGIN_PATH)
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
    commonHttpSetting(http);

    // 加一个登录方式。用户名、密码登录
    http.addFilterBefore(createLoginFilter(USERNAME_LOGIN_PATH), LogoutFilter.class);

    // 加一个登录方式。短信验证码 登录
    http.addFilterBefore(createLoginFilter(SMS_LOGIN_PATH), LogoutFilter.class);
    return http.build();
  }

  public Filter createLoginFilter(String path) {
    AbstractAuthenticationProcessingFilter filter = null;
    if (path.equals(USERNAME_LOGIN_PATH)) {
      filter = new UsernameAuthenticationFilter(path);
    } else if (path.equals(SMS_LOGIN_PATH)) {
      filter = new SmsCodeAuthenticationFilter(path);
    } else {
      throw new RuntimeException("Not supported login path: " + path);
    }
    // 指定将负责认证的authenticationManager
    filter.setAuthenticationManager(authenticationManager());
    // 登录成功后，要执行的代码
    filter.setAuthenticationSuccessHandler(applicationContext.getBean(LoginSuccessHandler.class));
    // 登录失败后，要执行的代码
    filter.setAuthenticationFailureHandler(applicationContext.getBean(LoginFailHandler.class));
    return filter;
  }

  @Bean
  public AuthenticationManager authenticationManager() {
    List<AuthenticationProvider> providers = new ArrayList<>();
    // 更方便简单的注册provider示例：直接获取所有AuthenticationProvider的bean
    Map<String, AuthenticationProvider> beansMap = applicationContext.getBeansOfType(
        AuthenticationProvider.class);
    providers.addAll(beansMap.values());
    return new ProviderManager(providers);
  }

  @Bean
  public SecurityFilterChain openApi1FilterChain(HttpSecurity http) throws Exception {
    // 使用securityMatcher限定当前配置作用的路径
    http.securityMatcher("/open-api/business-1")
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
    commonHttpSetting(http);

    OpenApi1AuthenticationFilter openApi1Filter = new OpenApi1AuthenticationFilter(
        applicationContext.getBean(JwtService.class));
    // 加一个登录方式。用户名、密码登录
    http.addFilterBefore(openApi1Filter, LogoutFilter.class);
    return http.build();
  }

  /** 默认鉴权过滤器 */
  @Bean
  public SecurityFilterChain defaultFilterChain(HttpSecurity http) throws Exception {
    // 不使用securityMatcher限定当前配置作用的路径。所有没有匹配上指定SecurityFilterChain的请求，都走这里鉴权
    http
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
    commonHttpSetting(http);

    OpenApi2AuthenticationFilter openApiFilter = new OpenApi2AuthenticationFilter(
        applicationContext.getBean(JwtService.class));
    // 加一个登录方式。用户名、密码登录
    http.addFilterBefore(openApiFilter, LogoutFilter.class);
    return http.build();
  }
}
