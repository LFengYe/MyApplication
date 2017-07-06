package com.cxgps.vehicle.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.Poi;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.cxgps.vehicle.AppConfig;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.CarLocationBean;
import com.cxgps.vehicle.bean.NavigationBean;
import com.cxgps.vehicle.bean.SimpleBackPage;
import com.cxgps.vehicle.service.LocationService;
import com.cxgps.vehicle.ui.BNDemoGuideActivity;
import com.cxgps.vehicle.utils.DialogHelper;
import com.cxgps.vehicle.utils.UIHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by taosong on 16/7/21.
 */
public class NavigationFragment extends BaseFragment implements  View.OnClickListener{

    //public LocationService locationService;
    public Vibrator mVibrator;


    @Bind(R.id.changePosition)
    ImageView mChangePosition;


    @Bind(R.id.tv_start_position)
    TextView  mStartPosition;

    @Bind(R.id.tv_end_position)
    TextView  mEndPosition;


    @Bind(R.id.navigaBtn)
    TextView  mNavigaBtn;

    private Handler  navigationHandler = new Handler();


    private ProgressDialog  progressDialog;

    private String mSDCardPath = null;

    private static final String APP_FOLDER_NAME = "BNSDKSimpleDemo";


    private   BNRoutePlanNode sNode = null;
    private   BNRoutePlanNode eNode = null;

