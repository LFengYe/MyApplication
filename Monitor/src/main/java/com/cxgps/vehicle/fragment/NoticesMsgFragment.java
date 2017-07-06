package com.cxgps.vehicle.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.adapter.NoticesListAdapter;
import com.cxgps.vehicle.api.remote.VehicleApi;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.NoticeInfoBean;
import com.cxgps.vehicle.bean.ResponseBean;
import com.cxgps.vehicle.bean.UserInfo;
import com.cxgps.vehicle.ui.ShowNoticeDetailActivity;
import com.cxgps.vehicle.widget.EmptyLayout;
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by taosong on 16/12/22.
 */

public class NoticesMsgFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener{

    @Bind(R.id.superRefreshLayout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;


    @Bind(R.id.error_layout)
    protected EmptyLayout mEmptyLayout;

    @Bind(R.id.listView)
    protected ListView mListView;

    private Handler dataHandler = new Handler(Looper.getMainLooper());


    private ArrayList<NoticeInfoBean> noticeInfoBeans = new ArrayList<>();

    private NoticesListAdapter noticesAdapter;

    private String firstTime = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_base_list, null);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }
    @Override
    public void initView(View view) {
        super.initView(view);

        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.swiperefresh_color1, R.color.swiperefresh_color2,
                R.color.swiperefresh_color3, R.color.swiperefresh_color4);



        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);


        mEmptyLayout.setOnLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRefresh();
            }
        });
    }


    @Override
    public void initData() {
        super.initData();
        noticesAdapter = new NoticesListAdapter(getActivity(),noticeInfoBeans);
        mListView.setAdapter(noticesAdapter);
        noticesAdapter.notifyDataSetChanged();

        onRefresh();
    }



    @Override
    public void onRefresh() {
        // 设置顶部正在刷新
        mListView.setSelection(0);
        setSwipeRefreshLoadingState();
        noticeInfoBeans.clear();
        firstTime = "";
        loadMsg();

    }

    /**************************加载数据****start**********************************/

    public  void  loadMsg(){

        showWaitDialog();

        UserInfo userInfo =  AppContext.getInstance().getLoginUser();

        if (Boolean.valueOf(userInfo.getuLoginFlag())){

            VehicleApi.getMessageList(userInfo.getuAccount(), firstTime,messageListener);

        }

    }


    private HttpCallback messageListener = new HttpCallback() {
        @Override
        public void onSuccess(Map<String, String> headers, byte[] t) {
            super.onSuccess(headers, t);
            hideWaitDialog();
            setSwipeRefreshLoadedState();
            ResponseBean responseBean = JSON.parseObject(new String(t), ResponseBean.class);

            if (responseBean!=null ) {


                if (responseBean.isRequestFlag()) {


                    ArrayList<NoticeInfoBean> carsInfoList = (ArrayList<NoticeInfoBean>) JSON.parseArray(responseBean.getData(), NoticeInfoBean.class);


                    dataHandler.post(new DataHandler(carsInfoList));




                }else {
                    dataHandler.post(new DataHandler(null));
                }
            }

        }

        @Override
        public void onFailure(int errorNo, String strMsg) {
            super.onFailure(errorNo, strMsg);
            hideWaitDialog();
            setSwipeRefreshLoadedState();
            dataHandler.post(new DataHandler(null));
        }
    };






    public   class DataHandler implements  Runnable{


        private ArrayList<NoticeInfoBean> data;
        private DataHandler(ArrayList<NoticeInfoBean> data){

            this.data = data;
        }


        @Override
        public void run() {

            if (data!=null && data.size()>0){

                noticeInfoBeans.addAll(data);
            }
            if (noticeInfoBeans.size() >0 ) {
                mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                noticesAdapter.notifyDataSetChanged(removeDuplicate(noticeInfoBeans));
            }else {
                mSwipeRefreshLayout.setVisibility(View.GONE);
                mEmptyLayout.setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
            }
        }
    }




    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (noticesAdapter == null || noticesAdapter.getCount() == 0) {
            return;
        }


        // 判断是否滚动到底部
        boolean scrollEnd = false;
        int lastPosition = 0;
        try {
            lastPosition  =  view.getLastVisiblePosition();

            if (lastPosition == noticeInfoBeans.size()-1){

                scrollEnd = true;
            }else {
                scrollEnd = false;
            }


        } catch (Exception e) {
            scrollEnd = false;
        }


        if (scrollEnd){

            NoticeInfoBean  noticeInfoBean  = (NoticeInfoBean) view.getItemAtPosition(lastPosition);
            firstTime = noticeInfoBean.getTime();
            loadMsg();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        NoticeInfoBean  noticeInfoBean = (NoticeInfoBean) view.getTag(R.id.tv_title);
        if (null != noticeInfoBean ) {
            Bundle bundle = new Bundle();

            bundle.putSerializable("noticeInfo", noticeInfoBean);
            Intent intent  = new Intent(getActivity(), ShowNoticeDetailActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }




    public static ArrayList<NoticeInfoBean> removeDuplicate(ArrayList<NoticeInfoBean> list)
    {
        Set set = new LinkedHashSet<NoticeInfoBean>();
        set.addAll(list);
        list.clear();
        list.addAll(set);
        return list;
    }

    /** 设置顶部正在加载的状态 */
    protected void setSwipeRefreshLoadingState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
            // 防止多次重复刷新
            mSwipeRefreshLayout.setEnabled(false);

            mEmptyLayout.setErrorType(EmptyLayout.NETWORK_LOADING);

        }
    }

    /** 设置顶部加载完毕的状态 */
    protected void setSwipeRefreshLoadedState() {
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(false);
            mSwipeRefreshLayout.setEnabled(true);

        }
    }


}
