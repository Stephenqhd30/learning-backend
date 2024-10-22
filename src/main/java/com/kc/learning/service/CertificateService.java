package com.kc.learning.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.kc.learning.model.dto.certificate.CertificatePrintRequest;
import com.kc.learning.model.dto.certificate.CertificateQueryRequest;
import com.kc.learning.model.entity.Certificate;
import com.kc.learning.model.vo.certificate.CertificateForUserVO;
import com.kc.learning.model.vo.certificate.CertificatePrintVO;
import com.kc.learning.model.vo.certificate.CertificateVO;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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
	 * 获取打印证书封装
	 *
	 * @param certificatePrintRequest certificatePrintRequest
	 * @return {@link CertificatePrintVO}
	 */
	CertificatePrintVO getCertificatePrintVO(CertificatePrintRequest certificatePrintRequest);
	
	/**
	 * 分页获取证书封装
	 *
	 * @param certificatePage certificatePage
	 * @param request         request
	 * @return Page<CertificateVO>
	 */
	Page<CertificateVO> getCertificateVOPage(Page<Certificate> certificatePage, HttpServletRequest request);
	
	/**
	 * 分页获取给用户展示的证书视图
	 *
	 * @param certificatePrintRequest certificatePrintRequest
	 * @return {@link  Page<CertificatePrintVO>}
	 */
	Page<CertificatePrintVO> getCertificatePrintVOByPage(CertificatePrintRequest certificatePrintRequest);
	
	/**
	 * 分页获取给用户展示的证书视图
	 *
	 * @param certificatePage certificatePage
	 * @param request         request
	 * @return Page<CertificateVO>
	 */
	Page<CertificateForUserVO> getCertificateForUserVOPage(Page<Certificate> certificatePage, HttpServletRequest request);
	
	/**
	 * 导入证书
	 *
	 * @param file    file
	 * @param request request
	 * @return @return {@link Map}<{@link String}, {@link Object}>
	 */
	Map<String, Object> importCertificates(MultipartFile file, HttpServletRequest request);
}