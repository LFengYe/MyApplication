package com.cn.wetrack.activity;

import java.util.List;

import com.cn.wetrack.R;
import com.cn.wetrack.entity.MyMessage;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * 消息适配器
 */
@SuppressLint("UseSparseArrays")
public class MessageItemAdapter extends BaseAdapter {
	private LayoutInflater layoutInflater;
	private List<MyMessage> messageList;

	public MessageItemAdapter(Context context, List<MyMessage> applicationItemInfoList) {
		this.messageList = applicationItemInfoList;
		layoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return messageList.size();
	}

	@Override
	public Object getItem(int position) {
		return messageList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View view, ViewGroup viewGroup) {
		ViewHolder viewHolder;
		if (view == null) {
			view = layoutInflater.inflate(R.layout.message_item, null);
			viewHolder = new ViewHolder();
			viewHolder.messageText = (TextView) view.findViewById(R.id.messageText);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		viewHolder.messageText.setText(messageList.get(position).getContent());
		return view;
	}

	/* 视图 */
	public final class ViewHolder {
		public TextView messageText;
	}

}
