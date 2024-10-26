package com.kc.learning.aop.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.util.ListUtils;
import com.kc.learning.constants.ExcelConstant;
import com.kc.learning.constants.UserConstant;
import com.kc.learning.model.dto.excel.ErrorRecord;
import com.kc.learning.model.dto.excel.SuccessRecord;
import com.kc.learning.model.entity.User;
import com.kc.learning.model.enums.UserGenderEnum;
import com.kc.learning.service.UserService;
import com.kc.learning.utils.EncryptionUtils;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 导入用户 excel文件监听器
 * @author: stephen qiu
 * @create: 2024-09-26 10:36
 **/
@Slf4j
public class UserExcelListener extends AnalysisEventListener<User> {
	
	private final UserService userService;
	
	/**
	 * 有个很重要的点 UserInfoListener 不能被spring管理，
	 * 要每次读取excel都要new,然后里面用到spring可以构造方法传进去
	 *
	 * @param userService userService
	 */
	public UserExcelListener(UserService userService) {
		this.userService = userService;
	}
	
	/**
	 * 缓存的数据
	 */
	private final List<User> cachedDataList = ListUtils.newArrayListWithExpectedSize(ExcelConstant.BATCH_COUNT);
	
	/**
	 * 用于记录异常信息
	 * -- GETTER --
	 * 返回异常信息给外部调用者
	 */
	@Getter
	private final List<ErrorRecord<User>> errorRecords = ListUtils.newArrayList();
	
	/**
	 * 用于记录正常导入信息
	 * -- GETTER --
	 * 返回异常信息给外部调用者
	 */
	@Getter
	private final List<SuccessRecord<User>> successRecords = ListUtils.newArrayList();
	
	
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
	 * @param user    user
	 * @param context context
	 */
	@Override
	public void invoke(User user, AnalysisContext context) {
		User newUser = new User();
		BeanUtils.copyProperties(user, newUser);
		try {
			// 先检查用户传入参数是否合法
			userService.validUser(user, true);
			newUser.setUserGender(Optional.ofNullable(newUser.getUserGender())
					.orElse(UserGenderEnum.SECURITY.getValue()));
			newUser.setUserEmail(Optional.ofNullable(newUser.getUserEmail())
					.orElse("该用户很懒没有设置邮箱"));
			newUser.setUserPhone(Optional.ofNullable(newUser.getUserPhone())
					.orElse("该用户很懒没有设置电话"));
			newUser.setUserAvatar(UserConstant.USER_AVATAR);
			newUser.setUserIdCard(EncryptionUtils.encrypt(newUser.getUserIdCard()));
			newUser.setCreateTime(new Date());
			newUser.setUpdateTime(new Date());
			newUser.setIsDelete(0);
			newUser.setUserRole(UserConstant.DEFAULT_ROLE);
			cachedDataList.add(newUser);
			successRecords.add(new SuccessRecord<>(newUser, "成功导入"));
		} catch (Exception e) {
			// 捕获异常并记录
			log.error("处理数据时出现异常: {}", e.getMessage());
			// 将错误的记录信息存储到列表中
			errorRecords.add(new ErrorRecord<>(newUser, e.getMessage()));
		}
		if (cachedDataList.size() >= ExcelConstant.BATCH_COUNT) {
			saveData();
			cachedDataList.clear();
		}
	}
	
	/**
	 * 当每个sheet所有数据读取完毕后，会调用这个方法，可以在这个方法中进行一些收尾工作，如资源释放、数据汇总等。
	 *
	 * @param context context
	 */
	@Override
	public void doAfterAllAnalysed(AnalysisContext context) {
		// 收尾工作，处理剩下的缓存数据。。。
		if (!cachedDataList.isEmpty()) {
			saveData();
		}
		log.info("sheet={} 所有数据解析完成！", context.readSheetHolder().getSheetName());
	}
	
	/**
	 * 处理数据，如插入数据库
	 */
	private void saveData() {
		log.info("{} 条数据，开始存储数据库！", cachedDataList.size());
		// 批量插入数据库的逻辑 例如通过服务保存
		userService.saveBatch(cachedDataList);
		log.info("存储数据库成功！");
	}
}
