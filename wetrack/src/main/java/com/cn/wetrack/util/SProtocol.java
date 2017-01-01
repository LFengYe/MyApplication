package com.cn.wetrack.util;

/**
 * 常量定义
 */
public class SProtocol {
	/* 协议相关 */
	public static final int CONNECT_FAIL = -1;// 连接服务器失败
	public static final int SUCCESS = 0;// 请求成功
	public static final int FAIL = 1;// 请求失败
	
	/*消息*/
	public static String CONNECTION_FAIL;
	public static String OPERATE_FAIL;
	
	/**
	 * 获取错误消息
	 * @param code
	 * @param message
	 * @return
	 */
	public static String getFailMessage(int code, String message) {
		if (code == FAIL) {
			return message;
		} else if (code == CONNECT_FAIL) {
			return CONNECTION_FAIL;
		} else {
			return OPERATE_FAIL;
		}
	}

}
