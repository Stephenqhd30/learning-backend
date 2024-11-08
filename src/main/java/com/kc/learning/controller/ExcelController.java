package com.kc.learning.controller;

import com.alibaba.excel.EasyExcel;
import com.kc.learning.annotation.AuthCheck;
import com.kc.learning.common.BaseResponse;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.constants.ExcelConstant;
import com.kc.learning.constants.UserConstant;
import com.kc.learning.exception.BusinessException;
import com.kc.learning.model.entity.Certificate;
import com.kc.learning.model.entity.Course;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.enums.*;
import com.kc.learning.model.vo.certificate.CertificateExcelVO;
import com.kc.learning.model.vo.certificate.CertificateImportExcelVO;
import com.kc.learning.model.vo.course.CourseExcelExampleVO;
import com.kc.learning.model.vo.course.CourseExcelVO;
import com.kc.learning.model.vo.logPrintCertificate.LogPrintCertificateExcelVO;
import com.kc.learning.model.vo.user.UserExcelExampleVO;
import com.kc.learning.model.vo.user.UserExcelVO;
import com.kc.learning.model.vo.userCertificate.UserCertificateExcelVO;
import com.kc.learning.model.vo.userCourse.UserCourseExcelVO;
import com.kc.learning.service.*;
import com.kc.learning.utils.EncryptionUtils;
import com.kc.learning.utils.ExcelUtils;
import com.kc.learning.utils.ResultUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 导入导出信息接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/excel")
@Slf4j
public class ExcelController {
	
	@Resource
	private UserService userService;
	
	@Resource
	private CertificateService certificateService;
	
	@Resource
	private CourseService courseService;
	
	@Resource
	private LogPrintCertificateService logPrintCertificateService;
	
	@Resource
	private UserCourseService userCourseService;
	
	@Resource
	private UserCertificateService userCertificateService;
	
	/**
	 * 用户数据批量导入
	 *
	 * @param file 用户 Excel 文件
	 * @return 导入结果
	 */
	@PostMapping("/user/import")
	@Transactional(rollbackFor = Exception.class)
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Map<String, Object>> importUserDataByExcel(@RequestPart("file") MultipartFile file) {
		// 检查文件是否为空
		ThrowUtils.throwIf(file.isEmpty(), ErrorCode.PARAMS_ERROR, "文件不能为空");
		
		// 获取文件名并检查是否为null
		String filename = file.getOriginalFilename();
		ThrowUtils.throwIf(filename == null, ErrorCode.PARAMS_ERROR, "文件名不能为空");
		
		// 检查文件格式是否为Excel格式
		if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
			throw new RuntimeException("上传文件格式不正确");
		}
		
