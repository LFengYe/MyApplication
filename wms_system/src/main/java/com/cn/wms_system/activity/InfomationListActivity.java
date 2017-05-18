package com.cn.wms_system.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.cn.wms_system.R;
import com.cn.wms_system.component.ClearEditText;
import com.cn.wms_system.component.Constants;
import com.cn.wms_system.component.ContentViewHolder;
import com.cn.wms_system.component.GetNowTime;
import com.cn.wms_system.component.SideBar;
import com.cn.wms_system.component.TableView;
import com.cn.wms_system.component.TitleViewHolder;
import com.cn.wms_system.dialog.DateTimeSetDialog;
import com.cn.wms_system.dialog.DateTimeSetDialog.GetDialogData;
import com.cn.wms_system.service.BootBroadcastReceiver;
import com.cn.wms_system.service.MyThread;
import com.cn.wms_system.service.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.cn.wms_system.component.Constants.serverUrl;

//import android.widget.FrameLayout.LayoutParams;

public class InfomationListActivity extends Activity {

    private TitleViewHolder titleHolder;
    private ContentViewHolder contentHolder;
    private HorizontalScrollView horizontalScrollView;
    private SideBar sideBar;
    private ZoomControls zoomControls;

    private BootBroadcastReceiver receiver;
    /**
     * 表格视图
     */
    private TableView tableView;
    /**
     * 表头
     */
    private String[] titleVale;
    /**
     * 表格数据
     */
    private List<String[]> tableData;
    private JSONArray datas;
    /**
     * 过滤后的表格数据
     */
    private List<String[]> filterData;
    /**
     * 数据过滤标志
     */
    private boolean filterFlag;
    /**
     * web操作
     */
//    private WebOperate webOperate;
    /**
     * 上传服务器参数列表
     */
    private Map<String, String> params;
    /**
     * webService函数名
     */
    private String methodName;

    private Bundle bundle;

    private boolean requiredDate = false;
    private int textSize = Constants.TEXTSIZE_INIT;

    /**
     * 各个点击事件
     */
    private OnClickListener onClickListener = new OnClickListener() {

        private int years;
        private int month;
        private int day;

        @Override
        public void onClick(final View v) {
            /**
             * 如果点击的为标题栏返回按钮
             */
            if (v.getId() == titleHolder.backButton.getId()) {
                finish();
                return;
            }
            /**
             * 如果设置的为标题栏刷新按钮
             */
            if (v.getId() == titleHolder.refreshButton.getId()) {
                requiredDate = false;
                downloadData();
                return;
            }

            /**
             * 如果点击的为日期设置
             */
            DatePickerDialog dialog = new DatePickerDialog(v.getContext(),
                    null, (years > 0) ? years : GetNowTime.getYear(),
                    (month > 0) ? (month - 1) : GetNowTime.getMonth(),
                    (day > 0) ? day : GetNowTime.getDay());
            dialog.setOnDismissListener(new OnDismissListener() {
                // 对话框消失事件
                @Override
                public void onDismiss(DialogInterface dialog) {
                    DatePicker picker = ((DatePickerDialog) dialog)
                            .getDatePicker();
                    years = picker.getYear();
                    month = picker.getMonth() + 1;
                    day = picker.getDayOfMonth();

                    // 开始时间判断，正确则下载数据
                    if (v.getId() == contentHolder.editText1.getId()) {
                        contentHolder.editText1.setText(composeDate(years,
                                month, day));
                        if (contentHolder.editText1
                                .getEditableText()
                                .toString()
                                .compareTo(
                                        contentHolder.editText2
                                                .getEditableText().toString()) > 0)
                            Toast.makeText(getApplicationContext(),
                                    R.string.the_start_time_bigger_end_time,
                                    Toast.LENGTH_SHORT).show();
                        else {
                            requiredDate = true;
                            downloadData();
                        }
                    }
                    // 截止时间判断，正确则下载数据
                    if (v.getId() == contentHolder.editText2.getId()) {
                        contentHolder.editText2.setText(composeDate(years,
                                month, day));
                        if (contentHolder.editText2
                                .getEditableText()
                                .toString()
                                .compareTo(
                                        contentHolder.editText1
                                                .getEditableText().toString()) < 0)
                            Toast.makeText(getApplicationContext(),
                                    R.string.the_end_time_smaller_start_time,
                                    Toast.LENGTH_SHORT).show();
                        else {
                            requiredDate = true;
                            downloadData();
                        }
                    }
                }
            });
            dialog.show();
        }
    };

