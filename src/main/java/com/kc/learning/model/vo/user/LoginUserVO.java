package com.kc.learning.model.vo.user;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 已登录用户视图（脱敏）
 *
 * @author stephen qiu
 **/
@Data
public class LoginUserVO implements Serializable {
	
	private static final long serialVersionUID = 2837672255648064012L;
	/**
	 * 用户 id
	 */
	private Long id;
	
	/**
	 * 用户昵称
	 */
	private String userName;
	
	/**
	 * 用户头像
	 */
	private String userAvatar;
	
	/**
	 * 性别（0-男，1-女，2-保密）
	 */
	private Integer userGender;
	
	/**
	 * 用户角色：user/admin/ban
	 */
	private String userRole;
	
	/**
	 * 手机号码
	 */
	private String userPhone;
	
	/**
	 * 学号
	 */
	private String userNumber;
	
	/**
	 * 院系
	 */
	private String userDepartment;
	
	/**
	 * 年级（例如2024）
	 */
	private String userGrade;
	
	/**
	 * 专业
	 */
	private String userMajor;
	
	/**
	 *  token
	 */
	private String token;
	
}