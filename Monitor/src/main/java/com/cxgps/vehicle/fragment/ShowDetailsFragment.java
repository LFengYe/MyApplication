package com.cxgps.vehicle.fragment;

import com.alibaba.fastjson.JSON;
import com.cxgps.vehicle.api.remote.VehicleApi;
import com.cxgps.vehicle.base.BaseCommonFragment;
import com.cxgps.vehicle.bean.ResponseBean;
import com.cxgps.vehicle.bean.ShowUrlBean;
import com.cxgps.vehicle.widget.EmptyLayout;

/**
 * Created by taosong on 16/12/22.
 */

public class ShowDetailsFragment extends BaseCommonFragment<ShowUrlBean> {


    @Override
    protected void sendRequestDataForNet() {
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        VehicleApi.requestCommonDetail(mId, systemNoList, startTime, endTime, mDetailHeandler);

    }

    @Override
    protected ShowUrlBean parseData(String is) {
        ResponseBean responseBean = JSON.parseObject(new String(is),ResponseBean.class);

        if (responseBean.isRequestFlag()){

            ShowUrlBean bean = JSON.parseObject(responseBean.getData(),ShowUrlBean.class);

            return bean;
        }



        return null;
    }

    @Override
    protected String getWebViewBody(ShowUrlBean detail) {
        if (null == detail){

            return "";
        }else {
            return mDetail.getShowUrl();
        }
    }

    @Override
    public void initData() {

    }
}
