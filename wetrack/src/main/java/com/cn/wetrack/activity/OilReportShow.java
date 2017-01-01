package com.cn.wetrack.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.cn.wetrack.R;
import com.cn.wetrack.activity.baidu.OilDetailInfoBaidu;
import com.cn.wetrack.entity.MileageOilReport;
import com.cn.wetrack.entity.SResponse;
import com.cn.wetrack.util.AppUtils;
import com.cn.wetrack.util.HttpRequestClient;
import com.cn.wetrack.util.SProtocol;
import com.cn.wetrack.widgets.CustomProgressDialog;

import java.util.ArrayList;
import java.util.List;

import de.codecrafters.tableview.TableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;

public class OilReportShow extends Activity {

	SWApplication glob;// 全局控制类
	private ImageButton backBtn;// 返回
	private TableView tableView;
	private List<String[]> data;
	private SResponse response;
	private CustomProgressDialog progressDialog;
	private List<MileageOilReport> resData;

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			progressDialog.dismiss();
			if (msg.what == 0) {
				resData = (List<MileageOilReport>) response.getResult();
				data = new ArrayList<>();
				for (MileageOilReport report: resData) {
					String[] item = new String[6];
					item[0] = report.getVehNoF() + "#" + ((report.getAddOilTimes().equals("0")) ? ("1") : (report.getAddOilTimes()));
					item[1] = (null == report.getAddOilTime()) ? ("") : (report.getAddOilTime().substring(0, 15));
					item[2] = report.getAddOilValue();
					item[3] = report.getAddOilTimes();
					item[4] = report.getLouOilValue();
					if (report.getAddOilAddress() != null && report.getAddOilAddress().length() > 0)
                        item[5] = report.getAddOilAddress().replace("|", "\n");
                    else item[5] = report.getAddOilAddress();
					data.add(item);
				}
				SimpleTableDataAdapter dataAdapter = new SimpleTableDataAdapter(OilReportShow.this, data);
				dataAdapter.setTextSize(12);
				tableView.setDataAdapter(dataAdapter);
			} else {
				Toast.makeText(OilReportShow.this, response.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_oil_report);
		/* 取得全局变量 */
		glob = (SWApplication) getApplicationContext();
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

		progressDialog = new CustomProgressDialog(OilReportShow.this);

		tableView = (TableView) findViewById(R.id.tableView);
		tableView.setColumnCount(6);
        tableView.setColumnWeight(0, 2);
        tableView.setColumnWeight(1, 2);
        tableView.setColumnWeight(2, 2);
        tableView.setColumnWeight(3, 3);
        tableView.setColumnWeight(4, 3);
        tableView.setColumnWeight(5, 3);
        tableView.setPadding(0, 5, 0, 5);
		SimpleTableHeaderAdapter headerAdapter = new SimpleTableHeaderAdapter(this, getResources().getStringArray(R.array.fuel_report_table_title));
		headerAdapter.setTextSize(14);
        tableView.setHeaderAdapter(headerAdapter);
		tableView.addDataClickListener(new TableDataClickListener() {
			@Override
			public void onDataClicked(int rowIndex, Object clickedData) {
				MileageOilReport reportItem = resData.get(rowIndex);
				Bundle bundle = new Bundle();
				bundle.putParcelable("oilReportItem", reportItem);
				bundle.putString("type", "oil");
				Intent intent = new Intent();
				intent.putExtras(bundle);
				if (glob.mapType == 0) {
					if (AppUtils.IsGooglePlayAvailable(OilReportShow.this)) {
						intent.setClass(OilReportShow.this, OilDetailInfo.class);
					}
				}
				if (glob.mapType == 1) {
					intent.setClass(OilReportShow.this, OilDetailInfoBaidu.class);
				}
				startActivity(intent);
			}
		});

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
				response = HttpRequestClient.getOilReport(systemNo, startTime, endTime);
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
}
