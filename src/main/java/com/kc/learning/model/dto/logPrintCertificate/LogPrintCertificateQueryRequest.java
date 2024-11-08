package com.kc.learning.model.dto.logPrintCertificate;

import com.kc.learning.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询打印证书日志请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LogPrintCertificateQueryRequest extends PageRequest implements Serializable {
	
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
	 * 创建用户id
	 */
	private Long createUserId;
	
	private static final long serialVersionUID = 1L;
}