package com.cxgps.vehicle.base;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cxgps.vehicle.R;
import com.cxgps.vehicle.bean.CarLocationBean;

import butterknife.Bind;


/**
 * Created by taosong on 16/7/23.
 */
public class BaseZuizongActivity extends BaseActivity {

    @Bind(R.id.tab_road_title)
    TextView roadClick;

    @Bind(R.id.tab_map_title)
    TextView  maptypeClick;

    protected CarLocationBean  locationBean;


    @Override
    protected int getLayoutId() {
        return R.layout.app_zuizong;
    }


    @Override
    protected boolean hasBackButton() {
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        try {
            locationBean = (CarLocationBean) bundle.getSerializable("carLocation");
        }catch (Exception e){
            e.printStackTrace();
        }
        }

    @Override
    public void initView() {

    }

    @Override
    public void initData() {

        setActionBarTitle(R.string.main_title_tracking);
    }



    public void menuRightTop(View view){

        int viewId = view.getId();
        switch (viewId){

            case  R.id.tab_road_title:
                break;
            case R.id.tab_map_title:
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

}
