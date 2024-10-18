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
public class UserExcelVO implements Serializable {
	private static final long serialVersionUID = -4002634298767485839L;
	/**
	 * id
	 */
	@ColumnWidth(30)
	@ExcelProperty("id")
	private String id;
	
	
	/**
	 * 身份证号
	 */
	@ColumnWidth(40)
	@ExcelProperty("身份证号")
	private String userIdCard;
	
	/**
	 * 姓名
	 */
	@ColumnWidth(20)
	@ExcelProperty("姓名")
	private String userName;
	
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
	@ColumnWidth(30)
	private String userRole;
	
	/**
	 * 用户邮箱
	 */
	@ExcelProperty("用户邮箱")
	@ColumnWidth(20)
	private String userEmail;
	
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
	@ColumnWidth(20)
	private String userNumber;
	
	/**
	 * 创建时间
	 */
	@ExcelProperty("创建时间")
	@ColumnWidth(20)
	private String createTime;
	
	/**
	 * 更新时间
	 */
	@ExcelProperty("更新时间")
	@ColumnWidth(20)
	private String updateTime;
}
