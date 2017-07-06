package com.cxgps.vehicle.api;

import com.cxgps.vehicle.AppContext;


/**
 * Created by cxiosapp on 16/2/15.
 */
public class URLs {

    public static final int PAZE_SIZE = 5;

    private String API_VERSION = "/apitest/api/v1/iosapp/appindex.ashx";// API版本
    private String HTTPS = "http://";

    private String URL_SPLITTER = "?";

    public static String METHOD = "Method";
    public final static String PARAMS = "Params";
    public final static String Language = "Language";

    public final  static String MapType = "Maptype";

    private static URLs urlInstance;

//    public static String getUrlInstance() {
//        if (null == urlInstance) {
//            urlInstance = new URLs();
//        }
//        return urlInstance.HTTPS + AppContext.getInstance().getAddress()
//                + urlInstance.API_VERSION + urlInstance.URL_SPLITTER;
//    }


    public static String getBaseurl() {
        if (null == urlInstance) {
            urlInstance = new URLs();
        }

      String partUrl =   AppContext.getInstance().getAddress();

        return urlInstance.HTTPS +partUrl
                + urlInstance.API_VERSION;

    }

    /*****************************接口方法*********************************/


    public static final String GET_USER_LOGIN = "get_user_Login";

    public static final String GET_CAR_LIST = "get_car_List";

    public static final String GET_CAR_TEAM = "get_car_Team";

    public static final String GET_CAR_LOCATION = "get_car_Location";

    public static final String GET_CAR_OPERATE = "get_car_Operate";

    public static final String GET_CAR_SEARCH = "get_car_Search";

    public static final String GET_CAR_HISTORY = "get_car_History";

    public static final String GET_APP_UPDATE = "get_app_update";

    public static final String GET_COMMON_DETAIL = "get_common_detail";

    public static final String GET_APP_LOG = "get_app_log";

    public static final String GET_PARSE_LATLNG = "get_address_Parse";


    public static final String GET_MESSAGE_LIST = "get_AlarmList";

}
