package com.cn.wms_system_new.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.StringCodec;
import com.cn.wms_system_new.R;
import com.cn.wms_system_new.component.Constants;
import com.cn.wms_system_new.component.GetNowTime;
import com.cn.wms_system_new.component.TitleViewHolder;
import com.cn.wms_system_new.fragment.DetailFunFragment;
import com.cn.wms_system_new.fragment.ListFunFragment;
import com.cn.wms_system_new.fragment.MainInterface;
import com.cn.wms_system_new.service.BootBroadcastReceiver;

/**
 * Created by LFeng on 2017/7/6.
 */

public class MainFragmentActivity extends FragmentActivity {

    private BootBroadcastReceiver receiver;
    private TitleViewHolder titleHolder;
    private JSONObject menuJson;
    private Bundle bundle;

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        getWindow().setFlags(Constants.FLAG_HOMEKEY_DISPATCHED,
                Constants.FLAG_HOMEKEY_DISPATCHED);
        setContentView(R.layout.activity_main_fragment);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.layout_title_bar);

        initTitleComponents();
        setTitleComponents();
        initParams();

        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.list_fun);
        ListFunFragment listFunFragment = (ListFunFragment) fragment;
        listFunFragment.setMenuJson(menuJson);
        listFunFragment.setCallback(new ListFunFragment.BtnClickCallback() {
            @Override
            public void btnClick(String funName, JSONObject object) {
                if (findViewById(R.id.detail_fun) != null) {
                    DetailFunFragment detailFunFragment = DetailFunFragment.newInstance(funName, object);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.detail_fun, detailFunFragment);
                    transaction.commit();
                }
            }
        });

        /**
         * Check that the activity is using the layout version with the
         * fragment_container FrameLayout
         */
        if (findViewById(R.id.detail_fun) != null) {
            /**
             * However, if we're being restored from a previous state, then we
             * don't need to do anything and should return or else we could end
             * up with overlapping fragments.
             */
            if (savedInstanceState != null)
                return;
            MainInterface mainInterface = new MainInterface();

            FragmentTransaction transaction = getSupportFragmentManager()
                    .beginTransaction();
            transaction.add(R.id.detail_fun, mainInterface);
            // transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void initTitleComponents() {
        titleHolder = new TitleViewHolder();
        titleHolder.backButton = (LinearLayout) findViewById(R.id.back);
        titleHolder.curSystemTime = (TextView) findViewById(R.id.cur_sys_time);
        titleHolder.titleTextView = (TextView) findViewById(R.id.title);
        titleHolder.refreshButton = (LinearLayout) findViewById(R.id.refresh);
        titleHolder.exitButton = (LinearLayout) findViewById(R.id.exit);
    }

    public void setTitleComponents() {
        titleHolder.backButton.setVisibility(View.INVISIBLE);
        titleHolder.curSystemTime.setText(getResources().getString(R.string.cur_time_promote)
                + GetNowTime.getHour()
                + ":"
                + ((GetNowTime.getMinute() < 10) ? ("0" + GetNowTime
                .getMinute()) : GetNowTime.getMinute()));
        titleHolder.titleTextView.setText(R.string.main_title);

        /**
         * 退出按钮参数设定
         */
        titleHolder.exitButton.setVisibility(View.VISIBLE);
        titleHolder.exitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /**
         * 隐藏刷新按钮
         */
        titleHolder.refreshButton.setVisibility(View.GONE);
    }

    public void initParams() {
        bundle = getIntent().getExtras();
        menuJson = JSONObject.parseObject(bundle.getString("menuJson"), Feature.OrderedField);
        //绑定Service
        Intent intent = new Intent("com.cn.service.NOTIFICATION_UPDATE");
        intent.setPackage("com.cn.wms_system_new");
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        // 注册时间更新广播接收者更新标题栏中的时间
        receiver = new BootBroadcastReceiver() {

            @Override
            public void updateTime() {
                titleHolder.curSystemTime.setText(getResources().getString(
                        R.string.cur_time_promote)
                        + GetNowTime.getHour()
                        + ":"
                        + ((GetNowTime.getMinute() < 10) ? ("0" + GetNowTime
                        .getMinute()) : GetNowTime.getMinute()));
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

    @Override
    protected void onDestroy() {
        unbindService(connection);
        super.onDestroy();
    }
}
