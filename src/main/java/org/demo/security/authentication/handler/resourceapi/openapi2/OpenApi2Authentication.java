package org.demo.security.authentication.handler.resourceapi.openapi2;

import org.springframework.security.authentication.AbstractAuthenticationToken;

public class OpenApi2Authentication extends AbstractAuthenticationToken {

  private String appId; // 前端传过来
  private String appSecurity; // 前端传过来
  private OpenApi2LoginInfo currentUser; // 认证成功后，后台从数据库获取信息

  public OpenApi2Authentication() {
    // 权限，用不上，直接null
    super(null);
  }

  @Override
  public Object getCredentials() {
    // 根据SpringSecurity的设计，授权成后，Credential（比如，登录密码）信息需要被清空
    return isAuthenticated() ? null : appSecurity;
  }

  @Override
  public Object getPrincipal() {
    // 根据SpringSecurity的设计，授权成功之前，getPrincipal返回的客户端传过来的数据。授权成功后，返回当前登陆用户的信息
    return isAuthenticated() ? currentUser : appId;
  }

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getAppSecurity() {
    return appSecurity;
  }

  public void setAppSecurity(String appSecurity) {
    this.appSecurity = appSecurity;
  }

  public OpenApi2LoginInfo getCurrentUser() {
    return currentUser;
  }

  public void setCurrentUser(
      OpenApi2LoginInfo currentUser) {
    this.currentUser = currentUser;
  }
}
