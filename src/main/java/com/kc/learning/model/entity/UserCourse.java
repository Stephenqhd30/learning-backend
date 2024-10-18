package com.kc.learning.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 用户课程表(硬删除)
 *
 * @author stephen qiu
 * @TableName user_course
 */
@TableName(value = "user_course")
@Data
public class UserCourse implements Serializable {
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 课程id
	 */
	private Long courseId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}