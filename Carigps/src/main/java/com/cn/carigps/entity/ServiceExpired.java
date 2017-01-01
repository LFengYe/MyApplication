package com.cn.carigps.entity;

/**
 * Created by LFeng on 16/9/25.
 */
public class ServiceExpired {
    private String itemNo;
    private String VehNoF;
    private String SystemNo;
    private String VehId;
    private String RunoutTime;

    public String getItemNo() {
        return itemNo;
    }

    public void setItemNo(String itemNo) {
        this.itemNo = itemNo;
    }

    public String getRunoutTime() {
        return RunoutTime;
    }

    public void setRunoutTime(String runoutTime) {
        RunoutTime = runoutTime;
    }

    public String getSystemNo() {
        return SystemNo;
    }

    public void setSystemNo(String systemNo) {
        SystemNo = systemNo;
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
}
