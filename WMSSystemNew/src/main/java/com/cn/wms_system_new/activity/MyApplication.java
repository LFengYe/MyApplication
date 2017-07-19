package com.cn.wms_system_new.activity;

import android.app.Application;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by LFeng on 2017/7/19.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        JPushInterface.requestPermission(this);
    }
}
