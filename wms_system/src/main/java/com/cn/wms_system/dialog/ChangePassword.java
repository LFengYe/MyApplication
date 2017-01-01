package com.cn.wms_system.dialog;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cn.wms_system.R;
import com.cn.wms_system.component.Constants;
import com.cn.wms_system.component.WebOperate;

import java.util.HashMap;
import java.util.Map;

public class ChangePassword extends Dialog {

	private EditText oldPassEdit;
	private EditText newPassEdit;
	private EditText confirmPassEdit;
	private Button confirmButton;
	private Button cancelButton;

	private WebOperate webOperate;
	private String username;
	private String oldPassword;
	private String newPassword;

	private Context context;

	private Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case Constants.GET_PLAN_LIST_MESSAGE:
				String result = webOperate.getResult();
				if (result.compareTo("0") == 0) {
					createDialog(context.getResources().getString(R.string.modity_password), 
							context.getResources().getString(R.string.old_password_error));
				} else if (result.compareTo("1") == 0) {
					createDialog(context.getResources().getString(R.string.modity_password),
							context.getResources().getString(R.string.modify_successed));
				} else {
					createDialog(context.getResources().getString(R.string.modity_password),
							result);
				}
				break;
			case Constants.GET_MESSAGE_IS_EMPTY:
				System.out.println("GET_MESSAGE_IS_EMPTY");
				break;
			default:
				break;
			}
		};
	};
	
	public ChangePassword(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_change_pass);

		oldPassEdit = (EditText) findViewById(R.id.old_password);
		newPassEdit = (EditText) findViewById(R.id.new_password);
		confirmPassEdit = (EditText) findViewById(R.id.confirm_password);

		confirmButton = (Button) findViewById(R.id.modity_password);
		confirmButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				oldPassword = oldPassEdit.getText().toString();
				newPassword = newPassEdit.getText().toString();
				String confirmPass = confirmPassEdit.getText().toString();
				/**
				 * 原密码输入不能为空
				 */
				if (oldPassword == null || oldPassword.length() == 0) {
					Toast.makeText(context, context.getResources().getString(R.string.input_old_password), Toast.LENGTH_SHORT).show();
					return;
				}
				/**
				 * 新密码不能为空
				 */
				if (newPassword == null || newPassword.length() == 0) {
					Toast.makeText(context, context.getResources().getString(R.string.input_new_password), Toast.LENGTH_SHORT).show();
					return;
				}
				/**
				 * 两次密码输入不一致
				 */
				if (!newPassword.equals(confirmPass)) {
					Toast.makeText(context, context.getResources().getString(R.string.confirm_password_error), Toast.LENGTH_SHORT).show();
					return;
				}
				/**
				 * 向服务器发送修改密码请求
				 */
				SharedPreferences preferences = context.getSharedPreferences("system_params", Context.MODE_PRIVATE);
				Map<String, String> params = new HashMap<String, String>();
				params.put("s", "{47;"
						+ preferences.getString("username", "").split(",")[0]
						+ ";" + oldPassword + ";" + newPassword+ ";}");
				
				webOperate.Request("send", params);
				dismiss();
			}
		});

		cancelButton = (Button) findViewById(R.id.modify_cancel);
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				dismiss();
			}
		});

		init();
	}

	public void init() {
		webOperate = new WebOperate(myHandler);
	}
	
	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getInputOldPassword() {
		return oldPassEdit.getEditableText().toString();
	}

	public void createDialog(String title, String message) {
		//弹出提示对话框
		Builder builder = new Builder(context)
				.setTitle(title).setMessage(message)
				.setPositiveButton(R.string.setting_confirm, null);
		Dialog dialog = builder.create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}
}
