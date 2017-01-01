package com.cn.wetrack.entity;

/**
 * 车辆
 */
public class Vehicle {
	/* 车辆编号 */
	private String VehId;
	/* 车牌号码 */
	private String VehNoF;
	/* sim卡号 */
	private String SimID;
	/* 车辆系统编号 */
	private String SystemNo;
	/* 车辆类型 */
	private String VehicleType;
	/* 车型 */
	private String Brand;
	/* 车辆颜色 */
	private String VehicleColor;
	/* 车辆备注 */
	private String Remark;
	/* 操作密码 */
	private String OperationPwd;
	/* 车主手机 */
	private String GdLinkTel;
	/* 是否过期 */
	private Boolean IsOverdue;
	/* 到期时间 */
	private String YyDate;
    /* 车辆当前状态: 1 行驶 2 停止 3 离线  5 不定位*/
	private int VehStatus;

	public int getCurStatus() {
		return VehStatus;
	}

	public void setCurStatus(int vehStatus) {
		VehStatus = vehStatus;
	}

	public int getVehStatus() {
		return VehStatus;
	}

	public void setVehStatus(int vehStatus) {
		VehStatus = vehStatus;
	}

	public String getYyDate() {
		return YyDate;
	}

	public void setYyDate(String yyDate) {
		YyDate = yyDate;
	}

	public String getVehId() {
		return VehId;
	}

	public void setVehId(String vehId) {
		VehId = vehId;
	}

	public String getVehNoF() {
		return VehNoF;
	}

	public void setVehNoF(String vehNoF) {
		VehNoF = vehNoF;
	}

	public String getSimID() {
		return SimID;
	}

	public void setSimID(String simID) {
		SimID = simID;
	}

	public String getSystemNo() {
		return SystemNo;
	}

	public void setSystemNo(String systemNo) {
		SystemNo = systemNo;
	}

	public String getVehicleType() {
		return VehicleType;
	}

	public void setVehicleType(String vehicleType) {
		VehicleType = vehicleType;
	}

	public String getBrand() {
		return Brand;
	}

	public void setBrand(String brand) {
		Brand = brand;
	}

	public String getVehicleColor() {
		return VehicleColor;
	}

	public void setVehicleColor(String vehicleColor) {
		VehicleColor = vehicleColor;
	}

	public String getRemark() {
		return Remark;
	}

	public void setRemark(String remark) {
		Remark = remark;
	}

	public String getOperationPwd() {
		return OperationPwd;
	}

	public void setOperationPwd(String operationPwd) {
		OperationPwd = operationPwd;
	}

	public String getGdLinkTel() {
		return GdLinkTel;
	}

	public void setGdLinkTel(String gdLinkTel) {
		GdLinkTel = gdLinkTel;
	}

	public Boolean getIsOverdue() {
		return IsOverdue;
	}

	public void setIsOverdue(Boolean isOverdue) {
		IsOverdue = isOverdue;
	}

}
