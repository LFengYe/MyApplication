package com.cn.wms_system.activity;

import android.app.Activity;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cn.wms_system.R;
import com.cn.wms_system.component.Constants;
import com.cn.wms_system.component.GetNowTime;
import com.cn.wms_system.component.TitleViewHolder;
import com.cn.wms_system.service.BootBroadcastReceiver;

public class ParamsSettingActivity extends Activity {

	private TitleViewHolder titleHolder;
	private BootBroadcastReceiver receiver;
	
	//private CheckBox enableTimeUpdate;
	//private EditText updateTimeEdit;
	private EditText webserverHttpEdit;
	
	private SharedPreferences preferences;
	private Bundle bundle;
	private String title;

	/**
	 * 点击事件
	 */
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			//点击保存按钮
			if (v.getId() == titleHolder.refreshButton.getId()) {
				saveParams();
			}
			//点击返回按钮
			if (v.getId() == titleHolder.backButton.getId()) {
			}
			
			finish();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_params_setting);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_title_bar);
		
		initTitleComponents();
		initParams();
		setTitleComponents();
		contentComponents();
	}
	
	public void initTitleComponents() {
		titleHolder = new TitleViewHolder();
		titleHolder.backButton = (LinearLayout) findViewById(R.id.back);
		titleHolder.curSystemTime = (TextView) findViewById(R.id.cur_sys_time);
		titleHolder.titleTextView = (TextView) findViewById(R.id.title);
		titleHolder.refreshButton = (LinearLayout) findViewById(R.id.refresh);
	}
	
	public void setTitleComponents() {
		//设置返回按钮
		titleHolder.backButton.setOnClickListener(onClickListener);
		//设置当前时间
		titleHolder.curSystemTime.setText(getResources().getString(R.string.cur_time_promte)
				+ GetNowTime.getHour() + ":"
				+ ((GetNowTime.getMinute() < 10) ? ("0" + GetNowTime.getMinute()) : GetNowTime.getMinute()));
		//设置标题
		titleHolder.titleTextView.setText(title);
		//设置刷新按钮为保存
		titleHolder.refreshButton.setOnClickListener(onClickListener);
		titleHolder.setRefreshText(getResources().getString(R.string.setting_save));
		titleHolder.setRefreshImage(-1);
	}
	
	/**
	 * 初始化参数
	 */
	public void initParams() {
		bundle = getIntent().getExtras();
		title = bundle.getString("title");
		preferences = getSharedPreferences("system_params", MODE_PRIVATE);
		/**
		 * 注册广播接受者，接受系统发送的时间更改广播（一分钟发送一次）
		 */
		receiver = new BootBroadcastReceiver() {
			@Override
			public void updateTime() {
				titleHolder.curSystemTime.setText(getResources().getString(R.string.cur_time_promte)
						+ GetNowTime.getHour() + ":"
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
	}
	
	/**
	 * 内容组件的初始化与设置
	 */
	public void contentComponents() {
		/*
		enableTimeUpdate = (CheckBox) findViewById(R.id.enable_time);
		enableTimeUpdate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				updateTimeEdit.setFocusableInTouchMode(isChecked);
				if (!isChecked)
					updateTimeEdit.clearFocus();
			}
		});
		
		updateTimeEdit = (EditText) findViewById(R.id.update_time);
		updateTimeEdit.setFocusableInTouchMode(false);
		*/
		webserverHttpEdit = (EditText) findViewById(R.id.webserver_http);
		webserverHttpEdit.setText(preferences.getString("webserver_http", null));
	}
	
	/**
	 * 保存参数
	 */
	public void saveParams() {
		Editor editor = preferences.edit();
		
		editor.putString("webserver_http", webserverHttpEdit.getEditableText().toString());
		Constants.webServiceURL = webserverHttpEdit.getEditableText().toString() + "Service.asmx";
//		WebOperate.setWebServiceURL(webserverHttpEdit.getEditableText().toString());
		
		editor.commit();
	}
	
	@Override
	protected void onDestroy() {
		unregisterReceiver(receiver);
		super.onDestroy();
	}
}
