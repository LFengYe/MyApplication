package com.cxgps.vehicle;

import android.content.Intent;
import android.view.View;

import com.cxgps.vehicle.base.BaseActivity;
import com.cxgps.vehicle.ui.GuideActivity;
import com.cxgps.vehicle.ui.LoginActivity;

import butterknife.Bind;

/**
 * app的欢迎界面
 */
public class SplashActivity extends BaseActivity {




    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Bind(R.id.app_start_view)
    View mView;


    @Override
    protected int getLayoutId() {
        return R.layout.app_start;
    }


    @Override
    public void initView() {

        mView.postDelayed(new Runnable() {
            @Override
            public void run() {
                redirectTo();
            }
        }, 800);
    }

    @Override
    public void initData() {

    }



    private void redirectTo() {
        boolean isFistFlag =   AppContext.isFirstStart();
        Intent intent = new Intent();
        if (isFistFlag){

            intent.setClass(SplashActivity.this, GuideActivity.class);
            startActivity(intent);

            finish();
            return;
        }
        intent.setClass(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

}
