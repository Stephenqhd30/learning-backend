package com.kc.learning.model.vo.course;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;

/**
 * 课程视图
 *
 * @author stephen
 */
@Data
public class CourseExcelVO implements Serializable {
	
	private static final long serialVersionUID = 1106240053422155491L;
	/**
	 * id
	 */
	@ExcelProperty(value = "id")
	@ColumnWidth(30)
	private String id;
	
	/**
	 * 课程号
	 */
	@ExcelProperty(value = "课程号")
	@ColumnWidth(20)
	private Integer courseNumber;
	
	/**
	 * 课程名称
	 */
	@ExcelProperty(value = "课程名称")
	@ColumnWidth(30)
	private String courseName;
	
	/**
	 * 创建用户id
	 */
	@ExcelProperty(value = "创建用户id")
	@ColumnWidth(30)
	private String userId;
	
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
	@ColumnWidth(30)
	private String updateTime;
}
