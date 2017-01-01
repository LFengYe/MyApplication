package com.cn.carigps.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.cn.carigps.R;
import com.cn.carigps.util.HttpRequestClient;
import com.cn.carigps.util.SProtocol;

import java.util.Locale;

/**
 * 欢迎
 */
@SuppressLint({ "WorldReadableFiles", "WorldWriteableFiles", "HandlerLeak" })
public class Welcome extends Activity {
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Intent intent = new Intent(Welcome.this, Login.class);
				startActivity(intent);
				finish();
				break;

			default:
				break;
			}
		};
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);


		/* 语言设置 */
		String country = Locale.getDefault().getCountry();
		String language = "en-us";
		if ("CN".equalsIgnoreCase(country)) {
			language = "zh-cn";
		} else if ("TW".equalsIgnoreCase(country)) {
			language = "zh-tw";
		} else if ("HK".equalsIgnoreCase(country)) {
			language = "zh-hk";
		}
		HttpRequestClient.systemLang=language;
		
		/*协议消息国际化*/
		SProtocol.CONNECTION_FAIL=getResources().getString(R.string.CONNECT_FAIL);
		SProtocol.OPERATE_FAIL=getResources().getString(R.string.OPERATE_FAIL);
		
		new intentThread().start();
	}

	/** 返回按键 结束activity */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
		}
		return true;
	}

	/** 退出时销毁 */
	protected void onDestroy() {
		super.onDestroy();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		/*暂时不更新、取消更新*/
		if (MyApplication.welcomeDismiss) {
			MyApplication.welcomeDismiss = false;
			finish();
		}
	}

	class intentThread extends Thread {
		@Override
		public void run() {
			try {
				Thread.sleep(3 * 1000);
				handler.sendEmptyMessage(0);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			super.run();
		}
	}
}