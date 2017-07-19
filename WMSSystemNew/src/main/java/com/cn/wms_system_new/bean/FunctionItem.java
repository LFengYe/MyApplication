package com.cn.wms_system_new.bean;

public class FunctionItem {

	private String title;
	private String imageName;
	private String operateName;
	private int unFinishedNum;
	
	public FunctionItem() {
	}
	
	public FunctionItem(String title, String imageName, String operateName, int unFinishedNum) {
		this.title = title;
		this.imageName = imageName;
		this.operateName = operateName;
		this.unFinishedNum = unFinishedNum;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImageName() {
		return imageName;
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getOperateName() {
		return operateName;
	}

	public void setOperateName(String operateName) {
		this.operateName = operateName;
	}

	public int getUnFinishedNum() {
		return unFinishedNum;
	}

	public void setUnFinishedNum(int unFinishedNum) {
		this.unFinishedNum = unFinishedNum;
	}
}
