package com.cxgps.vehicle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.View;

import com.cxgps.vehicle.AppConfig;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.base.BaseMonitorActivity;
import com.cxgps.vehicle.fragment.BaiduMonitorFragment;
import com.cxgps.vehicle.fragment.GoogleMonitorFragment;
import com.cxgps.vehicle.interf.IMonitorInter;
import com.cxgps.vehicle.service.UpdateManager;

/**
 * Created by taosong on 16/6/1.
 */
public class MainActivity  extends BaseMonitorActivity {

    private IMonitorInter monitorInter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setActionBarTitle(R.string.app_name);

        checkMapApp();

        //checkUpdate();
    }

    // 判断地图
    public  void checkMapApp(){

        int   maptype  =Integer.parseInt(AppContext.getMapWithKey());
        Bundle bundle = getIntent().getExtras();

        if (maptype ==1){ // 百度
            monitorInter= new BaiduMonitorFragment();
            ((Fragment) monitorInter).setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content, (Fragment) monitorInter).commit();

        }else {  // 谷歌
            monitorInter = new GoogleMonitorFragment();
            ((Fragment) monitorInter).setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.content,(Fragment) monitorInter).commit();

        }
    }




    public void menuRightTop(View view){

        super.menuRightTop(view);

        int viewId = view.getId();
        switch (viewId){

            case  R.id.tab_road_title:

                setTrafficeState(monitorInter.changeRoad());
                break;
            case R.id.tab_map_title:

                setMapyViewType(monitorInter.changeMaptype());
                break;
            case R.id.tab_map_location:

                monitorInter.changeLocation(setCarOrRenLocation());
                break;
            case R.id.leftClickBtn:
                monitorInter.changeBefore();
                break;
            case R.id.rightClickBtn:
                monitorInter.changeAfter();
                break;
            case R.id.map_add_level:

                mAddLevel.setEnabled(monitorInter.changeAdd());
                break;
            case R.id.map_reduce_level:
                mReducLevel.setEnabled(monitorInter.changeReduct());
                break;

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1000) {

            if (data != null) {

                monitorInter.reflushLocation(data.getStringExtra("systemNo"));
            }
        }
    }




    private void checkUpdate() {
        if (!AppContext.get(AppConfig.KEY_CHECK_UPDATE, true)) {


            return;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                new UpdateManager(MainActivity.this, false).checkUpdate();
            }
        }, 2000);
    }
}
