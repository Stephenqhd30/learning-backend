package com.kc.learning.utils;

import com.kc.learning.common.ErrorCode;

/**
 * @author stephen qiu
 */
public class MaskUtils {
	
	/**
	 * 对手机号码进行脱敏处理
	 *
	 * @param phoneNumber 原始手机号码
	 * @return 脱敏后的手机号码
	 */
	public static String maskPhoneNumber(String phoneNumber) {
		ThrowUtils.throwIf(phoneNumber == null || RegexUtils.checkPhone(phoneNumber), ErrorCode.PARAMS_ERROR, "手机号有误，请检查您的手机号！");
		return phoneNumber.substring(0, 3) + "****" + phoneNumber.substring(7);
	}
}