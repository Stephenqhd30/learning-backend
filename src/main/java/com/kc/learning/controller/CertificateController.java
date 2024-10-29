package com.kc.learning.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kc.learning.annotation.AuthCheck;
import com.kc.learning.common.BaseResponse;
import com.kc.learning.common.DeleteRequest;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.common.ReviewRequest;
import com.kc.learning.constants.ExcelConstant;
import com.kc.learning.constants.UserConstant;
import com.kc.learning.exception.BusinessException;
import com.kc.learning.model.dto.certificate.CertificateAddRequest;
import com.kc.learning.model.dto.certificate.CertificateQueryRequest;
import com.kc.learning.model.dto.certificate.CertificateUpdateRequest;
import com.kc.learning.model.entity.Certificate;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.entity.UserCertificate;
import com.kc.learning.model.enums.CertificateSituationEnum;
import com.kc.learning.model.enums.CertificateTypeEnum;
import com.kc.learning.model.enums.ReviewStatusEnum;
import com.kc.learning.model.vo.certificate.CertificateExcelVO;
import com.kc.learning.model.vo.certificate.CertificateForUserVO;
import com.kc.learning.model.vo.certificate.CertificateImportExcelVO;
import com.kc.learning.model.vo.certificate.CertificateVO;
import com.kc.learning.service.CertificateService;
import com.kc.learning.service.UserCertificateService;
import com.kc.learning.service.UserCourseService;
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
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 证书接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/certificate")
@Slf4j
public class CertificateController {
	
	@Resource
	private CertificateService certificateService;
	
	@Resource
	private UserCertificateService userCertificateService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private UserCourseService userCourseService;
	
	// region 增删改查
	
