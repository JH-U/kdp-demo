package org.demo.security.authentication.handler.login.gitee;

import java.util.HashMap;
import java.util.Map;
import org.demo.security.common.web.exception.ExceptionTool;
import org.demo.security.common.web.util.JSON;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class GiteeApiClient {

  private RestTemplate restTemplate = new RestTemplate();

  @Value("${login.gitee.clientId}")
  private String clientId;

  @Value("${login.gitee.clientSecret}")
  private String clientSecret;

  @Value("${login.gitee.redirectUri}")
  private String redirectUri;

  public String getTokenByCode(String code) {
    String url = "https://gitee.com/oauth/token?grant_type=authorization_code"
        + "&code=" + code
        + "&client_id=" + clientId
        + "&redirect_uri=" + redirectUri
        + "&client_secret=" + clientSecret;
    String responseJson = sendPostRequest(url, null);
    Map<String, Object> responseMap = JSON.parseToMap(responseJson);
    Object accessToken = responseMap.get("access_token");
    if (accessToken == null) {
      ExceptionTool.throwException("{gitee.get.token.fail:获取GiteeToken失败！}");
    }
    return accessToken.toString();
  }

  public Map<String, Object> getThirdUserInfo(String token) {
    // {"id":1483966,"login":"用户名"}
    String responseJson = sendGetRequest("https://gitee.com/api/v5/user?access_token=" + token);
    Map<String, Object> responseMap = JSON.parseToMap(responseJson);
    Object openId = responseMap.get("id");
    if (openId == null) {
      return null;
    }

    HashMap<String, Object> thirdUser = new HashMap<>();
    thirdUser.put("openId", openId);
    thirdUser.put("nickname", responseMap.get("login"));
    return thirdUser;
  }

  public String sendPostRequest(String url, Map<String, Object> body) {
    // 请求头
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    // 创建HttpEntity对象
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
    // 发送POST请求
    try {
      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity,
          String.class);
      // 返回响应体
      return response.getBody();
    } catch (HttpClientErrorException e) {
      return e.getResponseBodyAsString();
    }
  }

  public String sendGetRequest(String url) {
    // 请求头
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    // 创建HttpEntity对象
    HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(headers);
    // 发送GET请求
    try {
      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity,
          String.class);
      // 返回响应体
      return response.getBody();
    } catch (HttpClientErrorException e) {
      return e.getResponseBodyAsString();
    }
  }
}
