package com.cxgps.vehicle.bean;

/**
 * Created by taosong on 16/7/29.
 */
public class NoticeInfoBean extends BaseEntity {

   private String SystemNo,VehNoF,Velocity,DtStatus,Time,Longitude,Latitude,ysLongitude,ysLatitude,AlarmType;


    public String getSystemNo() {
        return SystemNo;
    }

    public void setSystemNo(String systemNo) {
        SystemNo = systemNo;
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

    public String getDtStatus() {
        return DtStatus;
    }

    public void setDtStatus(String dtStatus) {
        DtStatus = dtStatus;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }

    public String getYsLongitude() {
        return ysLongitude;
    }

    public void setYsLongitude(String ysLongitude) {
        this.ysLongitude = ysLongitude;
    }

    public String getYsLatitude() {
        return ysLatitude;
    }

    public void setYsLatitude(String ysLatitude) {
        this.ysLatitude = ysLatitude;
    }

    public String getAlarmType() {
        return AlarmType;
    }

    public void setAlarmType(String alarmType) {
        AlarmType = alarmType;
    }
}
