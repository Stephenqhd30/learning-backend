package com.kc.learning.model.dto.userCertificate;

import com.kc.learning.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询用户证书请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserCertificateQueryRequest extends PageRequest implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * id
	 */
	private Long notId;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 证书id
	 */
	private Long certificateId;
	
	private static final long serialVersionUID = 1L;
}