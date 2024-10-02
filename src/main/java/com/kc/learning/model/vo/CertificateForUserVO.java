package com.kc.learning.model.vo;

import com.kc.learning.model.entity.Certificate;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * 为用户展示的证书视图
 *
 * @author stephen
 */
@Data
public class CertificateForUserVO implements Serializable {
	
	private static final long serialVersionUID = 8466517044838172240L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 证书编号
	 */
	private String certificateNumber;
	
	/**
	 * 证书名称
	 */
	private String certificateName;
	
	/**
	 * 证书类型(0-干部培训,1-其他)
	 */
	private Integer certificateType;
	
	/**
	 * 证书获得时间
	 */
	private String certificateYear;
	
	/**
	 * 证书获得情况(0-有,1-没有)
	 */
	private Integer certificateSituation;
	
	/**
	 * 证书地址
	 */
	private String certificateUrl;
	
	/**
	 * 封装类转对象
	 *
	 * @param certificateForUserVO certificateForUserVO
	 * @return UserCertificate
	 */
	public static Certificate voToObj(CertificateForUserVO certificateForUserVO) {
		if (certificateForUserVO == null) {
			return null;
		}
		Certificate certificate = new Certificate();
		BeanUtils.copyProperties(certificateForUserVO, certificate);
		return certificate;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param certificate certificate
	 * @return UserCertificateVO
	 */
	public static CertificateForUserVO objToVo(Certificate certificate) {
		if (certificate == null) {
			return null;
		}
		CertificateForUserVO certificateForUserVO = new CertificateForUserVO();
		BeanUtils.copyProperties(certificate, certificateForUserVO);
		return certificateForUserVO;
	}
}
