package com.kc.learning.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 用户服务测试
 *
 * @author stephen qiu
 * 
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void userRegister() {
        String userName = "stephen";
        String userIdCard = "";
        String checkPassword = "123456";
        String userNumber = "1234567890";
        try {
            long result = userService.userRegister(userName, userIdCard, checkPassword, userNumber);
            Assertions.assertEquals(-1, result);
            userName = "yu";
            result = userService.userRegister(userName, userIdCard, checkPassword, userNumber);
            Assertions.assertEquals(-1, result);
        } catch (Exception e) {

        }
    }
}
