package com.stephen.popcorn.model.dto.user;

import com.stephen.popcorn.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 用户查询请求
 *
 * @author stephen qiu
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
	private static final long serialVersionUID = 8796619426266616906L;
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
	
}