package com.cn.wetrack.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.cn.wetrack.activity.AlarmReport;
import com.cn.wetrack.activity.NoticeActivity;
import com.cn.wetrack.activity.SWApplication;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by fuyzh on 16/5/20.
 */
public class MyJPushReceiver extends BroadcastReceiver {
    private static final String TAG = "MyJPushReceiver";
    private Handler carOrderHandler;
    private SWApplication glob;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "onReceive - " + intent.getAction());

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            /**
             * SDK 向 JPush Server 注册所得到的注册 ID可以在这里获取到。一般来说，可不处理此广播信息。
             * 要深入地集成极光推送，开发者想要自己保存App用户与JPush 用户关系时，则接受此广播，取得 Registration ID 并保存与App uid 的关系到开发者自己的应用服务器上。
             */
            glob = (SWApplication) context.getApplicationContext();
            glob.sp.edit().putString("RegistrationID", JPushInterface.getRegistrationID(context)).commit();
            Log.i(TAG, JPushInterface.getRegistrationID(context));
            Log.i(TAG, bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID));

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            /**
             * 收到自定义消息。自定义消息不会展示在通知栏，完全要开发者写代码去处理
             */
            Log.i(TAG, "收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            //MyToast.makeText(context, "收到推送的消息, 消息内容是:" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            //progressMessageData(context, bundle.getString(JPushInterface.EXTRA_MESSAGE));

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            /**
             * 收到通知。在这里可以做些统计，或者做些其他工作
             */
            Log.i(TAG, "收到了通知,通知的标题为:" + bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));
            Log.i(TAG, "通知的内容为:" + bundle.getString(JPushInterface.EXTRA_ALERT));
            Log.i(TAG, "通知的附加内容为:" + bundle.getString(JPushInterface.EXTRA_EXTRA));

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            /**
             * 用户点击了通知。在这里可以自己写代码去定义用户点击后的行为
             */
            Log.i(TAG, "用户点击打开了通知");
            Log.i(TAG, "通知的标题为:" + bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE));
            Log.i(TAG, "通知的内容为:" + bundle.getString(JPushInterface.EXTRA_ALERT));
            Log.i(TAG, "通知的附加内容为:" + bundle.getString(JPushInterface.EXTRA_EXTRA));
            progressNoticeData(context, bundle.getString(JPushInterface.EXTRA_EXTRA));
        } else {
            // 不能处理的Action
        }
    }

    private void progressNoticeData(Context context, String jsonData) {
        try {
            JSONObject object = new JSONObject(jsonData);
            int type = object.getInt("pushType");
            Intent intent = new Intent();
            switch (type) {
                case 1:
                    intent.setClass(context, AlarmReport.class);
                    break;
                case 2:
                    intent.setClass(context, NoticeActivity.class);
                    break;
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
