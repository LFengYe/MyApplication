package com.cn.carigps.entity;

public class Geofence {
	//半径
	private int Radius;
	//经度
	private double Longitude;
	//纬度
	private double Latitude;
	//是否开启
	private boolean IsOpen;
	public Geofence(){
		
	}
	public Geofence(int radius,double longitude,double latitude,boolean isopen){
		this.Radius=radius;
		this.Longitude=longitude;
		this.Latitude=latitude;
		this.IsOpen=isopen;
	}
	public int getRadius() {
		return Radius;
	}
	public void setRadius(int radius) {
		this.Radius = radius;
	}
	public double getLongitude() {
		return Longitude;
	}
	public void setLongitude(double longitude) {
		this.Longitude = longitude;
	}
	public double getLatitude() {
		return Latitude;
	}
	public void setLatitude(double latitude) {
		this.Latitude = latitude;
	}
	public boolean isIsOpen() {
		return IsOpen;
	}
	public void setIsOpen(boolean isOpen) {
		this.IsOpen = isOpen;
	}

	
}
