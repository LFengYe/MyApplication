package com.cn.wms_system.service;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import cz.msebera.android.httpclient.Consts;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.conn.scheme.Scheme;
import cz.msebera.android.httpclient.conn.scheme.SchemeRegistry;
import cz.msebera.android.httpclient.conn.ssl.SSLSocketFactory;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.impl.conn.tsccm.ThreadSafeClientConnManager;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * 访问工具类
 */
public class HttpRequestClient {
	private static HttpClient client = null;
	/*默认服务器*/
	public static String defaultHost = "https://wetrack.carigps.com/";
	public static String host = "https://wetrack.carigps.com/";
	public static String serverList = "https://appen.carigps.com/server.ashx?username=serveradmin&password=dd38eeb126ff3ecca564bbaa4f89c2be&action=getserverlist";

	/* 语言 */
	public static String systemLang = "zh-cn";

	/** 单例，维持一个session */
	public static HttpClient getClient() {
		try {
			if (client == null) {
				client = HttpClientBuilder.create().build();
			}
			return client;
		} catch (Exception e) {

		}
		return null;
	}


}
