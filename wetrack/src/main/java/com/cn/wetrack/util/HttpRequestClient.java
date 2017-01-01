package com.cn.wetrack.util;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cn.wetrack.entity.Account;
import com.cn.wetrack.entity.Alarm;
import com.cn.wetrack.entity.History;
import com.cn.wetrack.entity.Host;
import com.cn.wetrack.entity.Location;
import com.cn.wetrack.entity.MapPoint;
import com.cn.wetrack.entity.MileageOilReport;
import com.cn.wetrack.entity.Notice;
import com.cn.wetrack.entity.SResponse;
import com.cn.wetrack.entity.ServiceExpired;
import com.cn.wetrack.entity.Structure;
import com.cn.wetrack.entity.Vehicle;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

//import android.util.Log;
/**
 * 访问工具类
 */
public class HttpRequestClient {
	private static HttpClient client = null;
	/*默认服务器*/
	public static String  defaultHost="http://103.6.244.67/";
	public static String host="http://103.6.244.67/";
	/* 语言 */
	public static String systemLang = "zh-cn";

	/** 单例，维持一个session */
	public static HttpClient getClient() {
		if (client == null) {
			client = new HttpClient(new MultiThreadedHttpConnectionManager());
			client.getHttpConnectionManager().getParams().setConnectionTimeout(60 * 1000);
			client.getHttpConnectionManager().getParams().setSoTimeout(60 * 1000);
			client.getParams().setContentCharset("UTF-8");
		}
		return client;
	}

