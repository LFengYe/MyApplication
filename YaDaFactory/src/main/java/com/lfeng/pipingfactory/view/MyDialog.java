package com.lfeng.pipingfactory.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.lfeng.pipingfactory.R;

/**
 * Created by LFeng on 2016/8/2.
 */
public class MyDialog extends Dialog {

    public MyDialog(Context context, int width, int height, String text, String type) {
        super(context);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = View.inflate(context, R.layout.dialog, null);
        AVLoadingIndicatorView avLoadingIndicatorView = (AVLoadingIndicatorView) v.findViewById(R.id.progressbar);
        TextView tv = (TextView) v.findViewById(R.id.tv_tip);
        avLoadingIndicatorView.applyIndicator(type);
        tv.setText(text);
        this.setContentView(v);
        this.setCancelable(false);
        Window dialogWindow = this.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes(); // 获取对话框当前的参数值
        dialogWindow.setGravity(Gravity.CENTER);
        lp.height = height;
        lp.width = width;
        dialogWindow.setAttributes(lp);
    }
}
