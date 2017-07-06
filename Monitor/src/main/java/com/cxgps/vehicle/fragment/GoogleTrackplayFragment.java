package com.cxgps.vehicle.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cxgps.vehicle.AppConfig;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.HistoryInfo;
import com.cxgps.vehicle.interf.IFragmentUpdateData;
import com.cxgps.vehicle.interf.ITrackplayInter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by taosong on 16/7/23.
 */
public class GoogleTrackplayFragment extends BaseFragment implements   ITrackplayInter,OnMapReadyCallback {


    private MapView mMapView;

    private GoogleMap mBaiduMap;

    private Marker nowMarker;
    private  int marderIndex = 0 ;

    protected Polyline mPolyLine;

    private ArrayList<HistoryInfo> historyInfos = new ArrayList<>();
    private List<LatLng> drawGeoPint = new ArrayList<>();

    private long  defaultDelay = 1*500;
    // private  PlayThread  playThread = null;

    private IFragmentUpdateData iFragmentUpdateData;


    private int  isBackMode = 0;


    public long getDefaultDelay() {
        return defaultDelay;
    }

    public void setDefaultDelay(long defaultDelay) {
        this.defaultDelay = defaultDelay;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        super.onCreateView(inflater, container, savedInstanceState);
        GoogleMapOptions bmo =  new GoogleMapOptions();
        bmo.zoomControlsEnabled(false);
        mMapView = new MapView(getActivity(),bmo);
        mMapView.onCreate(savedInstanceState);
        mMapView.getMapAsync(this);
        return mMapView;
    }




    @Override
    public void initData() {
        super.initData();

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
            mBaiduMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }else {

            mBaiduMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            isChange = true;
        }

        return isChange;
    }




    @Override
    public int startPlay() {

        mBaiduMap.animateCamera(CameraUpdateFactory.zoomBy(16));

        if (marderIndex < historyInfos.size()){

            if (timmer != null){

                stopDataTimer();
                return AppConfig.PlayStop;

            }else {
                startDataTimmer();
                return AppConfig.PlayIng;
            }

        }else {

            stopDataTimer();
            marderIndex = 0 ;

            startDataTimmer();

            return AppConfig.PlayStop;

        }
    }

    @Override
    public void historyData(Bundle bundle) {
        historyInfos = (ArrayList<HistoryInfo>) AppContext.getInstance().getHistoryInfos();

    }

    @Override
    public void setPlayProgress(int playProgress) {

        Log.i("TAG","=========当前播放的进度===="+playProgress);
        marderIndex = playProgress;
    }

    @Override
    public void getSpeedProgress(int speedPro) {

        long tempTime = 0 ;
        if (speedPro == 0 || speedPro == 5){

            tempTime = 5*100;
        }else {

            tempTime = (5-speedPro) *100;
        }


        setDefaultDelay(tempTime);

        stopDataTimer();
        startDataTimmer();
        Log.i("TAG","==============tempTIme===="+tempTime);
    }


    @Override
    public void setIFragmentUpdateListener(IFragmentUpdateData iFragmentUpdateListener) {
        this.iFragmentUpdateData  = iFragmentUpdateListener;
    }



    private  void initHistoryOnMap(){


        if (historyInfos.size() == 0 ){

            return;
        }


        for (int i = 0 ;i <historyInfos.size();i++){


            HistoryInfo item = historyInfos.get(i);

            LatLng latLng = new LatLng(Double.parseDouble(item.getCarLat()),Double.parseDouble(item.getCarLng()));

            drawGeoPint.add(latLng);
        }





        PolylineOptions overlayOptions = new PolylineOptions().width(10).color(Color.BLUE).addAll(drawGeoPint);
        mPolyLine = (Polyline)mBaiduMap.addPolyline(overlayOptions);

        MarkerOptions startMarker = new MarkerOptions().position(drawGeoPint.get(0)).title(getString(R.string.history_loc_st))
                .zIndex(9).icon(BitmapDescriptorFactory.fromResource(R.mipmap.nav_route_result_start_point));

        MarkerOptions endMarker = new MarkerOptions().position(drawGeoPint.get(drawGeoPint.size() -1)).title(getString(R.string.history_loc_en))
                .zIndex(9).icon(BitmapDescriptorFactory.fromResource(R.mipmap.nav_route_result_end_point));

        mBaiduMap.addMarker(startMarker);
        mBaiduMap.addMarker(endMarker);

        mBaiduMap.animateCamera(CameraUpdateFactory.newLatLng(startMarker.getPosition()));

    }

    private  void startDrawRoute(){

        if (nowMarker!=null){

            nowMarker.remove();
        }

        HistoryInfo  historyInfo =  historyInfos.get(marderIndex);



        float Angle = Float.parseFloat(historyInfo.getCarRole());

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

        MarkerOptions middlePointer = new MarkerOptions().anchor(0.5f,0.5f).position(drawGeoPint.get(marderIndex)).flat(true)
                .zIndex(9).icon(BitmapDescriptorFactory.fromResource(resIconId));
        nowMarker = (Marker) mBaiduMap.addMarker(middlePointer);
        mBaiduMap.animateCamera(CameraUpdateFactory.newLatLng(nowMarker.getPosition()));


    }



    @Override
    public boolean onBackPressed() {

        stopDataTimer();

        return super.onBackPressed();

    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();

    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        mMapView.onDestroy();
        super.onDestroy();
        stopDataTimer();

    }

    /**************************************************************************/




    @Override
    public void onMapReady(GoogleMap googleMap) {

        mBaiduMap  = googleMap;


        // 设置
        UiSettings mUiSettings = mBaiduMap.getUiSettings();

        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMyLocationButtonEnabled(false);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);


        if (historyInfos!=null && historyInfos.size()>0){

            initHistoryOnMap();
        }
    }



