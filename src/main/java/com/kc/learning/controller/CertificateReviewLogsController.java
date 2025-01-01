package com.kc.learning.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kc.learning.common.BaseResponse;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.common.exception.BusinessException;
import com.kc.learning.constants.UserConstant;
import com.kc.learning.model.dto.certificateReviewLogs.CertificateReviewLogsAddRequest;
import com.kc.learning.model.dto.certificateReviewLogs.CertificateReviewLogsQueryRequest;
import com.kc.learning.model.entity.Certificate;
import com.kc.learning.model.entity.CertificateReviewLogs;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.entity.UserCertificate;
import com.kc.learning.model.enums.ReviewStatusEnum;
import com.kc.learning.model.vo.certificateReviewLogs.CertificateReviewLogsVO;
import com.kc.learning.service.CertificateReviewLogsService;
import com.kc.learning.service.CertificateService;
import com.kc.learning.service.UserCertificateService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.ResultUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 证书审核日志接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/certificateReviewLogs")
@Slf4j
public class CertificateReviewLogsController {
	
	@Resource
	private CertificateReviewLogsService certificateReviewLogsService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private CertificateService certificateService;
	
	@Resource
	private UserCertificateService userCertificateService;
	
	// region 增删改查
	
	/**
	 * 创建证书审核日志
	 *
	 * @param certificateReviewLogsAddRequest certificateReviewLogsAddRequest
	 * @param request                         request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Long> addCertificateReviewLogs(@RequestBody CertificateReviewLogsAddRequest certificateReviewLogsAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(certificateReviewLogsAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		CertificateReviewLogs certificateReviewLogs = new CertificateReviewLogs();
		BeanUtils.copyProperties(certificateReviewLogsAddRequest, certificateReviewLogs);
		// 数据校验
		certificateReviewLogsService.validCertificateReviewLogs(certificateReviewLogs, true);
		
		// todo 填充默认值
		User loginUser = userService.getLoginUser(request);
		certificateReviewLogs.setReviewerId(loginUser.getId());
		// 写入数据库
		boolean result = certificateReviewLogsService.save(certificateReviewLogs);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 获取审核状态并处理证书和用户关联
		Integer reviewStatus = certificateReviewLogsAddRequest.getReviewStatus();
		if (ReviewStatusEnum.PASS.getValue().equals(reviewStatus)) {
			Long certificateId = certificateReviewLogsAddRequest.getCertificateId();
			Certificate certificate = certificateService.getById(certificateId);
			ThrowUtils.throwIf(certificate == null, ErrorCode.NOT_FOUND_ERROR);
			// 更新证书的审核状态
			certificate.setReviewStatus(reviewStatus);
			certificate.setReviewMessage(certificateReviewLogsAddRequest.getReviewMessage());
			certificate.setReviewerId(certificateReviewLogsAddRequest.getCertificateId());
			certificate.setReviewTime(new Date());
			boolean updateResult = certificateService.updateById(certificate);
			ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);
			// 获取证书对应的用户
			Long userId = certificate.getUserId();
			// 将证书和用户关联
			UserCertificate userCertificate = new UserCertificate();
			userCertificate.setUserId(userId);
			userCertificate.setCertificateId(certificateId);
			boolean associateResult = userCertificateService.save(userCertificate);
			
			ThrowUtils.throwIf(!associateResult, ErrorCode.OPERATION_ERROR);
		}
		// 返回新写入的数据 id
		long newCertificateReviewLogsId = certificateReviewLogs.getId();
		return ResultUtils.success(newCertificateReviewLogsId);
	}
	
	/**
	 * 批量创建证书审核日志
	 *
	 * @param certificateReviewLogsAddRequest certificateReviewLogsAddRequest
	 * @param request                         request
	 * @return {@link BaseResponse<List<Long>>}
	 */
	@PostMapping("/add/batch")
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<List<Long>> addCertificateReviewLogsByBatch(@RequestBody CertificateReviewLogsAddRequest certificateReviewLogsAddRequest, HttpServletRequest request) {
		// 参数校验
		ThrowUtils.throwIf(certificateReviewLogsAddRequest == null || CollectionUtils.isEmpty(certificateReviewLogsAddRequest.getIdList()), ErrorCode.PARAMS_ERROR);
		// 审核证书列表
		List<CertificateReviewLogs> certificateReviewLogsList = new ArrayList<>();
		// 用于批量关联证书和用户
		List<Long> certificateIds = new ArrayList<>();
		for (Long certificate : certificateReviewLogsAddRequest.getIdList()) {
			// todo 在此处将实体类和 DTO 进行转换
			CertificateReviewLogs certificateReviewLogs = new CertificateReviewLogs();
			BeanUtils.copyProperties(certificateReviewLogsAddRequest, certificateReviewLogs);
			certificateReviewLogs.setCertificateId(certificate);
			// 数据校验
			certificateReviewLogsService.validCertificateReviewLogs(certificateReviewLogs, true);
			// todo 填充默认值
			User loginUser = userService.getLoginUser(request);
			certificateReviewLogs.setReviewerId(loginUser.getId());
			// 将审核日志添加到待保存列表
			certificateReviewLogsList.add(certificateReviewLogs);
			// 收集证书ID用于后续关联
			certificateIds.add(certificate);
		}
		// 批量写入数据库
		boolean result = certificateReviewLogsService.saveBatch(certificateReviewLogsList);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		
		// 获取审核状态并处理证书和用户关联
		Integer reviewStatus = certificateReviewLogsAddRequest.getReviewStatus();
		if (ReviewStatusEnum.PASS.getValue().equals(reviewStatus)) {
			// 批量处理证书与用户的关联
			for (Long certificateId : certificateIds) {
				Certificate certificate = certificateService.getById(certificateId);
				ThrowUtils.throwIf(certificate == null, ErrorCode.NOT_FOUND_ERROR);
				// 更新证书的审核状态
				certificate.setReviewStatus(reviewStatus);
				certificate.setReviewMessage(certificateReviewLogsAddRequest.getReviewMessage());
				certificate.setReviewerId(certificateReviewLogsAddRequest.getCertificateId());
				certificate.setReviewTime(new Date());
				boolean updateResult = certificateService.updateById(certificate);
				ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR);
				// 获取证书对应的用户
				Long userId = certificate.getUserId();
				// 将证书和用户关联
				UserCertificate userCertificate = new UserCertificate();
				userCertificate.setUserId(userId);
				userCertificate.setCertificateId(certificateId);
				boolean associateResult = userCertificateService.save(userCertificate);
				ThrowUtils.throwIf(!associateResult, ErrorCode.OPERATION_ERROR);
			}
		}
		
