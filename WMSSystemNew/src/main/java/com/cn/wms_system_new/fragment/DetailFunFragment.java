package com.cn.wms_system_new.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.cn.wms_system_new.R;
import com.cn.wms_system_new.activity.ListDataActivity;
import com.cn.wms_system_new.activity.ParamsSettingActivity;
import com.cn.wms_system_new.activity.ReportDataActivity;
import com.cn.wms_system_new.bean.FieldDescription;
import com.cn.wms_system_new.bean.FunctionItem;
import com.cn.wms_system_new.bean.SResponse;
import com.cn.wms_system_new.bean.UnFinishAmount;
import com.cn.wms_system_new.dialog.ChangePassword;
import com.cn.wms_system_new.service.MyHandler;
import com.cn.wms_system_new.service.MyThread;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import static com.cn.wms_system_new.service.MyThread.RESPONSE_SUCCESS;

/**
 * Created by LFeng on 2017/7/6.
 */

public class DetailFunFragment extends Fragment {

    private static DetailFunFragment detailFunFragment;

    private GridView gridView;
    private FunPicAdapter adapter;
    private ArrayList<FunctionItem> itemList;
    private AdapterView.OnItemClickListener onItemClickListener;

    private String funName;
    private MyHandler myHandler = new MyHandler(getActivity()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case RESPONSE_SUCCESS:
                    try {
                        SResponse response = (SResponse) msg.obj;
                        UnFinishAmount unFinishAmount = JSONObject.parseObject(response.getData(), UnFinishAmount.class);
                        Class objClass = Class.forName("com.cn.wms_system_new.bean.UnFinishAmount");
                        Field[] fields = objClass.getDeclaredFields();
                        for (FunctionItem item : itemList) {
                            for (Field field : fields) {
                                if (field.isAnnotationPresent(FieldDescription.class)) {
                                    FieldDescription description = field.getAnnotation(FieldDescription.class);
                                    if (description.description().compareTo(item.getTitle()) == 0) {
                                        field.setAccessible(true);
                                        item.setUnFinishedNum(field.getInt(unFinishAmount));
                                        break;
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

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

    public static DetailFunFragment newInstance(String funName, JSONObject menuJson) {
        detailFunFragment = new DetailFunFragment();
        Bundle args = new Bundle();
        args.putString("funName", funName);
        args.putString("menuJson", menuJson.toJSONString());
        detailFunFragment.setArguments(args);

        return detailFunFragment;
    }

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
        Bundle args = getArguments();
        funName = args.getString("funName");
        detailFunFragment.setOnItemClickListener(onItemClickListener);

        TextView textView = (TextView) getActivity().findViewById(R.id.back_to_main);
        textView.setOnClickListener(onClickListener);

        gridView = (GridView) getActivity().findViewById(R.id.gridview);
        gridView.setNumColumns(3);

        itemList = new ArrayList<>();
        JSONObject menuJson = JSONObject.parseObject(args.getString("menuJson"), Feature.OrderedField);
        Iterator iterator = menuJson.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
            String[] values = entry.getValue().split(",");

            FunctionItem item = new FunctionItem();
            item.setTitle(entry.getKey());
            item.setViewType(values[0]);
            item.setOperateName(values[1]);
            item.setImageName(values[3]);
            itemList.add(item);
        }
        adapter = new FunPicAdapter(getActivity(), itemList);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                FunctionItem item = itemList.get(i);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("funName", funName);
                bundle.putString("module", item.getTitle());
                bundle.putString("title", item.getTitle());
                bundle.putString("action", item.getOperateName());
                intent.putExtras(bundle);
                if (funName.compareTo("系统设置") == 0) {
                    if (item.getTitle().compareTo("参数设置") == 0)
                        intent.setClass(getActivity(), ParamsSettingActivity.class);
                    if (item.getTitle().compareTo("修改密码") == 0) {
                        ChangePassword changePassword = new ChangePassword(getActivity());
                        changePassword.setTitle(item.getTitle());
                        changePassword.show();
                        return;
                    }
                } else if (funName.compareTo("报表查询") == 0) {
                    intent.setClass(getActivity(), ReportDataActivity.class);
                } else {
                    intent.setClass(getActivity(), ListDataActivity.class);
                }
                startActivity(intent);
            }
        });
        if (funName.compareTo("系统设置") != 0 && funName.compareTo("报表查询") != 0)
            getUnFinishAmount();
    }


    public void getUnFinishAmount() {
        JSONObject object = new JSONObject();
        object.put("module", "unFinishAmount");
        new MyThread(myHandler, object, "app.do").start();
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
