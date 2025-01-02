package com.kc.learning.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;

import java.util.Objects;

/**
 * 课程状态枚举类
 * 课程状态(wait,begin,end)
 *
 * @author stephen qiu
 */
@Getter
@AllArgsConstructor
public enum CourseStatusEnum {
	
	WAIT("未开始", "wait"),
	BEGIN("进行中", "begin"),
	END("结束", "end");
	
	private final String text;
	
	private final String value;
	
	/**
	 * 根据 value 获取枚举
	 *
	 * @param value value
	 * @return {@link CourseStatusEnum}
	 */
	public static CourseStatusEnum getEnumByValue(String value) {
		if (ObjectUtils.isEmpty(value)) {
			return null;
		}
		for (CourseStatusEnum courseStatusEnum : CourseStatusEnum.values()) {
			if (Objects.equals(courseStatusEnum.value, value)) {
				return courseStatusEnum;
			}
		}
		return null;
	}
}
