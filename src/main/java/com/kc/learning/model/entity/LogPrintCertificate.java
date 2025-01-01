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
	 * 开课时间
	 */
	private Date acquisitionTime;
	
	/**
	 * 证书获得时间
	 */
	private Date finishTime;
	
	/**
	 * 证书状态(wait,running,succeed,failed)
	 */
	private String status;
	
	/**
	 * 执行信息
	 */
	private String executorMessage;
	
	/**
	 * 创建用户id
	 */
	private Long createUserId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}