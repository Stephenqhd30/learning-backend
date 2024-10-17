package com.kc.learning.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求体
 *
 * @author stephen qiu
 */
@Data
public class UserRegisterRequest implements Serializable {
	
	private static final long serialVersionUID = 3191241716373120793L;
	
	/**
	 * 用户名
	 */
	private String userName;
	
	/**
	 * 身份证号
	 */
	private String userIdCard;
	
	/**
	 * 再次输入身份证号
	 */
	private String userCheckIdCard;
	
	/**
	 * 学号
	 */
	private String userNumber;
}
