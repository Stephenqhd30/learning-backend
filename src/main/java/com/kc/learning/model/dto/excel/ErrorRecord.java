package com.kc.learning.model.dto.excel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误记录
 * @author stephen qiu
 * @param <T> 导入数据的类型
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ErrorRecord<T> {
	/**
	 * 导入的数据
	 */
	private T data;
	
	/**
	 * 错误信息
	 */
	private String errorMsg;
}
