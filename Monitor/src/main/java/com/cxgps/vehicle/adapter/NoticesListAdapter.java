package com.cxgps.vehicle.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxgps.vehicle.R;
import com.cxgps.vehicle.bean.NoticeInfoBean;
import com.cxgps.vehicle.utils.DateUtils;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by taosong on 16/8/1.
 */
public class NoticesListAdapter extends BaseAdapter {


    private Context context;

    private ArrayList<NoticeInfoBean> mData;



    public NoticesListAdapter(Context context, ArrayList<NoticeInfoBean> data){
        this.context = context;
        this.mData = data;
    }


    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        ViewHold vh = null;
        if (convertView == null ) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_noties_cell, null);
            vh = new ViewHold(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHold) convertView.getTag();
        }

        NoticeInfoBean infoBean = mData.get(position);



        StringBuffer buffer = new StringBuffer(infoBean.getAlarmType());



        buffer.append("             ").append(DateUtils.getTimestampString(DateUtils.string2Date(infoBean.getTime(),"yyyy-MM-dd HH:dd:ss")));


        vh.des.setText(buffer.toString());
        vh.name.setText(infoBean.getVehNoF());


       int  numberColor = Color.BLACK;
       int numberIcon = R.mipmap.icon_notice;

        vh.name.setTextColor(numberColor);

        vh.icons.setImageBitmap(BitmapFactory.decodeResource(parent.getContext().getResources(), numberIcon));

        convertView.setTag(R.id.tv_title, infoBean);

        return convertView;
    }

    static class ViewHold {
        @Bind(R.id.tv_title)
        TextView name;
        @Bind(R.id.tv_software_des)
        TextView des;


        @Bind(R.id.ivNews)
        ImageView icons;

        public ViewHold(View view) {
            ButterKnife.bind(this, view);
        }
    }


    public void notifyDataSetChanged(ArrayList<NoticeInfoBean> data) {
        notifyDataSetChanged();
        this.mData = data;
    }
}