	/**
	 * 创建证书（在创建证书的时候指定用户证书关系）
	 *
	 * @param certificateAddRequest certificateAddRequest
	 * @param request               request
	 * @return BaseResponse<Long>
	 */
	@PostMapping("/add")
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Long> addCertificate(@RequestBody CertificateAddRequest certificateAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(certificateAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		Certificate certificate = new Certificate();
		BeanUtils.copyProperties(certificateAddRequest, certificate);
		// 数据校验
		certificateService.validCertificate(certificate, true);
		// todo 填充默认值
		User loginUser = userService.getLoginUser(request);
		certificate.setUserId(loginUser.getId());
		// 写入数据库
		boolean result = certificateService.save(certificate);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 返回新写入的数据 id
		long newCertificateId = certificate.getId();
		return ResultUtils.success(newCertificateId);
	}
	
	/**
	 * 删除证书
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/delete")
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Boolean> deleteCertificate(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		User user = userService.getLoginUser(request);
		long id = deleteRequest.getId();
		// 判断是否存在
		Certificate oldCertificate = certificateService.getById(id);
		ThrowUtils.throwIf(oldCertificate == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅本人或管理员可删除
		if (!oldCertificate.getUserId().equals(user.getId()) || !userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		LambdaQueryWrapper<UserCertificate> queryWrapper = Wrappers.lambdaQuery(UserCertificate.class)
				.eq(UserCertificate::getCertificateId, id)
				.eq(UserCertificate::getUserId, oldCertificate.getGainUserId());
		UserCertificate userCertificate = userCertificateService.getOne(queryWrapper);
		// 操作数据库
		boolean result = certificateService.removeById(id);
		boolean save = userCertificateService.removeById(userCertificate.getId());
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 更新证书（仅管理员可用）
	 *
	 * @param certificateUpdateRequest certificateUpdateRequest
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/update")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> updateCertificate(@RequestBody CertificateUpdateRequest certificateUpdateRequest, HttpServletRequest request) {
		if (certificateUpdateRequest == null || certificateUpdateRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		// todo 在此处将实体类和 DTO 进行转换
		Certificate certificate = new Certificate();
		BeanUtils.copyProperties(certificateUpdateRequest, certificate);
		// 数据校验
		certificateService.validCertificate(certificate, false);
		// 判断是否存在
		long id = certificateUpdateRequest.getId();
		Certificate oldCertificate = certificateService.getById(id);
		ThrowUtils.throwIf(oldCertificate == null, ErrorCode.NOT_FOUND_ERROR);
		// 更新审核状态为待审核
		certificate.setReviewStatus(ReviewStatusEnum.REVIEWING.getValue());
		certificate.setReviewMessage("证书信息发生改变");
		certificate.setReviewerId(userService.getLoginUser(request).getId());
		certificate.setReviewTime(new Date());
		// 操作数据库
		boolean result = certificateService.updateById(certificate);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取证书（封装类）
	 *
	 * @param id id
	 * @return BaseResponse<CertificateVO>
	 */
	@GetMapping("/get/vo")
	public BaseResponse<CertificateVO> getCertificateVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR, "id有误");
		// 查询数据库
		Certificate certificate = certificateService.getById(id);
		ThrowUtils.throwIf(certificate == null, ErrorCode.NOT_FOUND_ERROR, "证书信息为空");
		// 获取封装类
		return ResultUtils.success(certificateService.getCertificateVO(certificate, request));
	}
	
	/**
	 * 分页获取证书列表（仅管理员可用）
	 *
	 * @param certificateQueryRequest certificateQueryRequest
	 * @return BaseResponse<Page < Certificate>>
	 */
	@PostMapping("/list/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<Certificate>> listCertificateByPage(@RequestBody CertificateQueryRequest certificateQueryRequest) {
		long current = certificateQueryRequest.getCurrent();
		long size = certificateQueryRequest.getPageSize();
		// 查询数据库
		Page<Certificate> certificatePage = certificateService.page(new Page<>(current, size),
				certificateService.getQueryWrapper(certificateQueryRequest));
		return ResultUtils.success(certificatePage);
	}
	
	/**
	 * 分页获取证书列表（封装类）
	 *
	 * @param certificateQueryRequest certificateQueryRequest
	 * @param request                 request
	 * @return BaseResponse<Page < CertificateVO>>
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<CertificateVO>> listCertificateVOByPage(@RequestBody CertificateQueryRequest certificateQueryRequest,
	                                                                 HttpServletRequest request) {
		long current = certificateQueryRequest.getCurrent();
		long size = certificateQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Certificate> certificatePage = certificateService.page(new Page<>(current, size),
				certificateService.getQueryWrapper(certificateQueryRequest));
		// 获取封装类
		return ResultUtils.success(certificateService.getCertificateVOPage(certificatePage, request));
	}
	
	
	/**
	 * 分页获取证书列表（封装类）
	 *
	 * @param certificateQueryRequest certificateQueryRequest
	 * @param request                 request
	 * @return BaseResponse<Page < CertificateVO>>
	 */
	@PostMapping("/list/page/vo/user")
	public BaseResponse<Page<CertificateForUserVO>> listCertificateForUserVOByPage(@RequestBody CertificateQueryRequest certificateQueryRequest,
	                                                                               HttpServletRequest request) {
		long current = certificateQueryRequest.getCurrent();
		long size = certificateQueryRequest.getPageSize();
		// 补充查询条件，只查询审核通过的证书
		certificateQueryRequest.setReviewStatus(ReviewStatusEnum.PASS.getValue());
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Certificate> certificatePage = certificateService.page(new Page<>(current, size),
				certificateService.getQueryWrapper(certificateQueryRequest));
		// 获取封装类
		return ResultUtils.success(certificateService.getCertificateForUserVOPage(certificatePage, request));
	}
	
	
	/**
	 * 分页获取当前登录用户创建的证书列表
	 *
	 * @param certificateQueryRequest certificateQueryRequest
	 * @param request                 request
	 * @return BaseResponse<Page < CertificateVO>>
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<CertificateVO>> listMyCertificateVOByPage(@RequestBody CertificateQueryRequest certificateQueryRequest,
	                                                                   HttpServletRequest request) {
		ThrowUtils.throwIf(certificateQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		certificateQueryRequest.setGainUserId(loginUser.getId());
		certificateQueryRequest.setReviewStatus(1);
		long current = certificateQueryRequest.getCurrent();
		long size = certificateQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Certificate> certificatePage = certificateService.page(new Page<>(current, size),
				certificateService.getQueryWrapper(certificateQueryRequest));
		// 获取封装类
		return ResultUtils.success(certificateService.getCertificateVOPage(certificatePage, request));
	}
	
	/**
	 * 分页获取待打印证书信息
	 *
	 * @param certificateQueryRequest certificateQueryRequest
	 * @param request                 request
	 * @return BaseResponse<Page < CertificateVO>>
	 */
	@PostMapping("/wait/print/list/page/vo")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<CertificateVO>> listWaitPrintCertificateVOByPage(@RequestBody CertificateQueryRequest certificateQueryRequest,
	                                                                          HttpServletRequest request) {
		ThrowUtils.throwIf(certificateQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		certificateQueryRequest.setCertificateSituation(CertificateSituationEnum.NONE.getValue());
		certificateQueryRequest.setReviewStatus(ReviewStatusEnum.PASS.getValue());
		long current = certificateQueryRequest.getCurrent();
		long size = certificateQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Certificate> certificatePage = certificateService.page(new Page<>(current, size),
				certificateService.getQueryWrapper(certificateQueryRequest));
		// 获取封装类
		return ResultUtils.success(certificateService.getCertificateVOPage(certificatePage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的证书列表
	 *
	 * @param certificateQueryRequest certificateQueryRequest
	 * @param request                 request
	 * @return BaseResponse<Page < CertificateVO>>
	 */
	@PostMapping("/my/list/page/vo/user")
	public BaseResponse<Page<CertificateForUserVO>> listMyCertificateForUserVOByPage(@RequestBody CertificateQueryRequest certificateQueryRequest,
	                                                                                 HttpServletRequest request) {
		ThrowUtils.throwIf(certificateQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		certificateQueryRequest.setGainUserId(loginUser.getId());
		// 补充查询条件，如果未传入审核状态默认查询已经过审的证书信息
		certificateQueryRequest.setReviewStatus(Optional.ofNullable(certificateQueryRequest.getReviewStatus())
				.orElse(ReviewStatusEnum.PASS.getValue()));
		certificateQueryRequest.setCertificateSituation(Optional.ofNullable(certificateQueryRequest.getCertificateSituation())
				.orElse(CertificateSituationEnum.HAVA.getValue()));
		long current = certificateQueryRequest.getCurrent();
		long size = certificateQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<Certificate> certificatePage = certificateService.page(new Page<>(current, size),
				certificateService.getQueryWrapper(certificateQueryRequest));
		// 获取封装类
		return ResultUtils.success(certificateService.getCertificateForUserVOPage(certificatePage, request));
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
		// 取出来请求中需要的属性
		Long id = reviewRequest.getId();
		Integer reviewStatus = reviewRequest.getReviewStatus();
		String reviewMessage = reviewRequest.getReviewMessage();
		ReviewStatusEnum reviewStatusEnum = ReviewStatusEnum.getEnumByValue(reviewStatus);
		ThrowUtils.throwIf(id == null || reviewStatusEnum == null, ErrorCode.PARAMS_ERROR);
		// 判断app是否存在
		Certificate oldCertificate = certificateService.getById(id);
		ThrowUtils.throwIf(oldCertificate == null, ErrorCode.NOT_FOUND_ERROR);
		// 判断是否已经审核
		ThrowUtils.throwIf(oldCertificate.getReviewStatus().equals(reviewStatus), ErrorCode.PARAMS_ERROR, "请勿重复审核");
		// 更新审核状态
		User loginUser = userService.getLoginUser(request);
		Certificate certificate = new Certificate();
		certificate.setId(id);
		certificate.setReviewStatus(reviewStatus);
		certificate.setReviewMessage(reviewMessage);
		certificate.setReviewerId(loginUser.getId());
		certificate.setReviewTime(new Date());
		boolean result = certificateService.updateById(certificate);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		// 如果审核通过，则关联用户证书关系
		if (ReviewStatusEnum.PASS.getValue().equals(reviewStatus)) {
			// 获取更新完之后的信息
			Certificate newCertificate = certificateService.getById(id);
			// 如果审核通过，则关联用户证书关系
			UserCertificate userCertificate = new UserCertificate();
			userCertificate.setUserId(newCertificate.getGainUserId());
			userCertificate.setCertificateId(newCertificate.getId());
			userCertificate.setGainTime(newCertificate.getCertificateYear());
			userCertificate.setCertificateNumber(newCertificate.getCertificateNumber());
			userCertificate.setCertificateName(newCertificate.getCertificateName());
			userCertificate.setGainUserName(userService.getById(newCertificate.getGainUserId()).getUserName());
			// 写入数据库
			boolean save = userCertificateService.save(userCertificate);
			ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
		}
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
		// 取出来请求中需要的属性
		String reviewMessage = reviewRequest.getReviewMessage();
		Integer reviewStatus = reviewRequest.getReviewStatus();
		String idList = reviewRequest.getIdList();
		List<Long> list = JSONUtil.toList(idList, Long.class);
		if (!list.isEmpty()) {
			for (Long id : list) {
				// 判断app是否存在
				Certificate oldCertificate = certificateService.getById(id);
				ThrowUtils.throwIf(oldCertificate == null, ErrorCode.NOT_FOUND_ERROR);
				// 判断是否已经审核
				ThrowUtils.throwIf(oldCertificate.getReviewStatus().equals(reviewStatus), ErrorCode.PARAMS_ERROR, "请勿重复审核");
				// 更新审核状态
				User loginUser = userService.getLoginUser(request);
				Certificate certificate = new Certificate();
				certificate.setId(id);
				certificate.setReviewStatus(reviewStatus);
				certificate.setReviewMessage(reviewMessage);
				certificate.setReviewerId(loginUser.getId());
				certificate.setReviewTime(new Date());
				boolean result = certificateService.updateById(certificate);
				ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
				// 如果审核通过，则关联用户证书关系
				if (ReviewStatusEnum.PASS.getValue().equals(reviewStatus)) {
					// 获取更新完之后的信息
					Certificate newCertificate = certificateService.getById(id);
					// 如果审核通过，则关联用户证书关系
					UserCertificate userCertificate = new UserCertificate();
					userCertificate.setUserId(newCertificate.getGainUserId());
					userCertificate.setCertificateId(newCertificate.getId());
					userCertificate.setGainTime(newCertificate.getCertificateYear());
					userCertificate.setCertificateNumber(newCertificate.getCertificateNumber());
					userCertificate.setCertificateName(newCertificate.getCertificateName());
					userCertificate.setGainUserName(userService.getById(newCertificate.getGainUserId()).getUserName());
					// 写入数据库
					boolean save = userCertificateService.save(userCertificate);
					ThrowUtils.throwIf(!save, ErrorCode.OPERATION_ERROR);
				}
			}
		}
		return ResultUtils.success(true);
	}
}