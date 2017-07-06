package com.cxgps.vehicle.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.AppManager;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.adapter.SimpleDataAdapter;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.SelectItemBean;
import com.cxgps.vehicle.utils.DialogHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by taosong on 16/12/27.
 */

public class MapAndLanFragment  extends BaseFragment {


    @Bind(R.id.listView)
    ListView   mListView;

    int  mode = -1;

    private SimpleDataAdapter  simpleDataAdapter;





    private List<SelectItemBean>  mapList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        try {
            mode =  bundle.getInt("mode",-1);

        }catch (Exception e){
            e.printStackTrace();

        }

        SelectItemBean map01 = null;

        SelectItemBean map02 = null;


        if (mode == 0){

            map01  = new SelectItemBean("zh",getString(R.string.lan_chinese));

            map02  = new SelectItemBean("en",getString(R.string.lan_english));


        }else if(mode ==1){


            map01  = new SelectItemBean("1",getString(R.string.map_baidu));

            map02  = new SelectItemBean("2",getString(R.string.map_google));

        }



        mapList.add(map01);
        mapList.add(map02);

    }



    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maplan_list, container,
                false);
        ButterKnife.bind(this, view);


        initView(view);
        initData();

        return view;
    }

    @Override
    public void initView(View view) {
        super.initView(view);

    }

    @Override
    public void initData() {

        simpleDataAdapter  = new SimpleDataAdapter(getActivity(),mapList,mode);
        mListView.setAdapter(simpleDataAdapter);
        mListView.setOnItemClickListener(onItemClickListener);

    }

    private AdapterView.OnItemClickListener  onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            SelectItemBean  selectItemBean = (SelectItemBean) view.getTag(R.id.tv_title);


            if (mode == -1){

                return;
            }else if (mode ==0){ // 语言

                String key = AppContext.getLanguageWithKey();


                if (key.equals(selectItemBean.getItemKey())){

                    return;
                }else {
                    AppContext.setLanguageWithData(selectItemBean.getItemKey());
                    AppContext.getInstance().switchLanguage(selectItemBean.getItemKey());

                    rebootApp();


                }

                simpleDataAdapter.notifyDataSetChanged(mapList);




            }else if(mode ==1){


                String key = AppContext.getMapWithKey();


                if (key.equals(selectItemBean.getItemKey())){

                    return;
                }else {
                    AppContext.setMaptypeWithData(selectItemBean.getItemKey());
                    // getActivity().onBackPressed();

                    rebootApp();
                }
                simpleDataAdapter.notifyDataSetChanged(mapList);


            }



        }
    };


    private  void rebootApp(){

        DialogHelper.getConfirmDialog(getActivity(), getString(R.string.tips_reboot_app), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                dialogInterface.dismiss();
                AppManager.getAppManager().AppExit(getActivity());

            }
        }).create().show();



    }
}
