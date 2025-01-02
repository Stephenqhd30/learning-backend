package com.kc.learning.model.dto.logPrintCertificate;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建打印证书日志请求
 *
 * @author stephen qiu
 */
@Data
public class LogPrintCertificateAddRequest implements Serializable {
	/**
	 * 获取人id
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
	 * 证书获得时间
	 */
	private Date finishTime;
	
	/**
	 * 课程id列表
	 */
	private List<Long> certificateIdList;
	
	private static final long serialVersionUID = 1L;
}