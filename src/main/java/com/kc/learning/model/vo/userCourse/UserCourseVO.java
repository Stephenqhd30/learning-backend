package com.kc.learning.model.vo.userCourse;

import com.kc.learning.model.entity.UserCourse;
import com.kc.learning.model.vo.course.CourseVO;
import com.kc.learning.model.vo.user.UserVO;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户课程视图
 *
 * @author stephen
 */
@Data
public class UserCourseVO implements Serializable {
	
	private static final long serialVersionUID = -382141467091694865L;
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 用户id
	 */
	private Long userId;
	
	/**
	 * 课程id
	 */
	private Long courseId;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**
	 * 用户信息
	 */
	private UserVO userVO;
	
	/**
	 *
	 */
	private CourseVO courseVO;
	
	/**
	 * 封装类转对象
	 *
	 * @param userCourseVO userCourseVO
	 * @return UserCourse
	 */
	public static UserCourse voToObj(UserCourseVO userCourseVO) {
		if (userCourseVO == null) {
			return null;
		}
		UserCourse userCourse = new UserCourse();
		BeanUtils.copyProperties(userCourseVO, userCourse);
		return userCourse;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param userCourse userCourse
	 * @return UserCourseVO
	 */
	public static UserCourseVO objToVo(UserCourse userCourse) {
		if (userCourse == null) {
			return null;
		}
		UserCourseVO userCourseVO = new UserCourseVO();
		BeanUtils.copyProperties(userCourse, userCourseVO);
		return userCourseVO;
	}
}
