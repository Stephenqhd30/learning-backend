package com.kc.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.constants.CommonConstant;
import com.kc.learning.mapper.LogPrintCertificateMapper;
import com.kc.learning.model.dto.logPrintCertificate.LogPrintCertificateQueryRequest;
import com.kc.learning.model.entity.Certificate;
import com.kc.learning.model.entity.Course;
import com.kc.learning.model.entity.LogPrintCertificate;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.enums.UserGenderEnum;
import com.kc.learning.model.vo.certificate.CertificateVO;
import com.kc.learning.model.vo.course.CourseVO;
import com.kc.learning.model.vo.logPrintCertificate.LogPrintCertificateVO;
import com.kc.learning.model.vo.user.UserVO;
import com.kc.learning.service.CertificateService;
import com.kc.learning.service.CourseService;
import com.kc.learning.service.LogPrintCertificateService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.RegexUtils;
import com.kc.learning.utils.SqlUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 打印证书日志服务实现
 *
 * @author stephen qiu
 */
@Service
@Slf4j
public class LogPrintCertificateServiceImpl extends ServiceImpl<LogPrintCertificateMapper, LogPrintCertificate> implements LogPrintCertificateService {
	
	@Resource
	private UserService userService;
	
	@Resource
	private CertificateService certificateService;
	
	@Resource
	private CourseService courseService;
	
	/**
	 * 校验数据
	 *
	 * @param logPrintCertificate logPrintCertificate
	 * @param add                 对创建的数据进行校验
	 */
	@Override
	public void validLogPrintCertificate(LogPrintCertificate logPrintCertificate, boolean add) {
		ThrowUtils.throwIf(logPrintCertificate == null, ErrorCode.PARAMS_ERROR);
		// todo 从对象中取值
		Long userId = logPrintCertificate.getUserId();
		Long certificateId = logPrintCertificate.getCertificateId();
		Long courseId = logPrintCertificate.getCourseId();
		String userName = logPrintCertificate.getUserName();
		Integer userGender = logPrintCertificate.getUserGender();
		String userIdCard = logPrintCertificate.getUserIdCard();
		String certificateNumber = logPrintCertificate.getCertificateNumber();
		String courseName = logPrintCertificate.getCourseName();
		Date acquisitionTime = logPrintCertificate.getAcquisitionTime();
		Date finishTime = logPrintCertificate.getFinishTime();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(ObjectUtils.isEmpty(userId) || ObjectUtils.isEmpty(certificateId) || ObjectUtils.isEmpty(courseId),
					ErrorCode.PARAMS_ERROR, "用户ID、证书ID、课程ID不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(userName), ErrorCode.PARAMS_ERROR, "姓名不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(userIdCard), ErrorCode.PARAMS_ERROR, "身份证号不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(certificateNumber), ErrorCode.PARAMS_ERROR, "身份证号不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(userGender), ErrorCode.PARAMS_ERROR, "性别不能为空");
			ThrowUtils.throwIf(StringUtils.isBlank(courseName), ErrorCode.PARAMS_ERROR, "课程名不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
		if (StringUtils.isNotBlank(userName)) {
			ThrowUtils.throwIf(userName.length() > 80, ErrorCode.PARAMS_ERROR, "姓名过长");
		}
		if (StringUtils.isNotBlank(userIdCard)) {
			ThrowUtils.throwIf(!RegexUtils.checkIdCard(userIdCard), ErrorCode.PARAMS_ERROR, "姓名过长");
		}
		if (StringUtils.isNotBlank(courseName)) {
			ThrowUtils.throwIf(courseName.length() > 80, ErrorCode.PARAMS_ERROR, "课程名过长");
		}
		if (ObjectUtils.isNotEmpty(userGender)) {
			ThrowUtils.throwIf(UserGenderEnum.getEnumByValue(userGender) == null, ErrorCode.PARAMS_ERROR, "性别不合法");
		}
		if (ObjectUtils.isNotEmpty(userId)) {
			User user = userService.getById(userId);
			ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "用户信息为空");
		}
		if (ObjectUtils.isNotEmpty(acquisitionTime) && ObjectUtils.isNotEmpty(finishTime)) {
			ThrowUtils.throwIf(acquisitionTime.getTime() > finishTime.getTime(), ErrorCode.PARAMS_ERROR, "开课时间不能大于结课时间");
		}
		if (ObjectUtils.isNotEmpty(certificateId)) {
			Certificate certificate = certificateService.getById(certificateId);
			ThrowUtils.throwIf(certificate == null, ErrorCode.NOT_FOUND_ERROR, "证书信息为空");
		}
		if (ObjectUtils.isNotEmpty(courseId)) {
			Course course = courseService.getById(courseId);
			ThrowUtils.throwIf(course == null, ErrorCode.NOT_FOUND_ERROR, "课程信息为空");
		}
		
	}
	
