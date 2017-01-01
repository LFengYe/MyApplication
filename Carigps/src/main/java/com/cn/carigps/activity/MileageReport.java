package com.cn.carigps.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cn.carigps.R;
import com.cn.carigps.entity.MileageOilReport;
import com.cn.carigps.entity.SResponse;
import com.cn.carigps.util.HttpRequestClient;
import com.cn.carigps.util.SProtocol;
import com.cn.carigps.widgets.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

/**
 * 累计里程
 */
@SuppressLint("HandlerLeak")
public class MileageReport extends Activity {
	MyApplication glob;// 全局控制类
	private ImageButton backBtn;// 返回
	private TableView tableView;
	private List<String[]> data;
	private CustomProgressDialog progressDialog;

	private SResponse response;
	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressDialog.dismiss();
			if (msg.what == 0) {
				List<MileageOilReport> resData = (List<MileageOilReport>) response.getResult();
				data = new ArrayList<>();
				for (MileageOilReport report: resData) {
					String[] item = new String[5];
					item[0] = report.getVehNoF();
					item[1] = String.valueOf(report.getTotalMiles());
					item[2] = report.getArgOile();
					item[3] = report.getUseOilValue();
					item[4] = report.getArgSpeed();
					data.add(item);
				}
				SimpleTableDataAdapter dataAdapter = new SimpleTableDataAdapter(MileageReport.this, data);
				dataAdapter.setTextSize(12);
				tableView.setDataAdapter(dataAdapter);
			} else {
				Toast.makeText(MileageReport.this, response.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mileage);

		/* 取得全局变量 */
		glob = (MyApplication) getApplicationContext();
		glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
		initView();

	}

	private void initView() {
		backBtn = (ImageButton) findViewById(R.id.backButton);
		backBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		progressDialog = new CustomProgressDialog(MileageReport.this);

		tableView = (TableView) findViewById(R.id.tableView);
		tableView.setColumnCount(5);
		SimpleTableHeaderAdapter headerAdapter = new SimpleTableHeaderAdapter(this, getResources().getStringArray(R.array.total_mileage_table_title));
		headerAdapter.setTextSize(14);
		tableView.setHeaderAdapter(headerAdapter);
		tableView.setHeaderBackgroundColor(android.R.color.white);

		String querySystemNoStr = getIntent().getStringExtra("querySystemNoStr");
		String startTime = getIntent().getStringExtra("startTime");
		String endTime = getIntent().getStringExtra("endTime");

		progressDialog.setMessage(getString(R.string.refreshing));
		progressDialog.show();
		getData(querySystemNoStr, startTime, endTime);
	}

	private void getData(final String systemNo, final String startTime, final String endTime) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				response = HttpRequestClient.vehicleMileage(systemNo, startTime, endTime);
				Message msg = new Message();
				if (response.getCode() == SProtocol.SUCCESS) {
					msg.what = 0;
				} else {
					msg.what = 1;
				}
				myHandler.sendMessage(msg);
			}
		}.start();
	}

	/** 返回按键 结束activity */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
			finish();
		return false;
	}
}