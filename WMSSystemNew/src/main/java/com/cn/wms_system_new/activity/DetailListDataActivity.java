package com.cn.wms_system_new.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cn.wms_system_new.R;
import com.cn.wms_system_new.component.ClearEditText;
import com.cn.wms_system_new.component.Constants;
import com.cn.wms_system_new.component.ContentViewHolder;
import com.cn.wms_system_new.component.TableView;
import com.cn.wms_system_new.component.TitleViewHolder;
import com.cn.wms_system_new.service.BootBroadcastReceiver;
import com.cn.wms_system_new.service.MyThread;

import java.util.List;

/**
 * Created by LFeng on 2017/8/13.
 */

public class DetailListDataActivity extends Activity {

    private TitleViewHolder titleHolder;
    private ContentViewHolder contentHolder;
    private HorizontalScrollView horizontalScrollView;
    private ZoomControls zoomControls;
    private BootBroadcastReceiver receiver;

    private Bundle bundle;
    private int textSize = Constants.TEXTSIZE_INIT;

    /**
     * 表格视图
     */
    private TableView tableView;
    /**
     * 表格标题
     */
    private String[] titleVale;
    private JSONObject titles;
    /**
     * 表格显示数据
     */
    private List<String[]> tableData;
    private JSONArray objects;
    /**
     * 过滤后的数据
     */
    private List<String[]> filterData;
    private JSONArray filterObjects;
    private boolean filterFlag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_list_data);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.layout_title_bar);
    }

    public void initComponents() {
        titleHolder = new TitleViewHolder();
        titleHolder.backButton = (LinearLayout) findViewById(R.id.back);
        titleHolder.curSystemTime = (TextView) findViewById(R.id.cur_sys_time);
        titleHolder.titleTextView = (TextView) findViewById(R.id.title);
        titleHolder.refreshButton = (LinearLayout) findViewById(R.id.refresh);

        contentHolder = new ContentViewHolder();
        contentHolder.textView1 = (TextView) findViewById(R.id.textView1);
        contentHolder.editText1 = (EditText) findViewById(R.id.editText1);
        contentHolder.textView2 = (TextView) findViewById(R.id.textView2);
        contentHolder.editText2 = (EditText) findViewById(R.id.editText2);
        contentHolder.textView3 = (TextView) findViewById(R.id.textView3);
        contentHolder.editText3 = (ClearEditText) findViewById(R.id.editText3);
        contentHolder.textView4 = (TextView) findViewById(R.id.textview4);
    }

    public void requestDetail(JSONObject object) {
        object.put("module", bundle.getString("module", ""));
        object.put("operation", "request_detail");
        object.put("type", "app");
        object.put("pageSize", Integer.MAX_VALUE);
        object.put("pageIndex", 1);
        //new MyThread(dateHandler, object, bundle.getString("action")).start();
    }
}
