package com.cn.wms_system.component;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.wms_system.R;

public class TitleViewHolder {
	public LinearLayout backButton;
	public TextView curSystemTime;
	public TextView titleTextView;
	public LinearLayout refreshButton;
	public LinearLayout exitButton;
	
	/**
	 * 设置返回按钮图片为传入的资源ID引用的图片
	 * 如果要设置按钮图片为空，传入资源ID为－1即可
	 * @param resId
	 */
	public void setBackImage(int resId) {
		if (resId == -1) {
			((ImageView)backButton.findViewById(R.id.back_image)).setImageDrawable(null);
			return ;
		}
		((ImageView)backButton.findViewById(R.id.back_image)).setImageResource(resId);
	}
	
	/**
	 * 设置返回按钮的文本为给定的text
	 * @param text
	 */
	public void setBackText(String text) {
		((TextView)backButton.findViewById(R.id.back_text)).setText(text);
	}
	
	/**
	 * 设置刷新按钮的文本为给定的text
	 * @param text
	 */
	public void setRefreshText(String text) {
		((TextView)refreshButton.findViewById(R.id.refresh_text)).setText(text);
	}
	
	/**
	 * 设置刷新按钮图片为传入的资源ID引用的图片
	 * 如果要设置按钮图片为空，传入资源ID为－1即可
	 * @param resId
	 */
	public void setRefreshImage(int resId) {
		if (resId == -1) {
			((ImageView)refreshButton.findViewById(R.id.refresh_image)).setImageDrawable(null);
			return ;
		}
		
		((ImageView)refreshButton.findViewById(R.id.refresh_image)).setImageResource(resId);
	}
}
