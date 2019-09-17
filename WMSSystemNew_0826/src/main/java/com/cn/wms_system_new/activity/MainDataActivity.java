package com.cn.wms_system_new.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.cn.wms_system_new.R;
import com.cn.wms_system_new.bean.SResponse;
import com.cn.wms_system_new.component.ClearEditText;
import com.cn.wms_system_new.component.Constants;
import com.cn.wms_system_new.component.ContentViewHolder;
import com.cn.wms_system_new.component.GetNowTime;
import com.cn.wms_system_new.component.TableView;
import com.cn.wms_system_new.component.TitleViewHolder;
import com.cn.wms_system_new.service.BootBroadcastReceiver;
import com.cn.wms_system_new.service.MyHandler;
import com.cn.wms_system_new.service.MyThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.cn.wms_system_new.service.MyThread.RESPONSE_FAILED;

/**
 * Created by LFeng on 2017/8/13.
 */

public class MainDataActivity extends Activity {
    public static final int REQUEST_REMARK = 1;
    public static final int REQUEST_DIALOG = 2;

    private TitleViewHolder titleHolder;
    private ContentViewHolder contentHolder;
    private HorizontalScrollView horizontalScrollView;
    private ZoomControls zoomControls;
    private BootBroadcastReceiver receiver;

    private Bundle bundle;
    private int selectedIndex = -1;
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
    private String primary;
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

    private String detailTitles;
    private String detailPrimary;

    private TableView.ButtonClickInterFace btnClick = new TableView.ButtonClickInterFace() {
        @Override
        public void btnClick(int position) {
            selectedIndex = position;
        }

        @Override
        public void sortFinish(final String titleName) {
            Iterator iterator = titles.entrySet().iterator();
            while (iterator.hasNext()) {
                final Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                if (entry.getValue().compareTo(titleName) == 0) {
                    Comparator<JSONObject> comparator = new Comparator<JSONObject>() {

                        @Override
                        public int compare(JSONObject lhs, JSONObject rhs) {
                            if (tableView.getSortRule() == TableView.SORT_ASCENDING)
                                return lhs.getString(entry.getKey()).compareTo(rhs.getString(entry.getKey()));
                            else
                                return rhs.getString(entry.getKey()).compareTo(lhs.getString(entry.getKey()));
                        }
                    };

                    List<JSONObject> list = new ArrayList<>();
                    for (int i = 0; i < objects.size(); i++) {
                        list.add(objects.getJSONObject(i));
                    }

                    Collections.sort(list, comparator);
                    objects.clear();
                    for (int i = 0; i < list.size(); i++) {
                        objects.add(list.get(i));
                    }
                    break;
                }
            }
        }
    };

    private boolean isRequireDate;

