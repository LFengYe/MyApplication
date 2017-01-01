package com.cn.carigps.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 位置
 */
public class Location implements Parcelable {
	private String SystemNo;
	private String VehNoF;
	private String SimID;
	private String Time;
	private Double Longitude;
	private Double Latitude;
//	private Double JPLongitude;
//	private Double JPLatitude;
	/* 速度 */
	private float Velocity;
	private int Angle;
	private String Locate;
	private int DtStatus;
	private float Oil;
	private String Miles;
	private String LevelNum;
	private String Temperature;
	private String alarmmsg;
	private String ServerTime;
	private int VehStatus;
	private float TodayMile;
	private String YyDate;
	private String IsOverdue;

	public String getIsOverdue() {
		return IsOverdue;
	}

	public void setIsOverdue(String isOverdue) {
		IsOverdue = isOverdue;
	}

	public float getTodayMile() {
		return TodayMile;
	}

	public void setTodayMile(float todayMile) {
		TodayMile = todayMile;
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

	public String getServerTime() {
		return ServerTime;
	}

	public void setServerTime(String serverTime) {
		ServerTime = serverTime;
	}

	public String getSystemNo() {
		return SystemNo;
	}

	public void setSystemNo(String systemNo) {
		this.SystemNo = systemNo;
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

	public float getVelocity() {
		return Velocity;
	}

	public void setVelocity(float velocity) {
		Velocity = velocity;
	}

	public int getAngle() {
		return Angle;
	}

	public void setAngle(int angle) {
		Angle = angle;
	}

	public String getLocate() {
		return Locate;
	}

	public void setLocate(String locate) {
		Locate = locate;
	}

	public int getDtStatus() {
		return DtStatus;
	}

	public void setDtStatus(int dtStatus) {
		DtStatus = dtStatus;
	}

	public float getOil() {
		return Oil;
	}

	public void setOil(float oil) {
		Oil = oil;
	}

	public String getMiles() {
		return Miles;
	}

	public void setMiles(String miles) {
		Miles = miles;
	}

	public String getLevelNum() {
		return LevelNum;
	}

	public void setLevelNum(String levelNum) {
		LevelNum = levelNum;
	}

	public String getTemperature() {
		return Temperature;
	}

	public void setTemperature(String temperature) {
		Temperature = temperature;
	}

	public String getAlarmmsg() {
		return alarmmsg;
	}

	public void setAlarmmsg(String alarmmsg) {
		this.alarmmsg = alarmmsg;
	}

	public String getSimID() {
		return SimID;
	}

	public void setSimID(String simID) {
		SimID = simID;
	}

	public String getVehNoF() {
		return VehNoF;
	}

	public void setVehNoF(String vehNoF) {
		VehNoF = vehNoF;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.SystemNo);
		dest.writeString(this.VehNoF);
		dest.writeString(this.SimID);
		dest.writeString(this.Time);
		dest.writeValue(this.Longitude);
		dest.writeValue(this.Latitude);
		dest.writeFloat(this.Velocity);
		dest.writeInt(this.Angle);
		dest.writeString(this.Locate);
		dest.writeInt(this.DtStatus);
		dest.writeFloat(this.Oil);
		dest.writeString(this.Miles);
		dest.writeString(this.LevelNum);
		dest.writeString(this.Temperature);
		dest.writeString(this.alarmmsg);
		dest.writeString(this.ServerTime);
		dest.writeInt(this.VehStatus);
		dest.writeFloat(this.TodayMile);
		dest.writeString(this.YyDate);
		dest.writeString(this.IsOverdue);
	}

	public Location() {
	}

	protected Location(Parcel in) {
		this.SystemNo = in.readString();
		this.VehNoF = in.readString();
		this.SimID = in.readString();
		this.Time = in.readString();
		this.Longitude = (Double) in.readValue(Double.class.getClassLoader());
		this.Latitude = (Double) in.readValue(Double.class.getClassLoader());
		this.Velocity = in.readFloat();
		this.Angle = in.readInt();
		this.Locate = in.readString();
		this.DtStatus = in.readInt();
		this.Oil = in.readFloat();
		this.Miles = in.readString();
		this.LevelNum = in.readString();
		this.Temperature = in.readString();
		this.alarmmsg = in.readString();
		this.ServerTime = in.readString();
		this.VehStatus = in.readInt();
		this.TodayMile = in.readFloat();
		this.YyDate = in.readString();
		this.IsOverdue = in.readString();
	}

	public static final Creator<Location> CREATOR = new Creator<Location>() {
		@Override
		public Location createFromParcel(Parcel source) {
			return new Location(source);
		}

		@Override
		public Location[] newArray(int size) {
			return new Location[size];
		}
	};
}
