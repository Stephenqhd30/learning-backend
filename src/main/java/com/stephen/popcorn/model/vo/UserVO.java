package com.stephen.popcorn.model.vo;

import com.stephen.popcorn.model.entity.User;
import com.stephen.popcorn.utils.EncryptionUtil;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户视图（脱敏）
 *
 * @author stephen qiu
 */
@Data
public class UserVO implements Serializable {
	
	/**
	 * id
	 */
	private Long id;
	
	/**
	 * 用户昵称
	 */
	private String userName;
	
	/**
	 * 性别（0-男，1-女，2-保密）
	 */
	private Integer userGender;
	
	/**
	 * 用户身份证号
	 */
	private String userIdCard;
	
	
	/**
	 * 用户头像
	 */
	private String userAvatar;
	
	/**
	 * 用户简介
	 */
	private String userProfile;
	
	
	/**
	 * 用户角色：user/admin/ban
	 */
	private String userRole;
	
	/**
	 * 用户邮箱
	 */
	private String userEmail;
	
	/**
	 * 手机号码
	 */
	private String userPhone;
	
	/**
	 * 学号
	 */
	private String userNumber;
	
	/**
	 * 创建时间
	 */
	private Date createTime;
	
	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * 封装类转对象
	 *
	 * @param userVO
	 * @return
	 */
	public static User voToObj(UserVO userVO) {
		if (userVO == null) {
			return null;
		}
		// todo 需要进行转换
		User user = new User();
		BeanUtils.copyProperties(userVO, user);
		user.setUserIdCard(EncryptionUtil.encrypt(userVO.getUserIdCard()));
		return user;
	}
	
	/**
	 * 对象转封装类
	 *
	 * @param user
	 * @return
	 */
	public static UserVO objToVo(User user) {
		if (user == null) {
			return null;
		}
		// todo 需要进行转换
		UserVO userVO = new UserVO();
		BeanUtils.copyProperties(user, userVO);
		userVO.setUserIdCard(EncryptionUtil.decrypt(user.getUserIdCard()));
		return userVO;
	}
}