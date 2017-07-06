package com.cxgps.vehicle;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.StrictMode;
import android.util.DisplayMetrics;

import com.alibaba.fastjson.JSON;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.cxgps.vehicle.base.BaseApplication;
import com.cxgps.vehicle.bean.HistoryInfo;
import com.cxgps.vehicle.bean.UserInfo;
import com.cxgps.vehicle.cache.DataCleanManager;
import com.cxgps.vehicle.utils.MethodsCompat;
import com.kymjs.okhttp.OkHttpStack;
import com.kymjs.rxvolley.RxVolley;
import com.kymjs.rxvolley.http.RequestQueue;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import im.fir.sdk.FIR;

import static com.cxgps.vehicle.AppConfig.KEY_FRITST_START;

/**
 * Created by taosong on 16/12/22.
 */

public class AppContext  extends BaseApplication{

    private static AppContext instance;

    //设置连接超时
    public final static int CONNECT_TIMEOUT =60;
    public final static int READ_TIMEOUT=100;
    public final static int WRITE_TIMEOUT=60;

    @Override
    public void onCreate() {

        if (AppConfig.DEVELOPER_MODE && Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().penaltyDialog().build());
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().detectAll().penaltyDeath().build());
        }
        super.onCreate();
        FIR.init(this);
        instance = this;

        init();
    }

    private void init() {
//        AppCrashHandler handler = AppCrashHandler.getInstance();
//        if (!BuildConfig.DEBUG)
//            handler.init(this);


        OkHttpClient okHttpClient =  new OkHttpClient();

        okHttpClient.setReadTimeout(READ_TIMEOUT, TimeUnit.SECONDS);//设置读取超时时间
        okHttpClient.setWriteTimeout(WRITE_TIMEOUT,TimeUnit.SECONDS);//设置写的超时时间
        okHttpClient .setConnectTimeout(CONNECT_TIMEOUT,TimeUnit.SECONDS);//设置连接超时时间

        File cacheFolder = getCacheDir();
        RxVolley.setRequestQueue(RequestQueue.newRequestQueue(cacheFolder, new
                OkHttpStack(okHttpClient)));
        switchLanguage(getLanguageWithKey());



        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(getInstance());
    }


    /**
     * 获得当前app运行的AppContext
     *
     * @return
     */
    public static AppContext getInstance() {
        return instance;
    }

    public Properties getProperties() {
        return AppConfig.getAppConfig(this).get();
    }

    public void setProperty(String key, String value) {
        AppConfig.getAppConfig(this).set(key, value);
    }

    /**
     * 获取cookie时传AppConfig.CONF_COOKIE
     *
     * @param key
     * @return
     */
    public String getProperty(String key) {
        return AppConfig.getAppConfig(this).get(key);
    }

    public void removeProperty(String... key) {
        AppConfig.getAppConfig(this).remove(key);
    }


    public static boolean isFirstStart() {
        return getPreferences().getBoolean(KEY_FRITST_START, true);
    }

    public static void setFirstStart(boolean frist) {
        set(KEY_FRITST_START, frist);
    }



    public void setAddress(String host, String port) {

        set("login.host", host);
        set("login.port", port);

    }

    // 获取IP地址
    public String getAddress() {

        String loign_host = get("login.host", AppConfig.CONSTANTS_DEFAULT_HOST)
                .toString();

        String login_port = get("login.port", AppConfig.CONSTANTS_DEFAULT_PORT)
                .toString();

        return loign_host + ":" + login_port;
    }

    public static String getLanguageWithKey(){

        return  getPreferences().getString(AppConfig.KEY_LANGUAGE,"zh");

    }

    public static void setLanguageWithData(String data){

        getPreferences().edit().putString(AppConfig.KEY_LANGUAGE,data).apply();
    }

    public static String getMapWithKey(){

        return  getPreferences().getString(AppConfig.KEY_MAPTYPW,"1");
    }

    public static void setMaptypeWithData(String data){

        getPreferences().edit().putString(AppConfig.KEY_MAPTYPW,data).apply();
    }





    /*********************** 保存用户信息 ***************************/

    /**
     * 保存登录信息
     *
     * @param user
     *            用户信息
     */
    @SuppressWarnings("serial")
    public void saveUserInfo(int loginstyle, final UserInfo user) {

        if (user == null) {
            return;
        }

        if (loginstyle == AppConfig.USER_LOGIN_TAG) {

            set(AppConfig.KEY_USER_LOGIN, JSON.toJSONString(user));
        } else if (loginstyle == AppConfig.CARNUMBER_LOGIN_TAG) {
            set(AppConfig.KEY_CAR_LOGIN, JSON.toJSONString(user));

        }
        set(AppConfig.KEY_NUMBER_USER, loginstyle);

    }

    /**
     * 获得登录用户的信息
     *
     * @return
     */
    public UserInfo getLoginUser() {

        int loginstyle = get(AppConfig.KEY_NUMBER_USER,
                AppConfig.USER_LOGIN_TAG);

        String userJson = "";
        if (loginstyle == AppConfig.USER_LOGIN_TAG) {
            userJson = get(AppConfig.KEY_USER_LOGIN, "").toString();

        } else if (loginstyle == AppConfig.CARNUMBER_LOGIN_TAG) {
            userJson = get(AppConfig.KEY_CAR_LOGIN, "").toString();
        }

        if (userJson == null || "".equals(userJson)) {

            return null;
        }
        UserInfo user = JSON.parseObject(userJson, UserInfo.class);

        return user;

    }

    /******************************* 刷新时间 ***********************************/

    public static long geReflushTime(String key) {

        long monitorTime = get(key, 15);

        return monitorTime;
    }

    public static void setReflushTime(String key, int time) {

        set(key, time);

    }

    /**
     * 清除app缓存
     */
    public void clearAppCache() {
        DataCleanManager.cleanDatabases(this);
        // 清除数据缓存
        DataCleanManager.cleanInternalCache(this);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            DataCleanManager.cleanCustomCache(MethodsCompat
                    .getExternalCacheDir(this));
        }
        // 清除编辑器保存的临时内容
        Properties props = getProperties();
        for (Object key : props.keySet()) {
            String _key = key.toString();
            if (_key.startsWith("temp"))
                removeProperty(_key);
        }
    }


    /**
     * 判断当前版本是否兼容目标版本的方法
     *
     * @param VersionCode
     * @return
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    public void switchLanguage(String language) {
        // 设置应用语言类型
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        if (language.equals("en")) {
            config.locale = Locale.ENGLISH;
        } else {
            config.locale = Locale.SIMPLIFIED_CHINESE;
        }
        resources.updateConfiguration(config, dm);

        // 保存设置语言的类型

        setLanguageWithData(language);

    }


    /**
     * 保存当前用户的系统编号
     */
    private ArrayList<String> carSystemNo = new ArrayList<>();


    public ArrayList<String> getCarSystemNo() {
        return carSystemNo;
    }

    public void setCarSystemNo(ArrayList<String> carSystemNo) {
        this.carSystemNo = carSystemNo;
    }


    private List<HistoryInfo> historyInfos;


    public List<HistoryInfo> getHistoryInfos() {
        return historyInfos;
    }

    public void setHistoryInfos(List<HistoryInfo> historyInfos) {
        this.historyInfos = historyInfos;
    }


    private BNRoutePlanNode tempEndNode;


    public BNRoutePlanNode getTempEndNode() {
        return tempEndNode;
    }

    public void setTempEndNode(BNRoutePlanNode tempEndNode) {
        this.tempEndNode = tempEndNode;
    }

    @Override
    public void onTerminate() {
        // 程序终止的时候执行

        super.onTerminate();


    }
    @Override
    public void onLowMemory() {
        // 低内存的时候执行

        super.onLowMemory();


    }
}
