package com.cxgps.vehicle.bean;

/**
 * Created by taosong on 16/5/9.
 */
public class UserInfo extends BaseEntity {


    private String uLoginFlag;

    private int uLoginType;

    private String userName;

    private String uAccount;

    private String uPassword;

    private String uIsAuto,uIsRember;

    private String uLogintime;

    private String userId;



    public int getuLoginType() {
        return uLoginType;
    }

    public void setuLoginType(int uLoginType) {
        this.uLoginType = uLoginType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getuAccount() {
        return uAccount;
    }

    public void setuAccount(String uAccount) {
        this.uAccount = uAccount;
    }

    public String getuPassword() {
        return uPassword;
    }

    public void setuPassword(String uPassword) {
        this.uPassword = uPassword;
    }

    public String getuIsAuto() {
        return uIsAuto;
    }

    public void setuIsAuto(String uIsAuto) {
        this.uIsAuto = uIsAuto;
    }

    public String getuIsRember() {
        return uIsRember;
    }

    public void setuIsRember(String uIsRember) {
        this.uIsRember = uIsRember;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getuLogintime() {
        return uLogintime;
    }

    public void setuLogintime(String uLogintime) {
        this.uLogintime = uLogintime;
    }

    public String getuLoginFlag() {
        return uLoginFlag;
    }

    public void setuLoginFlag(String uLoginFlag) {
        this.uLoginFlag = uLoginFlag;
    }
}
