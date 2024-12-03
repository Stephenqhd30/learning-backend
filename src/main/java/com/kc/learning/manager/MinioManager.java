package com.kc.learning.manager;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.config.oss.minio.condition.MinioCondition;
import com.kc.learning.config.oss.minio.properties.MinioProperties;
import com.kc.learning.constants.FileConstant;
import com.kc.learning.common.exception.BusinessException;
import com.kc.learning.model.entity.LogFiles;
import com.kc.learning.model.enums.OssTypeEnum;
import com.kc.learning.service.LogFilesService;
import com.kc.learning.utils.SHA3Utils;
import com.kc.learning.utils.ThrowUtils;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.UUID;

/**
 * MinIO配置
 *
 * @author stephen qiu
 */
@Component
@Slf4j
@Conditional(MinioCondition.class)
public class MinioManager {
	
	@Resource
	private MinioProperties minioProperties;
	
	@Resource
	private LogFilesService logFilesService;
	
	@Resource
	private MinioClient minioClient;
	
	/**
	 * 上传文件到 MinIO
	 *
	 * @param file 待上传的文件
	 * @param path 上传的路径
	 * @return {@link String}
	 */
	@Transactional(rollbackFor = Exception.class)
	public String uploadToMinio(MultipartFile file, String path) throws IOException {
		ThrowUtils.throwIf(file == null || file.isEmpty(), ErrorCode.PARAMS_ERROR, "文件为空");
		// 获取文件的原始名称和后缀
		String originalName = file.getOriginalFilename();
		String suffix = FilenameUtils.getExtension(originalName);
		long fileSize = file.getSize();
		
		// 生成唯一键
		String uniqueKey = SHA3Utils.encrypt(Arrays.toString(file.getBytes()) + originalName + suffix);
		
		// 查询数据库，看文件是否已存在
		LogFiles existingFile = logFilesService.getOne(
				new LambdaQueryWrapper<LogFiles>()
						.eq(LogFiles::getFileKey, uniqueKey)
						.eq(LogFiles::getFileOssType, OssTypeEnum.MINIO.getValue())
		);
		
		if (existingFile != null) {
			// 文件已存在，直接返回 URL
			return existingFile.getFileUrl();
		}
		
		String fileName = UUID.randomUUID().toString().replace("-", "") + "." + suffix;
		String filePath = String.format("%s/%s", path, fileName);
		try (InputStream inputStream = file.getInputStream()) {
			// 读取文件内容
			byte[] dataBytes = inputStream.readAllBytes();
			String key = StringUtils.isBlank(filePath) ? fileName : filePath;
			
			
			// 上传文件到 MinIO
			minioClient.putObject(PutObjectArgs.builder()
					.bucket(minioProperties.getBucket())
					.object(key)
					.stream(new ByteArrayInputStream(dataBytes), dataBytes.length, -1)
					.build());
			
			
			// 保存文件信息
			LogFiles newFile = new LogFiles();
			newFile.setFileKey(uniqueKey);
			newFile.setFileName(key);
			newFile.setFileOriginalName(originalName);
			newFile.setFileSuffix(suffix);
			newFile.setFileSize(fileSize);
			newFile.setFileUrl(FileConstant.MINIO_HOST + filePath);
			newFile.setFileOssType(OssTypeEnum.MINIO.getValue());
			logFilesService.save(newFile);
			
		} catch (Exception e) {
			log.error("文件上传失败: {}", e.getMessage());
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "文件上传失败: " + e.getMessage());
		}
		
		return FileConstant.MINIO_HOST + filePath;
	}
	
	/**
	 * 从MinIO中删除文件
	 *
	 * @param id 文件ID
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteInMinioById(Long id) {
		LogFiles fileInDatabase = logFilesService.getById(id);
		ThrowUtils.throwIf(fileInDatabase == null, ErrorCode.NOT_FOUND_ERROR, "文件不存在");
		if (!logFilesService.removeById(fileInDatabase.getId())) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
		}
		deleteInMinioByUrl(fileInDatabase.getFileUrl());
	}
	
	/**
	 * 从MinIO中彻底删除文件
	 *
	 * @param url 文件URL
	 */
	private void deleteInMinioByUrl(String url) {
		ThrowUtils.throwIf(StringUtils.isEmpty(url), ErrorCode.NOT_FOUND_ERROR, "被删除地址为空");
		String[] split = url.split(minioProperties.getEndpoint() + "/" + minioProperties.getBucket() + "/");
		ThrowUtils.throwIf(split.length != 2, ErrorCode.NOT_FOUND_ERROR, "文件不存在");
		String key = split[1];
		try {
			minioClient.removeObject(RemoveObjectArgs.builder()
					.bucket(minioProperties.getBucket())
					.object(key).build());
		} catch (Exception e) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
		}
	}
}
