package com.cn.carigps.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.carigps.entity.Account;
import com.cn.carigps.entity.Alarm;
import com.cn.carigps.entity.History;
import com.cn.carigps.entity.Host;
import com.cn.carigps.entity.Location;
import com.cn.carigps.entity.MapPoint;
import com.cn.carigps.entity.MileageOilReport;
import com.cn.carigps.entity.Notice;
import com.cn.carigps.entity.SResponse;
import com.cn.carigps.entity.ServiceExpired;
import com.cn.carigps.entity.Structure;
import com.cn.carigps.entity.Vehicle;

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
	public static String defaultHost = "https://gps.carigps.com/";
	public static String host = "https://gps.carigps.com/";
	public static String serverList = "https://app.carigps.com/server.ashx?username=serveradmin&password=dd38eeb126ff3ecca564bbaa4f89c2be&action=getserverlist";

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


	/** 登录 */
	public static SResponse login(String userName, String password) {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse response = new SResponse();

		try {
			post = new HttpPost(host + "APPInterface/Handler.ashx?Method=UserLogin");
			System.out.println("host:" + host);
			NameValuePair userNameP = new BasicNameValuePair("userName", userName);
			NameValuePair passwordP = new BasicNameValuePair("password", password);
			NameValuePair systemLangP = new BasicNameValuePair("systemLang", systemLang);
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(userNameP);
			nameValuePairs.add(passwordP);
			nameValuePairs.add(systemLangP);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);

			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				response.setCode(SProtocol.SUCCESS);
				/*判断是否为车牌登录(车牌登录没有返回)*/
				if(map.getString("result")!=null){
					Account account = JSON.parseObject(map.getString("result"), Account.class);
					response.setResult(account);
				}
			} else {
				response.setCode(SProtocol.FAIL);
				response.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return response;
	}

	/** 获取车队车辆数据 */
	public static SResponse getVehicleData(String userName) {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse respense = new SResponse();
		try {
			post = new HttpPost(host + "APPInterface/Handler.ashx?Method=GetVehicleData");
			NameValuePair userNameP = new BasicNameValuePair("userName", userName);
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(userNameP);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				respense.setCode(SProtocol.SUCCESS);
				List<Structure> structures = JSON.parseArray(map.getString("result"), Structure.class);
				respense.setResult(structures);
			} else {
				respense.setCode(SProtocol.FAIL);
				respense.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return respense;
	}

	/** 查询车辆信息 */
	public static SResponse getVehicleBySystemNo(String systemNo) {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse respense = new SResponse();

		try {
			post = new HttpPost(host + "APPInterface/Handler.ashx?Method=GetVehicleBySystemNo");
			NameValuePair systemNoP = new BasicNameValuePair("systemNo", systemNo);
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(systemNoP);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				respense.setCode(SProtocol.SUCCESS);
				Vehicle vehicle = JSON.parseObject(map.getString("result"), Vehicle.class);
				respense.setResult(vehicle);
			} else {
				respense.setCode(SProtocol.FAIL);
				respense.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return respense;
	}

	/** 位置信息 */
	public static SResponse getVehicleLocation(String systemNo, String mapType,
                                               String userName, String password) {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse respense = new SResponse();

		try {
			post = new HttpPost(host + "APPInterface/Handler.ashx?Method=GetVehicleLocation");
			NameValuePair systemNoP = new BasicNameValuePair("systemNo", systemNo);
			NameValuePair mapTypeP = new BasicNameValuePair("mapType", mapType);
            NameValuePair userNameP = new BasicNameValuePair("userName", userName);
            NameValuePair passwordP = new BasicNameValuePair("password", password);
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(userNameP);
			nameValuePairs.add(passwordP);
			nameValuePairs.add(systemNoP);
			nameValuePairs.add(mapTypeP);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				respense.setCode(SProtocol.SUCCESS);
				List<Location> locations = JSON.parseArray(map.getString("result"), Location.class);
				respense.setResult(locations);
			} else {
				respense.setCode(SProtocol.FAIL);
				respense.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return respense;
	}

	/**
	 * 获取用户自定义兴趣点
	 * @return
	 */
	public static SResponse getMapPoint() {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse respense = new SResponse();
		try {
			post = new HttpPost(host + "AppInterface/Handler.ashx?Method=MapPoint");
//			post.setEntity(new UrlEncodedFormEntity(null, Consts.UTF_8));
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			System.out.println("getMapPoint:" + result);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				respense.setCode(SProtocol.SUCCESS);
				List<MapPoint> mapPoints = JSON.parseArray(map.getString("result"), MapPoint.class);
				respense.setResult(mapPoints);
			} else {
				respense.setCode(SProtocol.FAIL);
				respense.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return respense;
	}

	/** 告警信息 */
	public static SResponse getAlarmList(String userName, String password, String endTime) {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse response = new SResponse();
		
		try {
			post = new HttpPost(host + "APPInterface/Handler.ashx?Method=GetAlarmList");
            NameValuePair userNameP = new BasicNameValuePair("userName", userName);
            NameValuePair passwordP = new BasicNameValuePair("password", password);
			NameValuePair endTimeP = new BasicNameValuePair("eTime", endTime);
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(userNameP);
			nameValuePairs.add(passwordP);
			nameValuePairs.add(endTimeP);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				response.setCode(SProtocol.SUCCESS);
				List<Alarm> alarms = JSON.parseArray(map.getString("result"), Alarm.class);
				response.setResult(alarms);
			} else {
				response.setCode(SProtocol.FAIL);
				response.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}
		return response;
	}

	/** 车辆指令 */
	public static SResponse vehicleOperational(String systemNo, Integer operate, Integer Value) {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse respense = new SResponse();

		try {
			post = new HttpPost(host + "APPInterface/Handler.ashx?Method=VehicleOperational");
			NameValuePair systemNoP = new BasicNameValuePair("systemNo", systemNo);
			NameValuePair operateP = new BasicNameValuePair("operate", String.valueOf(operate));
			NameValuePair ValueP = new BasicNameValuePair("Value", String.valueOf(Value));
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(systemNoP);
			nameValuePairs.add(operateP);
			nameValuePairs.add(ValueP);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				respense.setCode(SProtocol.SUCCESS);
				respense.setResult(map.getString("result"));
			} else {
				respense.setCode(SProtocol.FAIL);
				respense.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return respense;
	}

	/** 历史数据 */
	public static SResponse getVehHistoryData(String systemNo, String date, String startTime, String endTime, Integer mapType) {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse respense = new SResponse();

		try {
			post = new HttpPost(host + "APPInterface/Handler.ashx?Method=GetVehHistoryData");
			NameValuePair systemNoP = new BasicNameValuePair("systemNo", systemNo);
			NameValuePair dateP = new BasicNameValuePair("date", date);
			NameValuePair startTimeP = new BasicNameValuePair("startTime", startTime);
			NameValuePair endTimeP = new BasicNameValuePair("endTime", endTime);
			NameValuePair mapTypeP = new BasicNameValuePair("mapType", String.valueOf(mapType));
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(systemNoP);
			nameValuePairs.add(dateP);
			nameValuePairs.add(startTimeP);
			nameValuePairs.add(endTimeP);
			nameValuePairs.add(mapTypeP);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				respense.setCode(SProtocol.SUCCESS);
				List<History> historys = JSON.parseArray(map.getString("result"), History.class);
				respense.setResult(historys);
			} else {
				respense.setCode(SProtocol.FAIL);
				respense.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return respense;
	}

	/** 今日里程 */
	public static SResponse vehicleMileage(String systemNo, String startTime, String endTime) {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse response = new SResponse();

		try {
			post = new HttpPost(host + "APPInterface/Handler.ashx?Method=VehicleMileage");
			NameValuePair systemNoP = new BasicNameValuePair("SystemNo", systemNo);
			NameValuePair startTimeP = new BasicNameValuePair("StartTime", startTime);
			NameValuePair endTimeP = new BasicNameValuePair("EndTime", endTime);
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(systemNoP);
			nameValuePairs.add(startTimeP);
			nameValuePairs.add(endTimeP);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			Log.i("vehicleMileage", result);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				response.setCode(SProtocol.SUCCESS);
				List<MileageOilReport> reportList = JSONObject.parseArray(map.getString("result"), MileageOilReport.class);
				response.setResult(reportList);
			} else {
				response.setCode(SProtocol.FAIL);
				response.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return response;
	}

    /**
     * 查询公告（公告信息）
     * @return
     */
	public static SResponse offcialNews() {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse response = new SResponse();
        try {
            post = new HttpPost(host + "AppInterface/Handler.ashx?Method=OffcialNews");
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			System.out.println("offcialNews:" + result);
			JSONObject map = JSON.parseObject(result);
            Boolean res = map.getBoolean("res");
            if (res) {
                response.setCode(SProtocol.SUCCESS);
                List<Notice> notices = JSON.parseArray(map.getString("result"), Notice.class);
				response.setResult(notices);
            } else {
                response.setCode(SProtocol.FAIL);
                response.setMessage(map.getString("desc"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return response;
    }

	/** 地址解析 */
	public static SResponse addressTranslate(Double Longitude, Double Latitude) {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse respense = new SResponse();

		try {
			post = new HttpPost(host + "APPInterface/Handler.ashx?Method=AddressTranslate");
			NameValuePair LongitudeP = new BasicNameValuePair("Longitude", String.valueOf(Longitude));
			NameValuePair LatitudeP = new BasicNameValuePair("Latitude", String.valueOf(Latitude));
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(LongitudeP);
			nameValuePairs.add(LatitudeP);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				respense.setCode(SProtocol.SUCCESS);
				respense.setResult(map.getString("result"));
			} else {
				respense.setCode(SProtocol.FAIL);
				respense.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return respense;
	}

	/** 获取服务器列表 */
	public static SResponse getHostList() {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse response = new SResponse();

		try {
			SSLContext sslContext = SSLContext.getInstance("TLS");
			X509TrustManager manager = new X509TrustManager() {
				public X509Certificate[] getAcceptedIssuers() {
					return null;
				}
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {}
			};
			sslContext.init(null, new TrustManager[]{manager}, null);
			SSLSocketFactory ssf = new SSLSocketFactory(sslContext, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("https", 443, ssf));
			ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(registry);
			client = new DefaultHttpClient(connManager, null);

			post = new HttpPost(serverList);
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
//			System.out.println("getHostList:" + result);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				response.setCode(SProtocol.SUCCESS);
				List<Host> hosts = JSON.parseArray(map.getString("data"), Host.class);
				response.setResult(hosts);
			} else {
				response.setCode(SProtocol.FAIL);
				response.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return response;
	}

	/**油量查询*/
	public static SResponse getOilReport(String systemNo, String startTime, String endTime) {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse response = new SResponse();
		try {
			post = new HttpPost(host + "APPInterface/Handler.ashx");
			NameValuePair methodP = new BasicNameValuePair("Method", "FuelConsumption");
			NameValuePair systemNoP = new BasicNameValuePair("SystemNo", systemNo);
			NameValuePair startTimeP = new BasicNameValuePair("StartTime", startTime);
			NameValuePair endTimeP = new BasicNameValuePair("EndTime", endTime);
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(methodP);
			nameValuePairs.add(systemNoP);
			nameValuePairs.add(startTimeP);
			nameValuePairs.add(endTimeP);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				response.setCode(SProtocol.SUCCESS);
				List<MileageOilReport> reportList = JSON.parseArray(map.getString("result"), MileageOilReport.class);
				response.setResult(reportList);
			} else {
				response.setCode(SProtocol.FAIL);
				response.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return response;
	}

	/** 意见反馈 */
	public static SResponse AddFeedback(String FeedbackName,String FeedbackContent) {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse respense = new SResponse();

		try {
			post = new HttpPost(host + "APPInterface/Handler.ashx?Method=AddFeedback");
			NameValuePair nameP = new BasicNameValuePair("FeedbackName", FeedbackName);
			NameValuePair contentP = new BasicNameValuePair("FeedbackContent", FeedbackContent);
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(nameP);
			nameValuePairs.add(contentP);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				respense.setCode(SProtocol.SUCCESS);
				respense.setResult(map.getString("result"));
			} else {
				respense.setCode(SProtocol.FAIL);
				respense.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return respense;
	}

	/** 添加电子围栏 */
	public static SResponse AddFenceInfo(String systemNo,String CenterPoint,String Radius) {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse respense = new SResponse();

		try {
			post = new HttpPost(host + "APPInterface/Handler.ashx?Method=AddFenceInfo");
			NameValuePair systemNoP = new BasicNameValuePair("systemNo", systemNo);
			NameValuePair CenterPointP = new BasicNameValuePair("CenterPoint", CenterPoint);
			NameValuePair RadiusP = new BasicNameValuePair("Radius", Radius);
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(systemNoP);
			nameValuePairs.add(CenterPointP);
			nameValuePairs.add(RadiusP);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				respense.setCode(SProtocol.SUCCESS);
				respense.setResult(map.getString("result"));
			} else {
				respense.setCode(SProtocol.FAIL);
				respense.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return respense;
	}

	/**
	 * 获取服务器当前时间
	 * @return
	 */
	public static SResponse getServerTime() {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse response = new SResponse();
		try {
			post = new HttpPost(host + "APPInterface/Handler.ashx?Method=GetSystemTime");
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				response.setCode(SProtocol.SUCCESS);
				response.setResult(map.getString("result"));
			} else {
				response.setCode(SProtocol.FAIL);
				response.setMessage(map.getString("result"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return response;
	}

	public static SResponse getServiceExpiredList(String userName, String password) {
		HttpClient client = getClient();
		HttpPost post = null;
		SResponse response = new SResponse();
		try {
			post = new HttpPost(host + "AppInterface/Handler.ashx?Method=RunoutVeh");
			NameValuePair userNameP = new BasicNameValuePair("userName", userName);
			NameValuePair passwordP = new BasicNameValuePair("password", password);
			List<NameValuePair> nameValuePairs = new ArrayList<>();
			nameValuePairs.add(userNameP);
			nameValuePairs.add(passwordP);
			post.setEntity(new UrlEncodedFormEntity(nameValuePairs, Consts.UTF_8));
			String result = EntityUtils.toString(client.execute(post).getEntity(), Consts.UTF_8);
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				response.setCode(SProtocol.SUCCESS);
				List<ServiceExpired> expireds = JSON.parseArray(map.getString("result"), ServiceExpired.class);
				response.setResult(expireds);
			} else {
				response.setCode(SProtocol.FAIL);
				response.setMessage(map.getString("result"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			post.releaseConnection();
		}

		return response;
	}


}
