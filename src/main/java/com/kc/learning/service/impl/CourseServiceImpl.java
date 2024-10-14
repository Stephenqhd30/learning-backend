package com.kc.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.constants.CommonConstant;
import com.kc.learning.utils.ThrowUtils;
import com.kc.learning.mapper.CourseMapper;
import com.kc.learning.model.dto.course.CourseQueryRequest;
import com.kc.learning.model.entity.Course;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.vo.CourseVO;
import com.kc.learning.model.vo.UserVO;
import com.kc.learning.service.CourseService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 课程服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements CourseService {
	
	@Resource
	private UserService userService;
	
	/**
	 * 校验数据
	 *
	 * @param course course
	 * @param add    对创建的数据进行校验
	 */
	@Override
	public void validCourse(Course course, boolean add) {
		ThrowUtils.throwIf(course == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		Integer courseNumber = course.getCourseNumber();
		String courseName = course.getCourseName();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(ObjectUtils.isEmpty(courseNumber), ErrorCode.PARAMS_ERROR, "课程号不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(courseName), ErrorCode.PARAMS_ERROR, "课程名不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(courseName)) {
			ThrowUtils.throwIf(courseName.length() > 256, ErrorCode.PARAMS_ERROR, "课程名称过长");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param courseQueryRequest courseQueryRequest
	 * @return {@link QueryWrapper<Course>}
	 */
	@Override
	public QueryWrapper<Course> getQueryWrapper(CourseQueryRequest courseQueryRequest) {
		QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
		if (courseQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = courseQueryRequest.getId();
		Long notId = courseQueryRequest.getNotId();
		Integer courseNumber = courseQueryRequest.getCourseNumber();
		String courseName = courseQueryRequest.getCourseName();
		Long userId = courseQueryRequest.getUserId();
		String sortField = courseQueryRequest.getSortField();
		String sortOrder = courseQueryRequest.getSortOrder();
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(courseName), "courseName", courseName);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(courseNumber), "courseNumber", courseNumber);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取课程封装
	 *
	 * @param course  course
	 * @param request request
	 * @return {@link CourseVO}
	 */
	@Override
	public CourseVO getCourseVO(Course course, HttpServletRequest request) {
		// 对象转封装类
		CourseVO courseVO = CourseVO.objToVo(course);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Long userId = course.getUserId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		UserVO userVO = userService.getUserVO(user, request);
		courseVO.setUserVO(userVO);
		// endregion
		
		return courseVO;
	}
	
	/**
	 * 分页获取课程封装
	 *
	 * @param coursePage coursePage
	 * @param request    request
	 * @return {@link Page<CourseVO>}
	 */
	@Override
	public Page<CourseVO> getCourseVOPage(Page<Course> coursePage, HttpServletRequest request) {
		List<Course> courseList = coursePage.getRecords();
		Page<CourseVO> courseVOPage = new Page<>(coursePage.getCurrent(), coursePage.getSize(), coursePage.getTotal());
		if (CollUtil.isEmpty(courseList)) {
			return courseVOPage;
		}
		// 对象列表 => 封装对象列表
		List<CourseVO> courseVOList = courseList.stream().map(course -> {
			return CourseVO.objToVo(course);
		}).collect(Collectors.toList());
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> userIdSet = courseList.stream().map(Course::getUserId).collect(Collectors.toSet());
		Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
				.collect(Collectors.groupingBy(User::getId));
		// 填充信息
		courseVOList.forEach(courseVO -> {
			Long userId = courseVO.getUserId();
			User user = null;
			if (userIdUserListMap.containsKey(userId)) {
				user = userIdUserListMap.get(userId).get(0);
			}
			courseVO.setUserVO(userService.getUserVO(user, request));
		});
		// endregion
		courseVOPage.setRecords(courseVOList);
		return courseVOPage;
	}
	
}
