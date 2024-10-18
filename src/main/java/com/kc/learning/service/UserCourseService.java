package com.kc.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kc.learning.model.dto.userCourse.UserCourseQueryRequest;
import com.kc.learning.model.entity.UserCourse;
import com.kc.learning.model.vo.userCourse.UserCourseVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户课程服务
 *
 * @author stephen qiu
 */
public interface UserCourseService extends IService<UserCourse> {
	
	/**
	 * 校验数据
	 *
	 * @param userCourse userCourse
	 * @param add        对创建的数据进行校验
	 */
	void validUserCourse(UserCourse userCourse, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param userCourseQueryRequest userCourseQueryRequest
	 * @return {@link QueryWrapper<UserCourse>}
	 */
	QueryWrapper<UserCourse> getQueryWrapper(UserCourseQueryRequest userCourseQueryRequest);
	
	/**
	 * 获取用户课程封装
	 *
	 * @param userCourse userCourse
	 * @param request    request
	 * @return {@link UserCourseVO}
	 */
	UserCourseVO getUserCourseVO(UserCourse userCourse, HttpServletRequest request);
	
	/**
	 * 分页获取用户课程封装
	 *
	 * @param userCoursePage userCoursePage
	 * @param request        request
	 * @return {@link Page<UserCourseVO>}
	 */
	Page<UserCourseVO> getUserCourseVOPage(Page<UserCourse> userCoursePage, HttpServletRequest request);
}