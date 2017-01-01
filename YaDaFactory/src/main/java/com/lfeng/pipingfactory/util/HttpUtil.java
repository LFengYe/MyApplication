package com.lfeng.pipingfactory.util;

import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import java.io.InputStream;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;

import java.net.URL;

/**
 * Created by LFeng on 2016/8/1.
 */
public class HttpUtil {
    public static String defaultHost = "http://192.168.1.179:8080/";
    public static String loginURL = "PipingFactory/AppInterface/login.do";
    public static String inputOrderURL = "PipingFactory/AppInterface/inputOrder.do";
    public static String getOrderInfoURL = "PipingFactory/AppInterface/getOrderInfo.do";
    public static String orderIsGuanShuURL = "PipingFactory/AppInterface/orderIsGuanShu.do";
    public static String updateXiaLiaoStateURL = "PipingFactory/AppInterface/updateXiaLiaoState.do";
    public static String getUserInfoURL = "PipingFactory/AppInterface/getUserInfo.do";
    public static String getHistoryOrderListURL = "PipingFactory/AppInterface/getHistoryOrderList.do";
    public static String getCouldAutoXiaLiaoListURL = "PipingFactory/AppInterface/getCouldAutoXiaLiaoList.do";
    public static String getNewestVersionURL = "PipingFactory/AppInterface/getNewestVersion.do";

    public static String httpPost(String strURL, JSONObject strParam) {
        String resultData = "";
        try {
            URL url = new URL(strURL);
            System.out.println("url" + strURL);
            // 使用HttpURLConnection打开连接
            HttpURLConnection urlConn = null;
            DataOutputStream out = null;
            try {
                urlConn = (HttpURLConnection) url.openConnection();
                urlConn.setDoOutput(true);
                urlConn.setDoInput(true);
                // 设置以POST方式
                urlConn.setRequestMethod("POST");
                // 设置连接超时
                urlConn.setConnectTimeout(10 * 1000);
                urlConn.setReadTimeout(10 * 1000);
                // Post 请求不能使用缓存
                urlConn.setUseCaches(false);
                urlConn.setInstanceFollowRedirects(true);
                // 设置指定的请求头字段的值
                urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConn.setRequestProperty("Charset", "utf-8");
                urlConn.connect();
                out = new DataOutputStream(urlConn.getOutputStream());

                // 编码设置
                // URLEncoder.encode(strParam, "UTF_8");
                // 将要上传的内容写入流中
                out.write(strParam.toString().getBytes("UTF-8"));
                out.flush();
                out.close();
                // 获取数据
                int code = urlConn.getResponseCode();
                if (code == 200) {
                    InputStream in = urlConn.getInputStream();
                    resultData = readStream(in);
                }

                urlConn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return resultData;
    }


    private static String readStream(InputStream in) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int len = -1;
        byte[] bytes = new byte[1024];
        while ((len = in.read(bytes)) != -1) {
            baos.write(bytes, 0, len);
        }
        in.close();
        return new String(baos.toByteArray());

    }

}