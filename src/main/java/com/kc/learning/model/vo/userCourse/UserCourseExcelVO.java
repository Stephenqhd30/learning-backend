package com.kc.learning.model.vo.userCourse;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
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
	 * id
	 */
	@ExcelProperty("id")
	@ColumnWidth(30)
	private String id;
	
	/**
	 * 用户id
	 */
	@ExcelProperty("用户id")
	@ColumnWidth(30)
	private String userId;
	
	/**
	 * 课程id
	 */
	@ExcelProperty("课程id")
	@ColumnWidth(30)
	private String courseId;
	
	/**
	 * 创建时间
	 */
	@ExcelProperty("创建时间")
	@DateTimeFormat("yyyy年MM月dd日")
	@ColumnWidth(30)
	private String createTime;
}
