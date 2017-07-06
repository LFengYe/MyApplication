package com.cxgps.vehicle.bean;

/**
 * Created by taosong on 16/7/21.
 */
public class ShowUrlBean extends BaseEntity {

    private String resCode;

    private String showUrl;

    private String title;

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getShowUrl() {
        return showUrl;
    }

    public void setShowUrl(String showUrl) {
        this.showUrl = showUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
