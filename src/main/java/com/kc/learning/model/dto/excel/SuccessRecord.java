package com.kc.learning.model.dto.excel;

import com.kc.learning.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 成功记录
 * @author stephen qiu
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessRecord {
	/**
	 * 导入的用户数据
	 */
	private User user;
	
	/**
	 * 导入成功信息
	 */
	private String message;
}
