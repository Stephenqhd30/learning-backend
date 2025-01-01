package com.kc.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kc.learning.model.dto.certificateReviewLogs.CertificateReviewLogsQueryRequest;
import com.kc.learning.model.entity.CertificateReviewLogs;
import com.kc.learning.model.vo.certificateReviewLogs.CertificateReviewLogsVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 证书审核日志服务
 *
 * @author stephen qiu
 */
public interface CertificateReviewLogsService extends IService<CertificateReviewLogs> {
	
	/**
	 * 校验数据
	 *
	 * @param certificateReviewLogs certificateReviewLogs
	 * @param add                   对创建的数据进行校验
	 */
	void validCertificateReviewLogs(CertificateReviewLogs certificateReviewLogs, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param certificateReviewLogsQueryRequest certificateReviewLogsQueryRequest
	 * @return {@link QueryWrapper<CertificateReviewLogs>}
	 */
	QueryWrapper<CertificateReviewLogs> getQueryWrapper(CertificateReviewLogsQueryRequest certificateReviewLogsQueryRequest);
	
	/**
	 * 获取证书审核日志封装
	 *
	 * @param certificateReviewLogs certificateReviewLogs
	 * @param request               request
	 * @return {@link CertificateReviewLogsVO}
	 */
	CertificateReviewLogsVO getCertificateReviewLogsVO(CertificateReviewLogs certificateReviewLogs, HttpServletRequest request);
	
	/**
	 * 分页获取证书审核日志封装
	 *
	 * @param certificateReviewLogsPage certificateReviewLogsPage
	 * @param request                   request
	 * @return {@link Page<CertificateReviewLogsVO>}
	 */
	Page<CertificateReviewLogsVO> getCertificateReviewLogsVOPage(Page<CertificateReviewLogs> certificateReviewLogsPage, HttpServletRequest request);
}