package com.cn.carigps.entity;

/**
 * 历史数据
 */
public class History {
	/* 时间 */
	private String Time;
	/* 经度 */
	private Double Longitude;
	/* 纬度 */
	private Double Latitude;
	/* 校准后的纬度 */
	private String Angle;
	/* 停车时长 */
	private String DwellTime;

	public float getVelocity() {
		return Velocity;
	}

	public void setVelocity(float velocity) {
		Velocity = velocity;
	}

	public float getMiles() {
		return Miles;
	}

	public void setMiles(float miles) {
		Miles = miles;
	}

	private float Velocity;
	private float Miles;

	public String getTime() {
		return Time;
	}

	public void setTime(String time) {
		Time = time;
	}

	public Double getLongitude() {
		return Longitude;
	}

	public void setLongitude(Double longitude) {
		Longitude = longitude;
	}

	public Double getLatitude() {
		return Latitude;
	}

	public void setLatitude(Double latitude) {
		Latitude = latitude;
	}

	public String getAngle() {
		return Angle;
	}

	public void setAngle(String angle) {
		Angle = angle;
	}

	public String getDwellTime() {
		return DwellTime;
	}

	public void setDwellTime(String dwellTime) {
		DwellTime = dwellTime;
	}

}
