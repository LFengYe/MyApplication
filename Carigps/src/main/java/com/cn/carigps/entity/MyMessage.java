package com.cn.carigps.entity;

/**
 * 我的消息
 */
public class MyMessage {
	/* 时间 */
	private String time;
	/* 内容 */
	private String content;

	public MyMessage() {
		super();
	}

	public MyMessage(String time, String content) {
		super();
		this.time = time;
		this.content = content;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
