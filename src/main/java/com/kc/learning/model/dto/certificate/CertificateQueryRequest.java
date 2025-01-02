package com.kc.learning.model.dto.certificate;

import com.kc.learning.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询证书请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CertificateQueryRequest extends PageRequest implements Serializable {
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 证书编号
	 */
	private String certificateNumber;
	
	/**
	 * 证书名称
	 */
	private String certificateName;
	
	/**
	 * 证书类型(0-干部培训,1-其他)
	 */
	private Integer certificateType;
	
	/**
	 * 证书获得时间
	 */
	private String certificateYear;
	
	
	/**
	 * 证书获得情况(0-有,1-没有)
	 */
	private Integer certificateSituation;
	
	/**
	 * 证书状态(0-待审核,1-通过,2-拒绝)
	 */
	private Integer reviewStatus;
	
	/**
	 * 审核信息
	 */
	private String reviewMessage;
	
	/**
	 * 审核人id
	 */
	private Long reviewerId;
	
	/**
	 * 审核时间
	 */
	private Date reviewTime;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 需要过滤的审核状态id
	 */
	private Integer noId;
	
	/**
	 * 证书地址
	 */
	private String certificateUrl;
	
	/**
	 * 证书执行状态
	 */
	private String status;
	
	private static final long serialVersionUID = 1L;
}