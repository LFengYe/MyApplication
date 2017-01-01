package com.cn.wetrack.entity;

/**
 * 返回消息体
 * 
 */
public class SResponse {
	/* 代码 */
	private int code=-1;
	/* 消息 */
	private String message=null;
	/* 结果 */
	private Object result=null;

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}
