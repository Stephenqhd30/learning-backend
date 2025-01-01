package com.kc.learning.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

/**
 * 证书状态枚举类
 * 证书状态(wait,running,succeed,failed)
 *
 * @author stephen qiu
 */
@Getter
@AllArgsConstructor
public enum CertificateStatusEnum {
	
	WAIT("等待中", "wait"),
	RUNNING("执行中", "running"),
	SUCCEED("执行成功", "succeed"),
	FAILED("执行失败", "failed");
	
	private final String text;
	
	private final String value;
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value value
	 * @return {@link CertificateStatusEnum}
	 */
	public static CertificateStatusEnum getEnumByValue(String value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (CertificateStatusEnum reviewStatusEnum : CertificateStatusEnum.values()) {
			if (Objects.equals(reviewStatusEnum.value, value)) {
				return reviewStatusEnum;
			}
		}
		return null;
	}
}
