package com.kc.learning.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

/**
 * @author stephen qiu
 */
public class EncryptionUtils {
	// 固定密钥或盐值，长度必须为 16, 24 或 32 字符
	private static final String KEY = "1234567890abcdef";
	
	// AES 对象
	private static final AES aes;
	
	static {
		// 直接使用字符串作为密钥
		aes = SecureUtil.aes(KEY.getBytes());
	}
	
	/**
	 * 加密方法
	 *
	 * @param data 待加密的数据
	 * @return 加密后的十六进制字符串
	 */
	public static String encrypt(String data) {
		return aes.encryptHex(data);
	}
	
	/**
	 * 解密方法
	 *
	 * @param encryptedData 加密后的十六进制字符串
	 * @return 解密后的原始数据
	 */
	public static String decrypt(String encryptedData) {
		return aes.decryptStr(encryptedData);
	}
}
