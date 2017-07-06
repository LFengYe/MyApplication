package com.cn.wms_system.bean;

/**
 * 返回消息体
 * 
 */
public class SResponse {
	/* 代码 */
	private int status;
	/* 消息 */
	private String message;
	/* 结果 */
	private String data;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
