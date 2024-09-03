package com.stephen.popcorn.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stephen.popcorn.annotation.AuthCheck;
import com.stephen.popcorn.common.BaseResponse;
import com.stephen.popcorn.common.DeleteRequest;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.model.entity.Certificate;
import com.stephen.popcorn.model.enums.ReviewStatusEnum;
import com.stephen.popcorn.service.CertificateService;
import com.stephen.popcorn.utils.ResultUtils;
import com.stephen.popcorn.constant.UserConstant;
import com.stephen.popcorn.exception.BusinessException;
import com.stephen.popcorn.utils.ThrowUtils;
import com.stephen.popcorn.model.dto.userCertificate.UserCertificateAddRequest;
import com.stephen.popcorn.model.dto.userCertificate.UserCertificateQueryRequest;
import com.stephen.popcorn.model.entity.UserCertificate;
import com.stephen.popcorn.model.entity.User;
import com.stephen.popcorn.model.vo.UserCertificateVO;
import com.stephen.popcorn.service.UserCertificateService;
import com.stephen.popcorn.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
	 * 删除用户证书
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
	 * @param id
	 * @return
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
	 * @param userCertificateQueryRequest
	 * @return
	 */
	@PostMapping("/list/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
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
	 * @param userCertificateQueryRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<UserCertificateVO>> listUserCertificateVOByPage(@RequestBody UserCertificateQueryRequest userCertificateQueryRequest,
	                                                                         HttpServletRequest request) {
		long current = userCertificateQueryRequest.getCurrent();
		long size = userCertificateQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<UserCertificate> userCertificatePage = userCertificateService.getUserCertificates(userCertificateQueryRequest, current, size);
		// 获取封装类
		return ResultUtils.success(userCertificateService.getUserCertificateVOPage(userCertificatePage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的用户证书列表
	 *
	 * @param userCertificateQueryRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<UserCertificateVO>> listMyUserCertificateVOByPage(@RequestBody UserCertificateQueryRequest userCertificateQueryRequest,
	                                                                           HttpServletRequest request) {
		ThrowUtils.throwIf(userCertificateQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		userCertificateQueryRequest.setUserId(loginUser.getId());
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
