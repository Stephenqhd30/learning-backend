package com.kc.learning.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kc.learning.common.BaseResponse;
import com.kc.learning.common.DeleteRequest;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.common.exception.BusinessException;
import com.kc.learning.constants.UserConstant;
import com.kc.learning.model.dto.userCourse.UserCourseAddRequest;
import com.kc.learning.model.dto.userCourse.UserCourseQueryRequest;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.entity.UserCourse;
import com.kc.learning.model.vo.userCourse.UserCourseVO;
import com.kc.learning.service.UserCourseService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.ResultUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户课程接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/userCourse")
@Slf4j
public class UserCourseController {
	
	@Resource
	private UserCourseService userCourseService;
	
	@Resource
	private UserService userService;
	
	// region 增删改查
	
	/**
	 * 创建用户课程
	 *
	 * @param userCourseAddRequest userCourseAddRequest
	 * @param request              request
	 * @return {@link BaseResponse <{@link Long}>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addUserCourse(@RequestBody UserCourseAddRequest userCourseAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(userCourseAddRequest == null, ErrorCode.PARAMS_ERROR);
		// 拼接用户信息
		String userName = userCourseAddRequest.getUserName();
		String userNumber = userCourseAddRequest.getUserNumber();
		LambdaQueryWrapper<User> eq = Wrappers.lambdaQuery(User.class)
				.eq(User::getUserName, userName)
				.eq(User::getUserNumber, userNumber);
		User user = userService.getOne(eq);
		ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "添加的用户不存在");
		// todo 在此处将实体类和 DTO 进行转换
		UserCourse userCourse = new UserCourse();
		BeanUtils.copyProperties(userCourseAddRequest, userCourse);
		userCourse.setUserId(user.getId());
		// 数据校验
		try {
			userCourseService.validUserCourse(userCourse, true);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, e.getMessage());
		}
		// 避免重复添加信息
		LambdaQueryWrapper<UserCourse> userCourseLambdaQueryWrapper = Wrappers.lambdaQuery(UserCourse.class)
				.eq(UserCourse::getUserId, userCourse.getUserId())
				.eq(UserCourse::getCourseId, userCourse.getCourseId());
		UserCourse oldUserCourse = userCourseService.getOne(userCourseLambdaQueryWrapper);
		ThrowUtils.throwIf(oldUserCourse != null, ErrorCode.PARAMS_ERROR, "用户已经加入课程");
		// todo 补充默认信息
		// 写入数据库
		boolean result = userCourseService.save(userCourse);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newUserCourseId = userCourse.getId();
		return ResultUtils.success(newUserCourseId);
	}
	
	/**
	 * 删除用户课程
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse <{@link Boolean}>}
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteUserCourse(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		UserCourse oldUserCourse = userCourseService.getById(id);
		ThrowUtils.throwIf(oldUserCourse == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldUserCourse.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = userCourseService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	
	/**
	 * 根据 id 获取用户课程（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse <{@link UserCourseVO}>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<UserCourseVO> getUserCourseVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		UserCourse userCourse = userCourseService.getById(id);
		ThrowUtils.throwIf(userCourse == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(userCourseService.getUserCourseVO(userCourse, request));
	}
	
	/**
	 * 分页获取用户课程列表（仅管理员可用）
	 *
	 * @param userCourseQueryRequest userCourseQueryRequest
	 * @return {@link BaseResponse <{@link Page <{@link UserCourse}>}>}
	 */
	@PostMapping("/list/page")
	@SaCheckRole(UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<UserCourse>> listUserCourseByPage(@RequestBody UserCourseQueryRequest userCourseQueryRequest) {
		long current = userCourseQueryRequest.getCurrent();
		long size = userCourseQueryRequest.getPageSize();
		// 查询数据库
		Page<UserCourse> userCoursePage = userCourseService.page(new Page<>(current, size),
				userCourseService.getQueryWrapper(userCourseQueryRequest));
		return ResultUtils.success(userCoursePage);
	}
	
	/**
	 * 分页获取用户课程列表（封装类）
	 *
	 * @param userCourseQueryRequest userCourseQueryRequest
	 * @param request                request
	 * @return {@link BaseResponse <{@link Page <{@link UserCourseVO}>}>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<UserCourseVO>> listUserCourseVOByPage(@RequestBody UserCourseQueryRequest userCourseQueryRequest,
	                                                               HttpServletRequest request) {
		long current = userCourseQueryRequest.getCurrent();
		long size = userCourseQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<UserCourse> userCoursePage = userCourseService.page(new Page<>(current, size),
				userCourseService.getQueryWrapper(userCourseQueryRequest));
		// 获取封装类
		return ResultUtils.success(userCourseService.getUserCourseVOPage(userCoursePage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的用户课程列表
	 *
	 * @param userCourseQueryRequest userCourseQueryRequest
	 * @param request                request
	 * @return {@link BaseResponse <{@link Page <{@link UserCourseVO}>}>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<UserCourseVO>> listMyUserCourseVOByPage(@RequestBody UserCourseQueryRequest userCourseQueryRequest,
	                                                                 HttpServletRequest request) {
		ThrowUtils.throwIf(userCourseQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		userCourseQueryRequest.setUserId(loginUser.getId());
		long current = userCourseQueryRequest.getCurrent();
		long size = userCourseQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<UserCourse> userCoursePage = userCourseService.page(new Page<>(current, size),
				userCourseService.getQueryWrapper(userCourseQueryRequest));
		// 获取封装类
		return ResultUtils.success(userCourseService.getUserCourseVOPage(userCoursePage, request));
	}
	
	// endregion
}