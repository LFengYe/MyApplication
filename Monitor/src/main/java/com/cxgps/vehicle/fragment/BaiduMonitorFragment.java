package com.cxgps.vehicle.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.cxgps.vehicle.AppConfig;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.api.remote.VehicleApi;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.AddressInfoBean;
import com.cxgps.vehicle.bean.CarLocationBean;
import com.cxgps.vehicle.bean.CarsInfo;
import com.cxgps.vehicle.bean.ResponseBean;
import com.cxgps.vehicle.bean.SimpleBackPage;
import com.cxgps.vehicle.bean.UserInfo;
import com.cxgps.vehicle.broadcasts.SDKReceiver;
import com.cxgps.vehicle.interf.IMonitorInter;
import com.cxgps.vehicle.utils.DialogHelper;
import com.cxgps.vehicle.utils.StringUtils;
import com.cxgps.vehicle.utils.TLog;
import com.cxgps.vehicle.utils.UIHelper;
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * 车辆监控界面
 *
 * 思路：
 * （1）获取车辆列表
 * （2）保存车辆的系统编号
 * （3）通过系统编号去获取车辆位置信息
 * （4）将获取下来的位置信息添加到地图上
 *  (5) 开启定时器去刷新位置信息
 * Created by taosong on 16/12/29.
 */

public class BaiduMonitorFragment extends BaseFragment implements BaiduMap.OnMarkerClickListener,BaiduMap.OnMapClickListener,IMonitorInter {


    private MapView mMapView;

    private BaiduMap mBaiduMap;

    private InfoWindow mBaduInfoWindow;

    private Marker nowMarker;

    private   int markerIndex = 0 ;

    private Handler dataHandler = new Handler();

    private String nowAddress;

    private int loginResultStatus;

    private ArrayList<Marker> markerPointer = new ArrayList<>();

    private SDKReceiver mReceiver;

    private  boolean isToGetLocation = false;

    // 检测地图
    private  void checkBaiduMapView(Context context){

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        intentFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        intentFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);

        if (mReceiver == null ){
            mReceiver = new SDKReceiver();
            context.registerReceiver(mReceiver, intentFilter);

        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        checkBaiduMapView(getActivity());
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return  mMapView;
    }

    @Override
    public void initView(View view) {

        super.initView(view);

        mMapView = new MapView(getActivity(),new BaiduMapOptions());
        mBaiduMap = mMapView.getMap();

        MapStatus ms = new MapStatus.Builder().overlook(-30).build();

        MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);

        mBaiduMap.animateMapStatus(u,500);

        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

        UiSettings mSettings = mBaiduMap.getUiSettings();

        mSettings.setZoomGesturesEnabled(true);
        mSettings.setScrollGesturesEnabled(true);
        mSettings.setRotateGesturesEnabled(true);
        mSettings.setOverlookingGesturesEnabled(true);
        mSettings.setCompassEnabled(true);
        mBaiduMap.showMapPoi(true);

