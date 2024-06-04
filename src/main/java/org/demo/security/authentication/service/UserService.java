package org.demo.security.authentication.service;

import org.demo.security.common.web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private PasswordEncoder passwordEncoder;

  /**
   * 通过openId获取用户信息
   * @param openId
   * @param thirdPlatform 三方平台，比如gitee/qq/wechat
   * @return
   */
  public User getUserByOpenId(String openId, String thirdPlatform) {
    System.out.println("通过openId从数据库查询user"); // todo
    if (thirdPlatform.equals("gitee")) {
      User testUser = new User();
      testUser.setUserId(1003L);
      testUser.setUsername("Tom");
      testUser.setRoleId("manager");
      testUser.setPassword(passwordEncoder.encode("manager"));
      testUser.setPhone("123000123");
      return testUser;
    }
    return null;
  }

  public User getUserByPhone(String phoneNumber) {
    if (phoneNumber.equals("1234567890")) {
      User testUser = new User();
      testUser.setUserId(1002L);
      testUser.setUsername("manager");
      testUser.setRoleId("manager");
      testUser.setPassword(passwordEncoder.encode("manager"));
      testUser.setPhone("1234567890");
      return testUser;
    }
    return null;
  }

  public User getUserFromDB(String username) {
    if (username.equals("admin")) {
      User testUser = new User();
      testUser.setUserId(1001L);
      testUser.setUsername("admin");
      testUser.setRoleId("admin");
      testUser.setPassword(passwordEncoder.encode("admin"));
      return testUser;
    }
    return null;
  }

  public void createUserWithOpenId(User user, String openId, String platform) {
    System.out.println("在数据库创建一个user"); // todo
    System.out.println("user绑定openId"); // todo
  }
}
