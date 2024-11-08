package com.kc.learning.model.dto.logPrintCertificate;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建打印证书日志请求
 *
 * @author stephen qiu
 */
@Data
public class LogPrintCertificateAddRequest implements Serializable {
    
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

    private static final long serialVersionUID = 1L;
}