        mBaiduMap.setOnMarkerClickListener(this);
        mBaiduMap.setOnMapClickListener(this);

    }

    @Override
    public void initData() {

        loginResultStatus = getArguments().getInt("loginResultState");
        dataHandler.post(new DataHandler(-1));
    }



    @Override
    public boolean changeRoad() {

        if( mBaiduMap.isTrafficEnabled()){

            mBaiduMap.setTrafficEnabled(false);
            AppContext.showToast(R.string.map_traffic_close);
        }else {
            mBaiduMap.setTrafficEnabled(true);
            AppContext.showToast(R.string.map_traffic_open);

        }

        return mBaiduMap.isTrafficEnabled();



    }

    boolean  isChange = false;
    @Override
    public boolean changeMaptype() {

        if (isChange){
            isChange = false;
            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        }else {

            mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
            isChange = true;
        }

        return isChange;
    }

    @Override
    public void changeLocation(int type) {

        switch (type){

            case AppConfig.USER_LOGIN_TAG:

                stopDataTimer();

                startLocation();

                break;

            case AppConfig.CARNUMBER_LOGIN_TAG:

                nowMarker = markerPointer.get(markerIndex);
                onMarkerClick(nowMarker);
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(nowMarker.getPosition(), 18);

                mBaiduMap.animateMapStatus(u);

                startDataTimmer();

                stopLocation();
                break;



        }




    }

    @Override
    public void changeBefore() {
        int markerCount = markerPointer.size();

        if (markerCount < 2){

            return;

        }
        if (markerIndex <markerCount-1 && markerIndex!=0){

            markerIndex --;

        }else {

            markerIndex  = 0;

        }
        nowMarker = markerPointer.get(markerIndex);
        onMarkerClick(nowMarker);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(nowMarker.getPosition(), 18);

        mBaiduMap.animateMapStatus(u);
    }

    @Override
    public void changeAfter() {
        int markerCount = markerPointer.size();

        if (markerCount < 2){

            return;

        }
        if (markerIndex <markerCount-1){

            markerIndex ++;

        }else {

            markerIndex  = 0;

        }
        nowMarker = markerPointer.get(markerIndex);
        onMarkerClick(nowMarker);
        MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(nowMarker.getPosition(), 18);
        mBaiduMap.animateMapStatus(u);
    }

    @Override
    public boolean changeAdd() {
        return false;
    }

    @Override
    public boolean changeReduct() {
        return false;
    }

    @Override
    public void reflushLocation(String systemNo) {

        if (isAddPointer(systemNo)){
            nowMarker = markerPointer.get(markerIndex);
            onMarkerClick(nowMarker);
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(nowMarker.getPosition(), 18);
            mBaiduMap.animateMapStatus(u);


        }else {


            ArrayList<String>  systemNoList =  AppContext.getInstance().getCarSystemNo();

            systemNoList.add(systemNo);
            markerIndex = systemNoList.size()-1;
            dataHandler.post(new DataHandler(2));


        }

    }


    private  boolean isAddPointer(String systemNo) {

        boolean isFindFlag = false;

        ArrayList<String> systemNoList = AppContext.getInstance().getCarSystemNo();


        for (String f : systemNoList) {


            if (f.equals(systemNo)) {
                isFindFlag = true;
                markerIndex = systemNoList.indexOf(f);
                break;

            } else {
                isFindFlag = false;
            }
        }

        return isFindFlag;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        mBaiduMap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi mapPoi) {
        return false;
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        nowMarker = marker;
        markerIndex = nowMarker.getExtraInfo().getInt("tempIndex");
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_marker_pop, null);

        dataHandler.post(new DataHandler(1,view));
        return false;
    }


    /**************************加载数据****start**********************************/
    private void loadCarsList(){

        showWaitDialog();

        UserInfo userInfo =  AppContext.getInstance().getLoginUser();

        if (Boolean.valueOf(userInfo.getuLoginFlag())){

            VehicleApi.getCarList(userInfo.getUserName(), userInfo.getuLoginType(), 0, 6, carsHandler);

        }
    }

    private HttpCallback  carsHandler = new HttpCallback() {
        @Override
        public void onSuccess(Map<String, String> headers, byte[] t) {
            super.onSuccess(headers, t);

            hideWaitDialog();

            try {
                Log.i("TAG","=====baidu====cars======data======"+new String(t));
                ResponseBean responseBean = JSON.parseObject(new String(t), ResponseBean.class);

                if (null != responseBean) {

                    if (responseBean.isRequestFlag()) {

                        List<CarsInfo> carsInfoList = JSON.parseArray(responseBean.getData(), CarsInfo.class);

                        if (null !=carsInfoList && carsInfoList.size() >0){

                            ArrayList<String>  systemNoList = new ArrayList<>();

                            AppContext.getInstance().getCarSystemNo().clear();

                            for (CarsInfo carsInfo: carsInfoList){

                                int isOuttime = Integer.parseInt(carsInfo.getCarOutType());


                                if (isOuttime !=0 && systemNoList.size()-1 <=AppConfig.PAGE_SIZE){
                                    systemNoList.add(carsInfo.getSystemNo());
                                }

                            }

                            AppContext.getInstance().setCarSystemNo(systemNoList);


                            if (AppContext.getInstance().getCarSystemNo().size() > 0) {

                                dataHandler.post(new DataHandler(2)); // 获取位置数据
                            }



                        }else {

                            DialogHelper.getConfirmDialog(getActivity(), getString(R.string.tips_data_load_faile), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();

                                    dataHandler.post(new DataHandler(-1));

                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                }
                            }).create().show();

                        }

                    }else {

                        DialogHelper.getConfirmDialog(getActivity(), getString(R.string.tips_data_load_faile), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                dataHandler.post(new DataHandler(-1));
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                            }
                        }).create().show();
                    }


                }else {
                    DialogHelper.getConfirmDialog(getActivity(), getString(R.string.tips_data_load_faile), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                            dataHandler.post(new DataHandler(-1));
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                        }
                    }).create().show();

                }

            }catch (Exception e){

                TLog.d("TAG","***********exception**********"+e.getLocalizedMessage());

                DialogHelper.getConfirmDialog(getActivity(), getString(R.string.tips_parse_data_error), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        dataHandler.post(new DataHandler(-1));
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                    }
                }).create().show();
            }


        }

        @Override
        public void onFailure(int errorNo, String strMsg) {
            super.onFailure(errorNo, strMsg);
            hideWaitDialog();

            String message =  getString(R.string.tips_have_no_intent);

            if (errorNo == 404){
                message = getString(R.string.tips_data_load_faile);
            }

            DialogHelper.getConfirmDialog(getActivity(),message, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                    dataHandler.post(new DataHandler(-1));
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    dialogInterface.dismiss();
                }
            }).create().show();
        }
    };



    private void loadCarsLocation(){



        if (AppContext.getInstance().getCarSystemNo().size() > 0) {

            VehicleApi.getCarLocations(AppContext.getInstance().getCarSystemNo(), carsLocHandler);

        }

    }


    private HttpCallback  carsLocHandler = new HttpCallback() {

        @Override
        public void onSuccess(Map<String, String> headers, byte[] t) {
            super.onSuccess(headers, t);

            try {
                Log.i("TAG","=====baidu====loc======data======"+new String(t));
                ResponseBean  responseBean = JSON.parseObject(new String(t),ResponseBean.class);

                if (null != responseBean) {

                    if (responseBean.isRequestFlag()) {

                        ArrayList<CarLocationBean> carLocationList = (ArrayList<CarLocationBean>) JSON.parseArray(responseBean.getData(), CarLocationBean.class);

                        if (null !=carLocationList && carLocationList.size() >0){


                            dataHandler.post(new DataHandler(0,carLocationList));


                        }else {
                            if (AppContext.getInstance().getCarSystemNo().size() >0) {

                                DialogHelper.getConfirmDialog(getActivity(), getString(R.string.tips_data_load_faile), new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                        dialogInterface.dismiss();
                                        dataHandler.post(new DataHandler(2));
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                }).create().show();

                            }else {

                                dataHandler.post(new DataHandler(-1));
                            }
                        }

                    }else {

                        if (AppContext.getInstance().getCarSystemNo().size() >0) {

                            DialogHelper.getConfirmDialog(getActivity(), getString(R.string.tips_data_load_faile), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    dialogInterface.dismiss();
                                    dataHandler.post(new DataHandler(2));
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).create().show();

                        }else {

                            dataHandler.post(new DataHandler(-1));
                        }
                    }


                }else {


                    if (AppContext.getInstance().getCarSystemNo().size() >0) {

                        DialogHelper.getConfirmDialog(getActivity(), getString(R.string.tips_data_load_faile), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                dialogInterface.dismiss();
                                dataHandler.post(new DataHandler(2));
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).create().show();

                    }else {

                        dataHandler.post(new DataHandler(-1));
                    }
                }

            }catch (Exception e){

                TLog.d("TAG","***********exception**********"+e.getLocalizedMessage());

                String message = "抱歉，数据解析出错！";

                if (isAdded()){

                    message  =  getString(R.string.tips_parse_data_error);


                }

                if (AppContext.getInstance().getCarSystemNo().size() >0) {

                    DialogHelper.getConfirmDialog(getActivity(), message, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();
                            dataHandler.post(new DataHandler(2));
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();

                }else {

                    dataHandler.post(new DataHandler(-1));
                }
            }
        }

        @Override
        public void onFailure(int errorNo, String strMsg) {
            super.onFailure(errorNo, strMsg);

            String message =  getString(R.string.tips_have_no_intent);

            if (errorNo == 404){
                message = getString(R.string.tips_data_load_faile);
            }

            if (AppContext.getInstance().getCarSystemNo().size() >0) {

                DialogHelper.getConfirmDialog(getActivity(), message, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();
                        dataHandler.post(new DataHandler(2));
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).create().show();

            }else {

                dataHandler.post(new DataHandler(-1));
            }
        }



    };



    private  class DataHandler implements  Runnable{

        private int what;

        private ArrayList<CarLocationBean>  data;

        private View view;

        private DataHandler(int what,ArrayList<CarLocationBean>  data){

            this.what = what;
            this.data = data;
        }

        private  DataHandler(int what,View view){

            this.what = what;
            this.view = view;
        }

        private DataHandler(int what){
            this.what = what;
        }
        @Override
        public void run() {
            switch (what){

                case -1:
                    loadCarsList();
                    break;
                case  0 :
                    try {
                        addPointerOnMap(data);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case 1:

                    try {
                        if (null !=view){
                            showMyInfoWindow(view);
                        }
                    }catch (Exception e){
                e.printStackTrace();
                 }


                    break;

                case 2:

                    loadCarsLocation();
                    break;


            }
        }
    }

    private void addPointerOnMap(ArrayList<CarLocationBean>  data){

        resetMap();

        for (int i = 0 ; i <data.size();i++) {

            View  markerView = LayoutInflater.from(getActivity()).inflate(R.layout.view_marker_pointer,null);

            ImageView carIcon = (ImageView) markerView.findViewById(R.id.car_icon);

            TextView carName = (TextView) markerView.findViewById(R.id.car_name);



            CarLocationBean carBean = data.get(i);
            float Angle = Float.parseFloat(carBean.getCarDistriction());

            int      resIconId = R.drawable.vehicle0;;

            if (Angle > 350 || Angle < 10)
                resIconId = R.drawable.vehicle0;
            if (Angle >= 10 && Angle <= 35)
                resIconId = R.drawable.vehicle0_45;
            if (Angle > 35 && Angle < 55)
                resIconId = R.drawable.vehicle45;
            if (Angle >= 55 && Angle <= 80)
                resIconId = R.drawable.vehicle45_90;
            if (Angle > 80 && Angle < 100)
                resIconId = R.drawable.vehicle90;
            if (Angle >= 100 && Angle <= 125)
                resIconId = R.drawable.vehicle90_135;
            if (Angle > 125 && Angle < 145)
                resIconId = R.drawable.vehicle135;
            if (Angle >= 145 && Angle <= 170)
                resIconId = R.drawable.vehicle135_180;
            if (Angle > 170 && Angle < 190)
                resIconId = R.drawable.vehicle180;
            if (Angle >= 190 && Angle <= 215)
                resIconId = R.drawable.vehicle180_225;
            if (Angle > 215 && Angle < 235)
                resIconId = R.drawable.vehicle225;
            if (Angle >= 235 && Angle <= 260)
                resIconId = R.drawable.vehicle225_270;
            if (Angle > 260 && Angle < 280)
                resIconId = R.drawable.vehicle270;
            if (Angle >= 280 && Angle <= 305)
                resIconId = R.drawable.vehicle270_315;
            if (Angle > 305 && Angle < 325)
                resIconId = R.drawable.vehicle315;
            if (Angle >= 325 && Angle <= 350)
                resIconId = R.drawable.vehicle315_350;

            carIcon.setBackgroundResource(resIconId);
            // carIcon.setRotation(Angle);
            carName.setText(carBean.getCarNumber());
            LatLng position = new LatLng(Double.parseDouble(carBean.getCarLat()), Double.parseDouble(carBean.getCarLng()));
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(position);

            markerOptions.anchor(0.5f, 0.5f);
            BitmapDescriptor makerIcon =  BitmapDescriptorFactory.fromView(markerView);
           // BitmapDescriptor makerIcon =  BitmapDescriptorFactory.fromResource(resIconId);
            markerOptions.icon(makerIcon);  // 这里会有内存溢出
            markerOptions.draggable(true);
            Bundle bundle  = new Bundle();
            bundle.putSerializable("CarLocation", carBean);
            bundle.putInt("tempIndex", i);
            markerOptions.extraInfo(bundle);

            Marker marker  = (Marker) mBaiduMap.addOverlay(markerOptions);
            markerPointer.add(marker);
            if (null != makerIcon) {
                makerIcon.recycle();
            }
        }

        nowMarker =  markerPointer.get(markerIndex);
        onMarkerClick(nowMarker);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(nowMarker.getPosition()));

        isToGetLocation = true;

    }

    private  void showMyInfoWindow(final View view){



        final CarLocationBean carLocation = (CarLocationBean) nowMarker.getExtraInfo().getSerializable("CarLocation");

        render(carLocation,view,getString(R.string.data_loading));
        LatLng position = new LatLng(Double.parseDouble(carLocation.getCarLat()), Double.parseDouble(carLocation.getCarLng()));
        mBaduInfoWindow = new InfoWindow(view, position, -80);
        mBaiduMap.showInfoWindow(mBaduInfoWindow);

        VehicleApi.parseAddressByLatlng(carLocation.getLat(), carLocation.getLng(), new HttpCallback() {


            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                ResponseBean responseBean = JSON.parseObject(new String(t), ResponseBean.class);

                if (responseBean.isRequestFlag()) {

                    AddressInfoBean addressResult = JSON.parseObject(responseBean.getData(), AddressInfoBean.class);
                    render(carLocation, view, addressResult.getResultMessage());
                    LatLng position = new LatLng(Double.parseDouble(carLocation.getCarLat()), Double.parseDouble(carLocation.getCarLng()));
                    mBaduInfoWindow = new InfoWindow(view, position, -80);
                    mBaiduMap.showInfoWindow(mBaduInfoWindow);
                }

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                render(carLocation, view, getString(R.string.data_loading));
                LatLng position = new LatLng(Double.parseDouble(carLocation.getCarLat()), Double.parseDouble(carLocation.getCarLng()));
                mBaduInfoWindow = new InfoWindow(view, position, -80);
                mBaiduMap.showInfoWindow(mBaduInfoWindow);
            }

        });
    }


    /**
     * 重置地图
     */
    private  void resetMap(){

        if (mBaiduMap !=null){
            mBaiduMap.clear();
        }
        markerPointer.clear();

    }


    @Override
    public void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        }
        startDataTimmer();
        super.onResume();
    }

    @Override
    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        }
        // 让其停了
        stopDataTimer();
        super.onPause();
    }

    @Override
    public void onDestroy() {

        if (mMapView !=null){
            mMapView.onDestroy();
        }

        if (mReceiver !=null){
            getActivity().unregisterReceiver(mReceiver);
        }

        stopDataTimer();
        super.onDestroy();

    }



    // 显示强出框

    /**
     * 自定义infowinfow窗口
     */
    public void render(final CarLocationBean mCarlocation, View view,String myAddress) {



        TextView carnumber = (TextView) view.findViewById(R.id.carnumber);
        TextView cartime = (TextView) view.findViewById(R.id.cartime);
        TextView cardesc = (TextView) view.findViewById(R.id.cardesc);
        TextView carstate = (TextView) view.findViewById(R.id.carstate);

        TextView carmaile = (TextView) view.findViewById(R.id.carmaile);
        TextView carlatlng = (TextView) view.findViewById(R.id.carlatlng);
        TextView carlocation = (TextView) view.findViewById(R.id.carlocation);


        // 事件
        Button car_track =(Button) view.findViewById(R.id.car_track);
        if (loginResultStatus == 10002)
            car_track.setVisibility(View.GONE);
        else car_track.setVisibility(View.VISIBLE);
        car_track.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                onMarkerClickBySelf(v,mCarlocation);
            }
        });



        Button car_daohang =(Button) view.findViewById(R.id.car_daohang);
        //car_daohang.setVisibility(View.GONE);
        car_daohang.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                onMarkerClickBySelf(v,mCarlocation);
            }
        });

        Button car_detail =(Button) view.findViewById(R.id.car_detail);

        car_detail.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                onMarkerClickBySelf(v,mCarlocation);
            }
        });

        Button car_zhuizong =(Button) view.findViewById(R.id.car_zhuizong);
        //  car_zhuizong.setVisibility(View.GONE);
        car_zhuizong.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v) {
                onMarkerClickBySelf(v,mCarlocation);
            }
        });


        Button  car_report = (Button) view.findViewById(R.id.car_report);
        if (loginResultStatus == 10002)
            car_report.setVisibility(View.GONE);
        else car_report.setVisibility(View.VISIBLE);
        car_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onMarkerClickBySelf(v,mCarlocation);
            }
        });


        String sTimeForm = getResources().getString(R.string.car_map_poi_time);

        String sStateForm = getResources()
                .getString(R.string.car_map_poi_state);

        String sDescForm = getResources().getString(R.string.car_map_poi_desc);

        String sMileForm = getResources().getString(R.string.car_map_poi_mile);

        String sLatlngForm = getResources().getString(
                R.string.car_map_poi_latlng);

        String sLocationForm = getResources().getString(
                R.string.car_map_poi_location);

        String carNameTitle = getString(R.string.car_no_enable);

        String cartimeStr = getString(R.string.car_no_time);

        String carStateStr = getString(R.string.car_no_state);

        String carOilStr = "0.0";

        String carspeedStr = "0.0";

        String cartemplureStr = "0.0";

        String carmileStr = "0.0";
        String cartotalMileStr = "0.0";
        String carlatStr = getString(R.string.car_no_lat);
        String carlngStr = getString(R.string.car_no_lng);

        String carNumberStr = "";

        String carSystemNumberStr = "";

        String carlocationStr = getString(R.string.car_no_location);

        String omElectStr = "0.0";

        if (mCarlocation != null) {

            // 车牌号
            String tempSystemno = mCarlocation.getSystemNo();
            String temprNumberStr = mCarlocation.getCarNumber();
            String tempSIM = mCarlocation.getSimID();
            if (!StringUtils.isEmpty(tempSIM)) {
                carSystemNumberStr = tempSIM;
            }

            if (!StringUtils.isEmpty(temprNumberStr)) {
                carNumberStr = temprNumberStr;
            }
            // 时间
            String tempTime = mCarlocation.getNowDate();
            if (!StringUtils.isEmpty(tempTime)) {
                cartimeStr = tempTime;
            }

            // 状态
            String tempState = mCarlocation.getCarStateType();

            if (!StringUtils.isEmpty(tempState)
                    || !StringUtils.isEmpty(mCarlocation.getCarAcc())) {
                carStateStr = tempState + "   "+mCarlocation.getCarAcc() +"  "+getString(R.string.car_map_poi_stoptime)+mCarlocation.getStoptime();
            }
            // 详情
            String tempOil = mCarlocation.getCarOil();
            if (!StringUtils.isEmpty(tempOil)) {
                carOilStr = tempOil;
            }

            String tempSpeed = mCarlocation.getCarSpeed();
            if (!StringUtils.isEmpty(tempSpeed)) {
                carspeedStr = tempSpeed;
            }

            String tempTemplure = mCarlocation.getCarTempure();

            if (!StringUtils.isEmpty(tempTemplure)) {
                cartemplureStr = tempTemplure;
            }

            // 里程
            String tempMile = mCarlocation.getNowMile();
            if (!StringUtils.isEmpty(tempMile)) {
                carmileStr = tempMile;
            }else {
                carmileStr = "0.0";
            }

            String tempTotalMile = mCarlocation.getCountMile();

            if (!StringUtils.isEmpty(tempTotalMile)) {
                cartotalMileStr = tempTotalMile;
            }else {
                cartotalMileStr = "0.0";
            }

            // 经纬度
            String tempLat = mCarlocation.getLat();
            if (!StringUtils.isEmpty(carlatStr)) {

                carlatStr = tempLat;
            }

            final String[] tempLng = {mCarlocation.getLng()};
            if (!StringUtils.isEmpty(carlngStr)) {
                carlngStr = tempLng[0];

            }

            // 位置
            String tempLocation =myAddress;
            if (!StringUtils.isEmpty(tempLocation)) {
                carlocationStr = tempLocation;
            } else {
            }



            String omEle = mCarlocation.getCarVoltage();

            if (!StringUtils
                    .isEmpty(omEle)) {

                omElectStr =    omEle;

            }

        }
        nowAddress = myAddress;
        carnumber.setText(String.format(getString(R.string.car_map_poi_number),
                carNumberStr + "[" + carSystemNumberStr + "]"));
        cartime.setText(String.format(sTimeForm, cartimeStr));
        carstate.setText(String.format(sStateForm, carStateStr));
        cardesc.setText(String.format(sDescForm,omElectStr,carspeedStr, carOilStr,
                cartemplureStr));
        carmaile.setText(String.format(sMileForm, carmileStr, cartotalMileStr));
        carlatlng.setText(String.format(sLatlngForm, carlatStr, carlngStr));
        carlatlng.setVisibility(View.GONE);
        carlocation.setText(String.format(sLocationForm, carlocationStr));

    }


    public void onMarkerClickBySelf(View view,CarLocationBean carLocation) {

        Bundle bundle = new Bundle();

        switch (view.getId()) {
            case R.id.car_track:
                AppConfig.SELECT_DATA_MODE = AppConfig.MODE_TRACK;
                bundle.putString("systemNo", carLocation.getSystemNo());
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SELECTDATE, bundle);


                break;
            case R.id.car_detail:
                carLocation.setCarAddress(nowAddress);
                bundle.putSerializable("carLocation", carLocation);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.CARDETAIL, bundle);
                break;
            case R.id.car_zhuizong:
                bundle.putSerializable("carLocation", carLocation);
                UIHelper.showZuiZongActivity(getActivity(), bundle);

                // 开启服务去获取当前的GPS经綕度 然后显示在地图上.
                break;
            case R.id.car_daohang:

                bundle.putSerializable("carLocation", carLocation);
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.NAVIGATION,bundle);

                break;

            case R.id.car_report:

                AppConfig.SELECT_DATA_MODE = AppConfig.MODE_REPORT;
                bundle.putString("systemNo", carLocation.getSystemNo());
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SELECTDATE, bundle);

                break;

        }

    }



    /*********************************定时请求**********************************************/

    private ScheduledThreadPoolExecutor timmer;

    private void startDataTimmer(){
        try {
            if (null == timmer) {
                timmer = new ScheduledThreadPoolExecutor(10);
                long monitorSecond = AppContext.geReflushTime(AppConfig.KEY_REFLUSH_MONITOR_TIME);
                timmer.scheduleWithFixedDelay(dataTimerTask, 2, monitorSecond, TimeUnit.SECONDS);
            } else {


                stopDataTimer();
                startDataTimmer();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    private  void stopDataTimer(){

        if (null !=timmer){

            try {

               // timmer.shutdown();
                timmer.shutdownNow();
                timmer = null;
            }catch (Exception e){
                e.printStackTrace();
            }

            }

    }


    private TimerTask   dataTimerTask = new TimerTask() {
        @Override
        public void run() {
            try {

                if (isToGetLocation) {
                    isToGetLocation = false;
                    dataHandler.post(new DataHandler(2));

                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };




   /*******************************************************************/

   // 定位相关
     LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();

    boolean isFirstLoc = true; // 是否首次定位

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mBaiduMap == null) {

                return;
            }
            hideWaitDialog();
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(100).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();

            mBaiduMap.setMyLocationData(locData);
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 18);

                mBaiduMap.animateMapStatus(u);
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }
    private  void startLocation(){

        showWaitDialog(R.string.tips_location_ing);

        mBaiduMap
                .setMyLocationConfigeration(new MyLocationConfiguration(
                        MyLocationConfiguration.LocationMode.COMPASS, true, null));
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        // 定位初始化
        mLocClient = new LocationClient(getActivity());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(true); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(500);

        mLocClient.setLocOption(option);
        mLocClient.start();

    }


    private  void stopLocation(){
        hideWaitDialog();
        // 退出时销毁定位
        if (mLocClient !=null) {
            mLocClient.unRegisterLocationListener(myListener);
            mLocClient.stop();
            // 关闭定位图层
            mBaiduMap.setMyLocationEnabled(false);

            mLocClient = null;
        }


    }
}
