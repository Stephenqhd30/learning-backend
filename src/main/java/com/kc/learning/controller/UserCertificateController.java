package com.kc.learning.controller;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kc.learning.annotation.AuthCheck;
import com.kc.learning.common.BaseResponse;
import com.kc.learning.common.DeleteRequest;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.model.entity.Certificate;
import com.kc.learning.model.enums.ReviewStatusEnum;
import com.kc.learning.service.CertificateService;
import com.kc.learning.utils.ResultUtils;
import com.kc.learning.constant.UserConstant;
import com.kc.learning.exception.BusinessException;
import com.kc.learning.utils.ThrowUtils;
import com.kc.learning.model.dto.userCertificate.UserCertificateQueryRequest;
import com.kc.learning.model.entity.UserCertificate;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.vo.UserCertificateVO;
import com.kc.learning.service.UserCertificateService;
import com.kc.learning.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
	
	@Resource
	private CertificateService certificateService;
	
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
	 * @param userCertificateQueryRequest userCertificateQueryRequest
	 * @return {@Link BaseResponse<Page < UserCertificate>>}
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
	 * @param userCertificateQueryRequest userCertificateQueryRequest
	 * @param request                     request
	 * @return BaseResponse<Page < UserCertificateVO>>
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
