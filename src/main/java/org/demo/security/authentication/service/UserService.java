package org.demo.security.authentication.service;

import org.demo.security.common.web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  @Autowired
  private PasswordEncoder passwordEncoder;

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

}
