package com.kc.learning.utils;

import com.deepoove.poi.XWPFTemplate;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.exception.BusinessException;
import com.kc.learning.model.vo.logPrintCertificate.LogPrintCertificateExcelVO;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @description: word工具类
 * @author: stephen qiu
 * @create: 2024-11-09 16:07
 **/
public class WordUtils {
	
	/**
	 * 生成证书
	 *
	 * @param logPrintCertificateExcelVO logPrintCertificateExcelVO
	 * @return {@link String}
	 */
	public static String generateCertificate(LogPrintCertificateExcelVO logPrintCertificateExcelVO) {
		try {
			return CompletableFuture.supplyAsync(() -> {
				try {
					// 加载 Word 模板
					XWPFTemplate template = XWPFTemplate.compile("src/main/resources/template/certificate_template.docx").render(
							new HashMap<String, Object>() {
								private static final long serialVersionUID = -7128893985449366351L;
								
								{
									put("userName", logPrintCertificateExcelVO.getUserName());
									put("userIdCard", logPrintCertificateExcelVO.getUserIdCard());
									put("userGender", logPrintCertificateExcelVO.getUserGender());
									put("certificateNumber", logPrintCertificateExcelVO.getCertificateNumber());
									put("courseName", logPrintCertificateExcelVO.getCourseName());
									put("acquisitionTime", logPrintCertificateExcelVO.getAcquisitionTime());
									put("finishTime", logPrintCertificateExcelVO.getFinishTime());
								}
							}
					);
					
					// 输出填充后的 Word 文件
					String outputPath = "src/main/resources/temp/" + logPrintCertificateExcelVO.getUserName() + "_" + logPrintCertificateExcelVO.getCertificateNumber() + ".docx";
					template.writeToFile(outputPath);
					// 关闭文件输出流
					template.close();
					return outputPath;
				} catch (Exception e) {
					throw new BusinessException(ErrorCode.SYSTEM_ERROR, "证书生成失败: " + e.getMessage());
				}
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			throw new BusinessException(ErrorCode.SYSTEM_ERROR, "证书生成失败: " + e.getMessage());
		}
	}
}

