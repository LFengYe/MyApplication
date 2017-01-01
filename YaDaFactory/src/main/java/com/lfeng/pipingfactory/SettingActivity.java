package com.lfeng.pipingfactory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.lfeng.pipingfactory.util.HttpUtil;
import com.lfeng.pipingfactory.util.PreferenceUtil;

/**
 * Created by LFeng on 16/10/13.
 */
public class SettingActivity extends AppCompatActivity {

    private EditText et_server;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_setting: {
                    if (saveSetting()) finish();
                    break;
                }
                case R.id.bt_cancel: {
                    finish();
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        init();
    }

    private void init() {
        et_server = (EditText) findViewById(R.id.et_server);
        String curHost = PreferenceUtil.getString("userHost", SettingActivity.this);
        if (curHost == null)
            et_server.setText(HttpUtil.defaultHost);
        else et_server.setText(curHost);

        findViewById(R.id.bt_setting).setOnClickListener(onClickListener);
        findViewById(R.id.bt_cancel).setOnClickListener(onClickListener);
    }

    private boolean saveSetting() {
        if (TextUtils.isEmpty(et_server.getText())) {
            Toast.makeText(SettingActivity.this, getString(R.string.setting_server_error), Toast.LENGTH_LONG).show();
            return false;
        }

        String userHost = et_server.getText().toString();
        HttpUtil.defaultHost = userHost;
        PreferenceUtil.putString("userHost", userHost, SettingActivity.this);

        return true;
    }
}
