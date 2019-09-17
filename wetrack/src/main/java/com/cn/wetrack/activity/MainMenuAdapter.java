package com.cn.wetrack.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cn.wetrack.R;

/**
 * Created by LFeng on 2017/8/1.
 */

public class MainMenuAdapter extends BaseAdapter {

    //private ArrayList<HashMap<String, Object>> menuLists;// 菜单
    private String menuStr[];// 菜单
    private Integer[] menuIcon;// 菜单图标
    private Context context;
    private int alarmNum;
    private int noticeNum;

    public MainMenuAdapter(String[] menuStr, Integer[] menuIcon, Context context) {
        this.menuStr = menuStr;
        this.menuIcon = menuIcon;
        this.context = context;
    }

    @Override
    public int getCount() {
        return menuStr.length;
    }

    @Override
    public Object getItem(int position) {
        return menuStr[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.menuitem, null);
            holder.itemImage = (ImageView) convertView.findViewById(R.id.imageView_ItemImage);
            holder.itemText = (TextView) convertView.findViewById(R.id.textView_ItemText);
            holder.itemNum = (TextView) convertView.findViewById(R.id.textView_itemNum);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.itemImage.setImageResource(menuIcon[position]);
        holder.itemText.setText(menuStr[position]);

        if (menuStr[position].compareTo(context.getString(R.string.main_btn_title_alarm_infomation)) == 0) {
            if (alarmNum > 0) {
                holder.itemNum.setText(String.valueOf(alarmNum));
                holder.itemNum.setVisibility(View.VISIBLE);
            } else {
                holder.itemNum.setVisibility(View.GONE);
            }
        }

        if (menuStr[position].compareTo(context.getString(R.string.main_btn_title_official_news)) == 0) {
            if (noticeNum > 0) {
                holder.itemNum.setText(String.valueOf(noticeNum));
                holder.itemNum.setVisibility(View.VISIBLE);
            } else {
                holder.itemNum.setVisibility(View.GONE);
            }
        }
        return convertView;
    }

    public void setNoticeNum(int noticeNum) {
        this.noticeNum = noticeNum;
    }

    public void setAlarmNum(int alarmNum) {
        this.alarmNum = alarmNum;
    }

    class ViewHolder {
        ImageView itemImage;
        TextView itemText;
        TextView itemNum;
    }
}
