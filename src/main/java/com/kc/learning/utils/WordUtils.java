package com.kc.learning.utils;

import com.deepoove.poi.XWPFTemplate;
import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.exception.BusinessException;
import com.kc.learning.model.vo.logPrintCertificate.LogPrintCertificateExcelVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @description: word工具类
 * @author: stephen qiu
 * @create: 2024-11-09 16:07
 **/
@Slf4j
public class WordUtils {
	
	/**
	 * 生成证书
	 *
	 * @param logPrintCertificateExcelVO logPrintCertificateExcelVO
	 * @return {@link String} - 返回生成的 PDF 文件路径
	 */
	public static String generateCertificate(LogPrintCertificateExcelVO logPrintCertificateExcelVO) {
		try {
			return CompletableFuture.supplyAsync(() -> {
				try {
					// 获取当前工作目录
					
					String basePath = System.getProperty("user.dir");
					String templatePath = basePath + "/src/main/resources/template/certificate_template.docx";
					String tempWordPath = basePath + "/src/main/resources/temp/word/" + logPrintCertificateExcelVO.getUserName() + "_" + logPrintCertificateExcelVO.getCertificateNumber() + ".docx";
					String tempPdfPath = basePath + "/src/main/resources/temp/pdf/" + logPrintCertificateExcelVO.getUserName() + "_" + logPrintCertificateExcelVO.getCertificateNumber() + ".pdf";
					
					// 加载并渲染模板
					FileInputStream templateStream = new FileInputStream(templatePath);
					XWPFTemplate template = XWPFTemplate.compile(templateStream).render(
							new HashMap<String, Object>() {{
								put("userName", logPrintCertificateExcelVO.getUserName());
								put("userIdCard", logPrintCertificateExcelVO.getUserIdCard());
								put("userGender", logPrintCertificateExcelVO.getUserGender());
								put("certificateNumber", logPrintCertificateExcelVO.getCertificateNumber());
								put("courseName", logPrintCertificateExcelVO.getCourseName());
								put("acquisitionTime", logPrintCertificateExcelVO.getAcquisitionTime());
								put("finishTime", logPrintCertificateExcelVO.getFinishTime());
							}}
					);
					
					// 输出填充后的 Word 文件
					template.writeToFile(tempWordPath);
					template.close();
					// 将Word文档转换为PDF
					convertToPdf(tempWordPath, tempPdfPath);
					// 返回PDF文件路径
					return tempPdfPath;
				} catch (Exception e) {
					throw new BusinessException(ErrorCode.WORD_ERROR, "证书生成失败: " + e.getMessage());
				}
			}).get();
		} catch (InterruptedException | ExecutionException e) {
			Thread.currentThread().interrupt();
			throw new BusinessException(ErrorCode.WORD_ERROR, "证书生成失败: " + e.getMessage());
		}
	}
	
	/**
	 * 将文件流转换为 MultipartFile
	 *
	 * @param fileInputStream 文件输入流
	 * @param filePath        文件路径
	 * @return {@link MultipartFile}
	 * @throws IOException 异常
	 */
	public static MultipartFile convertToMultipartFile(FileInputStream fileInputStream, String filePath) throws IOException {
		File generatedFile = new File(filePath);
		FileItem fileItem = new DiskFileItem("file", "application/octet-stream", false, generatedFile.getName(), (int) generatedFile.length(), generatedFile.getParentFile());
		try (OutputStream outputStream = fileItem.getOutputStream()) {
			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = fileInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
		}
		return new CommonsMultipartFile(fileItem);
	}
	
	
	/**
	 * 将Word文档转换为PDF
	 *
	 * @param inputFilePath  输入Word文件路径
	 * @param outputFilePath 输出PDF文件路径
	 */
	public static void convertToPdf(String inputFilePath, String outputFilePath) {
		File inputWord = new File(inputFilePath);
		File outputFile = new File(outputFilePath);
		try {
			InputStream docxInputStream = new FileInputStream(inputWord);
			OutputStream outputStream = new FileOutputStream(outputFile);
			IConverter converter = LocalConverter.builder().build();
			// 获取文件类型
			String fileType = inputFilePath.substring(inputFilePath.lastIndexOf("."));
			switch (fileType) {
				case ".docx":
					converter.convert(docxInputStream).as(DocumentType.DOCX).to(outputStream).as(DocumentType.PDF).execute();
					break;
				case ".doc":
					converter.convert(docxInputStream).as(DocumentType.DOC).to(outputStream).as(DocumentType.PDF).execute();
					break;
				case ".xls":
					converter.convert(docxInputStream).as(DocumentType.XLS).to(outputStream).as(DocumentType.PDF).execute();
					break;
				case ".xlsx":
					converter.convert(docxInputStream).as(DocumentType.XLSX).to(outputStream).as(DocumentType.PDF).execute();
					break;
			}
			log.info("Word 文件已成功转换为 PDF 文件");
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.WORD_ERROR, "证书生成失败: " + e.getMessage());
		}
	}
}

