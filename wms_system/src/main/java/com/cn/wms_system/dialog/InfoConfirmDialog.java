package com.cn.wms_system.dialog;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.cn.wms_system.R;
import com.cn.wms_system.component.Constants;
import com.cn.wms_system.component.GetNowTime;
import com.cn.wms_system.component.WebOperate;

import java.util.HashMap;
import java.util.Map;

public class InfoConfirmDialog extends Activity{

	private Button confirmButton;
	private Button cancelButton;
	
	private WebOperate webOperate;
	private Map<String, String> params;
	private String methodName;
	
	SharedPreferences preferences;
	private Bundle bundle;
	
	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			if (view.getId() == confirmButton.getId()) {
				uploadData();
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("username", preferences.getString("username", "").split(",")[0]);
				bundle.putString("confirm_time", GetNowTime.getNowTime());
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
			}
			finish();
		}
	};
	
	private Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.GET_PLAN_LIST_MESSAGE:
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
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.dialog_info_confirm);
		
		initComponents();
		initParams();
	}
	
	public void initComponents() {
		Intent intent = getIntent();
		
		EditText supplier = (EditText) findViewById(R.id.supplier);
		supplier.setText(intent.getStringExtra("供应商"));


		EditText partsName = (EditText) findViewById(R.id.parts_name);
		partsName.setText(intent.getStringExtra("部品名称"));

		EditText partsNum = (EditText) findViewById(R.id.parts_num);
		partsNum.setText(intent.getStringExtra("部品件号"));
		
		EditText confirmText = (EditText) findViewById(R.id.confirm_num);
		confirmText.setText(intent.getStringExtra("数量"));
		
		confirmButton = (Button) findViewById(R.id.confirm_button);
		confirmButton.setText(R.string.setting_confirm);
		confirmButton.setOnClickListener(onClickListener);

		cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setText(R.string.setting_cancel);
		cancelButton.setOnClickListener(onClickListener);
	}
	
	/**
	 * 初始化参数
	 */
	public void initParams() {
		bundle = getIntent().getExtras();
		preferences = getSharedPreferences("system_params", MODE_PRIVATE);

		params = new HashMap<String, String>();
		webOperate = new WebOperate(myHandler);
	}
	
	/**
	 * 下载数据
	 */
	public void uploadData() {
		methodName = "send";
		int commandNO = 0;
		switch (bundle.getInt("selected_fun_index", 4)) {
		case 4:
			commandNO = 10 + bundle.getInt("selected_child_fun_item", 0) * 3;
			break;
		case 5:
			commandNO = 13 + bundle.getInt("selected_child_fun_item", 0) * 3;
		default:
			break;
		}
		
		params.put("s", "{" + commandNO + ";"
						+ bundle.getString("plan_odd_num") + ";"
						+ bundle.getString("部品件号") + ";"
						+ bundle.getString("部品批次") + ";"
						+ preferences.getString("username", "").split(",")[0]
						+ ";}");
		webOperate.Request(methodName, params);
	}
	
}
