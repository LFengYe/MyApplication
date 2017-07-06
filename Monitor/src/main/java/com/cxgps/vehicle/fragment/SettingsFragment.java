package com.cxgps.vehicle.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxgps.vehicle.AppConfig;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.SimpleBackPage;
import com.cxgps.vehicle.bean.UserInfo;
import com.cxgps.vehicle.service.UpdateManager;
import com.cxgps.vehicle.utils.DialogHelper;
import com.cxgps.vehicle.utils.FileUtil;
import com.cxgps.vehicle.utils.MethodsCompat;
import com.cxgps.vehicle.utils.UIHelper;
import com.cxgps.vehicle.widget.togglebutton.ToggleButton;

import org.kymjs.kjframe.http.HttpConfig;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by taosong on 16/12/22.
 */

public class SettingsFragment extends BaseFragment  implements ToggleButton.OnToggleChanged {


    @Bind(R.id.tb_autolgon)
    ToggleButton mAutoLoginTag;

    @Bind(R.id.tv_moreflush_time)
    TextView mMoreflushtime;

    @Bind(R.id.tv_zhuizong_time)
    TextView  mZhuizongtime;

    @Bind(R.id.tv_cache_size)
    TextView mCachesize;


    @Bind(R.id.tv_user_map_title)
    TextView  mMaptitle;

