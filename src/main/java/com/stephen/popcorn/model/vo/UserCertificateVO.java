package com.stephen.popcorn.model.vo;

import cn.hutool.json.JSONUtil;
import com.stephen.popcorn.model.entity.UserCertificate;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

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
	 * @param userCertificateVO
	 * @return
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
	 * @param userCertificate
	 * @return
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
