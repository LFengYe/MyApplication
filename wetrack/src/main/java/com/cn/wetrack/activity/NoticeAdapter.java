package com.cn.wetrack.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cn.wetrack.R;
import com.cn.wetrack.entity.Notice;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by fuyzh on 16/8/15.
 */
public class NoticeAdapter extends BaseAdapter {
    private List<Notice> data;
    private Context context;

    public List<Notice> getData() {
        return data;
    }

    public void setData(List<Notice> data) {
        this.data = data;
    }

    public NoticeAdapter(Context context, List<Notice> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        if (null != data)
            return data.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (null != data)
            return data.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = View.inflate(context, R.layout.item_notice, null);
            viewHolder = new ViewHolder();
            viewHolder.noticeTitle = (TextView) convertView.findViewById(R.id.notice_title);
            viewHolder.noticeDate = (TextView) convertView.findViewById(R.id.notice_date);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Notice notice = data.get(position);
        viewHolder.noticeTitle.setText(notice.getNoticeTitle());
        viewHolder.noticeDate.setText(notice.getNoticeData());
        return convertView;
    }

    class ViewHolder {
        private TextView noticeTitle;
        private TextView noticeDate;
    }
}
