package com.cxgps.vehicle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cxgps.vehicle.R;
import com.cxgps.vehicle.bean.NavigationBean;

import java.util.ArrayList;

/**
 * Created by taosong on 16/7/27.
 */
public class NavigationAdapter extends BaseAdapter {

    private ArrayList<NavigationBean> mDatas;

    private Context mContext;


    public NavigationAdapter(Context context, ArrayList<NavigationBean> data){

        this.mContext = context;
        this.mDatas = data;
    }


    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHolder  holder = null ;

        if (convertView == null ){

            convertView = LayoutInflater.from(mContext).inflate(R.layout.view_address_item,null);
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.item_title);
            holder.desc = (TextView) convertView.findViewById(R.id.item_desc);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        NavigationBean bean = mDatas.get(position);

        holder.desc.setText(bean.getTitleDesc());
        holder.name.setText(bean.getTitleName());

        convertView.setTag(R.id.item_title,bean);
        return convertView;
    }

    private class ViewHolder{

        private TextView name;

        private TextView desc;




    }
}
