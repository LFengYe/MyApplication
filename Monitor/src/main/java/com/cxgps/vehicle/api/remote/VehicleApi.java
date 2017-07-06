package com.cxgps.vehicle.api.remote;

import android.util.Base64;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cxgps.vehicle.AppConfig;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.api.AsyncHttpHelp;
import com.cxgps.vehicle.api.URLs;
import com.cxgps.vehicle.utils.TDevice;
import com.kymjs.rxvolley.client.HttpCallback;
import com.kymjs.rxvolley.client.HttpParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by taosong on 16/12/22.
 */

public class VehicleApi {

    public final static String TAG = VehicleApi.class.getName();

    // 将数据进行转码
    protected static String strToBase64(String str) {
        String result = "";
        try {

            result = Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);

        } catch (Exception e) {


            return result;
        }
        return result;
    }

    // 将数据进行解码
    protected static byte[] base64ToStr(String str) {
        byte result[] = null;
        try {

            result = Base64.decode(str, Base64.NO_WRAP);

        } catch (Exception e) {


            return result;
        }
        return result;
    }

    /**
     * 获取JSON
     * <p/>
     * params "[键名]", "[键值]", ...
     *
     * @throws Exception
     */
    protected static String GetParams(Object... params) {

        String jsonResult = null;
        Map<String, Object> maps = new HashMap<String, Object>();
        try {

            for (int i = 0; i < params.length; i++) {

                maps.put(params[i].toString(), params[i + 1]);
                i++;
            }

            jsonResult = JSON.toJSONString(maps);
        } catch (Exception e) {
            e.printStackTrace();

        }
        Log.d("========GetParams=====", jsonResult);

        return jsonResult;
    }

    public static HttpParams postParams(String method, String param){
        HttpParams  params = new HttpParams();
        params.put(URLs.METHOD, method);
        params.put(URLs.PARAMS, param);
        params.put(URLs.Language, strToBase64(AppContext.getLanguageWithKey()));
        params.put(URLs.MapType,strToBase64(AppContext.getMapWithKey()));

        return  params;
    }





    /**
     * (1)登陆
     *
     * @param username
     * @param password

     */
    public static void login(int logintype, String username, String password,
                             HttpCallback requestListener) {

        try {
            String Params = GetParams("loginType", logintype, "userName",
                    username, "userPwd", password);


            String params = strToBase64(Params);
            String method = strToBase64(URLs.GET_USER_LOGIN.trim());

            HttpParams  httpParams =   postParams(method, params);

            Log.i("TAG====URL==",URLs.getBaseurl());

            AsyncHttpHelp.post(URLs.getBaseurl(),httpParams,requestListener);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * (2) 检查版本更新

     */

    public static void checkUpdate(HttpCallback requestListener){


        try {
            int oldVersionCode = TDevice.getVersionCode(AppContext
                    .getInstance().getPackageName());
            String packageName = AppContext
                    .getInstance().getPackageName();
            String Params = GetParams("oldVersionCode",oldVersionCode, "packageName", packageName);

            String params = strToBase64(Params);
            String method = strToBase64(URLs.GET_APP_UPDATE.trim());

            HttpParams  params1 =   postParams(method, params);

            AsyncHttpHelp.post(URLs.getBaseurl(), params1, requestListener);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }




    /**
     * (3) 上传bug
     * @param data
     */
    public static void uploadLog(String data, HttpCallback  requestListener){

        uploadLog(data,"1",requestListener);


    }


    /**
     * (4) 意见反馈
     * @param data
     */
    public static void feedback(String data, HttpCallback  requestListener){

        uploadLog(data,"2",requestListener);


    }

    private static void uploadLog(String data, String catalog, HttpCallback  requestListener){


        try {
            String Params = GetParams("data",data,"catalog",catalog);

            String params = strToBase64(Params);
            String method = strToBase64(URLs.GET_APP_LOG.trim());

            HttpParams  params1 =   postParams(method, params);

            AsyncHttpHelp.post( URLs.getBaseurl(), params1, requestListener);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }






    /**
     * (5) 获取更多信息配置

     */

    public static void  requestCommonDetail(int type, String systemNoList, String startTime, String endTime, HttpCallback  requestListener){

        ArrayList<String> list = new ArrayList<>();

        list.add(systemNoList);


        try {
            String Params = "";

            switch (type){
                case AppConfig.SHARE:
                case AppConfig.ABOUT:
                case AppConfig.GUIDE:
                case AppConfig.WEIZHANG:
                    Params  = GetParams("type",type);
                    break;
                case AppConfig.REPORT:
                    Params  = GetParams("type",type,"systemNoList",list,"startTime",startTime,"endTime",endTime);
            }



            String params = strToBase64(Params);
            String method = strToBase64(URLs.GET_COMMON_DETAIL.trim());


            HttpParams  params1 =   postParams(method, params);

            AsyncHttpHelp.post( URLs.getBaseurl(), params1, requestListener);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }






    /**(6)
     * 获取车辆列表
     * @param userName
     * @param loginType
     * @param pageIndex
     * @param catalog  (0 表示获取车队)
     */

    public static void getCarList(String userName, int loginType, int pageIndex, int catalog, HttpCallback  requestListener){


        try {
            String Params = GetParams("userName", userName, "loginType", loginType, "pageIndex", pageIndex, "catalog", catalog);

            String params = strToBase64(Params);
            String method = strToBase64(URLs.GET_CAR_LIST.trim());


            HttpParams  params1 =   postParams(method, params);
            AsyncHttpHelp.post( URLs.getBaseurl(), params1, requestListener);

        } catch (Exception e) {
            e.printStackTrace();
        }



    }


    /**
     * (7) 获取报警消息
     * @param username
     * @param lasttime
     * @param SystemNo
     * @param requestListener
     */

    public static  void getMessageList(String username, String lasttime, String SystemNo, HttpCallback  requestListener){



        try {
            String Params = GetParams("UserName",username,"EndTime",lasttime,"SystemNo",SystemNo);

            String params = strToBase64(Params);
            String method = strToBase64(URLs.GET_MESSAGE_LIST.trim());

            HttpParams  params1 =   postParams(method, params);

            AsyncHttpHelp.post(URLs.getBaseurl(), params1, requestListener);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static  void getMessageList(String username, String lasttime, HttpCallback  requestListener){



        getMessageList(username,lasttime,"",requestListener);

    }




    /**
     * (10) 搜索车辆
     * @param userName
     * @param loginType
     * @param carContent
     * @param pageIndex

     */


    public static void getCarSearch(String userName, int loginType, String carContent, int pageIndex, HttpCallback  requestListener){


        String requestUrl = "url error";
        try {
            String Params = GetParams("userName",userName,"loginType",loginType,"carContent",carContent,"pageIndex",pageIndex);

            String params = strToBase64(Params);
            String method = strToBase64(URLs.GET_CAR_SEARCH.trim());


            HttpParams  params1 =   postParams(method, params);

            AsyncHttpHelp.post(URLs.getBaseurl(), params1, requestListener);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * (11) 解析地址位置
     * @param lat
     * @param lng
     * @param requestListener
     */
    public  static void parseAddressByLatlng(String lat,String lng, HttpCallback  requestListener){


        try {
            String Params = GetParams("lat",lat,"lng",lng);

            String params = strToBase64(Params);
            String method = strToBase64(URLs.GET_PARSE_LATLNG.trim());

            HttpParams  params1 =   postParams(method, params);

            AsyncHttpHelp.post(URLs.getBaseurl(), params1, requestListener);

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * (12) 获取车辆位置信息
     * @param systemNo
     * @param requestListener
     */

    public static void getCarLocations(List<String> systemNo, HttpCallback  requestListener){
        try {
            String Params = GetParams("systemNo",systemNo);
            String params = strToBase64(Params);
            String method = strToBase64(URLs.GET_CAR_LOCATION.trim());
            HttpParams  params1 =   postParams(method, params);
            AsyncHttpHelp.post(URLs.getBaseurl(), params1, requestListener);
        } catch (Exception e) {
            e.printStackTrace();
        }




    }



    /**
     * (13) 获取车辆历史轨迹
     * @param systemNo
     * @param startTime
     * @param endTime
     * @param pageIndex

     */

    public static void getCarHistory(String systemNo,String startTime,String endTime,int pageIndex,HttpCallback  requestListener){



        try {
            String Params = GetParams("systemNo",systemNo,"startTime",startTime,"endTime",endTime,"pageIndex",pageIndex);

            String params = strToBase64(Params);
            String method = strToBase64(URLs.GET_CAR_HISTORY.trim());
            HttpParams  params1 =   postParams(method, params);
            AsyncHttpHelp.post(URLs.getBaseurl(), params1, requestListener);

        } catch (Exception e) {
            e.printStackTrace();
        }



    }


}
