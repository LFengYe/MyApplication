package com.cxgps.vehicle.fragment;


import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.UserInfo;
import com.cxgps.vehicle.utils.DialogHelper;
import com.cxgps.vehicle.utils.UIHelper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by cxiosapp on 15/12/26.
 */
public class MyInformationFragment  extends BaseFragment implements View.OnClickListener {


    @Bind(R.id.tv_login_name)
    TextView mName;

    @Bind(R.id.tv_join_time)
    TextView mJoinTime;

    @Bind(R.id.tv_loginstyle)
    TextView mLoginstyle;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_information_detail, container,
                false);
        ButterKnife.bind(this, view);
        getActivity().setTitle(getString(R.string.main_title_persion));

        initView(view);
        initData();
        fillUI();
        return view;
    }

    public void fillUI() {

        UserInfo userInfo = AppContext.getInstance().getLoginUser();

        if (userInfo!= null && Boolean.valueOf(userInfo.getuLoginFlag()))
            mName.setText(userInfo.getUserName());
        mLoginstyle.setText(userInfo.getuLoginType() ==0 ? getString(R.string.user_login_way):getString(R.string.car_login_way));
        mJoinTime.setText(userInfo.getuLogintime());

    }

    @Override
    @OnClick({R.id.btn_logout})
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_logout:

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
            default:
                break;
        }
    }
}
