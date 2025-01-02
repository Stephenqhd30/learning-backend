package com.kc.learning.model.dto.certificateReviewLogs;

import com.kc.learning.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询证书审核日志请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CertificateReviewLogsQueryRequest extends PageRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * id
	 */
	private Long notId;
	
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
	
	private static final long serialVersionUID = 1L;
}