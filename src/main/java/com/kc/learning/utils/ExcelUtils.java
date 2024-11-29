package com.kc.learning.utils;

import com.kc.learning.common.ErrorCode;
import com.kc.learning.common.exception.BusinessException;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Excel工具类
 *
 * @author stephen qiu
 */
public class ExcelUtils {
	
	/**
	 * 获取路径
	 *
	 * @return 当前路径
	 */
	public static String getPath() {
		return ExcelUtils.class.getResource("/").getPath();
	}
	
	/**
	 * 创建新文件
	 *
	 * @param pathName 文件名
	 * @return 文件
	 */
	public static File createNewFile(String pathName) {
		File file = new File(getPath() + pathName);
		if (file.exists()) {
			file.delete();
		} else {
			if (!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
		}
		return file;
	}
	
	/**
	 * 设置响应结果
	 *
	 * @param response    响应结果对象
	 * @param rawFileName 文件名
	 */
	public static void setExcelResponseProp(HttpServletResponse response, String rawFileName) throws IOException {
		// 设置内容类型
		response.setContentType("application/vnd.vnd.ms-excel");
		// 设置编码格式
		response.setCharacterEncoding("utf-8");
		// 设置导出文件名称（避免乱码）
		String fileName = URLEncoder.encode(rawFileName.concat(".xlsx"), "UTF-8");
		// 设置响应头
		response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName);
	}
	
	/**
	 * Date转String
	 *
	 * @param date 日期
	 * @return 字符串
	 */
	public static String dateToString(Date date) {
		if (date == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
		}
		// Date转换为String
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	/**
	 * Date转 yyyy年MM月dd日
	 *
	 * @param date 日期
	 * @return 字符串
	 */
	public static String dateToExcelString(Date date) {
		if (date == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
		}
		// Date转换为String
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日");
		return sdf.format(date);
	}
	
}
