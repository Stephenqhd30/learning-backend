package com.kc.learning.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kc.learning.annotation.AuthCheck;
import com.kc.learning.common.BaseResponse;
import com.kc.learning.common.DeleteRequest;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.constants.UserConstant;
import com.kc.learning.exception.BusinessException;
import com.kc.learning.model.dto.logPrintCertificate.LogPrintCertificateAddRequest;
import com.kc.learning.model.dto.logPrintCertificate.LogPrintCertificateQueryRequest;
import com.kc.learning.model.entity.LogPrintCertificate;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.vo.logPrintCertificate.LogPrintCertificateVO;
import com.kc.learning.model.vo.userCertificate.UserCertificateVO;
import com.kc.learning.service.LogPrintCertificateService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.ResultUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 打印证书日志接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/logPrintCertificate")
@Slf4j
public class LogPrintCertificateController {
	
	@Resource
	private LogPrintCertificateService logPrintCertificateService;
	
	@Resource
	private UserService userService;
	
	// region 增删改查
	
	/**
	 * 创建打印证书日志
	 *
	 * @param logPrintCertificateAddRequest logPrintCertificateAddRequest
	 * @param request                       request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	public BaseResponse<Long> addLogPrintCertificate(@RequestBody LogPrintCertificateAddRequest logPrintCertificateAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(logPrintCertificateAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		LogPrintCertificate logPrintCertificate = new LogPrintCertificate();
		BeanUtils.copyProperties(logPrintCertificateAddRequest, logPrintCertificate);
		// 数据校验
		try {
			logPrintCertificateService.validLogPrintCertificate(logPrintCertificate, true);
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, e.getMessage());
		}
		// todo 填充默认值
		User loginUser = userService.getLoginUser(request);
		logPrintCertificate.setCreateUserId(loginUser.getId());
		// 写入数据库
		boolean result = logPrintCertificateService.save(logPrintCertificate);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newLogPrintCertificateId = logPrintCertificate.getId();
		return ResultUtils.success(newLogPrintCertificateId);
	}
	
	/**
	 * 删除打印证书日志
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteLogPrintCertificate(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		long id = deleteRequest.getId();
		// 判断是否存在
		LogPrintCertificate oldLogPrintCertificate = logPrintCertificateService.getById(id);
		ThrowUtils.throwIf(oldLogPrintCertificate == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = logPrintCertificateService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取用户证书（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse< UserCertificateVO >}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<LogPrintCertificateVO> getUserCertificateVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		LogPrintCertificate logPrintCertificate = logPrintCertificateService.getById(id);
		ThrowUtils.throwIf(logPrintCertificate == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(logPrintCertificateService.getLogPrintCertificateVO(logPrintCertificate, request));
	}
	
	/**
	 * 分页获取打印证书日志列表（仅管理员可用）
	 *
	 * @param logPrintCertificateQueryRequest logPrintCertificateQueryRequest
	 * @return {@link BaseResponse<Page<LogPrintCertificate>>}
	 */
	@PostMapping("/list/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<LogPrintCertificate>> listLogPrintCertificateByPage(@RequestBody LogPrintCertificateQueryRequest logPrintCertificateQueryRequest) {
		long current = logPrintCertificateQueryRequest.getCurrent();
		long size = logPrintCertificateQueryRequest.getPageSize();
		// 查询数据库
		Page<LogPrintCertificate> logPrintCertificatePage = logPrintCertificateService.page(new Page<>(current, size),
				logPrintCertificateService.getQueryWrapper(logPrintCertificateQueryRequest));
		return ResultUtils.success(logPrintCertificatePage);
	}
	
	/**
	 * 分页获取打印证书日志列表（封装类）
	 *
	 * @param logPrintCertificateQueryRequest logPrintCertificateQueryRequest
	 * @param request                         request
	 * @return {@link BaseResponse<Page<LogPrintCertificateVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<LogPrintCertificateVO>> listLogPrintCertificateVOByPage(@RequestBody LogPrintCertificateQueryRequest logPrintCertificateQueryRequest,
	                                                                                 HttpServletRequest request) {
		long current = logPrintCertificateQueryRequest.getCurrent();
		long size = logPrintCertificateQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<LogPrintCertificate> logPrintCertificatePage = logPrintCertificateService.page(new Page<>(current, size),
				logPrintCertificateService.getQueryWrapper(logPrintCertificateQueryRequest));
		// 获取封装类
		return ResultUtils.success(logPrintCertificateService.getLogPrintCertificateVOPage(logPrintCertificatePage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的打印证书日志列表
	 *
	 * @param logPrintCertificateQueryRequest logPrintCertificateQueryRequest
	 * @param request                         request
	 * @return {@link BaseResponse<Page<LogPrintCertificateVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<LogPrintCertificateVO>> listMyLogPrintCertificateVOByPage(@RequestBody LogPrintCertificateQueryRequest logPrintCertificateQueryRequest,
	                                                                                   HttpServletRequest request) {
		ThrowUtils.throwIf(logPrintCertificateQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		logPrintCertificateQueryRequest.setCreateUserId(loginUser.getId());
		long current = logPrintCertificateQueryRequest.getCurrent();
		long size = logPrintCertificateQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<LogPrintCertificate> logPrintCertificatePage = logPrintCertificateService.page(new Page<>(current, size),
				logPrintCertificateService.getQueryWrapper(logPrintCertificateQueryRequest));
		// 获取封装类
		return ResultUtils.success(logPrintCertificateService.getLogPrintCertificateVOPage(logPrintCertificatePage, request));
	}
	
	// endregion
}