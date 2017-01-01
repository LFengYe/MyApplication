package com.cn.wetrack.entity;

/**
 * 我的4s店
 */
public class My4SShop {
	/* 主键 */
	private Integer ID;
	/* 名称 */
	private String ShopName;
	/* 地址 */
	private String Address;
	/* 添加时间 */
	private String AddTime;

	public Integer getID() {
		return ID;
	}

	public void setID(Integer iD) {
		ID = iD;
	}

	public String getShopName() {
		return ShopName;
	}

	public void setShopName(String shopName) {
		ShopName = shopName;
	}

	public String getAddress() {
		return Address;
	}

	public void setAddress(String address) {
		Address = address;
	}

	public String getAddTime() {
		return AddTime;
	}

	public void setAddTime(String addTime) {
		AddTime = addTime;
	}

}