//
//
//    private Timer timmer = null;
//    private  PlayTask playTask = null;
//    private  void startTimmerPlay(){
//        try {
//            playTask = new PlayTask();
//            Log.i("defaultDelay", "======defaultDelay===" + defaultDelay);
//            // defaultDelay = 2 * 1000;
//            //timmer.schedule(new PlayTask(), defaultDelay);
//            timmer = new Timer();
//
//            timmer.schedule(new PlayTask(), 500, getDefaultDelay());
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//    private void stopTimmerPlay(){
//
//        if (playTask != null) {
//            playTask.cancel();
//
//            playTask = null;
//        }
//
//        if (timmer != null){
//            timmer.cancel();
//            timmer = null;
//        }
//    }
//
//
//
//    private Handler handlerThread = new Handler(Looper.getMainLooper()){
//
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            HistoryInfo  historyInfo = null;
//            if (marderIndex < historyInfos.size()) {
//
//                startDrawRoute();
//
//                historyInfo = historyInfos.get(marderIndex);
//
//
//                iFragmentUpdateData.fragmentData(marderIndex,AppConfig.PlayIng,historyInfo);
//
//                marderIndex++;
//            } else {
//                marderIndex = 0;
//                historyInfo = historyInfos.get(marderIndex);
//                iFragmentUpdateData.fragmentData(marderIndex, AppConfig.PlayStop, historyInfo);
//                stopDataTimer();
//
//            }
//        }
//    };



    int  tempIndex = 0;

    private Handler handlerThread = new Handler(Looper.getMainLooper()){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);




            HistoryInfo  historyInfo = null;
            if (marderIndex < historyInfos.size()) {

                startDrawRoute();

                historyInfo = historyInfos.get(marderIndex);


                iFragmentUpdateData.fragmentData(marderIndex,AppConfig.PlayIng,historyInfo);

                if (isBackMode!=0 && tempIndex <10){
                    tempIndex++;
                    marderIndex= marderIndex+isBackMode;
                    if (marderIndex ==0 || tempIndex == 10 ){
                        setDefaultDelay(1*500);
                        stopDataTimer();
                        startDataTimmer();
                        tempIndex = 0;
                        isBackMode =0;
                    }
                }else {

                    marderIndex++;
                }



            } else {
                marderIndex = 0;
                historyInfo = historyInfos.get(marderIndex);
                iFragmentUpdateData.fragmentData(marderIndex, AppConfig.PlayStop, historyInfo);
                stopDataTimer();

            }
        }
    };









    /*********************************定时请求**********************************************/

    private ScheduledThreadPoolExecutor timmer;

    private void startDataTimmer(){
        try {
            if (null == timmer) {
                timmer = new ScheduledThreadPoolExecutor(10);
                long monitorSecond = getDefaultDelay();
                timmer.scheduleWithFixedDelay(dataTimerTask, 2, monitorSecond, TimeUnit.MILLISECONDS);
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
                dataTimerTask.cancel();
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

                handlerThread.sendEmptyMessage(0);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };





    @Override
    public void backAndGoWithTimmer(int flag) {

        dealWitBackOrGo(flag);

    }

    private void dealWitBackOrGo(int flag) {

        if (marderIndex == 0 || marderIndex == historyInfos.size()-1){

            AppContext.showToast("请先播放！");

            return;
        }


        if (isBackMode==0) {

            stopDataTimer();

            setDefaultDelay(200);
            isBackMode  = flag;
            startDataTimmer();
        }




    }






}