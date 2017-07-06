package com.cxgps.vehicle.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxgps.vehicle.R;
import com.cxgps.vehicle.bean.CarsInfo;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by cxiosapp on 15/11/9.
 */
public class SearchAdapter extends BaseAdapter {

    private ArrayList<CarsInfo> mData;

    private Context mContext;


    public SearchAdapter(Context context, ArrayList<CarsInfo> data){

        this.mContext = context;
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
        return position;
    }

    @Override
    public View getView(final  int position, View convertView, ViewGroup parent) {
        ViewHolder vh = null;
        if (convertView == null || convertView.getTag() == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.list_cell_car, null);
            vh = new ViewHolder(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        CarsInfo softwareDes =   mData.get(position);

        String carDescStr = "";

        String remarksTag =  softwareDes.getCarOutType();

        if ("0".equals(remarksTag)){
            carDescStr = "已过期";

        }else if("1".equals(remarksTag)){

            carDescStr = "未过期";
        }else {

            carDescStr = softwareDes.getCarDesc();
        }






        //   1  行驶 2 停止 3 离线 4 报警  5 不定位

        int numberColor = 0;

        int numberIcon = R.mipmap.chedui_car_blue;

        switch (Integer.parseInt(softwareDes.getCarState())) {
            case 2:
                numberColor = Color.BLUE;
                numberIcon = R.mipmap.chedui_car_blue;
                break;
            case 1:
                numberColor = Color.parseColor("#20b2aa");
                numberIcon = R.mipmap.chedui_car_green;

                break;
            case 0:
                numberColor = Color.BLACK;
                numberIcon = R.mipmap.chedui_car_black;

                break;

            default:
                numberColor = Color.BLACK;
                break;
        }


        StringBuffer buffer = new StringBuffer(carDescStr);

        vh.description.setText(buffer.toString());

        Log.i("TAG", "=============Item=====" + softwareDes.getCarNumber() + "[" + softwareDes.getSystemNo() + "]");
        vh.title.setText(softwareDes.getCarNumber() + "[" + softwareDes.getSystemNo() + "]");


        vh.title.setTextColor(numberColor);

        vh.icons.setImageBitmap(BitmapFactory.decodeResource(parent.getContext().getResources(), numberIcon));

        convertView.setTag(R.id.tv_title,mData.get(position));

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.tv_title)TextView title;
        @Bind(R.id.tv_software_des)
        TextView description;

        @Bind(R.id.ivNews)
        ImageView icons;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }



    public void notifyDataSetChanged(ArrayList<CarsInfo> mData) {

        this.mData = mData;

        notifyDataSetChanged();
    }
}
