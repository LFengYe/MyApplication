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
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.api.remote.VehicleApi;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.AddressInfoBean;
import com.cxgps.vehicle.bean.CarLocationBean;
import com.cxgps.vehicle.bean.ResponseBean;
import com.cxgps.vehicle.interf.IZuiZongInter;
import com.cxgps.vehicle.utils.StringUtils;
import com.kymjs.rxvolley.client.HttpCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;


/**
 * Created by taosong on 16/7/26.
 */
public class BaiduZuizongFragment extends BaseFragment  implements  BaiduMap.OnMarkerClickListener,BaiduMap.OnMapClickListener, IZuiZongInter {


    private MapView mMapView;

    private BaiduMap mBaiduMap;

    private Marker  nowMarker;

    private  Polyline  mPolyLine;

    private String nowAddress;

    private InfoWindow mBaduInfoWindow;
    private Marker startMarker;

    private Handler dataHandler = new Handler();

    private ArrayList<MarkerBean> markerPointer = new ArrayList<>();

    private ArrayList<LatLng>  drawLineList = new ArrayList<>();

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
    public void loadLocation(CarLocationBean locationBean) {


        if (locationBean!=null){

            dataHandler.post(new DataHandler(0,locationBean));

        }
    }


    private  void addPointer(CarLocationBean carBean){


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
        markerOptions.anchor(0.5f, 0.5f);

        BitmapDescriptor makerIcon = null;

        if (drawLineList.size() == 1){

            makerIcon =  BitmapDescriptorFactory.fromResource(R.mipmap.nav_route_result_start_point);
        }else {

            makerIcon =  BitmapDescriptorFactory.fromResource(resIconId);
        }


        markerOptions.icon(makerIcon);  // 这里会有内存溢出
        markerOptions.draggable(true);
        Bundle bundle  = new Bundle();
        bundle.putSerializable("CarLocation", carBean);
        markerOptions.extraInfo(bundle);
        if (drawLineList.size() >1) {

            OverlayOptions overlayOptions = new PolylineOptions().width(10).color(Color.BLUE).points(drawLineList);

            mPolyLine = (Polyline) mBaiduMap.addOverlay(overlayOptions);
            nowMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
        }else {
            startMarker = (Marker) mBaiduMap.addOverlay(markerOptions);
            nowMarker= startMarker;
        }



        MarkerBean  markerBean = new MarkerBean(carBean,nowMarker);

        markerPointer.add(markerBean);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newLatLng(nowMarker.getPosition()));
        onMarkerClick(nowMarker);
        makerIcon.recycle();
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


    class MarkerBean  implements Serializable {

        private CarLocationBean  loc;

        private Marker marker;

        public MarkerBean(CarLocationBean loc,Marker marker){
            this.marker = marker;
            this.loc =loc;
        }


        public CarLocationBean getLoc() {
            return loc;
        }

        public void setLoc(CarLocationBean loc) {
            this.loc = loc;
        }

        public Marker getMarker() {
            return marker;
        }

        public void setMarker(Marker marker) {
            this.marker = marker;
        }
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

        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_marker_pop, null);

        dataHandler.post(new DataHandler(1,view));


        return false;
    }


    private  void   showMyInfoWindow(final  View view){


        final CarLocationBean carLocation = (CarLocationBean) nowMarker.getExtraInfo().getSerializable("CarLocation");


        render(carLocation,view,getString(R.string.data_loading));
        LatLng position = new LatLng(Double.parseDouble(carLocation.getCarLat()), Double.parseDouble(carLocation.getCarLng()));
        mBaduInfoWindow = new InfoWindow(view, position, -47);
        mBaiduMap.showInfoWindow(mBaduInfoWindow);

        VehicleApi.parseAddressByLatlng(carLocation.getLat(), carLocation.getLng(), new HttpCallback() {

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                render(carLocation, view, getString(R.string.car_no_location));
                LatLng position = new LatLng(Double.parseDouble(carLocation.getCarLat()), Double.parseDouble(carLocation.getCarLng()));
                mBaduInfoWindow = new InfoWindow(view, position, -47);
                mBaiduMap.showInfoWindow(mBaduInfoWindow);
            }

            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);


                ResponseBean responseBean = JSON.parseObject(new String(t), ResponseBean.class);

                if (responseBean.isRequestFlag()) {

                    AddressInfoBean addressResult = JSON.parseObject(responseBean.getData(), AddressInfoBean.class);
                    render(carLocation, view, addressResult.getResultMessage());
                    LatLng position = new LatLng(Double.parseDouble(carLocation.getCarLat()), Double.parseDouble(carLocation.getCarLng()));
                    mBaduInfoWindow = new InfoWindow(view, position, -47);
                    mBaiduMap.showInfoWindow(mBaduInfoWindow);
                }
            }

        });




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


}
