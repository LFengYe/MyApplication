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
import com.cxgps.vehicle.bean.CarsInfo;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by taosong on 16/7/25.
 */
public class CarsListAdapter extends BaseAdapter {

    private Context mContext;

    private ArrayList<CarsInfo> mData;

    public CarsListAdapter(Context context, ArrayList<CarsInfo> data){

        this.mContext = context;
        this.mData = data;

    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHold vh = null;
        if (convertView == null ) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_cell_car, null);
            vh = new ViewHold(convertView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHold) convertView.getTag();
        }


        CarsInfo softwareDes = (CarsInfo) mData.get(position);


        StringBuffer buffer = new StringBuffer(softwareDes.getCarDesc());



        buffer.append("  ").append(mContext.getString(R.string.car_out_data)).
                append(softwareDes.getOutDate());


        vh.des.setText(buffer.toString());
        vh.name.setText(softwareDes.getCarNumber()+"["+softwareDes.getSystemNo()+"]");

        //   1  行驶 2 停止 3 离线 4 报警  5 不定位

        int numberColor = 0;

        int numberIcon = R.mipmap.chedui_car_blue;

        switch (Integer.parseInt(softwareDes.getCarState())) {
            case 1:
                numberColor = Color.BLUE;
                numberIcon = R.mipmap.chedui_car_blue;
                break;
            case 2:
                numberColor = Color.parseColor("#20b2aa");
                numberIcon = R.mipmap.chedui_car_green;

                break;
            case 3:
                numberColor = Color.BLACK;
                numberIcon = R.mipmap.chedui_car_black;

                break;
            case 4:
                numberColor = Color.parseColor("#ffd46f");
                numberIcon = R.mipmap.chedui_car_alert;

                break;
            case 5:
                numberColor = Color.RED;
                numberIcon = R.mipmap.chedui_car_red;

                break;
            default:
                numberColor = Color.BLACK;
                break;
        }




        vh.name.setTextColor(numberColor);

        vh.icons.setImageBitmap(BitmapFactory.decodeResource(parent.getContext().getResources(), numberIcon));

        convertView.setTag(R.id.tv_title, softwareDes);

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



    public void notifyDataSetChanged(ArrayList<CarsInfo> data) {
        notifyDataSetChanged();
        this.mData = data;
    }
}
