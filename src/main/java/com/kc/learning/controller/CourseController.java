package com.kc.learning.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kc.learning.annotation.AuthCheck;
import com.kc.learning.common.BaseResponse;
import com.kc.learning.common.DeleteRequest;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.constants.ExcelConstant;
import com.kc.learning.constants.UserConstant;
import com.kc.learning.exception.BusinessException;
import com.kc.learning.model.dto.course.CourseAddRequest;
import com.kc.learning.model.dto.course.CourseQueryRequest;
import com.kc.learning.model.dto.course.CourseUpdateRequest;
import com.kc.learning.model.entity.Course;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.vo.course.CourseExcelExampleVO;
import com.kc.learning.model.vo.course.CourseExcelVO;
import com.kc.learning.model.vo.course.CourseVO;
import com.kc.learning.service.CourseService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.ExcelUtils;
import com.kc.learning.utils.ResultUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
	 * @param deleteRequest
	 * @param request
	 * @return
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
	 * @param courseUpdateRequest
	 * @return
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
	 * @param id
	 * @return
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
	 * @param courseQueryRequest
	 * @return
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
	 * @param courseQueryRequest
	 * @param request
	 * @return
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
	 * @param courseQueryRequest
	 * @param request
	 * @return
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
	
	/**
	 * 课程批量导入
	 *
	 * @param file    课程 Excel 文件
	 * @param request request
	 * @return 导入结果
	 */
	@PostMapping("/import")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Map<String, Object>> importCourseDataByExcel(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
		// 检查文件是否为空
		ThrowUtils.throwIf(file.isEmpty(), ErrorCode.PARAMS_ERROR, "文件不能为空");
		
		// 获取文件名并检查是否为null
		String filename = file.getOriginalFilename();
		ThrowUtils.throwIf(filename == null, ErrorCode.PARAMS_ERROR, "文件名不能为空");
		
		// 检查文件格式是否为Excel格式
		if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
			throw new RuntimeException("上传文件格式不正确");
		}
		
		// 调用服务层处理课程导入
		Map<String, Object> result = courseService.importCourse(file, request);
		return ResultUtils.success(result);
	}
	
	/**
	 * 课程数据导出
	 * 文件下载（失败了会返回一个有部分数据的Excel）
	 * 1. 创建excel对应的实体对象
	 * 2. 设置返回的 参数
	 * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
	 *
	 * @param response response
	 */
	@GetMapping("/download")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void downloadCourse(HttpServletResponse response) throws IOException {
		// 获取数据，根据自身业务修改
		List<CourseExcelVO> courseExcelVOList = courseService.list().stream().map(user -> {
					CourseExcelVO userExcelVO = new CourseExcelVO();
					BeanUtils.copyProperties(user, userExcelVO);
					userExcelVO.setId(String.valueOf(user.getId()));
					userExcelVO.setUserId(String.valueOf(user.getUserId()));
					userExcelVO.setCreateTime(ExcelUtils.dateToString(user.getCreateTime()));
					userExcelVO.setUpdateTime(ExcelUtils.dateToString(user.getUpdateTime()));
					return userExcelVO;
				})
				.collect(Collectors.toList());
		// 设置导出名称
		ExcelUtils.setExcelResponseProp(response, ExcelConstant.COURSE_EXCEL);
		// 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
		// 写入 Excel 文件
		try {
			EasyExcel.write(response.getOutputStream(), CourseExcelVO.class)
					.sheet(ExcelConstant.COURSE_EXCEL)
					.doWrite(courseExcelVOList);
			log.info("文件导出成功");
		} catch (Exception e) {
			log.error("导出失败:{}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败");
		}
	}
	
	
	/**
	 * 课程数据下载示例数据
	 * 文件下载（失败了会返回一个有部分数据的Excel）
	 * 1. 创建excel对应的实体对象
	 * 2. 设置返回的 参数
	 * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
	 *
	 * @param response response
	 */
	@GetMapping("/download/example")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void downloadCourseExample(HttpServletResponse response) throws IOException {
		// 获取数据，根据自身业务修改
		List<CourseExcelExampleVO> courseExcelExampleVOList = new ArrayList<>();
		CourseExcelExampleVO courseExcelExampleVO = new CourseExcelExampleVO();
		courseExcelExampleVO.setCourseNumber("课程号(必填)");
		courseExcelExampleVO.setCourseName("课程名称(必填)");
		courseExcelExampleVOList.add(courseExcelExampleVO);
		// 设置导出名称
		ExcelUtils.setExcelResponseProp(response, ExcelConstant.CERTIFICATE_EXCEL_EXAMPLE);
		// 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
		// 写入 Excel 文件
		try {
			EasyExcel.write(response.getOutputStream(), CourseExcelExampleVO.class)
					.sheet(ExcelConstant.CERTIFICATE_EXCEL_EXAMPLE)
					.doWrite(courseExcelExampleVOList);
			log.info("文件导出成功");
		} catch (Exception e) {
			log.error("导出失败:{}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败");
		}
	}
}