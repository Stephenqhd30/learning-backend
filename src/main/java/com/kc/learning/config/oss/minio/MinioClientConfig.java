package com.kc.learning.config.oss.minio;

import com.kc.learning.common.ErrorCode;
import com.kc.learning.common.exception.BusinessException;
import com.kc.learning.config.oss.minio.condition.MinioCondition;
import com.kc.learning.config.oss.minio.properties.MinioProperties;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;


/**
 * Minio配置属性
 *
 * @author stephen qiu
 */
@Slf4j
@Conditional(MinioCondition.class)
public class MinioClientConfig {
	
	@Resource
	private MinioProperties minioProperties;
	
	/**
	 * 获取MinioClient客户端
	 *
	 * @return 返回MinioClient客户端
	 */
	@Bean("minioClientBean")
	public MinioClient getMinioClient() {
		try {
			String[] ipAndPort = minioProperties.getEndpoint().split(":");
			return MinioClient.builder()
					.endpoint(ipAndPort[0], Integer.parseInt(ipAndPort[1]), minioProperties.getEnableTls())
					.credentials(minioProperties.getSecretId(), minioProperties.getSecretKey())
					.build();
		} catch (Exception e) {
			log.error("MinIO服务器构建异常：{}", e.getMessage());
			throw new BusinessException(ErrorCode.OPERATION_ERROR, "MinIO服务器构建异常");
		}
	}
	
	/**
	 * 依赖注入日志输出
	 */
	@PostConstruct
	private void initDi() {
		log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
	}
}