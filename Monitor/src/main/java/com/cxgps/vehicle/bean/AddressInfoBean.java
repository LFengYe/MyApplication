package com.cxgps.vehicle.bean;

/**
 * Created by taosong on 16/7/22.
 */
public class AddressInfoBean extends BaseEntity {

    private String resCode;

    private String resultMessage;


    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