		// 返回新写入的数据 ID 列表
		List<Long> newLogIds = certificateReviewLogsList.stream().map(CertificateReviewLogs::getId).collect(Collectors.toList());
		return ResultUtils.success(newLogIds);
	}
	
	/**
	 * 根据 id 获取证书审核日志（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse<CertificateReviewLogsVO>}
	 */
	@GetMapping("/get/vo")
	public BaseResponse<CertificateReviewLogsVO> getCertificateReviewLogsVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		CertificateReviewLogs certificateReviewLogs = certificateReviewLogsService.getById(id);
		ThrowUtils.throwIf(certificateReviewLogs == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(certificateReviewLogsService.getCertificateReviewLogsVO(certificateReviewLogs, request));
	}
	
	/**
	 * 分页获取证书审核日志列表（仅管理员可用）
	 *
	 * @param certificateReviewLogsQueryRequest certificateReviewLogsQueryRequest
	 * @return {@link BaseResponse<Page<CertificateReviewLogs>>}
	 */
	@PostMapping("/list/page")
	@SaCheckRole(UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<CertificateReviewLogs>> listCertificateReviewLogsByPage(@RequestBody CertificateReviewLogsQueryRequest certificateReviewLogsQueryRequest) {
		long current = certificateReviewLogsQueryRequest.getCurrent();
		long size = certificateReviewLogsQueryRequest.getPageSize();
		// 查询数据库
		Page<CertificateReviewLogs> certificateReviewLogsPage = certificateReviewLogsService.page(new Page<>(current, size),
				certificateReviewLogsService.getQueryWrapper(certificateReviewLogsQueryRequest));
		return ResultUtils.success(certificateReviewLogsPage);
	}
	
	/**
	 * 分页获取证书审核日志列表（封装类）
	 *
	 * @param certificateReviewLogsQueryRequest certificateReviewLogsQueryRequest
	 * @param request                           request
	 * @return {@link BaseResponse<Page<CertificateReviewLogsVO>>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<CertificateReviewLogsVO>> listCertificateReviewLogsVOByPage(@RequestBody CertificateReviewLogsQueryRequest certificateReviewLogsQueryRequest,
	                                                                                     HttpServletRequest request) {
		long current = certificateReviewLogsQueryRequest.getCurrent();
		long size = certificateReviewLogsQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<CertificateReviewLogs> certificateReviewLogsPage = certificateReviewLogsService.page(new Page<>(current, size),
				certificateReviewLogsService.getQueryWrapper(certificateReviewLogsQueryRequest));
		// 获取封装类
		return ResultUtils.success(certificateReviewLogsService.getCertificateReviewLogsVOPage(certificateReviewLogsPage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的证书审核日志列表
	 *
	 * @param certificateReviewLogsQueryRequest certificateReviewLogsQueryRequest
	 * @param request                           request
	 * @return {@link BaseResponse<Page<CertificateReviewLogsVO>>}
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<CertificateReviewLogsVO>> listMyCertificateReviewLogsVOByPage(@RequestBody CertificateReviewLogsQueryRequest certificateReviewLogsQueryRequest,
	                                                                                       HttpServletRequest request) {
		ThrowUtils.throwIf(certificateReviewLogsQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		certificateReviewLogsQueryRequest.setReviewerId(loginUser.getId());
		long current = certificateReviewLogsQueryRequest.getCurrent();
		long size = certificateReviewLogsQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<CertificateReviewLogs> certificateReviewLogsPage = certificateReviewLogsService.page(new Page<>(current, size),
				certificateReviewLogsService.getQueryWrapper(certificateReviewLogsQueryRequest));
		// 获取封装类
		return ResultUtils.success(certificateReviewLogsService.getCertificateReviewLogsVOPage(certificateReviewLogsPage, request));
	}
	
	// endregion
}