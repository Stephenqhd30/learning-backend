package com.kc.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kc.learning.common.ReviewRequest;
import com.kc.learning.model.dto.userCertificate.UserCertificateQueryRequest;
import com.kc.learning.model.entity.UserCertificate;
import com.kc.learning.model.vo.userCertificate.UserCertificateVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户证书服务
 *
 * @author stephen qiu
 */
public interface UserCertificateService extends IService<UserCertificate> {
	
	/**
	 * 校验数据
	 *
	 * @param userCertificate userCertificate
	 * @param add             对创建的数据进行校验
	 */
	void validUserCertificate(UserCertificate userCertificate, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param userCertificateQueryRequest userCertificateQueryRequest
	 * @return {@link QueryWrapper<UserCertificate>}
	 */
	QueryWrapper<UserCertificate> getQueryWrapper(UserCertificateQueryRequest userCertificateQueryRequest);
	
	/**
	 * 获取用户证书封装
	 *
	 * @param userCertificate userCertificate
	 * @param request         request
	 * @return {@link UserCertificateVO}
	 */
	UserCertificateVO getUserCertificateVO(UserCertificate userCertificate, HttpServletRequest request);
	
	/**
	 * 分页获取用户证书封装
	 *
	 * @param userCertificatePage userCertificatePage
	 * @param request             request
	 * @return {@link Page<UserCertificateVO> }
	 */
	Page<UserCertificateVO> getUserCertificateVOPage(Page<UserCertificate> userCertificatePage, HttpServletRequest request);
	
	/**
	 * 审核
	 *
	 * @param reviewRequest reviewRequest
	 * @param request       request
	 */
	void validReview(ReviewRequest reviewRequest, HttpServletRequest request);
}