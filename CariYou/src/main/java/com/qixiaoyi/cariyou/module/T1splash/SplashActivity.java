package com.qixiaoyi.cariyou.module.T1splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.qixiaoyi.cariyou.R;
import com.qixiaoyi.cariyou.module.T11.EditbusinesshoursActivity;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        startActivity(new Intent(SplashActivity.this,EditbusinesshoursActivity.class));
    }

}