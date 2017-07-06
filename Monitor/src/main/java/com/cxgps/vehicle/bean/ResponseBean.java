package com.cxgps.vehicle.bean;

/**
 * Created by taosong on 16/7/21.
 */
public class ResponseBean extends BaseEntity {

    private int CarCount,OnLine,OutLine;

    private String data,pageCounts;

    private boolean requestFlag;



    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isRequestFlag() {
        return requestFlag;
    }

    public void setRequestFlag(boolean requestFlag) {
        this.requestFlag = requestFlag;
    }

    public int getCarCount() {
        return CarCount;
    }

    public void setCarCount(int carCount) {
        CarCount = carCount;
    }

    public int getOnLine() {
        return OnLine;
    }

    public void setOnLine(int onLine) {
        OnLine = onLine;
    }

    public int getOutLine() {
        return OutLine;
    }

    public void setOutLine(int outLine) {
        OutLine = outLine;
    }

    public String getPageCounts() {
        return pageCounts;
    }

    public void setPageCounts(String pageCounts) {
        this.pageCounts = pageCounts;
    }
}
