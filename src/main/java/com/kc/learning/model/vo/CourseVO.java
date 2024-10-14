package com.kc.learning.model.vo;

import cn.hutool.json.JSONUtil;
import com.kc.learning.model.entity.Course;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 课程视图
 *
 * @author stephen
 */
@Data
public class CourseVO implements Serializable {
	
	private static final long serialVersionUID = -155623328873414072L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 课程号
	 */
	private Integer courseNumber;
	
	/**
	 * 课程名称
	 */
	private String courseName;
	
	/**
	 * 创建用户id
	 */
	private Long userId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 创建用户视图类
	 */
	private UserVO userVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param courseVO courseVO
	 * @return Course
	 */
	public static Course voToObj(CourseVO courseVO) {
		if (courseVO == null) {
			return null;
		}
		Course course = new Course();
		BeanUtils.copyProperties(courseVO, course);
		return course;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param course course
	 * @return CourseVO
	 */
	public static CourseVO objToVo(Course course) {
		if (course == null) {
			return null;
		}
		CourseVO courseVO = new CourseVO();
		BeanUtils.copyProperties(course, courseVO);
		return courseVO;
	}
}
