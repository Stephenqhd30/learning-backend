package com.kc.learning.model.dto.userCourse;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建用户课程请求
 *
 * @author stephen qiu
 */
@Data
public class UserCourseAddRequest implements Serializable {
    
    /**
     * 姓名
     */
    private String userName;
    
    /**
     * 学号
     */
    private String userNumber;
    
    /**
     * 课程id
     */
    private Long courseId;

    private static final long serialVersionUID = 1L;
}