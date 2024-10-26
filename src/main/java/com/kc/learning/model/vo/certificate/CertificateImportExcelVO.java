package com.kc.learning.model.vo.certificate;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;

/**
 * 证书导入
 *
 * @author stephen qiu
 */
@Data
public class CertificateImportExcelVO implements Serializable {
	
	private static final long serialVersionUID = 5738550481524735808L;
	/**
	 * 证书编号
	 */
	@ExcelProperty(value = "证书编号")
	@ColumnWidth(30)
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
	@ColumnWidth(30)
	private Integer certificateType;
	
	/**
	 * 证书获得时间
	 */
	@ExcelProperty(value = "证书获得时间")
	@ColumnWidth(30)
	private String certificateYear;
	
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
	@ColumnWidth(40)
	private String userNumber;
}