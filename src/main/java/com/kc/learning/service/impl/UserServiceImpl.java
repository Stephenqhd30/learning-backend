package com.kc.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kc.learning.aop.UserExcelListener;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.constants.CommonConstant;
import com.kc.learning.constants.UserConstant;
import com.kc.learning.exception.BusinessException;
import com.kc.learning.mapper.UserMapper;
import com.kc.learning.model.dto.user.UserQueryRequest;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.enums.UserGenderEnum;
import com.kc.learning.model.enums.UserRoleEnum;
import com.kc.learning.model.vo.user.LoginUserVO;
import com.kc.learning.model.vo.user.UserVO;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.EncryptionUtils;
import com.kc.learning.utils.RegexUtils;
import com.kc.learning.utils.SqlUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 用户服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
	
	/**
	 * 校验数据
	 *
	 * @param user user
	 * @param add  对创建的数据进行校验
	 */
	@Override
	public void validUser(User user, boolean add) {
		ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String userIdCard = user.getUserIdCard();
		String userName = user.getUserName();
		Integer userGender = user.getUserGender();
		String userProfile = user.getUserProfile();
		String userEmail = user.getUserEmail();
		String userPhone = user.getUserPhone();
		String userNumber = user.getUserNumber();
		
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(userName), ErrorCode.PARAMS_ERROR, "姓名不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(userIdCard), ErrorCode.PARAMS_ERROR, "身份证号不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(userNumber), ErrorCode.PARAMS_ERROR, "学号不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(userName)) {
			ThrowUtils.throwIf(!RegexUtils.checkUserName(userName), ErrorCode.PARAMS_ERROR, "用户名输入有误");
		}
		if (StringUtils.isNotBlank(userIdCard)) {
			ThrowUtils.throwIf(!RegexUtils.checkIdCard(userIdCard), ErrorCode.PARAMS_ERROR, "身份证号输入有误");
		}
		if (StringUtils.isNotBlank(userNumber)) {
			ThrowUtils.throwIf(!RegexUtils.checkSchoolNumber(userNumber), ErrorCode.PARAMS_ERROR, "学号输入有误");
		}
		if (StringUtils.isNotBlank(userProfile)) {
			ThrowUtils.throwIf(userProfile.length() > 50, ErrorCode.PARAMS_ERROR, "用户简介不能多余50字");
		}
		if (StringUtils.isNotBlank(userEmail)) {
			ThrowUtils.throwIf(!RegexUtils.checkEmail(userEmail), ErrorCode.PARAMS_ERROR, "用户邮箱有误");
		}
		if (StringUtils.isNotBlank(userPhone)) {
			ThrowUtils.throwIf(!RegexUtils.checkMobile(userPhone), ErrorCode.PARAMS_ERROR, "用户手机号码有误");
		}
		if (ObjectUtils.isNotEmpty(userGender)) {
			ThrowUtils.throwIf(UserGenderEnum.getEnumByValue(userGender) == null, ErrorCode.PARAMS_ERROR, "性别填写有误");
		}
	}
	
	/**
	 * 用户注册
	 *
	 * @param userName        用户账户
	 * @param userIdCard      身份证号
	 * @param userCheckIdCard 校验密码
	 * @return long
	 */
	@Override
	public long userRegister(String userName, String userIdCard, String userCheckIdCard, String userNumber) {
		// 1. 校验
		if (StringUtils.isAnyBlank(userName, userIdCard, userCheckIdCard, userNumber)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
		}
		ThrowUtils.throwIf(!RegexUtils.checkIdCard(userIdCard), ErrorCode.PARAMS_ERROR, "身份证号输入有误");
		ThrowUtils.throwIf(!RegexUtils.checkUserName(userName), ErrorCode.PARAMS_ERROR, "姓名输入有误");
		// 身份证号和校验身份证号相同
		if (!userIdCard.equals(userCheckIdCard)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的身份证号不一致");
		}
		synchronized (userIdCard.intern()) {
			// 身份证号不能重复
			QueryWrapper<User> queryWrapper = new QueryWrapper<>();
			queryWrapper.eq("userIdCard", userIdCard);
			long count = this.baseMapper.selectCount(queryWrapper);
			if (count > 0) {
				throw new BusinessException(ErrorCode.PARAMS_ERROR, "身份证号重复");
			}
			// 2. 加密
			String encryptIdCard = null;
			try {
				encryptIdCard = EncryptionUtils.encrypt(userIdCard);
			} catch (Exception e) {
				throw new BusinessException(ErrorCode.SYSTEM_ERROR);
			}
			// 3. 插入数据
			User user = new User();
			user.setUserName(userName);
			user.setUserIdCard(encryptIdCard);
			user.setUserNumber(userNumber);
			// 3. 给用户分配一个默认的头像
			user.setUserAvatar(UserConstant.USER_AVATAR);
			boolean saveResult = this.save(user);
			if (!saveResult) {
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
			}
			return user.getId();
		}
	}
	
	@Override
	public LoginUserVO userLogin(String userName, String userIdCard, HttpServletRequest request) {
		// 1. 校验
		if (StringUtils.isAnyBlank(userName, userIdCard)) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
		}
		ThrowUtils.throwIf(!RegexUtils.checkUserName(userName), ErrorCode.PARAMS_ERROR, "用户名输入有误");
		ThrowUtils.throwIf(!RegexUtils.checkIdCard(userIdCard), ErrorCode.PARAMS_ERROR, "身份证号输入有误");
		// 2. 加密
		String encryptIdCard = null;
		try {
			encryptIdCard = EncryptionUtils.encrypt(userIdCard);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR);
		}
		// 查询用户是否存在
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		queryWrapper.eq("userName", userName);
		queryWrapper.eq("userIdCard", encryptIdCard);
		User user = this.baseMapper.selectOne(queryWrapper);
		// 用户不存在
		if (user == null) {
			log.info("user login failed, userName cannot match userIdCard");
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
		}
		// 3. 记录用户的登录态
		request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, user);
		return this.getLoginUserVO(user);
	}
	
	/**
	 * 获取当前登录用户
	 *
	 * @param request
	 * @return
	 */
	@Override
	public User getLoginUser(HttpServletRequest request) {
		// 先判断是否已登录
		Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
		User currentUser = (User) userObj;
		if (currentUser == null || currentUser.getId() == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
		}
		// 从数据库查询（追求性能的话可以注释，直接走缓存）
		long userId = currentUser.getId();
		currentUser = this.getById(userId);
		if (currentUser == null) {
			throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
		}
		return currentUser;
	}
	
	
	/**
	 * 是否为管理员
	 *
	 * @param request
	 * @return
	 */
	@Override
	public boolean isAdmin(HttpServletRequest request) {
		// 仅管理员可查询
		Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
		User user = (User) userObj;
		return isAdmin(user);
	}
	
	@Override
	public boolean isAdmin(User user) {
		return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
	}
	
	/**
	 * 用户注销
	 *
	 * @param request
	 */
	@Override
	public boolean userLogout(HttpServletRequest request) {
		if (request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE) == null) {
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
		}
		// 移除登录态
		request.getSession().removeAttribute(UserConstant.USER_LOGIN_STATE);
		return true;
	}
	
	@Override
	public LoginUserVO getLoginUserVO(User user) {
		if (user == null) {
			return null;
		}
		// todo 在此处将实体类和 DTO 进行转换
		LoginUserVO loginUserVO = new LoginUserVO();
		BeanUtils.copyProperties(user, loginUserVO);
		return loginUserVO;
	}
	
	@Override
	public UserVO getUserVO(User user, HttpServletRequest request) {
		// 对象转封装类
		return UserVO.objToVo(user);
	}
	
	
	@Override
	public List<UserVO> getUserVO(List<User> userList, HttpServletRequest request) {
		if (CollUtil.isEmpty(userList)) {
			return new ArrayList<>();
		}
		return userList.stream().map(user -> getUserVO(user, request)).collect(Collectors.toList());
	}
	
	@Override
	public QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest) {
		if (userQueryRequest == null) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
		}
		Long id = userQueryRequest.getId();
		String encryptedUserIdCard = null;
		if (StringUtils.isNotBlank(userQueryRequest.getUserIdCard())) {
			encryptedUserIdCard = EncryptionUtils.encrypt(userQueryRequest.getUserIdCard());
		}
		String userName = userQueryRequest.getUserName();
		Integer userGender = userQueryRequest.getUserGender();
		String userProfile = userQueryRequest.getUserProfile();
		String userRole = userQueryRequest.getUserRole();
		String userEmail = userQueryRequest.getUserEmail();
		String userPhone = userQueryRequest.getUserPhone();
		String sortField = userQueryRequest.getSortField();
		String sortOrder = userQueryRequest.getSortOrder();
		String userNumber = userQueryRequest.getUserNumber();
		
		QueryWrapper<User> queryWrapper = new QueryWrapper<>();
		
		// 精准查询
		queryWrapper.eq(id != null, "id", id);
		queryWrapper.eq(StringUtils.isNotBlank(encryptedUserIdCard), "userIdCard", encryptedUserIdCard);
		queryWrapper.eq(StringUtils.isNotBlank(userRole), "userRole", userRole);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userGender), "userGender", userGender);
		
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(userProfile), "userProfile", userProfile);
		queryWrapper.like(StringUtils.isNotBlank(userNumber), "userNumber", userNumber);
		queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
		queryWrapper.like(StringUtils.isNotBlank(userEmail), "userEmail", userEmail);
		queryWrapper.like(StringUtils.isNotBlank(userPhone), "userPhone", userPhone);
		queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 导入用户数据
	 *
	 * @param file 上传的 Excel 文件
	 * @return 返回成功和错误信息
	 */
	@Override
	public Map<String, Object> importUsers(MultipartFile file) {
		ThrowUtils.throwIf(file == null || file.isEmpty(), ErrorCode.OPERATION_ERROR, "上传的文件为空");
		
		// 传递 userService 实例给 UserExcelListener
		UserExcelListener listener = new UserExcelListener(this);
		
		try {
			EasyExcel.read(file.getInputStream(), User.class, listener).sheet().doRead();
		} catch (IOException e) {
			log.error("文件读取失败: {}", e.getMessage());
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件读取失败");
		} catch (ExcelAnalysisException e) {
			log.error("Excel解析失败: {}", e.getMessage());
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "Excel解析失败");
		}
		
		// 返回处理结果，包括成功和异常的数据
		Map<String, Object> result = new HashMap<>();
		// 获取异常记录
		result.put("errorRecords", listener.getErrorRecords());
		
		log.info("成功导入 {} 条用户数据，{} 条错误数据", listener.getSuccessRecords().size(), listener.getErrorRecords().size());
		
		return result;
	}
}
