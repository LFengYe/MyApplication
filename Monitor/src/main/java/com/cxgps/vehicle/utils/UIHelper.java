package com.cxgps.vehicle.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ZoomButtonsController;

import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.bean.SimpleBackPage;
import com.cxgps.vehicle.interf.ICallbackResult;
import com.cxgps.vehicle.service.DownloadService;
import com.cxgps.vehicle.ui.DetailActivity;
import com.cxgps.vehicle.ui.GuideActivity;
import com.cxgps.vehicle.ui.LoginActivity;
import com.cxgps.vehicle.ui.MainActivity;
import com.cxgps.vehicle.ui.SimpleBackActivity;
import com.cxgps.vehicle.ui.TrackplayActivity;
import com.cxgps.vehicle.ui.ZuiZongActivity;

/**
 * Created by taosong on 16/12/29.
 */

public class UIHelper {


    /**（1）用户登录
     *
     * @param context
     */
    public static void showLoginActivity(Context context){

        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);


    }



    /**（2）主界面
     *
     * @param context
     */
    public static void showMainActivity(Context context){

        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);

    }


    /**（3）指南界面
     *
     * @param context
     */
    public static void showGuideActivity(Context context){

        Intent intent = new Intent(context, GuideActivity.class);
        context.startActivity(intent);

    }


    /*****************************通用的后退返回界面***************************************/

    public static void showSimpleBackForResult(Activity context,
                                               int requestCode, SimpleBackPage page, Bundle args) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
        context.startActivityForResult(intent, requestCode);
    }

    public static void showSimpleBackForResult(Activity context,
                                               int requestCode, SimpleBackPage page) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
        context.startActivityForResult(intent, requestCode);
    }

    public static void showSimpleBack(Context context, SimpleBackPage page) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
        context.startActivity(intent);
    }

    public static void showSimpleBack(Context context, SimpleBackPage page,
                                      Bundle args) {
        Intent intent = new Intent(context, SimpleBackActivity.class);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_ARGS, args);
        intent.putExtra(SimpleBackActivity.BUNDLE_KEY_PAGE, page.getValue());
        context.startActivity(intent);
    }


    /**(5)
     * 显示详情
     *
     * @param context
     */
    public static void showWebDetail(Context context, int type, int flag) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("type", type);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE, flag);
        context.startActivity(intent);
    }


    /**
     * (6)初始化WebView
     * @param webView
     */
    @SuppressLint({ "JavascriptInterface", "SetJavaScriptEnabled" })
    public static void initWebView(WebView webView) {
        WebSettings settings = webView.getSettings();
        settings.setDefaultFontSize(15);
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        int sysVersion = Build.VERSION.SDK_INT;
        if (sysVersion >= 11) {
            settings.setDisplayZoomControls(false);
        } else {
            ZoomButtonsController zbc = new ZoomButtonsController(webView);
            zbc.getZoomControls().setVisibility(View.GONE);
        }
        webView.setWebViewClient(UIHelper.getWebViewClient());
    }

    /**(7)
     * 获取webviewClient对象
     *
     * @return
     */
    public static WebViewClient getWebViewClient() {

        return new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        };
    }


    /**(8)
     * 清除app缓存
     *
     * @param activity
     */
    public static void clearAppCache(final Activity activity) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    AppContext.showToastShort(activity.getString(R.string.clear_cache_success));
                } else {
                    AppContext.showToastShort(activity.getString(R.string.clear_cache_faile));
                }
            }
        };
        new Thread() {
            @Override
            public void run() {
                Message msg = new Message();
                try {
                    AppContext.getInstance().clearAppCache();
                    msg.what = 1;
                } catch (Exception e) {
                    e.printStackTrace();
                    msg.what = -1;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }


    /**
     * (9) APP下载升级功能
     * @param context
     * @param downurl
     * @param tilte
     */

    public static void openDownLoadService(Context context, String downurl,
                                           String tilte) {
        final ICallbackResult callback = new ICallbackResult() {

            @Override
            public void OnBackResult(Object s) {
            }
        };
        ServiceConnection conn = new ServiceConnection() {

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DownloadService.DownloadBinder binder = (DownloadService.DownloadBinder) service;
                binder.addCallback(callback);
                binder.start();

            }
        };
        Intent intent = new Intent(context, DownloadService.class);
        intent.putExtra(DownloadService.BUNDLE_KEY_DOWNLOAD_URL, downurl);
        intent.putExtra(DownloadService.BUNDLE_KEY_TITLE, tilte);
        context.startService(intent);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }


    /**
     * （10）显示跟踪信息
     * @param context
     * @param args
     */
    public static void showZuiZongActivity(Context context, Bundle args){
        Intent intent = new Intent(context, ZuiZongActivity.class);
        intent.putExtras(args);
        context.startActivity(intent);
    }

    /**
     * （11）轨迹信息界面
     * @param context
     * @param args
     */
    public static void showTrackplayActivity(Context context, Bundle args){

        Intent intent = new Intent(context, TrackplayActivity.class);
        intent.putExtras(args);
        context.startActivity(intent);

    }


    /**
     * （12）详情
     *
     * @param context
     * @param systemNoList
     */
    public static void showWebDetail(Context context, int type, int flag,Bundle bundle) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra("type", type);
        intent.putExtras(bundle);
        intent.putExtra(DetailActivity.BUNDLE_KEY_DISPLAY_TYPE, flag);
        context.startActivity(intent);
    }


}
