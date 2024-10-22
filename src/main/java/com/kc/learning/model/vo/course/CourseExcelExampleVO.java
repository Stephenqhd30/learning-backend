package com.kc.learning.model.vo.course;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 课程视图
 *
 * @author stephen
 */
@Data
public class CourseExcelExampleVO implements Serializable {
	
	private static final long serialVersionUID = 1106240053422155491L;
	
	/**
	 * 课程号
	 */
	@ExcelProperty(value = "课程号")
	@ColumnWidth(30)
	private String courseNumber;
	
	/**
	 * 课程名称
	 */
	@ExcelProperty(value = "课程名称")
	@ColumnWidth(30)
	private String courseName;
	
	/**
	 * 开课时间
	 */
	@ExcelProperty(value = "开课时间")
	@ColumnWidth(40)
	@DateTimeFormat("yyyy年MM月dd日")
	private String acquisitionTime;
	
	/**
	 * 结课时间
	 */
	@ExcelProperty(value = "结课时间")
	@ColumnWidth(40)
	@DateTimeFormat("yyyy年MM月dd日")
	private String finishTime;
}