		// 调用服务层处理用户导入
		Map<String, Object> result = userService.importUsers(file);
		return ResultUtils.success(result);
	}
	
	/**
	 * 用户数据导出
	 * 文件下载（失败了会返回一个有部分数据的Excel）
	 * 1. 创建excel对应的实体对象
	 * 2. 设置返回的 参数
	 * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
	 *
	 * @param response response
	 */
	@GetMapping("/user/download")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void downloadUser(HttpServletResponse response) throws IOException {
		List<CompletableFuture<UserExcelVO>> futures = userService.list().stream().map(user -> CompletableFuture.supplyAsync(() -> {
			UserExcelVO userExcelVO = new UserExcelVO();
			BeanUtils.copyProperties(user, userExcelVO);
			userExcelVO.setUserIdCard(EncryptionUtils.decrypt(user.getUserIdCard()));
			userExcelVO.setUserGender(Objects.requireNonNull(UserGenderEnum.getEnumByValue(user.getUserGender())).getText());
			userExcelVO.setUserRole(Objects.requireNonNull(UserRoleEnum.getEnumByValue(user.getUserRole())).getText());
			return userExcelVO;
		})).collect(Collectors.toList());
		// 等待所有 CompletableFuture 执行完毕，并收集结果
		List<UserExcelVO> userExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		
		// 设置导出名称
		ExcelUtils.setExcelResponseProp(response, ExcelConstant.USER_EXCEL);
		
		// 写入 Excel 文件
		try {
			EasyExcel.write(response.getOutputStream(), UserExcelVO.class)
					.sheet(ExcelConstant.USER_EXCEL)
					.doWrite(userExcelVOList);
			log.info("文件导出成功");
		} catch (Exception e) {
			log.error("导出失败:{}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败");
		}
	}
	
	/**
	 * 用户导入信息示例下载
	 * 文件下载（失败了会返回一个有部分数据的Excel）
	 * 1. 创建excel对应的实体对象
	 * 2. 设置返回的 参数
	 * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
	 *
	 * @param response response
	 */
	@GetMapping("/user/download/example")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void downloadUserExample(HttpServletResponse response) throws IOException {
		List<UserExcelExampleVO> userExcelExampleVOList = new ArrayList<>();
		// 获取数据，根据自身业务修改
		UserExcelExampleVO userExcelExampleVO = new UserExcelExampleVO();
		userExcelExampleVO.setUserName("用户的姓名(必填)");
		userExcelExampleVO.setUserIdCard("用户的身份证号(必填)");
		userExcelExampleVO.setUserGender("用户的性别(0-男， 1-女)(必填)");
		userExcelExampleVO.setUserProfile("用户的简介(可以为空)");
		userExcelExampleVO.setUserEmail("用户的邮箱(可以为空)");
		userExcelExampleVO.setUserPhone("用户的电话(必填)");
		userExcelExampleVO.setUserNumber("用户的学号(必填)");
		userExcelExampleVOList.add(userExcelExampleVO);
		// 设置导出名称
		ExcelUtils.setExcelResponseProp(response, ExcelConstant.USER_EXCEL_EXAMPLE);
		// 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
		// 写入 Excel 文件
		try {
			EasyExcel.write(response.getOutputStream(), UserExcelExampleVO.class)
					.sheet(ExcelConstant.USER_EXCEL_EXAMPLE)
					.doWrite(userExcelExampleVOList);
			log.info("文件导出成功");
		} catch (Exception e) {
			log.error("导出失败:{}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败");
		}
	}
	
	
	/**
	 * 证书批量导入
	 *
	 * @param file 用户 Excel 文件
	 * @return 导入结果
	 */
	@PostMapping("/certificate/import")
	@Transactional(rollbackFor = Exception.class)
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Map<String, Object>> importCertificateDataByExcel(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
		// 检查文件是否为空
		ThrowUtils.throwIf(file.isEmpty(), ErrorCode.PARAMS_ERROR, "文件不能为空");
		
		// 获取文件名并检查是否为null
		String filename = file.getOriginalFilename();
		ThrowUtils.throwIf(filename == null, ErrorCode.PARAMS_ERROR, "文件名不能为空");
		
		// 检查文件格式是否为Excel格式
		if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
			throw new RuntimeException("上传文件格式不正确");
		}
		
		// 调用服务层处理用户导入
		Map<String, Object> result = certificateService.importCertificates(file, request);
		return ResultUtils.success(result);
	}
	
	
	/**
	 * 导出证书数据为Excel文件
	 * <p>
	 * 1. 查询证书数据并转换为Excel导出所需格式。
	 * 2. 设置下载的响应参数。
	 * 3. 将数据写入Excel并导出，若出现异常将返回部分数据。
	 *
	 * @param response HttpServletResponse对象，用于文件下载
	 * @throws IOException 当输出流发生异常时抛出
	 */
	@GetMapping("/certificate/download")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void downloadCertificate(HttpServletResponse response) throws IOException {
		// 查询证书数据并转换为CertificateExcelVO对象列表
		List<CompletableFuture<CertificateExcelVO>> futures = certificateService.list().stream().map(certificate -> CompletableFuture.supplyAsync(() -> {
			CertificateExcelVO certificateExcelVO = new CertificateExcelVO();
			BeanUtils.copyProperties(certificate, certificateExcelVO);
			
			// 转换证书及相关字段信息
			certificateExcelVO.setId(String.valueOf(certificate.getId()));
			certificateExcelVO.setCertificateType(
					Objects.requireNonNull(CertificateTypeEnum.getEnumByValue(certificate.getCertificateType())).getText());
			certificateExcelVO.setCertificateSituation(
					Objects.requireNonNull(CertificateSituationEnum.getEnumByValue(certificate.getCertificateSituation())).getText());
			certificateExcelVO.setReviewStatus(
					Objects.requireNonNull(ReviewStatusEnum.getEnumByValue(certificate.getReviewStatus())).getText());
			certificateExcelVO.setReviewerId(String.valueOf(certificate.getReviewerId()));
			certificateExcelVO.setReviewTime(ExcelUtils.dateToString(certificate.getReviewTime()));
			certificateExcelVO.setCertificateUrl(Optional.ofNullable(certificate.getCertificateUrl()).orElse("证书尚未生成"));
			
			// 获取证书获得人信息并设置
			User user = userService.getById(certificate.getGainUserId());
			certificateExcelVO.setGainUserName(user.getUserName());
			certificateExcelVO.setGainUserNumber(user.getUserNumber());
			return certificateExcelVO;
		})).collect(Collectors.toList());
		// 等待并发项目异步执行完成
		List<CertificateExcelVO> certificateExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 设置Excel文件下载的响应属性
		ExcelUtils.setExcelResponseProp(response, ExcelConstant.CERTIFICATE_EXCEL);
		// 写入 Excel 文件并下载
		try (OutputStream outputStream = response.getOutputStream()) {
			EasyExcel.write(outputStream, CertificateExcelVO.class)
					.sheet(ExcelConstant.CERTIFICATE_EXCEL)
					.doWrite(certificateExcelVOList);
			log.info("文件导出成功");
		} catch (Exception e) {
			log.error("文件导出失败: {}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "文件导出失败");
		}
	}
	
	/**
	 * 证书数据下载示例数据
	 * 文件下载（失败了会返回一个有部分数据的Excel）
	 * 1. 创建excel对应的实体对象
	 * 2. 设置返回的 参数
	 * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
	 *
	 * @param response response
	 */
	@GetMapping("/certificate/download/example")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void downloadCertificateExample(HttpServletResponse response) throws IOException {
		// 获取数据，根据自身业务修改
		List<CertificateImportExcelVO> certificateImportExcelVOList = new ArrayList<>();
		CertificateImportExcelVO certificateImportExcelVO = new CertificateImportExcelVO();
		certificateImportExcelVOList.add(certificateImportExcelVO);
		// 设置导出名称
		ExcelUtils.setExcelResponseProp(response, ExcelConstant.CERTIFICATE_EXCEL_EXAMPLE);
		// 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
		// 写入 Excel 文件
		try {
			EasyExcel.write(response.getOutputStream(), CertificateImportExcelVO.class)
					.sheet(ExcelConstant.CERTIFICATE_EXCEL_EXAMPLE)
					.doWrite(certificateImportExcelVOList);
			log.info("文件导出成功");
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败");
			log.error("导出失败:{}", e.getMessage());
		}
	}
	
	/**
	 * 课程批量导入
	 *
	 * @param file    课程 Excel 文件
	 * @param request request
	 * @return 导入结果
	 */
	@PostMapping("/course/import")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Map<String, Object>> importCourseDataByExcel(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
		// 检查文件是否为空
		ThrowUtils.throwIf(file.isEmpty(), ErrorCode.PARAMS_ERROR, "文件不能为空");
		
		// 获取文件名并检查是否为null
		String filename = file.getOriginalFilename();
		ThrowUtils.throwIf(filename == null, ErrorCode.PARAMS_ERROR, "文件名不能为空");
		
		// 检查文件格式是否为Excel格式
		if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
			throw new RuntimeException("上传文件格式不正确");
		}
		
		// 调用服务层处理课程导入
		Map<String, Object> result = courseService.importCourse(file, request);
		return ResultUtils.success(result);
	}
	
	/**
	 * 课程数据导出
	 * 文件下载（失败了会返回一个有部分数据的Excel）
	 * 1. 创建excel对应的实体对象
	 * 2. 设置返回的 参数
	 * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
	 *
	 * @param response response
	 */
	@GetMapping("/course/download")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void downloadCourse(HttpServletResponse response) throws IOException {
		// 获取数据，根据自身业务修改
		List<CompletableFuture<CourseExcelVO>> futures = courseService.list().stream().map(course -> CompletableFuture.supplyAsync(() -> {
			CourseExcelVO userExcelVO = new CourseExcelVO();
			BeanUtils.copyProperties(course, userExcelVO);
			// 异步获取用户信息
			CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> userService.getById(course.getUserId()));
			// 等待所有异步任务完成并设置相关信息
			try {
				User user = userFuture.get();
				userExcelVO.setUserName(user.getUserName());
				userExcelVO.setAcquisitionTime(ExcelUtils.dateToString(course.getAcquisitionTime()));
				userExcelVO.setFinishTime(ExcelUtils.dateToString(course.getFinishTime()));
			} catch (InterruptedException | ExecutionException e) {
				log.error("数据获取失败: {}", e.getMessage());
				Thread.currentThread().interrupt();
			}
			
			return userExcelVO;
		})).collect(Collectors.toList());
		// 等待所有 CompletableFuture 执行完毕
		List<CourseExcelVO> courseExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 设置导出名称
		ExcelUtils.setExcelResponseProp(response, ExcelConstant.COURSE_EXCEL);
		// 写入 Excel 文件
		try {
			EasyExcel.write(response.getOutputStream(), CourseExcelVO.class)
					.sheet(ExcelConstant.COURSE_EXCEL)
					.doWrite(courseExcelVOList);
			log.info("文件导出成功");
		} catch (Exception e) {
			log.error("导出失败:{}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败");
		}
	}
	
	
	/**
	 * 课程数据下载示例数据
	 * 文件下载（失败了会返回一个有部分数据的Excel）
	 * 1. 创建excel对应的实体对象
	 * 2. 设置返回的 参数
	 * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
	 *
	 * @param response response
	 */
	@GetMapping("/course/download/example")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void downloadCourseExample(HttpServletResponse response) throws IOException {
		// 获取数据，根据自身业务修改
		List<CourseExcelExampleVO> courseExcelExampleVOList = new ArrayList<>();
		CourseExcelExampleVO courseExcelExampleVO = new CourseExcelExampleVO();
		courseExcelExampleVOList.add(courseExcelExampleVO);
		// 设置导出名称
		ExcelUtils.setExcelResponseProp(response, ExcelConstant.CERTIFICATE_EXCEL_EXAMPLE);
		// 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
		// 写入 Excel 文件
		try {
			EasyExcel.write(response.getOutputStream(), CourseExcelExampleVO.class)
					.sheet(ExcelConstant.CERTIFICATE_EXCEL_EXAMPLE)
					.doWrite(courseExcelExampleVOList);
			log.info("文件导出成功");
		} catch (Exception e) {
			log.error("导出失败:{}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败");
		}
	}
	
	/**
	 * 用户证书数据导出
	 * 文件下载（失败了会返回一个有部分数据的Excel）
	 * 1. 创建excel对应的实体对象
	 * 2. 设置返回的 参数
	 * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
	 *
	 * @param response response
	 */
	@GetMapping("/user/certificate/download")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void downloadUserCertificate(HttpServletResponse response) throws IOException {
		// 获取数据，根据自身业务修改
		List<CompletableFuture<UserCertificateExcelVO>> futures = userCertificateService.list().stream().map(userCertificate -> CompletableFuture.supplyAsync(() -> {
					UserCertificateExcelVO userCertificateExcelVO = new UserCertificateExcelVO();
					BeanUtils.copyProperties(userCertificate, userCertificateExcelVO);
					
					// 异步获取用户信息
					CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> userService.getById(userCertificate.getUserId()));
					// 等待所有异步任务完成并设置相关信息
					try {
						User user = userFuture.get();
						userCertificateExcelVO.setUserName(user.getUserName());
						userCertificateExcelVO.setUserNumber(user.getUserNumber());
					} catch (InterruptedException | ExecutionException e) {
						log.error("数据获取失败: {}", e.getMessage());
						Thread.currentThread().interrupt();
					}
					return userCertificateExcelVO;
					
				}))
				.collect(Collectors.toList());
		List<UserCertificateExcelVO> userCertificateExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		
		// 设置导出名称
		ExcelUtils.setExcelResponseProp(response, ExcelConstant.USER_CERTIFICATE_EXCEL);
		// 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
		// 写入 Excel 文件
		try {
			EasyExcel.write(response.getOutputStream(), UserCertificateExcelVO.class)
					.sheet(ExcelConstant.USER_CERTIFICATE_EXCEL)
					.doWrite(userCertificateExcelVOList);
			log.info("文件导出成功");
		} catch (Exception e) {
			log.error("导出失败:{}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败");
		}
	}
	
	/**
	 * 打印证书数据导出
	 * 文件下载（失败了会返回一个有部分数据的Excel）
	 * 1. 创建excel对应的实体对象
	 * 2. 设置返回的 参数
	 * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
	 *
	 * @param response response
	 */
	@GetMapping("/log/certificate/download")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void downloadLogPrintCertificate(HttpServletResponse response) throws IOException {
		// 获取数据，根据自身业务修改
		List<CompletableFuture<LogPrintCertificateExcelVO>> futures = logPrintCertificateService.list().stream().map(logPrintCertificate -> CompletableFuture.supplyAsync(() -> {
			LogPrintCertificateExcelVO logPrintCertificateExcelVO = new LogPrintCertificateExcelVO();
			BeanUtils.copyProperties(logPrintCertificate, logPrintCertificateExcelVO);
			// 异步获取用户、课程、证书信息
			CompletableFuture<User> userCompletableFuture = CompletableFuture.supplyAsync(() -> userService.getById(logPrintCertificate.getUserId()));
			CompletableFuture<Course> courseCompletableFuture = CompletableFuture.supplyAsync(() -> courseService.getById(logPrintCertificate.getCourseId()));
			CompletableFuture<Certificate> certificateCompletableFuture = CompletableFuture.supplyAsync(() -> certificateService.getById(logPrintCertificate.getCertificateId()));
			
			// 等待所有异步任务完成并设置相关信息
			try {
				User user = userCompletableFuture.get();
				Course course = courseCompletableFuture.get();
				Certificate certificate = certificateCompletableFuture.get();
				logPrintCertificateExcelVO.setUserName(user.getUserName());
				logPrintCertificateExcelVO.setUserIdCard(EncryptionUtils.decrypt(user.getUserIdCard()));
				logPrintCertificateExcelVO.setUserGender(Objects.requireNonNull(UserGenderEnum.getEnumByValue(user.getUserGender())).getText());
				logPrintCertificateExcelVO.setCertificateNumber(certificate.getCertificateNumber());
				logPrintCertificateExcelVO.setCourseName(course.getCourseName());
				logPrintCertificateExcelVO.setAcquisitionTime(ExcelUtils.dateToExcelString(logPrintCertificate.getAcquisitionTime()));
				logPrintCertificateExcelVO.setFinishTime(ExcelUtils.dateToExcelString(logPrintCertificate.getFinishTime()));
				
				logPrintCertificateExcelVO.setAcquisitionTime(ExcelUtils.dateToExcelString(logPrintCertificate.getAcquisitionTime()));
				logPrintCertificateExcelVO.setFinishTime(ExcelUtils.dateToExcelString(logPrintCertificate.getFinishTime()));
			} catch (InterruptedException | ExecutionException e) {
				throw new BusinessException(ErrorCode.EXCEL_ERROR, "信息导出失败" + e.getMessage());
			}
			return logPrintCertificateExcelVO;
		})).collect(Collectors.toList());
		List<LogPrintCertificateExcelVO> logPrintCertificateExcelVOList = futures.stream().map(CompletableFuture::join).collect(Collectors.toList());
		// 设置导出名称
		ExcelUtils.setExcelResponseProp(response, ExcelConstant.LOG_PRINT_CERTIFICATE_EXCEL);
		// 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
		// 写入 Excel 文件
		try {
			EasyExcel.write(response.getOutputStream(), LogPrintCertificateExcelVO.class)
					.sheet(ExcelConstant.LOG_PRINT_CERTIFICATE_EXCEL)
					.doWrite(logPrintCertificateExcelVOList);
			log.info("文件导出成功");
		} catch (Exception e) {
			log.error("导出失败:{}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败");
		}
	}
	
	/**
	 * 用户课程批量导入
	 *
	 * @param file    课程 Excel 文件
	 * @param request request
	 * @return 导入结果
	 */
	@PostMapping("/user/course/import")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	@Transactional(rollbackFor = Exception.class)
	public BaseResponse<Map<String, Object>> importUserCourseDataByExcel(@RequestPart("file") MultipartFile file, HttpServletRequest request) {
		// 检查文件是否为空
		ThrowUtils.throwIf(file.isEmpty(), ErrorCode.PARAMS_ERROR, "文件不能为空");
		
		// 获取文件名并检查是否为null
		String filename = file.getOriginalFilename();
		ThrowUtils.throwIf(filename == null, ErrorCode.PARAMS_ERROR, "文件名不能为空");
		
		// 检查文件格式是否为Excel格式
		if (!filename.endsWith(".xlsx") && !filename.endsWith(".xls")) {
			throw new RuntimeException("上传文件格式不正确");
		}
		
		// 调用服务层处理课程导入
		Map<String, Object> result = userCourseService.importUserCourse(file, request);
		return ResultUtils.success(result);
	}
	
	/**
	 * 用户课程数据导出
	 * 文件下载（失败了会返回一个有部分数据的Excel）
	 * 1. 创建excel对应的实体对象
	 * 2. 设置返回的 参数
	 * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
	 *
	 * @param response response
	 */
	@GetMapping("/user/course/download")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void downloadUserCourse(HttpServletResponse response) throws IOException {
		// 获取数据，并发处理 userService 和 courseService 的查询操作
		List<CompletableFuture<UserCourseExcelVO>> futures = userCourseService.list().stream().map(userCourse ->
				CompletableFuture.supplyAsync(() -> {
					UserCourseExcelVO userCourseExcelVO = new UserCourseExcelVO();
					BeanUtils.copyProperties(userCourse, userCourseExcelVO);
					
					// 异步获取用户信息
					CompletableFuture<User> userFuture = CompletableFuture.supplyAsync(() -> userService.getById(userCourse.getUserId()));
					// 异步获取课程信息
					CompletableFuture<Course> courseFuture = CompletableFuture.supplyAsync(() -> courseService.getById(userCourse.getCourseId()));
					
					// 等待所有异步任务完成并设置相关信息
					try {
						User user = userFuture.get();
						Course course = courseFuture.get();
						
						userCourseExcelVO.setUserName(user.getUserName());
						userCourseExcelVO.setUserNumber(user.getUserNumber());
						userCourseExcelVO.setCourseName(course.getCourseName());
						userCourseExcelVO.setCourseNumber(String.valueOf(course.getCourseNumber()));
					} catch (InterruptedException | ExecutionException e) {
						log.error("数据获取失败: {}", e.getMessage());
						Thread.currentThread().interrupt();
					}
					
					return userCourseExcelVO;
				})
		).collect(Collectors.toList());
		
		// 等待所有 CompletableFuture 执行完毕
		List<UserCourseExcelVO> userCourseExcelVOList = futures.stream()
				.map(CompletableFuture::join)
				.collect(Collectors.toList());
		
		// 设置导出名称
		ExcelUtils.setExcelResponseProp(response, ExcelConstant.USER_COURSE_EXCEL);
		
		// 写入 Excel 文件
		try {
			EasyExcel.write(response.getOutputStream(), UserCourseExcelVO.class)
					.sheet(ExcelConstant.USER_COURSE_EXCEL)
					.doWrite(userCourseExcelVOList);
			log.info("文件导出成功");
		} catch (Exception e) {
			log.error("导出失败:{}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败");
		}
	}
	
	/**
	 * 用户课程数据下载示例数据
	 * 文件下载（失败了会返回一个有部分数据的Excel）
	 * 1. 创建excel对应的实体对象
	 * 2. 设置返回的 参数
	 * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
	 *
	 * @param response response
	 */
	@GetMapping("/uer/course/download/example")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void downloadUserCourseExample(HttpServletResponse response) throws IOException {
		// 获取数据，根据自身业务修改
		List<UserCourseExcelVO> userCourseExcelVOList = new ArrayList<>();
		UserCourseExcelVO userCourseExcelExampleVO = new UserCourseExcelVO();
		userCourseExcelVOList.add(userCourseExcelExampleVO);
		// 设置导出名称
		ExcelUtils.setExcelResponseProp(response, ExcelConstant.USER_COURSE_EXCEL_EXAMPLE);
		// 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
		// 写入 Excel 文件
		try {
			EasyExcel.write(response.getOutputStream(), UserCourseExcelVO.class)
					.sheet(ExcelConstant.USER_COURSE_EXCEL_EXAMPLE)
					.doWrite(userCourseExcelVOList);
			log.info("文件导出成功");
		} catch (Exception e) {
			log.error("导出失败:{}", e.getMessage());
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "导出失败");
		}
	}
}