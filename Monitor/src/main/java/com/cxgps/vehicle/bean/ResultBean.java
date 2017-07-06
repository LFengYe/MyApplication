package com.cxgps.vehicle.bean;

/**
 * Created by taosong on 16/7/21.
 */
public class ResultBean extends BaseEntity {

    private String resultMsg;

    private int resultState;

    private int uLoginType;

    private  boolean uLoginFlag;

    private String userName;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getResultState() {
        return resultState;
    }

    public void setResultState(int resultState) {
        this.resultState = resultState;
    }


    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public int getuLoginType() {
        return uLoginType;
    }

    public void setuLoginType(int uLoginType) {
        this.uLoginType = uLoginType;
    }

    public boolean isuLoginFlag() {
        return uLoginFlag;
    }

    public void setuLoginFlag(boolean uLoginFlag) {
        this.uLoginFlag = uLoginFlag;
    }
}
