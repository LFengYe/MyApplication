package com.cxgps.vehicle.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.SeekBar;

import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.base.BaseTrackActivity;
import com.cxgps.vehicle.bean.HistoryInfo;
import com.cxgps.vehicle.fragment.BaiduTrackplayFragment;
import com.cxgps.vehicle.fragment.GoogleTrackplayFragment;
import com.cxgps.vehicle.interf.IFragmentUpdateData;
import com.cxgps.vehicle.interf.ITrackplayInter;


/**
 * Created by taosong on 16/7/21.
 */
public class TrackplayActivity extends BaseTrackActivity implements SeekBar.OnSeekBarChangeListener,IFragmentUpdateData {


    private ITrackplayInter trackplayInter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        checkMapApp();
        initView();
    }

    @Override
    public void initView() {
        super.initView();
        mTrackSeekbar.setMax( AppContext.getInstance().getHistoryInfos().size()-1 );
        mSpeedBar.setMax(5);
        mSpeedBar.setProgress(3);
        mTrackSeekbar.setOnSeekBarChangeListener(this);
        mSpeedBar.setOnSeekBarChangeListener(this);
        mTrackplay.setBackgroundResource(R.mipmap.history_stop);



    }

    @Override
    public void initData() {
        super.initData();

    }

    // 判断地图
    public  void checkMapApp(){

        int   maptype  =Integer.parseInt(AppContext.getMapWithKey());

        Bundle bundle =  getIntent().getExtras();

        if (maptype ==1){ // 百度
            trackplayInter= new BaiduTrackplayFragment();

            trackplayInter.historyData(bundle);
            trackplayInter.setIFragmentUpdateListener(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, (Fragment) trackplayInter).commit();

        }else {  // 谷歌
            trackplayInter = new GoogleTrackplayFragment();
            trackplayInter.historyData(bundle);
            trackplayInter.setIFragmentUpdateListener(this);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,(Fragment) trackplayInter).commit();

        }
    }





    public void menuRightTop(View view) {

        super.menuRightTop(view);

        int viewId = view.getId();
        switch (viewId) {

            case R.id.tab_road_title:

                setTrafficeState(trackplayInter.changeRoad());
                break;
            case R.id.tab_map_title:

                setMapyViewType(trackplayInter.changeMaptype());
                break;

            case R.id.car_track_play:
                upDateActivityView(trackplayInter.startPlay());
                break;
            case R.id.car_track_increase:
               // trackplayInter.setPlayProgress(mTrackSeekbar.getProgress()+10);
                trackplayInter.backAndGoWithTimmer(1);


                break;
            case R.id.car_track_reduce:
                // trackplayInter.setPlayProgress(mTrackSeekbar.getProgress()-10);
                trackplayInter.backAndGoWithTimmer(-1);
                break;

        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


        if (seekBar.getId() == R.id.car_track_seekbar){



            trackplayInter.setPlayProgress(seekBar.getProgress());
        }else {

            trackplayInter.getSpeedProgress(seekBar.getProgress());
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {


        if (seekBar.getId() == R.id.car_track_seekbar){



            trackplayInter.setPlayProgress(seekBar.getProgress());
        }else {

            trackplayInter.getSpeedProgress(seekBar.getProgress());
        }
    }


    @Override
    public void fragmentData(int progress, int playstate,HistoryInfo bean) {



        Message message = new Message();
        Bundle  bundle = new Bundle();
        bundle.putSerializable("historyInfo",bean);
        message.setData(bundle);
        message.obj = new String[]{progress+"",playstate+""};

        handler.sendMessage(message);

    }


    private Handler  handler  = new Handler(){


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            String[] ss = (String[]) msg.obj;

            HistoryInfo temInfo =  null;
            try {
                temInfo = (HistoryInfo) msg.getData().getSerializable("historyInfo");
            }catch (Exception e){
                e.printStackTrace();
            }

            if (null != temInfo) {

                setTopTitle(temInfo.getNowDate(), temInfo.getCarNumber(), temInfo.getCarSpeed(), temInfo.getCarDistriction());

                mTrackSeekbar.setProgress(Integer.parseInt(ss[0]));
                upDateActivityView(Integer.parseInt(ss[1]));
            }
        }
    };
}
