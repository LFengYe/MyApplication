package com.cxgps.vehicle.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.cxgps.vehicle.R;
import com.cxgps.vehicle.api.remote.VehicleApi;
import com.cxgps.vehicle.base.BaseFragment;
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by taosong on 16/12/23.
 */

public class FeedBackFragment extends BaseFragment {



    @Bind(R.id.rb_error)
    RadioButton rb_error;

    @Bind(R.id.et_feed_back)
    EditText et_feed_back;




    private String mFilePath = "";
    private String mFeedbackStr = "[Android-APP-%s]";
    private ProgressDialog mDialog;


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feedback, container,
                false);
        ButterKnife.bind(this, view);


        initView(view);
        initData();

        return view;
    }


    @Override
    public void initView(View view) {
        super.initView(view);
        rb_error.setChecked(true);
    }

    @Override
    public void initData() {

    }


    @OnClick(R.id.btn_commit)
    public void onCommit(View view) {
        switch (view.getId()) {
            case R.id.btn_commit:

                String content = getFeedBackContent();
                if (TextUtils.isEmpty(content) && TextUtils.isEmpty(mFilePath)) {
                    return;
                }
                content = String.format(mFeedbackStr, rb_error.isChecked() ? getString(R.string.str_feedback_program) : getString(R.string.str_feedback_adverise)) + content;

                addFeedBack(content);

                break;

        }
    }


    /**
     * 添加反馈，走系统私信接口
     *
     * @param content content
     */
    private void addFeedBack(String content) {
        getDialog(getString(R.string.data_loading)).show();
        VehicleApi.feedback(content, new HttpCallback(){

            @Override
            public void onSuccess(Map<String, String> headers, byte[] t) {
                super.onSuccess(headers, t);
                Toast.makeText(getActivity(),getString(R.string.str_feedback_succeed), Toast.LENGTH_LONG).show();
                try {
                    Thread.sleep(2*1000);
                    getActivity().finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int errorNo, String strMsg) {
                super.onFailure(errorNo, strMsg);
                Toast.makeText(getActivity(), getString(R.string.tips_have_no_intent), Toast.LENGTH_LONG).show();
            }
            @Override
            public void onFinish() {
                super.onFinish();
                mDialog.dismiss();
            }
        });

    }

    public String getFeedBackContent() {
        return et_feed_back.getText().toString().trim();
    }

    public ProgressDialog getDialog(String message) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(getActivity());
            mDialog.setCancelable(false);
            mDialog.setCanceledOnTouchOutside(false);
        }
        mDialog.setMessage(message);
        return mDialog;
    }

}
