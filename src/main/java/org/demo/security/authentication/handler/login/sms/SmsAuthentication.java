package org.demo.security.authentication.handler.login.sms;

import org.demo.security.authentication.handler.login.UserLoginInfo;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public class SmsAuthentication extends AbstractAuthenticationToken {

  private String phone;
  private String smsCode;
  private UserLoginInfo currentUser;

  public SmsAuthentication() {
    super(null); // 权限，用不上，直接null
  }

  @Override
  public Object getCredentials() {
    return isAuthenticated() ? null : smsCode;
  }

  @Override
  public Object getPrincipal() {
    return isAuthenticated() ? currentUser : phone;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getSmsCode() {
    return smsCode;
  }

  public void setSmsCode(String smsCode) {
    this.smsCode = smsCode;
  }

  public UserLoginInfo getCurrentUser() {
    return currentUser;
  }

  public void setCurrentUser(UserLoginInfo currentUser) {
    this.currentUser = currentUser;
  }
}