package com.kc.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kc.learning.model.dto.logPrintCertificate.LogPrintCertificateQueryRequest;
import com.kc.learning.model.entity.LogPrintCertificate;
import com.kc.learning.model.vo.logPrintCertificate.LogPrintCertificateVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 打印证书日志服务
 *
 * @author stephen qiu
 */
public interface LogPrintCertificateService extends IService<LogPrintCertificate> {
	
	/**
	 * 校验数据
	 *
	 * @param logPrintCertificate logPrintCertificate
	 * @param add                 对创建的数据进行校验
	 */
	void validLogPrintCertificate(LogPrintCertificate logPrintCertificate, boolean add);
	
	/**
	 * 获取查询条件
	 *
	 * @param logPrintCertificateQueryRequest logPrintCertificateQueryRequest
	 * @return {@link QueryWrapper<LogPrintCertificate>}
	 */
	QueryWrapper<LogPrintCertificate> getQueryWrapper(LogPrintCertificateQueryRequest logPrintCertificateQueryRequest);
	
	/**
	 * 获取打印证书日志封装
	 *
	 * @param logPrintCertificate logPrintCertificate
	 * @param request             request
	 * @return {@link LogPrintCertificateVO}
	 */
	LogPrintCertificateVO getLogPrintCertificateVO(LogPrintCertificate logPrintCertificate, HttpServletRequest request);
	
	/**
	 * 分页获取打印证书日志封装
	 *
	 * @param logPrintCertificatePage logPrintCertificatePage
	 * @param request                 request
	 * @return {@link Page<LogPrintCertificateVO>}
	 */
	Page<LogPrintCertificateVO> getLogPrintCertificateVOPage(Page<LogPrintCertificate> logPrintCertificatePage, HttpServletRequest request);
}