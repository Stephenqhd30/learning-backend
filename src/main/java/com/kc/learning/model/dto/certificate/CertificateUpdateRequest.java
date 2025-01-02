package com.kc.learning.model.dto.certificate;

import lombok.Data;

import java.io.Serializable;

/**
 * 更新证书请求
 *
 * @author stephen qiu
 */
@Data
public class CertificateUpdateRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
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
	 * 获得人id
	 */
	private Long userId;
	
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