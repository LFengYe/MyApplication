package com.cn.wms_system_new.service;

import android.os.Message;

import com.alibaba.fastjson.JSONObject;
import com.cn.wms_system_new.bean.SResponse;


/**
 * Created by Administrator on 2016/4/29.
 */
public class MyThread extends Thread {
    public static final int RESPONSE_UNFINISH = 1;
    public static final int RESPONSE_SUCCESS = 0;
    public static final int RESPONSE_FAILED = -1;
    public static final int RESPONSE_UNLOGIN = -99;
    public static final int CONNECT_FAILED = -101;

    public static final int IN = 1;
    public static final int MOVE = 2;
    public static final int OUT = 3;
    public static final int REPORT = 4;
    public static final int APP = 5;

    private MyHandler handler;
    private JSONObject json;
    private int type;
    private String action;

    /*public MyThread(MyHandler handler, JSONObject json, int type) {
        this.handler = handler;
        this.json = json;
        this.type = type;
    }*/

    public MyThread(MyHandler handler, JSONObject json, String action) {
        this.handler = handler;
        this.json = json;
        this.action = action;
    }

    @Override
    public void run() {
        super.run();
        SResponse response = HttpRequestClient.getData(json, action);
        if (response != null) {
            Message message = new Message();
            message.what = response.getStatus();
            message.obj = response;
            handler.sendMessage(message);
        } else {
            handler.sendEmptyMessage(CONNECT_FAILED);
        }
    }
}
