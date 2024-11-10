package com.kc.learning.aop.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.constants.ExcelConstant;
import com.kc.learning.model.dto.excel.ErrorRecord;
import com.kc.learning.model.dto.excel.SuccessRecord;
import com.kc.learning.model.entity.Certificate;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.enums.CertificateSituationEnum;
import com.kc.learning.model.enums.ReviewStatusEnum;
import com.kc.learning.model.vo.certificate.CertificateImportExcelVO;
import com.kc.learning.service.CertificateService;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.ThrowUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Excel导入证书数据监听器类
 * <p>
 * 该类用于监听Excel的每一行数据，并将有效数据批量插入数据库。
 * 使用EasyExcel的AnalysisEventListener实现。
 * <p>
 * 注意：每次读取Excel时需新建该监听器实例。
 *
 * @author stephen qiu
 */
@Slf4j
public class CertificateExcelListener extends AnalysisEventListener<CertificateImportExcelVO> {
	
	private final CertificateService certificateService;
	private final UserService userService;
	private final HttpServletRequest request;
	
	/**
	 * 构造函数，初始化服务
	 *
	 * @param certificateService CertificateService实例，用于证书数据处理
	 * @param userService        UserService实例，用于用户数据处理
	 * @param request            HttpServletRequest对象，获取当前登录用户信息
	 */
	public CertificateExcelListener(CertificateService certificateService, UserService userService, HttpServletRequest request) {
		this.certificateService = certificateService;
		this.userService = userService;
		this.request = request;
	}
	
	/**
	 * 缓存的证书数据列表，每批次达到BATCH_COUNT后批量插入数据库
	 */
	private final List<Certificate> cachedDataList = ListUtils.newArrayListWithExpectedSize(ExcelConstant.BATCH_COUNT);
	
	/**
	 * 记录异常信息的列表，用于收集处理错误的数据
	 */
	@Getter
	private final List<ErrorRecord<Certificate>> errorRecords = ListUtils.newArrayList();
	
	/**
	 * 记录成功导入的信息，用于收集处理成功的数据
	 */
	@Getter
	private final List<SuccessRecord<Certificate>> successRecords = ListUtils.newArrayList();
	
	/**
	 * 当解析出现异常时调用
	 *
	 * @param exception 异常对象
	 * @param context   上下文信息
	 */
	@Override
	public void onException(Exception exception, AnalysisContext context) throws Exception {
		log.error("解析过程中出现异常：行号={}, 异常信息={}", context.readRowHolder().getRowIndex(), exception.getMessage());
		throw exception;
	}
	
	/**
	 * 处理每一行数据
	 *
	 * @param certificateImportExcelVO 当前行数据
	 * @param context                  上下文信息
	 */
	@Override
	public void invoke(CertificateImportExcelVO certificateImportExcelVO, AnalysisContext context) {
		Certificate newCertificate = new Certificate();
		BeanUtils.copyProperties(certificateImportExcelVO, newCertificate);
		User loginUser = userService.getLoginUser(request);
		try {
			// 获取当前导入数据的用户
			LambdaQueryWrapper<User> queryWrapper = Wrappers.lambdaQuery(User.class)
					.eq(User::getUserName, certificateImportExcelVO.getUserName())
					.eq(User::getUserNumber, certificateImportExcelVO.getUserNumber());
			User user = userService.getOne(queryWrapper);
			ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR, "未找到用户信息");
			newCertificate.setUserId(user.getId());
			newCertificate.setCertificateSituation(CertificateSituationEnum.NONE.getValue());
			
			// 验证证书数据合法性
			certificateService.validCertificate(newCertificate, true);
			newCertificate.setCertificateNumber(newCertificate.getCertificateNumber());
			newCertificate.setCertificateName(newCertificate.getCertificateName());
			newCertificate.setCertificateType(newCertificate.getCertificateType());
			newCertificate.setCertificateYear(newCertificate.getCertificateYear());
			// 设置证书的审核状态及其他字段
			newCertificate.setReviewStatus(ReviewStatusEnum.REVIEWING.getValue());
			newCertificate.setReviewMessage("管理员导入，请检查审核信息是否正确");
			newCertificate.setCreateUserId(loginUser.getId());
			
			// 将成功记录缓存到列表
			cachedDataList.add(newCertificate);
			successRecords.add(new SuccessRecord<>(newCertificate, "成功导入"));
			
		} catch (Exception e) {
			// 捕获并记录处理失败的数据
			log.error("数据处理异常：行号={}, 异常信息={}", context.readRowHolder().getRowIndex(), e.getMessage());
			errorRecords.add(new ErrorRecord<>(newCertificate, e.getMessage()));
		}
		
		// 达到批量保存的数量时，批量保存数据
		if (cachedDataList.size() >= ExcelConstant.BATCH_COUNT) {
			saveDataAsync();
			cachedDataList.clear();
		}
	}
	
	/**
	 * 数据解析完成后执行的收尾操作
	 *
	 * @param context 上下文信息
	 */
	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		// 处理剩余未保存的数据
		if (!cachedDataList.isEmpty()) {
			saveDataAsync();
			cachedDataList.clear();
		}
		log.info("所有数据解析完成，sheet名称={}！", context.readSheetHolder().getSheetName());
	}
	
	/**
	 * 执行批量保存数据操作
	 */
	private void saveDataAsync() {
		List<Certificate> dataToSave = List.copyOf(cachedDataList);
		CompletableFuture.runAsync(() -> {
			log.info("开始批量保存{}条数据到数据库...", dataToSave.size());
			try {
				certificateService.saveBatch(dataToSave);
				log.info("批量保存数据库成功！");
			} catch (Exception e) {
				log.error("批量保存数据库失败：{}", e.getMessage());
				errorRecords.add(new ErrorRecord<>(null, "批量保存失败：" + e.getMessage()));
			}
		});
	}
}
