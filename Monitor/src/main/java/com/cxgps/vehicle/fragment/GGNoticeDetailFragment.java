package com.cxgps.vehicle.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.api.remote.VehicleApi;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.AddressInfoBean;
import com.cxgps.vehicle.bean.NoticeInfoBean;
import com.cxgps.vehicle.bean.ResponseBean;
import com.cxgps.vehicle.interf.INoticeDetail;
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
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.Map;

/**
 * Created by taosong on 16/12/30.
 */

public class GGNoticeDetailFragment extends BaseFragment implements INoticeDetail,OnMapReadyCallback,GoogleMap.OnMarkerClickListener,GoogleMap.OnMapClickListener{

    private GoogleMap  mGoogleMap = null;

    private NoticeInfoBean  noticeInfoBean;

    private  Marker  nowMarker;


    private CustomInfoWindowAdapter  customInfoWindowAdapter;

    private String nowAddress;


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
    public void loadData(NoticeInfoBean data) {
        noticeInfoBean = data;
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
        if (noticeInfoBean !=null) {

            addPointerOnMap(noticeInfoBean);
        }
    }

    public  void addPointerOnMap(NoticeInfoBean  noticeBean){


        mGoogleMap.clear();


        LatLng  latLng = new LatLng(Double.parseDouble(noticeBean.getLatitude()),Double.parseDouble(noticeBean.getLongitude()));


        MarkerOptions markerOptions = new MarkerOptions();

        markerOptions.position(latLng);

        markerOptions.anchor(0.5f, 0.5f);
        BitmapDescriptor makerIcon =
                BitmapDescriptorFactory.fromResource(R.mipmap.map_pointer_blue);
        markerOptions.icon(makerIcon);  // 这里会有内存溢出
        markerOptions.draggable(true);

        nowMarker = mGoogleMap.addMarker(markerOptions);
        makerIcon.zzIy();
        onMarkerClick(nowMarker);
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLng(nowMarker.getPosition()));

    }

    @Override
    public void onMapClick(LatLng latLng) {

        nowMarker.hideInfoWindow();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        nowMarker = marker;
        final View view = LayoutInflater.from(getActivity()).inflate(R.layout.view_marker_notice, null);


        showMyInfoWindow(view);
        return false;
    }


    private  void   showMyInfoWindow(final  View view) {


        render(noticeInfoBean, view, getString(R.string.data_loading));
        LatLng position = new LatLng(Double.parseDouble(noticeInfoBean.getLatitude()), Double.parseDouble(noticeInfoBean.getLongitude()));
        customInfoWindowAdapter = new CustomInfoWindowAdapter(view);
        mGoogleMap.setInfoWindowAdapter(customInfoWindowAdapter);
        nowMarker.showInfoWindow();

        VehicleApi.parseAddressByLatlng(noticeInfoBean.getYsLatitude(), noticeInfoBean.getYsLongitude(), new HttpCallback() {


            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);

                ResponseBean responseBean = JSON.parseObject(new String(t), ResponseBean.class);

                if (responseBean.isRequestFlag()) {

                    AddressInfoBean addressResult = JSON.parseObject(responseBean.getData(), AddressInfoBean.class);
                    render(noticeInfoBean, view, addressResult.getResultMessage());
                    LatLng position = new LatLng(Double.parseDouble(noticeInfoBean.getLatitude()), Double.parseDouble(noticeInfoBean.getLongitude()));
                    customInfoWindowAdapter = new CustomInfoWindowAdapter(view);
                    mGoogleMap.setInfoWindowAdapter(customInfoWindowAdapter);
                    nowMarker.showInfoWindow();
                }

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                render(noticeInfoBean, view, getString(R.string.car_no_location));
                LatLng position = new LatLng(Double.parseDouble(noticeInfoBean.getLatitude()), Double.parseDouble(noticeInfoBean.getLongitude()));
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
