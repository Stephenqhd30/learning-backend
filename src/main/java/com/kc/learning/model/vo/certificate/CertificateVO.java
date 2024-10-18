package com.kc.learning.model.vo.certificate;

import com.kc.learning.model.entity.Certificate;
import com.kc.learning.model.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 证书视图
 *
 * @author stephen
 */
@Data
public class CertificateVO implements Serializable {
	
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
	 * 证书状态(0-待审核,1-通过,2-拒绝)
	 */
	private Integer reviewStatus;
	
	/**
	 * 审核信息
	 */
	private String reviewMessage;
	
	/**
	 * 审核人id
	 */
	private Long reviewerId;
	
	/**
	 * 审核时间
	 */
	private Date reviewTime;
	
	
	/**
	 * 创建用户id
	 */
	private Long userId;
	
	/**
	 * 获得人姓名
	 */
	private Long gainUserId;
	
	/**
	 * 证书地址
	 */
	private String certificateUrl;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 创建用户信息
	 */
	private UserVO userVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param certificateVO certificateVO
	 * @return Certificate
	 */
	public static Certificate voToObj(CertificateVO certificateVO) {
		if (certificateVO == null) {
			return null;
		}
		Certificate certificate = new Certificate();
		BeanUtils.copyProperties(certificateVO, certificate);
		return certificate;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param certificate certificate
	 * @return CertificateVO
	 */
	public static CertificateVO objToVo(Certificate certificate) {
		if (certificate == null) {
			return null;
		}
		CertificateVO certificateVO = new CertificateVO();
		BeanUtils.copyProperties(certificate, certificateVO);
		return certificateVO;
	}
}
