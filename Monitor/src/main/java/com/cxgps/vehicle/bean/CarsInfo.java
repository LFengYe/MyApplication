package com.cxgps.vehicle.bean;

/**
 * Created by cxiosapp on 16/2/29.
 */
public class CarsInfo extends BaseEntity {


    private String carDesc,carNumber,carState,outDate,systemNo,carOutType,carOutDesc,accStatuse;


    public String getCarDesc() {
        return carDesc;
    }

    public void setCarDesc(String carDesc) {
        this.carDesc = carDesc;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public void setCarNumber(String carNumber) {
        this.carNumber = carNumber;
    }

    public String getCarState() {
        return carState;
    }

    public void setCarState(String carState) {
        this.carState = carState;
    }

    public String getOutDate() {
        return outDate;
    }

    public void setOutDate(String outDate) {
        this.outDate = outDate;
    }

    public String getSystemNo() {
        return systemNo;
    }

    public void setSystemNo(String systemNo) {
        this.systemNo = systemNo;
    }

    public String getCarOutDesc() {
        return carOutDesc;
    }

    public void setCarOutDesc(String carOutDesc) {
        this.carOutDesc = carOutDesc;
    }

    public String getCarOutType() {
        return carOutType;
    }

    public void setCarOutType(String carOutType) {
        this.carOutType = carOutType;
    }
}
