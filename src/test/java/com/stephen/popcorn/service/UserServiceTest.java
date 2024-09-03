package com.stephen.popcorn.service;

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
        try {
            long result = userService.userRegister(userName, userIdCard, checkPassword);
            Assertions.assertEquals(-1, result);
            userName = "yu";
            result = userService.userRegister(userName, userIdCard, checkPassword);
            Assertions.assertEquals(-1, result);
        } catch (Exception e) {

        }
    }
}
