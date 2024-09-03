package com.stephen.popcorn.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 证书类型(0-干部培训,1-其他)
 *
 * @author: stephen qiu
 * @create: 2024-08-30 07:58
 **/
@Getter
public enum CertificateTypeEnum {
	
	CADRE("干部培训", 0),
	OTHER("其他", 1);
	
	private final String text;
	
	private final Integer value;
	
	CertificateTypeEnum(String text, Integer value) {
		this.text = text;
		this.value = value;
	}
	
	/**
	 * 获取值列表
	 *
	 * @return
	 */
	public static List<Integer> getValues() {
		return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
	}
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value
	 * @return
	 */
	public static CertificateTypeEnum getEnumByValue(Integer value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (CertificateTypeEnum anEnum : CertificateTypeEnum.values()) {
			if (anEnum.value.equals(value)) {
				return anEnum;
			}
		}
		return null;
	}
}
