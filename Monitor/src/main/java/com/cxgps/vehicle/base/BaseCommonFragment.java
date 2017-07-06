package com.cxgps.vehicle.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.cxgps.vehicle.AppConfig;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.utils.UIHelper;
import com.cxgps.vehicle.widget.EmptyLayout;
import com.kymjs.rxvolley.client.HttpCallback;

import java.io.Serializable;
import java.util.Map;

import butterknife.ButterKnife;

/**
 * Created by taosong on 16/12/22.
 */

public abstract class BaseCommonFragment<T extends Serializable> extends BaseFragment {

    protected int mId;

    protected EmptyLayout mEmptyLayout;


    protected WebView mWebView;

    protected T mDetail;

    // 请求的参数
    protected String startTime = "";

    protected String endTime ="";

    protected String systemNoList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mId = getActivity().getIntent().getIntExtra("type", 0);


        if (mId == AppConfig.REPORT){

            Bundle bundle =    getActivity().getIntent().getExtras();
            startTime  = bundle.getString("startTime");

            endTime  = bundle.getString("endTime");

            systemNoList  = bundle.getString("systemNo");

        }

    }
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_common_detail;
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);



        ButterKnife.bind(this, view);
        initView(view);
        initData();
        requestData(false);
        return view;
    }

    @Override
    public void initView(View view) {
        mEmptyLayout = (EmptyLayout) view.findViewById(R.id.error_layout);
        mWebView = (WebView) view.findViewById(R.id.webview);
        UIHelper.initWebView(mWebView);
    }

    private void requestData(boolean refresh) {
        sendRequestDataForNet();
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        recycleWebView();
    }

    private void recycleWebView() {
        if (mWebView != null) {
            mWebView.setVisibility(View.GONE);
            mWebView.removeAllViews();
            mWebView.destroy();
            mWebView = null;
        }
    }



    protected HttpCallback mDetailHeandler = new HttpCallback(){

        @Override
        public void onFailure(int errorNo, String strMsg) {
            super.onFailure(errorNo, strMsg);
            executeOnLoadDataError();
        }

        @Override
        public void onSuccess(Map<String, String> headers, byte[] t) {
            super.onSuccess(headers, t);

            try {
                T detail = parseData(new String(t));
                if (detail != null) {
                    mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                    executeOnLoadDataSuccess(detail);

                } else {
                    executeOnLoadDataError();
                }
            } catch (Exception e) {
                e.printStackTrace();
                executeOnLoadDataError();
            }
        }
    };





    protected void executeOnLoadDataSuccess(T detail) {
        this.mDetail = detail;
        if (this.mDetail == null || TextUtils.isEmpty(this.getWebViewBody(detail))) {
            executeOnLoadDataError();
            return;
        }

        mWebView.loadUrl(this.getWebViewBody(detail));


    }

    protected void executeOnLoadDataError() {
        mEmptyLayout.setErrorType(EmptyLayout.NETWORK_ERROR);
        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
                requestData(true);
            }
        });
    }


    // 从网络中读取数据
    protected abstract void sendRequestDataForNet();

    // 解析数据
    protected abstract T parseData(String is);

    // 返回填充到webview中的内容
    protected abstract String getWebViewBody(T detail);


}
