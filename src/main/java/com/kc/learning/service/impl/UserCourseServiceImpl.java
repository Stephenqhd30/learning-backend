package com.kc.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.constants.CommonConstant;
import com.kc.learning.mapper.UserCourseMapper;
import com.kc.learning.model.dto.userCourse.UserCourseAddRequest;
import com.kc.learning.model.dto.userCourse.UserCourseQueryRequest;
import com.kc.learning.model.entity.Course;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.entity.UserCourse;
import com.kc.learning.model.vo.course.CourseVO;
import com.kc.learning.model.vo.user.UserVO;
import com.kc.learning.model.vo.userCourse.UserCourseVO;
import com.kc.learning.service.CourseService;
import com.kc.learning.service.UserCourseService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.SqlUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户课程服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class UserCourseServiceImpl extends ServiceImpl<UserCourseMapper, UserCourse> implements UserCourseService {
	
	@Resource
	private UserService userService;
	
	@Resource
	private CourseService courseService;
	
	/**
	 * 校验数据
	 *
	 * @param userCourse userCourse
	 * @param add        对创建的数据进行校验
	 */
	@Override
	public void validUserCourse(UserCourse userCourse, boolean add) {
		ThrowUtils.throwIf(userCourse == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		Long userId = userCourse.getUserId();
		Long courseId = userCourse.getCourseId();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(ObjectUtils.isEmpty(userId), ErrorCode.PARAMS_ERROR, "用户id不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(courseId), ErrorCode.PARAMS_ERROR, "课程id不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		User user = userService.getById(userId);
		ThrowUtils.throwIf(user == null, ErrorCode.PARAMS_ERROR, "用户不存在");
		Course course = courseService.getById(courseId);
		ThrowUtils.throwIf(course == null, ErrorCode.PARAMS_ERROR, "课程不存在");
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param userCourseQueryRequest userCourseQueryRequest
	 * @return {@link QueryWrapper <{@link UserCourse}>}
	 */
	@Override
	public QueryWrapper<UserCourse> getQueryWrapper(UserCourseQueryRequest userCourseQueryRequest) {
		QueryWrapper<UserCourse> queryWrapper = new QueryWrapper<>();
		if (userCourseQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = userCourseQueryRequest.getId();
		Long notId = userCourseQueryRequest.getNotId();
		Long userId = userCourseQueryRequest.getUserId();
		Long courseId = userCourseQueryRequest.getCourseId();
		String sortField = userCourseQueryRequest.getSortField();
		String sortOrder = userCourseQueryRequest.getSortOrder();
		// todo 补充需要的查询条件
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(courseId), "userId", userId);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取用户课程封装
	 *
	 * @param userCourse userCourse
	 * @param request    request
	 * @return {@link UserCourseVO }
	 */
	@Override
	public UserCourseVO getUserCourseVO(UserCourse userCourse, HttpServletRequest request) {
		// 对象转封装类
		UserCourseVO userCourseVO = UserCourseVO.objToVo(userCourse);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Long userId = userCourse.getUserId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		UserVO userVO = userService.getUserVO(user, request);
		Long courseId = userCourse.getCourseId();
		Course course = null;
		if (courseId != null && courseId > 0) {
			course = courseService.getById(courseId);
		}
		CourseVO courseVO = courseService.getCourseVO(course, request);
		// 2. 关联课程信息
		userCourseVO.setUserVO(userVO);
		userCourseVO.setCourseVO(courseVO);
		// endregion
		
		return userCourseVO;
	}
	
	/**
	 * 分页获取用户课程封装
	 *
	 * @param userCoursePage userCoursePage
	 * @param request        request
	 * @return {@link Page<UserCourseVO>}
	 */
	@Override
	public Page<UserCourseVO> getUserCourseVOPage(Page<UserCourse> userCoursePage, HttpServletRequest request) {
		List<UserCourse> userCourseList = userCoursePage.getRecords();
		Page<UserCourseVO> userCourseVOPage = new Page<>(userCoursePage.getCurrent(), userCoursePage.getSize(), userCoursePage.getTotal());
		if (CollUtil.isEmpty(userCourseList)) {
			return userCourseVOPage;
		}
		// 对象列表 => 封装对象列表
		List<UserCourseVO> userCourseVOList = userCourseList.stream().map(UserCourseVO::objToVo).collect(Collectors.toList());
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		// 2. 关联查询课程信息
		Set<Long> userIdSet = userCourseList.stream().map(UserCourse::getUserId).collect(Collectors.toSet());
		Set<Long> courseIdSet = userCourseList.stream().map(UserCourse::getCourseId).collect(Collectors.toSet());
		Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
				.collect(Collectors.groupingBy(User::getId));
		Map<Long, List<Course>> courseIdCourseListMap = courseService.listByIds(courseIdSet).stream()
				.collect(Collectors.groupingBy(Course::getId));
		// 填充信息
		userCourseVOList.forEach(userCourseVO -> {
			Long userId = userCourseVO.getUserId();
			Long courseId = userCourseVO.getCourseId();
			User user = null;
			Course course = null;
			if (userIdUserListMap.containsKey(userId)) {
				user = userIdUserListMap.get(userId).get(0);
			}
			if (courseIdCourseListMap.containsKey(courseId)) {
				course = courseIdCourseListMap.get(courseId).get(0);
			}
			userCourseVO.setUserVO(userService.getUserVO(user, request));
			userCourseVO.setCourseVO(courseService.getCourseVO(course, request));
		});
		// endregion
		userCourseVOPage.setRecords(userCourseVOList);
		return userCourseVOPage;
	}
	
}
