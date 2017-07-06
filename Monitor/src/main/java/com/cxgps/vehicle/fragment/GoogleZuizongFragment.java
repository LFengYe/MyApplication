package com.cxgps.vehicle.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.api.remote.VehicleApi;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.AddressInfoBean;
import com.cxgps.vehicle.bean.CarLocationBean;
import com.cxgps.vehicle.bean.ResponseBean;
import com.cxgps.vehicle.interf.IZuiZongInter;
import com.cxgps.vehicle.utils.StringUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by taosong on 17/1/4.
 */

public class GoogleZuizongFragment extends BaseFragment implements IZuiZongInter,OnMapReadyCallback,GoogleMap.OnMarkerClickListener,GoogleMap.OnMapClickListener {


    private GoogleMap mGoogleMap = null;

    private Handler  dataHandler = new Handler();

    private ArrayList<Marker> markerPointer = new ArrayList<>();

    private ArrayList<LatLng>  drawLineList = new ArrayList<>();

    private Marker  nowMarker,startMarker;

    private CustomInfoWindowAdapter  customInfoWindowAdapter;

    private Polyline  mPolyline;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_google_map,null);

        SupportMapFragment mapFragment =
                (SupportMapFragment)getActivity(). getSupportFragmentManager().findFragmentById(R.id.googleMap);
        mapFragment.getMapAsync(this);

        return view;
    }


    @Override
    public void initView(View view) {
        super.initView(view);

    }

    @Override
    public void initData() {
        super.initData();

    }




    @Override
    public boolean changeRoad() {

        if( mGoogleMap.isTrafficEnabled()){

            mGoogleMap.setTrafficEnabled(false);
            AppContext.showToast(R.string.map_traffic_close);
        }else {
            mGoogleMap.setTrafficEnabled(true);
            AppContext.showToast(R.string.map_traffic_open);

        }

        return mGoogleMap.isTrafficEnabled();



    }

    boolean  isChange = false;
    @Override
    public boolean changeMaptype() {

        if (isChange){
            isChange = false;
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }else {

            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            isChange = true;
        }

        return isChange;
    }

    @Override
    public void loadLocation(CarLocationBean locationBean) {

        if (locationBean!=null){

            dataHandler.post(new DataHandler(0,locationBean));

        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap  = googleMap;

        // 设置
        UiSettings mUiSettings = mGoogleMap.getUiSettings();

        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(false);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);

        // 事件

        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnMapClickListener(this);


    }




    public   class  DataHandler implements  Runnable  {

        private int what;

        private CarLocationBean  data;

        private View view;

        private DataHandler(int what,CarLocationBean  data){

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


                case  0 :
                    try {
                        addPointer(data);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case 1:
                    showMyInfoWindow(view);
                    break;
            }
        }
    }


    private  void addPointer(CarLocationBean  carBean){

        if (null ==carBean){

            return;

        }

        if (nowMarker!= null){

            nowMarker.remove();
        }


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



        LatLng position = new LatLng(Double.parseDouble(carBean.getCarLat()), Double.parseDouble(carBean.getCarLng()));
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);
        drawLineList.add(position);

        BitmapDescriptor makerIcon = null;

        if (drawLineList.size() == 1){

            makerIcon =  BitmapDescriptorFactory.fromResource(R.mipmap.nav_route_result_start_point);
        }else {

            makerIcon =  BitmapDescriptorFactory.fromResource(resIconId);
        }



        markerOptions.icon(makerIcon);  // 这里会有内存溢出
        markerOptions.draggable(true);

        if (drawLineList.size() >1) {

            PolylineOptions overlayOptions = new PolylineOptions().width(10).color(Color.BLUE).addAll(drawLineList);

            mPolyline = mGoogleMap.addPolyline(overlayOptions);
            nowMarker =  mGoogleMap.addMarker(markerOptions);

        }else {
            startMarker = mGoogleMap.addMarker(markerOptions);
            nowMarker= startMarker;
        }
        nowMarker.setTag(carBean);

        markerPointer.add(nowMarker);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(nowMarker.getPosition()));
        onMarkerClick(nowMarker);
        makerIcon.zzIy();

    }




    @Override
    public void onMapClick(LatLng latLng) {

        nowMarker.hideInfoWindow();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        nowMarker = marker;
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_marker_pop, null);


        dataHandler.post(new DataHandler(1,view));
        return false;
    }


    public  void showMyInfoWindow(final  View view){

      final   CarLocationBean  carLocationBean = (CarLocationBean) nowMarker.getTag();


        render(carLocationBean, view, getString(R.string.data_loading));

        customInfoWindowAdapter = new CustomInfoWindowAdapter(view);
        mGoogleMap.setInfoWindowAdapter(customInfoWindowAdapter);
        nowMarker.showInfoWindow();

        VehicleApi.parseAddressByLatlng(carLocationBean.getLat(),carLocationBean.getLng(), new HttpCallback() {


            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);

                ResponseBean responseBean = JSON.parseObject(new String(t), ResponseBean.class);

                if (responseBean.isRequestFlag()) {

                    AddressInfoBean addressResult = JSON.parseObject(responseBean.getData(), AddressInfoBean.class);
                    render(carLocationBean, view, addressResult.getResultMessage());
                    customInfoWindowAdapter = new CustomInfoWindowAdapter(view);
                    mGoogleMap.setInfoWindowAdapter(customInfoWindowAdapter);
                    nowMarker.showInfoWindow();
                }

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                render(carLocationBean, view, getString(R.string.car_no_location));

                customInfoWindowAdapter = new CustomInfoWindowAdapter(view);
                mGoogleMap.setInfoWindowAdapter(customInfoWindowAdapter);
                nowMarker.showInfoWindow();
            }


        });

    }


    class  CustomInfoWindowAdapter  implements GoogleMap.InfoWindowAdapter{

        private  View mWindow;

        public CustomInfoWindowAdapter(View view){

            this.mWindow = view;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return mWindow;
        }

        @Override
        public View getInfoContents(Marker marker) {
            return null;
        }
    }


    // 显示强出框

    /**
     * 自定义infowinfow窗口
     */
    public void render(final CarLocationBean mCarlocation, View view,String myAddress) {

        Log.i("TAG", "=====mCarlocation======" + JSON.toJSONString(mCarlocation));


        TextView carnumber = (TextView) view.findViewById(R.id.carnumber);
        TextView cartime = (TextView) view.findViewById(R.id.cartime);
        TextView cardesc = (TextView) view.findViewById(R.id.cardesc);
        TextView carstate = (TextView) view.findViewById(R.id.carstate);

        TextView carmaile = (TextView) view.findViewById(R.id.carmaile);
        TextView carlatlng = (TextView) view.findViewById(R.id.carlatlng);
        TextView carlocation = (TextView) view.findViewById(R.id.carlocation);


        // 事件
        Button car_track =(Button) view.findViewById(R.id.car_track);
        car_track.setVisibility(View.GONE);



        Button car_daohang =(Button) view.findViewById(R.id.car_daohang);
        car_daohang.setVisibility(View.GONE);


        Button car_detail =(Button) view.findViewById(R.id.car_detail);
        car_detail.setVisibility(View.GONE);


        Button car_zhuizong =(Button) view.findViewById(R.id.car_zhuizong);
        car_zhuizong.setVisibility(View.GONE);


        Button  car_report = (Button) view.findViewById(R.id.car_report);
        car_report.setVisibility(View.GONE);



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
                carStateStr = tempState + "   "+mCarlocation.getCarAcc() +"   停车时长"+mCarlocation.getStoptime();
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




}
