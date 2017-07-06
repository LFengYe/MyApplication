package com.cxgps.vehicle.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
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
import com.cxgps.vehicle.interf.IMonitorInter;
import com.cxgps.vehicle.interf.OnInfoWindowElemTouchListener;
import com.cxgps.vehicle.utils.DialogHelper;
import com.cxgps.vehicle.utils.PermissionUtils;
import com.cxgps.vehicle.utils.StringUtils;
import com.cxgps.vehicle.utils.TDevice;
import com.cxgps.vehicle.utils.UIHelper;
import com.cxgps.vehicle.widget.MapWrapperLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class  GoogleMonitorFragment  extends BaseFragment implements GoogleMap.OnMyLocationChangeListener,IMonitorInter,OnMapReadyCallback,GoogleMap.OnMapClickListener,GoogleMap.OnMarkerClickListener,GoogleMap.InfoWindowAdapter {


    private GoogleMap mMap;



    private   int markerIndex = 0 ;

    private Marker nowMarker;

    private Handler dataHandler = new Handler();

    private String nowAddress;


    private ArrayList<Marker> markerPointer = new ArrayList<>();


    private  boolean isToGetLocation = false ;


    private ViewGroup infoWindow;


    protected   MapWrapperLayout  mapWrapperLayout;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private boolean mPermissionDenied = false;

    private boolean  isFirstLoc =  false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_google_map,null);
        mapWrapperLayout = (MapWrapperLayout)view.findViewById(R.id.map_relative_layout);

        SupportMapFragment mapFragment =
                (SupportMapFragment)getActivity(). getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);


        infoWindow= (ViewGroup) getLayoutInflater(savedInstanceState).inflate(R.layout.view_marker_gpop, null);

        // 事件
        Button car_track =(Button) infoWindow.findViewById(R.id.car_track);
        car_track.setOnTouchListener(new OnInfoWindowElemTouchListener(car_track) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                onMarkerClickBySelf(v,(CarLocationBean) nowMarker.getTag());
            }
        });



        Button car_daohang =(Button) infoWindow.findViewById(R.id.car_daohang);
        //car_daohang.setVisibility(View.GONE);
        car_daohang.setOnTouchListener(new OnInfoWindowElemTouchListener(car_daohang) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {

                onMarkerClickBySelf(v,(CarLocationBean) nowMarker.getTag());

            }
        });

        Button car_detail =(Button) infoWindow.findViewById(R.id.car_detail);

        car_detail.setOnTouchListener(new OnInfoWindowElemTouchListener(car_detail) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                onMarkerClickBySelf(v,(CarLocationBean) nowMarker.getTag());
            }
        });

        Button car_zhuizong =(Button) infoWindow.findViewById(R.id.car_zhuizong);
        //  car_zhuizong.setVisibility(View.GONE);
        car_zhuizong.setOnTouchListener(new OnInfoWindowElemTouchListener(car_zhuizong) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                onMarkerClickBySelf(v,(CarLocationBean) nowMarker.getTag());
            }
        });


        Button  car_report = (Button) infoWindow.findViewById(R.id.car_report);

        car_report.setOnTouchListener(new OnInfoWindowElemTouchListener(car_report) {
            @Override
            protected void onClickConfirmed(View v, Marker marker) {
                onMarkerClickBySelf(v,(CarLocationBean) nowMarker.getTag());
            }
        });

        return view;
    }

    @Override
    public void initView(View view) {
        super.initView(view);

    }

    @Override
    public void initData() {

        dataHandler.post(new DataHandler(-1));

    }

    /**************************回调事件***********************************/

    @Override
    public boolean changeRoad() {

        if( mMap.isTrafficEnabled()){

            mMap.setTrafficEnabled(false);
            AppContext.showToast(R.string.map_traffic_close);
        }else {
            mMap.setTrafficEnabled(true);
            AppContext.showToast(R.string.map_traffic_open);

        }

        return mMap.isTrafficEnabled();



    }

    boolean  isChange = false;
    @Override
    public boolean changeMaptype() {

        if (isChange){
            isChange = false;
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }else {

            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
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

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nowMarker.getPosition(), 11));

                startDataTimmer();

                stopLocation();

                break;




        }
    }

    @Override
    public void changeBefore() {

        if (!isFirstLoc){

            stopLocation();
        }


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

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nowMarker.getPosition(), 11));



    }

    @Override
    public void changeAfter() {

        if (!isFirstLoc){

            stopLocation();
        }

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
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nowMarker.getPosition(), 11));


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

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nowMarker.getPosition(),11));


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
        nowMarker.hideInfoWindow();
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        nowMarker = marker;
        CarLocationBean  tempLoc =(CarLocationBean) nowMarker.getTag();
        markerIndex = tempLoc.getMarderIndex();

        dataHandler.post(new DataHandler(1));


        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapWrapperLayout.init(mMap, TDevice.getPixelsFromDp(getActivity(),39+20));
        // 设置
        UiSettings mUiSettings = mMap.getUiSettings();

        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(false);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);

        // 事件
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapClickListener(this);



        mMap.setInfoWindowAdapter(this);
    }




    /**************************加载数据****start**********************************/
    private void loadCarsList(){

        showWaitDialog();

        UserInfo userInfo =  AppContext.getInstance().getLoginUser();

        if (Boolean.valueOf(userInfo.getuLoginFlag())){

            VehicleApi.getCarList(userInfo.getUserName(), userInfo.getuLoginType(), 0, 6, carsHandler);

        }
    }

    private HttpCallback carsHandler = new HttpCallback() {
        @Override
        public void onSuccess(Map<String, String> headers, byte[] t) {
            super.onSuccess(headers, t);

            hideWaitDialog();

            try {

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

              Log.i("TAG","=======String======"+new String(t));
            try {

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



                String message =  getString(R.string.tips_parse_data_error);


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

    @Override
    public void onMyLocationChange(Location location) {
        if (null == location){

            return;
        }


        LatLng position = new LatLng(location.getLatitude(),location.getLongitude());
        if (isFirstLoc){
            isFirstLoc = false;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position,11));
        }else {

            mMap.animateCamera(CameraUpdateFactory.newLatLng(position));
        }
    }


    private  class DataHandler implements  Runnable{

        private int what;

        private ArrayList<CarLocationBean>  data;


        private DataHandler(int what,ArrayList<CarLocationBean>  data){

            this.what = what;
            this.data = data;
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
                    showMyInfoWindow(infoWindow);
                    break;

                case 2:

                    loadCarsLocation();
                    break;


            }
        }
    }


    /**
     * 重置地图
     */
    private  void resetMap(){

        if (mMap !=null){
            mMap.clear();
        }
        markerPointer.clear();

    }



    private void addPointerOnMap(ArrayList<CarLocationBean>  data) {

        resetMap();

        for (int i = 0 ; i <data.size();i++) {

            CarLocationBean carBean = data.get(i);


            float Angle = Float.parseFloat(carBean.getCarDistriction());
            LatLng position = new LatLng(Double.parseDouble(carBean.getLat()), Double.parseDouble(carBean.getLng()));

            MarkerOptions markerOptions = new MarkerOptions();

            int resIconId = R.drawable.vehicle0;
            ;

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

            markerOptions.icon(BitmapDescriptorFactory.fromResource(resIconId));
            markerOptions.position(position);
            Marker marker = mMap.addMarker(markerOptions);
            carBean.setMarderIndex(i);
            marker.setTag(carBean);
            markerPointer.add(marker);

        }

        nowMarker  = markerPointer.get(markerIndex);
        onMarkerClick(nowMarker);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nowMarker.getPosition(),11));

        isToGetLocation  = true;
    }


    private  void showMyInfoWindow(final  View view){

        final CarLocationBean  noticeInfoBean = (CarLocationBean) nowMarker.getTag();
        render(noticeInfoBean, view, getString(R.string.data_loading));


        nowMarker.showInfoWindow();

        VehicleApi.parseAddressByLatlng(noticeInfoBean.getLat(), noticeInfoBean.getLng(), new HttpCallback() {


            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);

                ResponseBean responseBean = JSON.parseObject(new String(t), ResponseBean.class);

                if (responseBean.isRequestFlag()) {

                    AddressInfoBean addressResult = JSON.parseObject(responseBean.getData(), AddressInfoBean.class);
                    render(noticeInfoBean, view, addressResult.getResultMessage());

                    nowMarker.showInfoWindow();
                }

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                render(noticeInfoBean, view, getString(R.string.car_no_location));

                nowMarker.showInfoWindow();
            }


        });

    }



    @Override
    public View getInfoWindow(Marker marker) {

        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {



        mapWrapperLayout.setMarkerWithInfoWindow(marker, infoWindow);
        return infoWindow;
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

                DialogHelper.getConfirmDialog(getActivity(), getString(R.string.tips_no_navia_error), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();
                    }
                }).create().show();

//                bundle.putSerializable("carLocation", carLocation);
//                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.NAVIGATION,bundle);

                break;

            case R.id.car_report:

                AppConfig.SELECT_DATA_MODE = AppConfig.MODE_REPORT;
                bundle.putString("systemNo", carLocation.getSystemNo());
                UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SELECTDATE, bundle);

                break;

        }

    }

    @Override
    public void onResume() {
        startDataTimmer();
        super.onResume();


        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // stopDataTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopDataTimer();
    }

    /*********************************定时请求**********************************************/

    private ScheduledThreadPoolExecutor timmer;

    private void startDataTimmer(){
        try {
            if (null == timmer) {
                timmer = new ScheduledThreadPoolExecutor(10);
                long monitorSecond = AppContext.geReflushTime(AppConfig.KEY_REFLUSH_MONITOR_TIME);
                Log.i("TYAH","====dfdf========"+monitorSecond);
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


    private TimerTask dataTimerTask = new TimerTask() {
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



    /************************定位**************************************/

    public void startLocation(){

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.

            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationChangeListener(this);
        }

    }

    public  void stopLocation(){

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.

            mMap.setMyLocationEnabled(false);

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            startLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }


    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getActivity().getSupportFragmentManager(), "dialog");
    }



}