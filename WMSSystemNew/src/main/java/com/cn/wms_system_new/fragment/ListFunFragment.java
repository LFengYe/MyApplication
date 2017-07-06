package com.cn.wms_system_new.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.cn.wms_system_new.R;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by LFeng on 2017/7/6.
 */

public class ListFunFragment extends Fragment {

    private JSONObject menuJson;
    private LinearLayout layout;
    private BtnClickCallback callback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_fun, container, false);
        layout = (LinearLayout) view.findViewById(R.id.list_fun_layout);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Iterator iterator = menuJson.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry<String, JSONObject> entry = (Map.Entry<String, JSONObject>) iterator.next();
            Button button = new Button(getActivity());
            button.setText(entry.getKey());
            button.setTextSize(18);
            button.setGravity(Gravity.CENTER);
            button.setBackgroundResource(R.drawable.custom_button);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callback.btnClick(entry.getValue());
                }
            });

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(30, 30, 30, 30);
            params.gravity = Gravity.CENTER;

            layout.addView(button, params);
        }
    }

    public void setCallback(BtnClickCallback callback) {
        this.callback = callback;
    }

    public void setMenuJson(JSONObject menuJson) {
        this.menuJson = menuJson;
    }

    public interface BtnClickCallback {
        void btnClick(JSONObject object);
    }
}
