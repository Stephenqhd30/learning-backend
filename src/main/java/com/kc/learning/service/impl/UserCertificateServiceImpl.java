package com.kc.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.common.exception.BusinessException;
import com.kc.learning.constants.CommonConstant;
import com.kc.learning.mapper.UserCertificateMapper;
import com.kc.learning.model.dto.userCertificate.UserCertificateQueryRequest;
import com.kc.learning.model.entity.Certificate;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.entity.UserCertificate;
import com.kc.learning.model.vo.certificate.CertificateVO;
import com.kc.learning.model.vo.user.UserVO;
import com.kc.learning.model.vo.userCertificate.UserCertificateVO;
import com.kc.learning.service.CertificateService;
import com.kc.learning.service.UserCertificateService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.SqlUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 用户证书服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class UserCertificateServiceImpl extends ServiceImpl<UserCertificateMapper, UserCertificate> implements UserCertificateService {
	
	@Resource
	private UserService userService;
	
	@Resource
	private CertificateService certificateService;
	
	/**
	 * 校验数据
	 *
	 * @param userCertificate userCertificate
	 * @param add             对创建的数据进行校验
	 */
	@Override
	public void validUserCertificate(UserCertificate userCertificate, boolean add) {
		ThrowUtils.throwIf(userCertificate == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		Long userId = userCertificate.getUserId();
		Long certificateId = userCertificate.getCertificateId();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(ObjectUtils.isEmpty(userId), ErrorCode.PARAMS_ERROR, "参数不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(certificateId), ErrorCode.PARAMS_ERROR, "参数不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (ObjectUtils.isNotEmpty(userId)) {
			User user = userService.getById(userId);
			ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
		}
		
		if (ObjectUtils.isNotEmpty(certificateId)) {
			Certificate certificate = certificateService.getById(certificateId);
			ThrowUtils.throwIf(certificate == null, ErrorCode.NOT_FOUND_ERROR, "证书不存在");
		}
		
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param userCertificateQueryRequest userCertificateQueryRequest
	 * @return QueryWrapper<UserCertificate>
	 */
	@Override
	public QueryWrapper<UserCertificate> getQueryWrapper(UserCertificateQueryRequest userCertificateQueryRequest) {
		QueryWrapper<UserCertificate> queryWrapper = new QueryWrapper<>();
		if (userCertificateQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = userCertificateQueryRequest.getId();
		Long userId = userCertificateQueryRequest.getUserId();
		Long certificateId = userCertificateQueryRequest.getCertificateId();
		String sortField = userCertificateQueryRequest.getSortField();
		String sortOrder = userCertificateQueryRequest.getSortOrder();
		// todo 补充需要的查询条件
		// 精确查询
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(certificateId), "certificateId", certificateId);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取用户证书封装
	 *
	 * @param userCertificate userCertificate
	 * @param request         request
	 * @return UserCertificateVO
	 */
	@Override
	public UserCertificateVO getUserCertificateVO(UserCertificate userCertificate, HttpServletRequest request) {
		// 对象转封装类
		UserCertificateVO userCertificateVO = UserCertificateVO.objToVo(userCertificate);
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// 1. 关联查询用户信息
		CompletableFuture<UserVO> userFutrue = CompletableFuture.supplyAsync(() -> {
			Long userId = userCertificate.getUserId();
			if (userId != null && userId > 0) {
				User user = userService.getById(userId);
				return userService.getUserVO(user, request);
			}
			return null;
		});
		CompletableFuture<CertificateVO> certificateFuture = CompletableFuture.supplyAsync(() -> {
			Long certificateId = userCertificate.getCertificateId();
			if (certificateId != null && certificateId > 0) {
				Certificate certificate = certificateService.getById(certificateId);
				return certificateService.getCertificateVO(certificate, request);
			}
			return null;
		});
		
		// 等待所有异步任务完成，并获取结果
		try {
			userCertificateVO.setUserVO(userFutrue.get());
			userCertificateVO.setCertificateVO(certificateFuture.get());
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取打印证书日志封装失败");
		}
		return userCertificateVO;
	}
	
	
	/**
	 * 分页获取用户证书封装
	 *
	 * @param userCertificatePage userCertificatePage
	 * @param request             request
	 * @return Page<UserCertificateVO>
	 */
	@Override
	public Page<UserCertificateVO> getUserCertificateVOPage(Page<UserCertificate> userCertificatePage, HttpServletRequest request) {
		List<UserCertificate> userCertificateList = userCertificatePage.getRecords();
		Page<UserCertificateVO> userCertificateVOPage = new Page<>(userCertificatePage.getCurrent(), userCertificatePage.getSize(), userCertificatePage.getTotal());
		if (CollUtil.isEmpty(userCertificateList)) {
			return userCertificateVOPage;
		}
		// 封装 UserCertificateVO 对象列表
		List<UserCertificateVO> userCertificateVOList = userCertificateList.stream()
				.map(UserCertificateVO::objToVo)
				.collect(Collectors.toList());
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// 提取用户和证书的 ID 集合
		Set<Long> userIdSet = userCertificateList.stream().map(UserCertificate::getUserId).collect(Collectors.toSet());
		Set<Long> certificateIdSet = userCertificateList.stream().map(UserCertificate::getCertificateId).collect(Collectors.toSet());
		
		// 使用 CompletableFuture 并发查询用户和证书信息
		CompletableFuture<Map<Long, User>> userFuture = CompletableFuture.supplyAsync(() ->
				userService.listByIds(userIdSet).stream().collect(Collectors.toMap(User::getId, user -> user))
		);
		
		CompletableFuture<Map<Long, Certificate>> certificateFuture = CompletableFuture.supplyAsync(() ->
				certificateService.listByIds(certificateIdSet).stream().collect(Collectors.toMap(Certificate::getId, certificate -> certificate))
		);
		
		
		try {
			// 获取并发任务的结果
			Map<Long, User> userMap = userFuture.get();
			Map<Long, Certificate> certificateMap = certificateFuture.get();
			
			// 填充 UserCertificateVO 的用户和证书信息
			userCertificateVOList.forEach(userCertificateVO -> {
				Long userId = userCertificateVO.getUserId();
				Long certificateId = userCertificateVO.getCertificateId();
				
				User user = userMap.get(userId);
				Certificate certificate = certificateMap.get(certificateId);
				
				if (user != null) {
					userCertificateVO.setUserVO(userService.getUserVO(user, request));
				}
				if (certificate != null) {
					userCertificateVO.setCertificateVO(certificateService.getCertificateVO(certificate, request));
				}
			});
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取用户证书封装失败");
		}
		
		// 设置分页结果
		userCertificateVOPage.setRecords(userCertificateVOList);
		return userCertificateVOPage;
		
	}
}
