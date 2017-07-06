package com.cxgps.vehicle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.bean.SelectItemBean;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by taosong on 16/12/27.
 */

public class SimpleDataAdapter extends BaseAdapter {


    private List<SelectItemBean> mData;

    private Context mContext;

    private int mode = -1;

    public SimpleDataAdapter(Context context, List<SelectItemBean> temp, int tempMode){

        this.mData = temp;

        this.mContext = context;

        this.mode = tempMode;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHold vh = null;
        if (convertView == null ) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_text_cell, null);
            vh = new ViewHold(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHold) convertView.getTag();
        }

        SelectItemBean content = mData.get(position);


        vh.itemTitle.setText(content.getItemName());

        if (mode == 0){

         String key =     AppContext.getLanguageWithKey();

            if (key.equals(content.getItemKey()))
            {

                vh.itemCheck.setVisibility(View.VISIBLE);
            }else {
                vh.itemCheck.setVisibility(View.GONE);

            }
        }else if(mode == 1){

            String key =     AppContext.getMapWithKey();

            if (key.equals(content.getItemKey()))
            {
                vh.itemCheck.setVisibility(View.VISIBLE);
            }else {
                vh.itemCheck.setVisibility(View.GONE);

            }
        }else {

            vh.itemCheck.setVisibility(View.GONE);
        }




        convertView.setTag(R.id.tv_title, content);

        return convertView;
    }


   static  class ViewHold{

       @Bind(R.id.item_title)
       TextView itemTitle;

       @Bind(R.id.item_check)
       ImageView itemCheck;

       public ViewHold(View view){

           ButterKnife.bind(this,view);
       }


   }

    public void notifyDataSetChanged(List<SelectItemBean> data) {
        notifyDataSetChanged();
        this.mData = data;
    }
}
