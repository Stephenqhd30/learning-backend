package com.kc.learning.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kc.learning.common.BaseResponse;
import com.kc.learning.common.DeleteRequest;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.common.exception.BusinessException;
import com.kc.learning.constants.UserConstant;
import com.kc.learning.model.dto.logPrintCertificate.LogPrintCertificateAddRequest;
import com.kc.learning.model.dto.logPrintCertificate.LogPrintCertificateQueryRequest;
import com.kc.learning.model.entity.Certificate;
import com.kc.learning.model.entity.Course;
import com.kc.learning.model.entity.LogPrintCertificate;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.enums.CertificateSituationEnum;
import com.kc.learning.model.enums.CertificateStatusEnum;
import com.kc.learning.model.enums.UserGenderEnum;
import com.kc.learning.model.vo.logPrintCertificate.LogPrintCertificateExcelVO;
import com.kc.learning.model.vo.logPrintCertificate.LogPrintCertificateVO;
import com.kc.learning.model.vo.userCertificate.UserCertificateVO;
import com.kc.learning.service.CertificateService;
import com.kc.learning.service.CourseService;
import com.kc.learning.service.LogPrintCertificateService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

/**
 * 打印证书日志接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/logPrintCertificate")
@Slf4j
public class LogPrintCertificateController {
	
	@Resource
	private LogPrintCertificateService logPrintCertificateService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private CertificateService certificateService;
	
	@Resource
	private CourseService courseService;
	
	// region 增删改查
	
	/**
	 * 创建打印证书日志
	 *
	 * @param logPrintCertificateAddRequest logPrintCertificateAddRequest
	 * @param request                       request
	 * @return {@link BaseResponse<Long>}
	 */
	@PostMapping("/add")
	@Transactional(rollbackFor = Exception.class)
	@SaCheckRole(UserConstant.ADMIN_ROLE)
	public BaseResponse<Long> addLogPrintCertificate(@RequestBody LogPrintCertificateAddRequest logPrintCertificateAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(logPrintCertificateAddRequest == null, ErrorCode.PARAMS_ERROR);
		// todo 在此处将实体类和 DTO 进行转换
		LogPrintCertificate logPrintCertificate = new LogPrintCertificate();
		BeanUtils.copyProperties(logPrintCertificateAddRequest, logPrintCertificate);
		// 校验数据
		logPrintCertificateService.validLogPrintCertificate(logPrintCertificate, true);
		// 异步获取证书、课程和用户数据
		CompletableFuture<Certificate> certificateFuture = CompletableFuture.supplyAsync(() -> certificateService.getById(logPrintCertificateAddRequest.getCertificateId()));
		CompletableFuture<Course> courseFuture = CompletableFuture.supplyAsync(() -> courseService.getById(logPrintCertificateAddRequest.getCourseId()));
		CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> userService.getById(logPrintCertificateAddRequest.getUserId()));
		
		try {
			// 等待所有异步任务完成
			CompletableFuture.allOf(certificateFuture, courseFuture, userFuture).join();
			Certificate certificate = certificateFuture.get();
			Course course = courseFuture.get();
			User user = userFuture.get();
			// 生成证书
			LogPrintCertificateExcelVO logPrintCertificateExcelVO = new LogPrintCertificateExcelVO();
			logPrintCertificateExcelVO.setUserName(user.getUserName());
			logPrintCertificateExcelVO.setUserIdCard(EncryptionUtils.decrypt(user.getUserIdCard()));
			logPrintCertificateExcelVO.setUserGender(Objects.requireNonNull(UserGenderEnum.getEnumByValue(user.getUserGender())).getText());
			logPrintCertificateExcelVO.setCertificateNumber(certificate.getCertificateNumber());
			logPrintCertificateExcelVO.setCourseName(course.getCourseName());
			logPrintCertificateExcelVO.setAcquisitionTime(ExcelUtils.dateToExcelString(course.getAcquisitionTime()));
			logPrintCertificate.setAcquisitionTime(course.getAcquisitionTime());
			logPrintCertificateExcelVO.setFinishTime(ExcelUtils.dateToExcelString(logPrintCertificate.getFinishTime()));
			
			// 生成证书文件并上传到 Minio
			String filePath = WordUtils.generateCertificate(logPrintCertificateExcelVO);
			String certificateUrl = logPrintCertificateService.uploadCertificateToMinio(filePath, logPrintCertificateExcelVO.getCertificateNumber());
			
			// 设置证书路径
			Certificate newCertificate = new Certificate();
			newCertificate.setId(certificate.getId());
			newCertificate.setCertificateUrl(certificateUrl);
			newCertificate.setCertificateSituation(CertificateSituationEnum.HAVA.getValue());
			certificateService.updateById(newCertificate);
			
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR, "证书生成失败" + e.getMessage());
		}
		
		// todo 填充默认值
		User loginUser = userService.getLoginUser(request);
		logPrintCertificate.setCreateUserId(loginUser.getId());
		try {
			// 写入数据库
			boolean result = logPrintCertificateService.save(logPrintCertificate);
			ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
			// 返回新写入的数据 id
			long newLogPrintCertificateId = logPrintCertificate.getId();
			return ResultUtils.success(newLogPrintCertificateId);
		} catch (Exception e) {
			return ResultUtils.error(ErrorCode.OPERATION_ERROR, e.getMessage());
		}
	}
	
	
	/**
	 * 批量生成证书
	 *
	 * @param logPrintCertificateAddRequest logPrintCertificateAddRequest
	 * @param request                       request
	 * @return {@link BaseResponse<List<Long>> }
	 */
	@PostMapping("/add/batch")
	@Transactional(rollbackFor = Exception.class)
	@SaCheckRole(UserConstant.ADMIN_ROLE)
	public BaseResponse<List<Long>> addLogPrintCertificatesByBatch(@RequestBody LogPrintCertificateAddRequest logPrintCertificateAddRequest, HttpServletRequest request) {
		ThrowUtils.throwIf(logPrintCertificateAddRequest == null, ErrorCode.PARAMS_ERROR);
		
		// 获取当前登录用户
		User loginUser = userService.getLoginUser(request);
		List<Long> certificateIds = logPrintCertificateAddRequest.getCertificateIds();
		List<Long> generatedIds = new ArrayList<>();
		
		for (Long certificateId : certificateIds) {
			// 创建单个证书的生成请求
			LogPrintCertificate logPrintCertificate = new LogPrintCertificate();
			BeanUtils.copyProperties(logPrintCertificateAddRequest, logPrintCertificate);
			// 校验数据
			logPrintCertificateService.validLogPrintCertificate(logPrintCertificate, true);
			
			// 设置当前用户ID
			logPrintCertificate.setCreateUserId(loginUser.getId());
			
			// 保存生成的打印证书日志
			boolean saveResult = logPrintCertificateService.save(logPrintCertificate);
			if (!saveResult) {
				throw new BusinessException(ErrorCode.OPERATION_ERROR, "保存日志失败");
			}
			
			try {
				// 设置初始状态为运行中
				logPrintCertificate.setStatus(CertificateStatusEnum.RUNNING.getValue());
				logPrintCertificate.setExecutorMessage(CertificateStatusEnum.RUNNING.getText());
				logPrintCertificateService.updateById(logPrintCertificate);
				
				// 获取证书、课程和用户数据
				Certificate certificate = certificateService.getById(certificateId);
				Course course = courseService.getById(logPrintCertificateAddRequest.getCourseId());
				User user = userService.getById(logPrintCertificateAddRequest.getUserId());
				
				// 填充证书信息
				logPrintCertificate.setAcquisitionTime(course.getAcquisitionTime());
				
				// 生成证书
				LogPrintCertificateExcelVO logPrintCertificateExcelVO = new LogPrintCertificateExcelVO();
				logPrintCertificateExcelVO.setUserName(user.getUserName());
				logPrintCertificateExcelVO.setUserIdCard(EncryptionUtils.decrypt(user.getUserIdCard()));
				logPrintCertificateExcelVO.setUserGender(Objects.requireNonNull(UserGenderEnum.getEnumByValue(user.getUserGender())).getText());
				logPrintCertificateExcelVO.setCertificateNumber(certificate.getCertificateNumber());
				logPrintCertificateExcelVO.setCourseName(course.getCourseName());
				logPrintCertificateExcelVO.setAcquisitionTime(ExcelUtils.dateToExcelString(course.getAcquisitionTime()));
				logPrintCertificateExcelVO.setFinishTime(ExcelUtils.dateToExcelString(logPrintCertificate.getFinishTime()));
				
				// 生成证书文件并上传到 Minio
				String filePath = WordUtils.generateCertificate(logPrintCertificateExcelVO);
				String certificateUrl = logPrintCertificateService.uploadCertificateToMinio(filePath, logPrintCertificateExcelVO.getCertificateNumber());
				
				// 更新证书路径
				logPrintCertificateService.updateCertificateUrl(certificate, certificateUrl);
				
				// 更新证书状态为完成
				logPrintCertificate.setStatus(CertificateStatusEnum.SUCCEED.getValue());
				logPrintCertificate.setExecutorMessage(CertificateStatusEnum.SUCCEED.getText());
				logPrintCertificateService.updateById(logPrintCertificate);
				
				// 将生成的证书ID加入列表
				generatedIds.add(logPrintCertificate.getId());
			} catch (Exception e) {
				// 如果出错，更新状态为失败并记录错误
				logPrintCertificate.setStatus(CertificateStatusEnum.FAILED.getValue());
				logPrintCertificate.setExecutorMessage("生成失败: " + e.getMessage());
				logPrintCertificateService.updateById(logPrintCertificate);
			}
		}
		
		// 返回生成的证书 ID 列表
		return ResultUtils.success(generatedIds);
	}
	
	
	/**
	 * 删除打印证书日志
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return {@link BaseResponse<Boolean>}
	 */
	@PostMapping("/delete")
	@SaCheckRole(UserConstant.ADMIN_ROLE)
	public BaseResponse<Boolean> deleteLogPrintCertificate(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		long id = deleteRequest.getId();
		// 判断是否存在
		LogPrintCertificate oldLogPrintCertificate = logPrintCertificateService.getById(id);
		ThrowUtils.throwIf(oldLogPrintCertificate == null, ErrorCode.NOT_FOUND_ERROR);
		// 操作数据库
		boolean result = logPrintCertificateService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取用户证书（封装类）
	 *
	 * @param id id
	 * @return {@link BaseResponse <{@link UserCertificateVO}> }
	 */
	@GetMapping("/get/vo")
	public BaseResponse<LogPrintCertificateVO> getUserCertificateVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		LogPrintCertificate logPrintCertificate = logPrintCertificateService.getById(id);
		ThrowUtils.throwIf(logPrintCertificate == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(logPrintCertificateService.getLogPrintCertificateVO(logPrintCertificate, request));
	}
	
	/**
	 * 分页获取打印证书日志列表（仅管理员可用）
	 *
	 * @param logPrintCertificateQueryRequest logPrintCertificateQueryRequest
	 * @return {@link BaseResponse <{@link Page} {@link LogPrintCertificate}>}
	 */
	@PostMapping("/list/page")
	@SaCheckRole(UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<LogPrintCertificate>> listLogPrintCertificateByPage(@RequestBody LogPrintCertificateQueryRequest logPrintCertificateQueryRequest) {
		long current = logPrintCertificateQueryRequest.getCurrent();
		long size = logPrintCertificateQueryRequest.getPageSize();
		// 查询数据库
		Page<LogPrintCertificate> logPrintCertificatePage = logPrintCertificateService.page(new Page<>(current, size),
				logPrintCertificateService.getQueryWrapper(logPrintCertificateQueryRequest));
		return ResultUtils.success(logPrintCertificatePage);
	}
	
	/**
	 * 分页获取打印证书日志列表（封装类）
	 *
	 * @param logPrintCertificateQueryRequest logPrintCertificateQueryRequest
	 * @param request                         request
	 * @return {@link BaseResponse <{@link Page} {@link LogPrintCertificateVO}>}
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<LogPrintCertificateVO>> listLogPrintCertificateVOByPage(
			@RequestBody LogPrintCertificateQueryRequest logPrintCertificateQueryRequest,
			HttpServletRequest request) {
		long current = logPrintCertificateQueryRequest.getCurrent();
		long size = logPrintCertificateQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<LogPrintCertificate> logPrintCertificatePage = logPrintCertificateService.page(new Page<>(current, size),
				logPrintCertificateService.getQueryWrapper(logPrintCertificateQueryRequest));
		// 获取封装类
		return ResultUtils.success(logPrintCertificateService.getLogPrintCertificateVOPage(logPrintCertificatePage, request));
	}
	// endregion
}