    @Bind(R.id.tv_user_language_title)
    TextView  mLanguagetitle;



    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container,
                false);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }


    @Override
    public void initView(View view) {
        super.initView(view);

    }


    @Override
    public void initData() {
        mAutoLoginTag.setOnToggleChanged(this);

        long secondZhuizong =  AppContext.geReflushTime(AppConfig.KEY_REFLUSH_ZUIZONG_TIME);

        long secondMonitor =  AppContext.geReflushTime(AppConfig.KEY_REFLUSH_MONITOR_TIME);


        mZhuizongtime.setText(secondZhuizong+ "s");
        mMoreflushtime.setText(secondMonitor + "s");

        caculateCacheSize();

        UserInfo userInfo = AppContext.getInstance().getLoginUser();

        String isAutoLogin = "";

        if (userInfo!= null) {
            isAutoLogin = userInfo.getuIsAuto();

        }else {
            isAutoLogin = "false";

        }

        setToggle(Boolean.valueOf(isAutoLogin), mAutoLoginTag);



        String lanStr =  AppContext.getLanguageWithKey();

        String  lanText = getString(R.string.lan_chinese);

        if ("en".equals(lanStr)){

            lanText  = getString(R.string.lan_english);

        }

        mLanguagetitle.setText(lanText);


        String mapWithKey =  AppContext.getMapWithKey();

        String  mapText = getString(R.string.map_baidu);

        if ("2".equals(mapWithKey)){

            mapText = getString(R.string.map_google);

        }

        mMaptitle.setText(mapText);





    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    /**
     * 计算缓存的大小
     */
    private void caculateCacheSize() {
        long fileSize = 0;
        String cacheSize = "0KB";
        File filesDir = getActivity().getFilesDir();
        File cacheDir = getActivity().getCacheDir();

        fileSize += FileUtil.getDirSize(filesDir);
        fileSize += FileUtil.getDirSize(cacheDir);
        // 2.2版本才有将应用缓存转移到sd卡的功能
        if (AppContext.isMethodsCompat(android.os.Build.VERSION_CODES.FROYO)) {
            File externalCacheDir = MethodsCompat
                    .getExternalCacheDir(getActivity());
            fileSize += FileUtil.getDirSize(externalCacheDir);
            fileSize += FileUtil.getDirSize(new File(
                    org.kymjs.kjframe.utils.FileUtils.getSDCardPath()
                            + File.separator + HttpConfig.CACHEPATH));
        }
        if (fileSize > 0)
            cacheSize = FileUtil.formatFileSize(fileSize);
        mCachesize.setText(cacheSize);
    }


    private void setToggle(boolean value, ToggleButton tb) {
        if (value)
            tb.setToggleOn();
        else
            tb.setToggleOff();
    }

    @Override
    public void onToggle(boolean on) {


        UserInfo  userInfo = AppContext.getInstance().getLoginUser();
        userInfo.setuIsAuto(String.valueOf(on));
        userInfo.setuIsRember(userInfo.getuIsRember());
        AppContext.getInstance().saveUserInfo(userInfo.getuLoginType(),userInfo);
    }



    private void onClickCleanCache() {
        DialogHelper.getConfirmDialog(getActivity(), getString(R.string.tip_clear_cache), new DialogInterface.OnClickListener
                () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UIHelper.clearAppCache(getActivity());
                mCachesize.setText("0KB");
            }
        }).show();
    }




    @OnClick({R.id.rl_app_scope,R.id.rl_user_logout,R.id.rl_monitor_reflush_time,R.id.rl_zhuizong_reflush_time,R.id.rl_clean_cache,R.id.tb_autolgon,R.id.rl_user_map_title,R.id.rl_user_language_title,R.id.rl_set_car_update})
    public  void  onSettingBtn(View view){

        final int id = view.getId();
        switch (id) {
            case R.id.rl_clean_cache:
                onClickCleanCache();
                break;
            case R.id.rl_set_car_update:
                checkUpdate();
                break;
            case R.id.rl_app_scope:
                showToScope();
                break;
            case R.id.rl_monitor_reflush_time:
                showReflushMonitorTime();
                break;
            case R.id.rl_zhuizong_reflush_time:
                showReflusZhizongTime();
                break;
            case R.id.tb_autolgon:
                mAutoLoginTag.toggle();
                break;
            case R.id.rl_user_language_title:
                switchMapAndLan(0);
                break;
            case R.id.rl_user_map_title:

                switchMapAndLan(1);
                break;

            case R.id.rl_user_logout:

                DialogHelper.getConfirmDialog(getActivity(), getString(R.string.tips_logout_msg), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserInfo userInfo = AppContext.getInstance().getLoginUser();

                        if (userInfo != null && Boolean.valueOf(userInfo.getuLoginFlag())) {


                            userInfo.setuLoginFlag("false");

                            AppContext.getInstance().saveUserInfo(userInfo.getuLoginType(), userInfo);
                            UIHelper.showLoginActivity(getActivity());

                            getActivity().finish();
                        }

                    }
                }).create().show();

                break;

        }
    }






    private void showReflushMonitorTime() {

        final String[] selectTimes = getActivity().getResources().getStringArray(R.array.select_times);

        DialogHelper.getSelectDialog(getContext(), getString(R.string.set_monitor_time), selectTimes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String time = selectTimes[which];


                mMoreflushtime.setText(time+"s");



                AppContext.getInstance().setReflushTime(AppConfig.KEY_REFLUSH_MONITOR_TIME, Integer.parseInt(time));

            }
        }).create().show();

    }

    private void showReflusZhizongTime(){
        final String[] selectTimes = getResources().getStringArray(R.array.select_times);

        DialogHelper.getSelectDialog(getContext(), getString(R.string.set_zhuizong_time), selectTimes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                String time = selectTimes[which];


                mZhuizongtime.setText(time+"s");


    ;

                AppContext.getInstance().setReflushTime(AppConfig.KEY_REFLUSH_ZUIZONG_TIME, Integer.parseInt(time));

            }
        }).create().show();

    }



    // 更新
    private void checkUpdate() {
        if (!AppContext.get(AppConfig.KEY_CHECK_UPDATE, true)) {


            return;
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                new UpdateManager(getActivity(), false).checkUpdate();
            }
        }, 2000);
    }


    // 评分
    private void showToScope(){

        Uri uri = Uri.parse("market://details?id="+ AppContext.getInstance().getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

    }

    private  void switchMapAndLan(int mode){

        Bundle  bundle = new Bundle();

        bundle.putInt("mode",mode);

        if (mode == 0){
            UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SWITCHLAN,bundle);


        }else {

            UIHelper.showSimpleBack(getActivity(), SimpleBackPage.SWITCHMAP,bundle);

        }


    }
}
