package com.cxgps.vehicle.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.adapter.SearchAdapter;
import com.cxgps.vehicle.api.remote.VehicleApi;
import com.cxgps.vehicle.base.BaseActivity;
import com.cxgps.vehicle.bean.CarsInfo;
import com.cxgps.vehicle.bean.ResponseBean;
import com.cxgps.vehicle.bean.UserInfo;
import com.cxgps.vehicle.utils.DialogHelper;
import com.cxgps.vehicle.utils.StringUtils;
import com.cxgps.vehicle.utils.TDevice;
import com.cxgps.vehicle.widget.EmptyLayout;
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by 火蚁 on 15/5/27.
 */
public class SearchActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private SearchView mSearchView;

    @Bind(R.id.listview)
    ListView mListView;

    @Bind(R.id.error_layout)
    EmptyLayout mErrorLayout;

    private SearchAdapter mAdapter;

    private ArrayList<CarsInfo> searchCarList = new ArrayList<CarsInfo>();

    private HttpCallback mHandle = new HttpCallback() {


        @Override
        public void onFailure(int errorNo, String strMsg) {
            super.onFailure(errorNo, strMsg);
            executeOnLoadDataSuccess(null);
        }

        @Override
        public void onSuccess(Map<String, String> headers, byte[] t) {
            super.onSuccess(headers, t);

            mErrorLayout.setErrorType(EmptyLayout.HIDE_LAYOUT);

            ResponseBean responseBean = JSON.parseObject(new String(t), ResponseBean.class);

            if (responseBean != null && responseBean.isRequestFlag()){

                List<CarsInfo> list = JSON.parseArray(responseBean.getData(),CarsInfo.class);

                executeOnLoadDataSuccess(list);


            }else {
                executeOnLoadDataSuccess(null);
            }


        }

    };

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem search = menu.findItem(R.id.search_content);
        mSearchView = (SearchView) search.getActionView();
        mSearchView.setIconifiedByDefault(false);

        setSearch();
        return super.onCreateOptionsMenu(menu);
    }

    private void setSearch() {
        mSearchView.setQueryHint(getString(R.string.search_content_hit));
        TextView textView = (TextView) mSearchView
                .findViewById(R.id.search_src_text);
        textView.setTextColor(Color.WHITE);
        //   mSearchView.setSubmitButtonEnabled(true);
        mSearchView
                .setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String arg0) {

                        search(arg0);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String arg0) {

                        return false;
                    }
                });
    }

    private void search(String nickName) {
        if (nickName == null || StringUtils.isEmpty(nickName)) {
            return;
        }
        mErrorLayout.setErrorType(EmptyLayout.NETWORK_LOADING);
        mListView.setVisibility(View.GONE);

        UserInfo userInfo = AppContext.getInstance().getLoginUser();

        if (userInfo != null && Boolean.valueOf(userInfo.getuLoginFlag())) {
            VehicleApi.getCarSearch(userInfo.getUserName(),userInfo.getuLoginType(), nickName, 1, mHandle);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        ButterKnife.bind(this);
        initView();

    }


    protected int getLayoutId() {
        return R.layout.app_search;
    }

    protected boolean hasBackButton() {
        return true;
    }


    public void initView() {
        mAdapter = new SearchAdapter(SearchActivity.this,searchCarList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mErrorLayout.setOnLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                search(mSearchView.getQuery().toString());
            }
        });
    }


    public void initData() {

    }


    public void onClick(View view) {

    }


    private void executeOnLoadDataSuccess(List<CarsInfo> data) {

        if (data!=null) {

            if (data.size() > 0) {

                searchCarList.clear();
                searchCarList.addAll(data);
                mAdapter.notifyDataSetChanged();
                mListView.setVisibility(View.VISIBLE);
            } else {
                mErrorLayout.setErrorType(EmptyLayout.NODATA_ENABLE_CLICK);
                mListView.setVisibility(View.GONE);
            }

        }else {
            mErrorLayout.setErrorType(EmptyLayout.NODATA);
            mListView.setVisibility(View.GONE);
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
            if (result && Integer.parseInt(remarks) == 0) {

                DialogHelper.getMessageDialog(SearchActivity.this, getString(R.string.car_service_out)).create().show();


            } else {
                Intent intent = new Intent();

                intent.putExtra("systemNo", softwareDes.getSystemNo());
                setResult(1000, intent);
                TDevice.hideSoftKeyboard(mSearchView);
               finish();
            }



        }







    }


    @Override
    protected void onDestroy() {
        TDevice.hideSoftKeyboard(mSearchView);
        super.onDestroy();
    }


}