package com.cn.carigps.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cn.carigps.R;
import com.cn.carigps.entity.SResponse;
import com.cn.carigps.util.HttpRequestClient;
import com.cn.carigps.util.SProtocol;
import com.cn.carigps.widgets.CustomProgressDialog;

public class Feedback extends Activity {
	private ImageButton back;
	private EditText edittext;
	private Button submit;
	MyApplication glob;// 全局控制类
	private CustomProgressDialog progressDialog = null;
	private Thread feedbackthread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout .feedback);
		/*取得全局变量*/
		glob = (MyApplication) getApplicationContext();
		/*返回*/
		back=(ImageButton)this.findViewById(R.id.backButton);
		back.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		edittext=(EditText)this.findViewById(R.id.feedback_edittext);
		submit=(Button)this.findViewById(R.id.feedback_post);
		submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(edittext.getText().toString().equals("")){
					Toast.makeText(Feedback.this, getResources().getString(R.string.feedback_nodata), Toast.LENGTH_SHORT).show();
				}
				else {
					progressDialog = new CustomProgressDialog(Feedback.this);
			    	progressDialog.setMessage(getResources().getString(R.string.feedback_submiting));
			    	progressDialog.show();
			    	feedbackthread=new FeedbackThread();
			    	handler.post(feedbackthread);
				}
			}
		});
		
	}
	/** 反馈 */
	class FeedbackThread extends Thread {
		public void run() {
//			Log.d("test", edittext.getText().toString());
			SResponse response1 = HttpRequestClient.AddFeedback(glob.account.getNiceName(), edittext.getText().toString());
			if (response1.getCode() == SProtocol.SUCCESS) {
				Message msg = new Message();
				msg.what = 0;
				handler.sendMessage(msg);
			}
			else{
				Message msg = new Message();
				msg.what = 1;
				handler.sendMessage(msg);
			}
			handler.post(this);
		}
	}
	public Handler handler=new Handler(){
		public void handleMessage(Message msg) {
			progressDialog.dismiss();
			switch(msg.what){
			case 0:
				Toast.makeText(Feedback.this, getResources().getString(R.string.feedback_success), Toast.LENGTH_SHORT).show();
				finish();
				break;
			case 1:
				Toast.makeText(Feedback.this, getResources().getString(R.string.feedback_fail), Toast.LENGTH_SHORT).show();
				break;
			}
			handler.removeCallbacks(feedbackthread);
		};
	};

}
