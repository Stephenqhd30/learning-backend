package com.kc.learning.model.dto.course;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建课程请求
 *
 * @author stephen qiu
 */
@Data
public class CourseAddRequest implements Serializable {
    
    /**
     * 课程号
     */
    private Integer courseNumber;
    
    /**
     * 课程名称
     */
    private String courseName;

    private static final long serialVersionUID = 1L;
}