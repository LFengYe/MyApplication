package com.cn.wms_system.dialog;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cn.wms_system.component.Constants;
import com.cn.wms_system.component.WebOperate;
import com.cn.wms_system_new.R;

import java.util.HashMap;
import java.util.Map;

public class InformationDialog extends Activity {

	private EditText theNumEdit;
	private Button expandButton;
	private Button decreaseButton;
	private Button confirmButton;
	private Button cancelButton;

	private WebOperate webOperate;
	private Map<String, String> params;
	private String methodName;

	private boolean finished2Display;
	private Bundle bundle;
	private int laveNum;
	private int theNum;
	
	/**
	 * 监听编辑文本框的文本变化
	 */
	private TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			//在afterTextChanged中，调用setText()方法会循环递归触发监听器，必须合理退出递归，不然会产生异常
			if (!Constants.checkStringIsNum(s.toString()))
				return ;
			if (s.length() > 0 && Integer.valueOf(s.toString()) != theNum) {
				theNum = Integer.valueOf(s.toString());
				if (theNum > laveNum)
					theNum = laveNum;
	            theNumEdit.setText(String.valueOf(theNum));
	            theNumEdit.setSelection(String.valueOf(theNum).length());
	        }
			
			if (s.length() == 0) {
				theNum = 0;
				theNumEdit.setText(String.valueOf(theNum));
	            theNumEdit.setSelection(String.valueOf(theNum).length());
			}
		}
	};
	
	/**
	 * 按钮监听响应事件
	 */
	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(View view) {
			/**
			 * 点击增加按钮
			 */
			if (view.getId() == expandButton.getId()) {
				if (theNum < laveNum)
					theNum++;
				theNumEdit.setText(String.valueOf(theNum));
				theNumEdit.setSelection(String.valueOf(theNum).length());
				return ;
			}
			
			/**
			 * 点击减少按钮
			 */
			if (view.getId() == decreaseButton.getId()) {
				if (theNum > 0)
					theNum--;
				theNumEdit.setText(String.valueOf(theNum));
				theNumEdit.setSelection(String.valueOf(theNum).length());
				return ;
			}
			
			/**
			 * 点击确认按钮
			 */
			if (view.getId() == confirmButton.getId()) {
				uploadData();
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putInt("本次完成数量", Integer.valueOf(theNumEdit.getEditableText().toString()));
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
		setContentView(R.layout.dialog_info_set_num);

		initComponents();
		initParams();
	}

	/**
	 * 初始化组件
	 */
	public void initComponents() {
		Intent intent = getIntent();

		EditText supplier = (EditText) findViewById(R.id.supplier);
		supplier.setText(intent.getStringExtra("供应商"));

		EditText partsBatch = (EditText) findViewById(R.id.parts_batch);
		partsBatch.setText(intent.getStringExtra("部品批次"));

		EditText partsName = (EditText) findViewById(R.id.parts_name);
		partsName.setText(intent.getStringExtra("部品名称"));

		EditText partsNum = (EditText) findViewById(R.id.parts_num);
		partsNum.setText(intent.getStringExtra("部品件号"));

		EditText dressVessles = (EditText) findViewById(R.id.dress_vessels);
		dressVessles.setText(intent.getStringExtra("包装盛具"));

		TextView dressNumText = (TextView) findViewById(R.id.dress_num_text);
		if (intent.getIntExtra("selected_fun_index", 1) == 2 || intent.getIntExtra("selected_fun_index", 1) == 3)
			dressNumText.setVisibility(View.INVISIBLE);
		
		EditText dressNum = (EditText) findViewById(R.id.dress_num);
		dressNum.setText(intent.getStringExtra("换装数量"));
		if (intent.getIntExtra("selected_fun_index", 1) == 2 || intent.getIntExtra("selected_fun_index", 1) == 3)
			dressNum.setVisibility(View.INVISIBLE);

		EditText vesselsRequire = (EditText) findViewById(R.id.vessles_require);
		vesselsRequire.setText(intent.getStringExtra("盛具需求"));

		EditText suitableModels = (EditText) findViewById(R.id.suitable_models);
		suitableModels.setText(intent.getStringExtra("车型"));

		TextView storeAddressText = (TextView) findViewById(R.id.store_address_text);
		if (intent.getIntExtra("selected_fun_index", 1) == 2 || intent.getIntExtra("selected_fun_index", 1) == 1)
			storeAddressText.setText("存放地址");
		if (intent.getIntExtra("selected_fun_index", 1) == 3) {
			storeAddressText.setText("配送地址");
		}
		
		EditText storeAddress = (EditText) findViewById(R.id.store_address);
		if (intent.getIntExtra("selected_fun_index", 1) == 2 || intent.getIntExtra("selected_fun_index", 1) == 1)
			storeAddress.setText(intent.getStringExtra("存放地址"));
		if (intent.getIntExtra("selected_fun_index", 1) == 3) {
			storeAddress.setText(intent.getStringExtra("配送地址"));
		}

		EditText remark = (EditText) findViewById(R.id.remark);
		remark.setText(intent.getStringExtra("备注"));

		EditText planNum = (EditText) findViewById(R.id.plan_num_edit);
		planNum.setText(intent.getStringExtra("计划数量"));

		TextView finished1Text = (TextView) findViewById(R.id.finished1_text);
		finished1Text.setText(intent.getStringExtra("finished1_text"));

		EditText finished1Edit = (EditText) findViewById(R.id.finished1_edit);
		finished1Edit.setText(intent.getStringExtra(finished1Text.getText()
				.toString()));

		/**
		 * 第二个完成是否显示
		 */
		finished2Display = intent.getBooleanExtra("finished2_display", false);
		if (finished2Display) {
			TextView finished2Text = (TextView) findViewById(R.id.finished2_text);
			finished2Text.setVisibility(View.VISIBLE);
			finished2Text.setText(intent.getStringExtra("finished2_text"));

			EditText finished2Edit = (EditText) findViewById(R.id.finished2_edit);
			finished2Edit.setVisibility(View.VISIBLE);
			finished2Edit.setText(intent.getStringExtra(finished2Text.getText()
					.toString()));
		}

		/**
		 * 剩余数量显示
		 */
		EditText laveNumEdit = (EditText) findViewById(R.id.lave_num);
		laveNumEdit.setText(intent.getStringExtra("剩余数量"));

		/**
		 * 本次可以完成的最大数量
		 */
		theNum = laveNum = Integer.valueOf(intent.getStringExtra("最大数量"));
		
		/**
		 * 本次完成数量文本提示
		 * 分备货、领货、配送
		 */
		TextView theNumText = (TextView) findViewById(R.id.the_num_text);
		if (intent.getIntExtra("selected_fun_index", 1) == 1)
			theNumText.setText(R.string.stocking_num);
		if (intent.getIntExtra("selected_fun_index", 1) == 2)
			theNumText.setText(R.string.picking_num);
		if (intent.getIntExtra("selected_fun_index", 1) == 3)
			theNumText.setText(R.string.distribution_num);
		
		/**
		 * 本次完成（备货、领货或配送）数量编辑
		 */
		theNumEdit = (EditText) findViewById(R.id.the_num);
		theNumEdit.setText(String.valueOf(theNum));
		theNumEdit.addTextChangedListener(watcher);

		/**
		 * 增减按钮
		 */
		expandButton = (Button) findViewById(R.id.expand);
		expandButton.setOnClickListener(onClickListener);
		
		/**
		 * 减少按钮
		 */
		decreaseButton = (Button) findViewById(R.id.decrease);
		decreaseButton.setOnClickListener(onClickListener);
		
		/**
		 * 确认按钮
		 */
		confirmButton = (Button) findViewById(R.id.confirm_button);
		confirmButton.setText(R.string.setting_confirm);
		confirmButton.setOnClickListener(onClickListener);

		/**
		 * 取消按钮
		 */
		cancelButton = (Button) findViewById(R.id.cancel_button);
		cancelButton.setText(R.string.setting_cancel);
		cancelButton.setOnClickListener(onClickListener);
	}

	/**
	 * 初始化参数
	 */
	public void initParams() {
		bundle = getIntent().getExtras();

		params = new HashMap<String, String>();
		webOperate = new WebOperate(myHandler);
	}

	/**
	 * 下载数据
	 */
	public void uploadData() {
		methodName = "send";
		params.put(
				"s",
				"{" + (bundle.getInt("selected_fun_index") + 4) + ";"
						+ bundle.getString("plan_odd_num") + ";"
						+ bundle.getString("部品件号") + ";"
						+ bundle.getString("部品批次") + ";"
						+ theNumEdit.getText().toString() + ";}");
		webOperate.Request(methodName, params);
	}

	/**
	 * @return the finished2Display
	 */
	public boolean isFinished2Display() {
		return finished2Display;
	}

	/**
	 * @param finished2Display
	 *            the finished2Display to set
	 */
	public void setFinished2Display(boolean finished2Display) {
		this.finished2Display = finished2Display;
	}

	/**
	 * @return the onClickListener
	 */
	public OnClickListener getOnClickListener() {
		return onClickListener;
	}

	/**
	 * @param onClickListener
	 *            the onClickListener to set
	 */
	public void setOnClickListener(OnClickListener onClickListener) {
		this.onClickListener = onClickListener;
	}
}
