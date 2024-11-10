package com.kc.learning.model.entity;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 证书表
 *
 * @author stephen qiu
 * @TableName certificate
 */
@TableName(value = "certificate")
@Data
public class Certificate implements Serializable {
	/**
	 * id
	 */
	@TableId(type = IdType.ASSIGN_ID)
	@ExcelIgnore
	private Long id;
	
	/**
	 * 证书编号
	 */
	@ExcelProperty(value = "证书编号")
	private String certificateNumber;
	
	/**
	 * 证书名称
	 */
	@ExcelProperty(value = "证书名称")
	private String certificateName;
	
	/**
	 * 证书类型(0-干部培训,1-其他)
	 */
	@ExcelProperty(value = "证书类型(0-干部培训,1-其他)")
	private Integer certificateType;
	
	/**
	 * 证书获得时间
	 */
	@ExcelProperty(value = "证书获得时间")
	private String certificateYear;
	
	/**
	 * 证书获得情况(0-有,1-没有)
	 */
	@ExcelProperty(value = "证书获得情况(0-有,1-没有)")
	private Integer certificateSituation;
	
	/**
	 * 证书状态(0-待审核,1-通过,2-拒绝)
	 */
	@ExcelIgnore
	private Integer reviewStatus;
	
	/**
	 * 审核信息
	 */
	@ExcelIgnore
	private String reviewMessage;
	
	/**
	 * 审核人id
	 */
	@ExcelIgnore
	private Long reviewerId;
	
	/**
	 * 审核时间
	 */
	@ExcelIgnore
	private Date reviewTime;
	
	/**
	 * 获得人id
	 */
	@ExcelProperty(value = "获得人id")
	private Long userId;
	
	/**
	 * 证书地址下载地址
	 */
	@ExcelProperty(value = "证书地址下载地址")
	private String certificateUrl;
	
	/**
	 * 创建用户id
	 */
	@ExcelIgnore
	private Long createUserId;
	
	
	/**
	 * 创建时间
	 */
	@ExcelIgnore
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	@ExcelIgnore
	private Date updateTime;
	
	/**
	 * 是否删除(0-正常,1删除)
	 */
	@TableLogic
	@ExcelIgnore
	private Integer isDelete;
	
	@TableField(exist = false)
	private static final long serialVersionUID = 1L;
}