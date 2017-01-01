package com.cn.carigps.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**油量查询结果*/
public class MileageOilReport implements Parcelable {
	private String SystemNo;
	private String VehNoF;
	private String UseOilValue;
	private String AddOilValue;
	private String LouOilValue;
	private String AddOilTimes;
	private String ArgSpeed;
	private float TotalMiles;
	private String ArgOile;
	private String AddOilTime;
	private String AddOilAddress;

	public String getAddOilAddress() {
		return AddOilAddress;
	}

	public void setAddOilAddress(String addOilAddress) {
		AddOilAddress = addOilAddress;
	}

	public String getAddOilTime() {
		return AddOilTime;
	}

	public void setAddOilTime(String addOilTime) {
		AddOilTime = addOilTime;
	}

	public String getAddOilTimes() {
		return AddOilTimes;
	}

	public void setAddOilTimes(String addOilTimes) {
		AddOilTimes = addOilTimes;
	}

	public String getAddOilValue() {
		return AddOilValue;
	}

	public void setAddOilValue(String addOilValue) {
		AddOilValue = addOilValue;
	}

	public String getArgOile() {
		return ArgOile;
	}

	public void setArgOile(String argOile) {
		ArgOile = argOile;
	}

	public String getArgSpeed() {
		return ArgSpeed;
	}

	public void setArgSpeed(String argSpeed) {
		ArgSpeed = argSpeed;
	}

	public String getLouOilValue() {
		return LouOilValue;
	}

	public void setLouOilValue(String louOilValue) {
		LouOilValue = louOilValue;
	}

	public String getSystemNo() {
		return SystemNo;
	}

	public void setSystemNo(String systemNo) {
		SystemNo = systemNo;
	}

	public float getTotalMiles() {
		return TotalMiles;
	}

	public void setTotalMiles(float totalMiles) {
		TotalMiles = totalMiles;
	}

	public String getUseOilValue() {
		return UseOilValue;
	}

	public void setUseOilValue(String useOilValue) {
		UseOilValue = useOilValue;
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
		dest.writeString(this.UseOilValue);
		dest.writeString(this.AddOilValue);
		dest.writeString(this.LouOilValue);
		dest.writeString(this.AddOilTimes);
		dest.writeString(this.ArgSpeed);
		dest.writeFloat(this.TotalMiles);
		dest.writeString(this.ArgOile);
		dest.writeString(this.AddOilTime);
		dest.writeString(this.AddOilAddress);
	}

	public MileageOilReport() {
	}

	protected MileageOilReport(Parcel in) {
		this.SystemNo = in.readString();
		this.VehNoF = in.readString();
		this.UseOilValue = in.readString();
		this.AddOilValue = in.readString();
		this.LouOilValue = in.readString();
		this.AddOilTimes = in.readString();
		this.ArgSpeed = in.readString();
		this.TotalMiles = in.readFloat();
		this.ArgOile = in.readString();
		this.AddOilTime = in.readString();
		this.AddOilAddress = in.readString();
	}

	public static final Creator<MileageOilReport> CREATOR = new Creator<MileageOilReport>() {
		@Override
		public MileageOilReport createFromParcel(Parcel source) {
			return new MileageOilReport(source);
		}

		@Override
		public MileageOilReport[] newArray(int size) {
			return new MileageOilReport[size];
		}
	};
}
