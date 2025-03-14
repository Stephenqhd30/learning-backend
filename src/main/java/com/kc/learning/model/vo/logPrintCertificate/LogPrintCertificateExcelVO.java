package com.kc.learning.model.vo.logPrintCertificate;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;

/**
 * 打印证书日志视图
 *
 * @author stephen
 */
@Data
public class LogPrintCertificateExcelVO implements Serializable {
	
	private static final long serialVersionUID = 8635682960358749816L;
	
	/**
	 * 姓名
	 */
	@ExcelProperty("姓名")
	@ColumnWidth(30)
	private String userName;
	
	/**
	 * 身份证号
	 */
	@ExcelProperty("身份证号")
	@ColumnWidth(40)
	private String userIdCard;
	
	/**
	 * 性别(0-男, 1-女)
	 */
	@ExcelProperty("性别")
	@ColumnWidth(20)
	private String userGender;
	
	/**
	 * 证书编号
	 */
	@ExcelProperty("证书编号")
	@ColumnWidth(40)
	private String certificateNumber;
	
	/**
	 * 课程名
	 */
	@ExcelProperty("课程名")
	@ColumnWidth(50)
	private String courseName;
	
	/**
	 * 开课时间
	 */
	@ExcelProperty("开课时间")
	@DateTimeFormat("yyyy年MM月dd日")
	@ColumnWidth(30)
	private String acquisitionTime;
	
	/**
	 * 证书获得时间
	 */
	@ExcelProperty("证书获得时间")
	@DateTimeFormat("yyyy年MM月dd日")
	@ColumnWidth(30)
	private String finishTime;
	
}
