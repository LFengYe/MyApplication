package com.cn.carigps.entity;

/**
 * Created by fuyzh on 16/8/27.
 */
public class MapPoint {
    private String MpName;
    private double Longitude;
    private double Latitude;
    private double JPLng;
    private double JPLat;

    public String getMpName() {
        return MpName;
    }

    public void setMpName(String mpName) {
        MpName = mpName;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getJPLng() {
        return JPLng;
    }

    public void setJPLng(double JPLng) {
        this.JPLng = JPLng;
    }

    public double getJPLat() {
        return JPLat;
    }

    public void setJPLat(double JPLat) {
        this.JPLat = JPLat;
    }
}
