package com.kc.learning.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件上传业务类型枚举
 *
 * @author stephen qiu
 */
@Getter
public enum FileUploadBizEnum {
	
	USER_AVATAR("用户头像", "user_avatar"),
	CERTIFICATE_URL("证书", "certificate_url");
	
	private final String text;
	
	private final String value;
	
	FileUploadBizEnum(String text, String value) {
		this.text = text;
		this.value = value;
	}
	
	/**
	 * 获取值列表
	 *
	 * @return List<String>
	 */
	public static List<String> getValues() {
		return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
	}
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value value
	 * @return FileUploadBizEnum
	 */
	public static FileUploadBizEnum getEnumByValue(String value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (FileUploadBizEnum anEnum : FileUploadBizEnum.values()) {
			if (anEnum.value.equals(value)) {
				return anEnum;
			}
		}
		return null;
	}
	
}