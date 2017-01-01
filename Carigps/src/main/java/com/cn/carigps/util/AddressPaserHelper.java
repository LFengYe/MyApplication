package com.cn.carigps.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddressPaserHelper {
public AddressPaserHelper(){
	
}
public String Addresspaser(Double Longitude, Double Latitude) throws IOException{
	String address="";
	JSONArray jsonresult=null;
	JSONObject jsonobjectresult=null;
	String strUrl="https://maps.googleapis.com/maps/api/geocode/json?latlng=";
	String data = "";
	strUrl=strUrl+Latitude+","+Longitude+"&&language=zh-CN";
	InputStream iStream = null;  
    HttpURLConnection urlConnection = null; 
    try {  
        URL url = new URL(strUrl);  
  
        // Creating an http connection to communicate with url  
        urlConnection = (HttpURLConnection) url.openConnection();  
  
        // Connecting to url  
        urlConnection.connect();  
  
        // Reading data from url  
        iStream = urlConnection.getInputStream();  
  
        BufferedReader br = new BufferedReader(new InputStreamReader(  
                iStream));  
  
        StringBuffer sb = new StringBuffer();   
        String line = "";  
        while ((line = br.readLine()) != null) {  
            sb.append(line);  
        }  
  
        data = sb.toString();  
        Log.d("google", "google:"+data);
        Log.d("google", "google:1");
        JSONObject json = JSON.parseObject(data);
        Log.d("google", "google:2"+json.toString());
        jsonresult=json.getJSONArray("results");
        Log.d("google", "google:3"+jsonresult.toString());
        jsonobjectresult=(JSONObject)jsonresult.get(0);
        Log.d("google", "google:4");
        address=(String)jsonobjectresult.get("formatted_address");
        Log.d("google", "address:"+address);
        br.close();  
  
    } catch (Exception e) {  
        Log.d("Exception while downloading url", e.toString());  
        Log.d("google", "fail:");
    } finally {  
    	if(iStream!=null)
        iStream.close();  
        urlConnection.disconnect();  
    }  
    
	return address;
}
}