    private CarLocationBean carLocationBean = null;

    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_navigation, container,
                false);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }



    @Override
    public void initView(View view) {
        super.initView(view);

        mNavigaBtn.setOnClickListener(this);
        mChangePosition.setOnClickListener(this);
        mStartPosition.setOnClickListener(this);
        mEndPosition.setOnClickListener(this);
        progressDialog = DialogHelper.getWaitDialog(getActivity(),getString(R.string.tips_location_ing));
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        initLocation();
        startLocation();

        initNavigation();

    }

    @Override
    public void initData() {
        super.initData();

        Bundle  bundle = getArguments();

        try {
            carLocationBean = (CarLocationBean) bundle.getSerializable("carLocation");

            if (carLocationBean != null) {


                mEndPosition.setText(carLocationBean.getCarNumber());

                eNode = new BNRoutePlanNode(Double.parseDouble(carLocationBean.getCarLng()), Double.parseDouble(carLocationBean.getCarLat()), mStartPosition.getText().toString(), null, BNRoutePlanNode.CoordinateType.BD09LL);


            }
        }catch (Exception e){
            e.printStackTrace();
            carLocationBean = null;
        }
    }

    @Override
    public void onClick(View v) {
        // 0 表示起点,1 表示 终点
        Bundle  bundle = new Bundle();

        switch (v.getId()) {

            case R.id.tv_start_position:
                bundle.putInt("mode", 0);
                UIHelper.showSimpleBackForResult(getActivity(), 1000, SimpleBackPage.SEARCHADDRESSLIST, bundle);

                break;

            case R.id.tv_end_position:

                bundle.putInt("mode",1);
                UIHelper.showSimpleBackForResult(getActivity(), 1000, SimpleBackPage.SEARCHADDRESSLIST, bundle);

                break;


            case R.id.changePosition:
                changeStartAndEnd();

                break;

            case R.id.navigaBtn:

                showWaitDialog();

                if (checkSearchText(mStartPosition.getText().toString().trim(),mEndPosition.getText().toString())) {
                    routeplanToNavi();
                }
                break;

        }


    }



    // 起始位置进行切换
    boolean  isChangeFlag = false ;
    public void changeStartAndEnd(){

        String tempStart = mStartPosition.getText().toString().trim();

        String tempEnd = mEndPosition.getText().toString().trim();


        if (!checkSearchText(tempStart,tempEnd))
            return;

        BNRoutePlanNode  tempNode = null;

        if (!isChangeFlag){


            tempNode  = sNode;

            sNode = eNode;

            eNode = tempNode;

            mStartPosition.setText(tempEnd);
            mEndPosition.setText(tempStart);

        }else {




            tempNode  = eNode;

            eNode = sNode;

            sNode = tempNode;



            mStartPosition.setText(tempStart);
            mEndPosition.setText(tempEnd);

        }




    }


    // 检查输入框的内容
    public boolean  checkSearchText(String startStr,String endStr){

        if (startStr.length() == 0 ){
            AppContext.showToast(R.string.transform_start_hit);
            return false;
        }

        if (endStr.length() == 0 ){
            AppContext.showToast(R.string.transform_start_hit);
            return  false;
        }

        if (sNode == null){
            AppContext.showToast(R.string.transform_start_hit);
            return  false;
        }

        if (eNode == null){
            AppContext.showToast(R.string.transform_start_hit);
            return false;
        }

        return  true;



    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (data == null){

            return;

        }


        if (resultCode == 1000){


            Bundle  bundle =   data.getExtras();

            int mode =   bundle.getInt("mode");
            NavigationBean navigation = (NavigationBean) bundle.getSerializable("navigation");

            navigationHandler.post(new DataHandler(mode, navigation));


        }
    }


    public class DataHandler implements  Runnable{


        private int a ;

        private NavigationBean  data;

        private DataHandler(int what,NavigationBean b){

            this.a = what;
            this.data = b;
        }


        @Override
        public void run() {

            switch (a){

                case 0:

                    hideWaitDialog();
                    mStartPosition.setText(data.getTitleName());

                    sNode = new BNRoutePlanNode(data.getSulng(), data.getSulat(), mStartPosition.getText().toString(), null, BNRoutePlanNode.CoordinateType.BD09LL);



                    break;

                case 1:
                    hideWaitDialog();
                    mEndPosition.setText(data.getTitleName());

                    eNode = new BNRoutePlanNode(data.getSulng(), data.getSulat(), mStartPosition.getText().toString(), null, BNRoutePlanNode.CoordinateType.BD09LL);

                    break;




            }



        }
    }



    /*******************************导航**************************************/

    private void initNavigation(){

        if (initDirs()) {
            initNavi();
        }





    }




    private boolean initDirs() {
        mSDCardPath = getSdcardDir();
        if (mSDCardPath == null) {
            return false;
        }
        File f = new File(mSDCardPath, APP_FOLDER_NAME);
        if (!f.exists()) {
            try {
                f.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }

    String authinfo = null;



    /**
     * 内部TTS播报状态回传handler
     */
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    Log.i("TAG", "Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    Log.i("TAG","Handler : TTS play end");
                    break;
                }
                default :
                    break;
            }
        }
    };


    /**
     * 内部TTS播报状态回调接口
     */
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
//            showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
//            showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };





    private void initNavi() {

        BNOuterTTSPlayerCallback ttsCallback = null;

        BaiduNaviManager.getInstance().init(getActivity(), mSDCardPath, APP_FOLDER_NAME, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                if (0 == status) {
                    authinfo = "key校验成功!";
                } else {
                    authinfo = "key校验失败, " + msg;
                }
                Log.i("TAG",authinfo);
            }

            public void initSuccess() {

                initSetting();
            }

            public void initStart() {

            }

            public void initFailed() {

            }


        }, null, ttsHandler, null);

    }

    private String getSdcardDir() {
        if (Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED)) {
            return Environment.getExternalStorageDirectory().toString();
        }
        return null;
    }

    private void routeplanToNavi() {

        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);

            AppContext.getInstance().setTempEndNode(eNode);
            BaiduNaviManager.getInstance().launchNavigator(getActivity(), list, 1, true, new DemoRoutePlanListener(sNode));
        }
    }




    public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

        private BNRoutePlanNode mBNRoutePlanNode = null;

        public DemoRoutePlanListener(BNRoutePlanNode node) {
            mBNRoutePlanNode = node;
        }

        @Override
        public void onJumpToNavigator() {
			/*
			 * 设置途径点以及resetEndNode会回调该接口
			 */

            Bundle bundle = new Bundle();
            bundle.putSerializable(AppConfig.ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);

            Intent intent = new Intent(getActivity(), BNDemoGuideActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            hideWaitDialog();
        }

        @Override
        public void onRoutePlanFailed() {
            // TODO Auto-generated method stub
            hideWaitDialog();
            AppContext.showToast("算路失败");
        }
    }

    private void initSetting(){
        BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }


    private BNOuterTTSPlayerCallback mTTSCallback = new BNOuterTTSPlayerCallback() {

        @Override
        public void stopTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "stopTTS");
        }

        @Override
        public void resumeTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "resumeTTS");
        }

        @Override
        public void releaseTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "releaseTTSPlayer");
        }

        @Override
        public int playTTSText(String speech, int bPreempt) {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "playTTSText" + "_" + speech + "_" + bPreempt);

            return 1;
        }

        @Override
        public void phoneHangUp() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneHangUp");
        }

        @Override
        public void phoneCalling() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "phoneCalling");
        }

        @Override
        public void pauseTTS() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "pauseTTS");
        }

        @Override
        public void initTTSPlayer() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "initTTSPlayer");
        }

        @Override
        public int getTTSState() {
            // TODO Auto-generated method stub
            Log.e("test_TTS", "getTTSState");
            return 1;
        }
    };







    /***********************************获取当前位置*****************************************/

    private LocationService locationService;


    private  void initLocation(){




        hideWaitDialog();
        // -----------location config ------------

        int checkPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION);
        if (checkPermission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            Log.d("TTTT", "弹出提示");
            return;
        } else {


            locationService = new LocationService(getActivity());
            mVibrator =(Vibrator)getActivity().getSystemService(Service.VIBRATOR_SERVICE);
        }



        //获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
        locationService.registerListener(mListener);
        //注册监听
        int type = getActivity().getIntent().getIntExtra("from", 0);
        if (type == 0) {
            locationService.setLocationOption(locationService.getDefaultLocationClientOption());
        } else if (type == 1) {
            locationService.setLocationOption(locationService.getOption());
        }

    }







    private void startLocation(){

        locationService.start();// 定位SDK

    }

    private void stopLocation(){
        progressDialog.cancel();
        locationService.stop(); //停止定位服务
    }


    private void destoryLocation(){

        locationService.unregisterListener(mListener); //注销掉监听
        stopLocation();


    }


    /*****
     * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
     *
     */
    private BDLocationListener mListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // TODO Auto-generated method stub
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {
                //location.getLatitude();
                //  location.getLongitude();

                if (location.getPoiList() != null && !location.getPoiList().isEmpty()) {

                    Poi poi = (Poi) location.getPoiList().get(0);

                    NavigationBean navigation =new NavigationBean() ;

                    navigation.setTitleName(poi.getName());
                    navigation.setSulat(location.getLatitude());
                    navigation.setSulng(location.getLongitude());
                    navigationHandler.post(new DataHandler(0, navigation));


                    progressDialog.dismiss();
                    destoryLocation();

                }



            }
        }
    };



    /***
     * Stop location service
     */
    @Override
    public void onStop() {
        // TODO Auto-generated method stub
        locationService.unregisterListener(mListener); //注销掉监听
        locationService.stop(); //停止定位服务
        super.onStop();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
