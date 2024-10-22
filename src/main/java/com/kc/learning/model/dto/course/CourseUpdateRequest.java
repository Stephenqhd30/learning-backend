package com.kc.learning.model.dto.course;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 更新课程请求
 *
 * @author stephen qiu
 */
@Data
public class CourseUpdateRequest implements Serializable {
    
    /**
     * id
     */
    private Long id;
    
    /**
     * 课程号
     */
    private Integer courseNumber;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 开课时间
     */
    private Date acquisitionTime;
    
    /**
     * 结课时间
     */
    private Date finishTime;

    private static final long serialVersionUID = 1L;
}