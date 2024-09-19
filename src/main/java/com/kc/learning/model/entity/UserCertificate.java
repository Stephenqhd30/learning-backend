package com.kc.learning.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户证书表
 * @author stephen qiu
 * @TableName user_certificate
 */
@TableName(value ="user_certificate")
@Data
public class UserCertificate implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

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
     * 证书编号
     */
    private String certificateNumber;
    
    /**
     * 证书名称
     */
    private String certificateName;
    
    /**
     * 获得人名称
     */
    private String gainUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}