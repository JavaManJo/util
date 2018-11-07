package com.zpq.web.exception;


import com.zpq.web.common.LogicCode;

/**
 * 业务逻辑异常
 * @author zpq
 *
 */
public class LogicException extends RuntimeException {

	private static final long serialVersionUID = -3651828036599228205L;
	
	private final int code;
	
	public static LogicException valueOf(int code, String message) {
		return new LogicException(code, message);
	}
	
	public static LogicException valueOfUnknow(String message) {
		return new LogicException(LogicCode.unknown, message);
	}
	
	public LogicException(int code, String message) {
		super(message);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
}
