package com.cn.wms_system.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;

import com.cn.wms_system_new.R;


/**
 * Created by LFeng on 2017/6/15.
 */

public class ConfirmAmountDialog extends Activity {

    Bundle bundle = new Bundle();
    private EditText remarkEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_amount_confirm);

        initComponents();
        initParams();
    }

    private void initParams() {
        bundle = getIntent().getExtras();
    }

    private void initComponents() {

    }
}
