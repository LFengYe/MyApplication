package com.cn.wetrack.activity;

import java.util.Locale;

import com.cn.wetrack.R;
import com.cn.wetrack.util.HttpRequestClient;
import com.cn.wetrack.util.SProtocol;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

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
//	private CustomProgressDialog progressDialog = null;// 进度;
	//private UpdateManager manager = new UpdateManager(Welcome.this);// 更新

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

//		handler = createHandler();
//		progressDialog = new CustomProgressDialog(Welcome.this);

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
		/*启动更新线程*/
//		new checkUpdateThread().start();
	}

	/** 创建Handler */
//	private Handler createHandler() {
//		Handler handler = new Handler() {
//			@Override
//			public void handleMessage(Message message) {
//				/* 关闭进度条 */
//				if (progressDialog != null && progressDialog.isShowing()) {
//					progressDialog.dismiss();
//				}
//				
//				Bundle b = message.getData();
//				switch (message.what) {
//				case 0: {
//					boolean result = b.getBoolean("result");
//					/* 有更新 */
//					if (result) {
//						SWApplication.welcomeUpdate=true;
//						SWApplication.welcomeDismiss=true;
//						manager.showNoticeDialog();
//					} else {
//						Intent intent = new Intent(Welcome.this, Login.class);
//						startActivity(intent);
//						finish();
//					}
//					break;
//				}
//				}
//				super.handleMessage(message);
//			}
//		};
//		return handler;
//	}

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
		if (SWApplication.welcomeDismiss) {
			SWApplication.welcomeDismiss = false;
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

	/** 检查更新 */
//	class checkUpdateThread extends Thread {
//		public void run() {
//			boolean result = manager.checkUpdate();
//
//			Message msg = new Message();
//			msg.what = 0;
//			Bundle b = new Bundle();
//			b.putBoolean("result", result);
//			msg.setData(b);
//			handler.sendMessage(msg);
//		}
//	}
}