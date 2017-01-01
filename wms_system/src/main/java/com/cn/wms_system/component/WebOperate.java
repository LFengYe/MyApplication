package com.cn.wms_system.component;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpResponseException;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;


import android.os.AsyncTask;
import android.os.Handler;

public class WebOperate {

	private static String webServiceURL;//webService地址
	private static String nameSpace;//命名空间
	private static String methodName;//方法名
	private Map<String, String> params;//参数列表
	private String result;//服务器返回的结果
	private boolean dataIsReady;//服务器结果返回完毕
	
	public Handler myHandler;
	public WebOperate(Handler handler) {
		this.myHandler = handler;
	}
	/**
	 * 调用WebService
	 * 
	 * @return WebService的返回值
	 * 
	 */
	public String CallWebService(String MethodName, Map<String, String> Params) {
		boolean haveNotParams = true;//判断是否有输入参数
		
		// 1、指定webservice的命名空间和调用的方法名
		SoapObject request = new SoapObject(Constants.nameSpace, MethodName);
		
		// 2、设置调用方法的参数值，如果没有参数，可以省略，
		if (Params != null) {
			haveNotParams = false;
			Iterator<Entry<String, String>> iter = Params.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<String, String> entry = iter.next();
//				Log.e("Key:", entry.getKey());
//				Log.e("Value", entry.getValue());
				request.addProperty((String) entry.getKey(),
						(String) entry.getValue());
			}
		}

		// 3、生成调用Webservice方法的SOAP请求信息。该信息由SoapSerializationEnvelope对象描述
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER12);
		envelope.bodyOut = request;
		// c#写的应用程序必须加上这句
		envelope.dotNet = true;
		System.out.println("url:" + Constants.webServiceURL);
		HttpTransportSE ht = new HttpTransportSE(Constants.webServiceURL);
		
		// 使用call方法调用WebService方法
		try {
			ht.call(Constants.nameSpace + MethodName, envelope);
		} catch (HttpResponseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		}
		
		/**
		 * 判断是否有输入参数，进行不同的处理
		 */
		if (haveNotParams) {
			SoapObject object = (SoapObject) envelope.bodyIn;
			if (object != null) {
//				Log.e("---收到的回复---", object.toString());
				return object.toString();
			}
		} else {
			try {
				SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
				if (result != null) {
//					Log.e("----收到的回复----", result.toString());
					return result.toString();
				}
			} catch (SoapFault e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	/**
	 * 执行异步任务
	 * 
	 * @param params
	 *            方法名+参数列表（哈希表形式）
	 */
	public void Request(Object... params) {
		new AsyncTask<Object, Object, String>() {

			@SuppressWarnings("unchecked")
			@Override
			protected String doInBackground(Object... params) {
				if (params != null && params.length == 2) {
					
					return CallWebService((String) params[0],
							((Map<String, String>) params[1]));
				} else if (params != null && params.length == 1) {
					return CallWebService((String) params[0], null);
				} else {
					return null;
				}
			}

			protected void onPostExecute(String result) {
				if (result != null) {
					System.out.println(result);
					setResult(result);
					myHandler.sendEmptyMessage(Constants.GET_PLAN_LIST_MESSAGE);
					return ;
				}
				myHandler.sendEmptyMessage(Constants.GET_MESSAGE_IS_EMPTY);
			};

		}.execute(params);
	}

	/**
	 * @return the webServiceURL
	 */
	public String getWebServiceURL() {
		return WebOperate.webServiceURL;
	}

	/**
	 * @param webServiceURL the webServiceURL to set
	 */
	public static void setWebServiceURL(String webServiceURL) {
		WebOperate.webServiceURL = webServiceURL;
	}

	/**
	 * @return the namespace
	 */
	public String getNamespace() {
		return WebOperate.nameSpace;
	}

	/**
	 * @param namespace the namespace to set
	 */
	public void setNamespace(String namespace) {
		WebOperate.nameSpace = namespace;
	}

	/**
	 * @return the result
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param result the result to set
	 */
	public void setResult(String result) {
		this.result = result;
	}

	/**
	 * @return the dataIsReady
	 */
	public boolean isDataIsReady() {
		return dataIsReady;
	}

	/**
	 * @param dataIsReady the dataIsReady to set
	 */
	public void setDataIsReady(boolean dataIsReady) {
		this.dataIsReady = dataIsReady;
	}

	/**
	 * @return the params
	 */
	public Map<String, String> getParams() {
		return params;
	}

	/**
	 * @param params the params to set
	 */
	public void setParams(Map<String, String> params) {
		this.params = params;
	}

	/**
	 * @return the methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(String methodName) {
		WebOperate.methodName = methodName;
	}

}
