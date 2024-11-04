package com.kc.learning.model.vo.userCourse;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.io.Serializable;

/**
 * 用户课程视图
 *
 * @author stephen
 */
@Data
public class UserCourseExcelVO implements Serializable {
	
	private static final long serialVersionUID = -382141467091694865L;
	
	/**
	 * 姓名
	 */
	@ExcelProperty("姓名")
	@ColumnWidth(30)
	private String userName;
	
	/**
	 * 学号
	 */
	@ExcelProperty("学号")
	@ColumnWidth(30)
	private String userNumber;
	
	/**
	 * 课程名
	 */
	@ExcelProperty("课程名")
	@ColumnWidth(30)
	private String courseName;
	
	/**
	 * 课程号
	 */
	@ExcelProperty("课程号")
	@ColumnWidth(30)
	private String courseNumber;
}
