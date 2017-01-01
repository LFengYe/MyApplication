package com.cn.carigps.util;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cn.carigps.entity.Alarm;
import com.cn.carigps.entity.MyMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据库工具类
 * @author Leon
 *
 */
public class DBUtil {
	/*********************************************我的消息****************************************/
	/**创建消息表*/
	public static void createMessage(Context context){
		/*打开或创建数据库*/  
		SQLiteDatabase db = context.openOrCreateDatabase("seeworld.db", Context.MODE_PRIVATE, null);  
		/*创建消息表*/  
		db.execSQL("CREATE TABLE IF NOT EXISTS Message (time VARCHAR,content VARCHAR)");
	}
	
	/**添加消息*/
	public static void addMessage(Context context,MyMessage message){
		/*打开或创建数据库*/  
		SQLiteDatabase db = context.openOrCreateDatabase("seeworld.db", Context.MODE_PRIVATE, null);
		//插入数据  
        db.execSQL("INSERT INTO Message VALUES ( ?, ?)", new Object[]{message.getTime(), message.getContent()}); 
	}
	
	/**删除消息*/
	public static void delMessage(Context context,String time){
		/*打开或创建数据库*/  
		SQLiteDatabase db = context.openOrCreateDatabase("seeworld.db", Context.MODE_PRIVATE, null);
		//删除数据  
        db.delete("Message", "time = ?", new String[]{time});  
	}
	
	/**查询消息*/
	public static List<MyMessage> getAllMessage(Context context){
		/*打开或创建数据库*/  
		SQLiteDatabase db = context.openOrCreateDatabase("seeworld.db", Context.MODE_PRIVATE, null);
		Cursor c = db.rawQuery("SELECT * FROM Message", new String[]{}); 
		List<MyMessage> messages=new ArrayList<MyMessage>();
        while (c.moveToNext()) {  
            String time = c.getString(c.getColumnIndex("time"));  
            String content = c.getString(c.getColumnIndex("content")); 
            
            messages.add(new MyMessage(time,content));
        }  
        c.close(); 
        return messages;
	}
	
	/*********************************************告警****************************************/
	/**创建告警表*/
	public static void createAlarm(Context context){
		/*打开或创建数据库*/  
		SQLiteDatabase db = context.openOrCreateDatabase("seeworld.db", Context.MODE_PRIVATE, null);  
		/*创建告警表*/  
		db.execSQL("CREATE TABLE IF NOT EXISTS Alarm (SystemNo VARCHAR,Time VARCHAR,Longitude VARCHAR,Latitude VARCHAR,AlarmType VARCHAR)");
	}
	
	/**添加告警*/
	public static void addAlarm(Context context,Alarm alarm){
		/*打开或创建数据库*/  
		SQLiteDatabase db = context.openOrCreateDatabase("seeworld.db", Context.MODE_PRIVATE, null);
		//插入数据  
		db.execSQL("INSERT INTO Alarm VALUES ( ?, ?, ?, ?, ?)", new Object[]{alarm.getSystemNo(),alarm.getTime(),alarm.getLongitude(),alarm.getLatitude(),alarm.getAlarmType()}); 
	}
	
	/**删除告警*/
	public static void delAlarm(Context context,String SystemNo,String Time){
		/*打开或创建数据库*/  
		SQLiteDatabase db = context.openOrCreateDatabase("seeworld.db", Context.MODE_PRIVATE, null);
		//删除数据  
		db.delete("Alarm", "SystemNo =? and Time = ?", new String[]{SystemNo,Time});  
	}
	/**查询消息*/
	public static List<Alarm> getAllAlarm(Context context){
		/*打开或创建数据库*/  
		SQLiteDatabase db = context.openOrCreateDatabase("seeworld.db", Context.MODE_PRIVATE, null);
		Cursor c = db.rawQuery("SELECT * FROM Alarm", new String[]{}); 
		List<Alarm> alarms=new ArrayList<Alarm>();
		while (c.moveToNext()) {  
			String SystemNo = c.getString(c.getColumnIndex("SystemNo"));  
			String Time = c.getString(c.getColumnIndex("Time"));  
			String Longitude = c.getString(c.getColumnIndex("Longitude"));  
			String Latitude = c.getString(c.getColumnIndex("Latitude")); 
			String AlarmType = c.getString(c.getColumnIndex("AlarmType"));
			alarms.add(new Alarm(SystemNo,Time,Double.valueOf(Longitude),Double.valueOf(Latitude),AlarmType));
		}  
		c.close(); 
		return alarms;
	}
	
}
