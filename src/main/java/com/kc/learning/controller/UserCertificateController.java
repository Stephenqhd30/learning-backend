package com.kc.learning.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kc.learning.annotation.AuthCheck;
import com.kc.learning.common.BaseResponse;
import com.kc.learning.common.DeleteRequest;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.common.ReviewRequest;
import com.kc.learning.constants.UserConstant;
import com.kc.learning.exception.BusinessException;
import com.kc.learning.model.dto.userCertificate.UserCertificateQueryRequest;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.entity.UserCertificate;
import com.kc.learning.model.vo.userCertificate.UserCertificateVO;
import com.kc.learning.service.UserCertificateService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.ResultUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
	
	// 可根据实际需求调整大小
	private final ExecutorService executorService = Executors.newFixedThreadPool(10);
	
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
	
	/**
	 * 分页获取当前登录用户创建的用户证书列表
	 *
	 * @param userCertificateQueryRequest userCertificateQueryRequest
	 * @param request                     request
	 * @return {@link BaseResponse <{@link Page <{@link UserCertificateVO}>}>}
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
	
	/**
	 * 审核证书（仅管理员可用）
	 *
	 * @param reviewRequest reviewRequest
	 * @param request       request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/review")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> doCertificateReview(@RequestBody ReviewRequest reviewRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(reviewRequest == null, ErrorCode.PARAMS_ERROR);
		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
			// 审核信息并更新证书状态，若审核信息通过更新到用户证书表中
			userCertificateService.validReview(reviewRequest, request);
		}, executorService);
		// 等待任务完成
		future.join();
		return ResultUtils.success(true);
	}
	
	
	/**
	 * 批量应用审核
	 *
	 * @param reviewRequest reviewRequest
	 * @param request       request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/review/batch")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Boolean> doCertificateReviewByBatch(@RequestBody ReviewRequest reviewRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(reviewRequest == null, ErrorCode.PARAMS_ERROR);
		String reviewMessage = reviewRequest.getReviewMessage();
		Integer reviewStatus = reviewRequest.getReviewStatus();
		List<Long> idList = reviewRequest.getIdList();
		if (!idList.isEmpty()) {
			// 等待所有任务完成
			CompletableFuture.allOf(idList.stream()
					.map(id -> CompletableFuture.runAsync(() -> {
						ReviewRequest newReviewRequest = new ReviewRequest();
						newReviewRequest.setId(id);
						newReviewRequest.setReviewMessage(reviewMessage);
						newReviewRequest.setReviewStatus(reviewStatus);
						// 审核信息并更新证书状态，若审核信息通过更新到用户证书表中
						userCertificateService.validReview(newReviewRequest, request);
					}, executorService)).toArray(CompletableFuture[]::new)).join();
		}
		return ResultUtils.success(true);
	}
}
