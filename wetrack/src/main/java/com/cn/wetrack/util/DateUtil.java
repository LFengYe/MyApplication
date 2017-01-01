package com.cn.wetrack.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 时间工具类
 */
public class DateUtil {
	private static String partern = "yyyy-MM-dd";
	private static SimpleDateFormat sf = new SimpleDateFormat(partern, Locale.CHINA);

	/** 获取日、时、分、秒 */
	public static Map<String, Integer> getDHMSByTime(int second) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		int d, h, m, s;
		/* 天 */
		d = second / (3600 * 24);
		map.put("d", d);
		h = (second - d * 3600 * 24) / 3600;
		map.put("h", h);
		m = (second - d * 3600 * 24 - h * 3600) / 60;
		map.put("m", m);
		s = second - d * 3600 * 24 - h * 3600 - m * 60;
		map.put("s", s);
		return map;
	}

	/** 判断是否过期 */
	public static boolean isOverServiceTime(String overServiceTime) {
		boolean result = false;
		long nowTime = 0;
		long serviceTime = 0;
		try {
			nowTime = sf.parse(sf.format(new Date())).getTime();
			serviceTime = sf.parse(overServiceTime).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (nowTime > serviceTime) {
			result = true;
		}
		return result;
	}
	
	public static String getTodayYMD(){
		return sf.format(new Date());
	}
}
