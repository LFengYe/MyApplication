package com.cn.wetrack.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by LFeng on 16/9/25.
 */
public class AppUtils {

    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    public static int getScreenWidth(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.widthPixels;
    }

    public static int getScreenHeight(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return metrics.heightPixels;
    }

    public static boolean IsGooglePlayAvailable(Context context) {
        // 檢查服務是否可用
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        // 如果可用
        if (ConnectionResult.SUCCESS == resultCode) {
            return true;
            // 如果不可用
        } else {
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(resultCode, (Activity) context,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);
            if (errorDialog != null)
                errorDialog.show();
        }
        return false;
    }
}
