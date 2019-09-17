package com.lfeng.pipingfactory;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.lfeng.pipingfactory.bean.LoginBean;
import com.lfeng.pipingfactory.bean.OrderInfo;
import com.lfeng.pipingfactory.util.HttpUtil;
import com.lfeng.pipingfactory.view.MyDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fuyzh on 16/8/25.
 */
public class HistoryOrderActivity extends Activity {

    private Dialog dialog;
    private ListView listView;
    private List<OrderInfo> orderList;
    private OrderListAdapter adapter;

    private OrderListAdapter.ItemBtnClickListener itemBtnClickListener = new OrderListAdapter.ItemBtnClickListener() {
        @Override
        public void itemBtnClick(int orderId) {
            Intent intent = new Intent();
            intent.putExtra("orderId", orderId);
            setResult(RESULT_OK, intent);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_order);

        orderList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.history_order_list);
        adapter = new OrderListAdapter(this, orderList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OrderInfo info = orderList.get(position);
                Intent intent = new Intent();
                intent.putExtra("orderId", info.getOrderId());
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        JSONObject object = new JSONObject();
        object.put("pageIndex", 1);
        object.put("pageSize", 9999);
        new GetOrderInfoAsyncTask().execute(object);
    }

    class GetOrderInfoAsyncTask extends AsyncTask<JSONObject, String, String> {
        @Override
        protected void onPreExecute() {
            Point size = new Point();
            HistoryOrderActivity.this.getWindowManager().getDefaultDisplay().getSize(size);
            int width = (int) (size.x * 0.25);
            int height = (int) (size.y * 0.15);
            String text = "查询数据中...";
            String type = "Load";
            dialog = new MyDialog(HistoryOrderActivity.this, width, height, text, type);
            dialog.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject jsonObject = params[0];
            String data = HttpUtil.httpPost(HttpUtil.defaultHost + HttpUtil.getHistoryOrderListURL, jsonObject);
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            super.onPostExecute(s);
            if (s.isEmpty()) {
                Toast.makeText(HistoryOrderActivity.this, R.string.servlet_error, Toast.LENGTH_SHORT).show();
                return;
            }
            LoginBean loginBean = JSONObject.parseObject(s, LoginBean.class);
            if (loginBean.getStatus() == 0) {
                Toast.makeText(HistoryOrderActivity.this, R.string.select_success, Toast.LENGTH_SHORT).show();
                orderList.clear();
                orderList.addAll(JSONObject.parseArray(loginBean.getData(), OrderInfo.class));
                System.out.println("size:" + orderList.size());
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(HistoryOrderActivity.this, loginBean.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