	/** 登录 */
	public static SResponse login(String userName, String password) {
		HttpClient client = getClient();
		PostMethod post = null;
		SResponse respense = new SResponse();

		try {
			post = new PostMethod(host + "APPInterface/Handler.ashx?Method=UserLogin");
			NameValuePair userNameP = new NameValuePair("userName", userName);
			NameValuePair passwordP = new NameValuePair("password", password);
			NameValuePair systemLangP = new NameValuePair("systemLang", systemLang);
			post.setRequestBody(new NameValuePair[] {userNameP, passwordP,systemLangP });
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				respense.setCode(SProtocol.SUCCESS);
				/*判断是否为车牌登录(车牌登录没有返回)*/
				if(map.getString("result")!=null){
					Account account = JSON.parseObject(map.getString("result"), Account.class);
					respense.setResult(account);
				}
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

	/** 获取车队车辆数据 */
	public static SResponse getVehicleData(String userName) {
		HttpClient client = getClient();
		PostMethod post = null;
		SResponse respense = new SResponse();
		try {
			post = new PostMethod(host + "APPInterface/Handler.ashx?Method=GetVehicleData");
			NameValuePair userNameP = new NameValuePair("userName", userName);
			post.setRequestBody(new NameValuePair[]{userNameP});
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
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
		PostMethod post = null;
		SResponse respense = new SResponse();

		try {
			post = new PostMethod(host + "APPInterface/Handler.ashx?Method=GetVehicleBySystemNo");
			NameValuePair systemNoP = new NameValuePair("systemNo", systemNo);
			post.setRequestBody(new NameValuePair[] { systemNoP });
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
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
		PostMethod post = null;
		SResponse respense = new SResponse();

		try {
			post = new PostMethod(host + "APPInterface/Handler.ashx?Method=GetVehicleLocation");
			NameValuePair systemNoP = new NameValuePair("systemNo", systemNo);
			NameValuePair mapTypeP = new NameValuePair("mapType", mapType);
            NameValuePair userNameP = new NameValuePair("userName", userName);
            NameValuePair passwordP = new NameValuePair("password", password);
            post.setRequestBody(new NameValuePair[] { systemNoP, mapTypeP, userNameP, passwordP });
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
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
		PostMethod post = null;
		SResponse respense = new SResponse();

		try {
			post = new PostMethod(host + "AppInterface/Handler.ashx?Method=MapPoint");
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
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
		PostMethod post = null;
		SResponse response = new SResponse();
		
		try {
			post = new PostMethod(host + "APPInterface/Handler.ashx?Method=GetAlarmList");
            NameValuePair userNameP = new NameValuePair("userName", userName);
            NameValuePair passwordP = new NameValuePair("password", password);
			NameValuePair endTimeP = new NameValuePair("eTime", endTime);
            post.setRequestBody(new NameValuePair[]{userNameP, passwordP, endTimeP});
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
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
		PostMethod post = null;
		SResponse respense = new SResponse();

		try {
			post = new PostMethod(host + "APPInterface/Handler.ashx?Method=VehicleOperational");
			NameValuePair systemNoP = new NameValuePair("systemNo", systemNo);
			NameValuePair operateP = new NameValuePair("operate", String.valueOf(operate));
			NameValuePair ValueP = new NameValuePair("Value", String.valueOf(Value));
			post.setRequestBody(new NameValuePair[]{systemNoP, operateP, ValueP });
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
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
		PostMethod post = null;
		SResponse respense = new SResponse();

		try {
			post = new PostMethod(host + "APPInterface/Handler.ashx?Method=GetVehHistoryData");
			NameValuePair systemNoP = new NameValuePair("systemNo", systemNo);
			NameValuePair dateP = new NameValuePair("date", date);
			NameValuePair startTimeP = new NameValuePair("startTime", startTime);
			NameValuePair endTimeP = new NameValuePair("endTime", endTime);
			NameValuePair mapTypeP = new NameValuePair("mapType", String.valueOf(mapType));
			post.setRequestBody(new NameValuePair[] { systemNoP, dateP, startTimeP, endTimeP, mapTypeP });
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
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
		PostMethod post = null;
		SResponse response = new SResponse();

		try {
			post = new PostMethod(host + "APPInterface/Handler.ashx?Method=VehicleMileage");
			NameValuePair systemNoP = new NameValuePair("SystemNo", systemNo);
			NameValuePair startTimeP = new NameValuePair("StartTime", startTime);
			NameValuePair endTimeP = new NameValuePair("EndTime", endTime);
			post.setRequestBody(new NameValuePair[] { systemNoP, startTimeP, endTimeP });
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
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
		PostMethod post = null;
		SResponse response = new SResponse();
        try {
            post = new PostMethod(host + "AppInterface/Handler.ashx?Method=OffcialNews");
            client.executeMethod(post);
            String result = post.getResponseBodyAsString();
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
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            post.releaseConnection();
        }
        return response;
    }

	/** 地址解析 */
	public static SResponse addressTranslate(Double Longitude, Double Latitude) {
		HttpClient client = getClient();
		PostMethod post = null;
		SResponse respense = new SResponse();

		try {
			post = new PostMethod(host + "APPInterface/Handler.ashx?Method=AddressTranslate");
			NameValuePair LongitudeP = new NameValuePair("Longitude", String.valueOf(Longitude));
			NameValuePair LatitudeP = new NameValuePair("Latitude", String.valueOf(Latitude));
			post.setRequestBody(new NameValuePair[] {LongitudeP, LatitudeP });
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
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
	
	/** 心跳接口 */
	public static SResponse keepAlive() {
		HttpClient client = getClient();
		GetMethod get = null;
		SResponse respense = new SResponse();

		try {
			get = new GetMethod(host + "APPInterface/Handler.ashx?Method=SessionHeartbeat");
			client.executeMethod(get);
			String result = get.getResponseBodyAsString();
			JSONObject map = JSON.parseObject(result);
			Boolean res = map.getBoolean("res");
			if (res) {
				respense.setCode(SProtocol.SUCCESS);
			} else {
				respense.setCode(SProtocol.FAIL);
				respense.setMessage(map.getString("desc"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			get.releaseConnection();
		}

		return respense;
	}

	/** 获取服务器列表 */
	public static SResponse getHostList() {
		HttpClient client = getClient();
		PostMethod post = null;
		SResponse response = new SResponse();

		try {
			post = new PostMethod("http://apps.carigps.com/server.ashx?username=serveradmin&password=dd38eeb126ff3ecca564bbaa4f89c2be&action=getserverlist");
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
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

	/**获取更新信息*/
	public static int getVersion() {
		GetMethod get = null;
		int result=0;
		try {
			get = new GetMethod(defaultHost + "version/android/version.xml");
			getClient().executeMethod(get);
			InputStream version = get.getResponseBodyAsStream();
			SAXBuilder builder = new SAXBuilder(false);
			Document doc = builder.build(version);
			Element root = doc.getRootElement();
			Element versionString = root.getChild("v");
			result=Integer.parseInt(versionString.getValue());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			get.releaseConnection();
		}
		return result;
	}

	/**油量查询*/
	public static SResponse getOilReport(String systemNo, String startTime, String endTime) {
		HttpClient client = getClient();
		GetMethod getMethod = null;
		SResponse response = new SResponse();
		try {
			getMethod = new GetMethod(host + "APPInterface/Handler.ashx");
//			post = new PostMethod(host + "APPInterface/Handler.ashx");
			NameValuePair methodP = new NameValuePair("Method", "FuelConsumption");
			NameValuePair systemNoP = new NameValuePair("SystemNo", systemNo);
			NameValuePair startTimeP = new NameValuePair("StartTime", startTime);
			NameValuePair endTimeP = new NameValuePair("EndTime", endTime);
			getMethod.setQueryString(new NameValuePair[]{methodP, systemNoP, startTimeP, endTimeP});
//			post.setRequestBody(new NameValuePair[] {methodP, systemNoP, startTimeP, endTimeP});
			client.executeMethod(getMethod);
			String result = getMethod.getResponseBodyAsString();
//			Log.i("getOilReport", result);
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
			getMethod.releaseConnection();
//			post.releaseConnection();
		}

		return response;
	}

	/** 意见反馈 */
	public static SResponse AddFeedback(String FeedbackName,String FeedbackContent) {
		HttpClient client = getClient();
		PostMethod post = null;
		SResponse respense = new SResponse();

		try {
			post = new PostMethod(host + "APPInterface/Handler.ashx?Method=AddFeedback");
			NameValuePair nameP = new NameValuePair("FeedbackName", FeedbackName);
			NameValuePair contentP = new NameValuePair("FeedbackContent", FeedbackContent);
			post.setRequestBody(new NameValuePair[]{nameP, contentP});
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
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
		PostMethod post = null;
		SResponse respense = new SResponse();

		try {
			post = new PostMethod(host + "APPInterface/Handler.ashx?Method=AddFenceInfo");
			NameValuePair systemNoP = new NameValuePair("systemNo", systemNo);
			NameValuePair CenterPointP = new NameValuePair("CenterPoint", CenterPoint);
			NameValuePair RadiusP = new NameValuePair("Radius", Radius);
			post.setRequestBody(new NameValuePair[]{systemNoP, CenterPointP, RadiusP});
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
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
		PostMethod post = null;
		SResponse response = new SResponse();
		try {
			post = new PostMethod(host + "APPInterface/Handler.ashx?Method=GetSystemTime");
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
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
		PostMethod post = null;
		SResponse response = new SResponse();
		try {
			post = new PostMethod(host + "AppInterface/Handler.ashx?Method=RunoutVeh");
			NameValuePair userNameP = new NameValuePair("userName", userName);
			NameValuePair passwordP = new NameValuePair("password", password);
			post.setRequestBody(new NameValuePair[] {userNameP, passwordP });
			client.executeMethod(post);
			String result = post.getResponseBodyAsString();
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
