package com.kc.learning.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kc.learning.model.dto.certificate.CertificatePrintRequest;
import com.kc.learning.model.entity.Certificate;
import com.kc.learning.model.vo.certificate.CertificatePrintVO;
import org.apache.ibatis.annotations.Param;

/**
 * @author stephen qiu
 * @description 针对表【certificate(证书表)】的数据库操作Mapper
 * @createDate 2024-08-30 07:38:30
 * @Entity entity.model.com.kc.learning.Certificate
 */
public interface CertificateMapper extends BaseMapper<Certificate> {
	
	/**
	 * 获取 CertificatePrintVO 数据
	 *
	 * @param certificatePrintRequest 请求参数
	 * @return CertificatePrintVO
	 */
	CertificatePrintVO getCertificatePrintData(@Param("query") CertificatePrintRequest certificatePrintRequest);
	
	/**
	 * 分页获取 CertificatePrintVO 数据
	 *
	 * @param certificatePrintRequest 请求参数
	 * @return CertificatePrintVO
	 */
	Page<CertificatePrintVO> getCertificatePrintDataByPage(@Param("query") CertificatePrintRequest certificatePrintRequest, Page<CertificatePrintVO> page);
}




