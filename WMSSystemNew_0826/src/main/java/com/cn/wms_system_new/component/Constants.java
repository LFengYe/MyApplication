package com.cn.wms_system_new.component;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.LinearLayout.LayoutParams;

import java.io.DataOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Constants {
	public static final boolean debug_mode = true;
	public static final String debug_Mac_address = "84:3A:4B:00:2A:9B";

	public static final int JPUSH_ALIAS_SEQUENCE = 4001;
	/**
	 * 屏蔽home键时，修改的标志位
	 */
	public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

	//网络返回数据类型
	/** 返回消息为空 */
	public static final int GET_MESSAGE_IS_EMPTY = 0;//返回消息为空
	/** 返回列表消息 */
	public static final int GET_PLAN_LIST_MESSAGE = 1;//返回计划列表
//	public static final int GET_PLAN_DETAIL_MESSAGE = 2;//返回计划明细
//	public static final int GET_SOCK_DETAIL_MESSAGE = 3;//返回备货明细
//	public static final int GET_PICK_DETAIL_MESSAGE = 4;//返回领货明细
//	public static final int GET_DISTRI_DETAIL_MESSAGE = 5;//返回领货明细
	
	/**表格字体初始大小 */
	public static final int TEXTSIZE_INIT = 22;
	/**表格字体大小最大值 */
	public static final int TEXTSIZE_MAX = 36;
	/**表格字体大小最小值 */
	public static final int TEXTSIZE_MIN = 16;
	/**
	 * 登录与退出按钮点击响应
	 */
	public static final int BTN_LAND_CLICKED = 0x33;
	public static final int BTN_EXIT_CLICKED = 0x34;
	
	/** 填满父控件布局参数 */
	public static LayoutParams FILL_FILL_LAYOUTPARAMS = new LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
	/** 水平方向填满父控件布局*/
	public static LayoutParams HORIZONTAL_FILL_LAYOUTPATAMS = new LayoutParams(
			LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
	/** 垂直方向填满父控件布局*/
	public static LayoutParams VERTICAL_FILL_LAYOUTPARAMS = new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
	/** 自适应内容布局*/
	public static LayoutParams WRAP_WRAP_LAYOUTPARAMS = new LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	
	/** 服务器WebService地址 */
//	public static final String webServiceURL = "http://90.0.15.5/myweb/Service.asmx";
	public static String webServiceURL = "http://222.179.138.103:8091/Service.asmx";
	public static String nameSpace = "http://tempuri.org/";
	/**  当前登录用户*/
	public static String landUser = "";
	
	/**
	 * 获取Mac地址
	 * @param context
	 * @return
	 */
	public static String getMacAddress(Context context) {
		WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = manager.getConnectionInfo();
		System.out.println("Mac address:" + info.getMacAddress());
		return info.getMacAddress();
	}
	
	/**
	 * 去除字符串中的空格、回车、换行符、制表符
	 * @param str 要过滤的字符串
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 去掉字符串中的回车符
	 * @param str the will replaced string
	 * @return non enter string
	 */
	public static String replaceEnter(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}
	
	/**
	 * 将指定的字符串addValue，添加到字符串数组destination的指定位置position
	 * @param original 原字符串
	 * @param addValue 要添加的字符串
	 * @param position 添加的位置
	 * @return 添加后的字符串
	 */
	public static String[] addToStringArray(String[] original, String addValue, int position) {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < original.length; i++) {
			arrayList.add(original[i]);
		}
		arrayList.add(position, addValue);
		return listToArray(arrayList);
	}
	
	/**
	 * 将字符串列表转换成为字符串
	 * @param data 要转换的字符列表
	 * @return
	 */
	public static String[] listToArray(ArrayList<String> data) {
		String[] result = new String[data.size()];
		for (int i = 0; i < data.size(); i++) {
			result[i] = data.get(i);
		}
		return result;
	}
	
	/**
	 * 检测字符串是否为纯数字
	 * @param examined 被检测字符串
	 * @return true 字符串只包含0-9的字符
	 *         false 字符串包含0-9之外的字符
	 */
	public static boolean checkStringIsNum(String examined) {
		if (examined.length() > 0)
			return examined.matches("[0-9]+");
		return false;
	}
	
	public static void switchSystemUI() throws Exception {
			Process p;
			p = Runtime.getRuntime().exec("su");
			File systemUIapkFile = new File("/system/app/SystemUI.apk");

			// Attempt to write a file to a root-only
			DataOutputStream os = new DataOutputStream(p.getOutputStream());
			os.writeBytes("mount -o remount,rw /dev/block/stl6 /system\n");
			if (systemUIapkFile.exists()) {
				os.writeBytes("mv /system/app/SystemUI.apk /system/SystemUI.apk\n");
			} else {
				os.writeBytes("mv /system/SystemUI.apk /system/app/SystemUI.apk\n");
			}
			os.writeBytes("mount -o remount,ro /dev/block/stl6 /system\n");

			// Close the terminal
			os.writeBytes("exit\n");
			os.flush();
			p.waitFor();
	}
	
	/**
	 * 移除系统UI应用程序
	 * 执行该段程序后需执行下面一段代码，否则系统会重启
	 * 
	 * Intent intent2 = new Intent();
	 * intent2.setComponent(new ComponentName(
				"com.android.systemui","com.android.systemui.SystemUIService"));
	 * context.startService(intent2);
	 * @throws Exception
	 */
	public static void removeSystemUI() throws Exception {
		Process p;
		p = Runtime.getRuntime().exec("su");
		File systemUIapkFile = new File("/system/app/SystemUI.apk");
		File launcherapkFile = new File("/system/app/LenovoLauncher.apk");

		// Attempt to write a file to a root-only
		DataOutputStream os = new DataOutputStream(p.getOutputStream());
		os.writeBytes("mount -o remount,rw /dev/block/stl6 /system\n");
		//移除系统文件SystemUI.apk
		if (systemUIapkFile.exists())
			os.writeBytes("mv /system/app/SystemUI.apk /system/SystemUI.apk\n");
		//移除系统文件LenovoLauncher.apk
		if (launcherapkFile.exists())
			os.writeBytes("mv /system/app/LenovoLauncher.apk /system/LenovoLauncher.apk\n");
		
		os.writeBytes("mount -o remount,ro /dev/block/stl6 /system\n");

		// Close the terminal
		os.writeBytes("exit\n");
		os.flush();
		p.waitFor();
	}
	
	/**
	 * 恢复系统UI应用程序
	 * 执行该段程序后需执行下面一段代码，否则系统会重启
	 * 
	 * Intent intent2 = new Intent();
	 * intent2.setComponent(new ComponentName(
				"com.android.systemui","com.android.systemui.SystemUIService"));
	 * context.startService(intent2);
	 */
	public static void recoverySystemUI() throws Exception {
		Process p;
		p = Runtime.getRuntime().exec("su");
		File systemUIapkFile = new File("/system/app/SystemUI.apk");
		File launcherapkFile = new File("/system/app/LenovoLauncher.apk");

		// Attempt to write a file to a root-only
		DataOutputStream os = new DataOutputStream(p.getOutputStream());
		os.writeBytes("mount -o remount,rw /dev/block/stl6 /system\n");
		
		//恢复系统文件SystemUI.apk
		if (!systemUIapkFile.exists()) {
			os.writeBytes("mv /system/SystemUI.apk /system/app/SystemUI.apk\n");
		}
		//恢复系统文件LenovoLauncher.apk
		if (!launcherapkFile.exists()) {
			os.writeBytes("mv /system/LenovoLauncher.apk /system/app/LenovoLauncher.apk\n");
		}
		os.writeBytes("mount -o remount,ro /dev/block/stl6 /system\n");

		// Close the terminal
		os.writeBytes("exit\n");
		os.flush();
		p.waitFor();
	}

	/**
	 * 移除启动器应用程序
	 * 执行该段程序后需执行下面一段代码，否则系统会重启
	 * 
	 * Intent intent2 = new Intent();
	 * intent2.setComponent(new ComponentName(
				"com.android.systemui","com.android.systemui.SystemUIService"));
	 * context.startService(intent2);
	 * @throws Exception
	 */
	/*public static void removeLauncher() throws Exception {
		Process p;
		p = Runtime.getRuntime().exec("su");
		File systemUIapkFile = new File("/system/app/LenovoLauncher.apk");

		// Attempt to write a file to a root-only
		DataOutputStream os = new DataOutputStream(p.getOutputStream());
		os.writeBytes("mount -o remount,rw /dev/block/stl6 /system\n");
		if (systemUIapkFile.exists()) {
			os.writeBytes("mv /system/app/LenovoLauncher.apk /system/LenovoLauncher.apk\n");
		}
		os.writeBytes("mount -o remount,ro /dev/block/stl6 /system\n");

		// Close the terminal
		os.writeBytes("exit\n");
		os.flush();
		p.waitFor();
	}
	*/
	/**
	 * 恢复启动器应用程序
	 * 执行该段程序后需执行下面一段代码，否则系统会重启
	 * 
	 * Intent intent2 = new Intent();
	 * intent2.setComponent(new ComponentName(
				"com.android.systemui","com.android.systemui.SystemUIService"));
	 * context.startService(intent2);
	 * @throws Exception
	 */
	/*public static void recoveryLauncher() throws Exception {
		Process p;
		p = Runtime.getRuntime().exec("su");
		File systemUIapkFile = new File("/system/app/LenovoLauncher.apk");

		// Attempt to write a file to a root-only
		DataOutputStream os = new DataOutputStream(p.getOutputStream());
		os.writeBytes("mount -o remount,rw /dev/block/stl6 /system\n");
		if (!systemUIapkFile.exists()) {
			os.writeBytes("mv /system/LenovoLauncher.apk /system/app/LenovoLauncher.apk\n");
		}
		os.writeBytes("mount -o remount,ro /dev/block/stl6 /system\n");

		// Close the terminal
		os.writeBytes("exit\n");
		os.flush();
		p.waitFor();
	}*/
}
