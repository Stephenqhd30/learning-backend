package com.kc.learning.model.vo.course;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
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
	 * 课程号
	 */
	@ExcelProperty(value = "课程号")
	@ColumnWidth(20)
	private Integer courseNumber;
	
	/**
	 * 课程名称
	 */
	@ExcelProperty(value = "课程名称")
	@ColumnWidth(40)
	private String courseName;
	
	/**
	 * 创建用户
	 */
	@ExcelProperty(value = "创建用户")
	@ColumnWidth(40)
	private String userName;
	
	/**
	 * 开课时间
	 */
	@ExcelProperty(value = "开课时间")
	@DateTimeFormat("yyyy年MM月dd日")
	@ColumnWidth(50)
	private String startTime;
	
	/**
	 * 结课时间
	 */
	@ExcelProperty(value = "结课时间")
	@DateTimeFormat("yyyy年MM月dd日")
	@ColumnWidth(50)
	private String endTime;
	
	/**
	 * 课程状态(0-未开始, 1-进行中, 2-已结束)
	 */
	@ExcelProperty(value = "课程状态")
	@ColumnWidth(30)
	private String status;
}
