package com.kc.learning.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 *
 * @author stephen qiu
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
	
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@ExcelIgnore
	private Long id;
	
	
	/**
	 * 身份证号
	 */
	@ExcelProperty("身份证号")
	private String userIdCard;
	
	/**
	 * 姓名
	 */
	@ExcelProperty("姓名")
	private String userName;
	
	/**
	 * 用户头像
	 */
	@ExcelIgnore
	private String userAvatar;
	
	/**
	 * 性别（0-男，1-女，2-保密）
	 */
	@ExcelProperty("性别（0-男，1-女，2-保密）")
	private Integer userGender;
	
	/**
	 * 用户简介
	 */
	@ExcelProperty("用户简介")
	private String userProfile;
	
	/**
	 * 用户角色：user/admin/ban
	 */
	@ExcelIgnore
	private String userRole;
	
	/**
	 * 用户邮箱
	 */
	@ExcelProperty("用户邮箱")
	private String userEmail;
	
	/**
	 * 手机号码
	 */
	@ExcelProperty("手机号码")
	private String userPhone;
	
	/**
	 * 学号
	 */
	@ExcelProperty("学号")
	private String userNumber;
	
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
	 * 是否删除
	 */
	@ExcelIgnore
	@TableLogic
	private Integer isDelete;
	
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}