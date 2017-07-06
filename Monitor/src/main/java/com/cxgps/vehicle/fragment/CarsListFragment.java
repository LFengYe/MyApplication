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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.alibaba.fastjson.JSON;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.adapter.CarsListAdapter;
import com.cxgps.vehicle.api.remote.VehicleApi;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.CarsInfo;
import com.cxgps.vehicle.bean.ResponseBean;
import com.cxgps.vehicle.bean.UserInfo;
import com.cxgps.vehicle.ui.SearchActivity;
import com.cxgps.vehicle.utils.DialogHelper;
import com.cxgps.vehicle.widget.EmptyLayout;
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CarsListFragment extends BaseFragment implements RadioGroup.OnCheckedChangeListener, SwipeRefreshLayout.OnRefreshListener,AdapterView.OnItemClickListener,
        AbsListView.OnScrollListener{

    @Bind(R.id.superRefreshLayout)
    protected SwipeRefreshLayout mSwipeRefreshLayout;


    @Bind(R.id.error_layout)
    protected EmptyLayout mEmptyLayout;

    @Bind(R.id.listView)
    protected ListView mListView;

    @Bind(R.id.cars_state_group)
    RadioGroup mCarsRG;

    @Bind(R.id.cars_all)
    RadioButton mAllDeviceView_sub;

    @Bind(R.id.car_online)
    RadioButton  mAllDeviceOnView_sub;


    @Bind(R.id.car_outline)
    RadioButton  mAllDeviceOffView_sub;

    private Handler  dataHandler = new Handler(Looper.getMainLooper());

    private CarsListAdapter  carsListAdapter;

    private ArrayList<CarsInfo>  carsInfos = new ArrayList<>();


    private int mType = 6;
    private int pageIndex = 0;

    private int pageCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cars_list, null);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        mCarsRG.setOnCheckedChangeListener(this);


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
        carsListAdapter = new CarsListAdapter(getActivity(),carsInfos);
        mListView.setAdapter(carsListAdapter);
        loadCarsList();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {


        switch (checkedId){

            case R.id.cars_all:

                pageIndex = 0;

                mType = 6;

                break;

            case R.id.car_online:

                pageIndex = 0;

                mType = 7;

                break;

            case R.id.car_outline:

                pageIndex = 0;

                mType = 3;
                break;


        }
        onRefresh();

    }






    /**************************加载数据****start**********************************/
    private void loadCarsList(){


        showWaitDialog();

        UserInfo userInfo =  AppContext.getInstance().getLoginUser();

        if (Boolean.valueOf(userInfo.getuLoginFlag())){

            VehicleApi.getCarList(userInfo.getUserName(), userInfo.getuLoginType(), pageIndex, mType, carsHandler);

        }
    }


    private HttpCallback carsHandler = new HttpCallback() {


        @Override
        public void onFailure(int errorNo, String strMsg) {
            super.onFailure(errorNo, strMsg);

            hideWaitDialog();
            setSwipeRefreshLoadedState();

            dataHandler.post(new DataHandler(1,new  ArrayList<CarsInfo>()));
        }

        @Override
        public void onSuccess(Map<String, String> headers, byte[] t) {
            super.onSuccess(headers, t);



            hideWaitDialog();
            setSwipeRefreshLoadedState();
            ResponseBean responseBean = JSON.parseObject(new String(t), ResponseBean.class);

            if (responseBean!=null ) {

                pageCount =Integer.parseInt(responseBean.getPageCounts());
                dataHandler.post(new DataHandler(0, new int[]{responseBean.getCarCount(),responseBean.getOnLine(),responseBean.getOutLine()}));


                if (responseBean.isRequestFlag()) {


                    ArrayList<CarsInfo> carsInfoList = (ArrayList<CarsInfo>) JSON.parseArray(responseBean.getData(), CarsInfo.class);


                    dataHandler.post(new DataHandler(1,carsInfoList));




                }else {

                    dataHandler.post(new DataHandler(1,new  ArrayList<CarsInfo>()));
                }
            }
        }
    };

    @Override
    public void onRefresh() {
        // 设置顶部正在刷新
        mListView.setSelection(0);
        setSwipeRefreshLoadingState();
        carsInfos.clear();

        pageIndex = 0;

        pageCount = 0;
        loadCarsList();

    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (carsListAdapter == null || carsListAdapter.getCount() == 0) {
            return;
        }


        // 判断是否滚动到底部
        boolean scrollEnd = false;
        try {
            int lastPosition =  view.getLastVisiblePosition();

            if (lastPosition == carsInfos.size()-1){

                scrollEnd = true;
            }else {
                scrollEnd = false;
            }


        } catch (Exception e) {
            scrollEnd = false;
        }


        if (scrollEnd && pageIndex < pageCount){



            pageIndex++;

            if (pageIndex < pageCount) {
                loadCarsList();
            }

        }

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }


    public   class DataHandler implements  Runnable{

        private int what;
        private int[] number;

        private ArrayList<CarsInfo> data;

        private DataHandler(int what,int[] numbers){

            this.what = what;
            this.number = numbers;

        }

        private DataHandler(int what,ArrayList<CarsInfo> data){
            this.what = what;
            this.data = data;
        }

        @Override
        public void run() {

            switch (what){

                case 0:

                    mAllDeviceView_sub.setText( String.format(getString(R.string.cars_all_title),"("+number[0]+")"));
                    mAllDeviceOnView_sub.setText(String.format(getString(R.string.cars_online_title),"("+number[1]+")"));
                    mAllDeviceOffView_sub.setText(String.format(getString(R.string.cars_outline_title),"("+number[2]+")"));

                    break;

                case 1:

                    if (data!=null && data.size()>0){

                        carsInfos.addAll(data);
                    }
                    if (carsInfos.size() >0 ) {
                        mEmptyLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);
                        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
                        carsListAdapter.notifyDataSetChanged(removeDuplicate(carsInfos));
                    }else {
                        mSwipeRefreshLayout.setVisibility(View.GONE);
                        mEmptyLayout.setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
                    }
                    break;


            }


        }
    }


    public static ArrayList<CarsInfo> removeDuplicate(ArrayList<CarsInfo> list)
    {
        Set set = new LinkedHashSet<CarsInfo>();
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





    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CarsInfo softwareDes  = (CarsInfo) view.getTag(R.id.tv_title);

        if (softwareDes != null) {

            String remarks = softwareDes.getCarOutType();


            Pattern pattern = Pattern.compile("[0-9]+");
            Matcher matcher = pattern.matcher((CharSequence) remarks);
            boolean result = matcher.matches();
            if (result &&Integer.parseInt(remarks) == 0) {

                DialogHelper.getMessageDialog(getActivity(), getString(R.string.car_service_out)).create().show();


            } else {
                Intent intent = new Intent();

                intent.putExtra("systemNo", softwareDes.getSystemNo());
                getActivity().setResult(1000, intent);
                getActivity().onBackPressed();
            }



        }

    }





    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == 1000){

            if(data != null ){



                Intent intent = new Intent();

                intent.putExtra("systemNo", data.getStringExtra("systemNo"));
                getActivity().setResult(1000, intent);
                getActivity().onBackPressed();
            }
        }
    }



    public void showSearchFragment(){
        Intent intent = new Intent();
        intent.setClass(getActivity(), SearchActivity.class);
        getActivity().startActivityForResult(intent, 1000);
    }
}