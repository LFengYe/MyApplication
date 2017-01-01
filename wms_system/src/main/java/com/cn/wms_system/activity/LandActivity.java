package com.cn.wms_system.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wms_system.R;
import com.cn.wms_system.component.Constants;
import com.cn.wms_system.component.GetNowTime;
import com.cn.wms_system.component.WebOperate;
import com.cn.wms_system.service.BootBroadcastReceiver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LandActivity extends Activity {

	private View view;
	private AutoCompleteTextView userCodeEdit;
	private Button userHistoryList;
	private EditText landPasswordEdit;
	// private EditText dataAccountEdit;
	private TextView dateText;

	private WebOperate webOperate;
	private ArrayList<String> authorityIDList;
	private BootBroadcastReceiver receiver;

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.land_button) {
				Map<String, String> params = new HashMap<String, String>();
				if (Constants.debug_mode) {
					params.put("s", "{33;"
							+ userCodeEdit.getEditableText().toString() + ";"
							+ Constants.debug_Mac_address + ";}");
				} else {
					params.put("s", "{33;"
							+ userCodeEdit.getEditableText().toString() + ";"
							+ Constants.getMacAddress(LandActivity.this) + ";}");
				}
				webOperate.Request("send", params);
			}

			if (v.getId() == R.id.server_setting) {
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString(
						"title",
						getResources().getStringArray(
								R.array.system_setting_list)[1]);
				intent.putExtras(bundle);
				intent.setClass(getApplicationContext(),
						ParamsSettingActivity.class);
				startActivity(intent);
			}

			if (v.getId() == R.id.user_history_list) {
				userCodeEdit.showDropDown();
			}

			if (v.getId() == R.id.exit_button) {
				finish();
			}
		}
	};

	private Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.GET_PLAN_LIST_MESSAGE:
				String password = progressData(webOperate.getResult());

				if (password.compareTo(landPasswordEdit.getEditableText()
						.toString()) == 0) {

					Intent intent = new Intent();
					Bundle bundle = new Bundle();
					bundle.putStringArrayList("authority_id_list",
							authorityIDList);
					intent.putExtras(bundle);
					intent.setClass(getApplicationContext(), MainFragment.class);
					startActivity(intent);
					// 保存用户名
					saveHistory("username", userCodeEdit);
					/*
					 * SharedPreferences preferences =
					 * getSharedPreferences("system_params", MODE_PRIVATE);
					 * Editor editor = preferences.edit();
					 * editor.putString("username",
					 * userCodeEdit.getEditableText().toString());
					 * editor.putString("password",
					 * landPasswordEdit.getEditableText().toString());
					 * editor.commit();
					 */
				} else {
					Toast.makeText(getApplicationContext(), "用户名或密码错误",
							Toast.LENGTH_SHORT).show();
				}
				break;
			case Constants.GET_MESSAGE_IS_EMPTY:
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(Constants.FLAG_HOMEKEY_DISPATCHED,
				Constants.FLAG_HOMEKEY_DISPATCHED);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		getLayoutInflater();
		view = LayoutInflater.from(this).inflate(R.layout.activity_land_system,
				null);
		setContentView(view);

		initComponents();
		setParams();
	}

	private void initComponents() {

		TextView tempDisplay = (TextView) findViewById(R.id.temp_display);
		tempDisplay.setText("Mac Address:" + Constants.getMacAddress(this));

		dateText = (TextView) findViewById(R.id.date_reminder);
		dateText.setText(GetNowTime.getHanYuDate());

		Button landButton = (Button) findViewById(R.id.land_button);
		landButton.setOnClickListener(clickListener);

		Button exitButton = (Button) findViewById(R.id.exit_button);
		exitButton.setOnClickListener(clickListener);

		Button serverSetButton = (Button) findViewById(R.id.server_setting);
		serverSetButton.setOnClickListener(clickListener);

		userCodeEdit = (AutoCompleteTextView) findViewById(R.id.user_code);
		userHistoryList = (Button) findViewById(R.id.user_history_list);
		landPasswordEdit = (EditText) findViewById(R.id.land_password);
		userCodeEdit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				landPasswordEdit.setText("");
			}
		});

		userHistoryList.setOnClickListener(clickListener);
		// dataAccountEdit = (EditText) findViewById(R.id.data_account);
	}

	private void initAutoComplete(String field, AutoCompleteTextView auto) {

		SharedPreferences sp = getSharedPreferences("system_params",
				MODE_PRIVATE);
		String longhistory = sp.getString(field, "");
		String[] hisArrays = longhistory.split(",");
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line, hisArrays);

		if (hisArrays.length > 5) {
			String[] newArrays = new String[5];
			System.arraycopy(hisArrays, 0, newArrays, 0, 5);
			adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_dropdown_item_1line, newArrays);
		}

		userCodeEdit.setAdapter(adapter);
		userCodeEdit.setThreshold(1);
		userCodeEdit.setCompletionHint("最多显示5个记录");
		/*
		 * userCodeEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
		 * 
		 * @Override public void onFocusChange(View v, boolean hasFocus) {
		 * AutoCompleteTextView view = (AutoCompleteTextView) v; if (hasFocus) {
		 * view.showDropDown(); } } });
		 */
	}

	private void saveHistory(String field, AutoCompleteTextView auto) {
		String text = auto.getText().toString();
		SharedPreferences sp = getSharedPreferences("system_params",
				MODE_PRIVATE);
		String history = sp.getString(field, "");

		//
		if (!history.contains(text + ",")) {
			StringBuilder sb = new StringBuilder(history);
			sb.insert(0, text + ",");
			sp.edit().putString(field, sb.toString()).commit();
		} else {
			StringBuilder sb = new StringBuilder(history);
			sb.delete(sb.indexOf(text + ","), sb.indexOf(text + ",")
					+ (text + ",").length());
			sb.insert(0, text + ",");
			sp.edit().putString(field, sb.toString()).commit();
		}
	}

	private void setParams() {
		// SharedPreferences preferences = getSharedPreferences("system_params",
		// MODE_PRIVATE);
		// Constants.webServiceURL = preferences.getString("webserver_http",
		// Constants.webServiceURL) + "Service.asmx";
		webOperate = new WebOperate(myHandler);
	}

	/**
	 * 从返回数据获取用户的权限列表和登录密码
	 * @param data
	 * @return 用户登录密码
	 */
	public String progressData(String data) {
		String[] items = data.substring(1, data.length() - 1).split(";"); 
		
		authorityIDList = new ArrayList<String>();
		String[] authorityList = items[1].split(",");
		for (String temp : authorityList) {
			authorityIDList.add(temp);
		}
		return items[0];
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}

	@Override
	protected void onResume() {
		dateText.setText(GetNowTime.getHanYuDate());
		initAutoComplete("username", userCodeEdit);
		// 注册时间更新广播接收者更新标题栏中的时间
		receiver = new BootBroadcastReceiver() {

			@Override
			public void updateTime() {
				dateText.setText(GetNowTime.getHanYuDate());
			}

			@Override
			public void updateData() {
			}
		};
		IntentFilter filter = new IntentFilter(
				"android.intent.action.TIME_TICK");
		registerReceiver(receiver, filter);
		super.onResume();
	}
}
