package com.lfeng.pipingfactory.bean;

/**
 * Created by gpw on 2016/8/1.
 * --加油
 */
public class LoginBean {
    /**
     * status : -1
     * message : 暂无需要执行的计划单!
     * data : null
     */

    private int status;
    private String message;
    private String data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getData() {

        return data;
    }

    public void setData(String data) {
        this.data = data;
    }


}
