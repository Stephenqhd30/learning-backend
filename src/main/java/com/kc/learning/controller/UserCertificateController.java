package com.kc.learning.controller;

import com.alibaba.excel.EasyExcel;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.kc.learning.annotation.AuthCheck;
import com.kc.learning.common.BaseResponse;
import com.kc.learning.common.DeleteRequest;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.constants.ExcelConstant;
import com.kc.learning.constants.UserConstant;
import com.kc.learning.exception.BusinessException;
import com.kc.learning.model.dto.userCertificate.UserCertificateQueryRequest;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.entity.UserCertificate;
import com.kc.learning.model.vo.UserCertificateExcelVO;
import com.kc.learning.model.vo.UserCertificateVO;
import com.kc.learning.service.CertificateService;
import com.kc.learning.service.UserCertificateService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.ExcelUtils;
import com.kc.learning.utils.ResultUtils;
import com.kc.learning.utils.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户证书接口
 *
 * @author stephen qiu
 */
@RestController
@RequestMapping("/userCertificate")
@Slf4j
public class UserCertificateController {
	
	@Resource
	private UserCertificateService userCertificateService;
	
	@Resource
	private UserService userService;
	
	@Resource
	private CertificateService certificateService;
	
	// region 增删改查
	
	/**
	 * 删除用户证书
	 *
	 * @param deleteRequest deleteRequest
	 * @param request       request
	 * @return BaseResponse<Boolean>
	 */
	@PostMapping("/delete")
	public BaseResponse<Boolean> deleteUserCertificate(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
		if (deleteRequest == null || deleteRequest.getId() <= 0) {
			throw new BusinessException(ErrorCode.PARAMS_ERROR);
		}
		long id = deleteRequest.getId();
		// 判断是否存在
		UserCertificate oldUserCertificate = userCertificateService.getById(id);
		ThrowUtils.throwIf(oldUserCertificate == null, ErrorCode.NOT_FOUND_ERROR);
		// 仅管理员可删除
		if (!userService.isAdmin(request)) {
			throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
		}
		// 操作数据库
		boolean result = userCertificateService.removeById(id);
		ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
		return ResultUtils.success(true);
	}
	
	/**
	 * 根据 id 获取用户证书（封装类）
	 *
	 * @param id
	 * @return
	 */
	@GetMapping("/get/vo")
	public BaseResponse<UserCertificateVO> getUserCertificateVOById(long id, HttpServletRequest request) {
		ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		UserCertificate userCertificate = userCertificateService.getById(id);
		ThrowUtils.throwIf(userCertificate == null, ErrorCode.NOT_FOUND_ERROR);
		// 获取封装类
		return ResultUtils.success(userCertificateService.getUserCertificateVO(userCertificate, request));
	}
	
	/**
	 * 分页获取用户证书列表（仅管理员可用）
	 *
	 * @param userCertificateQueryRequest userCertificateQueryRequest
	 * @return {@Link BaseResponse<Page < UserCertificate>>}
	 */
	@PostMapping("/list/page")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public BaseResponse<Page<UserCertificate>> listUserCertificateByPage(@RequestBody UserCertificateQueryRequest userCertificateQueryRequest) {
		long current = userCertificateQueryRequest.getCurrent();
		long size = userCertificateQueryRequest.getPageSize();
		// 查询数据库
		Page<UserCertificate> userCertificatePage = userCertificateService.page(new Page<>(current, size),
				userCertificateService.getQueryWrapper(userCertificateQueryRequest));
		return ResultUtils.success(userCertificatePage);
	}
	
	/**
	 * 分页获取用户证书列表（封装类）
	 * 只查看已经过审核了的证书
	 *
	 * @param userCertificateQueryRequest userCertificateQueryRequest
	 * @param request                     request
	 * @return BaseResponse<Page < UserCertificateVO>>
	 */
	@PostMapping("/list/page/vo")
	public BaseResponse<Page<UserCertificateVO>> listUserCertificateVOByPage(@RequestBody UserCertificateQueryRequest userCertificateQueryRequest,
	                                                                         HttpServletRequest request) {
		long current = userCertificateQueryRequest.getCurrent();
		long size = userCertificateQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<UserCertificate> userCertificatePage = userCertificateService.page(new Page<>(current, size),
				userCertificateService.getQueryWrapper(userCertificateQueryRequest));
		// 获取封装类
		return ResultUtils.success(userCertificateService.getUserCertificateVOPage(userCertificatePage, request));
	}
	
	/**
	 * 分页获取当前登录用户创建的用户证书列表
	 *
	 * @param userCertificateQueryRequest
	 * @param request
	 * @return
	 */
	@PostMapping("/my/list/page/vo")
	public BaseResponse<Page<UserCertificateVO>> listMyUserCertificateVOByPage(@RequestBody UserCertificateQueryRequest userCertificateQueryRequest,
	                                                                           HttpServletRequest request) {
		ThrowUtils.throwIf(userCertificateQueryRequest == null, ErrorCode.PARAMS_ERROR);
		// 补充查询条件，只查询当前登录用户的数据
		User loginUser = userService.getLoginUser(request);
		userCertificateQueryRequest.setUserId(loginUser.getId());
		long current = userCertificateQueryRequest.getCurrent();
		long size = userCertificateQueryRequest.getPageSize();
		// 限制爬虫
		ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
		// 查询数据库
		Page<UserCertificate> userCertificatePage = userCertificateService.page(new Page<>(current, size),
				userCertificateService.getQueryWrapper(userCertificateQueryRequest));
		// 获取封装类
		return ResultUtils.success(userCertificateService.getUserCertificateVOPage(userCertificatePage, request));
	}
	
	// endregion
	
	/**
	 * 用户证书数据导出
	 * 文件下载（失败了会返回一个有部分数据的Excel）
	 * 1. 创建excel对应的实体对象
	 * 2. 设置返回的 参数
	 * 3. 直接写，这里注意，finish的时候会自动关闭OutputStream,当然你外面再关闭流问题不大
	 *
	 * @param response response
	 */
	@GetMapping("/download")
	@AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
	public void downloadUserCertificate(HttpServletResponse response) throws IOException {
		// 获取数据，根据自身业务修改
		List<UserCertificateExcelVO> userCertificateExcelVOList = userCertificateService.list().stream().map(userCertificate -> {
					UserCertificateExcelVO userCertificateExcelVO = new UserCertificateExcelVO();
					BeanUtils.copyProperties(userCertificate, userCertificateExcelVO);
					userCertificateExcelVO.setId(String.valueOf(userCertificate.getId()));
					userCertificateExcelVO.setUserId(String.valueOf(userCertificate.getUserId()));
					userCertificateExcelVO.setCertificateId(String.valueOf(userCertificate.getCertificateId()));
					userCertificateExcelVO.setCreateTime(ExcelUtils.dateToString(userCertificate.getCreateTime()));
					userCertificateExcelVO.setUpdateTime(ExcelUtils.dateToString(userCertificate.getUpdateTime()));
					
					return userCertificateExcelVO;
				})
				.collect(Collectors.toList());
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
	
}
