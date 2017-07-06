package com.cn.wms_system.fragment;

import java.util.HashMap;
import java.util.Map;

import com.cn.wms_system.component.Constants;
import com.cn.wms_system.component.WebOperate;
import com.cn.wms_system_new.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class DetailFunFragment extends Fragment {

	/** 详细功能列表功能名称 */
	private String[] titles;
	/** 详细功能列表功能图标 */
	private int[] images;
	/** 详细功能列表各功能未完成计划单数 */
	private int[] unFinishedNums;
	/** 选中的功能号 */
	private int selectedFun;
	/** 选中功能获取未完成计划单数已读取次数 */
	private static int selectedFunReadTimes = -1;
	
	private GridView gridView;
	private WebOperate webOperate;
	private OnItemClickListener onItemClickListener;
	private static DetailFunFragment detailFunFragment;
	
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch(msg.what) {
			case Constants.GET_PLAN_LIST_MESSAGE:
				//获取从服务器返回数据，并对数据进行分离
				String result = webOperate.getResult();
				String[] items = result.substring(1, result.length() - 1)
						.split(";");
				//获取备货管理、领货管理、配送管理计划单数
				if (selectedFun <= 3) {
					if (getActivity() != null) {
						unFinishedNums = new int[items.length];

						for (int i = 0; i < items.length; i++) {
							unFinishedNums[i] = Integer.valueOf(items[i]);
						}
						if (getActivity() != null)
							gridView.setAdapter(new FunPicAdapter(titles,
									images, unFinishedNums, getActivity()));
					}
				}
				//获取库房作业管理计划单数（非生产领料、部品退货、送检出库、送检返还、返修出库、返修入库、部品退库）
				if (selectedFun == 4) {
					if (selectedFunReadTimes == 0) {//获取非生产领料计划单数
						unFinishedNums = new int[7];
						//unFinishedNums[0] = unFinishedNums[6] = unFinishedNums[7] = unFinishedNums[8] = 0;
					}
					if (getActivity() != null) {
						if (selectedFunReadTimes != -1)
							unFinishedNums[selectedFunReadTimes] = Integer.valueOf(items[0]);

						if (selectedFunReadTimes == 6) {// 获取部品退库计划单数
							gridView.setAdapter(new FunPicAdapter(titles,
									images, unFinishedNums, getActivity()));
							selectedFunReadTimes = -1;
							break;
						}

						// 发送下一功能未完成计划单数的请求（部品退货、送检出库、送检返还、返修出库、返修入库）
						Map<String, String> param = new HashMap<String, String>();
						param.put("s", "{" + (38 + selectedFunReadTimes) + ";}");
						webOperate.Request("send", param);
						selectedFunReadTimes += 1;
					}
				}
				break;
			default:
				break;
			}
		};
	};
	
	//返回主界面
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if (getActivity().findViewById(R.id.detail_fun) != null) {
			MainInterface mainInterface = new MainInterface();
			getFragmentManager().beginTransaction().replace(R.id.detail_fun, mainInterface).commit();
			}
		}
	};
	
	/**
	 * 获取对象单例，在Fragment重绘时（如屏幕翻转）保存参数
	 * @param titles
	 * @param images
	 * @param onItemClickListener
	 * @return
	 */
	public static DetailFunFragment newInstance(String[] titles, int[] images,
			OnItemClickListener onItemClickListener) {
		detailFunFragment = new DetailFunFragment();
		
		Bundle args = new Bundle();
		args.putStringArray("titles", titles);
		args.putIntArray("images", images);
		detailFunFragment.setArguments(args);
		detailFunFragment.setOnItemClickListener(onItemClickListener);
		
		return detailFunFragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_detail_fun, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		//获取保存的参数,恢复参数
		Bundle args = getArguments();
		titles = args.getStringArray("titles");
		images = args.getIntArray("images");
		detailFunFragment.setOnItemClickListener(onItemClickListener);
		
		TextView textView = (TextView) getActivity().findViewById(R.id.back_to_main);
		textView.setOnClickListener(onClickListener);
		
		webOperate = new WebOperate(handler);
		
		gridView = (GridView) getActivity().findViewById(R.id.gridview);
		gridView.setNumColumns(3);
		//???(updateNums数据为空，需要从网络获取，在Adapter中用到改变量时使用的定值)
		if (getActivity() != null)
			gridView.setAdapter(new FunPicAdapter(titles, images, null, getActivity()));
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				if (onItemClickListener != null) {
					onItemClickListener.onItemClick(parent, view, position, id);
				}
			}
		});
		super.onActivityCreated(savedInstanceState);
	}
	
	@Override
	public void onResume() {
		//downloadData();
		super.onResume();
	}
	
	/** 
	 * 下载数据
	 */
	public void downloadData() {
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				System.out.println("selectedFun:" + selectedFun);
				switch (selectedFun) {
				case 1://获取备货未完成的计划单数
					SharedPreferences preferences = getActivity().getSharedPreferences("system_params", Context.MODE_PRIVATE);
					params.put("s", "{34;"+ preferences.getString("username", "").split(",")[0] + ";}");
					break;
				case 2://获取领货未完成的计划单数
					params.put("s", "{35;}");
					break;
				case 3://获取配送未完成的计划单数
					params.put("s", "{36;}");
					break;
				case 4://获取库房作业管理未完成的计划单数
					params.put("s", "{37;}");
					selectedFunReadTimes = 0;
					break;
				case 5://报表查询无计划单数
					return;
				case 6://交接单无计划单数
					return;
				case 7://系统设置无计划单数
					return;
				default:
					break;
				}
				webOperate.Request("send", params);
			}
		});
		thread.start();
	}
	
	/**
	 * @return the titles
	 */
	public String[] getTitles() {
		return titles;
	}

	/**
	 * @param titles the titles to set
	 */
	public void setTitles(String[] titles) {
		this.titles = titles;
	}

	/**
	 * @return the images
	 */
	public int[] getImages() {
		return images;
	}

	/**
	 * @param images the images to set
	 */
	public void setImages(int[] images) {
		this.images = images;
	}

	/**
	 * @return the onItemClickListener
	 */
	public OnItemClickListener getOnItemClickListener() {
		return onItemClickListener;
	}

	/**
	 * @param onItemClickListener the onItemClickListener to set
	 */
	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.onItemClickListener = onItemClickListener;
	}

	/**
	 * @return the updateNums
	 */
	public int[] getUpdateNums() {
		return unFinishedNums;
	}

	/**
	 * @param updateNums the updateNums to set
	 */
	public void setUpdateNums(int[] updateNums) {
		this.unFinishedNums = updateNums;
	}

	/**
	 * @return the selectedFun
	 */
	public int getSelectedFun() {
		return selectedFun;
	}

	/**
	 * @param selectedFun the selectedFun to set
	 */
	public void setSelectedFun(int selectedFun) {
		this.selectedFun = selectedFun;
	}
}
