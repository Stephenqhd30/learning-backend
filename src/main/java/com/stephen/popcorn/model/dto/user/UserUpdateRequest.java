package com.stephen.popcorn.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户更新请求
 *
 * @author stephen qiu
 */
@Data
public class UserUpdateRequest implements Serializable {
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 身份证号
	 */
	private String userIdCard;
	
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
	 * 用户简介
	 */
	private String userProfile;
	
	/**
	 * 用户角色：user/admin/ban
	 */
	private String userRole;
	
	/**
	 * 用户邮箱
	 */
	private String userEmail;
	
	/**
	 * 手机号码
	 */
	private String userPhone;
	
	/**
	 * 学号
	 */
	private String userNumber;
	
	private static final long serialVersionUID = 1L;
}