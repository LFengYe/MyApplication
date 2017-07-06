package com.cn.wms_system_new.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.cn.wms_system_new.R;
import com.cn.wms_system_new.bean.SResponse;
import com.cn.wms_system_new.component.Constants;
import com.cn.wms_system_new.component.GetNowTime;
import com.cn.wms_system_new.service.BootBroadcastReceiver;
import com.cn.wms_system_new.service.HttpRequestClient;
import com.cn.wms_system_new.service.MyHandler;
import com.cn.wms_system_new.service.MyThread;

public class LandActivity extends Activity {

    private View view;
    private AutoCompleteTextView userCodeEdit;
    private Button userHistoryList;
    private EditText landPasswordEdit;
    // private EditText dataAccountEdit;
    private TextView dateText;

    private BootBroadcastReceiver receiver;

    //region 按钮点击事件
    private OnClickListener clickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.land_button) {
                String username = userCodeEdit.getText().toString();
                String password = landPasswordEdit.getText().toString();
                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                    JSONObject object = new JSONObject();
                    object.put("module", "userLogin");
                    object.put("operation", "employeeLogin");
                    object.put("type", "app");
                    object.put("username", username);
                    object.put("password", password);

                    new MyThread(myHandler, object, "action.do").start();
                } else {
                    Toast.makeText(LandActivity.this, R.string.username_password_empty, Toast.LENGTH_LONG).show();
                }
            }

            if (v.getId() == R.id.server_setting) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putString("title", getResources().getString(R.string.server_setting));
                intent.putExtras(bundle);
                intent.setClass(getApplicationContext(),
                        ParamsSettingActivity.class);
                startActivity(intent);
            }

            if (v.getId() == R.id.user_history_list) {
                userCodeEdit.showDropDown();
            }

            if (v.getId() == R.id.exit_button) {
                finish();
            }
        }
    };
    //endregion

    //region 数据处理
    private MyHandler myHandler = new MyHandler(LandActivity.this) {
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyThread.RESPONSE_SUCCESS: {
                    SResponse response = (SResponse) msg.obj;
                    if (response.getStatus() == 0) {
                        JSONObject object = JSONObject.parseObject(response.getData());
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("authority_id_list", object.getString("roleRightList"));
                        bundle.putString("menuJson", object.getString("menuJson"));
                        bundle.putString("employee", object.getString("employee"));
                        intent.putExtras(bundle);
                        //intent.setClass(getApplicationContext(), MainFragment.class);
                        startActivity(intent);
                        // 保存用户名
                        saveHistory("username", userCodeEdit);
                    } else {
                        Toast.makeText(LandActivity.this, response.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(Constants.FLAG_HOMEKEY_DISPATCHED,
                Constants.FLAG_HOMEKEY_DISPATCHED);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getLayoutInflater();
        view = LayoutInflater.from(this).inflate(R.layout.activity_land, null);
        setContentView(view);

        initComponents();
    }

    private void initComponents() {
        /*
        TextView tempDisplay = (TextView) findViewById(R.id.temp_display);
        tempDisplay.setText("Mac Address:" + Constants.getMacAddress(this));
        */

        dateText = (TextView) findViewById(R.id.date_reminder);
        dateText.setText(GetNowTime.getHanYuDate());

        Button landButton = (Button) findViewById(R.id.land_button);
        landButton.setOnClickListener(clickListener);

        Button exitButton = (Button) findViewById(R.id.exit_button);
        exitButton.setOnClickListener(clickListener);

        Button serverSetButton = (Button) findViewById(R.id.server_setting);
        serverSetButton.setOnClickListener(clickListener);

        userCodeEdit = (AutoCompleteTextView) findViewById(R.id.user_code);
        userHistoryList = (Button) findViewById(R.id.user_history_list);
        landPasswordEdit = (EditText) findViewById(R.id.land_password);
        userCodeEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                landPasswordEdit.setText("");
            }
        });

        userHistoryList.setOnClickListener(clickListener);

        SharedPreferences sp = getSharedPreferences("system_params", MODE_PRIVATE);
        HttpRequestClient.ipAddress = sp.getString("ipAddress", HttpRequestClient.ipAddress);
        HttpRequestClient.port = sp.getString("port", HttpRequestClient.port);
        HttpRequestClient.refreshHost();
    }

    private void initAutoComplete(String field, AutoCompleteTextView auto) {

        SharedPreferences sp = getSharedPreferences("system_params",
                MODE_PRIVATE);
        String longhistory = sp.getString(field, "");
        String[] hisArrays = longhistory.split(",");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, hisArrays);

        if (hisArrays.length > 5) {
            String[] newArrays = new String[5];
            System.arraycopy(hisArrays, 0, newArrays, 0, 5);
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_dropdown_item_1line, newArrays);
        }

        userCodeEdit.setAdapter(adapter);
        userCodeEdit.setThreshold(1);
        userCodeEdit.setCompletionHint("最多显示5个记录");
        /*
         * userCodeEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
		 * 
		 * @Override public void onFocusChange(View v, boolean hasFocus) {
		 * AutoCompleteTextView view = (AutoCompleteTextView) v; if (hasFocus) {
		 * view.showDropDown(); } } });
		 */
    }

    private void saveHistory(String field, AutoCompleteTextView auto) {
        String text = auto.getText().toString();
        SharedPreferences sp = getSharedPreferences("system_params",
                MODE_PRIVATE);
        String history = sp.getString(field, "");

        //
        if (!history.contains(text + ",")) {
            StringBuilder sb = new StringBuilder(history);
            sb.insert(0, text + ",");
            sp.edit().putString(field, sb.toString()).commit();
        } else {
            StringBuilder sb = new StringBuilder(history);
            sb.delete(sb.indexOf(text + ","), sb.indexOf(text + ",")
                    + (text + ",").length());
            sb.insert(0, text + ",");
            sp.edit().putString(field, sb.toString()).commit();
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        dateText.setText(GetNowTime.getHanYuDate());
        initAutoComplete("username", userCodeEdit);
        // 注册时间更新广播接收者更新标题栏中的时间
        receiver = new BootBroadcastReceiver() {

            @Override
            public void updateTime() {
                dateText.setText(GetNowTime.getHanYuDate());
            }

            @Override
            public void updateData() {
            }
        };
        IntentFilter filter = new IntentFilter(
                "android.intent.action.TIME_TICK");
        registerReceiver(receiver, filter);
        super.onResume();
    }
}
