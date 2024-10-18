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
     * 用户id
     */
    private Long userId;
    
    /**
     * 课程id
     */
    private Long courseId;

    private static final long serialVersionUID = 1L;
}