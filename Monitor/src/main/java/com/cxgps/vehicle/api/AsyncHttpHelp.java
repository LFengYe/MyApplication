package com.cxgps.vehicle.api;

import com.cxgps.vehicle.utils.TDevice;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;

/**
 * Created by taosong on 16/12/22.
 */

public class AsyncHttpHelp {


    public static void get(String url, HttpCallback handler) {
        RxVolley.get(url, handler);
    }

    public static void get(String url, HttpParams params, HttpCallback handler) {
        new RxVolley.Builder().shouldCache(false).url(url).params(params).callback(handler).doTask();
    }

    public static void post(String url, HttpParams params, HttpCallback handler) {
        RxVolley.post(url, params, handler);

    }


    /**
     * 获得UserAgent
     *
     * @return
     */
    private static String getUserAgent() {

        StringBuilder ua = new StringBuilder("cxgps.com");
        ua.append('/' + TDevice.getVersionName()+ '_' + TDevice.getVersionCode());//App版本
        ua.append("/Android");//手机系统平台
        ua.append("/" + android.os.Build.VERSION.RELEASE);//手机系统版本
        ua.append("/" + android.os.Build.MODEL); //手机型号

        return ua.toString();
    }

    public static HttpParams getPrivateTokenWithParams() {
        HttpParams params = new HttpParams();
        params.putHeaders("User-Agent", getUserAgent());

        return params;
    }

    public static HttpParams getHttpParams() {
        return getPrivateTokenWithParams();
    }
}
