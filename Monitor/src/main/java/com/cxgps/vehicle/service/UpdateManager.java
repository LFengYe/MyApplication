package com.cxgps.vehicle.service;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.api.remote.VehicleApi;
import com.cxgps.vehicle.bean.ResponseBean;
import com.cxgps.vehicle.bean.UpdateInfo;
import com.cxgps.vehicle.utils.DialogHelper;
import com.cxgps.vehicle.utils.TDevice;
import com.cxgps.vehicle.utils.UIHelper;
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.Map;


/**
 * 更新管理类
 *
 * @author FireAnt（http://my.oschina.net/LittleDY）
 * @version 创建时间：2014年11月18日 下午4:21:00
 */

public class UpdateManager {

    private UpdateInfo mUpdate;

    private Context mContext;

    private boolean isShow = false;

    private ProgressDialog _waitDialog;

    private HttpCallback mCheckUpdateHandle = new HttpCallback() {


        @Override
        public void onFailure(int errorNo, String strMsg) {
            super.onFailure(errorNo, strMsg);
            Log.i("TAG","========="+new String(strMsg)+"======="+errorNo);
            hideCheckDialog();
            if (isShow) {
                showFaileDialog();
            }
        }

        @Override
        public void onSuccess(Map<String, String> headers, byte[] t) {
            super.onSuccess(headers, t);

            Log.i("TAG","========="+new String(t));
            hideCheckDialog();

            ResponseBean responseBean = JSON.parseObject(new String(t), ResponseBean.class);
            if (responseBean.isRequestFlag()) {
                mUpdate = JSON.parseObject(responseBean.getData(), UpdateInfo.class);
            }


            onFinshCheck();
        }
    };

    public UpdateManager(Context context, boolean isShow) {
        this.mContext = context;
        this.isShow = isShow;
    }

    public boolean haveNew() {
        if (this.mUpdate == null) {
            return false;
        }
        boolean haveNew = false;
        int curVersionCode = TDevice.getVersionCode(AppContext
                .getInstance().getPackageName());
        if (curVersionCode < mUpdate
                .getVersionCode()) {
            haveNew = true;
        }
        return haveNew;
    }

    public void checkUpdate() {
        if (isShow) {
            showCheckDialog();
        }
        VehicleApi.checkUpdate(mCheckUpdateHandle);
    }

    private void onFinshCheck() {
        if (haveNew()) {
            showUpdateInfo();
        } else {
            if (isShow) {
                showLatestDialog();
            }
        }
    }

    private void showCheckDialog() {
        if (_waitDialog == null) {
            _waitDialog = DialogHelper.getWaitDialog(mContext,mContext.getString(R.string.data_loading));
        }
        _waitDialog.show();
    }

    private void hideCheckDialog() {
        if (_waitDialog != null) {
            _waitDialog.dismiss();
        }
    }

    private void showUpdateInfo() {
        if (mUpdate == null) {
            return;
        }

        AlertDialog.Builder dialog = DialogHelper.getConfirmDialog(mContext, mUpdate.getUpdateLog(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                UIHelper.openDownLoadService(mContext, mUpdate.getDownloadUrl(), mUpdate.getVersionName());
            }
        });
        dialog.show();
    }

    private void showLatestDialog() {
        DialogHelper.getMessageDialog(mContext,mContext.getString(R.string.update_error_last) ).show();
    }

    private void showFaileDialog() {
        DialogHelper.getMessageDialog(mContext,mContext.getString(R.string.update_error_network)).show();
    }
}
