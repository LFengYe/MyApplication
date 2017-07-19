package com.cn.wms_system_new.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
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

public class ReportDataActivity extends Activity {
    public static final int REQUEST_DIALOG = 2;

    private TitleViewHolder titleHolder;
    private ContentViewHolder contentHolder;
    private HorizontalScrollView horizontalScrollView;
    //    private SideBar sideBar;
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
    private String[] detailTitleValue;

    private JSONObject titles;
    private JSONObject detailTitles;
    private String[] detailPrimary;

    /**
     * 表格数据
     */
    private List<String[]> tableData;
    //private JSONArray objects;

    private List<String[]> reportData;
    private int reportDataSize;
    private JSONArray reportObjects;

    private int pageSize = 30;
    private int pageIndex = 1;

    /**
     * 过滤后的表格数据
     */
    private List<String[]> filterData;
    //private JSONArray filterObjects;
    /**
     * 数据过滤标志
     */
    private boolean filterFlag;

    private int selectedIndex = -1;
    private Bundle bundle;

    private boolean requiredDate = false;
    private boolean isDetailList = false;
    private boolean requestDetail = false;
    private int textSize = Constants.TEXTSIZE_INIT;

    //region 各个点击事件
    private OnClickListener onClickListener = new OnClickListener() {

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
                    if (isDetailList) {
                        isDetailList = false;
                        downloadData();
                    } else {
                        finish();
                    }
                    break;
                }
                case R.id.refresh: {
                    /**
                     * 如果设置的为标题栏刷新按钮
                     */
                    requiredDate = false;
                    isDetailList = false;
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

                    dialog.setOnDismissListener(new OnDismissListener() {
                        // 对话框消失事件
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            DatePicker picker = ((DatePickerDialog) dialog)
                                    .getDatePicker();
                            years = picker.getYear();
                            month = picker.getMonth() + 1;
                            day = picker.getDayOfMonth();

                            if (editTextIndex == 1)
                                contentHolder.editText1.setText(composeDate(years, month, day));
                            if (editTextIndex == 2)
                                contentHolder.editText2.setText(composeDate(years, month, day));

                            requiredDate = true;
                            isDetailList = false;
                            downloadData();
                        }
                    });
                    dialog.show();
                    break;
                }
            }
        }
    };
    //endregion

    private TableView.ButtonClickInterFace btnClick = new TableView.ButtonClickInterFace() {
        @Override
        public void btnClick(int position) {
            selectedIndex = position;

        }

        @Override
        public void sortFinish(final String titleName) {
            JSONObject title = isDetailList ? detailTitles : titles;
            Iterator iterator = title.entrySet().iterator();
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
                    break;
                }
            }
        }
    };


    private MyHandler operateHandler = new MyHandler(this) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            SResponse response = (SResponse) msg.obj;
            switch (msg.what) {
                case MyThread.RESPONSE_SUCCESS:
                    downloadData();
                    Toast.makeText(ReportDataActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
                case MyThread.RESPONSE_UNFINISH:
                    showInputDialog();
                    Toast.makeText(ReportDataActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //region 消息处理程序
    private MyHandler dateHandler = new MyHandler(this) {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MyThread.RESPONSE_SUCCESS:
                    SResponse response = (SResponse) msg.obj;

                    if (!TextUtils.isEmpty(response.getData()) &&
                            !(response.getData().compareTo("null") == 0)) {

                        if (requestDetail) isDetailList = true;
                        else isDetailList = false;
                        //状态为0, 返回数据不为空
                        JSONObject object = JSONObject.parseObject(response.getData(), Feature.OrderedField);
                        if (JSONObject.parseObject(object.getString("titles"), Feature.OrderedField) != null)
                            titles = JSONObject.parseObject(object.getString("titles"), Feature.OrderedField);
                        if (JSONObject.parseObject(object.getString("detailTitles"), Feature.OrderedField) != null)
                            detailTitles = JSONObject.parseObject(object.getString("detailTitles"), Feature.OrderedField);
                        if (!TextUtils.isEmpty(object.getString("detailPrimary"))) {
                            detailPrimary = object.getString("detailPrimary").split(",");
                        }
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

                        if (detailTitles != null && detailTitles.size() > 0) {
                            detailTitleValue = new String[detailTitles.size()];
                            Iterator iterator = detailTitles.entrySet().iterator();
                            int index = 0;
                            while (iterator.hasNext()) {
                                Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                                detailTitleValue[index] = entry.getValue().split(",")[0];
                                index++;
                            }
                        }
                        /**
                         * 生成数据
                         */
                        //objects = object.getJSONArray("datas");
                        reportObjects = object.getJSONArray("datas");
                        tableData = new ArrayList<>();
                        reportData = new ArrayList<>();
                        if (reportObjects != null) {
                            if (filterFlag) {
                                contentHolder.editText3.setText("");
                            }
                            reportDataSize = reportObjects.size();
                            JSONObject title = isDetailList ? detailTitles : titles;
                            for (int i = 0; i < reportDataSize; i++) {
                                JSONObject obj = reportObjects.getJSONObject(i);
                                Iterator iterator = title.entrySet().iterator();
                                int tmp = 0;
                                String[] data = new String[title.size()];
                                while (iterator.hasNext()) {
                                    Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
                                    data[tmp] = (null == obj.getString(entry.getKey())) ? ("") : (obj.getString(entry.getKey()));
                                    tmp++;
                                }
                                reportData.add(data);
                            }

                            if (reportDataSize > pageSize) {
                                tableData.addAll(reportData.subList(0, pageSize));

                            } else {
                                tableData.addAll(reportData);
                            }
                        }

                        setTableViewCom(titleVale, tableData);

                    } else {
                        //状态为0, 返回数据为空
                        //downloadData();
                        Toast.makeText(ReportDataActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                    }

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
                    filterData(reportData, s.toString());
                    pageIndex = 1;
                    filterFlag = true;
                    //tableView.refreshData(filterData);
                }
            } else {
                pageIndex = 1;
                filterFlag = false;
                //tableView.refreshData(tableData);
            }
            refreshData();
        }
    };
    //endregion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        getWindow().setFlags(Constants.FLAG_HOMEKEY_DISPATCHED,
                Constants.FLAG_HOMEKEY_DISPATCHED);
        setContentView(R.layout.activity_report_data);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.layout_title_bar);

        initComponents();
        initParams();
        setComponents();
        downloadData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_DIALOG:
                if (resultCode == RESULT_OK)
                    downloadData();
                break;
        }
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
        //tableView.setOnItemClickListener(onItemClickListener);
        tableView.setBtnClick(btnClick);

        horizontalScrollView = (HorizontalScrollView) findViewById(R.id.scrollview);
        horizontalScrollView.removeAllViews();
        horizontalScrollView.addView(tableView);
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
//        contentHolder.editText1 = (EditText) findViewById(R.id.editText1);
//        contentHolder.textView2 = (TextView) findViewById(R.id.textView2);
        contentHolder.editText2 = (EditText) findViewById(R.id.editText2);
        contentHolder.textView3 = (TextView) findViewById(R.id.textView3);
        contentHolder.editText3 = (ClearEditText) findViewById(R.id.editText3);
        //contentHolder.textView4 = (TextView) findViewById(R.id.textview4);
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

        contentHolder.textView1.setText(R.string.report_end_time);

        contentHolder.editText2.setText(composeDate(GetNowTime.getYear(),
                GetNowTime.getMonth() + 1, GetNowTime.getDay()));
        contentHolder.editText2.setFocusable(false);
        contentHolder.editText2.setOnClickListener(onClickListener);


        contentHolder.textView3.setText(R.string.quick_search);
        contentHolder.editText3.setHint(R.string.list_search_promte);
        contentHolder.editText3.addTextChangedListener(watcher);

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

        findViewById(R.id.previout_page).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                pageIndex--;
                refreshData();
                /*
                tableData.clear();
                tableData.addAll(reportData.subList((pageIndex - 1) * pageSize, pageIndex * pageSize));
                tableView.refreshData(tableData);
                */
            }
        });

        findViewById(R.id.next_page).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                pageIndex++;
                refreshData();
                /*
                int countPage = (reportDataSize % pageSize == 0) ? (reportDataSize / pageSize) : (reportDataSize / pageSize + 1);
                if (pageIndex > countPage) {
                    Toast.makeText(ReportDataActivity.this, R.string.last_page_promote, Toast.LENGTH_LONG).show();
                    return;
                }
                if (pageIndex < countPage) {
                    tableData.clear();
                    tableData.addAll(reportData.subList((pageIndex - 1) * pageSize, pageIndex * pageSize));
                } else if (pageIndex == countPage) {
                    tableData.clear();
                    tableData.addAll(reportData.subList((pageIndex - 1) * pageSize, reportDataSize - 1));
                }
                tableView.refreshData(tableData);
                */
            }
        });
    }

    /**
     * 初始化参数
     */
    public void initParams() {
        bundle = getIntent().getExtras();
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
     * 向服务器发送请求
     */
    public void downloadData() {
        requestDetail = false;

        if (bundle.getString("funName").compareTo("报表查询") == 0) {
            JSONObject object = new JSONObject();
            object.put("module", bundle.getString("module", ""));
            object.put("operation", "create");
            object.put("type", "create");
            if (bundle.getString("module", "").compareTo("报检信息查询") == 0) {
                JSONObject dateObj = new JSONObject();
                //dateObj.put("start", contentHolder.editText1.getEditableText().toString() + " 00:00");
                dateObj.put("end", contentHolder.editText2.getEditableText().toString() + " 23:59");
                object.put("rely", dateObj.toJSONString());
            } else {
                //object.put("start", contentHolder.editText1.getEditableText().toString() + " 00:00");
                object.put("end", contentHolder.editText2.getEditableText().toString() + " 23:59");
            }
            new MyThread(dateHandler, object, bundle.getString("action")).start();
        }
    }

    public void inspection(JSONObject object) {
        object.put("module", bundle.getString("module", ""));
        object.put("operation", "inspection");
        object.put("type", "app");
        new MyThread(operateHandler, object, bundle.getString("action")).start();
    }

    public void confirmItem(JSONObject object) {
        object.put("module", bundle.getString("funName", ""));
        object.put("operation", "confirm");
        new MyThread(operateHandler, object, bundle.getString("action")).start();
    }

    // 数据过滤
    public void filterData(List<String[]> data, String s) {
        if (filterData != null) {
            filterData.clear();
        } else {
            filterData = new ArrayList<>();
        }
        /*
        if (filterObjects != null) {
            filterObjects.clear();
        } else {
            filterObjects = new JSONArray();
        }
        */

        for (int i = 0; i < data.size(); i++) {
            String[] item = data.get(i);
            for (String str : item) {
                if (str.contains(s)) {
                    filterData.add(item);
                    //filterObjects.add(objects.get(i));
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
        //downloadData();
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

    private void showInputDialog() {
        final EditText editText = new EditText(ReportDataActivity.this);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ReportDataActivity.this);
        builder.setTitle(R.string.input_remark_promote).setView(editText);
        builder.setPositiveButton(R.string.setting_confirm, null);

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String remarkInfo = editText.getText().toString();
                if (TextUtils.isEmpty(remarkInfo)) {
                    Toast.makeText(ReportDataActivity.this, R.string.remark_empty_promote, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    /*
                    JSONObject object;
                    if (filterFlag) {
                        object = filterObjects.getJSONObject(selectedIndex);
                    } else {
                        object = objects.getJSONObject(selectedIndex);
                    }
                    object.put("remark", remarkInfo);
                    confirmItem(object);
                    */
                    dialog.dismiss();
                }
            }
        });
    }

    private void showInspectionDialog() {
        final EditText editText = new EditText(ReportDataActivity.this);
        AlertDialog.Builder builder =
                new AlertDialog.Builder(ReportDataActivity.this);
        builder.setTitle(R.string.input_reject_promote).setView(editText);
        builder.setPositiveButton(R.string.accept, null);
        builder.setNegativeButton(R.string.reject, null);

        final AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                JSONObject object;
                if (filterFlag) {
                    object = filterObjects.getJSONObject(selectedIndex);
                } else {
                    object = objects.getJSONObject(selectedIndex);
                }
                object.put("inspectionResult", "合格");
                JSONObject obj = new JSONObject();
                obj.put("item", object);
                inspection(obj);
                */
                dialog.dismiss();
            }
        });

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String remarkInfo = editText.getText().toString();
                if (TextUtils.isEmpty(remarkInfo)) {
                    Toast.makeText(ReportDataActivity.this, R.string.reject_empty_promote, Toast.LENGTH_LONG).show();
                    return;
                } else {
                    /*
                    JSONObject object;
                    if (filterFlag) {
                        object = filterObjects.getJSONObject(selectedIndex);
                    } else {
                        object = objects.getJSONObject(selectedIndex);
                    }
                    object.put("inspectionResult", "不合格");
                    object.put("inspectionReportListRemark", remarkInfo);

                    JSONObject obj = new JSONObject();
                    obj.put("item", object);
                    inspection(obj);
                    */
                    dialog.dismiss();
                }
            }
        });
    }

    /**
     * 根据data数据刷新数据
     */
    public void refreshData() {

        if (pageIndex < 1) {
            pageIndex = 1;
            Toast.makeText(ReportDataActivity.this, R.string.first_page_promote, Toast.LENGTH_LONG).show();
            return;
        }

        if (!filterFlag) {
            int countPage = (reportData.size() % pageSize == 0) ? (reportData.size() / pageSize) : (reportData.size() / pageSize + 1);

            if (pageIndex > countPage) {
                pageIndex = countPage;
                Toast.makeText(ReportDataActivity.this, R.string.last_page_promote, Toast.LENGTH_LONG).show();
                return;
            }
            if (pageIndex < countPage) {
                tableData.clear();
                tableData.addAll(reportData.subList((pageIndex - 1) * pageSize, pageIndex * pageSize));
            } else if (pageIndex == countPage) {
                tableData.clear();
                tableData.addAll(reportData.subList((pageIndex - 1) * pageSize, reportData.size() - 1));
            }
            /*
            if (reportData.size() > pageSize * pageIndex) {
                tableData.clear();
                tableData.addAll(reportData.subList((pageIndex - 1) * pageSize, pageIndex * pageSize));
            } else {
                tableData.clear();
                tableData.addAll(reportData.subList((pageIndex - 1) * pageSize, reportData.size()));
            }
            */
        } else {
            int countPage = (filterData.size() % pageSize == 0) ? (filterData.size() / pageSize) : (filterData.size() / pageSize + 1);
            if (pageIndex > countPage) {
                pageIndex = countPage;
                Toast.makeText(ReportDataActivity.this, R.string.last_page_promote, Toast.LENGTH_LONG).show();
                return;
            }
            if (pageIndex < countPage) {
                tableData.clear();
                tableData.addAll(filterData.subList((pageIndex - 1) * pageSize, pageIndex * pageSize));
            } else if (pageIndex == countPage) {
                tableData.clear();
                tableData.addAll(filterData.subList((pageIndex - 1) * pageSize, filterData.size() - 1));
            }
            /*
            if (filterData.size() > pageSize * pageIndex) {
                tableData.clear();
                tableData.addAll(filterData.subList((pageIndex - 1) * pageSize, pageIndex * pageSize));
            } else {
                tableData.clear();
                tableData.addAll(filterData.subList((pageIndex - 1) * pageSize, filterData.size()));
            }
            */
        }

        tableView.refreshData(tableData);
    }
}