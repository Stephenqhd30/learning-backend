package com.kc.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.common.exception.BusinessException;
import com.kc.learning.constants.CommonConstant;
import com.kc.learning.mapper.CertificateReviewLogsMapper;
import com.kc.learning.model.dto.certificateReviewLogs.CertificateReviewLogsQueryRequest;
import com.kc.learning.model.entity.Certificate;
import com.kc.learning.model.entity.CertificateReviewLogs;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.enums.ReviewStatusEnum;
import com.kc.learning.model.vo.certificateReviewLogs.CertificateReviewLogsVO;
import com.kc.learning.model.vo.user.UserVO;
import com.kc.learning.service.CertificateReviewLogsService;
import com.kc.learning.service.CertificateService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.SqlUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 证书审核日志服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class CertificateReviewLogsServiceImpl extends ServiceImpl<CertificateReviewLogsMapper, CertificateReviewLogs> implements CertificateReviewLogsService {
	
	@Resource
	private UserService userService;
	
	@Resource
	private CertificateService certificateService;
	
	/**
	 * 校验数据
	 *
	 * @param certificateReviewLogs certificateReviewLogs
	 * @param add                   对创建的数据进行校验
	 */
	@Override
	public void validCertificateReviewLogs(CertificateReviewLogs certificateReviewLogs, boolean add) {
		ThrowUtils.throwIf(certificateReviewLogs == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		Long certificateId = certificateReviewLogs.getCertificateId();
		Integer reviewStatus = certificateReviewLogs.getReviewStatus();
		String reviewMessage = certificateReviewLogs.getReviewMessage();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(ObjectUtils.isEmpty(certificateId), ErrorCode.PARAMS_ERROR, "证书id不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(reviewStatus), ErrorCode.PARAMS_ERROR, "审核状态不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(reviewMessage), ErrorCode.PARAMS_ERROR, "审核信息不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (ObjectUtils.isNotEmpty(certificateId)) {
			Certificate certificate = certificateService.getById(certificateId);
			ThrowUtils.throwIf(certificate == null, ErrorCode.PARAMS_ERROR, "证书不存在");
		}
		if (ObjectUtils.isNotEmpty(reviewMessage)) {
			ThrowUtils.throwIf(ReviewStatusEnum.getEnumByValue(reviewStatus) == null, ErrorCode.PARAMS_ERROR, "审核状态错误");
		}
		if (StringUtils.isNotBlank(reviewMessage)) {
			ThrowUtils.throwIf(reviewMessage.length() > 80, ErrorCode.PARAMS_ERROR, "审核信息过长");
		}
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param certificateReviewLogsQueryRequest certificateReviewLogsQueryRequest
	 * @return {@link QueryWrapper<CertificateReviewLogs>}
	 */
	@Override
	public QueryWrapper<CertificateReviewLogs> getQueryWrapper(CertificateReviewLogsQueryRequest certificateReviewLogsQueryRequest) {
		QueryWrapper<CertificateReviewLogs> queryWrapper = new QueryWrapper<>();
		if (certificateReviewLogsQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = certificateReviewLogsQueryRequest.getId();
		Long notId = certificateReviewLogsQueryRequest.getNotId();
		Long certificateId = certificateReviewLogsQueryRequest.getCertificateId();
		Long reviewerId = certificateReviewLogsQueryRequest.getReviewerId();
		Integer reviewStatus = certificateReviewLogsQueryRequest.getReviewStatus();
		String reviewMessage = certificateReviewLogsQueryRequest.getReviewMessage();
		Date reviewTime = certificateReviewLogsQueryRequest.getReviewTime();
		String sortField = certificateReviewLogsQueryRequest.getSortField();
		String sortOrder = certificateReviewLogsQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 从多字段中搜索
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(reviewMessage), "reviewMessage", reviewMessage);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(certificateId), "certificateId", certificateId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(reviewerId), "reviewerId", reviewerId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(reviewStatus), "reviewStatus", reviewStatus);
		queryWrapper.eq(ObjectUtils.isNotEmpty(reviewTime), "reviewTime", reviewTime);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取证书审核日志封装
	 *
	 * @param certificateReviewLogs certificateReviewLogs
	 * @param request               request
	 * @return {@link CertificateReviewLogsVO}
	 */
	@Override
	public CertificateReviewLogsVO getCertificateReviewLogsVO(CertificateReviewLogs certificateReviewLogs, HttpServletRequest request) {
		// 对象转封装类
		CertificateReviewLogsVO certificateReviewLogsVO = CertificateReviewLogsVO.objToVo(certificateReviewLogs);
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Long reviewerId = certificateReviewLogs.getReviewerId();
		User user = null;
		if (reviewerId != null && reviewerId > 0) {
			user = userService.getById(reviewerId);
		}
		UserVO reviewerVO = userService.getUserVO(user, request);
		certificateReviewLogsVO.setReviewerVO(reviewerVO);
		
		// endregion
		return certificateReviewLogsVO;
	}
	
	/**
	 * 分页获取证书审核日志封装
	 *
	 * @param certificateReviewLogsPage certificateReviewLogsPage
	 * @param request                   request
	 * @return {@link Page<CertificateReviewLogsVO>}
	 */
	@Override
	public Page<CertificateReviewLogsVO> getCertificateReviewLogsVOPage(Page<CertificateReviewLogs> certificateReviewLogsPage, HttpServletRequest request) {
		List<CertificateReviewLogs> certificateReviewLogsList = certificateReviewLogsPage.getRecords();
		Page<CertificateReviewLogsVO> certificateReviewLogsVOPage = new Page<>(certificateReviewLogsPage.getCurrent(), certificateReviewLogsPage.getSize(), certificateReviewLogsPage.getTotal());
		if (CollUtil.isEmpty(certificateReviewLogsList)) {
			return certificateReviewLogsVOPage;
		}
		// 对象列表 => 封装对象列表
		List<CertificateReviewLogsVO> certificateReviewLogsVOList = certificateReviewLogsList.stream()
				.map(CertificateReviewLogsVO::objToVo)
				.collect(Collectors.toList());
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> reviewerIdSet = certificateReviewLogsList.stream().map(CertificateReviewLogs::getReviewerId).collect(Collectors.toSet());
		// 填充信息
		if (CollUtil.isNotEmpty(reviewerIdSet)) {
			CompletableFuture<Map<Long, List<User>>> mapCompletableFuture = CompletableFuture.supplyAsync(
					() ->
							userService.listByIds(reviewerIdSet).stream()
									.collect(Collectors.groupingBy(User::getId)));
			try {
				Map<Long, List<User>> userIdUserListMap = mapCompletableFuture.get();
				// 填充信息
				certificateReviewLogsVOList.forEach(certificateReviewLogsVO -> {
					Long reviewerId = certificateReviewLogsVO.getReviewerId();
					User user = null;
					if (userIdUserListMap.containsKey(reviewerId)) {
						user = userIdUserListMap.get(reviewerId).get(0);
					}
					certificateReviewLogsVO.setReviewerVO(userService.getUserVO(user, request));
				});
			} catch (InterruptedException | ExecutionException e) {
				Thread.currentThread().interrupt();
				throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取信息失败" + e.getMessage());
			}
		}
		// endregion
		certificateReviewLogsVOPage.setRecords(certificateReviewLogsVOList);
		return certificateReviewLogsVOPage;
	}
	
}
