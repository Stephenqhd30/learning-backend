package com.kc.learning.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kc.learning.annotation.AuthCheck;
import com.kc.learning.common.BaseResponse;
import com.kc.learning.common.DeleteRequest;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.constants.ExcelConstant;
import com.kc.learning.constants.UserConstant;
import com.kc.learning.exception.BusinessException;
import com.kc.learning.model.dto.userCourse.UserCourseAddRequest;
import com.kc.learning.model.dto.userCourse.UserCourseQueryRequest;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.entity.UserCourse;
import com.kc.learning.model.vo.userCourse.UserCourseExcelVO;
import com.kc.learning.model.vo.userCourse.UserCourseVO;
import com.kc.learning.service.UserCourseService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.ExcelUtils;
import com.kc.learning.utils.ResultUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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
		// todo 在此处将实体类和 DTO 进行转换
		UserCourse userCourse = new UserCourse();
		BeanUtils.copyProperties(userCourseAddRequest, userCourse);
		// 数据校验
		try {
			userCourseService.validUserCourse(userCourse, true);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, e.getMessage());
		}
		// 避免重复添加信息
		LambdaQueryWrapper<UserCourse> userCourseLambdaQueryWrapper = Wrappers.lambdaQuery(UserCourse.class)
				.eq(UserCourse::getUserId, userCourseAddRequest.getUserId())
				.eq(UserCourse::getCourseId, userCourseAddRequest.getCourseId());
		UserCourse oldUserCourse = userCourseService.getOne(userCourseLambdaQueryWrapper);
		ThrowUtils.throwIf(oldUserCourse != null, ErrorCode.PARAMS_ERROR, "用户已经加入课程");
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
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
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
	
	/**
	 * 用户证书数据导出
	 * 文件下载（失败了会返回一个有部分数据的Excel）
	 * 1. 创建excel对应的实体对象
	 * 2. 设置返回的 参数
	 * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
	 *
	 * @param response response
	 */
	@GetMapping("/download")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void downloadUserCourse(HttpServletResponse response) throws IOException {
		// 获取数据，根据自身业务修改
		List<UserCourseExcelVO> userCourseExcelVOList = userCourseService.list().stream().map(userCourse -> {
					UserCourseExcelVO userCourseExcelVO = new UserCourseExcelVO();
					BeanUtils.copyProperties(userCourse, userCourseExcelVO);
					userCourseExcelVO.setId(String.valueOf(userCourse.getId()));
					userCourseExcelVO.setCourseId(String.valueOf(userCourse.getCourseId()));
					userCourseExcelVO.setUserId(String.valueOf(userCourse.getUserId()));
					userCourseExcelVO.setCreateTime(ExcelUtils.dateToExcelString(userCourse.getCreateTime()));
					
					return userCourseExcelVO;
				})
				.collect(Collectors.toList());
		// 设置导出名称
		ExcelUtils.setExcelResponseProp(response, ExcelConstant.USER_COURSE_EXCEL);
		// 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
		// 写入 Excel 文件
		try {
			EasyExcel.write(response.getOutputStream(), UserCourseExcelVO.class)
					.sheet(ExcelConstant.USER_COURSE_EXCEL)
					.doWrite(userCourseExcelVOList);
			log.info("文件导出成功");
		} catch (Exception e) {
			log.error("导出失败:{}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败");
		}
	}
}