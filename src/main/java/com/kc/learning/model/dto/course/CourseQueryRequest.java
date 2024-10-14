package com.kc.learning.model.dto.course;

import com.kc.learning.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询课程请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CourseQueryRequest extends PageRequest implements Serializable {
    
    /**
     * id
     */
    private Long id;
    
    /**
     * 需要过滤的id
     */
    private Long notId;
    
    /**
     * 课程号
     */
    private Integer courseNumber;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 创建用户id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}