package com.kc.learning.constants;

/**
 * 用户常量
 *
 * @author stephen qiu
 */
public interface UserConstant {
	
	/**
	 * 用户登录态键
	 */
	String USER_LOGIN_STATE = "user_login";
	
	/**
	 * 用户默认头像地址
	 */
	String USER_AVATAR = "http://152.136.235.18:19000/base/R-C.jpg";
	
	
	//  region 权限
	
	/**
	 * 默认角色
	 */
	String DEFAULT_ROLE = "user";
	
	/**
	 * 管理员角色
	 */
	String ADMIN_ROLE = "admin";
	
	/**
	 * 被封号
	 */
	String BAN_ROLE = "ban";
	
	// endregion
}
