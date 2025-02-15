package com.kc.learning.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kc.learning.common.BaseResponse;
import com.kc.learning.common.DeleteRequest;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.common.exception.BusinessException;
import com.kc.learning.constants.UserConstant;
import com.kc.learning.model.dto.userCertificate.UserCertificateQueryRequest;
import com.kc.learning.model.entity.UserCertificate;
import com.kc.learning.model.vo.userCertificate.UserCertificateVO;
import com.kc.learning.service.UserCertificateService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.ResultUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 用户证书接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/userCertificate")
@Slf4j
public class UserCertificateController {
	
	@Resource
	private UserCertificateService userCertificateService;
	
	@Resource
	private UserService userService;
	
	// region 增删改查
	
	/**
	 * 删除用户证书(硬删除)
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteUserCertificate(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		long id = deleteRequest.getId();
		// 判断是否存在
		UserCertificate oldUserCertificate = userCertificateService.getById(id);
		ThrowUtils.throwIf(oldUserCertificate == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅管理员可删除
		if (!userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = userCertificateService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取用户证书（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<UserCertificateVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<UserCertificateVO> getUserCertificateVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		UserCertificate userCertificate = userCertificateService.getById(id);
		ThrowUtils.throwIf(userCertificate == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(userCertificateService.getUserCertificateVO(userCertificate, request));
	}
	
	/**
	 * 分页获取用户证书列表（仅管理员可用）
	 *
	 * @param userCertificateQueryRequest userCertificateQueryRequest
	 * @return {@link  BaseResponse <{@link Page} {@link UserCertificateVO}}>}
	 */
	@PostMapping("/list/page")
	@SaCheckRole(UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<UserCertificate>> listUserCertificateByPage(@RequestBody UserCertificateQueryRequest userCertificateQueryRequest) {
		long current = userCertificateQueryRequest.getCurrent();
		long size = userCertificateQueryRequest.getPageSize();
		// 查询数据库
		Page<UserCertificate> userCertificatePage = userCertificateService.page(new Page<>(current, size),
				userCertificateService.getQueryWrapper(userCertificateQueryRequest));
		return ResultUtils.success(userCertificatePage);
	}
	
	/**
	 * 分页获取用户证书列表（封装类）
	 * 只查看已经过审核了的证书
	 *
	 * @param userCertificateQueryRequest userCertificateQueryRequest
	 * @param request                     request
	 * @return {@link  BaseResponse <{@link Page} {@link UserCertificateVO}}>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<UserCertificateVO>> listUserCertificateVOByPage(@RequestBody UserCertificateQueryRequest userCertificateQueryRequest,
	                                                                         HttpServletRequest request) {
		long current = userCertificateQueryRequest.getCurrent();
		long size = userCertificateQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<UserCertificate> userCertificatePage = userCertificateService.page(new Page<>(current, size),
				userCertificateService.getQueryWrapper(userCertificateQueryRequest));
		// 获取封装类
		return ResultUtils.success(userCertificateService.getUserCertificateVOPage(userCertificatePage, request));
	}
	
	// endregion
}
