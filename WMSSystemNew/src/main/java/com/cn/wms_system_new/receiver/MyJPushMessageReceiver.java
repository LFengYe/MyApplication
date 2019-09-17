package com.cn.wms_system_new.receiver;


import android.content.Context;
import android.util.Log;

import com.cn.wms_system_new.component.Constants;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * Created by LFeng on 2017/7/19.
 */

public class MyJPushMessageReceiver extends JPushMessageReceiver {
    private static final String TAG = "MyJPushMessageReceiver";

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
        String logs;
        switch (jPushMessage.getErrorCode()) {
            case 0: {
                logs = "Set alias " + jPushMessage.getAlias() + " success";
                Log.i(TAG, logs);
                break;
            }
            case 6002: {
                logs = "Failed to set alias due to timeout. Try again after 60s.";
                Log.i(TAG, logs);
                if (jPushMessage.getSequence() == Constants.JPUSH_ALIAS_SEQUENCE)
                    JPushInterface.setAlias(context, Constants.JPUSH_ALIAS_SEQUENCE, jPushMessage.getAlias());
                break;
            }
            default:
                logs = "Failed with errorCode = " + jPushMessage.getErrorCode();
                Log.e(TAG, logs);
                break;
        }
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onCheckTagOperatorResult(context, jPushMessage);
    }
}
