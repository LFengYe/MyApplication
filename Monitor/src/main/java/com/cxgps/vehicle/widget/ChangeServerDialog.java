package com.cxgps.vehicle.widget;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.cxgps.vehicle.AppConfig;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.utils.StringUtils;


/**
 * Created by cxiosapp on 15/8/13.
 */
public class ChangeServerDialog extends Dialog implements TextView.OnEditorActionListener,View.OnClickListener{



  private EditText server_host;

  private EditText server_port;

  private TextView dialog_cancle;

  private TextView dialog_ok;

  private InputMethodManager im;

    public interface  onChangeServerformClick{
        void onChangeServer();

    }

    private onChangeServerformClick  mChangeListener;

    private ChangeServerDialog(Context context, boolean flag, OnCancelListener listener){

        super(context,flag,  listener);

    }


   public ChangeServerDialog(Context context){
       this(context, R.style.quick_option_dialog);
   }

    private ChangeServerDialog(Context context, int defStyle){

        super(context,defStyle);

        im = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);

        View contentView = getLayoutInflater().inflate(R.layout.view_dialog__settingip,null);
        server_host =  (EditText) contentView.findViewById(R.id.server_host);
        server_port = (EditText) contentView.findViewById(R.id.server_port);
        dialog_cancle = (TextView)contentView.findViewById(R.id.dialog_cancle);
        dialog_ok = (TextView)contentView.findViewById(R.id.dialog_ok);


        // 设置事件
        dialog_ok.setOnClickListener(this);
        dialog_cancle.setOnClickListener(this);
        server_port.setOnEditorActionListener(this);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        contentView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                ChangeServerDialog.this.dismiss();
                return true;
            }
        });


        server_host.setText(AppContext.get("login.host", AppConfig.CONSTANTS_DEFAULT_HOST).toString());
        server_port.setText(AppContext.get("login.port", AppConfig.CONSTANTS_DEFAULT_PORT).toString());

        super.setContentView(contentView);


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        WindowManager m = getWindow().getWindowManager();

        Display d = m.getDefaultDisplay();

        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();

        getWindow().setAttributes(p);


    }

    @Override
    public void onClick(View v) {

        final  int id = v.getId();

        switch (id){

            case R.id.dialog_cancle:
                dismiss();
                break;
            case R.id.dialog_ok:

             boolean flag =    checkAndSetServer();

                if (flag){

                    dismiss();
                }
                break;


        }

    }


    @Override
    public void dismiss() {
        im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        super.dismiss();
        Log.i("TAG", "=======setOnDismissListener========");


    }

    @Override
    public void setOnDismissListener(OnDismissListener listener) {
        super.setOnDismissListener(listener);

    }

    private boolean checkAndSetServer(){

        String serverip = server_host.getText().toString().trim();
        String port = server_port.getText().toString().trim();

        if (StringUtils.isEmpty(serverip)) {

            AppContext.showToast(R.string.login_msg_ip_null);
            return false;
        }
        if (!StringUtils.isIpV4AndV6AndHost(serverip)) {

            AppContext.showToast(getContext().getString(R.string.login_msg_ip_error));
            return false;
        }

        if (StringUtils.isEmpty(port)) {
            AppContext.showToast(getContext().getString(R.string.login_msg_port_null));
            return false;
        }

        if (!StringUtils.isPort(port)) {
            AppContext.showToast(
                    getContext().getString(R.string.login_msg_port_error));
            return false;
        }

        AppContext.getInstance().setAddress(serverip,port);

       return true;

    }



    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        // 在输入法里点击了“完成”，则去登录
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            checkAndSetServer();
            // 将输入法隐藏
            im = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(server_port.getWindowToken(), 0);
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                im.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);

    }
}
