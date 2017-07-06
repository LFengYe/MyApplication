package com.cxgps.vehicle.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;

import com.cxgps.vehicle.AppConfig;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.base.BaseActivity;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.fragment.ShowDetailsFragment;


/**
 * Created by taosong on 16/6/2.
 */
public class DetailActivity extends BaseActivity {


    public static final String BUNDLE_KEY_DISPLAY_TYPE = "BUNDLE_KEY_DISPLAY_TYPE";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected boolean hasBackButton() {
        return true;
    }

    @Override
    protected void init(Bundle savedInstanceState) {
        super.init(savedInstanceState);

        int displayType = getIntent().getIntExtra(BUNDLE_KEY_DISPLAY_TYPE, AppConfig.REPORT);

        BaseFragment fragment = null;
        int actionBarTitle = 0;
        switch (displayType) {
            case AppConfig.REPORT:

                actionBarTitle = R.string.main_slide_report;
                fragment = new ShowDetailsFragment();
                break;
            case AppConfig.ABOUT:
                actionBarTitle = R.string.main_slide_about;
                fragment = new ShowDetailsFragment();
                break;
            case AppConfig.GUIDE:
                actionBarTitle = R.string.main_slide_ubook;
                fragment = new ShowDetailsFragment();
                break;
            case AppConfig.WEIZHANG:
                actionBarTitle = R.string.main_slide_wzcq;
                fragment = new ShowDetailsFragment();
                break;

            default:
                break;
        }
        setActionBarTitle(actionBarTitle);
        FragmentTransaction trans = getSupportFragmentManager()
                .beginTransaction();
        trans.replace(R.id.container, fragment);
        trans.commitAllowingStateLoss();

    }


    @Override
    public void initView() {


    }

    @Override
    public void initData() {
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

        }
        return super.onKeyDown(keyCode, event);
    }








}
