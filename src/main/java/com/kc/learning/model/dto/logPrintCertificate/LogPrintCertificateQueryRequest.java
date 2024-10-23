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
     * 姓名
     */
    private String userName;
    
    /**
     * 性别(0-男, 1-女)
     */
    private Integer userGender;
    
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
    
    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}