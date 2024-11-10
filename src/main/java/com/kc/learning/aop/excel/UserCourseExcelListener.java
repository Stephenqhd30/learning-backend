package com.kc.learning.aop.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.kc.learning.common.ErrorCode;
import com.kc.learning.constants.ExcelConstant;
import com.kc.learning.exception.BusinessException;
import com.kc.learning.model.dto.excel.ErrorRecord;
import com.kc.learning.model.dto.excel.SuccessRecord;
import com.kc.learning.model.entity.Course;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.entity.UserCourse;
import com.kc.learning.model.vo.userCourse.UserCourseExcelVO;
import com.kc.learning.service.CourseService;
import com.kc.learning.service.UserCourseService;
import com.kc.learning.service.UserService;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 导入用户 excel文件监听器
 *
 * @author: stephen qiu
 * @create: 2024-09-26 10:36
 **/
@Slf4j
public class UserCourseExcelListener extends AnalysisEventListener<UserCourseExcelVO> {
	
	private final UserCourseService userCourseService;
	
	private final UserService userService;
	
	private final CourseService courseService;
	
	private final HttpServletRequest request;
	
	/**
	 * 有个很重要的点 UserCourseInfoListener 不能被spring管理，
	 * 要每次读取excel都要new,然后里面用到spring可以构造方法传进去
	 *
	 * @param userCourseService userCourseService
	 * @param userService       userService
	 * @param request           request
	 */
	public UserCourseExcelListener(UserCourseService userCourseService, UserService userService, CourseService courseService, HttpServletRequest request) {
		this.userCourseService = userCourseService;
		this.userService = userService;
		this.courseService = courseService;
		this.request = request;
	}
	
	/**
	 * 缓存的数据
	 */
	private final List<UserCourse> cachedDataList = ListUtils.newArrayListWithExpectedSize(ExcelConstant.BATCH_COUNT);
	
	/**
	 * 用于记录异常信息
	 * -- GETTER --
	 * 返回异常信息给外部调用者
	 */
	@Getter
	private final List<ErrorRecord<UserCourse>> errorRecords = ListUtils.newArrayList();
	
	/**
	 * 用于记录正常导入信息
	 * -- GETTER --
	 * 返回异常信息给外部调用者
	 */
	@Getter
	private final List<SuccessRecord<UserCourse>> successRecords = ListUtils.newArrayList();
	
	
	/**
	 * @param exception exception
	 * @param context   context
	 */
	@Override
	public void onException(Exception exception, AnalysisContext context) throws Exception {
		log.error("解析异常: {}", exception.getMessage());
		throw exception;
	}
	
	/**
	 * 当读取到一行数据时，会调用这个方法，并将读取到的数据以及上下文信息作为参数传入
	 * 可以在这个方法中对读取到的数据进行处理和操作，处理数据时要注意异常错误，保证读取数据的稳定性
	 *
	 * @param userCourseExcelVO userCourseExcelVO
	 * @param context           context
	 */
	@Override
	public void invoke(UserCourseExcelVO userCourseExcelVO, AnalysisContext context) {
		UserCourse userCourse = new UserCourse();
		BeanUtils.copyProperties(userCourseExcelVO, userCourse);
		try {
			// 构建查询条件
			LambdaQueryWrapper<User> userLambdaQueryWrapper = Wrappers.lambdaQuery(User.class)
					.eq(User::getUserName, userCourseExcelVO.getUserName())
					.eq(User::getUserNumber, userCourseExcelVO.getUserNumber());
			LambdaQueryWrapper<Course> courseLambdaQueryWrapper = Wrappers.lambdaQuery(Course.class)
					.eq(Course::getCourseName, userCourseExcelVO.getCourseName())
					.eq(Course::getCourseNumber, userCourseExcelVO.getCourseNumber());
			User user = userService.getOne(userLambdaQueryWrapper);
			Course course = courseService.getOne(courseLambdaQueryWrapper);
			userCourse.setUserId(user.getId());
			userCourse.setCourseId(course.getId());
			
			// 检查用户是否已经存在
			LambdaQueryWrapper<UserCourse> userCourseLambdaQueryWrapper = Wrappers.lambdaQuery(UserCourse.class)
					.eq(UserCourse::getUserId, user.getId())
					.eq(UserCourse::getCourseId, course.getId());
			if (userCourseService.count(userCourseLambdaQueryWrapper) > 0) {
				throw new BusinessException(ErrorCode.EXCEL_ERROR, "用户课程已存在");
			}
			// 获取当前登录用户信息
			User loginUser = userService.getLoginUser(request);
			userCourse.setCreateUserId(loginUser.getId());
			cachedDataList.add(userCourse);
			successRecords.add(new SuccessRecord<>(userCourse, "成功导入"));
		} catch (Exception e) {
			log.error("处理数据时出现异常: {}", e.getMessage());
			errorRecords.add(new ErrorRecord<>(userCourse, e.getMessage()));
		}
		if (cachedDataList.size() >= ExcelConstant.BATCH_COUNT) {
			saveDataAsync();
			cachedDataList.clear();
		}
	}
	
	/**
	 * 数据解析完成后执行的收尾操作
	 *
	 * @param context 上下文信息
	 */
	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		// 处理剩余未保存的数据
		if (!cachedDataList.isEmpty()) {
			saveDataAsync();
			cachedDataList.clear();
		}
		log.info("所有数据解析完成，sheet名称={}！", context.readSheetHolder().getSheetName());
	}
	
	/**
	 * 执行批量保存数据操作
	 */
	private void saveDataAsync() {
		List<UserCourse> dataToSave = List.copyOf(cachedDataList);
		// 异步执行批量保存操作
		CompletableFuture.runAsync(() -> {
			log.info("开始批量保存{}条数据到数据库...", dataToSave.size());
			try {
				userCourseService.saveBatch(dataToSave);
				log.info("批量保存数据库成功！");
			} catch (Exception e) {
				log.error("批量保存数据库失败：{}", e.getMessage());
				errorRecords.add(new ErrorRecord<>(null, "批量保存失败：" + e.getMessage()));
			}
		});
	}
}
