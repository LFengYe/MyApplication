package com.cn.wms_system.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.cn.wms_system.component.Constants;
import com.cn.wms_system.component.GetNowTime;
import com.cn.wms_system.component.TitleViewHolder;
import com.cn.wms_system.dialog.ChangePassword;
import com.cn.wms_system.fragment1.DetailFunFragment;
import com.cn.wms_system.fragment.MainInterface;
import com.cn.wms_system.fragment1.ListFunFragment;
import com.cn.wms_system.service.BootBroadcastReceiver;
import com.cn.wms_system_new.R;

public class MainFragment extends FragmentActivity {

	private BootBroadcastReceiver receiver;
	private TitleViewHolder titleHolder;
	private JSONArray authorityIDList;
	private JSONObject menuJson;
	private String authorityID;
	private String[] titles;
	private Bundle bundle;

	private int selectedFunIndex = 0;
	private static final int REQUEST_CODE = 1;

	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			authorityID = String.valueOf(8000 + 10 * selectedFunIndex + position + 1);

			if (null != authorityIDList && authorityIDList.contains(authorityID)) {//判断当前点击功能是否包含在用户权限列表中
				bundle.putString("title", titles[position]);
				bundle.putInt("selected_fun_index", selectedFunIndex);
				bundle.putInt("selected_child_fun_item", position);
				Intent intent = new Intent();
				
				if (selectedFunIndex <= 5 || selectedFunIndex == 7)//进入数据列表
					intent.setClass(getApplicationContext(), ListActivity.class);
				
				if (selectedFunIndex == 6) {
					if (position == 0) {//修改密码
						ChangePassword changePassword = new ChangePassword(MainFragment.this);
						changePassword.setTitle(R.string.modity_password);
						changePassword.show();
						return;
					}
					if (position == 1)//参数设置
						intent.setClass(getApplicationContext(), ParamsSettingActivity.class);
				}
				intent.putExtras(bundle);
				startActivity(intent);
			} else {
				//System.outHost.println("authorityID:" + authorityID);
				if (Build.VERSION.SDK_INT >= 23) {
					if(!Settings.canDrawOverlays(getApplicationContext())) {
						Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
						intent.setData(Uri.parse("package:" + getPackageName()));
						startActivityForResult(intent, REQUEST_CODE);
						//return;
					} else {
						Builder builder = new Builder(getBaseContext())
								.setTitle(R.string.no_primission_title)
								.setMessage(R.string.no_primission_message);
						Dialog dialog = builder.create();
						dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
						dialog.show();
					}
				} else {
					Builder builder = new Builder(getBaseContext())
							.setTitle(R.string.no_primission_title)
							.setMessage(R.string.no_primission_message);
					Dialog dialog = builder.create();
					dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					dialog.show();
				}
			}
		}
	};

	private ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
		}
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
//			notificationService = ((NotificationService.MyBinder)service).getService();
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		getWindow().setFlags(Constants.FLAG_HOMEKEY_DISPATCHED,
				Constants.FLAG_HOMEKEY_DISPATCHED);
		setContentView(R.layout.activity_main_fragment);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.layout_title_bar);
		
		initTitleComponents();
		setTitleComponents();
		initParams();

		FragmentManager manager = getFragmentManager();
		Fragment fragment = manager.findFragmentById(R.id.list_fun);
		ListFunFragment listFunFragment = (ListFunFragment) fragment;
		listFunFragment.setMenuJson(menuJson);
		listFunFragment.setCallback(new ListFunFragment.BtnClickCallback() {
			@Override
			public void btnClick(JSONObject object) {
				if (findViewById(R.id.detail_fun) != null) {
				}
			}
		});

		/**
		 * Check that the activity is using the layout version with the
		 * fragment_container FrameLayout
		 */
		if (findViewById(R.id.detail_fun) != null) {
			/**
			 * However, if we're being restored from a previous state, then we
			 * don't need to do anything and should return or else we could end
			 * up with overlapping fragments.
			 */
			if (savedInstanceState != null)
				return;
			MainInterface mainInterface = new MainInterface();

			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.add(R.id.detail_fun, mainInterface);
			// transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	@SuppressLint("NewApi")
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CODE) {
			if (Settings.canDrawOverlays(this)) {
				Toast.makeText(this, "授权成功!", Toast.LENGTH_LONG).show();
			}
		}
	}

	public void initTitleComponents() {
		titleHolder = new TitleViewHolder();
		titleHolder.backButton = (LinearLayout) findViewById(R.id.back);
		titleHolder.curSystemTime = (TextView) findViewById(R.id.cur_sys_time);
		titleHolder.titleTextView = (TextView) findViewById(R.id.title);
		titleHolder.refreshButton = (LinearLayout) findViewById(R.id.refresh);
		titleHolder.exitButton = (LinearLayout) findViewById(R.id.exit);
	}

	public void setTitleComponents() {
		titleHolder.backButton.setVisibility(View.INVISIBLE);
		titleHolder.curSystemTime.setText(getResources().getString(R.string.cur_time_promte)
				+ GetNowTime.getHour()
				+ ":"
				+ ((GetNowTime.getMinute() < 10) ? ("0" + GetNowTime
						.getMinute()) : GetNowTime.getMinute()));
		titleHolder.titleTextView.setText(R.string.main_title);

		/**
		 * 退出按钮参数设定
		 */
		titleHolder.exitButton.setVisibility(View.VISIBLE);
		titleHolder.exitButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		/**
		 * 隐藏刷新按钮
		 */
		titleHolder.refreshButton.setVisibility(View.GONE);
	}

	public void initParams() {
		bundle = getIntent().getExtras();
//		authorityIDList = bundle.getStringArrayList("authority_id_list");//获取用户权限列表
		authorityIDList = JSONObject.parseArray(bundle.getString("authority_id_list"));
		menuJson = JSONObject.parseObject(bundle.getString("menuJson"), Feature.OrderedField);
		//绑定Service
		Intent intent = new Intent("com.cn.service.NOTIFICATION_UPDATE");
		intent.setPackage("com.cn.wms_system_new");
		bindService(intent, connection, Context.BIND_AUTO_CREATE);
	}

	@Override
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// 注册时间更新广播接收者更新标题栏中的时间
		receiver = new BootBroadcastReceiver() {

			@Override
			public void updateTime() {
				titleHolder.curSystemTime.setText(getResources().getString(
						R.string.cur_time_promte)
						+ GetNowTime.getHour()
						+ ":"
						+ ((GetNowTime.getMinute() < 10) ? ("0" + GetNowTime
								.getMinute()) : GetNowTime.getMinute()));
			}
			@Override
			public void updateData() {
			}
		};
		IntentFilter filter = new IntentFilter(
				"android.intent.action.TIME_TICK");
		registerReceiver(receiver, filter);
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		unbindService(connection);
		super.onDestroy();
	}

	/*
	public void button1Click(View view) {
		if (findViewById(R.id.detail_fun) != null) {
			setSelectedFunIndex(1);
			bundle.putString("selected_fun_index_name", getResources().getString(R.string.stocking_manager));
			titles = getResources().getStringArray(R.array.stocking_plan_list);
			int[] images = { R.drawable.ic_stocking1, R.drawable.ic_stocking2,
					R.drawable.ic_stocking3 };

			DetailFunFragment detailFunFragment = DetailFunFragment
					.newInstance(titles, images, onItemClickListener);
			detailFunFragment.setSelectedFun(1);

			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.detail_fun, detailFunFragment);
			transaction.commit();
		}
	}

	public void button2Click(View view) {
		if (findViewById(R.id.detail_fun) != null) {
			setSelectedFunIndex(2);
			bundle.putString("selected_fun_index_name", getResources().getString(R.string.picking_manager));
			titles = getResources().getStringArray(R.array.picking_plan_list);
			int[] images = { R.drawable.ic_picking1, R.drawable.ic_pinking2,
					R.drawable.ic_picking3 };
			DetailFunFragment detailFunFragment = DetailFunFragment
					.newInstance(titles, images, onItemClickListener);
			detailFunFragment.setSelectedFun(2);

			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.detail_fun, detailFunFragment);
			transaction.commit();
		}
	}

	public void button3Click(View view) {
		if (findViewById(R.id.detail_fun) != null) {
			setSelectedFunIndex(3);
			bundle.putString("selected_fun_index_name", getResources().getString(R.string.distribution_manager));
			titles = getResources().getStringArray(
					R.array.distribution_plan_list);
			int[] images = { R.drawable.ic_distribution1,
					R.drawable.ic_distribution2, R.drawable.ic_distribution3 };
			DetailFunFragment detailFunFragment = DetailFunFragment
					.newInstance(titles, images, onItemClickListener);
			detailFunFragment.setSelectedFun(3);

			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.detail_fun, detailFunFragment);
			transaction.commit();
		}
	}

	public void button4Click(View view) {
		if (findViewById(R.id.detail_fun) != null) {
			setSelectedFunIndex(4);
			bundle.putString("selected_fun_index_name", getResources().getString(R.string.pick_return));
			titles = getResources().getStringArray(R.array.pick_return_list);
			int[] images = { R.drawable.ic_non_pro_pick,
					R.drawable.ic_return_goods,
					R.drawable.ic_send_check_out,
					R.drawable.ic_send_check_in,
					R.drawable.ic_rework_out,
					R.drawable.ic_rework_in,
					R.drawable.ic_return_storehouse,
					R.drawable.ic_out_finished};
			DetailFunFragment detailFunFragment = DetailFunFragment
					.newInstance(titles, images, onItemClickListener);
			detailFunFragment.setSelectedFun(4);

			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.detail_fun, detailFunFragment);
			transaction.commit();
		}
	}

	public void button5Click(View view) {
		if (findViewById(R.id.detail_fun) != null) {
			setSelectedFunIndex(5);
			bundle.putString("selected_fun_index_name", getResources().getString(R.string.stock_manager));
			titles = getResources().getStringArray(R.array.stock_manager_list);
			int[] images = { R.drawable.ic_inspection,
					R.drawable.ic_stock_inquiry,
					R.drawable.ic_stock_alarm,
					R.drawable.ic_out_finished,
					R.drawable.ic_rework_out,
					R.drawable.ic_rework_in,
					R.drawable.ic_return_goods,
					R.drawable.ic_return_storehouse};
			DetailFunFragment detailFunFragment = DetailFunFragment
					.newInstance(titles, images, onItemClickListener);
			detailFunFragment.setSelectedFun(5);

			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.detail_fun, detailFunFragment);
			transaction.commit();
		}
	}

	public void button6Click(View view) {
		if (findViewById(R.id.detail_fun) != null) {
			setSelectedFunIndex(6);
			bundle.putString("selected_fun_index_name", getResources().getString(R.string.system_setting));
			titles = getResources().getStringArray(R.array.system_setting_list);
			int[] images = { R.drawable.ic_user_manager,
					R.drawable.ic_system_setting };
			DetailFunFragment detailFunFragment = DetailFunFragment
					.newInstance(titles, images, onItemClickListener);
			detailFunFragment.setSelectedFun(6);
			
			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.detail_fun, detailFunFragment);
			// transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	public void button7Click(View view) {
		if (findViewById(R.id.detail_fun) != null) {
			setSelectedFunIndex(7);
			bundle.putString("selected_fun_index_name", getResources().getString(R.string.delivery_receipt));
			titles = getResources().getStringArray(R.array.delivery_receipt_list);
			int[] images = {
					R.drawable.ic_rework_in,
					R.drawable.ic_return_goods,
					R.drawable.ic_return_storehouse};
			DetailFunFragment detailFunFragment = DetailFunFragment
					.newInstance(titles, images, onItemClickListener);
			detailFunFragment.setSelectedFun(7);

			FragmentTransaction transaction = getSupportFragmentManager()
					.beginTransaction();
			transaction.replace(R.id.detail_fun, detailFunFragment);
			// transaction.addToBackStack(null);
			transaction.commit();
		}
	}

	public int getSelectedFunIndex() {
		return selectedFunIndex;
	}

	public void setSelectedFunIndex(int selectedFunIndex) {
		this.selectedFunIndex = selectedFunIndex;
	}
	*/
}
