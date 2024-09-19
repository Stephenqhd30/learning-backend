package com.kc.learning.model.enums;

import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 证书状态(0-待审核,1-通过,2-拒绝)
 *
 * @author: stephen qiu
 * @create: 2024-08-30 07:58
 **/
@Getter
public enum ReviewStatusEnum {
	
	REVIEWING("待审核", 0),
	PASS("通过", 1),
	REJECT("拒绝", 2);
	
	private final String text;
	
	private final Integer value;
	
	ReviewStatusEnum(String text, Integer value) {
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
	public static ReviewStatusEnum getEnumByValue(Integer value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (ReviewStatusEnum anEnum : ReviewStatusEnum.values()) {
			if (anEnum.value.equals(value)) {
				return anEnum;
			}
		}
		return null;
	}
}
