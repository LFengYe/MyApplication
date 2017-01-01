package com.cn.carigps.activity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cn.carigps.R;
import com.cn.carigps.entity.ServiceExpired;

import java.util.List;

/**
 * Created by LFeng on 16/9/25.
 */
public class ExpiredListAdapter extends BaseAdapter {

    private Context context;
    private List<ServiceExpired> expiredList;

    public ExpiredListAdapter(Context context, List<ServiceExpired> expiredList) {
        this.context = context;
        this.expiredList = expiredList;
    }

    @Override
    public int getCount() {
        if (expiredList != null)
            return expiredList.size();
        return 0;
    }

    @Override
    public Object getItem(int position) {
        if (expiredList != null)
            return expiredList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.expired_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.listNo = (TextView) convertView.findViewById(R.id.expired_item_no);
            viewHolder.carNo = (TextView) convertView.findViewById(R.id.expired_item_carNo);
            viewHolder.expiredTime = (TextView) convertView.findViewById(R.id.expired_item_time);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ServiceExpired item = expiredList.get(position);
        viewHolder.listNo.setText((position + 1) + "");
        viewHolder.carNo.setText(item.getVehNoF());
        viewHolder.expiredTime.setText(item.getRunoutTime().substring(0, item.getRunoutTime().indexOf(" ")));

        return convertView;
    }

    class ViewHolder {
        TextView listNo;
        TextView carNo;
        TextView expiredTime;
    }
}
