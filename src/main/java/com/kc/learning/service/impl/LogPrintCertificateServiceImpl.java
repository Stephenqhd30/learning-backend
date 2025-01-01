package com.kc.learning.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.common.exception.BusinessException;
import com.kc.learning.constants.CommonConstant;
import com.kc.learning.manager.MinioManager;
import com.kc.learning.mapper.LogPrintCertificateMapper;
import com.kc.learning.model.dto.logPrintCertificate.LogPrintCertificateAddRequest;
import com.kc.learning.model.dto.logPrintCertificate.LogPrintCertificateQueryRequest;
import com.kc.learning.model.entity.*;
import com.kc.learning.model.enums.CertificateSituationEnum;
import com.kc.learning.model.enums.CertificateStatusEnum;
import com.kc.learning.model.enums.ReviewStatusEnum;
import com.kc.learning.model.enums.UserGenderEnum;
import com.kc.learning.model.vo.certificate.CertificateVO;
import com.kc.learning.model.vo.course.CourseVO;
import com.kc.learning.model.vo.logPrintCertificate.LogPrintCertificateExcelVO;
import com.kc.learning.model.vo.logPrintCertificate.LogPrintCertificateVO;
import com.kc.learning.model.vo.user.UserVO;
import com.kc.learning.service.*;
import com.kc.learning.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
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
	
	@Resource
	private UserCourseService userCourseService;
	
	@Resource
	private MinioManager minioManager;
	
	
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
		Date acquisitionTime = logPrintCertificate.getAcquisitionTime();
		Date finishTime = logPrintCertificate.getFinishTime();
		// 创建数据时，参数不能为空
		if (add) {
			// todo 补充校验规则
			ThrowUtils.throwIf(ObjectUtils.isEmpty(userId), ErrorCode.PARAMS_ERROR, "用户id不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(certificateId), ErrorCode.PARAMS_ERROR, "证书id不能为空");
			ThrowUtils.throwIf(ObjectUtils.isEmpty(courseId), ErrorCode.PARAMS_ERROR, "课程id不能为空");
		}
		// 修改数据时，有参数则校验
		// todo 补充校验规则
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
			if (ObjectUtils.isNotEmpty(certificate)) {
				ThrowUtils.throwIf(!certificate.getUserId().equals(userId), ErrorCode.PARAMS_ERROR, "证书不属于该用户");
				ThrowUtils.throwIf(ReviewStatusEnum.getEnumByValue(certificate.getReviewStatus()) == null, ErrorCode.PARAMS_ERROR, "证书未通过审核");
			}
		}
		if (ObjectUtils.isNotEmpty(courseId)) {
			Course course = courseService.getById(courseId);
			ThrowUtils.throwIf(course == null, ErrorCode.NOT_FOUND_ERROR, "课程信息为空");
		}
		if (ObjectUtils.isNotEmpty(courseId) && ObjectUtils.isNotEmpty(userId)) {
			// 验证用户是否属于课程
			UserCourse userCourse = userCourseService.getOne(Wrappers.lambdaQuery(UserCourse.class)
					.eq(UserCourse::getUserId, userId)
					.eq(UserCourse::getCourseId, courseId));
			ThrowUtils.throwIf(userCourse == null, ErrorCode.NOT_FOUND_ERROR, "用户未加入该课程");
			
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
		Date acquisitionTime = logPrintCertificateQueryRequest.getAcquisitionTime();
		Date finishTime = logPrintCertificateQueryRequest.getFinishTime();
		Long createUserId = logPrintCertificateQueryRequest.getCreateUserId();
		String sortField = logPrintCertificateQueryRequest.getSortField();
		String sortOrder = logPrintCertificateQueryRequest.getSortOrder();
		
		// todo 补充需要的查询条件
		// 模糊查询
		queryWrapper.like(ObjectUtils.isNotEmpty(acquisitionTime), "acquisitionTime", acquisitionTime);
		queryWrapper.like(ObjectUtils.isNotEmpty(finishTime), "finishTime", finishTime);
		// 精确查询
		queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
		queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(certificateId), "certificateId", certificateId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(courseId), "courseId", courseId);
		queryWrapper.eq(ObjectUtils.isNotEmpty(createUserId), "createUserId", createUserId);
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
		CompletableFuture<UserVO> userFuture = CompletableFuture.supplyAsync(() -> {
			Long userId = logPrintCertificate.getUserId();
			if (userId != null && userId > 0) {
				User user = userService.getById(userId);
				return userService.getUserVO(user, request);
			}
			return null;
		});
		
		CompletableFuture<CertificateVO> certificateFuture = CompletableFuture.supplyAsync(() -> {
			Long certificateId = logPrintCertificate.getCertificateId();
			if (certificateId != null && certificateId > 0) {
				Certificate certificate = certificateService.getById(certificateId);
				return certificateService.getCertificateVO(certificate, request);
			}
			return null;
		});
		
		CompletableFuture<CourseVO> courseFuture = CompletableFuture.supplyAsync(() -> {
			Long courseId = logPrintCertificate.getCourseId();
			if (courseId != null && courseId > 0) {
				Course course = courseService.getById(courseId);
				return courseService.getCourseVO(course, request);
			}
			return null;
		});
		
		// 等待所有异步任务完成，并获取结果
		try {
			logPrintCertificateVO.setUserVO(userFuture.get());
			logPrintCertificateVO.setCertificateVO(certificateFuture.get());
			logPrintCertificateVO.setCourseVO(courseFuture.get());
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取打印证书日志封装失败");
		}
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
		List<LogPrintCertificateVO> logPrintCertificateVOList = logPrintCertificateList.stream()
				.map(LogPrintCertificateVO::objToVo)
				.collect(Collectors.toList());
		// todo 可以根据需要为封装对象补充值，不需要的内容可以删除
		// 提取所有ID集合
		Set<Long> userIdSet = logPrintCertificateList.stream().map(LogPrintCertificate::getUserId).collect(Collectors.toSet());
		Set<Long> certificateIdSet = logPrintCertificateList.stream().map(LogPrintCertificate::getCertificateId).collect(Collectors.toSet());
		Set<Long> courseIdSet = logPrintCertificateList.stream().map(LogPrintCertificate::getCourseId).collect(Collectors.toSet());
		
		// 使用 CompletableFuture 进行并发查询
		CompletableFuture<Map<Long, User>> userFuture = CompletableFuture.supplyAsync(() ->
				userService.listByIds(userIdSet).stream().collect(Collectors.toMap(User::getId, user -> user))
		);
		
		CompletableFuture<Map<Long, Certificate>> certificateFuture = CompletableFuture.supplyAsync(() ->
				certificateService.listByIds(certificateIdSet).stream().collect(Collectors.toMap(Certificate::getId, certificate -> certificate))
		);
		
		CompletableFuture<Map<Long, Course>> courseFuture = CompletableFuture.supplyAsync(() ->
				courseService.listByIds(courseIdSet).stream().collect(Collectors.toMap(Course::getId, course -> course))
		);
		
		// 等待所有并发任务完成
		try {
			Map<Long, User> userMap = userFuture.get();
			Map<Long, Certificate> certificateMap = certificateFuture.get();
			Map<Long, Course> courseMap = courseFuture.get();
			
			// 填充信息
			logPrintCertificateVOList.forEach(logPrintCertificateVO -> {
				Long userId = logPrintCertificateVO.getUserId();
				Long certificateId = logPrintCertificateVO.getCertificateId();
				Long courseId = logPrintCertificateVO.getCourseId();
				
				User user = userMap.get(userId);
				Certificate certificate = certificateMap.get(certificateId);
				Course course = courseMap.get(courseId);
				
				logPrintCertificateVO.setUserVO(userService.getUserVO(user, request));
				logPrintCertificateVO.setCertificateVO(certificateService.getCertificateVO(certificate, request));
				logPrintCertificateVO.setCourseVO(courseService.getCourseVO(course, request));
			});
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取打印证书日志封装失败");
		}
		
		// 设置分页结果
		logPrintCertificateVOPage.setRecords(logPrintCertificateVOList);
		return logPrintCertificateVOPage;
	}
	
	/**
	 * 上传证书文件到minio
	 *
	 * @param filePath          filePath
	 * @param certificateNumber certificateNumber
	 * @return {@link String}
	 */
	@Override
	public String uploadCertificateToMinio(String filePath, String certificateNumber) throws IOException {
		FileInputStream fileInputStream = new FileInputStream(filePath);
		MultipartFile multipartFile = WordUtils.convertToMultipartFile(fileInputStream, filePath);
		String path = String.format("/%s/%s", "certificate", certificateNumber);
		return minioManager.uploadToMinio(multipartFile, path);
	}
	
	/**
	 * 更新证书url
	 *
	 * @param certificate    certificate
	 * @param certificateUrl certificateUrl
	 */
	@Override
	public void updateCertificateUrl(Certificate certificate, String certificateUrl) {
		certificate.setCertificateUrl(certificateUrl);
		certificate.setCertificateSituation(CertificateSituationEnum.HAVA.getValue());
		certificateService.updateById(certificate);
	}
	
}
