package com.cxgps.vehicle.broadcasts;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.baidu.mapapi.SDKInitializer;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;

public class SDKReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String sdkVersion = intent.getAction();

        if (sdkVersion.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)){

            AppContext.showToast(context.getString(R.string.map_error_key));
        }else if (sdkVersion.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)){


        }else if(sdkVersion.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)){

            AppContext.showToast(context.getString(R.string.tips_have_no_intent));

        }

    }
}
