package com.cxgps.vehicle.base;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cxgps.vehicle.AppConfig;
import com.cxgps.vehicle.R;

import butterknife.Bind;


/**
 * Created by taosong on 16/7/23.
 */
public  class  BaseTrackActivity extends  BaseActivity {



    @Bind(R.id.tab_road_title)
    TextView roadClick;

    @Bind(R.id.tab_map_title)
    TextView  maptypeClick;


    @Bind(R.id.speedSeekbar)
    protected   SeekBar  mSpeedBar;

    @Bind(R.id.car_track_seekbar)
    protected  SeekBar  mTrackSeekbar;


    @Bind(R.id.car_track_play)
    protected  ImageView mTrackplay;

    @Bind(R.id.car_track_increase)
    protected  ImageView mTrackincrease;

    @Bind(R.id.car_track_reduce)
    protected  ImageView mTrackreduce;


    @Bind(R.id.main_container)
    protected FrameLayout mContent;


    @Bind(R.id.playresult)
    protected   TextView  mPlayResult;


    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_trackplay;
    }



    @Override
    public void initView() {

    }

    @Override
    public void initData() {

        setActionBarTitle(R.string.main_title_trackplay);


    }

    // 设置顶部的内容 
    public void setTopTitle(String time,String carNumber,String speed,String distrion) {

        try {
            mPlayResult.setText(String.format(getString(R.string.history_seekbar_title), carNumber, speed, distrion, time));
        }catch (Exception e){
            e.printStackTrace();
        }
        }




    public void menuRightTop(View view){

        int viewId = view.getId();
        switch (viewId){

            case  R.id.tab_road_title:
                break;
            case R.id.tab_map_title:
                break;
            case R.id.car_track_play:
                break;
            case R.id.car_track_increase:
                break;
            case R.id.car_track_reduce:
                break;



        }
    }

    public void setMapyViewType(boolean mapFlag){



        Drawable img_on, img_off;
        Resources res = getResources();
        img_off = res.getDrawable(R.mipmap.monitor_ico_earth);
        img_on  = res.getDrawable(R.mipmap.monitor_ico_earthselected);
        //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示

        if (mapFlag){
            img_on.setBounds(0, 0, img_on.getMinimumWidth(), img_on.getMinimumHeight());
            maptypeClick.setCompoundDrawables(null, img_on, null, null); //设置左图标
        }else {
            img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
            maptypeClick.setCompoundDrawables(null, img_off, null, null); //设置左图标
        }



    }


    public  void setTrafficeState(boolean flag){

        Drawable img_on, img_off;
        Resources res = getResources();
        img_off = res.getDrawable(R.mipmap.monitor_ico_lukuang);
        img_on  = res.getDrawable(R.mipmap.monitor_ico_lukuang_pre);
        //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示

        if (flag){
            img_on.setBounds(0, 0, img_on.getMinimumWidth(), img_on.getMinimumHeight());
            roadClick.setCompoundDrawables(null, img_on, null, null); //设置左图标
        }else {
            img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
            roadClick.setCompoundDrawables(null, img_off, null, null); //设置左图标
        }
    }



    public void upDateActivityView(int staste){

        try {

            switch (staste) {

                case AppConfig.PlayIng:

                    mTrackplay.setBackgroundResource(R.mipmap.history_play);

                    break;
                case AppConfig.PlayStop:

                    mTrackplay.setBackgroundResource(R.mipmap.history_stop);
                    break;
                case AppConfig.RePlay:

                    mTrackplay.setBackgroundResource(R.mipmap.history_stop);
                    break;

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
