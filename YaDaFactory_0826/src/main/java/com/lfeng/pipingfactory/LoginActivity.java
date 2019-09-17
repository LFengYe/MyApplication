package com.lfeng.pipingfactory;

import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lfeng.pipingfactory.bean.LoginBean;
import com.lfeng.pipingfactory.util.HttpUtil;
import com.lfeng.pipingfactory.util.NetUtil;
import com.lfeng.pipingfactory.util.PreferenceUtil;
import com.lfeng.pipingfactory.view.MyAutoCompleteTextView;
import com.lfeng.pipingfactory.view.MyDialog;

import java.util.LinkedList;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private Button bt_login;
    private TextView tv_setting;
    private MyAutoCompleteTextView et_account;
    private EditText et_password;
    private MyDialog dialog;
    private String account;
    private  LinkedList<String> accounts;
    private  ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initData();
        initView();
    }

    private void initData() {
        accounts = getShareData("account");
        adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.auto_item, accounts);

        String userHost = PreferenceUtil.getString("userHost", LoginActivity.this);
        if (null != userHost) {
            HttpUtil.defaultHost = userHost;
        }
    }

    private void initView() {
        bt_login = (Button) findViewById(R.id.bt_login);
        tv_setting = (TextView) findViewById(R.id.setting);
        et_account = (MyAutoCompleteTextView) findViewById(R.id.et_account);
        et_password = (EditText) findViewById(R.id.et_password);
        if (!accounts.isEmpty()){
           // et_account.setSelection(accounts.get(0).length());
            et_account.setText(accounts.get(0));
        }
        et_account.setAdapter(adapter);

        bt_login.setOnClickListener(this);
        tv_setting.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login: {
                account = et_account.getText().toString();
                String password = et_password.getText().toString().trim();
                if (account.isEmpty()||password.isEmpty()){
                    Toast.makeText(LoginActivity.this, R.string.login_null, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!NetUtil.checkNetworkConnection(LoginActivity.this)){
                    Toast.makeText(LoginActivity.this, R.string.Net_error, Toast.LENGTH_SHORT).show();
                    return;
                }
                JSONObject obj = new JSONObject();
                obj.put("username",account);
                obj.put("password",password);

                LoginAsyncTask loginAsyncTask = new LoginAsyncTask();
                loginAsyncTask.execute(obj);
                break;
            }
            case R.id.setting: {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, SettingActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    class LoginAsyncTask extends AsyncTask<JSONObject, String, String> {


        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject json_data = params[0];
            return HttpUtil.httpPost(HttpUtil.defaultHost + HttpUtil.loginURL,json_data);
        }

        @Override
        protected void onPreExecute() {
            Point size = new Point();
            LoginActivity.this.getWindowManager().getDefaultDisplay().getSize(size);
            int width = (int) (size.x*0.25);
            int height = (int) (size.y*0.35);
            String text = "正在登录中...";
            String type = "Refresh";
            dialog = new MyDialog(LoginActivity.this,width,height,text,type);
            dialog.show();
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            super.onPostExecute(s);

            if (s.isEmpty()){
                Toast.makeText(LoginActivity.this, R.string.servlet_error, Toast.LENGTH_SHORT).show();
                return;
            }

            LoginBean loginbean = JSON.parseObject(s,LoginBean.class);
            if (loginbean.getStatus()==-1){
                Toast.makeText(LoginActivity.this, loginbean.getMessage(), Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(LoginActivity.this, R.string.login_success, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                  String data = loginbean.getData();
                  int status = loginbean.getStatus();
                  intent.putExtra("data",data);
                  intent.putExtra("account",account);
                  intent.putExtra("status",status);
                  startActivity(intent);

                  if (!accounts.contains(account)) {
                      accounts.addFirst(account);
                  }
                  LinkedList<String> auto_accounts = cutData(accounts);
                  String save_data = JSON.toJSONString(auto_accounts, true);
                  PreferenceUtil.putString("account", save_data, LoginActivity.this);
                  finish();
            }
        }


    }


    private LinkedList<String> getShareData(String filename) {
        String msg = PreferenceUtil.getString(filename, LoginActivity.this);
        JSONArray jsonArr = JSON.parseArray(msg);

        LinkedList<String> list_data = new LinkedList<>();
        if (jsonArr != null) {
            for (int i = 0; i < jsonArr.size(); i++) {
                list_data.add(jsonArr.get(i).toString());
            }
        }
        return list_data;

    }


    private LinkedList<String> cutData(LinkedList<String> auto_account) {
        if (auto_account.size() > 5) {
            auto_account.removeLast();
        }
        return auto_account;
    }
}
