package com.kc.learning.model.vo.userCertificate;

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
public class UserCertificateExcelVO implements Serializable {
	private static final long serialVersionUID = -5741413222214936521L;
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
	@ColumnWidth(30)
	private String certificateName;
	
	/**
	 * 获得时间
	 */
	@ExcelProperty(value = "获得时间")
	@ColumnWidth(20)
	private String gainTime;
	
	/**
	 * 获得人名称
	 */
	@ExcelProperty(value = "获得人名称")
	@ColumnWidth(30)
	private String userName;
	
	/**
	 * 获得人学号
	 */
	@ExcelProperty(value = "获得人学号")
	@ColumnWidth(30)
	private String userNumber;
}
