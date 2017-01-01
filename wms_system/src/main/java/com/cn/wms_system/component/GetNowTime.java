package com.cn.wms_system.component;

import android.text.format.Time;

public class GetNowTime {

	private static Time nowTime;
	
	/*public GetNowTime() {
		String timeZone = Time.getCurrentTimezone();
		nowTime = new Time(timeZone);
		nowTime.setToNow();
	}*/
	public static String[] weekDay = {"日", "一", "二", "三", "四", "五", "六"};
	
	static{
		String timeZone = Time.getCurrentTimezone();
		nowTime = new Time(timeZone);
		nowTime.setToNow();
	}
	
	public static int getYear() {
		nowTime.setToNow();
		return nowTime.year;
	}
	
	public static int getMonth() {
		nowTime.setToNow();
		return nowTime.month;
	}
	
	public static int getDay() {
		nowTime.setToNow();
		return nowTime.monthDay;
	}
	
	public static int getHour() {
		nowTime.setToNow();
		return nowTime.hour;
	}
	
	public static int getMinute() {
		nowTime.setToNow();
		return nowTime.minute;
	}
	
	public static int getSecond() {
		nowTime.setToNow();
		return nowTime.second;
	}
	
	public static void refreshTime() {
		nowTime.setToNow();
	}
	
	public static String getNowTime() {
		nowTime.setToNow();
		String now = nowTime.year + "-" + 
				(((nowTime.month + 1) < 10) ? ("0" + (nowTime.month + 1)) : (nowTime.month + 1))+ "-" + 
				((nowTime.monthDay < 10) ? ("0" + nowTime.monthDay) : nowTime.monthDay) + " " + 
				((nowTime.hour < 10) ? "0" + nowTime.hour : nowTime.hour) + ":"+ 
				((nowTime.minute < 10) ? "0" + nowTime.minute : nowTime.minute) + ":"+ 
				((nowTime.second < 10) ? "0" + nowTime.second : nowTime.second);
		return now;
	}
	
	public static String getDate() {
		nowTime.setToNow();
		return nowTime.year + "-" + 
				(((nowTime.month + 1) < 10) ? ("0" + (nowTime.month + 1)) : (nowTime.month + 1))+ "-" + 
				((nowTime.monthDay < 10) ? ("0" + nowTime.monthDay) : nowTime.monthDay);
	}
	
	public static String getTime() {
		nowTime.setToNow();
		return ((nowTime.hour < 10) ? "0" + nowTime.hour : nowTime.hour) + ":"
		+ ((nowTime.minute < 10) ? "0" + nowTime.minute : nowTime.minute) + ":"
		+ ((nowTime.second < 10) ? "0" + nowTime.second : nowTime.second);
	}
	
	public static String getHanYuDate() {
		nowTime.setToNow();
		return nowTime.year + "年" + 
				(((nowTime.month + 1) < 10) ? ("0" + (nowTime.month + 1)) : (nowTime.month + 1))+ "月" + 
				((nowTime.monthDay < 10) ? ("0" + nowTime.monthDay) : nowTime.monthDay) + "日" +
				"  " +
				((nowTime.hour < 10) ? "0" + nowTime.hour : nowTime.hour) + ":" + 
				((nowTime.minute < 10) ? ("0" + nowTime.minute) : nowTime.minute) +
				"      " +
				"星期" + weekDay[nowTime.weekDay];
	}
	
	public static String getHanYuTime() {
		nowTime.setToNow();
		return ((nowTime.hour < 10) ? "0" + nowTime.hour : nowTime.hour) + "时" + 
				((nowTime.minute < 10) ? ("0" + nowTime.minute) : nowTime.minute) + "分";
	}
}
