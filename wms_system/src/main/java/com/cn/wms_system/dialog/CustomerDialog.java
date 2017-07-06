package com.cn.wms_system.dialog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.cn.wms_system.activity.ListActivity;
import com.cn.wms_system.bean.SResponse;
import com.cn.wms_system.service.MyHandler;
import com.cn.wms_system.service.MyThread;
import com.cn.wms_system_new.R;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by LFeng on 2017/7/3.
 */

public class CustomerDialog extends Activity {
    private JSONObject title;
    private JSONObject data;
    private String module;
    private int selected_fun_index;

    private MyHandler operateHandler = new MyHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SResponse response = (SResponse) msg.obj;
            switch (msg.what) {
                case MyThread.RESPONSE_SUCCESS:
                    Toast.makeText(CustomerDialog.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                    break;
                case MyThread.RESPONSE_UNFINISH:
                    Toast.makeText(CustomerDialog.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_info);

        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        module = intent.getStringExtra("selected_fun_index_name");
        selected_fun_index = intent.getIntExtra("selected_fun_index", 1);
        title = JSONObject.parseObject(intent.getStringExtra("title"), Feature.OrderedField);
        data = JSONObject.parseObject(intent.getStringExtra("data"), Feature.OrderedField);
    }

    private void initView() {
        final LinearLayout dialogLayout = (LinearLayout) findViewById(R.id.dialog_layout);
        Iterator titleIterator = title.entrySet().iterator();
        int operate = -1;
        //System.out.println("jhStatus:" + data.getIntValue("jhStatus"));

        while (titleIterator.hasNext()) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            Map.Entry<String, String> entry = (Map.Entry<String, String>) titleIterator.next();
            if (entry.getKey().compareTo("jhStatus") == 0) {
                //TODO 需确保jhStatus字段在最后一个
                operate = data.getIntValue("jhStatus");
                dialogLayout.addView(linearLayout);
                continue;
            }
            LinearLayout linearLayout1 = new LinearLayout(this);
            linearLayout1.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1
            );
            linearLayout1.setLayoutParams(layoutParams);
            linearLayout1.addView(getTextView(entry.getValue().split(",")[0], 1));
            linearLayout1.addView(getEditText(entry.getKey(), data.getString(entry.getKey()), 2));
            linearLayout.addView(linearLayout1);

            if (titleIterator.hasNext()) {
                entry = (Map.Entry<String, String>) titleIterator.next();
                if (entry.getKey().compareTo("jhStatus") == 0) {
                    //TODO 需确保jhStatus字段在最后一个
                    operate = data.getIntValue("jhStatus");
                    dialogLayout.addView(linearLayout);
                    continue;
                }
                LinearLayout linearLayout2 = new LinearLayout(this);
                layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        1
                );
                linearLayout2.setLayoutParams(layoutParams);
                linearLayout2.addView(getTextView(entry.getValue().split(",")[0], 1));
                linearLayout2.addView(getEditText(entry.getKey(), data.getString(entry.getKey()), 2));
                linearLayout.addView(linearLayout2);
            }

            dialogLayout.addView(linearLayout);
        }

        Button button = (Button) findViewById(R.id.btn_confirm);
        if (operate == -1) {
            button.setText(R.string.btn_finish);
        }
        if (operate == 0 || operate == -2) {
            button.setText(R.string.btn_start);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = dialogLayout.getFocusedChild();
                if (view != null)
                    view.clearFocus();
                confirmItem(data);
            }
        });
    }

    public void confirmItem(JSONObject object) {
        SharedPreferences preferences = getSharedPreferences(
                "system_params", MODE_PRIVATE);
        object.put("module", module);
        object.put("operation", "confirm");
        object.put("username", preferences.getString("username", "").split(",")[0]);
        if (selected_fun_index <= 3) {
            new MyThread(operateHandler, object, "app.do").start();
        }
    }

    private EditText getTextView(String fieldValue, float width) {
        LinearLayout.LayoutParams LP_WW2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, width);
        EditText et_false = (EditText) View.inflate(CustomerDialog.this, R.layout.et_false, null);
        et_false.setText(fieldValue);
        et_false.setLayoutParams(LP_WW2);
        return et_false;
    }


    private EditText getEditText(final String fieldName, final String fieldValue, float width) {
        LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, width);
        EditText et_true = (EditText) View.inflate(CustomerDialog.this, R.layout.et_true, null);
        et_true.setTag(fieldName);
        if (!TextUtils.isEmpty(fieldValue)) {
            et_true.setText(fieldValue);
            et_true.setFocusable(false);
        }
        et_true.setLayoutParams(LP_WW);
        et_true.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText view = (EditText) v;
                String fileValue = view.getText().toString();
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(fileValue)) {
                        data.put(fieldName, fileValue);
                    }
                }
            }
        });
        return et_true;
    }
}
