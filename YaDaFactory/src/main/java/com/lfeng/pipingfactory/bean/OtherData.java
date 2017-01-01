package com.lfeng.pipingfactory.bean;

/**
 * Created by gpw on 2016/8/1.
 * --加油
 */
public class OtherData {


    /**
     * password : 密码
     * stationId : 工位ID
     * userId : 用户ID
     * username : 用户名
     * stationName : 工位名称
     * stationStructJson : 工位结构json字符串
     */

    private String password;
    private int stationId;
    private String userId;
    private String username;
    private String stationName;
    private String stationStructJson;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getStationStructJson() {
        return stationStructJson;
    }

    public void setStationStructJson(String stationStructJson) {
        this.stationStructJson = stationStructJson;
    }
}
