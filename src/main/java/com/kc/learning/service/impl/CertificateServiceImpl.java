package com.kc.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kc.learning.aop.excel.CertificateExcelListener;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.common.exception.BusinessException;
import com.kc.learning.constants.CommonConstant;
import com.kc.learning.mapper.CertificateMapper;
import com.kc.learning.model.dto.certificate.CertificateQueryRequest;
import com.kc.learning.model.entity.Certificate;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.enums.CertificateSituationEnum;
import com.kc.learning.model.enums.CertificateTypeEnum;
import com.kc.learning.model.vo.certificate.CertificateForUserVO;
import com.kc.learning.model.vo.certificate.CertificateImportExcelVO;
import com.kc.learning.model.vo.certificate.CertificateVO;
import com.kc.learning.model.vo.user.UserVO;
import com.kc.learning.service.CertificateService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.SqlUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 证书服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class CertificateServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements CertificateService {
	
	@Resource
	private UserService userService;
	
	/**
	 * 校验数据
	 *
	 * @param certificate certificate
	 * @param add         对创建的数据进行校验
	 */
	@Override
	public void validCertificate(Certificate certificate, boolean add) {
		ThrowUtils.throwIf(certificate == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		String certificateName = certificate.getCertificateName();
		Integer certificateType = certificate.getCertificateType();
		String certificateYear = certificate.getCertificateYear();
		Integer certificateSituation = certificate.getCertificateSituation();
		Long userId = certificate.getUserId();
		String certificateNumber = certificate.getCertificateNumber();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(certificateName), ErrorCode.PARAMS_ERROR, "证书名称不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(certificateYear), ErrorCode.PARAMS_ERROR, "获得证书年份不能为空");
			ThrowUtils.throwIf(Integer.parseInt(certificateYear) > DateUtil.year(DateUtil.date()), ErrorCode.PARAMS_ERROR, "获得证书年份不能超过当前年份");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(certificateType), ErrorCode.PARAMS_ERROR, "证书类型不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(certificateSituation), ErrorCode.PARAMS_ERROR, "证书获得情况不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(userId), ErrorCode.PARAMS_ERROR, "获得人id不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(certificateNumber), ErrorCode.PARAMS_ERROR, "证书编号不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(certificateName)) {
			ThrowUtils.throwIf(certificateName.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
		}
		if (ObjectUtils.isEmpty(certificateType)) {
			ThrowUtils.throwIf(CertificateTypeEnum.getEnumByValue(certificateType) == null, ErrorCode.PARAMS_ERROR, "证书类型有误");
		}
		if (ObjectUtils.isEmpty(certificateSituation)) {
			ThrowUtils.throwIf(CertificateSituationEnum.getEnumByValue(certificateSituation) == null, ErrorCode.PARAMS_ERROR, "证书获得情况有误");
		}
		if (ObjectUtils.isEmpty(userId)) {
			User user = userService.getById(userId);
			ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param certificateQueryRequest certificateQueryRequest
	 * @return QueryWrapper<Certificate>
	 */
	@Override
	public QueryWrapper<Certificate> getQueryWrapper(CertificateQueryRequest certificateQueryRequest) {
		QueryWrapper<Certificate> queryWrapper = new QueryWrapper<>();
		if (certificateQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = certificateQueryRequest.getId();
		Integer noId = certificateQueryRequest.getNoId();
		String certificateNumber = certificateQueryRequest.getCertificateNumber();
		String certificateName = certificateQueryRequest.getCertificateName();
		Integer certificateType = certificateQueryRequest.getCertificateType();
		String certificateYear = certificateQueryRequest.getCertificateYear();
		Integer certificateSituation = certificateQueryRequest.getCertificateSituation();
		Integer reviewStatus = certificateQueryRequest.getReviewStatus();
		String reviewMessage = certificateQueryRequest.getReviewMessage();
		String status = certificateQueryRequest.getStatus();
		Long reviewerId = certificateQueryRequest.getReviewerId();
		Date reviewTime = certificateQueryRequest.getReviewTime();
		Long userId = certificateQueryRequest.getUserId();
		String sortField = certificateQueryRequest.getSortField();
		String sortOrder = certificateQueryRequest.getSortOrder();
		
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(certificateName), "certificateName", certificateName);
		queryWrapper.like(StringUtils.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
		queryWrapper.like(StringUtils.isNotBlank(certificateNumber), "certificateNumber", certificateNumber);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(noId), "reviewStatus", noId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(StringUtils.isNotBlank(certificateYear), "certificateYear", certificateYear);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(certificateType), "certificateType", certificateType);
		queryWrapper.eq(ObjectUtils.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
		queryWrapper.eq(ObjectUtils.isNotEmpty(certificateSituation), "certificateSituation", certificateSituation);
		queryWrapper.eq(ObjectUtils.isNotEmpty(reviewerId), "reviewerId", reviewerId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(reviewTime), "reviewTime", reviewTime);
		queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
		
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取证书封装
	 *
	 * @param certificate certificate
	 * @param request     request
	 * @return CertificateVO
	 */
	@Override
	public CertificateVO getCertificateVO(Certificate certificate, HttpServletRequest request) {
		// 对象转封装类
		CertificateVO certificateVO = CertificateVO.objToVo(certificate);
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// 1. 关联查询用户信息
		Long userId = certificate.getUserId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		UserVO userVO = userService.getUserVO(user, request);
		certificateVO.setUserVO(userVO);
		// 2. 关联查询审核人信息
		Long reviewerId = certificate.getReviewerId();
		User reviewer = null;
		if (reviewerId != null && reviewerId > 0) {
			reviewer = userService.getById(reviewerId);
		}
		UserVO reviewerVO = userService.getUserVO(reviewer, request);
		certificateVO.setReviewerVO(reviewerVO);
		return certificateVO;
	}
	
	/**
	 * 分页获取证书封装
	 *
	 * @param certificatePage certificatePage
	 * @param request         request
	 * @return Page<CertificateVO>
	 */
	@Override
	public Page<CertificateVO> getCertificateVOPage(Page<Certificate> certificatePage, HttpServletRequest request) {
		List<Certificate> certificateList = certificatePage.getRecords();
		Page<CertificateVO> certificateVOPage = new Page<>(certificatePage.getCurrent(), certificatePage.getSize(), certificatePage.getTotal());
		if (CollUtil.isEmpty(certificateList)) {
			return certificateVOPage;
		}
		// 对象列表 => 封装对象列表
		// 1. 异步转换 Certificate -> CertificateVO 列表
		List<CompletableFuture<CertificateVO>> futures = certificateList.stream()
				.map(certificate -> CompletableFuture.supplyAsync(() -> getCertificateVO(certificate, request)))
				.collect(Collectors.toList());
		// 2. 等待所有 CertificateVO 创建完成
		List<CertificateVO> certificateVOList = futures.stream()
				.map(CompletableFuture::join)
				.collect(Collectors.toList());
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 3. 并发查询用户信息和审核人信息
		Set<Long> userIdSet = certificateList.stream().map(Certificate::getUserId).collect(Collectors.toSet());
		Set<Long> reviewerIdSet = certificateList.stream().map(Certificate::getReviewerId).collect(Collectors.toSet());
		CompletableFuture<Map<Long, User>> userMapFuture = CompletableFuture.supplyAsync(() ->
				userService.listByIds(userIdSet).stream()
						.collect(Collectors.toMap(User::getId, user -> user))
		);
		CompletableFuture<Map<Long, User>> reviewerMapFuture = CompletableFuture.supplyAsync(() ->
				userService.listByIds(reviewerIdSet).stream()
						.collect(Collectors.toMap(User::getId, user -> user))
		);
		try {
			// 等待用户信息和审核人信息查询完成
			Map<Long, User> userIdUserMap = userMapFuture.get();
			Map<Long, User> userIdReviewerMap = reviewerMapFuture.get();
			// 填充用户信息至 CertificateVO
			certificateVOList.forEach(certificateVO -> {
				Long userId = certificateVO.getUserId();
				Long reviewerId = certificateVO.getReviewerId();
				User user = userIdUserMap.get(userId);
				User reviewer = userIdReviewerMap.get(reviewerId);
				if (user != null) {
					certificateVO.setUserVO(userService.getUserVO(user, request));
				}
				if (reviewer != null) {
					certificateVO.setReviewerVO(userService.getUserVO(reviewer, request));
				}
			});
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "证书获取失败" + e.getMessage());
		}
		certificateVOPage.setRecords(certificateVOList);
		return certificateVOPage;
	}
	
	/**
	 * 分页获取给用户展示的证书视图
	 *
	 * @param certificatePage certificatePage
	 * @param request         request
	 * @return Page<CertificateVO>
	 */
	@Override
	public Page<CertificateForUserVO> getCertificateForUserVOPage(Page<Certificate> certificatePage, HttpServletRequest request) {
		List<Certificate> certificateList = certificatePage.getRecords();
		Page<CertificateForUserVO> certificateVOPage = new Page<>(certificatePage.getCurrent(), certificatePage.getSize(), certificatePage.getTotal());
		if (CollUtil.isEmpty(certificateList)) {
			return certificateVOPage;
		}
		// 对象列表 => 封装对象列表
		CompletableFuture<List<CertificateForUserVO>> futures = CompletableFuture.supplyAsync(() -> certificateList.stream()
				.map(CertificateForUserVO::objToVo)
				.collect(Collectors.toList()));
		try {
			List<CertificateForUserVO> certificateVOList = futures.get();
			certificateVOPage.setRecords(certificateVOList);
		} catch (InterruptedException | ExecutionException e) {
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取证书列表失败" + e.getMessage());
		}
		return certificateVOPage;
	}
	
	/**
	 * 导入证书
	 *
	 * @param file file
	 * @return Map<String, Object>
	 */
	@Override
	public Map<String, Object> importCertificates(MultipartFile file, HttpServletRequest request) {
		// 传递 userService 实例给 UserExcelListener
		CertificateExcelListener listener = new CertificateExcelListener(this, userService, request);
		try {
			EasyExcel.read(file.getInputStream(), CertificateImportExcelVO.class, listener).sheet().doRead();
		} catch (IOException | ExcelAnalysisException e) {
			log.error("文件读取失败: {}", e.getMessage());
			throw new BusinessException(ErrorCode.EXCEL_ERROR, "文件读取失败");
		}
		// 返回处理结果，包括成功和异常的数据
		Map<String, Object> result = new HashMap<>();
		// 获取异常记录
		result.put("errorRecords", listener.getErrorRecords());
		log.info("成功导入 {} 条数据，{} 条错误数据", listener.getSuccessRecords().size(), listener.getErrorRecords().size());
		return result;
	}
	
}
