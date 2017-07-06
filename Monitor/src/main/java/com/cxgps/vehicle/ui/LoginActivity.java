package com.cxgps.vehicle.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cxgps.vehicle.AppConfig;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.api.remote.VehicleApi;
import com.cxgps.vehicle.base.BaseActivity;
import com.cxgps.vehicle.bean.ResponseBean;
import com.cxgps.vehicle.bean.ResultBean;
import com.cxgps.vehicle.bean.SimpleBackPage;
import com.cxgps.vehicle.bean.UserInfo;
import com.cxgps.vehicle.interf.DialogControl;
import com.cxgps.vehicle.utils.DateUtils;
import com.cxgps.vehicle.utils.DialogHelper;
import com.cxgps.vehicle.utils.SimpleTextWatcher;
import com.cxgps.vehicle.utils.StringUtils;
import com.cxgps.vehicle.utils.TDevice;
import com.cxgps.vehicle.utils.UIHelper;
import com.cxgps.vehicle.widget.ChangeServerDialog;
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.Map;

/**
 * Created by taosong on 16/7/21.
 */
public class LoginActivity extends BaseActivity implements
        RadioGroup.OnCheckedChangeListener, View.OnClickListener,
        DialogControl, TextView.OnEditorActionListener,
        CompoundButton.OnCheckedChangeListener {


    EditText mEtUserName;

    EditText mEtPassword;

    View mIvClearUserName;

    View mIvClearPassword;

    Button mBtnLogin;

    TextView mBtnLanguage;

    TextView mBtntryuse;

    TextView mBtnChange;

    TextView mTextAppversion;

    RadioGroup mRGLoginways;

    CheckBox mIsAutoLogin;

    CheckBox mIsRember;

    private String mUserName;
    private String mPassword;
    private int loginType = AppConfig.USER_LOGIN_TAG;
    private boolean isAuto = false;
    private boolean isRember = false;

    private InputMethodManager im;

    private final TextWatcher mUserNameWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            mIvClearUserName
                    .setVisibility(TextUtils.isEmpty(s) ? View.INVISIBLE
                            : View.VISIBLE);
        }
    };
    private final TextWatcher mPassswordWatcher = new SimpleTextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            mIvClearPassword
                    .setVisibility(TextUtils.isEmpty(s) ? View.INVISIBLE
                            : View.VISIBLE);
        }
    };

    @Override
    protected boolean hasActionBar() {
        return false;
    }


    @Override
    protected int getLayoutId() {
        return R.layout.app_login;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TDevice.hideSoftKeyboard(getCurrentFocus());
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        switch (id) {
            case R.id.iv_clear_username:
                mEtUserName.getText().clear();
                mEtUserName.requestFocus();
                break;
            case R.id.iv_clear_password:
                mEtPassword.getText().clear();
                mEtPassword.requestFocus();
                break;
            case R.id.user_btn_login:
                handleLogin();
                break;
            case R.id.change:
                showChangeServerDialog();
                break;
            case R.id.select_language:

                showLanguageDialog(0);

                break;
            case R.id.try_to_use:

                // 先在这里注释掉.等接口通了再解开
                DialogHelper
                        .getConfirmDialog(LoginActivity.this,
                                getString(R.string.tips_try_use_message),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        tryToUse();
                                    }
                                }, new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.dismiss();
                                    }
                                }).create().show();

                break;
            default:
                break;
        }
    }

    // 设置服务器地址
    private void showChangeServerDialog() {

        final ChangeServerDialog changeServerDialog = new ChangeServerDialog(
                LoginActivity.this);

        changeServerDialog.setCancelable(true);
        changeServerDialog.setCanceledOnTouchOutside(true);
        changeServerDialog.show();
    }

    // 体验版
    private void tryToUse() {

        showWaitDialog(R.string.data_loading);
        loginType = AppConfig.USER_LOGIN_TAG;
        mUserName = AppConfig.CONSTANTS_DEFAULT_ACCOUNT;
        mPassword = AppConfig.CONSTANTS_DEFAULT_PASSWD;

        AppContext.getInstance().setAddress(AppConfig.CONSTANTS_DEFAULT_HOST,AppConfig.CONSTANTS_DEFAULT_PORT);
        VehicleApi.login(loginType, mUserName, mPassword, mHandler);

    }


    private void handleLogin() {

        if (!prepareForLogin()) {
            return;
        }

        // if the data has ready
        mUserName = mEtUserName.getText().toString();
        mPassword = mEtPassword.getText().toString();

        showWaitDialog(R.string.data_loading);
        VehicleApi.login(loginType, mUserName, mPassword, mHandler);
    }

    private boolean prepareForLogin() {
        if (!TDevice.hasInternet()) {
            AppContext.showToastShort(R.string.tips_have_no_intent);
            return false;
        }

        String uName = mEtUserName.getText().toString();
        if (StringUtils.isEmpty(uName)) {
            AppContext.showToastShort(R.string.user_edit_name_hit);
            mEtUserName.requestFocus();
            return false;
        }

        String pwd = mEtPassword.getText().toString();

        if (loginType == AppConfig.USER_LOGIN_TAG) {
            if (StringUtils.isEmpty(pwd)) {
                AppContext.showToastShort(R.string.user_edit_passwd_hit);
                mEtPassword.requestFocus();
                return false;
            }
        }
        return true;
    }


    private final HttpCallback mHandler = new HttpCallback() {


        @Override
        public void onFailure(int errorNo, String strMsg) {
            super.onFailure(errorNo, strMsg);

            System.out.println("failure msg:" + strMsg);
            hideWaitDialog();
            if (errorNo == 404) {
                AppContext.showToast(R.string.tips_server_error);
            }
            if (isRember) {

                UserInfo user = new UserInfo();

                user.setuAccount(mUserName);

                user.setuPassword(mPassword);
                user.setuLoginFlag("false");
                user.setuIsAuto(String.valueOf(isAuto));
                user.setuIsRember(String.valueOf(isRember));

                user.setuLoginType(loginType);
                user.setUserName(mUserName);
                user.setuLogintime(DateUtils.getFormateDate());
                // 保存数据,并且进行跳转
                AppContext.getInstance().saveUserInfo(loginType, user);
            }

        }

        @Override
        public void onSuccess(Map<String, String> headers, byte[] t) {
            super.onSuccess(headers, t);


            try {

                hideWaitDialog();
                System.out.println("login:" + new String(t));
                ResponseBean  responseBean = JSON.parseObject(new String(t),ResponseBean.class);

                if (responseBean.isRequestFlag()){

                    ResultBean  resultBean = JSON.parseObject(responseBean.getData(),ResultBean.class);

                    if (resultBean.isuLoginFlag()){

                        UserInfo  user = new UserInfo();


                        loginType = resultBean.getuLoginType();
                        user.setuAccount(mUserName);

                        user.setuPassword(mPassword);
                        user.setuLoginFlag(String.valueOf(resultBean.isuLoginFlag()));
                        user.setuIsAuto(String.valueOf(isAuto));
                        user.setuIsRember(String.valueOf(isRember));
                        user.setUserName(mUserName);
                        user.setuLogintime(DateUtils.getFormateDate());
                        user.setuLoginType(loginType);

                        // 保存数据,并且进行跳转

                        // 保存数据,并且进行跳转
                        AppContext.getInstance().saveUserInfo(loginType,
                                user);

                        hideWaitDialog();
                        handleLoginSuccess(resultBean.getResultState());
                    }else {
                        if (isRember) {
                            UserInfo users = new UserInfo();

                            users.setuAccount(mUserName);

                            users.setuPassword(mPassword);
                            users.setuLoginFlag(String.valueOf(resultBean.isuLoginFlag()));
                            users.setuIsAuto(String.valueOf(isAuto));
                            users.setuIsRember(String.valueOf(isRember));
                            users.setUserName(mUserName);
                            users.setuLogintime(DateUtils.getFormateDate());
                            users.setuLoginType(loginType);
                            // 保存数据,并且进行跳转
                            AppContext.getInstance().saveUserInfo(loginType,
                                    users);

                        }

                        AppContext.showToast(resultBean.getResultMsg());

                    }

                }else {
                    AppContext.showToast(R.string.tips_login_faile);

                }
            }catch (Exception e){
                e.printStackTrace();


            }
        }



    };

    private void handleLoginSuccess(int resultState) {

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("loginResultState", resultState);
        intent.putExtras(bundle);
        intent.setClass(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    private void initUserData() {

        UserInfo user = AppContext.getInstance().getLoginUser();

        if (user != null){


            if(Boolean.valueOf(user.getuIsRember())) {

                mEtUserName.setText(user.getuAccount());
                mEtPassword.setText(user.getuPassword());
                isAuto  = Boolean.valueOf(user.getuIsAuto());
                mIsAutoLogin.setChecked(isAuto);
                isRember = Boolean.valueOf(user.getuIsRember());
                mIsRember.setChecked(isRember);
            }else {

                mEtUserName.setText(user.getuAccount());
                mEtPassword.setText("");
                isAuto  = Boolean.valueOf(user.getuIsAuto());
                mIsAutoLogin.setChecked(isAuto);
                isRember = Boolean.valueOf(user.getuIsRember());
                mIsRember.setChecked(isRember);
            }
        } else {
            mEtUserName.setText("");
            mEtPassword.setText("");
            mIsAutoLogin.setChecked(isAuto);
            mIsRember.setChecked(isRember);

        }

        loginType = AppContext.get(AppConfig.KEY_NUMBER_USER,
                AppConfig.USER_LOGIN_TAG);
        if (loginType == AppConfig.USER_LOGIN_TAG) {
            // 设置用户信息
            mRGLoginways.check(R.id.user_login_way);
        } else {
            // 设置用户信息
            mRGLoginways.check(R.id.car_login_way);
        }



        if(user!=null && Boolean.valueOf(user.getuIsAuto())){
            handleLogin();
        }


    }

    // 选择语言
    private void showLanguageDialog(int mode) {

        Bundle bundle = new Bundle();

        bundle.putInt("mode",mode);

        if (mode == 0){
            UIHelper.showSimpleBack(LoginActivity.this, SimpleBackPage.SWITCHLAN,bundle);


        }else {

            UIHelper.showSimpleBack(LoginActivity.this, SimpleBackPage.SWITCHMAP,bundle);

        }

    }




    @Override
    public void initView() {
        // 通过注解绑定控件
        im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        mIsRember = (CheckBox) findViewById(R.id.isRember);
        mIsAutoLogin = (CheckBox) findViewById(R.id.isAutoLogin);
        mRGLoginways = (RadioGroup) findViewById(R.id.loginWays);
        mTextAppversion = (TextView) findViewById(R.id.app_version);

        mEtUserName = (EditText) findViewById(R.id.user_edit_name);
        mEtPassword = (EditText) findViewById(R.id.user_edit_passwd);
        mIvClearPassword = findViewById(R.id.iv_clear_password);
        mIvClearUserName = findViewById(R.id.iv_clear_username);
        mBtnLogin = (Button) findViewById(R.id.user_btn_login);

        mBtnLanguage = (TextView) findViewById(R.id.select_language);
        mBtntryuse = (TextView) findViewById(R.id.try_to_use);
        mBtnChange = (TextView) findViewById(R.id.change);

        // 清除事件
        mIvClearUserName.setOnClickListener(this);
        mIvClearPassword.setOnClickListener(this);

        // 输入框事件
        mEtUserName.addTextChangedListener(mUserNameWatcher);
        mEtPassword.addTextChangedListener(mPassswordWatcher);
        mEtPassword.setOnEditorActionListener(this);


        // 按钮事件
        mBtnLogin.setOnClickListener(this);
        mBtnLanguage.setOnClickListener(this);
        mBtntryuse.setOnClickListener(this);
        mBtnChange.setOnClickListener(this);
        mRGLoginways.setOnCheckedChangeListener(this);
        mIsRember.setOnCheckedChangeListener(this);
        mIsAutoLogin.setOnCheckedChangeListener(this);


    }

    @Override
    public void initData() {

        String lanStr =  AppContext.getLanguageWithKey();

        String  lanText = getString(R.string.lan_chinese);

        if ("en".equals(lanStr)){

            lanText  = getString(R.string.lan_english);

        }

        mBtnLanguage.setText(lanText);

      //  mBtnLanguage.setVisibility(View.GONE);
        // 设置版本
        String versionName = TDevice.getVersionName();

        UserInfo user = AppContext.getInstance().getLoginUser();

        loginType = AppContext.get(AppConfig.KEY_NUMBER_USER,
                AppConfig.USER_LOGIN_TAG);

        if (null == user) {

            user = null;
        } else {

            loginType = user.getuLoginType();
        }

        mTextAppversion.setText(String.format(getString(R.string.app_version),
                versionName));
        initUserData();

    }








    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // 在输入法里点击了“完成”，则去登录
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            handleLogin();
            // 将输入法隐藏
            im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(mEtPassword.getWindowToken(), 0);
            return true;
        }
        return false;
    }



    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {

            case R.id.user_login_way:
                loginType = AppConfig.USER_LOGIN_TAG;

                break;
            case R.id.car_login_way:
                loginType = AppConfig.CARNUMBER_LOGIN_TAG;
                break;
        }

        AppContext.set(AppConfig.KEY_NUMBER_USER, loginType);
        initUserData();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        switch (buttonView.getId()) {

            case R.id.isAutoLogin:

                isAuto = isChecked;

                break;
            case R.id.isRember:

                isRember = isChecked;

                break;

        }

    }





}