package com.kc.learning.model.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 证书 Excel 数据模型
 *
 * @author: stephen qiu
 * @create: 2024-09-27 09:22
 **/
@Data
public class UserCertificateExcelVO implements Serializable {
	private static final long serialVersionUID = -5741413222214936521L;
	
	/**
	 * id
	 */
	@ExcelProperty(value = "id")
	@ColumnWidth(30)
	private String id;
	
	/**
	 * 用户id
	 */
	@ExcelProperty(value = "用户id")
	@ColumnWidth(30)
	private String userId;
	
	/**
	 * 用户id
	 */
	@ExcelProperty(value = "证书id")
	@ColumnWidth(30)
	private String certificateId;
	
	/**
	 * 证书编号
	 */
	@ExcelProperty(value = "证书编号")
	@ColumnWidth(40)
	private String certificateNumber;
	
	/**
	 * 证书名称
	 */
	@ExcelProperty(value = "证书名称")
	@ColumnWidth(30)
	private String certificateName;
	
	/**
	 * 获得时间
	 */
	@ExcelProperty(value = "获得时间")
	@ColumnWidth(20)
	private String gainTime;
	
	/**
	 * 获得人名称
	 */
	@ExcelProperty(value = "获得人名称")
	@ColumnWidth(20)
	private String gainUserName;
	
	/**
	 * 创建时间
	 */
	@ExcelProperty(value = "创建时间")
	@ColumnWidth(20)
	private String createTime;
	
	/**
	 * 更新时间
	 */
	@ExcelProperty(value = "更新时间")
	@ColumnWidth(20)
	private String updateTime;
}
