package com.stephen.popcorn.model.dto.userCertificate;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建用户证书请求
 *
 * @author stephen qiu
 */
@Data
public class UserCertificateAddRequest implements Serializable {
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 证书id
	 */
	private Long certificateId;
	
	/**
	 * 获得时间
	 */
	private String gainTime;
	
	/**
	 * 证书名称
	 */
	private String certificateName;
	
	/**
	 * 获得人名称
	 */
	private String gainUserName;
	
	
	private static final long serialVersionUID = 1L;
}