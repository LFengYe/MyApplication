package com.cn.wms_system_new.service;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cn.wms_system_new.activity.LandActivity;

public abstract class BootBroadcastReceiver extends BroadcastReceiver {
	
	int count = 0;
	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Intent startApp = new Intent(context, LandActivity.class);
			startApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(startApp);
		}
		
		if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
			updateTime();
			count ++;
			if (count > 10) {
				System.out.println("10 min notification");
				updateData();
				count = 0;
			}
		}
	}
	
	public abstract void updateTime();
	public abstract void updateData();
}
