package com.cn.carigps.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 告警
 */
public class Alarm implements Parcelable {
	/* 系统编号 */
	private String SystemNo;
	/* 时间 */
	private String Time;
	/* 经度 */
	private Double Longitude;
	/* 纬度 */
	private Double Latitude;
	/* 告警类型 */
	private String AlarmType;
	/*车牌号*/
	private String VehNoF;
	/*速度*/
	private String Velocity;
	/*ACC状态>0为开，等于0为关*/
	private String DtStatus;

	public String getDtStatus() {
		return DtStatus;
	}

	public void setDtStatus(String dtStatus) {
		DtStatus = dtStatus;
	}

	public String getVehNoF() {
		return VehNoF;
	}

	public void setVehNoF(String vehNoF) {
		VehNoF = vehNoF;
	}

	public String getVelocity() {
		return Velocity;
	}

	public void setVelocity(String velocity) {
		Velocity = velocity;
	}

	public Alarm() {
		super();
	}

	public Alarm(String systemNo, String time, Double longitude, Double latitude, String alarmType) {
		super();
		SystemNo = systemNo;
		Time = time;
		Longitude = longitude;
		Latitude = latitude;
		AlarmType = alarmType;
	}

	public String getSystemNo() {
		return SystemNo;
	}

	public void setSystemNo(String systemNo) {
		SystemNo = systemNo;
	}

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

	public String getAlarmType() {
		return AlarmType;
	}

	public void setAlarmType(String alarmType) {
		AlarmType = alarmType;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.SystemNo);
		dest.writeString(this.Time);
		dest.writeValue(this.Longitude);
		dest.writeValue(this.Latitude);
		dest.writeString(this.AlarmType);
		dest.writeString(this.VehNoF);
		dest.writeString(this.Velocity);
		dest.writeString(this.DtStatus);
	}

	protected Alarm(Parcel in) {
		this.SystemNo = in.readString();
		this.Time = in.readString();
		this.Longitude = (Double) in.readValue(Double.class.getClassLoader());
		this.Latitude = (Double) in.readValue(Double.class.getClassLoader());
		this.AlarmType = in.readString();
		this.VehNoF = in.readString();
		this.Velocity = in.readString();
		this.DtStatus = in.readString();
	}

	public static final Creator<Alarm> CREATOR = new Creator<Alarm>() {
		@Override
		public Alarm createFromParcel(Parcel source) {
			return new Alarm(source);
		}

		@Override
		public Alarm[] newArray(int size) {
			return new Alarm[size];
		}
	};
}
