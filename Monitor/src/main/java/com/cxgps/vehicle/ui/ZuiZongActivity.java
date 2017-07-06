package com.cxgps.vehicle.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;

import com.alibaba.fastjson.JSON;
import com.cxgps.vehicle.AppConfig;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.api.remote.VehicleApi;
import com.cxgps.vehicle.base.BaseZuizongActivity;
import com.cxgps.vehicle.bean.CarLocationBean;
import com.cxgps.vehicle.bean.ResponseBean;
import com.cxgps.vehicle.fragment.BaiduZuizongFragment;
import com.cxgps.vehicle.fragment.GoogleZuizongFragment;
import com.cxgps.vehicle.interf.IZuiZongInter;
import com.cxgps.vehicle.utils.DialogHelper;
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Created by taosong on 16/7/23.
 */
public class ZuiZongActivity extends BaseZuizongActivity {

    private IZuiZongInter  iZuiZongInter;

    private boolean isToGetLocation = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkMapApp();
        initView();


    }


    @Override
    public void initData() {
        super.initData();
        startDataTimmer();
    }

    // 判断地图
    public  void checkMapApp(){

        int   maptype  =Integer.parseInt(AppContext.getMapWithKey());

        if (maptype ==1){ // 百度
            iZuiZongInter= new BaiduZuizongFragment();
            if (locationBean!= null){

                iZuiZongInter.loadLocation(locationBean);

                isToGetLocation = true;

            }
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, (Fragment) iZuiZongInter).commit();

        }else {  // 谷歌
            iZuiZongInter= new GoogleZuizongFragment();
            if (locationBean!= null){

                iZuiZongInter.loadLocation(locationBean);
                isToGetLocation = true;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,(Fragment) iZuiZongInter).commit();

        }
    }


    public void menuRightTop(View view) {

        super.menuRightTop(view);

        int viewId = view.getId();
        switch (viewId) {

            case R.id.tab_road_title:

                setTrafficeState(iZuiZongInter.changeRoad());
                break;
            case R.id.tab_map_title:

                setMapyViewType(iZuiZongInter.changeMaptype());
                break;

        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopDataTimer();
    }

    /*********************************定时请求**********************************************/

    private ScheduledThreadPoolExecutor timmer;

    private void startDataTimmer(){
        try {
            if (null == timmer) {
                timmer = new ScheduledThreadPoolExecutor(10);
                long monitorSecond = AppContext.geReflushTime(AppConfig.KEY_REFLUSH_ZUIZONG_TIME);
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

    private Handler  dataHandler = new Handler();

    private TimerTask dataTimerTask = new TimerTask() {
        @Override
        public void run() {
            try {

                if (isToGetLocation) {
                    isToGetLocation = false;

                    dataHandler.post(new DataHandler());


                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private class DataHandler implements  Runnable{

        @Override
        public void run() {
            requestLocation();
        }
    }


    public void requestLocation(){


        List<String> systemNoList = new ArrayList<>();
        if (null !=locationBean) {
            systemNoList.add(locationBean.getSystemNo());

            VehicleApi.getCarLocations(systemNoList, carsLocHandler);
        }

    }






    private HttpCallback carsLocHandler = new HttpCallback() {


        @Override
        public void onFailure(int errorNo, String strMsg) {
            super.onFailure(errorNo, strMsg);
            hideWaitDialog();
            AppContext.showToast(R.string.error_view_no_data);
        }

        @Override
        public void onSuccess(Map<String, String> headers, byte[] t) {
            super.onSuccess(headers, t);

            hideWaitDialog();

            ResponseBean responseBean = JSON.parseObject(new String(t), ResponseBean.class);

            if (responseBean.isRequestFlag()){

                ArrayList<CarLocationBean> carLocationList = (ArrayList<CarLocationBean>) JSON.parseArray(responseBean.getData(),CarLocationBean.class);

                if (carLocationList.size()>0){
                    isToGetLocation = true;
                    iZuiZongInter.loadLocation(carLocationList.get(0));
                }


            }else {

                DialogHelper.getConfirmDialog(ZuiZongActivity.this, getString(R.string.tips_data_load_faile, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isToGetLocation = false;
                        requestLocation();
                    }
                }), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isToGetLocation = true;
                        dialog.dismiss();
                    }
                }).create().show();

            }
        }
    };





}
