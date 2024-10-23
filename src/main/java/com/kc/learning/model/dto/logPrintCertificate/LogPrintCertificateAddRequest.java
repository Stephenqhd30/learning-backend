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
     * 姓名
     */
    private String userName;
    
    /**
     * 性别(0-男, 1-女)
     */
    private Integer userGender;
    
    /**
     * 身份证号
     */
    private String userIdCard;
    
    /**
     * 证书编号
     */
    private String certificateNumber;
    
    /**
     * 课程名称
     */
    private String courseName;
    
    /**
     * 开课时间
     */
    private Date acquisitionTime;
    
    /**
     * 结课时间
     */
    private Date finishTime;

    private static final long serialVersionUID = 1L;
}