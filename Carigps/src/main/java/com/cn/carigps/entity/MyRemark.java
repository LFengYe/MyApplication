package com.cn.carigps.entity;

/**
 * 我的备忘
 */
public class MyRemark {
	/* 主键 */
	private Integer ID;
	/* 添加时间 */
	private String AddTime;
	/* 备注 */
	private String Remark;

	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public String getAddTime() {
		return AddTime;
	}

	public void setAddTime(String addTime) {
		AddTime = addTime;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

}
