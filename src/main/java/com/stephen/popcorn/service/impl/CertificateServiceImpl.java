package com.stephen.popcorn.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.stephen.popcorn.common.ErrorCode;
import com.stephen.popcorn.constant.CommonConstant;
import com.stephen.popcorn.mapper.CertificateMapper;
import com.stephen.popcorn.model.dto.certificate.CertificateQueryRequest;
import com.stephen.popcorn.model.entity.Certificate;
import com.stephen.popcorn.model.entity.User;
import com.stephen.popcorn.model.enums.CertificateSituationEnum;
import com.stephen.popcorn.model.enums.CertificateTypeEnum;
import com.stephen.popcorn.model.enums.ReviewStatusEnum;
import com.stephen.popcorn.model.vo.CertificateVO;
import com.stephen.popcorn.model.vo.UserVO;
import com.stephen.popcorn.service.CertificateService;
import com.stephen.popcorn.service.UserService;
import com.stephen.popcorn.utils.SqlUtils;
import com.stephen.popcorn.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
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
		Long gainUserId = certificate.getGainUserId();
		String certificateUrl = certificate.getCertificateUrl();
		String certificateId = certificate.getCertificateId();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(certificateName), ErrorCode.PARAMS_ERROR, "证书名称不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(certificateUrl), ErrorCode.PARAMS_ERROR, "证书存放地址不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(certificateYear), ErrorCode.PARAMS_ERROR, "获得证书年份不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(certificateType), ErrorCode.PARAMS_ERROR, "证书类型不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(certificateSituation), ErrorCode.PARAMS_ERROR, "证书获得情况不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(gainUserId), ErrorCode.PARAMS_ERROR, "获得人id不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(certificateId), ErrorCode.PARAMS_ERROR, "证书编号不能为空");
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
		if (ObjectUtils.isEmpty(gainUserId)) {
			User user = userService.getById(gainUserId);
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
		String certificateId = certificateQueryRequest.getCertificateId();
		String certificateName = certificateQueryRequest.getCertificateName();
		Integer certificateType = certificateQueryRequest.getCertificateType();
		String certificateYear = certificateQueryRequest.getCertificateYear();
		Integer certificateSituation = certificateQueryRequest.getCertificateSituation();
		Integer reviewStatus = certificateQueryRequest.getReviewStatus();
		String reviewMessage = certificateQueryRequest.getReviewMessage();
		Long reviewerId = certificateQueryRequest.getReviewerId();
		Date reviewTime = certificateQueryRequest.getReviewTime();
		Long userId = certificateQueryRequest.getUserId();
		Long gainUserId = certificateQueryRequest.getGainUserId();
		String sortField = certificateQueryRequest.getSortField();
		String sortOrder = certificateQueryRequest.getSortOrder();
		
		
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(certificateName), "certificateName", certificateName);
		queryWrapper.like(StringUtils.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
		queryWrapper.like(StringUtils.isNotBlank(certificateId), "certificateId", certificateId);
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
		queryWrapper.eq(ObjectUtils.isNotEmpty(gainUserId), "gainUserId", gainUserId);
		
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
		// region 可选
		// 1. 关联查询用户信息
		Long userId = certificate.getUserId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		UserVO userVO = userService.getUserVO(user, request);
		certificateVO.setUserVO(userVO);
		// endregion
		return certificateVO;
	}
	
	/**
	 * 分页获取证书封装
	 *
	 * @param certificatePage certificatePage
	 * @param request request
	 * @return
	 */
	@Override
	public Page<CertificateVO> getCertificateVOPage(Page<Certificate> certificatePage, HttpServletRequest request) {
		List<Certificate> certificateList = certificatePage.getRecords();
		Page<CertificateVO> certificateVOPage = new Page<>(certificatePage.getCurrent(), certificatePage.getSize(), certificatePage.getTotal());
		if (CollUtil.isEmpty(certificateList)) {
			return certificateVOPage;
		}
		// 对象列表 => 封装对象列表
		List<CertificateVO> certificateVOList = certificateList.stream().map(CertificateVO::objToVo).collect(Collectors.toList());
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> userIdSet = certificateList.stream().map(Certificate::getUserId).collect(Collectors.toSet());
		Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
				.collect(Collectors.groupingBy(User::getId));
		// 填充信息
		certificateVOList.forEach(certificateVO -> {
			Long userId = certificateVO.getUserId();
			User user = null;
			if (userIdUserListMap.containsKey(userId)) {
				user = userIdUserListMap.get(userId).get(0);
			}
			certificateVO.setUserVO(userService.getUserVO(user, request));
		});
		// endregion
		
		certificateVOPage.setRecords(certificateVOList);
		return certificateVOPage;
	}
	
}
