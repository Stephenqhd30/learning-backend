package com.kc.learning.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 课程表
 *
 * @author stephen qiu
 * @TableName course
 */
@TableName(value = "course")
@Data
public class Course implements Serializable {
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@ExcelIgnore
	private Long id;
	
	/**
	 * 课程号
	 */
	@ExcelProperty(value = "课程号")
	private Integer courseNumber;
	
	/**
	 * 课程名称
	 */
	@ExcelProperty(value = "课程名称")
	private String courseName;
	
	/**
	 * 创建用户id
	 */
	@ExcelIgnore
	private Long userId;
	
	/**
	 * 创建时间
	 */
	@ExcelIgnore
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	@ExcelIgnore
	private Date updateTime;
	
	/**
	 * 是否删除(0-未删除，1-删除)
	 */
	@TableLogic
	@ExcelIgnore
	private Integer isDelete;
	
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}