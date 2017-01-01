package com.cn.wetrack.entity;

/**
 * 服务器
 */
public class Host {
	/* 服务器名称 */
	private String name;
	private String enname;
	/* 服务器IP*/
	private String ip;
	private String port;

	public String getEnname() {
		return enname;
	}

	public void setEnname(String enname) {
		this.enname = enname;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPort() {
		return port;
	}

	public void setPort(String port) {
		this.port = port;
	}
}