	/**
	 * 获取查询条件
	 *
	 * @param logPrintCertificateQueryRequest logPrintCertificateQueryRequest
	 * @return {@link QueryWrapper<LogPrintCertificate>}
	 */
	@Override
	public QueryWrapper<LogPrintCertificate> getQueryWrapper(LogPrintCertificateQueryRequest logPrintCertificateQueryRequest) {
		QueryWrapper<LogPrintCertificate> queryWrapper = new QueryWrapper<>();
		if (logPrintCertificateQueryRequest == null) {
			return queryWrapper;
		}
		// todo 从对象中取值
		Long id = logPrintCertificateQueryRequest.getId();
		Long notId = logPrintCertificateQueryRequest.getNotId();
		Long userId = logPrintCertificateQueryRequest.getUserId();
		Long certificateId = logPrintCertificateQueryRequest.getCertificateId();
		Long courseId = logPrintCertificateQueryRequest.getCourseId();
		String userName = logPrintCertificateQueryRequest.getUserName();
		Integer userGender = logPrintCertificateQueryRequest.getUserGender();
		String certificateNumber = logPrintCertificateQueryRequest.getCertificateNumber();
		String courseName = logPrintCertificateQueryRequest.getCourseName();
		Date acquisitionTime = logPrintCertificateQueryRequest.getAcquisitionTime();
		Date finishTime = logPrintCertificateQueryRequest.getFinishTime();
		String sortField = logPrintCertificateQueryRequest.getSortField();
		String sortOrder = logPrintCertificateQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
		queryWrapper.like(StringUtils.isNotBlank(certificateNumber), "certificateNumber", certificateNumber);
		queryWrapper.like(StringUtils.isNotBlank(courseName), "courseName", courseName);
		queryWrapper.like(ObjectUtils.isNotEmpty(acquisitionTime), "acquisitionTime", acquisitionTime);
		queryWrapper.like(ObjectUtils.isNotEmpty(finishTime), "finishTime", finishTime);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(certificateId), "certificateId", certificateId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(courseId), "courseId", courseId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userGender), "userGender", userGender);
		// 排序规则
		queryWrapper.orderBy(SqlUtils.validSortField(sortField),
				sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
				sortField);
		return queryWrapper;
	}
	
	/**
	 * 获取打印证书日志封装
	 *
	 * @param logPrintCertificate logPrintCertificate
	 * @param request             request
	 * @return {@link LogPrintCertificateVO}
	 */
	@Override
	public LogPrintCertificateVO getLogPrintCertificateVO(LogPrintCertificate logPrintCertificate, HttpServletRequest request) {
		// 对象转封装类
		LogPrintCertificateVO logPrintCertificateVO = LogPrintCertificateVO.objToVo(logPrintCertificate);
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Long userId = logPrintCertificate.getUserId();
		User user = null;
		if (userId != null && userId > 0) {
			user = userService.getById(userId);
		}
		UserVO userVO = userService.getUserVO(user, request);
		logPrintCertificateVO.setUserVO(userVO);
		// 2. 关联查询证书信息
		Long certificateId = logPrintCertificate.getCertificateId();
		Certificate certificate = null;
		if (certificateId != null && certificateId > 0) {
			certificate = certificateService.getById(certificateId);
		}
		CertificateVO certificateVO = certificateService.getCertificateVO(certificate, request);
		logPrintCertificateVO.setCertificateVO(certificateVO);
		// 3. 关联查询课程信息
		Long courseId = logPrintCertificate.getCourseId();
		Course course = null;
		if (courseId != null && courseId > 0) {
			course = courseService.getById(courseId);
		}
		CourseVO courseVO = courseService.getCourseVO(course, request);
		logPrintCertificateVO.setCourseVO(courseVO);
		// endregion
		return logPrintCertificateVO;
	}
	
	/**
	 * 分页获取打印证书日志封装
	 *
	 * @param logPrintCertificatePage logPrintCertificatePage
	 * @param request                 request
	 * @return {@link Page<LogPrintCertificateVO>}
	 */
	@Override
	public Page<LogPrintCertificateVO> getLogPrintCertificateVOPage(Page<LogPrintCertificate> logPrintCertificatePage, HttpServletRequest request) {
		List<LogPrintCertificate> logPrintCertificateList = logPrintCertificatePage.getRecords();
		Page<LogPrintCertificateVO> logPrintCertificateVOPage = new Page<>(logPrintCertificatePage.getCurrent(), logPrintCertificatePage.getSize(), logPrintCertificatePage.getTotal());
		if (CollUtil.isEmpty(logPrintCertificateList)) {
			return logPrintCertificateVOPage;
		}
		// 对象列表 => 封装对象列表
		List<LogPrintCertificateVO> logPrintCertificateVOList = logPrintCertificateList.stream().map(LogPrintCertificateVO::objToVo).collect(Collectors.toList());
		
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// region 可选
		// 1. 关联查询用户信息
		Set<Long> userIdSet = logPrintCertificateList.stream().map(LogPrintCertificate::getUserId).collect(Collectors.toSet());
		Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
				.collect(Collectors.groupingBy(User::getId));
		// 2. 关联查询证书信息
		Set<Long> certificateIdSet = logPrintCertificateList.stream().map(LogPrintCertificate::getCertificateId).collect(Collectors.toSet());
		Map<Long, List<Certificate>> certificateIdCertificateListMap = certificateService.listByIds(certificateIdSet).stream()
				.collect(Collectors.groupingBy(Certificate::getId));
		// 3. 关联查询课程信息
		Set<Long> courseIdSet = logPrintCertificateList.stream().map(LogPrintCertificate::getCourseId).collect(Collectors.toSet());
		Map<Long, List<Course>> courseIdCourseListMap = courseService.listByIds(courseIdSet).stream()
				.collect(Collectors.groupingBy(Course::getId));
		// endregion
		// 填充信息
		logPrintCertificateVOList.forEach(logPrintCertificateVO -> {
			Long userId = logPrintCertificateVO.getUserId();
			User user = null;
			Long certificateId = logPrintCertificateVO.getCertificateId();
			Certificate certificate = null;
			Long courseId = logPrintCertificateVO.getCourseId();
			Course course = null;
			if (userIdUserListMap.containsKey(userId)) {
				user = userIdUserListMap.get(userId).get(0);
			}
			if (certificateIdCertificateListMap.containsKey(certificateId)) {
				certificate = certificateIdCertificateListMap.get(certificateId).get(0);
			}
			if (courseIdCourseListMap.containsKey(courseId)) {
				course = courseIdCourseListMap.get(courseId).get(0);
			}
			logPrintCertificateVO.setUserVO(userService.getUserVO(user, request));
			logPrintCertificateVO.setCertificateVO(certificateService.getCertificateVO(certificate, request));
			logPrintCertificateVO.setCourseVO(courseService.getCourseVO(course, request));
		});
		logPrintCertificateVOPage.setRecords(logPrintCertificateVOList);
		return logPrintCertificateVOPage;
	}
	
}
