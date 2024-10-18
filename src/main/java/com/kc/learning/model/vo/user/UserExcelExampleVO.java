package com.kc.learning.model.vo.user;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: stephen qiu
 * @create: 2024-09-26 14:04
 **/
@Data
public class UserExcelExampleVO implements Serializable {
	private static final long serialVersionUID = -4002634298767485839L;
	
	/**
	 * 姓名
	 */
	@ColumnWidth(30)
	@ExcelProperty("姓名")
	private String userName;
	
	/**
	 * 身份证号
	 */
	@ColumnWidth(40)
	@ExcelProperty("身份证号")
	private String userIdCard;
	
	/**
	 * 学号
	 */
	@ExcelProperty("学号")
	@ColumnWidth(30)
	private String userNumber;
	
	
	/**
	 * 性别（0-男，1-女，2-保密）
	 */
	@ColumnWidth(35)
	@ExcelProperty("性别（0-男，1-女，2-保密）")
	private String userGender;
	
	/**
	 * 用户简介
	 */
	@ExcelProperty("用户简介")
	@ColumnWidth(35)
	private String userProfile;
	
	
	/**
	 * 用户邮箱
	 */
	@ExcelProperty("用户邮箱")
	@ColumnWidth(30)
	private String userEmail;
	
	/**
	 * 手机号码
	 */
	@ExcelProperty("手机号码")
	@ColumnWidth(30)
	private String userPhone;
}
