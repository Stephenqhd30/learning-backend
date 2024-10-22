package com.kc.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kc.learning.common.ErrorCode;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
		Long certificateNumber = userCertificate.getCertificateId();
		String gainTime = userCertificate.getGainTime();
		String certificateName = userCertificate.getCertificateName();
		String gainUserName = userCertificate.getGainUserName();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(StringUtils.isBlank(gainTime), ErrorCode.PARAMS_ERROR, "获得时间不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(certificateName), ErrorCode.PARAMS_ERROR, "证书名称不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(gainUserName), ErrorCode.PARAMS_ERROR, "获得人姓名不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(gainTime)) {
			int gainYear = Integer.parseInt(gainTime);
			int currentYear = Calendar.getInstance().get(Calendar.YEAR);
			ThrowUtils.throwIf(gainYear > currentYear, ErrorCode.PARAMS_ERROR, "证书获取时间不能超过当前时间");
		}
		if (ObjectUtils.isNotEmpty(userId)) {
			User user = userService.getById(userId);
			ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户不存在");
		}
		
		if (ObjectUtils.isNotEmpty(certificateNumber)) {
			Certificate certificate = certificateService.getById(certificateNumber);
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
		String gainTime = userCertificateQueryRequest.getGainTime();
		String certificateNumber = userCertificateQueryRequest.getCertificateNumber();
		String certificateName = userCertificateQueryRequest.getCertificateName();
		String gainUserName = userCertificateQueryRequest.getGainUserName();
		String sortField = userCertificateQueryRequest.getSortField();
		String sortOrder = userCertificateQueryRequest.getSortOrder();
		
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(certificateName), "certificateName", certificateName);
		queryWrapper.like(StringUtils.isNotBlank(gainUserName), "gainUserName", gainUserName);
		queryWrapper.like(StringUtils.isNotBlank(certificateNumber), "certificateNumber", certificateNumber);
		// 精确查询
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(certificateId), "certificateId", certificateId);
		queryWrapper.eq(StringUtils.isNotBlank(gainTime), "gainTime", gainTime);
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
		// region 可选
		// 1. 关联查询用户信息
		Long userId = userCertificate.getUserId();
		Long certificateId = userCertificate.getCertificateId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		Certificate certificate = null;
		if (certificateId != null && certificateId > 0) {
			certificate = certificateService.getById(certificateId);
		}
		UserVO userVO = userService.getUserVO(user, request);
		CertificateVO certificateVO = certificateService.getCertificateVO(certificate, request);
		userCertificateVO.setUserVO(userVO);
		userCertificateVO.setCertificateVO(certificateVO);
		// endregion
		
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
		// 对象列表 => 封装对象列表
		List<UserCertificateVO> userCertificateVOList = userCertificateList.stream().map(UserCertificateVO::objToVo).collect(Collectors.toList());
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> userIdSet = userCertificateList.stream().map(UserCertificate::getUserId).collect(Collectors.toSet());
		Set<Long> certificateNumberSet = userCertificateList.stream().map(UserCertificate::getCertificateId).collect(Collectors.toSet());
		Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
				.collect(Collectors.groupingBy(User::getId));
		Map<Long, List<Certificate>> certificateNumberCertificateListMap = certificateService.listByIds(certificateNumberSet).stream()
				.collect(Collectors.groupingBy(Certificate::getId));
		// 填充信息
		userCertificateVOList.forEach(userCertificateVO -> {
			Long userId = userCertificateVO.getUserId();
			Long certificateId = userCertificateVO.getCertificateId();
			User user = null;
			Certificate certificate = null;
			if (userIdUserListMap.containsKey(userId)) {
				user = userIdUserListMap.get(userId).get(0);
			}
			if (certificateNumberCertificateListMap.containsKey(certificateId)) {
				certificate = certificateNumberCertificateListMap.get(certificateId).get(0);
			}
			userCertificateVO.setUserVO(userService.getUserVO(user, request));
			userCertificateVO.setCertificateVO(certificateService.getCertificateVO(certificate, request));
		});
		// endregion
		userCertificateVOPage.setRecords(userCertificateVOList);
		return userCertificateVOPage;
	}
	
	/**
	 *
	 * @param userCertificateQueryRequest userCertificateQueryRequest
	 * @param current                     current
	 * @param size                        size
	 * @return Page<UserCertificate>
	 */
	/**
	 * 分页获取用户证书封装（通过审核）
	 *
	 * @param userCertificateQueryRequest userCertificateQueryRequest
	 * @param current                     current
	 * @param size                        size
	 * @return Page<UserCertificate>
	 */
	@Override
	public Page<UserCertificate> getUserCertificates(UserCertificateQueryRequest userCertificateQueryRequest, long current, long size) {
		QueryWrapper<UserCertificate> queryWrapper = new QueryWrapper<>();
		
		// 如果传入了 certificateId，进一步过滤
		if (ObjectUtils.isNotEmpty(userCertificateQueryRequest.getCertificateId())) {
			queryWrapper.eq("certificateId", userCertificateQueryRequest.getCertificateId());
		}
		
		// 关联查询 Certificate 表，过滤掉 review_status 不等于 1 的记录
		queryWrapper.exists("SELECT 1 FROM certificate WHERE certificate.id = user_certificate.certificateId AND certificate.reviewStatus = 1");
		
		// 执行分页查询
		return this.page(new Page<>(current, size), queryWrapper);
	}
}
