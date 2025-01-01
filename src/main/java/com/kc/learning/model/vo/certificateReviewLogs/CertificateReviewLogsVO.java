package com.kc.learning.model.vo.certificateReviewLogs;

import com.kc.learning.model.entity.CertificateReviewLogs;
import com.kc.learning.model.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 证书审核日志视图
 *
 * @author stephen
 */
@Data
public class CertificateReviewLogsVO implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 证书ID，关联certificate表
	 */
	private Long certificateId;
	
	/**
	 * 审核人ID，关联用户表
	 */
	private Long reviewerId;
	
	/**
	 * 审核状态（0-待审核，1-通过，2-拒绝）
	 */
	private Integer reviewStatus;
	
	/**
	 * 审核意见
	 */
	private String reviewMessage;
	
	/**
	 * 审核时间
	 */
	private Date reviewTime;
	
	/**
	 * 审核人信息
	 */
	private UserVO reviewerVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param certificateReviewLogsVO certificateReviewLogsVO
	 * @return {@link CertificateReviewLogs}
	 */
	public static CertificateReviewLogs voToObj(CertificateReviewLogsVO certificateReviewLogsVO) {
		if (certificateReviewLogsVO == null) {
			return null;
		}
		CertificateReviewLogs certificateReviewLogs = new CertificateReviewLogs();
		BeanUtils.copyProperties(certificateReviewLogsVO, certificateReviewLogs);
		return certificateReviewLogs;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param certificateReviewLogs certificateReviewLogs
	 * @return {@link CertificateReviewLogsVO}
	 */
	public static CertificateReviewLogsVO objToVo(CertificateReviewLogs certificateReviewLogs) {
		if (certificateReviewLogs == null) {
			return null;
		}
		CertificateReviewLogsVO certificateReviewLogsVO = new CertificateReviewLogsVO();
		BeanUtils.copyProperties(certificateReviewLogs, certificateReviewLogsVO);
		return certificateReviewLogsVO;
	}
}
