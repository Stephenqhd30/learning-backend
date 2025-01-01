package com.kc.learning.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新个人信息请求
 *
 * @author stephen qiu
 */
@Data
public class UserEditRequest implements Serializable {
	
	private static final long serialVersionUID = 402901746420005392L;
	
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
	
}