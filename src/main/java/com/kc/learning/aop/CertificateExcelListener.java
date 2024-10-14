package com.kc.learning.aop;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.kc.learning.constants.ExcelConstant;
import com.kc.learning.model.dto.excel.ErrorRecord;
import com.kc.learning.model.dto.excel.SuccessRecord;
import com.kc.learning.model.entity.Certificate;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.enums.ReviewStatusEnum;
import com.kc.learning.service.CertificateService;
import com.kc.learning.service.UserService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 导入证书 excel文件监听器
 *
 * @author: stephen qiu
 * @create: 2024-09-26 10:36
 **/
@Slf4j
public class CertificateExcelListener extends AnalysisEventListener<Certificate> {
	
	private final CertificateService certificateService;
	
	private final UserService userService;
	
	private final HttpServletRequest request;
	
	/**
	 * 有个很重要的点 CertificateExcelListener 不能被spring管理，
	 * 要每次读取excel都要new,然后里面用到spring可以构造方法传进去
	 *
	 * @param certificateService certificateService
	 */
	public CertificateExcelListener(CertificateService certificateService, UserService userService, HttpServletRequest request) {
		this.certificateService = certificateService;
		this.userService = userService;
		this.request = request;
	}
	
	/**
	 * 缓存的数据
	 */
	private final List<Certificate> cachedDataList = ListUtils.newArrayListWithExpectedSize(ExcelConstant.BATCH_COUNT);
	
	/**
	 * 用于记录异常信息
	 * -- GETTER --
	 * 返回异常信息给外部调用者
	 */
	@Getter
	private final List<ErrorRecord<Certificate>> errorRecords = ListUtils.newArrayList();
	
	/**
	 * 用于记录正常导入信息
	 * -- GETTER --
	 * 返回异常信息给外部调用者
	 */
	@Getter
	private final List<SuccessRecord<Certificate>> successRecords = ListUtils.newArrayList();
	
	
	/**
	 * @param exception exception
	 * @param context   context
	 */
	@Override
	public void onException(Exception exception, AnalysisContext context) throws Exception {
		log.error("解析异常: {}", exception.getMessage());
		throw exception;
	}
	
	/**
	 * 当读取到一行数据时，会调用这个方法，并将读取到的数据以及上下文信息作为参数传入
	 * 可以在这个方法中对读取到的数据进行处理和操作，处理数据时要注意异常错误，保证读取数据的稳定性
	 *
	 * @param certificate certificate
	 * @param context     context
	 */
	@Override
	public void invoke(Certificate certificate, AnalysisContext context) {
		Certificate newCertificate = new Certificate();
		BeanUtils.copyProperties(certificate, newCertificate);
		User loginUser = userService.getLoginUser(request);
		try {
			// 先检查用户传入参数是否合法
			certificateService.validCertificate(newCertificate, true);
			// 提取当前时间，避免重复调用
			Date currentTime = new Date();
			
			newCertificate.setReviewStatus(ReviewStatusEnum.REVIEWING.getValue());
			newCertificate.setReviewMessage("管理员导入,请检查审核信息是否正确");
			newCertificate.setUserId(loginUser.getId());
			newCertificate.setCreateTime(currentTime);
			newCertificate.setUpdateTime(currentTime);
			newCertificate.setIsDelete(0);
			
			cachedDataList.add(newCertificate);
			successRecords.add(new SuccessRecord<>(newCertificate, "成功导入"));
		} catch (Exception e) {
			// 捕获异常并记录
			log.error("处理数据时出现异常: {}", e.getMessage());
			// 将错误的记录信息存储到列表中
			errorRecords.add(new ErrorRecord<>(newCertificate, e.getMessage()));
		}
		if (cachedDataList.size() >= ExcelConstant.BATCH_COUNT) {
			saveData();
			cachedDataList.clear();
		}
	}
	
	/**
	 * 当每个sheet所有数据读取完毕后，会调用这个方法，可以在这个方法中进行一些收尾工作，如资源释放、数据汇总等。
	 *
	 * @param context context
	 */
	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		// 收尾工作，处理剩下的缓存数据。。。
		if (!cachedDataList.isEmpty()) {
			saveData();
		}
		log.info("sheet={} 所有数据解析完成！", context.readSheetHolder().getSheetName());
	}
	
	/**
	 * 处理数据，如插入数据库
	 */
	private void saveData() {
		log.info("{} 条数据，开始存储数据库！", cachedDataList.size());
		// 批量插入数据库的逻辑 例如通过服务保存
		certificateService.saveBatch(cachedDataList);
		log.info("存储数据库成功！");
	}
}
