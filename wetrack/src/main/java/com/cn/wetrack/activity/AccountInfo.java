package com.cn.wetrack.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cn.wetrack.R;

/**
 * 用户信息
 */
@SuppressLint("HandlerLeak")
public class AccountInfo extends Activity {
	SWApplication glob;// 全局控制类
	private ImageButton InfoButtonBack;// 返回
	private TextView NiceName;
	private TextView CompanyName;
	private TextView LinkMan;
	private TextView Contact;
	private TextView Address;
	private TextView Remark;
	private TextView CompanyAddress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.account_info);

		/* 取得全局变量 */
		glob = (SWApplication) getApplicationContext();
		glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);

		/* 返回 */
		InfoButtonBack = (ImageButton) findViewById(R.id.InfoButtonBack);
		InfoButtonBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		NiceName = (TextView) findViewById(R.id.NiceName);
		NiceName.setText(glob.account.getNiceName() + "");
		CompanyName = (TextView) findViewById(R.id.CompanyName);
		CompanyName.setText(glob.account.getCompanyName() + "");
		LinkMan = (TextView) findViewById(R.id.LinkMan);
		LinkMan.setText(glob.account.getLinkMan() + "");
		Contact = (TextView) findViewById(R.id.Contact);
		Contact.setText(glob.account.getContact() + "");
		Address = (TextView) findViewById(R.id.Address);
		Address.setText(glob.account.getAddress() + "");
		Remark = (TextView) findViewById(R.id.Remark);
		Remark.setText(glob.account.getRemark() + "");
		CompanyAddress = (TextView) findViewById(R.id.companyaddress);
		CompanyAddress.setText(glob.account.getCompanyAddress() + "");
	}

	/** 返回按键 结束activity */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
			finish();
		return false;
	}
}