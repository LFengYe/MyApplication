package com.cn.wetrack.util;

import java.util.List;

import android.content.Context;
import android.location.LocationManager;

/**
 * 检测手机GPS、wifi、网络和蓝牙状态
 */
public class CheckStateUtils {
	/** 检查GPS是否可用 */
	public static boolean isGpsEnabled(Context context) {
		LocationManager locationManager = ((LocationManager) context.getSystemService(Context.LOCATION_SERVICE));
		List<String> accessibleProviders = locationManager.getProviders(true);
		return accessibleProviders != null && accessibleProviders.size() > 0;
	}

}
