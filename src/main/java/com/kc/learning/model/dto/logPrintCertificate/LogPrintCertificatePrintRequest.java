package com.kc.learning.model.dto.logPrintCertificate;

import com.kc.learning.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 打印证书请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class LogPrintCertificatePrintRequest extends PageRequest implements Serializable {
	/**
	 * 用户课程id
	 */
	private Long userCourseId;
	
	/**
	 * 证书id
	 */
	private Long certificateId;
	
	private static final long serialVersionUID = 1L;
}