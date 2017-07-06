package com.cxgps.vehicle.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.baidu.mapapi.map.UiSettings;
import com.baidu.mapapi.model.LatLng;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.api.remote.VehicleApi;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.AddressInfoBean;
import com.cxgps.vehicle.bean.NoticeInfoBean;
import com.cxgps.vehicle.bean.ResponseBean;
import com.cxgps.vehicle.interf.INoticeDetail;
import com.cxgps.vehicle.utils.StringUtils;
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.Map;

/**
 * Created by taosong on 16/12/30.
 */

public class BDNoticeDetailFragment  extends BaseFragment implements INoticeDetail,BaiduMap.OnMarkerClickListener,BaiduMap.OnMapClickListener{


    private MapView  mMapView;

    private BaiduMap   mBaiduMap;

    private InfoWindow  mBaduInfoWindow;

    private Marker  nowMarker;

    private NoticeInfoBean  noticeInfoBean;

    private String nowAddress;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       super.onCreateView(inflater,container,savedInstanceState);
        return mMapView;
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
        super.initData();

        addPointerOnMap(noticeInfoBean);
    }

    @Override
    public void loadData(NoticeInfoBean data) {
        noticeInfoBean  = data;
    }


    // 添加点到地图上
    private  void addPointerOnMap(NoticeInfoBean  carBean){

        mBaiduMap.clear();

        LatLng position = new LatLng(Double.parseDouble(carBean.getLatitude()), Double.parseDouble(carBean.getLongitude()));


        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(position);

        markerOptions.anchor(0.5f, 0.5f);
        BitmapDescriptor makerIcon =
                BitmapDescriptorFactory.fromResource(R.mipmap.map_pointer_blue);
        markerOptions.icon(makerIcon);  // 这里会有内存溢出
        markerOptions.draggable(true);

        nowMarker = (Marker) mBaiduMap.addOverlay(markerOptions);


        makerIcon.recycle();

        onMarkerClick(nowMarker);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(nowMarker.getPosition()));

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
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_marker_notice, null);


        showMyInfoWindow(view);
        return false;
    }


    private  void   showMyInfoWindow(final  View view){




        render(noticeInfoBean,view,getString(R.string.data_loading));
        LatLng position = new LatLng(Double.parseDouble(noticeInfoBean.getLatitude()), Double.parseDouble(noticeInfoBean.getLongitude()));
        mBaduInfoWindow = new InfoWindow(view, position, -47);
        mBaiduMap.showInfoWindow(mBaduInfoWindow);

        VehicleApi.parseAddressByLatlng(noticeInfoBean.getYsLatitude(), noticeInfoBean.getYsLongitude(), new HttpCallback() {


            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);

                ResponseBean responseBean = JSON.parseObject(new String(t), ResponseBean.class);

                if (responseBean.isRequestFlag()) {

                    AddressInfoBean addressResult = JSON.parseObject(responseBean.getData(), AddressInfoBean.class);
                    render(noticeInfoBean, view, addressResult.getResultMessage());
                    LatLng position = new LatLng(Double.parseDouble(noticeInfoBean.getLatitude()), Double.parseDouble(noticeInfoBean.getLongitude()));
                    mBaduInfoWindow = new InfoWindow(view, position, -47);
                    mBaiduMap.showInfoWindow(mBaduInfoWindow);
                }

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                render(noticeInfoBean, view, "获取失败...");
                LatLng position = new LatLng(Double.parseDouble(noticeInfoBean.getLatitude()), Double.parseDouble(noticeInfoBean.getLongitude()));
                mBaduInfoWindow = new InfoWindow(view, position, -47);
                mBaiduMap.showInfoWindow(mBaduInfoWindow);
            }


        });




    }


    // 显示强出框

    /**
     * 自定义infowinfow窗口
     */
    public void render(final NoticeInfoBean mCarlocation, View view,String myAddress) {


        TextView carnumber = (TextView) view.findViewById(R.id.carnumber);
        TextView cartime = (TextView) view.findViewById(R.id.cartime);
        TextView cardesc = (TextView) view.findViewById(R.id.cardesc);
        TextView cararmtype = (TextView) view.findViewById(R.id.cararmtype);
        TextView carlocation = (TextView) view.findViewById(R.id.carlocation);





        String cartimeStr = getActivity().getString(R.string.car_no_time);

        String carAccStr= getString(R.string.car_no_state);

        String cararmtypeStr = getString(R.string.tips_alarm_type_error);

        String carspeedStr = "0.0";



        String carNumberStr = "";

        String carSystemNumberStr = "";

        String carlocationStr = getString(R.string.car_no_location);



        if (mCarlocation != null) {

            // 车牌号
            String tempSystemno = mCarlocation.getSystemNo();
            String temprNumberStr = mCarlocation.getVehNoF();
            if (!StringUtils.isEmpty(tempSystemno)) {
                carSystemNumberStr = tempSystemno;
            }

            if (!StringUtils.isEmpty(temprNumberStr)) {
                carNumberStr = temprNumberStr;
            }
            // 时间
            String tempTime = mCarlocation.getTime();
            if (!StringUtils.isEmpty(tempTime)) {
                cartimeStr = tempTime;
            }


            String  tempSpeed = mCarlocation.getVelocity();

            if (!StringUtils.isEmpty(tempSpeed)){

                carspeedStr = tempSpeed;
            }



            // ACC
            String tempAccState = mCarlocation.getDtStatus();

            if (!StringUtils.isEmpty(tempAccState)) {
                carAccStr = tempAccState;
            }
            // Alarm
            String tempAlarm = mCarlocation.getAlarmType();
            if (!StringUtils.isEmpty(tempAlarm)) {
                cararmtypeStr = tempAlarm;
            }



            // 位置
            String tempLocation =myAddress;
            if (!StringUtils.isEmpty(tempLocation)) {
                carlocationStr = tempLocation;
            } else {
            }


        }
        nowAddress = myAddress;
        carnumber.setText(String.format(getString(R.string.pop_alarm_title),
                carNumberStr,carSystemNumberStr));
        cartime.setText(String.format(getString(R.string.pop_alarm_time), cartimeStr));
        cararmtype.setText(String.format(getString(R.string.pop_alarm_type), cararmtypeStr));
        cardesc.setText(String.format(getString(R.string.pop_alarm_desc), carspeedStr,carAccStr));

        carlocation.setText(String.format(getString(R.string.pop_alarm_location), carlocationStr));

    }
}
