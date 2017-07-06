package com.cn.wms_system.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;


import com.cn.wms_system_new.R;

import java.util.ArrayList;

public class FunPicAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<FunctionItem> itemList;

	public FunPicAdapter(String[] titles, int[] images, int[] unFinishedNums, Context context) {
		super();
		itemList = new ArrayList<FunctionItem>();
		inflater = LayoutInflater.from(context);
		for (int i = 0; i < images.length; i++) {
			FunctionItem funItem = null;
			if (unFinishedNums != null && unFinishedNums.length >= images.length)
				funItem= new FunctionItem(titles[i], images[i], unFinishedNums[i]);
			else
				funItem= new FunctionItem(titles[i], images[i], 0);
			itemList.add(funItem);
		}
	}
	
	@Override
	public int getCount() {
		if (itemList != null)
			return itemList.size();
		return 0;
	}

	@Override
	public Object getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.layout_fun_item, null);
			viewHolder = new ViewHolder();
			viewHolder.title = (TextView) convertView.findViewById(R.id.item_title);
			viewHolder.image = (ImageView) convertView.findViewById(R.id.item_image);
			viewHolder.unFinishedPrompt = (FrameLayout) convertView.findViewById(R.id.update_prompt);
			viewHolder.unFinishedNum = (TextView) convertView.findViewById(R.id.update_num);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		FunctionItem item = itemList.get(position);
		if (item != null) {
			viewHolder.title.setText(item.getName());
			viewHolder.image.setImageResource(item.getImageId());
			if (item.getUnFinishedNum() != 0) {
				viewHolder.unFinishedPrompt.setVisibility(View.VISIBLE);
				viewHolder.unFinishedNum.setText(String.valueOf(item.getUnFinishedNum()));
			}
			if (item.getUnFinishedNum() == 0)
				viewHolder.unFinishedPrompt.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

}

class ViewHolder {
	/** 子功能标题 */
	public TextView title;
	/** 子功能图片 */
	public ImageView image;
	/** 子功能未完成计划提示  */
	public FrameLayout unFinishedPrompt;
	/** 子功能未完成计划数量 */
	public TextView unFinishedNum;
}
