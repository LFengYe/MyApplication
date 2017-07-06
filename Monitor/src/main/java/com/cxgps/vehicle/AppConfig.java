package com.cxgps.vehicle;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * Created by taosong on 16/12/22.
 */

public class AppConfig {
    public static final boolean DEVELOPER_MODE = false;


    public static final int PAGE_SIZE = 20;

    public final static int  MODE_TRACK = 0 ;

    public final static int MODE_REPORT = 1;

    public static int SELECT_DATA_MODE = AppConfig.MODE_TRACK;

    private final static String APP_CONFIG = "config";
    public static final String KEY_DOUBLE_CLICK_EXIT = "KEY_DOUBLE_CLICK_EXIT";
    public static final String KEY_FRITST_START = "KEY_FRIST_START";
    public static final String KEY_CHECK_UPDATE = "KEY_CHECK_UPDATE";


    public static final String KEY_LANGUAGE = "KEY_LANGUAGE";
    public static final String KEY_MAPTYPW = "KEY_MAPTYPE";


    // 用户登陆信息 key
    public static final String KEY_USER_LOGIN = "user_login";
    public static final String KEY_CAR_LOGIN = "car_login";
    public static final int USER_LOGIN_TAG = 0 ;
    public static final  int CARNUMBER_LOGIN_TAG = 1;
    public static final String KEY_NUMBER_USER = "KEY_USER_NUMBER";




    // 请求链接   测试账号  http://218.10.91.84:8001/
    public final static String CONSTANTS_DEFAULT_HOST = "120.26.197.152"; //58.60.185.172

    public final static String CONSTANTS_DEFAULT_PORT = "8000"; //8000

    public final static String CONSTANTS_DEFAULT_ACCOUNT = ""; //58.60.185.172

    public final static String CONSTANTS_DEFAULT_PASSWD = ""; //8000


    public final static int REPORT = 0;  //报表

    public final static int ABOUT = 1;  //关于我们

    public final static int GUIDE = 2;  //指南

    public final static int WEIZHANG = 3; // 违章查询

    public final static int SHARE = 4; //分享内容

    public static final String ROUTE_PLAN_NODE = "routePlanNode";

    // 播放状态
    public static final int PlayStop = 0;

    public static final int PlayIng = 1;

    public static final int RePlay = 2;

    // 请求刷新时间

    public static final String KEY_REFLUSH_MONITOR_TIME = "KEY_REFLUSH_MONITOR_TIME";

    public static final String KEY_REFLUSH_ZUIZONG_TIME = "KEY_REFLUSH_ZUIZONG_TIME";




    // 默认存放文件下载的路径
    public final static String DEFAULT_SAVE_FILE_PATH = Environment
            .getExternalStorageDirectory()
            + File.separator
            + "CarTrace"
            + File.separator + "download" + File.separator;





    private Context mContext;
    private static AppConfig appConfig;

    public static AppConfig getAppConfig(Context context) {
        if (appConfig == null) {
            appConfig = new AppConfig();
            appConfig.mContext = context;
        }
        return appConfig;
    }


    /**
     * 获取Preference设置
     */
    public static SharedPreferences getSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    public String get(String key) {
        Properties props = get();
        return (props != null) ? props.getProperty(key) : null;
    }

    public Properties get() {
        FileInputStream fis = null;
        Properties props = new Properties();
        try {
            // 读取files目录下的config
            // fis = activity.openFileInput(APP_CONFIG);

            // 读取app_config目录下的config
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            fis = new FileInputStream(dirConf.getPath() + File.separator
                    + APP_CONFIG);

            props.load(fis);
        } catch (Exception e) {
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return props;
    }

    private void setProps(Properties p) {
        FileOutputStream fos = null;
        try {
            // 把config建在files目录下
            // fos = activity.openFileOutput(APP_CONFIG, Context.MODE_PRIVATE);

            // 把config建在(自定义)app_config的目录下
            File dirConf = mContext.getDir(APP_CONFIG, Context.MODE_PRIVATE);
            File conf = new File(dirConf, APP_CONFIG);
            fos = new FileOutputStream(conf);

            p.store(fos, null);
            fos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (Exception e) {
            }
        }
    }

    public void set(Properties ps) {
        Properties props = get();
        props.putAll(ps);
        setProps(props);
    }

    public void set(String key, String value) {
        Properties props = get();
        props.setProperty(key, value);
        setProps(props);
    }

    public void remove(String... key) {
        Properties props = get();
        for (String k : key)
            props.remove(k);
        setProps(props);
    }

}
