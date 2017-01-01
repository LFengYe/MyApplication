package com.cn.carigps.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.widget.Toast;

/**
 * 工具类
 */
public class UtilTool {
	/**
	 * 显示消息
	 * @param context
	 * @param message
	 */
	public static void showToast(Context context,String message){
		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 检查是否连接网络
	 * @param context
	 * @return
	 */
	public static Boolean CheckNetwork(final Context context) {
		boolean flag = false;
		ConnectivityManager cwjManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cwjManager.getActiveNetworkInfo() != null)
			flag = cwjManager.getActiveNetworkInfo().isAvailable();
		return flag;
	}

}
