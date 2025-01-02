package com.kc.learning.model.dto.userCourse;

import com.kc.learning.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询用户课程请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserCourseQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;
    
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