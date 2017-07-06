package com.cxgps.vehicle.ui;

import android.support.v4.app.Fragment;

import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.base.BaseActivity;
import com.cxgps.vehicle.bean.NoticeInfoBean;
import com.cxgps.vehicle.fragment.BDNoticeDetailFragment;
import com.cxgps.vehicle.fragment.GGNoticeDetailFragment;
import com.cxgps.vehicle.interf.INoticeDetail;

/**
 * Created by taosong on 16/12/30.
 */

public class ShowNoticeDetailActivity extends BaseActivity {

    INoticeDetail   iNoticeDetail = null;

    private NoticeInfoBean  noticeInfoBean = null;



    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.app_notice_detail;
    }

    @Override
    public void initView() {

        noticeInfoBean = (NoticeInfoBean) getIntent().getSerializableExtra("noticeInfo");

        if (noticeInfoBean!=null){

            setActionBarTitle(noticeInfoBean.getVehNoF());
        }else {

            setActionBarTitle(R.string.notice_detial_title);
        }

        checkMapApp();
    }

    @Override
    public void initData() {

    }




    // 判断地图
    public  void checkMapApp(){

        int   maptype  =Integer.parseInt(AppContext.getMapWithKey());

        if (maptype ==1){ // 百度
            iNoticeDetail= new BDNoticeDetailFragment();
            iNoticeDetail.loadData(noticeInfoBean);

            getSupportFragmentManager().beginTransaction().replace(R.id.main_content, (Fragment) iNoticeDetail).commit();

        }else {  // 谷歌
            iNoticeDetail = new GGNoticeDetailFragment();
            iNoticeDetail.loadData(noticeInfoBean);
            getSupportFragmentManager().beginTransaction().replace(R.id.main_content,(Fragment) iNoticeDetail).commit();

        }
    }

}
