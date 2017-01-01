package com.cn.wetrack.widgets;

import com.cn.wetrack.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * 自定义进度
 */
public class CustomProgressDialog extends Dialog {

	public CustomProgressDialog(Context context) {
		super(context, R.style.CustomProgressDialog);
		this.setContentView(R.layout.customprogressdialog);
		this.getWindow().getAttributes().gravity = Gravity.CENTER;
	}

	public void onWindowFocusChanged(boolean hasFocus) {
		ImageView imageView = (ImageView) this.findViewById(R.id.loadingImageView);
		AnimationDrawable animationDrawable = (AnimationDrawable) imageView.getBackground();
		animationDrawable.start();
	}

	/** 标题 */
	public CustomProgressDialog setTitile(String strTitle) {
		return this;
	}

	/** 提示内容 */
	public void setMessage(String strMessage) {
		TextView tvMsg = (TextView) this.findViewById(R.id.id_tv_loadingmsg);
		if (tvMsg != null) {
			tvMsg.setText(strMessage);
		}
	}
}
