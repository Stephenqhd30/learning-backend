package com.kc.learning.model.vo.certificate;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author: stephen qiu
 * @create: 2024-10-18 23:54
 **/
@Data
public class CertificatePrintVO implements Serializable {
	private static final long serialVersionUID = 3494789515343796912L;
	
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
	private Integer userGender;
	
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
	private Date acquisitionTime;
	
	/**
	 * 结课时间
	 */
	@ExcelProperty("结课时间")
	@DateTimeFormat("yyyy年MM月dd日")
	@ColumnWidth(30)
	private Date finishTime;
}
