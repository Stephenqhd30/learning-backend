package com.kc.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kc.learning.annotation.AuthCheck;
import com.kc.learning.common.BaseResponse;
import com.kc.learning.common.DeleteRequest;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.constants.UserConstant;
import com.kc.learning.exception.BusinessException;
import com.kc.learning.model.dto.course.CourseAddRequest;
import com.kc.learning.model.dto.course.CourseQueryRequest;
import com.kc.learning.model.dto.course.CourseUpdateRequest;
import com.kc.learning.model.entity.Course;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.vo.course.CourseVO;
import com.kc.learning.service.CourseService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.ResultUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 课程接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/course")
@Slf4j
public class CourseController {
	
	@Resource
	private CourseService courseService;
	
	@Resource
	private UserService userService;
	
	// region 增删改查
	
	/**
	 * 创建课程
	 *
	 * @param courseAddRequest courseAddRequest
	 * @param request          request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Long> addCourse(@RequestBody CourseAddRequest courseAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(courseAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		Course course = new Course();
		BeanUtils.copyProperties(courseAddRequest, course);
		// 数据校验
		try {
			courseService.validCourse(course, true);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, e.getMessage());
		}
		// todo 填充默认值
		User loginUser = userService.getLoginUser(request);
		course.setUserId(loginUser.getId());
		// 写入数据库
		boolean result = courseService.save(course);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newCourseId = course.getId();
		return ResultUtils.success(newCourseId);
	}
	
	/**
	 * 删除课程
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteCourse(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		Course oldCourse = courseService.getById(id);
		ThrowUtils.throwIf(oldCourse == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldCourse.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = courseService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新课程（仅管理员可用）
	 *
	 * @param courseUpdateRequest courseUpdateRequest
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateCourse(@RequestBody CourseUpdateRequest courseUpdateRequest) {
		if (courseUpdateRequest == null || courseUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		Course course = new Course();
		BeanUtils.copyProperties(courseUpdateRequest, course);
		// 数据校验
		try {
			courseService.validCourse(course, false);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, e.getMessage());
		}
		// 判断是否存在
		long id = courseUpdateRequest.getId();
		Course oldCourse = courseService.getById(id);
		ThrowUtils.throwIf(oldCourse == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = courseService.updateById(course);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取课程（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<CourseVO>}
	 */
	@GetMapping("/get/vo")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<CourseVO> getCourseVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Course course = courseService.getById(id);
		ThrowUtils.throwIf(course == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(courseService.getCourseVO(course, request));
	}
	
	/**
	 * 分页获取课程列表（仅管理员可用）
	 *
	 * @param courseQueryRequest courseQueryRequest
	 * @return {@link BaseResponse<Page<Course>>}
	 */
	@PostMapping("/list/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<Course>> listCourseByPage(@RequestBody CourseQueryRequest courseQueryRequest) {
		long current = courseQueryRequest.getCurrent();
		long size = courseQueryRequest.getPageSize();
		// 查询数据库
		Page<Course> coursePage = courseService.page(new Page<>(current, size),
				courseService.getQueryWrapper(courseQueryRequest));
		return ResultUtils.success(coursePage);
	}
	
	/**
	 * 分页获取课程列表（封装类）
	 *
	 * @param courseQueryRequest courseQueryRequest
	 * @param request            request
	 * @return {@link BaseResponse {@link Page} {@link CourseVO }}
	 */
	@PostMapping("/list/page/vo")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<CourseVO>> listCourseVOByPage(@RequestBody CourseQueryRequest courseQueryRequest,
	                                                       HttpServletRequest request) {
		long current = courseQueryRequest.getCurrent();
		long size = courseQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Course> coursePage = courseService.page(new Page<>(current, size),
				courseService.getQueryWrapper(courseQueryRequest));
		// 获取封装类
		return ResultUtils.success(courseService.getCourseVOPage(coursePage, request));
	}
	
	/**
	 * 分页获取当前登录课程创建的课程列表
	 *
	 * @param courseQueryRequest courseQueryRequest
	 * @param request            request
	 * @return {@link BaseResponse {@link Page} {@link CourseVO }}
	 */
	@PostMapping("/my/list/page/vo")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<CourseVO>> listMyCourseVOByPage(@RequestBody CourseQueryRequest courseQueryRequest,
	                                                         HttpServletRequest request) {
		ThrowUtils.throwIf(courseQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录课程的数据
		User loginUser = userService.getLoginUser(request);
		courseQueryRequest.setUserId(loginUser.getId());
		long current = courseQueryRequest.getCurrent();
		long size = courseQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Course> coursePage = courseService.page(new Page<>(current, size),
				courseService.getQueryWrapper(courseQueryRequest));
		// 获取封装类
		return ResultUtils.success(courseService.getCourseVOPage(coursePage, request));
	}
	// endregion
}