package com.stephen.popcorn.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author stephen qiu
 */
@Data
public class UserLoginRequest implements Serializable {
	
	private static final long serialVersionUID = 3191241716373120793L;
	
	/**
	 * 用户姓名
	 */
	private String userName;
	
	/**
	 * 身份证号
	 */
	private String userIdCard;
}
