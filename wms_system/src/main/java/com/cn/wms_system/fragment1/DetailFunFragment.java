package com.cn.wms_system.fragment1;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.cn.wms_system_new.R;

/**
 * Created by LFeng on 2017/7/6.
 */

public class DetailFunFragment extends Fragment {

    private GridView gridView;

    //返回主界面
    private View.OnClickListener onClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            if (getActivity().findViewById(R.id.detail_fun) != null) {
                MainInterface mainInterface = new MainInterface();
                getFragmentManager().beginTransaction().replace(R.id.detail_fun, mainInterface).commit();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detail_fun, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        TextView textView = (TextView) getActivity().findViewById(R.id.back_to_main);
        textView.setOnClickListener(onClickListener);

        gridView = (GridView) getActivity().findViewById(R.id.gridview);
        gridView.setNumColumns(3);
    }
}
