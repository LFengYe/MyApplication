package com.cn.wms_system_new.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.cn.wms_system_new.R;
import com.cn.wms_system_new.bean.SResponse;

/**
 * Created by LFeng on 2017/5/4.
 */

public class MyHandler extends Handler {

    private Context context;

    public MyHandler(Context context) {
        this.context = context;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        SResponse response = (SResponse) msg.obj;
        switch (msg.what) {
            case MyThread.RESPONSE_SUCCESS: {
                break;
            }
            case MyThread.RESPONSE_FAILED: {
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_LONG).show();
                break;
            }
            case MyThread.RESPONSE_UNLOGIN: {
                Toast.makeText(context, R.string.response_unLogin, Toast.LENGTH_LONG).show();
                break;
            }
            case MyThread.CONNECT_FAILED: {
                Toast.makeText(context, R.string.response_error, Toast.LENGTH_LONG).show();
                break;
            }
            default:
                Toast.makeText(context, response.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