    //region 各个点击事件
    private View.OnClickListener onClickListener = new View.OnClickListener() {

        private int years;
        private int month;
        private int day;
        private int editTextIndex = 0;

        @Override
        public void onClick(View v) {

            editTextIndex = 2;
            switch (v.getId()) {
                case R.id.back: {
                    /**
                     * 如果点击的为标题栏返回按钮
                     */
                    finish();
                    break;
                }
                case R.id.refresh: {
                    /**
                     * 如果设置的为标题栏刷新按钮
                     */
                    isRequireDate = false;
                    downloadData();

                    break;
                }
                case R.id.editText1:
                    editTextIndex = 1;
                case R.id.editText2: {
                    /**
                     * 如果点击的为日期设置
                     */
                    DatePickerDialog dialog = new DatePickerDialog(v.getContext(),
                            null, GetNowTime.getYear(),
                            GetNowTime.getMonth(),
                            GetNowTime.getDay());

                    dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        // 对话框消失事件
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            isRequireDate = true;
                            DatePicker picker = ((DatePickerDialog) dialog)
                                    .getDatePicker();
                            years = picker.getYear();
                            month = picker.getMonth() + 1;
                            day = picker.getDayOfMonth();

                            if (editTextIndex == 1)
                                contentHolder.editText1.setText(composeDate(years, month, day));
                            if (editTextIndex == 2)
                                contentHolder.editText2.setText(composeDate(years, month, day));

                            if (contentHolder.editText1.getEditableText().toString().compareTo(
                                    contentHolder.editText2.getEditableText().toString()) > 0) {
                                Toast.makeText(getApplicationContext(),
                                        R.string.the_start_time_bigger_end_time,
                                        Toast.LENGTH_SHORT).show();
                            }
                            else {
                                downloadData();
                            }
                        }
                    });
                    dialog.show();
                    break;
                }
            }
        }
    };
    //endregion

    //region 点击表格行事件
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            selectedIndex = position;
            JSONObject selectObj;
            if (filterFlag) {
                selectObj = filterObjects.getJSONObject(selectedIndex);
            } else {
                selectObj = objects.getJSONObject(selectedIndex);
            }

            bundle.putString("primary", primary);
            bundle.putString("detailTitles", detailTitles);
            bundle.putString("detailPrimary", detailPrimary);
            bundle.putString("selectObj", selectObj.toJSONString());
            Intent intent = new Intent(MainDataActivity.this, DetailDataActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };
    //endregion

    //region 消息处理程序
    private MyHandler dateHandler = new MyHandler(this) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyThread.RESPONSE_SUCCESS:
                    SResponse response = (SResponse) msg.obj;
                    if (tableData != null) {
                        tableData.clear();
                    } else {
                        tableData = new ArrayList<>();
                    }

                    if (!TextUtils.isEmpty(response.getData()) &&
                            !(response.getData().compareTo("null") == 0)) {

                        //状态为0, 返回数据不为空
                        JSONObject object = JSONObject.parseObject(response.getData(), Feature.OrderedField);
                        if (JSONObject.parseObject(object.getString("titles"), Feature.OrderedField) != null)
                            titles = JSONObject.parseObject(object.getString("titles"), Feature.OrderedField);
                        if (!TextUtils.isEmpty(object.getString("primary")))
                            primary = object.getString("primary");

                        if (JSONObject.parseObject(object.getString("detailTitles"), Feature.OrderedField) != null)
                            detailTitles = object.getString("detailTitles");
                        if (!TextUtils.isEmpty(object.getString("detailPrimary")))
                            detailPrimary = object.getString("detailPrimary");

                        /**
                         * 生成标题
                         */
                        if (titles != null && titles.size() > 0) {
                            titleVale = new String[titles.size()];
                            Iterator iterator = titles.entrySet().iterator();
                            int index = 0;
                            while (iterator.hasNext()) {
                                Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                                titleVale[index] = entry.getValue().split(",")[0];
                                index++;
                            }
                        }

                        /**
                         * 生成数据
                         */
                        objects = object.getJSONArray("datas");

                        if (objects != null && objects.size() > 0) {
                            if (filterFlag) {
                                contentHolder.editText3.setText("");
                            }

                            for (int i = 0; i < objects.size(); i++) {
                                JSONObject obj = objects.getJSONObject(i);
                                Iterator iterator = titles.entrySet().iterator();
                                int tmp = 0;
                                String[] data = new String[titles.size()];
                                while (iterator.hasNext()) {
                                    Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                                    data[tmp] = (null == obj.getString(entry.getKey())) ? ("") : (obj.getString(entry.getKey()));
                                    tmp++;
                                }
                                tableData.add(data);
                            }
                        } else {
                            Toast.makeText(MainDataActivity.this, getString(R.string.data_empty), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        //状态为0, 返回数据为空
                        downloadData();
                        Toast.makeText(MainDataActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    setTableViewCom(titleVale, tableData);
                    break;
                case RESPONSE_FAILED:
                    if (tableData != null) {
                        tableData.clear();
                    } else {
                        tableData = new ArrayList<>();
                    }
                    if (titleVale != null && titleVale.length > 0)
                        setTableViewCom(titleVale, tableData);
                    break;
            }
        }
    };
    //endregion

    //region搜索框文本发生改变时，根据文本内容过滤数据
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s != null && s.length() > 0) {
                if (s.length() > 3) {
                    filterData(tableData, s.toString());
                    tableView.refreshData(filterData);
                    filterFlag = true;
                }
            } else {
                tableView.refreshData(tableData);
                filterFlag = false;
            }
        }
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        /*getWindow().setFlags(Constants.FLAG_HOMEKEY_DISPATCHED,
                Constants.FLAG_HOMEKEY_DISPATCHED);*/
        setContentView(R.layout.activity_list_data);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.layout_title_bar);

        initComponents();
        initParams();
        setComponents();
        downloadData();
    }

    public void setTableViewCom(String[] titleVale, List<String[]> tableData) {
        tableView = new TableView(getApplicationContext(), titleVale, tableData, true);
        tableView.setTitleTextSize(textSize);
        tableView.setContentTextSize(textSize);
        tableView.setItemHeight(130);
        tableView.setTitleBackgroundColor(getResources().getColor(
                R.color.the_table_title_bg_color));
        tableView.setItemBackgroundColor(
                getResources().getColor(R.color.the_table_content_bg_color1),
                getResources().getColor(R.color.the_table_content_bg_color2));
        tableView.definedSetChanged();
        tableView.setOnItemClickListener(onItemClickListener);
        tableView.setBtnClick(btnClick);

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.scrollview);
        horizontalScrollView.removeAllViews();
        horizontalScrollView.addView(tableView);

        zoomControls = (ZoomControls) findViewById(R.id.zoom_controls);
        // 　setOnZoomInClickListener()　-　响应单击放大按钮的事件
        zoomControls.setOnZoomInClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 点击放大按钮之前，若textSize小于等于最小文本值，则使能缩小按钮
                if (textSize <= Constants.TEXTSIZE_MIN)
                    zoomControls.setIsZoomOutEnabled(true);
                // 若textSize小于最大文本值，则放大文本
                if (textSize < Constants.TEXTSIZE_MAX)
                    textSize += 2;
                // 若textSize大于等于最大文本值，则禁用放大按钮
                if (textSize >= Constants.TEXTSIZE_MAX)
                    zoomControls.setIsZoomInEnabled(false);
                // 重新设置表格文本字体大小，刷新表格
                tableView.setTitleTextSize(textSize);
                tableView.setContentTextSize(textSize);
                tableView.definedSetChanged();
            }
        });
        // 　setOnZoomOutClickListener()　-　响应单击缩小按钮的事件
        zoomControls.setOnZoomOutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // 点击放大按钮之前，若textSize大于等于最大文本值，则使能放大按钮
                if (textSize >= Constants.TEXTSIZE_MAX)
                    zoomControls.setIsZoomInEnabled(true);
                // 若textSize大于最小文本值，则缩小文本
                if (textSize > Constants.TEXTSIZE_MIN)
                    textSize -= 2;
                // 若textSize小于等于最小文本值，则禁用缩小按钮
                if (textSize <= Constants.TEXTSIZE_MIN)
                    zoomControls.setIsZoomOutEnabled(false);
                // 重新设置表格文本字体大小，刷新表格
                tableView.setTitleTextSize(textSize);
                tableView.setContentTextSize(textSize);
                tableView.definedSetChanged();
            }
        });
    }

    /**
     * 初始化UI组件
     */
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

    /**
     * 设置UI组件属性
     */
    public void setComponents() {
        titleHolder.backButton.setOnClickListener(onClickListener);
        titleHolder.refreshButton.setOnClickListener(onClickListener);
        titleHolder.curSystemTime.setText(getResources().getString(
                R.string.cur_time_promote)
                + GetNowTime.getHour()
                + ":"
                + ((GetNowTime.getMinute() < 10) ? ("0" + GetNowTime
                .getMinute()) : GetNowTime.getMinute()));
        titleHolder.titleTextView.setText(bundle.getString("module"));

        contentHolder.textView1.setText(R.string.plan_demand_date);
        contentHolder.editText1.setText(composeDate(GetNowTime.getYear(),
                GetNowTime.getMonth() + 1, GetNowTime.getDay()));
        contentHolder.editText1.setFocusable(false);
        contentHolder.editText1.setOnClickListener(onClickListener);

        contentHolder.textView2.setText(R.string.date_arrive);
        contentHolder.editText2.setText(composeDate(GetNowTime.getYear(),
                GetNowTime.getMonth() + 1, GetNowTime.getDay()));
        contentHolder.editText2.setFocusable(false);
        contentHolder.editText2.setOnClickListener(onClickListener);

        contentHolder.textView3.setText(R.string.quick_search);
        contentHolder.editText3.setHint(R.string.list_search_promte);
        contentHolder.editText3.addTextChangedListener(watcher);
    }

    /**
     * 初始化参数
     */
    public void initParams() {

        bundle = getIntent().getExtras();
        isRequireDate = false;
        //employee = JSONObject.parseObject(bundle.getString("employee"), Employee.class);
    }

    /**
     * 组合年月日
     *
     * @param year  year
     * @param month month
     * @param day   day
     * @return year-month-day
     */
    public String composeDate(int year, int month, int day) {
        return year + "-" + ((month < 10) ? ("0" + month) : month) + "-"
                + ((day < 10) ? ("0" + day) : day);
    }

    // 数据过滤
    public void filterData(List<String[]> data, String s) {
        if (filterData != null) {
            filterData.clear();
        } else {
            filterData = new ArrayList<>();
        }
        if (filterObjects != null) {
            filterObjects.clear();
        } else {
            filterObjects = new JSONArray();
        }

        for (int i = 0; i < data.size(); i++) {
            String[] item = data.get(i);
            for (String str : item) {
                if (str.contains(s)) {
                    filterData.add(item);
                    filterObjects.add(objects.get(i));
                    break;
                }
            }
        }
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        /**
         * 注册广播接收者，用于更新时间
         */
        receiver = new BootBroadcastReceiver() {
            @Override
            public void updateTime() {
                titleHolder.curSystemTime.setText(getResources().getString(
                        R.string.cur_time_promote)
                        + GetNowTime.getHour()
                        + ":"
                        + ((GetNowTime.getMinute() < 10) ? ("0" + GetNowTime
                        .getMinute()) : GetNowTime.getMinute()));
            }

            @Override
            public void updateData() {
            }
        };
        IntentFilter filter = new IntentFilter("android.intent.action.TIME_TICK");
        registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void downloadData() {
        /**
         * 备货管理、领货管理、配送管理计划单消息请求
         */
        if (bundle.getString("funName").compareTo("备货管理") == 0 ||
                bundle.getString("funName").compareTo("领货管理") == 0 ||
                bundle.getString("funName").compareTo("配送管理") == 0) {
            JSONObject object = new JSONObject();
            object.put("module", bundle.getString("funName"));
            object.put("operation", "create");
            object.put("type", "app");
            if (isRequireDate) {
                object.put("startTime", contentHolder.editText1.getEditableText().toString() + " 00:00");
                object.put("endTime", contentHolder.editText2.getEditableText().toString() + " 23:59");
            }

            if (bundle.getString("module").contains("一工厂")) {
                object.put("ZDCustomerID", "9997");
            }
            if (bundle.getString("module").contains("二工厂")) {
                object.put("ZDCustomerID", "9998");
            }
            if (bundle.getString("module").contains("三工厂")) {
                object.put("ZDCustomerID", "9999");
            }
            new MyThread(dateHandler, object, bundle.getString("action")).start();
        }

        if (bundle.getString("funName").compareTo("库房作业管理") == 0) {
            JSONObject dateObj = new JSONObject();
            if (isRequireDate) {
                dateObj.put("start", contentHolder.editText1.getEditableText().toString() + " 00:00");
                dateObj.put("end", contentHolder.editText2.getEditableText().toString() + " 23:59");
            }

            JSONObject object = new JSONObject();
            object.put("module", bundle.getString("module", ""));
            object.put("operation", "create");
            object.put("type", "app");
            object.put("rely", dateObj.toJSONString());
            new MyThread(dateHandler, object, bundle.getString("action")).start();
        }
    }

}
