package com.cn.wms_system.service;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Administrator on 2016/4/29.
 */
public class MyThread extends Thread {
    private String TAG = "MyThread";

    public static final int SUCCESS = 1;
    public static final int UNKNOWN = 2;
    public static final int ENCODINGEXE = 3;
    public static final int PROTOCOLEXE = 4;
    public static final int IOEXE = 5;
    public static final int ISNULL = 6;
    public static final int UNAuthorization = 7;
    public static final int UNFORMAT = 8;

    private Handler handler;
    private JSONObject json;
    private String path;
    private String result;
    private Message msg;

    public MyThread(String path, Handler handler, JSONObject json) {
        this.path = path;
        this.handler = handler;
        this.json = json;
    }
    public MyThread(String path, Handler handler, JSONObject json, int i) {
        this.path = path;
        this.handler = handler;
        this.json = json;

    }

    @Override
    public void run() {

        super.run();
        try {
//            Log.i(TAG, "当前的请求是" + path);
            URL url = new URL(path);

            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            //因为这个是post请求,设立需要设置为true
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            // 设置以POST方式
            urlConn.setRequestMethod("POST");
            // Post 请求不能使用缓存

            urlConn.setUseCaches(false);
            urlConn.setInstanceFollowRedirects(true);
            urlConn.setRequestProperty("Content-Type", "application/json");
            urlConn.setRequestProperty("Accept", "application/json");
            // 连接，从postUrl.openConnection()至此的配置必须要在connect之前完成，
            // 要注意的是connection.getOutputStream会隐含的进行connect。

            urlConn.connect();
            //DataOutputStream流
            DataOutputStream out = new DataOutputStream(urlConn.getOutputStream());

            if (json != null) {
                //将要上传的内容写入流中
                out.write(json.toString().getBytes("UTF-8"));
                //刷新、关闭
                out.flush();
                out.close();
            }
            Log.i(TAG, "请求的数据为"+json.toString());


            int code = urlConn.getResponseCode();
            Log.i(TAG, "code=" + String.valueOf(code));
            if (code == 200) {
                //获取数据
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(urlConn.getInputStream(),"UTF-8"));

                result = reader.readLine();

                if (!TextUtils.isEmpty(result)) {
                    msg = new Message();
//                    Log.i(TAG, "正常响应返回的数据为：" + result);
                    msg.what = SUCCESS;
                    msg.obj = new JSONObject(result);
                    handler.sendMessage(msg);
                } else {
                    msg = new Message();
                    msg.what = ISNULL;
                    handler.sendMessage(msg);
                }
            }else if (code == 401) {
                msg = new Message();
                msg.what = UNAuthorization;
                handler.sendMessage(msg);
            }  else {
                msg = new Message();
//                msg.what = response.getStatusLine().getStatusCode();
                handler.sendMessage(msg);
            }
        } catch (UnsupportedEncodingException e) {
            msg = new Message();
            msg.what = ENCODINGEXE;
            handler.sendMessage(msg);
        } catch (IOException e) {
            msg = new Message();
            msg.what = IOEXE;
            handler.sendMessage(msg);
        } catch (JSONException e) {
            msg = new Message();
            msg.what = UNFORMAT;
            handler.sendMessage(msg);
        }
    }
}
