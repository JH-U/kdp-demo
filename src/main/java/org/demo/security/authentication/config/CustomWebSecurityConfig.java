package org.demo.security.authentication.config;

import jakarta.servlet.Filter;
import java.util.List;
import org.demo.security.authentication.handler.exception.CustomAuthenticationExceptionHandler;
import org.demo.security.authentication.handler.exception.CustomAuthorizationExceptionHandler;
import org.demo.security.authentication.handler.exception.CustomSecurityExceptionHandler;
import org.demo.security.authentication.handler.login.LoginFailHandler;
import org.demo.security.authentication.handler.login.LoginSuccessHandler;
import org.demo.security.authentication.handler.login.gitee.GiteeAuthenticationFilter;
import org.demo.security.authentication.handler.login.gitee.GiteeAuthenticationProvider;
import org.demo.security.authentication.handler.login.sms.SmsAuthenticationFilter;
import org.demo.security.authentication.handler.login.sms.SmsAuthenticationProvider;
import org.demo.security.authentication.handler.login.username.UsernameAuthenticationFilter;
import org.demo.security.authentication.handler.login.username.UsernameAuthenticationProvider;
import org.demo.security.authentication.handler.resourceapi.openapi1.MyJwtAuthenticationFilter;
import org.demo.security.authentication.handler.resourceapi.openapi2.OpenApi2AuthenticationFilter;
import org.demo.security.authentication.handler.resourceapi.openapi3.OpenApi3AuthenticationFilter;
import org.demo.security.authentication.service.JwtService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

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
  /** 登录api */
  @Bean
  public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {
    commonHttpSetting(http);
    // 使用securityMatcher限定当前配置作用的路径
    http.securityMatcher("/user/login/*")
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());

    LoginSuccessHandler loginSuccessHandler = applicationContext.getBean(LoginSuccessHandler.class);
    LoginFailHandler loginFailHandler = applicationContext.getBean(LoginFailHandler.class);

    // 加一个登录方式。用户名、密码登录
    UsernameAuthenticationFilter usernameLoginFilter = new UsernameAuthenticationFilter(
        new AntPathRequestMatcher("/user/login/username", HttpMethod.POST.name()),
        new ProviderManager(
            List.of(applicationContext.getBean(UsernameAuthenticationProvider.class))),
        loginSuccessHandler,
        loginFailHandler);
    http.addFilterBefore(usernameLoginFilter, UsernamePasswordAuthenticationFilter.class);

    // 加一个登录方式。短信验证码 登录
    SmsAuthenticationFilter smsLoginFilter = new SmsAuthenticationFilter(
        new AntPathRequestMatcher("/user/login/sms", HttpMethod.POST.name()),
        new ProviderManager(
            List.of(applicationContext.getBean(SmsAuthenticationProvider.class))),
        loginSuccessHandler,
        loginFailHandler);
    http.addFilterBefore(smsLoginFilter, UsernamePasswordAuthenticationFilter.class);


    // 加一个登录方式。Gitee 登录
    GiteeAuthenticationFilter giteeFilter = new GiteeAuthenticationFilter(
        new AntPathRequestMatcher("/user/login/gitee", HttpMethod.POST.name()),
        new ProviderManager(
            List.of(applicationContext.getBean(GiteeAuthenticationProvider.class))),
        loginSuccessHandler,
        loginFailHandler);
    http.addFilterBefore(giteeFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }


  @Bean
  public SecurityFilterChain myApiFilterChain(HttpSecurity http) throws Exception {
    // 使用securityMatcher限定当前配置作用的路径
    http.securityMatcher("/open-api/business-1")
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
    commonHttpSetting(http);

    MyJwtAuthenticationFilter openApi1Filter = new MyJwtAuthenticationFilter(
        applicationContext.getBean(JwtService.class));
    // 加一个登录方式。用户名、密码登录
    http.addFilterBefore(openApi1Filter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public SecurityFilterChain thirdApiFilterChain(HttpSecurity http) throws Exception {
    // 不使用securityMatcher限定当前配置作用的路径。所有没有匹配上指定SecurityFilterChain的请求，都走这里鉴权
    http.securityMatcher("/open-api/business-2")
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
    commonHttpSetting(http);

    OpenApi2AuthenticationFilter openApiFilter = new OpenApi2AuthenticationFilter();
    // 加一个登录方式。用户名、密码登录
    http.addFilterBefore(openApiFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
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

  /** 其余路径，走这个默认过滤链 */
  @Bean
  @Order(Integer.MAX_VALUE) // 这个过滤链最后加载
  public SecurityFilterChain defaultApiFilterChain(HttpSecurity http) throws Exception {
    commonHttpSetting(http);
    http // 不用securityMatcher表示缺省值，匹配不上其他过滤链时，都走这个过滤链
        .authorizeHttpRequests(authorize -> authorize.anyRequest().authenticated());
    http.addFilterBefore(new OpenApi3AuthenticationFilter(),
        UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
