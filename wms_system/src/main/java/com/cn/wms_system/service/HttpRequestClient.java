package com.cn.wms_system.service;

import com.alibaba.fastjson.JSONObject;
import com.cn.wms_system.bean.SResponse;

import cz.msebera.android.httpclient.Consts;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.entity.StringEntity;
import cz.msebera.android.httpclient.impl.client.HttpClientBuilder;
import cz.msebera.android.httpclient.util.EntityUtils;

/**
 * 访问工具类
 */
public class HttpRequestClient {
	private static HttpClient client = null;
	/*默认服务器*/
	public static String ipAddress = "192.168.1.181";
	public static String port = "9698";
	public static String baseUrl = "http://" + ipAddress + ":" + port + "/RuiBangWuLiu/";
	public static String appHost = "http://" + ipAddress + ":" + port + "/RuiBangWuLiu/app.do";
	public static String moveHost = "http://" + ipAddress + ":" + port + "/RuiBangWuLiu/move.do";
	public static String outHost = "http://" + ipAddress + ":" + port + "/RuiBangWuLiu/out.do";
	public static String inHost = "http://" + ipAddress + ":" + port + "/RuiBangWuLiu/in.do";
	public static String reportHost = "http://" + ipAddress + ":" + port + "/RuiBangWuLiu/report.do";

	public static void refreshHost() {
		baseUrl = "http://" + ipAddress + ":" + port + "/RuiBangWuLiu/";
		appHost = "http://" + ipAddress + ":" + port + "/RuiBangWuLiu/app.do";
		moveHost = "http://" + ipAddress + ":" + port + "/RuiBangWuLiu/move.do";
		outHost = "http://" + ipAddress + ":" + port + "/RuiBangWuLiu/out.do";
		inHost = "http://" + ipAddress + ":" + port + "/RuiBangWuLiu/in.do";
		reportHost = "http://" + ipAddress + ":" + port + "/RuiBangWuLiu/report.do";
	}

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

	public static SResponse getData(JSONObject jsonParam, String action) {
		HttpClient client = getClient();
		HttpPost post = null;

		try {
			System.out.println("发送数据:" + jsonParam.toJSONString());
			post = new HttpPost(baseUrl + action);
			StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");//解决中文乱码问题
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			post.setEntity(entity);
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			System.out.println("返回数据:" + result);
			SResponse response = JSONObject.parseObject(result, SResponse.class);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return null;
	}

	/*
	public static SResponse getAppData(JSONObject jsonParam) {
		HttpClient client = getClient();
		HttpPost post = null;

		try {
			System.out.println("发送数据:" + jsonParam.toJSONString());
			post = new HttpPost(appHost);
			StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");//解决中文乱码问题
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			post.setEntity(entity);
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			System.out.println("返回数据:" + result);
			SResponse response = JSONObject.parseObject(result, SResponse.class);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return null;
	}

	public static SResponse getMoveData(JSONObject jsonParam) {
		HttpClient client = getClient();
		HttpPost post = null;

		try {
			System.out.println("发送数据:" + jsonParam.toJSONString());
			post = new HttpPost(moveHost);
			StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");//解决中文乱码问题
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			post.setEntity(entity);
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			System.out.println("返回数据:" + result);
			SResponse response = JSONObject.parseObject(result, SResponse.class);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return null;
	}

	public static SResponse getInData(JSONObject jsonParam) {
		HttpClient client = getClient();
		HttpPost post = null;

		try {
			System.out.println("发送数据:" + jsonParam.toJSONString());
			post = new HttpPost(inHost);
			StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");//解决中文乱码问题
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			post.setEntity(entity);
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			System.out.println("返回数据:" + result);
			SResponse response = JSONObject.parseObject(result, SResponse.class);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return null;
	}

	public static SResponse getOutData(JSONObject jsonParam) {
		HttpClient client = getClient();
		HttpPost post = null;

		try {
			System.out.println("发送数据:" + jsonParam.toJSONString());
			post = new HttpPost(outHost);
			StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");//解决中文乱码问题
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			post.setEntity(entity);
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			System.out.println("返回数据:" + result);
			SResponse response = JSONObject.parseObject(result, SResponse.class);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return null;
	}

	public static SResponse getReportData(JSONObject jsonParam) {
		HttpClient client = getClient();
		HttpPost post = null;

		try {
			System.out.println("发送数据:" + jsonParam.toJSONString());
			post = new HttpPost(reportHost);
			StringEntity entity = new StringEntity(jsonParam.toString(),"utf-8");//解决中文乱码问题
			entity.setContentEncoding("UTF-8");
			entity.setContentType("application/json");
			post.setEntity(entity);
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			System.out.println("返回数据:" + result);
			SResponse response = JSONObject.parseObject(result, SResponse.class);
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return null;
	}
	*/
}
