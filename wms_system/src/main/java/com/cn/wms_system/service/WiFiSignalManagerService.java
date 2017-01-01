package com.cn.wms_system.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by LFeng on 16/10/22.
 */
public class WiFiSignalManagerService extends Service {
    private static final String TAG = "WiFiSignalManagerService";
    private IntentFilter wifiFilter;

    private BroadcastReceiver wifiIntentReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifi_state = intent.getIntExtra("wifi_state", 0);
            int level = Math.abs(((WifiManager)getSystemService(WIFI_SERVICE)).getConnectionInfo().getRssi());
            switch (wifi_state) {
                case WifiManager.WIFI_STATE_DISABLED: {
                    break;
                }
                case WifiManager.WIFI_STATE_DISABLING: {
                    break;
                }
                case WifiManager.WIFI_STATE_ENABLED: {
                    if (level <= 60) {

                    } else {
                        //切换网络
                    }
                    break;
                }
                case WifiManager.WIFI_STATE_ENABLING: {
                    break;
                }
                case WifiManager.WIFI_STATE_UNKNOWN: {
                    break;
                }
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        wifiFilter = new IntentFilter();
        wifiFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        registerReceiver(wifiIntentReceiver, wifiFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiIntentReceiver);
    }
}
