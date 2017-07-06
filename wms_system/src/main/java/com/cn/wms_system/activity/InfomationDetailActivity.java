package com.cn.wms_system.activity;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.cn.wms_system.component.ClearEditText;
import com.cn.wms_system.component.Constants;
import com.cn.wms_system.component.ContentViewHolder;
import com.cn.wms_system.component.GetNowTime;
import com.cn.wms_system.component.SideBar;
import com.cn.wms_system.component.SideBar.OnTouchingLetterChangedListener;
import com.cn.wms_system.component.TableView;
import com.cn.wms_system.component.TitleViewHolder;
import com.cn.wms_system.component.WebOperate;
import com.cn.wms_system.dialog.InfoConfirmDialog;
import com.cn.wms_system.dialog.InformationDialog;
import com.cn.wms_system.service.BootBroadcastReceiver;
import com.cn.wms_system_new.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfomationDetailActivity extends Activity {

	private TitleViewHolder titleHolder;
	private ContentViewHolder contentHolder;
	private HorizontalScrollView horizontalScrollView;
	private SideBar sideBar;
	private TextView sideBarDialog;
	private ZoomControls zoomControls;

	private BootBroadcastReceiver receiver;
	/** 表格视图 */
	private TableView tableView;
	/** 表头 */
	private String[] titleValue;
	/** 表格数据 */
	private List<String[]> data;
	/** 过滤后的表格数据 */
	private List<String[]> filterData;
	/** 数据过滤标志 */
	private boolean filterFlag;
	/** 计划单号 */
	private String planOddNum;
	/** 计划类型 */
	private String planType;
	/** 当前界面标题 */
	private String title;
	/** web操作 */
	private WebOperate webOperate;
	/** 上传服务器参数列表 */
	private Map<String, String> params;
	/** webService函数名 */
	private String methodName;
	
	private Bundle bundle;
	/** 功能标记 */
	private String functionMark;
	private int selectedFunIndex;
	private int selectedFunChildItem;
	private int devitation;
	/**表格字体初始大小 */
	private int textSize = Constants.TEXTSIZE_INIT;
	/** 当前选中行 */
	private int selectedItem = -1;
	/** 是否进行操作(备货、领货、配送)标志位 */
	private boolean operateFlag = false;
	/** 本次操作(备货、领货、配送)完成后的剩余数量 */
	private int lavedNum;
	/** 本次操作(备货、领货、配送)操作记录的部品件号 */
	private String thePartNo = "";
	/** 本次操作(备货、领货、配送)操作记录的部品批次 */
	private String thePartBatch = "";

	/**
	 * 侧边栏快速查找条滑动时，根据选中的字符来查找数据
	 */
	private OnTouchingLetterChangedListener onTouchingLetterChangedListener = new OnTouchingLetterChangedListener() {
		@Override
		public void onTouchingLetterChanged(String s) {
			switch (selectedFunIndex) {
			case 1:
				break;
			case 2:
				break;
			default:
				break;
			}
		}
	};
	
	/**
	 * 搜索框文本发生改变时，根据文本内容过滤数据
	 */
	private TextWatcher watcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (s != null && s.length() > 3) {
				filterData = filterData(data, s.toString());
				tableView.refreshData(filterData);
				filterFlag = true;
				return;
			}
			tableView.refreshData(data);
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
		}
	}; 
	
	/**
	 * 点击事件
	 */
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (v.getId() == titleHolder.backButton.getId()) {
				finish();
			}
			
			if (v.getId() == titleHolder.refreshButton.getId()) {
				downloadData();
			}
		}
	};
	
	/**
	 * 进程间通信
	 */
	private Handler myHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			case Constants.GET_PLAN_LIST_MESSAGE:
				data.clear();
				data.addAll(progressData(Constants.replaceEnter(webOperate.getResult())));
				if (operateFlag) {
					if (selectedFunIndex == 1) {//判断本次备货是否成功
						if (lavedNum == 0 && Integer.valueOf(data.get(selectedItem)[7]) > lavedNum) {
							createDialog(getResources().getString(R.string.the_operate_failed_title), 
									getResources().getString(R.string.the_operate_failed_message));
						}
					}
					
					if (selectedFunIndex == 2 || selectedFunIndex == 3) {//判断本次领货、配送是否成功
						if (lavedNum == 0 && Integer.valueOf(data.get(selectedItem)[8]) > lavedNum) {
							createDialog(getResources().getString(R.string.the_operate_failed_title),
									getResources().getString(R.string.the_operate_failed_message));
						}
					}
					operateFlag = false;
				}
				Collections.sort(data, comparator);
				tableView.definedSetChanged();
				break;
			case Constants.GET_MESSAGE_IS_EMPTY:
				break;
			default:
				break;
			}
		};
	};
	
	/**
	 * 列表排序规则，将已完成明细置于底部
	 */
	private Comparator<String[]> comparator = new Comparator<String[]>() {
		
		@Override
		public int compare(String[] lhs, String[] rhs) {
			return lhs[lhs.length - 1].compareTo(rhs[rhs.length - 1]);
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		getWindow().setFlags(Constants.FLAG_HOMEKEY_DISPATCHED, Constants.FLAG_HOMEKEY_DISPATCHED);
		setContentView(R.layout.activity_info_list);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_title_bar);
		
		initComponents();
		initParams();
		setComponents();

		switch(selectedFunIndex) {
		case 1:
			functionMark = "备货管理";
			devitation = 1;
			titleValue = getResources().getStringArray(R.array.stock_plan_detail_table_header);
			bundle.putString("finished1_text", "备货数量");
			bundle.putBoolean("finished2_display", false);
			break;
		case 2:
			functionMark = "领货管理";
			devitation = 1;
			titleValue = getResources().getStringArray(R.array.pick_plan_detail_table_header);
			bundle.putString("finished1_text", "备货数量");
			bundle.putString("finished2_text", "领取数量");
			bundle.putBoolean("finished2_display", true);
			break;
		case 3:
			functionMark = "配送管理";
			devitation = 1;
			titleValue = getResources().getStringArray(R.array.distribution_plan_detail_table_header);
			bundle.putString("finished1_text", "领取数量");
			bundle.putString("finished2_text", "配送数量");
			bundle.putBoolean("finished2_display", true);
			break;
		case 4:
			devitation = 5;
			if (selectedFunChildItem == 0) {
				functionMark = "非生产领料";
				titleValue = getResources().getStringArray(R.array.non_pro_pick_detail_table_header);
			}
			if (selectedFunChildItem == 1) {
				functionMark = "部品退货";
				titleValue = getResources().getStringArray(R.array.return_detail_table_header);
			}
			if (selectedFunChildItem >= 2 && selectedFunChildItem <= 5) {
				functionMark = "部品退货";
				titleValue = getResources().getStringArray(R.array.return_rework_check_detail_table_header);
			}
			if (selectedFunChildItem == 6) {
				functionMark = "部品退货";
				titleValue = getResources().getStringArray(R.array.return_warehouse_detail_table_header);
			}
			break;
		case 5:
			break;
		case 6:
			break;
		default:
			break;
		}
		
		//设置tableView各属性
		data = new ArrayList<>();
		tableView = new TableView(getApplicationContext(), titleValue, data, false);
		tableView.setTitleTextSize(textSize);
		tableView.setContentTextSize(textSize);
		tableView.setTitleBackgroundColor(getResources().getColor(R.color.the_table_title_bg_color));
		tableView.setItemBackgroundColor(getResources().getColor(R.color.the_table_content_bg_color1),
				getResources().getColor(R.color.the_table_content_bg_color2));
		tableView.definedSetChanged();
		tableView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				
				//判断是使用表格全部数据还是过滤后的数据
				List<String[]> tempData = (filterFlag ? filterData : data);
				selectedItem = position;
				
				//备货明细中，剩余数量为0，则返回
				if (selectedFunIndex == 1 && tempData.get(position)[7].compareTo("0") == 0) {
					return ;
				}
				//领货、配送明细中，剩余数量为0，则返回
				if ((selectedFunIndex == 2 || selectedFunIndex == 3) && tempData.get(position)[8].compareTo("0") == 0) {
					return ;
				}
				
				Intent intent = new Intent();
				
				/**
				 * 根据标题名称设置各字段的值
				 */
				for (int i = 0; i < titleValue.length; i++) {
					bundle.putString(titleValue[i], tempData.get(position)[i]);
					//记录本次操作的部品件号和部品批次
					if (titleValue[i].compareTo("部品件号") == 0)
						thePartNo = tempData.get(position)[i];
					if (titleValue[i].compareTo("部品批次") == 0)
						thePartBatch = tempData.get(position)[i];
				}

				/**
				 * 设置不包含在表头中的字段的值
				 */
				if (functionMark.equalsIgnoreCase("备货管理")) {
					bundle.putString("换装数量", tempData.get(position)[tempData.get(position).length - 1]);
					bundle.putString("最大数量", tempData.get(position)[7]);
				}
				if (functionMark.equalsIgnoreCase("领货管理") || functionMark.equalsIgnoreCase("配送管理")) {
//					bundle.putString("部品批次", tempData.get(position)[tempData.get(position).length - 2]);
//					thePartBatch = tempData.get(position)[tempData.get(position).length - 2];
					bundle.putString("最大数量", String.valueOf(
							Integer.valueOf(tempData.get(position)[6]) - Integer.valueOf(tempData.get(position)[7])));
				}
				intent.putExtras(bundle);
				
				/**
				 * 点击item弹出信息对话框或是确认对话框
				 */
				if (selectedFunIndex <= 3)
					intent.setClass(InfomationDetailActivity.this,InformationDialog.class);
				if (selectedFunIndex == 4 || selectedFunIndex == 5)
					intent.setClass(InfomationDetailActivity.this,InfoConfirmDialog.class);
				
				startActivityForResult(intent, 0);
			}
		});
		
		//将tableView添加到滚动视图
		horizontalScrollView = (HorizontalScrollView) findViewById(R.id.scrollview);
		horizontalScrollView.removeAllViews();
		horizontalScrollView.addView(tableView);
		
		//设置sideBar各属性
		sideBar = (SideBar) findViewById(R.id.sidebar);
		sideBarDialog = (TextView) findViewById(R.id.sidebar_dialog);
		sideBar.setTextView(sideBarDialog);
		sideBar.setOnTouchingLetterChangedListener(onTouchingLetterChangedListener);
		
		zoomControls = (ZoomControls) findViewById(R.id.zoom_controls);
		//　setOnZoomInClickListener()　-　响应单击放大按钮的事件
		zoomControls.setOnZoomInClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//点击放大按钮之前，若textSize小于等于最小文本值，则使能缩小按钮
				if (textSize <= Constants.TEXTSIZE_MIN)
					zoomControls.setIsZoomOutEnabled(true);
				//若textSize小于最大文本值，则放大文本
				if (textSize < Constants.TEXTSIZE_MAX)
					textSize += 2;
				//若textSize大于等于最大文本值，则禁用放大按钮
				if (textSize >= Constants.TEXTSIZE_MAX)
					zoomControls.setIsZoomInEnabled(false);
				//重新设置表格文本字体大小，刷新表格
				tableView.setTitleTextSize(textSize);
				tableView.setContentTextSize(textSize);
				tableView.definedSetChanged();
			}
		});
		//　setOnZoomOutClickListener()　-　响应单击缩小按钮的事件
		zoomControls.setOnZoomOutClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//点击放大按钮之前，若textSize大于等于最大文本值，则使能放大按钮
				if (textSize >= Constants.TEXTSIZE_MAX)
					zoomControls.setIsZoomInEnabled(true);
				//若textSize大于最小文本值，则缩小文本
				if (textSize > Constants.TEXTSIZE_MIN)
					textSize -= 2;
				//若textSize小于等于最小文本值，则禁用缩小按钮
				if (textSize <= Constants.TEXTSIZE_MIN)
					zoomControls.setIsZoomOutEnabled(false);
				//重新设置表格文本字体大小，刷新表格
				tableView.setTitleTextSize(textSize);
				tableView.setContentTextSize(textSize);
				tableView.definedSetChanged();
			}
		});
	}
	
	/**
	 * 初始化UI组件
	 */
	public void initComponents() {
		titleHolder = new TitleViewHolder();
		titleHolder.backButton = (LinearLayout) findViewById(R.id.back);
		titleHolder.curSystemTime = (TextView) findViewById(R.id.cur_sys_time);
		titleHolder.titleTextView = (TextView) findViewById(R.id.title);
		titleHolder.refreshButton = (LinearLayout) findViewById(R.id.refresh);
		
		contentHolder = new ContentViewHolder();
		contentHolder.textView1 = (TextView) findViewById(R.id.textview1);
		contentHolder.editText1 = (EditText) findViewById(R.id.edittext1);
		contentHolder.textView2 = (TextView) findViewById(R.id.textview2);
		contentHolder.editText2 = (EditText) findViewById(R.id.edittext2);
		contentHolder.textView3 = (TextView) findViewById(R.id.textview3);
		contentHolder.editText3 = (ClearEditText) findViewById(R.id.edittext3);
		contentHolder.editText3.addTextChangedListener(watcher);
	}
	
	/**
	 * 设置UI组件属性
	 */
	public void setComponents() {
		titleHolder.backButton.setOnClickListener(onClickListener);
		titleHolder.curSystemTime.setText(getResources().getString(R.string.cur_time_promte)
				+ GetNowTime.getHour() + ":" +
				((GetNowTime.getMinute() < 10) ? ("0" + GetNowTime.getMinute()) : GetNowTime.getMinute()));
		titleHolder.titleTextView.setText(title);
		titleHolder.refreshButton.setOnClickListener(onClickListener);
		
		contentHolder.textView1.setText(R.string.plan_odd_num);
		contentHolder.editText1.setText(planOddNum);
		contentHolder.editText1.setFocusable(false);
		
		contentHolder.textView2.setText(R.string.plan_type);
		contentHolder.editText2.setText(planType);
		contentHolder.editText2.setFocusable(false);
		
		contentHolder.textView3.setText(R.string.quick_search);
		contentHolder.editText3.setHint(R.string.detail_search_promte);
	}

	/**
	 * 初始化参数
	 */
	public void initParams() {
		bundle = getIntent().getExtras();
		planType = bundle.getString("plan_type");
		planOddNum = bundle.getString("plan_odd_num");
		title = bundle.getString("title");
		selectedFunIndex = bundle.getInt("selected_fun_index", 1);
		selectedFunChildItem = bundle.getInt("selected_child_fun_item", 1);
		params = new HashMap<String, String>();
		webOperate = new WebOperate(myHandler);
		
	}

	/**
	 * 向服务器发送请求
	 */
	public void downloadData() {
		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				methodName = "send";
				if (selectedFunIndex <= 3)
					params.put("s", "{" + (selectedFunIndex + devitation) + ";" + planOddNum + ";}");
				if (selectedFunIndex == 4 || selectedFunIndex == 5)
					params.put("s", "{" + (selectedFunIndex + selectedFunChildItem * 3 + devitation) + ";" + planOddNum + ";}");
				webOperate.Request(methodName, params);
			}
		});
		thread.start();
	}
	
	
	/**
	 * 处理从服务器返回的数据
	 * @param data
	 * @return
	 */
	public ArrayList<String[]> progressData(String data) {
		ArrayList<String[]> result = new ArrayList<String[]>();
		String[] items = data.substring(1, data.length() - 1).split("\\},\\{");
		for (int i = 0; i < items.length; i++) {
			/**
			 * 直接对字符串使用分隔符进行分割
			 */
			String[] temp = items[i].split(";");
			
			//设定配送地址(需要修改)
//			if (selectedFunIndex == 3)
//				temp[temp.length - 3] = "一工厂";
			if (selectedFunIndex == 1) {
				//根据部品编号和部品批次，确定所选中行
				if (temp[3].compareTo(thePartNo) == 0 && temp[temp.length - 4].compareTo(thePartBatch) == 0) {
//					Log.e("thePartNo", thePartNo);
//					Log.e("thePartBatch", thePartBatch);
					selectedItem = i;
				}
				//判断该明细是否完成
				if (Integer.valueOf(temp[7]) == 0) {
					temp[temp.length - 1] = "1";
				}
			}
			
			if (selectedFunIndex == 2 || selectedFunIndex == 3) {
				//根据部品编号和部品批次，确定所选中行
				if (temp[3].compareTo(thePartNo) == 0 && temp[temp.length - 3].compareTo(thePartBatch) == 0) {
//					Log.e("thePartNo", thePartNo);
//					Log.e("thePartBatch", thePartBatch);
					selectedItem = i;
				}
				//判断该明细是否完成
				if (Integer.valueOf(temp[8]) == 0) {
					temp[temp.length - 1] = "1";
				}
			}
			//添加空字符串，为确认时填写确认时间留下字段空间
			if (selectedFunIndex == 4 || selectedFunIndex == 5)
				temp = Constants.addToStringArray(temp, "", temp.length - 2);
			
			result.add(temp);
		}
		return result;
	}
	
	/**
	 * 将String类型的list列表转换成为字符串数组
	 * @param data
	 * @return
	 */
	public String[] listToArray(ArrayList<String> data) {
		String[] result = new String[data.size()];
		for (int i = 0; i < data.size(); i++) {
			result[i] = data.get(i);
		}
		return result;
	}
	
	/**
	 * 过滤数据，过滤出data中包含s的数据
	 * @param data
	 * @param s
	 * @return
	 */
	public List<String[]> filterData(List<String[]> data, String s) {
		List<String[]> result = new ArrayList<String[]>();
		for (String[] item : data) {
			for (String temp : item) {
				if (temp.contains(s)) {
					result.add(item);
					break;
				}
			}
		}
		return result;
	}
	
	public void createDialog(String title, String message) {
		//弹出提示对话框
		Builder builder = new Builder(getBaseContext())
				.setTitle(title).setMessage(message)
				.setPositiveButton(R.string.setting_confirm, null);
		Dialog dialog = builder.create();
		dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		dialog.show();
	}
	
	@Override
	protected void onResume() {
		downloadData();
		/**
		 * 注册广播接受者，接受系统发送的时间更改广播（一分钟发送一次）
		 */
		receiver = new BootBroadcastReceiver() {
			@Override
			public void updateTime() {
				titleHolder.curSystemTime.setText(getResources().getString(R.string.cur_time_promte)
						+ GetNowTime.getHour() + ":"+ ((GetNowTime.getMinute() < 10) ? ("0" + GetNowTime
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
	protected void onPause() {
		unregisterReceiver(receiver);
		super.onPause();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data != null) {
			Bundle bundle = data.getExtras();
			if (resultCode == RESULT_OK) {
				//判断是使用表格全部数据还是过滤后的数据
				List<String[]> tempData = (filterFlag ? filterData : this.data);
				
				if (selectedItem != -1) {
					// 回写本次备货完成数量
					if (selectedFunIndex == 1) {
						tempData.get(selectedItem)[6] = 
								String.valueOf(Integer.valueOf(tempData.get(selectedItem)[6]) + bundle.getInt("本次完成数量", 0));
						lavedNum = Integer.valueOf(tempData.get(selectedItem)[7]) - bundle.getInt("本次完成数量", 0);
						tempData.get(selectedItem)[7] = String.valueOf(lavedNum);
						operateFlag = true;
					}
					// 回写本次领货、配送完成数量
					if (selectedFunIndex == 2 || selectedFunIndex == 3) {
						tempData.get(selectedItem)[7] = 
								String.valueOf(Integer.valueOf(tempData.get(selectedItem)[7]) + bundle.getInt("本次完成数量", 0));
						lavedNum = Integer.valueOf(tempData.get(selectedItem)[8]) - bundle.getInt("本次完成数量", 0);
//						Log.e("剩余数量", String.valueOf(lavedNum));
						tempData.get(selectedItem)[8] = String.valueOf(lavedNum);
						operateFlag = true;
					}
				}
				tableView.definedSetChanged();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * @return the titleVale
	 */
	public String[] getTitleVale() {
		return titleValue;
	}

	/**
	 * @param titleVale the titleVale to set
	 */
	public void setTitleVale(String[] titleVale) {
		this.titleValue = titleVale;
	}

	/**
	 * @return the planOddNum
	 */
	public String getPlanOddNum() {
		return planOddNum;
	}

	/**
	 * @param planOddNum the planOddNum to set
	 */
	public void setPlanOddNum(String planOddNum) {
		this.planOddNum = planOddNum;
	}

	/**
	 * @return the planType
	 */
	public String getPlanType() {
		return planType;
	}

	/**
	 * @param planType the planType to set
	 */
	public void setPlanType(String planType) {
		this.planType = planType;
	}

	/**
	 * @return the data
	 */
	public List<String[]> getData() {
		return data;
	}
	
	/**
	 * @param data the data to set
	 */
	public void setData(List<String[]> data) {
		this.data = data;
	}
}
