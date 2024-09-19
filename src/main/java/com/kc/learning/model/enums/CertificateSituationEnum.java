package com.kc.learning.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 证书获得情况(0-有,1-没有)
 *
 * @author: stephen qiu
 * @create: 2024-08-30 07:58
 **/
@Getter
public enum CertificateSituationEnum {
	
	HAVA("有", 0),
	NONE("没有", 1);
	
	private final String text;
	
	private final Integer value;
	
	CertificateSituationEnum(String text, Integer value) {
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
	public static CertificateSituationEnum getEnumByValue(Integer value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (CertificateSituationEnum anEnum : CertificateSituationEnum.values()) {
			if (anEnum.value.equals(value)) {
				return anEnum;
			}
		}
		return null;
	}
}
