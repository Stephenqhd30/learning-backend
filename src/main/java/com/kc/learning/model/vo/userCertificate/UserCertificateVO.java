package com.kc.learning.model.vo.userCertificate;

import com.kc.learning.model.entity.UserCertificate;
import com.kc.learning.model.vo.certificate.CertificateVO;
import com.kc.learning.model.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户证书视图
 *
 * @author stephen
 */
@Data
public class UserCertificateVO implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 证书id
	 */
	private Long certificateId;
	
	/**
	 * 获得时间
	 */
	private String gainTime;
	
	/**
	 * 证书编号
	 */
	private String certificateNumber;
	
	/**
	 * 证书名称
	 */
	private String certificateName;
	
	/**
	 * 获得人名称
	 */
	private String gainUserName;
	
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
	 * 证书信息
	 */
	private CertificateVO certificateVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param userCertificateVO userCertificateVO
	 * @return UserCertificate
	 */
	public static UserCertificate voToObj(UserCertificateVO userCertificateVO) {
		if (userCertificateVO == null) {
			return null;
		}
		UserCertificate userCertificate = new UserCertificate();
		BeanUtils.copyProperties(userCertificateVO, userCertificate);
		return userCertificate;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param userCertificate userCertificate
	 * @return UserCertificateVO
	 */
	public static UserCertificateVO objToVo(UserCertificate userCertificate) {
		if (userCertificate == null) {
			return null;
		}
		UserCertificateVO userCertificateVO = new UserCertificateVO();
		BeanUtils.copyProperties(userCertificate, userCertificateVO);
		return userCertificateVO;
	}
}
