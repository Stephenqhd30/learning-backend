package com.stephen.popcorn.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.stephen.popcorn.model.dto.certificate.CertificateQueryRequest;
import com.stephen.popcorn.model.entity.Certificate;
import com.stephen.popcorn.model.vo.CertificateVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 证书服务
 *
 * @author stephen qiu
 */
public interface CertificateService extends IService<Certificate> {
	
	/**
	 * 校验数据
	 *
	 * @param certificate
	 * @param add         对创建的数据进行校验
	 */
	void validCertificate(Certificate certificate, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param certificateQueryRequest
	 * @return
	 */
	QueryWrapper<Certificate> getQueryWrapper(CertificateQueryRequest certificateQueryRequest);
	
	/**
	 * 获取证书封装
	 *
	 * @param certificate
	 * @param request
	 * @return
	 */
	CertificateVO getCertificateVO(Certificate certificate, HttpServletRequest request);
	
	/**
	 * 分页获取证书封装
	 *
	 * @param certificatePage
	 * @param request
	 * @return
	 */
	Page<CertificateVO> getCertificateVOPage(Page<Certificate> certificatePage, HttpServletRequest request);
}