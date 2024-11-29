package com.kc.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kc.learning.aop.excel.UserCourseExcelListener;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.constants.CommonConstant;
import com.kc.learning.common.exception.BusinessException;
import com.kc.learning.mapper.UserCourseMapper;
import com.kc.learning.model.dto.userCourse.UserCourseQueryRequest;
import com.kc.learning.model.entity.Course;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.entity.UserCourse;
import com.kc.learning.model.vo.course.CourseVO;
import com.kc.learning.model.vo.user.UserVO;
import com.kc.learning.model.vo.userCourse.UserCourseExcelVO;
import com.kc.learning.model.vo.userCourse.UserCourseVO;
import com.kc.learning.service.CourseService;
import com.kc.learning.service.UserCourseService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.SqlUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
		Long createUserId = userCourseQueryRequest.getCreateUserId();
		String sortField = userCourseQueryRequest.getSortField();
		String sortOrder = userCourseQueryRequest.getSortOrder();
		// todo 补充需要的查询条件
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(courseId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(createUserId), "createUserId", createUserId);
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
		// 1. 关联查询用户信息
		CompletableFuture<UserVO> userFuture = CompletableFuture.supplyAsync(() -> {
			Long userId = userCourse.getUserId();
			
			if (userId != null && userId > 0) {
				User user = userService.getById(userId);
				return userService.getUserVO(user, request);
			}
			return null;
		});
		// 2. 关联查询课程信息
		CompletableFuture<CourseVO> courseFuture = CompletableFuture.supplyAsync(() -> {
			Long courseId = userCourse.getCourseId();
			if (courseId != null && courseId > 0) {
				Course course = courseService.getById(courseId);
				return courseService.getCourseVO(course, request);
			}
			return null;
		});
		
		try {
			userCourseVO.setUserVO(userFuture.get());
			userCourseVO.setCourseVO(courseFuture.get());
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取用户课程封装失败");
		}
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
		// 转换为 UserCourseVO 列表
		List<UserCourseVO> userCourseVOList = userCourseList.stream()
				.map(UserCourseVO::objToVo)
				.collect(Collectors.toList());
		
		// 提取用户和课程的 ID 集合
		Set<Long> userIdSet = userCourseList.stream().map(UserCourse::getUserId).collect(Collectors.toSet());
		Set<Long> courseIdSet = userCourseList.stream().map(UserCourse::getCourseId).collect(Collectors.toSet());
		
		// 并发查询用户和课程信息
		CompletableFuture<Map<Long, User>> userFuture = CompletableFuture.supplyAsync(() ->
				userService.listByIds(userIdSet).stream().collect(Collectors.toMap(User::getId, user -> user))
		);
		CompletableFuture<Map<Long, Course>> courseFuture = CompletableFuture.supplyAsync(() ->
				courseService.listByIds(courseIdSet).stream().collect(Collectors.toMap(Course::getId, course -> course))
		);
		
		try {
			// 获取查询结果
			Map<Long, User> userMap = userFuture.get();
			Map<Long, Course> courseMap = courseFuture.get();
			
			// 填充 UserCourseVO 的用户和课程信息
			userCourseVOList.forEach(userCourseVO -> {
				Long userId = userCourseVO.getUserId();
				Long courseId = userCourseVO.getCourseId();
				
				User user = userMap.get(userId);
				Course course = courseMap.get(courseId);
				
				if (user != null) {
					userCourseVO.setUserVO(userService.getUserVO(user, request));
				}
				if (course != null) {
					userCourseVO.setCourseVO(courseService.getCourseVO(course, request));
				}
			});
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取用户课程封装失败");
		}
		
		// 设置分页结果
		userCourseVOPage.setRecords(userCourseVOList);
		return userCourseVOPage;
	}
	
	/**
	 * 导入课程数据
	 *
	 * @param file    上传的 Excel 文件
	 * @param request request
	 * @return 返回成功和错误信息
	 */
	@Override
	public Map<String, Object> importUserCourse(MultipartFile file, HttpServletRequest request) {
		ThrowUtils.throwIf(file == null || file.isEmpty(), ErrorCode.OPERATION_ERROR, "上传的文件为空");
		UserCourseExcelListener listener = new UserCourseExcelListener(this, userService, courseService, request);
		
		try {
			EasyExcel.read(file.getInputStream(), UserCourseExcelVO.class, listener).sheet().doRead();
		} catch (IOException | ExcelAnalysisException e) {
			log.error("文件读取失败: {}", e.getMessage());
			throw new BusinessException(ErrorCode.EXCEL_ERROR, "文件读取失败");
		}
		// 返回处理结果，包括成功和异常的数据
		Map<String, Object> result = new HashMap<>();
		// 获取异常记录
		result.put("errorRecords", listener.getErrorRecords());
		log.info("成功导入 {} 条用户数据，{} 条错误数据", listener.getSuccessRecords().size(), listener.getErrorRecords().size());
		return result;
	}
	
}
