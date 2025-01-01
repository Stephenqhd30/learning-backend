package com.kc.learning.model.dto.certificateReviewLogs;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建证书审核日志请求
 *
 * @author stephen qiu
 */
@Data
public class CertificateReviewLogsAddRequest implements Serializable {
	
	private static final long serialVersionUID = 1973973718590531783L;
	
	/**
	 * 证书ID，关联certificate表
	 */
	private Long certificateId;
	
	/**
	 * 审核状态 0-待审核 1-通过 2-拒绝
	 */
	private Integer reviewStatus;
	
	/**
	 * 审核信息
	 */
	private String reviewMessage;
	
	/**
	 * id列表
	 */
	private List<Long> idList;
}