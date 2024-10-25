package com.kc.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kc.learning.model.dto.user.UserQueryRequest;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.vo.user.LoginUserVO;
import com.kc.learning.model.vo.user.UserVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
	 * 用户登录
	 *
	 * @param userName   用户账户
	 * @param userIdCard 身份证号
	 * @param request    request
	 * @return 脱敏后的用户信息
	 */
	LoginUserVO userLogin(String userName, String userIdCard, HttpServletRequest request);
	
	/**
	 * 获取当前登录用户
	 *
	 * @param request request
	 * @return {@link User}
	 */
	User getLoginUser(HttpServletRequest request);
	
	/**
	 * 是否为管理员
	 *
	 * @param request request
	 * @return {@link boolean}
	 */
	boolean isAdmin(HttpServletRequest request);
	
	/**
	 * 是否为管理员
	 *
	 * @param user user
	 * @return {@link boolean}
	 */
	boolean isAdmin(User user);
	
	/**
	 * 用户注销
	 *
	 * @param request request
	 * @return {@link boolean}
	 */
	boolean userLogout(HttpServletRequest request);
	
	/**
	 * 获取脱敏的已登录用户信息
	 *
	 * @return {@link LoginUserVO}
	 */
	LoginUserVO getLoginUserVO(User user);
	
	/**
	 * 获取脱敏的用户信息
	 *
	 * @param user    user
	 * @param request request
	 * @return {@link UserVO}
	 */
	UserVO getUserVO(User user, HttpServletRequest request);
	
	
	/**
	 * 获取脱敏的用户信息
	 *
	 * @param userList userList
	 * @return {@link List}<{@link UserVO}>
	 */
	List<UserVO> getUserVO(List<User> userList, HttpServletRequest request);
	
	/**
	 * 获取查询条件
	 *
	 * @param userQueryRequest userQueryRequest
	 * @return {@link QueryWrapper}<{@link User}>
	 */
	QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);
	
	/**
	 * 导入用户
	 *
	 * @param file file
	 * @return {@link Map}<{@link String}, {@link Object}>
	 */
	Map<String, Object> importUsers(MultipartFile file);
}
