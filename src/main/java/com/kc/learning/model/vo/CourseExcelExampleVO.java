package com.kc.learning.model.vo;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;

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
}
