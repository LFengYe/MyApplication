package com.lfeng.pipingfactory;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.lfeng.pipingfactory.bean.OrderInfo;

import java.util.List;

/**
 * Created by fuyzh on 16/9/4.
 */
public class OrderListAdapter extends BaseAdapter {

    private Context context;
    private List<OrderInfo> orderList;
    private boolean isBtnVisible;
    private ItemBtnClickListener btnClickListener;

    public boolean isBtnVisible() {
        return isBtnVisible;
    }

    public void setIsBtnVisiable(boolean isBtnVisible) {
        this.isBtnVisible = isBtnVisible;
    }

    public ItemBtnClickListener getBtnClickListener() {
        return btnClickListener;
    }

    public void setBtnClickListener(ItemBtnClickListener btnClickListener) {
        this.btnClickListener = btnClickListener;
    }

    public OrderListAdapter(Context context, List<OrderInfo> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @Override
    public int getCount() {
        return orderList.size();
    }

    @Override
    public Object getItem(int position) {
        if (null != orderList && orderList.size() > position)
            orderList.get(position);
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (null == convertView) {
            convertView = View.inflate(context, R.layout.order_item, null);
            holder = new ViewHolder();
            holder.orderCommand = (TextView) convertView.findViewById(R.id.item_orderCommand);
            holder.productCode = (TextView) convertView.findViewById(R.id.item_productCode);
            holder.productStandard = (TextView) convertView.findViewById(R.id.item_productStandard);
            holder.productPlanNum = (TextView) convertView.findViewById(R.id.item_productPlanNum);
            holder.cardNum = (TextView) convertView.findViewById(R.id.item_cardNum);
            holder.itemBtn = (Button) convertView.findViewById(R.id.item_btn);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final OrderInfo orderInfo = orderList.get(position);
        holder.orderCommand.setText(orderInfo.getProductCommand());
        holder.productCode.setText(orderInfo.getProductCode());
        holder.productStandard.setText(orderInfo.getProductStandard());
        holder.productPlanNum.setText(orderInfo.getPlanNum() + "");
        //holder.cardNum.setText();
        if (isBtnVisible) {
            holder.itemBtn.setVisibility(View.VISIBLE);
            holder.itemBtn.setText(context.getString(R.string.auto_xiaLiao));
            holder.itemBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btnClickListener.itemBtnClick(orderInfo.getOrderId());
                    v.setVisibility(View.GONE);
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        TextView orderCommand;
        TextView productCode;
        TextView productStandard;
        TextView productPlanNum;
        TextView cardNum;
        Button itemBtn;
    }

    interface ItemBtnClickListener {
        public void itemBtnClick(int orderId);
    }
}
