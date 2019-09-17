package com.lfeng.pipingfactory;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
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
 * Created by fuyzh on 16/9/3.
 */
public class AutoXiaLiaoSelectActivity extends Activity {

    private Dialog dialog;
    private ListView listView;
    private List<OrderInfo> orderList;
    private OrderListAdapter adapter;

    private OrderListAdapter.ItemBtnClickListener itemBtnClickListener = new OrderListAdapter.ItemBtnClickListener() {
        @Override
        public void itemBtnClick(int orderId) {
            JSONObject object = new JSONObject();
            object.put("orderId", orderId);
            object.put("xiaLiaoState", 3);
            new UpdateXiaLiaoStateTask().execute(object);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_xialiao_select);
        orderList = new ArrayList<>();
        listView = (ListView) findViewById(R.id.auto_xiaLiao_order_list);
        adapter = new OrderListAdapter(this, orderList);
        adapter.setIsBtnVisiable(true);
        adapter.setBtnClickListener(itemBtnClickListener);
        listView.setAdapter(adapter);

        JSONObject object = new JSONObject();
        object.put("pageIndex", 1);
        object.put("pageSize", 9999);
        new GetOrderInfoAsyncTask().execute(object);
    }

    class GetOrderInfoAsyncTask extends AsyncTask<JSONObject, String, String> {
        @Override
        protected void onPreExecute() {
            Point size = new Point();
            AutoXiaLiaoSelectActivity.this.getWindowManager().getDefaultDisplay().getSize(size);
            int width = (int) (size.x * 0.25);
            int height = (int) (size.y * 0.15);
            String text = "查询数据中...";
            String type = "Load";
            dialog = new MyDialog(AutoXiaLiaoSelectActivity.this, width, height, text, type);
            dialog.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject jsonObject = params[0];
            String data = HttpUtil.httpPost(HttpUtil.defaultHost + HttpUtil.getCouldAutoXiaLiaoListURL, jsonObject);
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            super.onPostExecute(s);
            if (s.isEmpty()) {
                Toast.makeText(AutoXiaLiaoSelectActivity.this, R.string.servlet_error, Toast.LENGTH_SHORT).show();
                return;
            }
            LoginBean loginBean = JSONObject.parseObject(s, LoginBean.class);
            if (loginBean.getStatus() == 0) {
                Toast.makeText(AutoXiaLiaoSelectActivity.this, R.string.select_success, Toast.LENGTH_SHORT).show();
                orderList.clear();
                orderList.addAll(JSONObject.parseArray(loginBean.getData(), OrderInfo.class));
                System.out.println("size:" + orderList.size());
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(AutoXiaLiaoSelectActivity.this, loginBean.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class UpdateXiaLiaoStateTask extends AsyncTask<JSONObject, String, String> {
        @Override
        protected void onPreExecute() {
            Point size = new Point();
            AutoXiaLiaoSelectActivity.this.getWindowManager().getDefaultDisplay().getSize(size);
            int width = (int) (size.x * 0.25);
            int height = (int) (size.y * 0.15);
            String text = "数据上传中...";
            String type = "Load";
            dialog = new MyDialog(AutoXiaLiaoSelectActivity.this, width, height, text, type);
            dialog.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject jsonObject = params[0];
            String data = HttpUtil.httpPost(HttpUtil.defaultHost + HttpUtil.updateXiaLiaoStateURL, jsonObject);
            return data;
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            super.onPostExecute(s);
            if (s.isEmpty()) {
                Toast.makeText(AutoXiaLiaoSelectActivity.this, R.string.servlet_error, Toast.LENGTH_SHORT).show();
                return;
            }
            LoginBean loginBean = JSONObject.parseObject(s, LoginBean.class);
            if (loginBean.getStatus() == 0) {
                Toast.makeText(AutoXiaLiaoSelectActivity.this, R.string.select_success, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(AutoXiaLiaoSelectActivity.this, loginBean.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
