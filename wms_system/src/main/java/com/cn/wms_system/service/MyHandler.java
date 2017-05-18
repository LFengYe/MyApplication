package com.cn.wms_system.service;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.cn.wms_system.R;

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
        switch (msg.what) {
            case MyThread.SUCCESS: {
                break;
            }
            case MyThread.ENCODINGEXE: {
                Toast.makeText(context, R.string.response_error, Toast.LENGTH_LONG).show();
                break;
            }
            case MyThread.ISNULL: {
                Toast.makeText(context, R.string.response_empty, Toast.LENGTH_LONG).show();
                break;
            }
            case MyThread.IOEXE: {
                Toast.makeText(context, R.string.response_error, Toast.LENGTH_LONG).show();
                break;
            }
            case MyThread.PROTOCOLEXE: {
                Toast.makeText(context, R.string.response_error, Toast.LENGTH_LONG).show();
                break;
            }
            case MyThread.UNAuthorization: {
                Toast.makeText(context, R.string.response_unLogin, Toast.LENGTH_LONG).show();
                break;
            }
            case MyThread.UNKNOWN: {
                Toast.makeText(context, R.string.response_error, Toast.LENGTH_LONG).show();
                break;
            }
            case MyThread.UNFORMAT: {
                Toast.makeText(context, R.string.response_unformat, Toast.LENGTH_LONG).show();
                break;
            }
        }
    }
}
