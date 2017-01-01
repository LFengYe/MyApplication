package com.lfeng.pipingfactory.util;

import android.content.Context;
import android.content.SharedPreferences;


public class PreferenceUtil {

    public static SharedPreferences preferences;
    /*
     * 将数据存入配置文件的方法
     * void无返回类型
     */

    public static void putString(String key, String value, Context context) {
        checkSP(context);
        preferences.edit().putString(key, value).apply();

    }

    public static String getString(String key, Context context) {
        checkSP(context);
        return preferences.getString(key, null);

    }



    private static void checkSP(Context context) {
        if (preferences == null) {
            preferences = context.getSharedPreferences("Auto", Context.MODE_PRIVATE);
        }
    }

    public static void clear() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.apply();
    }
}