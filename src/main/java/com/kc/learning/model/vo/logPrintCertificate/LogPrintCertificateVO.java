package com.kc.learning.model.vo.logPrintCertificate;

import com.kc.learning.model.entity.LogPrintCertificate;
import com.kc.learning.model.vo.certificate.CertificateVO;
import com.kc.learning.model.vo.course.CourseVO;
import com.kc.learning.model.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 打印证书日志视图
 *
 * @author stephen
 */
@Data
public class LogPrintCertificateVO implements Serializable {
	
	private static final long serialVersionUID = 8635682960358749816L;
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
	 * 课程id
	 */
	private Long courseId;
	
	/**
	 * 姓名
	 */
	private String userName;
	
	/**
	 * 性别(0-男, 1-女)
	 */
	private Integer userGender;
	
	/**
	 * 身份证号
	 */
	private String userIdCard;
	
	/**
	 * 证书编号
	 */
	private String certificateNumber;
	
	/**
	 * 课程名称
	 */
	private String courseName;
	
	/**
	 * 开课时间
	 */
	private Date acquisitionTime;
	
	/**
	 * 结课时间
	 */
	private Date finishTime;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 用户信息
	 */
	private UserVO userVO;
	
	/**
	 * 课程信息
	 */
	private CourseVO courseVO;
	
	/**
	 * 证书信息
	 */
	private CertificateVO certificateVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param logPrintCertificateVO logPrintCertificateVO
	 * @return LogPrintCertificate
	 */
	public static LogPrintCertificate voToObj(LogPrintCertificateVO logPrintCertificateVO) {
		if (logPrintCertificateVO == null) {
			return null;
		}
		LogPrintCertificate logPrintCertificate = new LogPrintCertificate();
		BeanUtils.copyProperties(logPrintCertificateVO, logPrintCertificate);
		return logPrintCertificate;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param logPrintCertificate logPrintCertificate
	 * @return LogPrintCertificateVO
	 */
	public static LogPrintCertificateVO objToVo(LogPrintCertificate logPrintCertificate) {
		if (logPrintCertificate == null) {
			return null;
		}
		LogPrintCertificateVO logPrintCertificateVO = new LogPrintCertificateVO();
		BeanUtils.copyProperties(logPrintCertificate, logPrintCertificateVO);
		return logPrintCertificateVO;
	}
}
