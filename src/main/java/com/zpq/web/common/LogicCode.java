package com.zpq.web.common;

/**
 * 逻辑异常
 * @author zpq
 *
 */
public interface LogicCode {

	/** 成功 */
	int success = 0;
	
	/** 未知异常 */
	int unknown = -255;
	
	/** 没有登陆 */
	int nologin = -254;
	
	/** 没有权限 */
	int no_authority = -253;
	
	/** 验证码错误 */
	int validate_code_error = -252;
	
	/** 用户或密码错误 */
	int password_error = -251;
	
	/** 超时 */
	int session_timeout = -250;
	
	/** 非法参数 */
	int argument_not_valid = -249;
	
	/** 账户已经停用 */
	int account_disabled = -248;
	
	/** 用户编号重复 */
	int user_no_duplicate = -1;
	
	/** 部门用户编号重复 */
	int dept_user_no_duplicate = -2;
	
}
