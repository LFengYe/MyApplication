package com.cn.wms_system_new.dialog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.cn.wms_system_new.R;
import com.cn.wms_system_new.bean.SResponse;
import com.cn.wms_system_new.service.MyHandler;
import com.cn.wms_system_new.service.MyThread;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by LFeng on 2017/7/3.
 */

public class CustomerDialog extends Activity {
    private JSONObject title;
    private JSONObject data;
    private JSONObject upload;
    private String module;
    private String funName;
    private String action;

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
        setContentView(R.layout.layout_customer_dialog);

        Window window = getWindow();
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        WindowManager.LayoutParams params = window.getAttributes();
        params.width = (int)(metrics.widthPixels * 0.8);
        params.height = metrics.heightPixels;
        window.setAttributes(params);

        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        module = intent.getStringExtra("module");
        funName = intent.getStringExtra("funName");
        action = intent.getStringExtra("action");
        upload = JSONObject.parseObject(intent.getStringExtra("upload"));
        title = JSONObject.parseObject(intent.getStringExtra("title"), Feature.OrderedField);
        data = JSONObject.parseObject(intent.getStringExtra("data"), Feature.OrderedField);
    }

    private void initView() {
        final LinearLayout dialogLayout = (LinearLayout) findViewById(R.id.dialog_layout);
        Iterator titleIterator = title.entrySet().iterator();
        int operate = data.getIntValue("jhStatus");

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        //Log.i("屏幕尺寸", "width:" + metrics.widthPixels + ",height:" + metrics.heightPixels + ",density:" + metrics.density);

        while (titleIterator.hasNext()) {
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            Map.Entry<String, String> entry = (Map.Entry<String, String>) titleIterator.next();
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

            if (titleIterator.hasNext() && (metrics.widthPixels / metrics.density) > 720) {
                entry = (Map.Entry<String, String>) titleIterator.next();
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
        Button otherBtn = (Button) findViewById(R.id.btn_finish);
        otherBtn.setVisibility(View.GONE);
        if (funName.compareTo("库房作业管理") == 0) {
            if (module.compareTo("报检信息") == 0) {
                otherBtn.setVisibility(View.VISIBLE);
                otherBtn.setText(R.string.reject);

                button.setText(R.string.accept);
            } else {
                button.setText(R.string.btn_audit);
            }
        } else {
            if (operate == -1) {
                button.setText(R.string.btn_finish);
            }
            if (operate == 0 || operate == -2) {
                button.setText(R.string.btn_start);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = dialogLayout.getFocusedChild();
                if (view != null)
                    view.clearFocus();
                if (funName.compareTo("库房作业管理") == 0) {
                    if (module.compareTo("报检信息") == 0) {
                        data.put("inspectionResult", "合格");
                        JSONObject object = new JSONObject();
                        object.put("item", data);
                        inspection(object);
                    } else {
                        auditItem(upload);
                    }
                } else {
                    confirmItem(data);
                }
            }
        });

        otherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = dialogLayout.getFocusedChild();
                if (view != null)
                    view.clearFocus();
                if (funName.compareTo("库房作业管理") == 0) {
                    if (module.compareTo("报检信息") == 0) {
                        data.put("inspectionResult", "不合格");
                        JSONObject object = new JSONObject();
                        object.put("item", data);
                        inspection(object);
                    }
                }
            }
        });
    }

    public void confirmItem(JSONObject object) {
        object.put("module", funName);
        object.put("operation", "confirm");
        object.put("type", "app");
        new MyThread(operateHandler, object, action).start();
    }

    public void auditItem(JSONObject object) {
        object.put("module", module);
        object.put("operation", "auditItem");
        object.put("type", "app");
        new MyThread(operateHandler, object, action).start();
    }

    public void inspection(JSONObject object) {
        object.put("module", module);
        object.put("operation", "inspection");
        object.put("type", "app");
        new MyThread(operateHandler, object, action).start();
    }

    private EditText getTextView(String fieldValue, float width) {
        LinearLayout.LayoutParams LP_WW2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, width);
        EditText et_false = (EditText) View.inflate(CustomerDialog.this, R.layout.view_text, null);
        et_false.setText(fieldValue);
        et_false.setLayoutParams(LP_WW2);
        return et_false;
    }

    private EditText getEditText(final String fieldName, final String fieldValue, float width) {
        LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, width);
        EditText et_true = (EditText) View.inflate(CustomerDialog.this, R.layout.view_edit, null);
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
