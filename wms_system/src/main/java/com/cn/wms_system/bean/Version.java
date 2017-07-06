/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cn.wms_system.bean;

/**
 *
 * @author LFeng
 */
public class Version {
    private int varsionID;
    private String updateTime;
    private int versionNumber;
    private String versionNumberName;
    private String appPathUrl;

    public int getVarsionID() {
        return varsionID;
    }

    public void setVarsionID(int varsionID) {
        this.varsionID = varsionID;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getVersionNumberName() {
        return versionNumberName;
    }

    public void setVersionNumberName(String versionNumberName) {
        this.versionNumberName = versionNumberName;
    }

    public String getAppPathUrl() {
        return appPathUrl;
    }

    public void setAppPathUrl(String appPathUrl) {
        this.appPathUrl = appPathUrl;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(int versionNumber) {
        this.versionNumber = versionNumber;
    }
}
