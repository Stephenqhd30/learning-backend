package com.kc.learning.model.vo.certificate;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;

/**
 * 证书 Excel 数据模型
 *
 * @author: stephen qiu
 * @create: 2024-09-27 09:22
 **/
@Data
public class CertificateExcelVO implements Serializable {
	private static final long serialVersionUID = -5741413222214936521L;
	/**
	 * id
	 */
	@ExcelProperty(value = "id")
	@ColumnWidth(30)
	private String id;
	
	/**
	 * 证书编号
	 */
	@ExcelProperty(value = "证书编号")
	@ColumnWidth(40)
	private String certificateNumber;
	
	/**
	 * 证书名称
	 */
	@ExcelProperty(value = "证书名称")
	@ColumnWidth(40)
	private String certificateName;
	
	/**
	 * 证书类型(0-干部培训,1-其他)
	 */
	@ExcelProperty(value = "证书类型(0-干部培训,1-其他)")
	@ColumnWidth(20)
	private String certificateType;
	
	/**
	 * 证书获得时间
	 */
	@ExcelProperty(value = "证书获得时间")
	@ColumnWidth(20)
	private String certificateYear;
	
	/**
	 * 证书获得情况(0-有,1-没有)
	 */
	@ExcelProperty(value = "证书获得情况(0-有,1-没有)")
	@ColumnWidth(20)
	private String certificateSituation;
	
	/**
	 * 证书状态(0-待审核,1-通过,2-拒绝)
	 */
	@ExcelProperty(value = "证书状态(0-待审核,1-通过,2-拒绝)")
	@ColumnWidth(20)
	private String reviewStatus;
	
	/**
	 * 审核信息
	 */
	@ExcelProperty(value = "审核信息")
	@ColumnWidth(30)
	private String reviewMessage;
	
	/**
	 * 审核人id
	 */
	@ExcelProperty(value = "审核人id")
	@ColumnWidth(30)
	private String reviewerId;
	
	/**
	 * 审核时间
	 */
	@ExcelProperty(value = "审核时间")
	@ColumnWidth(20)
	private String reviewTime;
	
	/**
	 * 获得人姓名
	 */
	@ExcelProperty(value = "获得人姓名")
	@ColumnWidth(30)
	private String userName;
	
	/**
	 * 获得人学号
	 */
	@ExcelProperty(value = "获得人学号")
	@ColumnWidth(30)
	private String userNumber;
	
	/**
	 * 证书地址下载地址
	 */
	@ExcelProperty(value = "证书地址下载地址")
	@ColumnWidth(60)
	private String certificateUrl;
}