    /**
     * 点击表格行事件
     */
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            try {
                JSONObject object = datas.getJSONObject(position);
                confirmItem(object);
            } catch (JSONException e) {
            }
            /*
            // 判断是使用表格全部数据还是过滤后的数据
            List<String[]> tempData = (filterFlag ? filterData : tableData);

            if (bundle.getInt("selected_fun_index", 1) == 5) {
                // 点击报表无响应

                return;
            }
            Intent intent = new Intent();
            // 将数据捆绑到Bundle来传递数据
            if (bundle.getInt("selected_fun_index", 1) <= 3) {
                bundle.putString("plan_type", tempData.get(position)[0]);
                bundle.putString("plan_odd_num", tempData.get(position)[7]);
                bundle.putString("title", titleHolder.titleTextView.getText()
                        .toString() + "---" + tempData.get(position)[2]);
            }
            if (bundle.getInt("selected_fun_index", 1) == 4) {
                bundle.putString("plan_type", tempData.get(position)[1]);
                bundle.putString("plan_odd_num", tempData.get(position)[2]);
                bundle.putString("title", titleHolder.titleTextView.getText()
                        .toString() + "---" + tempData.get(position)[0]);
            }
            // 界面跳转，传递数据
            intent.putExtras(bundle);
            intent.setClass(InfomationListActivity.this,
                    InfomationDetailActivity.class);
            startActivity(intent);
            */
        }
    };

    /**
     * 消息处理程序
     */
    private Handler myHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MyThread.SUCCESS:
                    try {
                        JSONObject response = (JSONObject) msg.obj;
                        if (response.getInt("status") == 0) {
                            JSONObject object = new JSONObject(response.getString("data"));
                            JSONObject titles = object.getJSONObject("titles");
                            /**
                             * 生成标题
                             */
                            titleVale = new String[titles.length()];
                            Iterator<String> iterator = titles.keys();
                            int index = 0;
                            while (iterator.hasNext()) {
                                String key = iterator.next();
                                titleVale[index] = titles.getString(key);
                                index++;
                            }
                            /**
                             * 生成数据
                             */
                            datas = object.getJSONArray("datas");
                            tableData = new ArrayList<>();
                            for (int i = 0; i < datas.length(); i++) {
                                JSONObject obj = datas.getJSONObject(i);
                                String[] data = new String[titles.length()];
                                iterator = titles.keys();
                                int tmp = 0;
                                while (iterator.hasNext()) {
                                    String key = iterator.next();
                                    data[tmp] = (null == obj.getString(key)) ? ("") : (obj.getString(key));
                                    tmp++;
                                }
                                tableData.add(data);
                            }
                            setTableViewCom(titleVale, tableData);
                        } else {
                            Toast.makeText(InfomationListActivity.this, response.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        Toast.makeText(InfomationListActivity.this, R.string.response_unformat, Toast.LENGTH_LONG).show();
                    }
                    break;
                case Constants.GET_MESSAGE_IS_EMPTY:
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 搜索框文本发生改变时，根据文本内容过滤数据
     */
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
                filterData = filterData(tableData, s.toString());
                tableView.refreshData(filterData);
                filterFlag = true;
                return;
            }
            tableView.refreshData(tableData);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        getWindow().setFlags(Constants.FLAG_HOMEKEY_DISPATCHED,
                Constants.FLAG_HOMEKEY_DISPATCHED);
        setContentView(R.layout.activity_info_list);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.layout_title_bar);

        initComponents();
        initParams();
        setComponents();
        //setTableViewCom();
        downloadData();
    }

    public void setTableViewCom(String[] titleVale, List<String[]> tableData) {
        tableView = new TableView(getApplicationContext(), titleVale, tableData, false);
        tableView.setTitleTextSize(textSize);
        tableView.setContentTextSize(textSize);
        tableView.setTitleBackgroundColor(getResources().getColor(
                R.color.the_table_title_bg_color));
        tableView.setItemBackgroundColor(
                getResources().getColor(R.color.the_table_content_bg_color1),
                getResources().getColor(R.color.the_table_content_bg_color2));
        tableView.definedSetChanged();
        tableView.setOnItemClickListener(onItemClickListener);

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.scrollview);
        horizontalScrollView.removeAllViews();
        horizontalScrollView.addView(tableView);

        sideBar = (SideBar) findViewById(R.id.sidebar);
        sideBar.setVisibility(View.INVISIBLE);

        zoomControls = (ZoomControls) findViewById(R.id.zoom_controls);
        // 　setOnZoomInClickListener()　-　响应单击放大按钮的事件
        zoomControls.setOnZoomInClickListener(new OnClickListener() {

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
        zoomControls.setOnZoomOutClickListener(new OnClickListener() {

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

    public void setTableViewCom() {
        tableData = new ArrayList<>();
        /** 备货、领货、配送计划单表头 */
        if (bundle.getInt("selected_fun_index", 1) <= 3) {
            titleVale = getResources().getStringArray(
                    R.array.stock_plan_list_table_header);
            tableView = new TableView(getApplicationContext(), titleVale, tableData, true);
        }
        /** 库房作业管理 */
        if (bundle.getInt("selected_fun_index", 1) == 4) {
            // 非生产领料计划单表头
            if (bundle.getInt("selected_child_fun_item", 1) == 0) {
                titleVale = getResources().getStringArray(
                        R.array.non_pro_pick_list_table_header);
            }
            // 部品退货计划单表头
            if (bundle.getInt("selected_child_fun_item", 1) == 1) {
                titleVale = getResources().getStringArray(
                        R.array.return_list_table_header);
            }
            // 送检出库、送检返还、返修出库、返修入库计划单表头
            if (bundle.getInt("selected_child_fun_item", 1) >= 2
                    && bundle.getInt("selected_child_fun_item", 1) <= 5) {
                titleVale = getResources().getStringArray(
                        R.array.return_rework_check_list_table_header);
            }
            // 部品退库计划单表头
            if (bundle.getInt("selected_child_fun_item", 1) == 6) {
                titleVale = getResources().getStringArray(
                        R.array.return_warehouse_list_table_header);
            }

            tableView = new TableView(getApplicationContext(), titleVale, tableData, true);
        }
        /** 报表查询 */
        if (bundle.getInt("selected_fun_index", 1) == 5) {
            switch (bundle.getInt("selected_child_fun_item", 1)) {
                case 0:// 报检信息查询计划单表头
                    titleVale = getResources().getStringArray(
                            R.array.inspec_information_display_table_header);
                    break;
                case 1:// 部品库存查询计划单表头
                    titleVale = getResources().getStringArray(
                            R.array.stock_information_display_table_header);
                    break;
                case 2:// 库存报警信息计划单表头
                    titleVale = getResources().getStringArray(
                            R.array.stock_alarm_message_display_table_header);
                    break;
                case 3:// 出库计划完成情况计划单表头
                    titleVale = getResources().getStringArray(
                            R.array.outbound_plan_comple_table_header);
                    break;
                case 4:// 出库分录报表单头
                    titleVale = getResources().getStringArray(
                            R.array.outbound_detail_record_table_header);
                    break;
                case 5:// 入库分录报表单头
                    titleVale = getResources().getStringArray(
                            R.array.inbound_detail_record_table_header);
                    break;
                case 6:// 退货分录报表单头
                    titleVale = getResources().getStringArray(
                            R.array.return_detail_record_table_header);
                    break;
                case 7:// 入库明细报表单头
                    titleVale = getResources().getStringArray(
                            R.array.inbound_detail_table_header);
                    break;
            }
            tableView = new TableView(getApplicationContext(), titleVale, tableData,
                    false);
        }
        /** 交接单 */
        if (bundle.getInt("selected_fun_index", 1) == 7) {
            switch (bundle.getInt("selected_child_fun_item", 1)) {
                case 1:
                    break;
                case 2:
                    break;
                case 3:
                    break;
            }
            titleVale = getResources().getStringArray(R.array.delivery_receipt_list_table_header);
            tableView = new TableView(getApplicationContext(), titleVale, tableData,
                    false);
        }


        tableView.setTitleTextSize(textSize);
        tableView.setContentTextSize(textSize);
        tableView.setTitleBackgroundColor(getResources().getColor(
                R.color.the_table_title_bg_color));
        tableView.setItemBackgroundColor(
                getResources().getColor(R.color.the_table_content_bg_color1),
                getResources().getColor(R.color.the_table_content_bg_color2));
        tableView.definedSetChanged();
        tableView.setOnItemClickListener(onItemClickListener);

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.scrollview);
        horizontalScrollView.removeAllViews();
        horizontalScrollView.addView(tableView);

        sideBar = (SideBar) findViewById(R.id.sidebar);
        sideBar.setVisibility(View.INVISIBLE);

        zoomControls = (ZoomControls) findViewById(R.id.zoom_controls);
        // 　setOnZoomInClickListener()　-　响应单击放大按钮的事件
        zoomControls.setOnZoomInClickListener(new OnClickListener() {

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
        zoomControls.setOnZoomOutClickListener(new OnClickListener() {

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
        contentHolder.textView1 = (TextView) findViewById(R.id.textview1);
        contentHolder.editText1 = (EditText) findViewById(R.id.edittext1);
        contentHolder.textView2 = (TextView) findViewById(R.id.textview2);
        contentHolder.editText2 = (EditText) findViewById(R.id.edittext2);
        contentHolder.textView3 = (TextView) findViewById(R.id.textview3);
        contentHolder.editText3 = (ClearEditText) findViewById(R.id.edittext3);
        contentHolder.textView4 = (TextView) findViewById(R.id.textview4);
    }

    /**
     * 设置UI组件属性
     */
    public void setComponents() {
        titleHolder.backButton.setOnClickListener(onClickListener);
        titleHolder.refreshButton.setOnClickListener(onClickListener);
        titleHolder.curSystemTime.setText(getResources().getString(
                R.string.cur_time_promte)
                + GetNowTime.getHour()
                + ":"
                + ((GetNowTime.getMinute() < 10) ? ("0" + GetNowTime
                .getMinute()) : GetNowTime.getMinute()));
        titleHolder.titleTextView.setText(bundle.getString("title"));

        //除报表查询只有结束时间外, 其他都有开始时间和结束时间
        if (bundle.getInt("selected_fun_index", 1) != 5) {
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
        }

        //报表查询只有结束时间
        if (bundle.getInt("selected_fun_index", 1) == 5) {
            contentHolder.textView1.setText(R.string.report_end_time);
            contentHolder.editText1.setVisibility(View.GONE);

            contentHolder.textView2.setVisibility(View.GONE);
            contentHolder.editText2.setText(composeDateTime(
                    GetNowTime.getYear(), GetNowTime.getMonth() + 1,
                    GetNowTime.getDay(), GetNowTime.getHour(),
                    GetNowTime.getMinute()));
            contentHolder.editText2.setFocusable(false);
            contentHolder.editText2.setOnClickListener(new OnClickListener() {
                int day = GetNowTime.getDay();
                int hour = GetNowTime.getHour();
                int minute = GetNowTime.getMinute();
                int month = GetNowTime.getMonth();
                int year = GetNowTime.getYear();

                @Override
                public void onClick(View paramView) {
                    GetDialogData dialogData = new GetDialogData() {

                        @Override
                        public void getTime(TimePicker paramTimePicker) {
                            hour = paramTimePicker.getCurrentHour().intValue();
                            minute = paramTimePicker.getCurrentMinute()
                                    .intValue();
                        }

                        @Override
                        public void getDate(DatePicker paramDatePicker) {
                            year = paramDatePicker.getYear();
                            month = paramDatePicker.getMonth() + 1;
                            day = paramDatePicker.getDayOfMonth();
                        }
                    };
                    DateTimeSetDialog dialog = new DateTimeSetDialog(
                            InfomationListActivity.this, dialogData);
                    dialog.setTitle(R.string.date_time_set);
                    dialog.setOnDismissListener(new OnDismissListener() {

                        @Override
                        public void onDismiss(
                                DialogInterface paramDialogInterface) {
                            contentHolder.editText2.setText(composeDateTime(
                                    year, month, day, hour, minute));
                            downloadData();
                        }
                    });
                    dialog.show();
                }
            });
        }

        contentHolder.textView3.setText(R.string.quick_search);
        contentHolder.editText3.setHint(R.string.list_search_promte);
        contentHolder.editText3.addTextChangedListener(watcher);
    }

    /**
     * 初始化参数
     */
    public void initParams() {
        bundle = getIntent().getExtras();
        params = new HashMap<String, String>();
//        webOperate = new WebOperate(myHandler);
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

    /**
     * 组合年月日,时分
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @return
     */
    public String composeDateTime(int year, int month, int day, int hour,
                                  int minute) {
        return year + "-" + ((month < 10) ? ("0" + month) : month) + "-"
                + ((day < 10) ? ("0" + day) : day) + " "
                + ((hour < 10) ? ("0" + hour) : hour) + ":"
                + ((minute < 10) ? ("0" + minute) : minute);
    }

    /**
     * 向服务器发送请求
     */
    public void downloadData() {

        try {
            JSONObject object = new JSONObject();
            /**
             * 备货管理、领货管理、配送管理计划单消息请求
             */
            if (bundle.getInt("selected_fun_index", 1) <= 3) {
                SharedPreferences preferences = getSharedPreferences(
                        "system_params", MODE_PRIVATE);
                object.put("module", bundle.getString("selected_fun_index_name", ""));
                object.put("operation", "create");
                object.put("username", preferences.getString("username", "").split(",")[0]);
                object.put("startTime", contentHolder.editText1.getEditableText().toString() + " 00:00");
                object.put("endTime", contentHolder.editText2.getEditableText().toString() + " 23:59");

                if (requiredDate) {
                    /** 获取所有计划单 */
                    object.put("isFinished", 1);
                    requiredDate = false;
                } else {
                    /** 获取未完成计划单 */
                    object.put("isFinished", 0);
                }

                switch (bundle.getInt("selected_child_fun_item", 1)) {
                    case 0:
                        object.put("ZDCustomerID", "9997");
                        break;
                    case 1:
                        object.put("ZDCustomerID", "9998");
                        break;
                    case 2:
                        object.put("ZDCustomerID", "9999");
                        break;
                    default:
                        break;
                }
            }

            /**
             * 库房作业管理消息请求
             */
            if (bundle.getInt("selected_fun_index", 1) == 4) {
                params.put(
                        "s",
                        "{"
                                + (8 + bundle.getInt(
                                "selected_child_fun_item", 0) * 3)
                                + ";}");
            }

            /**
             * 报表消息请求
             */
            if (bundle.getInt("selected_fun_index", 1) == 5) {
                // 发送获取报检信息查询报表请求
                if (bundle.getInt("selected_child_fun_item", 1) == 0) {
                    params.put("s", "{"
                            + 29
                            + ";"
                            + contentHolder.editText1.getEditableText()
                            .toString()
                            + " 00:00;"
                            + contentHolder.editText2.getEditableText()
                            .toString() + ";" + "}");
                }
                // 发送获取部品库存查询报表请求
                if (bundle.getInt("selected_child_fun_item", 1) == 1)
                    params.put("s", "{"
                            + 30
                            + ";"
                            + contentHolder.editText1.getEditableText()
                            .toString()
                            + " 00:00;"
                            + contentHolder.editText2.getEditableText()
                            .toString() + ";" + "}");
                // 发送获取库存报警信息报表请求
                if (bundle.getInt("selected_child_fun_item", 1) == 2)
                    params.put("s", "{"
                            + 31
                            + ";"
                            + contentHolder.editText1.getEditableText()
                            .toString()
                            + " 00:00;"
                            + contentHolder.editText2.getEditableText()
                            .toString() + ";" + "}");
                // 发送获取出库计划完成情况报表请求
                if (bundle.getInt("selected_child_fun_item", 1) == 3)
                    params.put("s", "{"
                            + 32
                            + ";"
                            + contentHolder.editText1.getEditableText()
                            .toString()
                            + " 00:00;"
                            + contentHolder.editText2.getEditableText()
                            .toString() + ";" + "}");
                // 发送获取出库分录报表请求
                if (bundle.getInt("selected_child_fun_item", 1) == 4)
                    params.put("s", "{"
                            + 44
                            + ";"
                            + contentHolder.editText1.getEditableText()
                            .toString()
                            + " 00:00;"
                            + contentHolder.editText2.getEditableText()
                            .toString() + ";" + "}");
                // 发送获取入库分录报表请求
                if (bundle.getInt("selected_child_fun_item", 1) == 5)
                    params.put("s", "{"
                            + 45
                            + ";"
                            + contentHolder.editText1.getEditableText()
                            .toString()
                            + " 00:00;"
                            + contentHolder.editText2.getEditableText()
                            .toString() + ";" + "}");
                // 发送获取退货分录报表请求
                if (bundle.getInt("selected_child_fun_item", 1) == 6)
                    params.put("s", "{"
                            + 46
                            + ";"
                            + contentHolder.editText1.getEditableText()
                            .toString()
                            + " 00:00;"
                            + contentHolder.editText2.getEditableText()
                            .toString() + ";" + "}");
                // 发送获取入库明细报表请求
                if (bundle.getInt("selected_child_fun_item", 1) == 7)
                    params.put("s", "{"
                            + 48
                            + ";"
                            + contentHolder.editText1.getEditableText()
                            .toString()
                            + " 00:00;"
                            + contentHolder.editText2.getEditableText()
                            .toString() + ";" + "}");
            }
            /**
             * 交接单消息请求
             */
            if (bundle.getInt("selected_fun_index", 1) == 7) {
                // 全部交接单
                if (bundle.getInt("selected_child_fun_item", 1) == 0)
                    params.put("s", "{"
                            + 49
                            + ";"
                            + contentHolder.editText1.getEditableText()
                            .toString()
                            + " 00:00;"
                            + contentHolder.editText2.getEditableText()
                            .toString() + " 23:59;" + "}");
                // 未传回交接单
                if (bundle.getInt("selected_child_fun_item", 1) == 1)
                    params.put("s", "{"
                            + 50
                            + ";"
                            + contentHolder.editText1.getEditableText()
                            .toString()
                            + " 00:00;"
                            + contentHolder.editText2.getEditableText()
                            .toString() + " 23:59;" + "}");
                // 已传回结交单
                if (bundle.getInt("selected_child_fun_item", 1) == 2)
                    params.put("s", "{"
                            + 51
                            + ";"
                            + contentHolder.editText1.getEditableText()
                            .toString()
                            + " 00:00;"
                            + contentHolder.editText2.getEditableText()
                            .toString() + " 23:59;" + "}");
            }

            new MyThread(serverUrl, myHandler, object).start();
        } catch (JSONException e) {
            Toast.makeText(InfomationListActivity.this, R.string.response_unformat, Toast.LENGTH_LONG).show();
        }
    }

    public void confirmItem(JSONObject object) {
        try {
            SharedPreferences preferences = getSharedPreferences(
                    "system_params", MODE_PRIVATE);
            object.put("module", bundle.getString("selected_fun_index_name", ""));
            object.put("operation", "confirm");
            object.put("username", preferences.getString("username", "").split(",")[0]);
            new MyThread(serverUrl, myHandler, object).start();
        } catch (JSONException e) {
            Toast.makeText(InfomationListActivity.this, R.string.response_unformat, Toast.LENGTH_LONG).show();
        }
    }

    // 处理网络返回数据
    public ArrayList<String[]> progressData(String data) {
        ArrayList<String[]> result = new ArrayList<String[]>();
        String[] items = data.substring(1, data.length() - 1).split("\\},\\{");
        for (int i = 0; i < items.length; i++) {
            String[] temp = items[i].split(";");
            // 为数据添加序号
            if (bundle.getInt("selected_fun_index", 1) == 5
                    || bundle.getInt("selected_fun_index", 1) == 7)
                temp = Constants.addToStringArray(temp, String.valueOf(i + 1),
                        0);

            result.add(temp);
        }
        return result;
    }

    // 数据过滤
    public List<String[]> filterData(List<String[]> data, String s) {
        List<String[]> result = new ArrayList<String[]>();
        for (String[] item : data) {
            for (String temp : item) {
                if (temp.contains(s)) {
                    result.add(item);
                    break;
                }
            }
        }
        return result;
    }

    @Override
    protected void onPause() {
        unregisterReceiver(receiver);
        super.onPause();
    }

    @Override
    protected void onResume() {
        //downloadData();
        /**
         * 注册广播接收者，用于更新时间
         */
        receiver = new BootBroadcastReceiver() {
            @Override
            public void updateTime() {
                titleHolder.curSystemTime.setText(getResources().getString(
                        R.string.cur_time_promte)
                        + GetNowTime.getHour()
                        + ":"
                        + ((GetNowTime.getMinute() < 10) ? ("0" + GetNowTime
                        .getMinute()) : GetNowTime.getMinute()));
            }

            @Override
            public void updateData() {
            }
        };
        IntentFilter filter = new IntentFilter(
                "android.intent.action.TIME_TICK");
        registerReceiver(receiver, filter);
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 根据data数据刷新数据
     *
     * @param data
     */
    public void refreshData(ArrayList<String[]> data) {
        this.tableData = data;
        tableView.definedSetChanged();
    }

    /**
     * @return the titleVale
     */
    public String[] getTitleVale() {
        return titleVale;
    }

    /**
     * @param titleVale the titleVale to set
     */
    public void setTitleVale(String[] titleVale) {
        this.titleVale = titleVale;
    }

    /**
     * @return the tableData
     */
    public List<String[]> getTableData() {
        return tableData;
    }

    /**
     * @param tableData the tableData to set
     */
    public void setTableData(List<String[]> tableData) {
        this.tableData = tableData;
    }

    /**
     * @return the onItemClickListener
     */
    public OnItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    /**
     * @param onItemClickListener the onItemClickListener to set
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}