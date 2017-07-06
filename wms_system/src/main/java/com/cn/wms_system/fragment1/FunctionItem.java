package com.cn.wms_system.fragment1;

public class FunctionItem {

	private String name;
	private int imageId;
	private int unFinishedNum;
	
	public FunctionItem() {
		super();
	}
	
	public FunctionItem(String itemName, int itemImageId, int updateNum) {
		super();
		this.setName(itemName);
		this.setImageId(itemImageId);
		this.setUnFinishedNum(updateNum);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the imageId
	 */
	public int getImageId() {
		return imageId;
	}

	/**
	 * @param imageId the imageId to set
	 */
	public void setImageId(int imageId) {
		this.imageId = imageId;
	}

	/**
	 * @return the updateNum
	 */
	public int getUnFinishedNum() {
		return unFinishedNum;
	}

	/**
	 * @param updateNum the updateNum to set
	 */
	public void setUnFinishedNum(int updateNum) {
		this.unFinishedNum = updateNum;
	}
}
