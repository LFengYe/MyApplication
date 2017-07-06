package com.cxgps.vehicle.bean;

/**
 * Created by taosong on 16/7/27.
 */
public class NavigationBean extends BaseEntity {

    private String titleName;

    private String titleDesc;



    private double  sulat,sulng;

    public String getTitleName() {
        return titleName;
    }

    public void setTitleName(String titleName) {
        this.titleName = titleName;
    }

    public String getTitleDesc() {
        return titleDesc;
    }

    public void setTitleDesc(String titleDesc) {
        this.titleDesc = titleDesc;
    }

    public double getSulat() {
        return sulat;
    }

    public void setSulat(double sulat) {
        this.sulat = sulat;
    }

    public double getSulng() {
        return sulng;
    }

    public void setSulng(double sulng) {
        this.sulng = sulng;
    }
}
