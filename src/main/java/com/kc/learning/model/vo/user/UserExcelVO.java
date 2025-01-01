package com.kc.learning.model.vo.user;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户导出
 *
 * @author: stephen qiu
 * @create: 2024-09-26 14:04
 **/
@Data
public class UserExcelVO implements Serializable {
	private static final long serialVersionUID = -4002634298767485839L;
	/**
	 * 姓名
	 */
	@ColumnWidth(20)
	@ExcelProperty("姓名")
	private String userName;
	
	/**
	 * 身份证号
	 */
	@ColumnWidth(40)
	@ExcelProperty("身份证号")
	private String userIdCard;
	
	/**
	 * 性别（0-男，1-女，2-保密）
	 */
	@ColumnWidth(30)
	@ExcelProperty("性别（0-男，1-女，2-保密）")
	private String userGender;
	
	/**
	 * 用户简介
	 */
	@ExcelProperty("用户简介")
	@ColumnWidth(20)
	private String userProfile;
	
	/**
	 * 用户角色：user/admin/ban
	 */
	@ExcelProperty("用户角色：user/admin/ban")
	@ColumnWidth(20)
	private String userRole;
	
	/**
	 * 手机号码
	 */
	@ExcelProperty("手机号码")
	@ColumnWidth(20)
	private String userPhone;
	
	/**
	 * 学号
	 */
	@ExcelProperty("学号")
	@ColumnWidth(30)
	private String userNumber;
	
	/**
	 * 院系
	 */
	@ExcelProperty("院系")
	@ColumnWidth(30)
	private String userDepartment;
	
	/**
	 * 年级（例如2024）
	 */
	@ExcelProperty("年级")
	@ColumnWidth(30)
	private String userGrade;
	
	/**
	 * 专业
	 */
	@ExcelProperty("专业")
	@ColumnWidth(40)
	private String userMajor;
}
