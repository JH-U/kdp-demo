package org.demo.security.authentication.handler.resourceapi.openapi2;

public class OpenApi2LoginInfo {

  private String appId;
  private String appSecret;
  private String merchantName; // 下游商户名称

  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }

  public String getAppSecret() {
    return appSecret;
  }

  public void setAppSecret(String appSecret) {
    this.appSecret = appSecret;
  }

  public String getMerchantName() {
    return merchantName;
  }

  public void setMerchantName(String merchantName) {
    this.merchantName = merchantName;
  }

  @Override
  public String toString() {
    return "OpenApi2LoginInfo{" +
        "appId='" + appId + '\'' +
        ", appSecret='" + appSecret + '\'' +
        ", merchantName='" + merchantName + '\'' +
        '}';
  }
}
