package com.kc.learning.model.dto.certificate;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建证书请求
 *
 * @author stephen qiu
 */
@Data
public class CertificateAddRequest implements Serializable {
	
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
	 * 获得证书时间
	 */
	private String certificateYear;
	
	/**
	 * 证书获得情况(0-有,1-没有)
	 */
	private Integer certificateSituation;
	
	/**
	 * 获得人姓名
	 */
	private Long gainUserId;
	
	/**
	 * 证书地址
	 */
	private String certificateUrl;
	
	private static final long serialVersionUID = 1L;
}