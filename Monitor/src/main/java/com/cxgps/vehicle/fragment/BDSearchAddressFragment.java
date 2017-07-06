package com.cxgps.vehicle.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.adapter.NavigationAdapter;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.NavigationBean;
import com.cxgps.vehicle.utils.TDevice;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by taosong on 16/7/27.
 */
public class BDSearchAddressFragment extends BaseFragment implements AdapterView.OnItemClickListener{


    private  int pointMode = 0;

    private SearchView mSearchView;

    @Bind(R.id.listView)
    ListView  mListView;

    private ArrayList<NavigationBean> navigationBeans = new ArrayList<>();

    private NavigationAdapter  navigationAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_address, container,
                false);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }


    @Override
    public void initData() {
        super.initData();
        navigationAdapter = new NavigationAdapter(getActivity(),navigationBeans);
        mListView.setAdapter(navigationAdapter);
        mListView.setOnItemClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        MenuItem search = menu.findItem(R.id.search_content);
        mSearchView = (SearchView) search.getActionView();
        mSearchView.setIconifiedByDefault(false);
        setSearch();
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void setSearch() {
        mSearchView.setQueryHint(getString(R.string.top_search_hit));
        TextView textView = (TextView) mSearchView.findViewById(R.id.search_src_text);
        textView.setTextColor(Color.WHITE);
        textView.setHintTextColor(0x90ffffff);

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String arg0) {
                mSearchView.clearFocus();
                TDevice.hideSoftKeyboard(mSearchView);
                search(arg0);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {

                mSearchView.clearFocus();
                TDevice.hideSoftKeyboard(mSearchView);
                search(arg0);


                return false;
            }
        });
        mSearchView.requestFocus();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle args = getArguments();
        pointMode = args.getInt("mode",0);


        int mode = WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN
                | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN;
        getActivity().getWindow().setSoftInputMode(mode);;

        initBaiduSearch();
    }


    private  void search(String content){


        Log.i("TAG", "=====content========" + content);

        if (content.length()>0) {

            /**
             * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
             */
            mSuggestionSearch
                    .requestSuggestion((new SuggestionSearchOption())
                            .keyword(content).city("北京"));

        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        NavigationBean  bean = (NavigationBean) view.getTag(R.id.item_title);

        if (bean!= null){


            Intent  intent = new Intent();
            intent.putExtra("mode",pointMode);
            intent.putExtra("navigation",bean);

            getActivity().setResult(1000, intent);
            getActivity().finish();

        }

    }

    /*********************地理位置搜索****************************/

    private SuggestionSearch mSuggestionSearch = null;


    private void  initBaiduSearch() {

        // 初始化建议搜索模块，注册建议搜索事件监听
        mSuggestionSearch = SuggestionSearch.newInstance();
        mSuggestionSearch.setOnGetSuggestionResultListener(suggestionResultListener);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }


    OnGetSuggestionResultListener  suggestionResultListener = new OnGetSuggestionResultListener() {
        @Override
        public void onGetSuggestionResult(SuggestionResult res) {
            if (res == null || res.getAllSuggestions() == null) {
                return;
            }

            ArrayList<NavigationBean>  suggestNaviga = new ArrayList<>();

            for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
                if (info.key != null) {
                    //     suggest.add(info.key);

                    NavigationBean bean = new NavigationBean();

                    bean.setTitleName(info.key);
                    bean.setTitleDesc(info.city + info.district + info.key);

                    if (info.pt != null) {
                        bean.setSulat(info.pt.latitude);
                        bean.setSulng(info.pt.longitude);
                    }


                    suggestNaviga.add(bean);
                }
            }

            if (suggestNaviga.size() > 0) {


                navigationBeans.addAll(suggestNaviga);

                navigationAdapter.notifyDataSetChanged();
            }
        }


    };



    @Override
    public void onDestroy() {

        mSuggestionSearch.destroy();
        super.onDestroy();
    }
}
