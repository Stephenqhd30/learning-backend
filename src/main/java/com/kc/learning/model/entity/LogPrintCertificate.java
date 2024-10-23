package com.kc.learning.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 打印证书记录表
 *
 * @author stephen qiu
 * @TableName log_print_certificate
 */
@TableName(value = "log_print_certificate")
@Data
public class LogPrintCertificate implements Serializable {
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
	 * 证书id
	 */
	private Long certificateId;
	
	/**
	 * 课程id
	 */
	private Long courseId;
	
	/**
	 * 姓名
	 */
	private String userName;
	
	/**
	 * 性别(0-男, 1-女)
	 */
	private Integer userGender;
	
	/**
	 * 身份证号
	 */
	private String userIdCard;
	
	/**
	 * 证书编号
	 */
	private String certificateNumber;
	
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
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}