package com.cn.wms_system_new.dialog;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cn.wms_system_new.R;
import com.cn.wms_system_new.service.MyHandler;
import com.cn.wms_system_new.service.MyThread;

public class ChangePassword extends Dialog {

	private EditText oldPassEdit;
	private EditText newPassEdit;
	private EditText confirmPassEdit;
	private Button confirmButton;
	private Button cancelButton;

	private String username;
	private String oldPassword;
	private String newPassword;

	private Context context;

	private Handler handler = new MyHandler(getContext()) {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case MyThread.RESPONSE_SUCCESS: {
					break;
				}
			}
		}
	};
	
	public ChangePassword(Context context) {
		super(context);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_change_pass_dialog);

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
