package com.kc.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kc.learning.model.dto.user.UserQueryRequest;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.vo.LoginUserVO;
import com.kc.learning.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author stephen qiu
 */
public interface UserService extends IService<User> {
	
	/**
	 * 校验用户参数
	 *
	 * @param user user
	 * @param add  是否是添加
	 */
	void validUser(User user, boolean add);
	
	/**
	 * 用户注册
	 *
	 * @param userName      用户账户
	 * @param userIdCard    身份证号
	 * @param checkPassword 校验密码
	 * @return 新用户 id
	 */
	long userRegister(String userName, String userIdCard, String checkPassword) throws Exception;
	
	/**
	 * 用户登录
	 *
	 * @param userName   用户账户
	 * @param userIdCard 身份证号
	 * @param request
	 * @return 脱敏后的用户信息
	 */
	LoginUserVO userLogin(String userName, String userIdCard, HttpServletRequest request);
	
	/**
	 * 获取当前登录用户
	 *
	 * @param request
	 * @return
	 */
	User getLoginUser(HttpServletRequest request);
	
	/**
	 * 获取当前登录用户（允许未登录）
	 *
	 * @param request
	 * @return
	 */
	User getLoginUserPermitNull(HttpServletRequest request);
	
	/**
	 * 是否为管理员
	 *
	 * @param request
	 * @return
	 */
	boolean isAdmin(HttpServletRequest request);
	
	/**
	 * 是否为管理员
	 *
	 * @param user
	 * @return
	 */
	boolean isAdmin(User user);
	
	/**
	 * 用户注销
	 *
	 * @param request
	 * @return
	 */
	boolean userLogout(HttpServletRequest request);
	
	/**
	 * 获取脱敏的已登录用户信息
	 *
	 * @return
	 */
	LoginUserVO getLoginUserVO(User user);
	
	/**
	 * 获取脱敏的用户信息
	 *
	 * @param user
	 * @param request
	 * @return
	 */
	UserVO getUserVO(User user, HttpServletRequest request);
	
	
	/**
	 * 获取脱敏的用户信息
	 *
	 * @param userList
	 * @return
	 */
	List<UserVO> getUserVO(List<User> userList, HttpServletRequest request);
	
	/**
	 * 获取查询条件
	 *
	 * @param userQueryRequest
	 * @return
	 */
	QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
	
}
