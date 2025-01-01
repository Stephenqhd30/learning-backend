package com.kc.learning.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 证书审核日志
 *
 * @author stephen qiu
 * @TableName certificate_review_logs
 */
@TableName(value = "certificate_review_logs")
@Data
public class CertificateReviewLogs implements Serializable {
	/**
	 * 审核记录ID
	 */
	@TableId(type = IdType.ASSIGN_ID)
	private Long id;
	
	/**
	 * 证书ID，关联certificate表
	 */
	private Long certificateId;
	
	/**
	 * 审核人ID，关联用户表
	 */
	private Long reviewerId;
	
	/**
	 * 审核状态（0-待审核，1-通过，2-拒绝）
	 */
	private Integer reviewStatus;
	
	/**
	 * 审核意见
	 */
	private String reviewMessage;
	
	/**
	 * 审核时间
	 */
	private Date reviewTime;
	
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}