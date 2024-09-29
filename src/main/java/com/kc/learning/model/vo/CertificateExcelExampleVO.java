package com.kc.learning.model.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
public class CertificateExcelExampleVO implements Serializable {
	private static final long serialVersionUID = -5741413222214936521L;
	
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
	@ColumnWidth(40)
	private String certificateName;
	
	/**
	 * 证书类型(0-干部培训,1-其他)
	 */
	@ExcelProperty(value = "证书类型(0-干部培训,1-其他)")
	@ColumnWidth(30)
	private String certificateType;
	
	/**
	 * 证书获得时间
	 */
	@ExcelProperty(value = "证书获得时间")
	@ColumnWidth(30)
	private String certificateYear;
	
	/**
	 * 证书获得情况(0-有,1-没有)
	 */
	@ExcelProperty(value = "证书获得情况(0-有,1-没有)")
	@ColumnWidth(30)
	private String certificateSituation;
	
	/**
	 * 获得人id
	 */
	@ExcelProperty(value = "获得人id")
	@ColumnWidth(30)
	private String gainUserId;
	
	/**
	 * 证书地址下载地址
	 */
	@ExcelProperty(value = "证书地址下载地址")
	@ColumnWidth(40)
	private String certificateUrl;
	
}
