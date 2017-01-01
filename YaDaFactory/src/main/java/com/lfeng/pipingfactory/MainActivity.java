package com.lfeng.pipingfactory;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lfeng.pipingfactory.bean.Data;
import com.lfeng.pipingfactory.bean.LoginBean;
import com.lfeng.pipingfactory.bean.OtherData;
import com.lfeng.pipingfactory.bean.RowData;
import com.lfeng.pipingfactory.bean.SelectData;
import com.lfeng.pipingfactory.bean.Version;
import com.lfeng.pipingfactory.bean.XlData;
import com.lfeng.pipingfactory.util.AutoUpdate;
import com.lfeng.pipingfactory.util.HttpUtil;
import com.lfeng.pipingfactory.util.JsonUtil;
import com.lfeng.pipingfactory.util.NetUtil;
import com.lfeng.pipingfactory.util.PreferenceUtil;
import com.lfeng.pipingfactory.view.MyAutoCompleteTextView;
import com.lfeng.pipingfactory.view.MyDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {
    private static final int ACTIVITY_RESULT_HISTORY_LIST = 0;

    private LinearLayout ll_head;
    private LinearLayout ll_content;
    private LinearLayout ll_total;
    private TextView tv_title;
    private EditText et_search;
    private LinearLayout guanShuSelectLayout;
    private RadioGroup guanShuSelectGroup;
    private RadioButton guanShuYes;
    private RadioButton guanShuNo;

    private ArrayList<LinearLayout> ll_heads;
    private ArrayList<LinearLayout> ll_contents;
    private JSONArray json;
    private String UserName;
    private Dialog dialog;
    private int status;
    private int orderId;
    private int stationId;
    private int isGuanShu;
    private Button bt_search;
    private Button bt_finish;
    private boolean select_state = false;
    private HashMap<String, String> confirm_content;
    private String[] items;
    private String[] mSpecifications;
    private String data_head = "{\"fieldName\":\"Title\",\"fieldValue\":\"产品信息\",\"childRowNum\":3,\"textHorizon\":true,\"width\":1,\n" +
            " \"rowData1\":[" +
            "{\"fieldName\":\"ProductZl\", \"fieldValue\":\"生成指令\", \"viewType\":1,\"fieldType\":3,\"childRowNum\":1,\"textHorizon\":true,\"width\":1}," +
            "{\"fieldName\":\"ProductZlValue\", \"fieldValue\":\"\", \"viewType\":1,\"fieldType\":3,\"childRowNum\":1,\"textHorizon\":true,\"width\":3}" +
            "]," +
            "\"rowData2\":[" +
            "{\"fieldName\":\"ProductName\", \"fieldValue\":\"产品名称/图号\", \"viewType\":1,\"fieldType\":3,\"childRowNum\":1,\"textHorizon\":true,\"width\":1}," +
            "{\"fieldName\":\"ProductNameValue\", \"fieldValue\":\"\", \"viewType\":1,\"fieldType\":3,\"childRowNum\":1,\"textHorizon\":true,\"width\":3}" +
            "]," +
            "\"rowData3\":[" +
            "{\"fieldName\":\"CustomerName\", \"fieldValue\":\"客户名称/计划数量\", \"viewType\":1,\"fieldType\":3,\"childRowNum\":1,\"textHorizon\":true,\"width\":1}," +
            "{\"fieldName\":\"CustomerNameValue\", \"fieldValue\":\"\", \"viewType\":1,\"fieldType\":3,\"childRowNum\":1,\"textHorizon\":true,\"width\":3}" +
            "]" +
            "}";
    private String data_xiaLiao = "{\"fieldName\":\"Baiting\",\"fieldValue\":\"下料\",\"viewType\":1,\"fieldType\":3,\"childRowNum\":4,\"textHorizon\":true,\"width\":1," +
            " \"rowData1\":[" +
            "{\"fieldName\":\"BnilongName\",\"fieldValue\":\"尼龙管规格/批次\",\"viewType\":1,\"fieldType\":3,\"childRowNum\":1,\"textHorizon\":true,\"width\":1}," +
            "{\"fieldName\":\"BnilongValue\",\"fieldValue\":\"\",\"viewType\":4,\"fieldType\":2,\"childRowNum\":1,\"textHorizon\":true,\"width\":3}]," +
            " \"rowData2\":[" +
            "{\"fieldName\":\"BshoujianName\",\"fieldValue\":\"首件/末件\",\"viewType\":1,\"fieldType\":3,\"childRowNum\":1,\"textHorizon\":true,\"width\":2}," +
            "{\"fieldName\":\"BshoujianFValue\",\"fieldValue\":\"\",\"viewType\":2,\"fieldType\":1,\"childRowNum\":1,\"textHorizon\":true,\"width\":3}," +
            "{\"fieldName\":\"BshoujianEValue\",\"fieldValue\":\"\",\"viewType\":2,\"fieldType\":1,\"childRowNum\":1,\"textHorizon\":true,\"width\":3}]," +
            " \"rowData3\":[" +
            "{\"fieldName\":\"BcaibiaoName\",\"fieldValue\":\"采标数（条）\",\"viewType\":1,\"fieldType\":3,\"childRowNum\":1,\"textHorizon\":true,\"width\":1}," +
            "{\"fieldName\":\"BcaibiaoValue\",\"fieldValue\":\"0\",\"viewType\":2,\"fieldType\":3,\"childRowNum\":1,\"textHorizon\":true,\"width\":3}]," +
            " \"rowData4\":[" +
            "{\"fieldName\":\"BcaozuoName\",\"fieldValue\":\"操作者\",\"viewType\":1,\"fieldType\":3,\"childRowNum\":1,\"textHorizon\":true,\"width\":1}," +
            "{\"fieldName\":\"BcaozuoValue\",\"fieldValue\":\"\",\"viewType\":5,\"fieldType\":3,\"childRowNum\":1,\"textHorizon\":true,\"width\":3}]" +
            "}";

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId == R.id.guan_shu_select_yes) {
                switchGuanShuState(1);
            }
            if (checkedId == R.id.guan_shu_select_no) {
                switchGuanShuState(0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();
        JSONObject object = new JSONObject();
        object.put("updateType", 1);
        new GetUpdateInfoTask().execute(object);
    }

    private void initView() {
        ll_head = (LinearLayout) findViewById(R.id.ll_head);
        ll_content = (LinearLayout) findViewById(R.id.ll_content);
        ll_total = (LinearLayout) findViewById(R.id.ll_total);
        tv_title = (TextView) findViewById(R.id.tv_title);
        guanShuYes = (RadioButton) findViewById(R.id.guan_shu_select_yes);
        guanShuNo = (RadioButton) findViewById(R.id.guan_shu_select_no);
        guanShuSelectLayout = (LinearLayout) findViewById(R.id.guan_shu_select_layout);
        guanShuSelectGroup = (RadioGroup) findViewById(R.id.guan_shu_select_group);
        et_search = (EditText) findViewById(R.id.et_search);
        bt_search = (Button) findViewById(R.id.bt_search);
        bt_finish = (Button) findViewById(R.id.btn_finish);
        ll_heads = new ArrayList<>();
        ll_contents = new ArrayList<>();
        json = new JSONArray();
        confirm_content = new HashMap<>();
    }

    private void initData() {
        Intent intent = getIntent();
        String loginData = intent.getStringExtra("data");
        UserName = intent.getStringExtra("account");
        status = intent.getIntExtra("status", 0);
        isGuanShu = 0;

        mSpecifications = getResources().getStringArray(R.array.mSpecifications);
        items = getResources().getStringArray(R.array.mColors);

//        System.out.println(items.length);

        String head = null;
        String content = null;
        String head_name = null;
        if (status == 0) {
            XlData xlData = JSON.parseObject(loginData, XlData.class);
            head = xlData.getProductLineStructJson();
            content = xlData.getStationStructJson();
            head_name = xlData.getProductLineShort();
            orderId = xlData.getOrderId();
            isGuanShu = xlData.getIsGuanShu();
            changeViewWithGuanShuState(isGuanShu);
            bt_search.setText(R.string.history_order);
            bt_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    historyOrderList();
                }
            });
            guanShuSelectLayout.setVisibility(View.VISIBLE);
            getUserInfo();
        } else if (status == 1) {
            OtherData otherData = JSON.parseObject(loginData, OtherData.class);
            head = data_head;
            content = otherData.getStationStructJson();
            stationId = otherData.getStationId();
            head_name = otherData.getStationName();
            bt_search.setText(R.string.select);
            bt_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    select(v);
                }
            });
            guanShuSelectLayout.setVisibility(View.GONE);
        } else if (status == -99) {
            System.out.println("loginData:" + loginData);
            if (loginData != null) {
                XlData xlData = JSON.parseObject(loginData, XlData.class);
                head = xlData.getProductLineStructJson();
                content = xlData.getStationStructJson();
                head_name = xlData.getProductLineShort();
                orderId = xlData.getOrderId();
            } else {
                head = data_head;
                content = data_xiaLiao;
                head_name = getString(R.string.auto_xiaLiao);
                orderId = -99;
            }
            bt_search.setText(R.string.auto_xiaLiao_order_select);
            bt_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    couldAutoXiaLiaoOrderList();
                }
            });
            guanShuSelectLayout.setVisibility(View.GONE);
            getUserInfo();
        }
        confirm_content.put("confirm_content", content);
        confirm_content.put("confirm_head_name", head_name);
        createView(head, content, head_name);
    }

    private void historyOrderList() {
        Intent intent = new Intent();
        intent.putExtra("userName", UserName);
        intent.putExtra("stationId", stationId);
        intent.setClass(this, HistoryOrderActivity.class);
        startActivityForResult(intent, ACTIVITY_RESULT_HISTORY_LIST);
    }

    private void couldAutoXiaLiaoOrderList() {
        Intent intent = new Intent();
        intent.setClass(this, AutoXiaLiaoSelectActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case ACTIVITY_RESULT_HISTORY_LIST: {
                if (resultCode == RESULT_OK) {
                    int orderId = data.getIntExtra("orderId", 0);
                }
                break;
            }
        }
    }

    private void createView(String head, String content, String head_name) {
        Data data_head = JsonUtil.getDate(head);
        Data data_content = JsonUtil.getDate(content);
        tv_title.setText(head_name + data_content.getFieldValue());
        ll_contents = createTotalRow(data_content);
        ll_heads = createTotalRow(data_head);
        LinearLayout.LayoutParams LP_FW = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        System.out.println("head size:" + ll_heads.size());
        for (int i = 0; i < ll_heads.size(); i++) {
            ll_head.addView(ll_heads.get(i), LP_FW);
        }
        /*
        LinearLayout layout_sub_Lin = new LinearLayout(this);
        layout_sub_Lin.setMinimumHeight(50);
        layout_sub_Lin.setOrientation(LinearLayout.HORIZONTAL);
        ll_head.addView(layout_sub_Lin);
        */

        for (int j = 0; j < ll_contents.size(); j++) {
            ll_content.addView(ll_contents.get(j), LP_FW);
        }
        ll_head.setBackgroundResource(R.drawable.shape5);
        ll_content.setBackgroundResource(R.drawable.shape5);
    }

    /**
     * 初始化分行，放回行的集合
     *
     * @param data 总数据的描述
     * @return 行的集合
     */
    private ArrayList<LinearLayout> createTotalRow(Data data) {

        LinearLayout newSingleRL;
        ArrayList<LinearLayout> ll_contents = new ArrayList<>();
        for (int i = 0; i < data.getChildRowNum(); i++) {
            newSingleRL = generateSingleLayout(i, data);
            ll_contents.add(newSingleRL);
        }
        return ll_contents;
    }

    /**
     * 创建行
     *
     * @param row  第几行
     * @param data 相对的数据
     * @return 行的view (LinearLayout)
     */
    private LinearLayout generateSingleLayout(int row, Data data) {
        LinearLayout layout_sub_Lin = new LinearLayout(this);
        layout_sub_Lin.setOrientation(LinearLayout.HORIZONTAL);

        int RowWidth = data.getWidth();
        LinearLayout.LayoutParams LP_FW = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, RowWidth);

        for (int i = 0; i < data.getRowData().get(row).size(); i++) {

            int row_num = data.getRowData().get(row).get(i).getChildRowNum();
            String fieldValue = data.getRowData().get(row).get(i).getFieldValue();
            String fieldName = data.getRowData().get(row).get(i).getFieldName();
            int width = data.getRowData().get(row).get(i).getWidth();
            int viewFieldType = data.getRowData().get(row).get(i).getViewType();
            int fieldType = data.getRowData().get(row).get(i).getFieldType();


            if (viewFieldType == 1) {
                EditText et_false = getTextView(fieldValue, width);
                layout_sub_Lin.addView(et_false);
            } else if (viewFieldType == 2) {
                EditText et_true = getEditText(fieldName, fieldValue, width, fieldType);
                layout_sub_Lin.addView(et_true);
            } else if (viewFieldType == 3) {
                View getRadioGroup = getRadioGroup(fieldName, width, fieldType);
                layout_sub_Lin.addView(getRadioGroup);
            } else if (viewFieldType == 4) {
                View AutoCompleteTextView = getAutoCompleteTextView(fieldName, width, fieldType);
                layout_sub_Lin.addView(AutoCompleteTextView);
            } else if (viewFieldType == 5) {
                EditText et_true = getAutoEditText(fieldName, width, UserName);
                layout_sub_Lin.addView(et_true);
            } else if (viewFieldType == 6) {
//                System.out.println("fieldName:" + fieldName);
                View et_color = getColorText(fieldName, fieldValue, width, fieldType);
                layout_sub_Lin.addView(et_color);
            } else if (viewFieldType == 7) {
                View et_specifications = getSpecificationsText(fieldName, fieldValue, width, fieldType);
                layout_sub_Lin.addView(et_specifications);
            } else if (viewFieldType == 8) {
                View yaZhuangOrHuaXian = getYaZhuangOrHuaXian(fieldName, width, fieldType);
                layout_sub_Lin.addView(yaZhuangOrHuaXian);
            } else if (viewFieldType == 9) {
                View yaZhuangOrHuaXian = getYaZhuangOrHuaXianSort(fieldName, width, fieldType);
                layout_sub_Lin.addView(yaZhuangOrHuaXian);
            } else if (viewFieldType == 10) {
                View yaZhuangOrHuaXian = getHandWorkMould(fieldName, width, fieldType);
                layout_sub_Lin.addView(yaZhuangOrHuaXian);
            }


            if (row_num > 1) {
                Data data1 = data.getRowData().get(row).get(i);
                int row_total_width = getTotalwidth(data1, i);
                ArrayList<LinearLayout> ll_contents = createTotalRow(data1);
                LinearLayout layout_sub_Lin1 = new LinearLayout(this);
                layout_sub_Lin1.setOrientation(LinearLayout.VERTICAL);
                for (int j = 0; j < ll_contents.size(); j++) {
                    LinearLayout linearLayout = ll_contents.get(j);
                    layout_sub_Lin1.addView(linearLayout);
                }
                LinearLayout.LayoutParams LP_FW1 = new LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.MATCH_PARENT, row_total_width);
                layout_sub_Lin.addView(layout_sub_Lin1, LP_FW1);
            }
        }
        layout_sub_Lin.setLayoutParams(LP_FW);

        return layout_sub_Lin;
    }

    /**
     * 获得子布局的宽度总比例
     *
     * @param data 子布局的数据描述
     * @param i    相对于的数据
     * @return 宽度总比例
     */
    private int getTotalwidth(Data data, int i) {
        int row_width = 0;
        for (int k = 0; k < data.getRowData().get(i).size(); k++) {
            row_width = row_width + data.getRowData().get(i).get(k).getWidth();
        }
        return row_width;
    }

    /**
     * 创建EditText 不可输入的的EditText
     *
     * @param fieldValue 显示的名字
     * @param width      宽度比例
     * @return 返回EditText
     */
    private EditText getTextView(String fieldValue, float width) {
        LinearLayout.LayoutParams LP_WW2 = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, width);
        EditText et_false = (EditText) View.inflate(MainActivity.this, R.layout.et_false, null);
        et_false.setText(fieldValue);
        et_false.setLayoutParams(LP_WW2);
        return et_false;
    }

    /**
     * 创建EditText 可输入的的EditText
     *
     * @param filename  EditText的标记名字
     * @param width     宽度比例
     * @param fieldType 字段类型
     * @return 返回EditText
     */
    private EditText getEditText(final String filename, final String fieldValue, float width, final int fieldType) {
        LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, width);
        EditText et_true = (EditText) View.inflate(MainActivity.this, R.layout.et_true, null);
        et_true.setLayoutParams(LP_WW);
        if (!fieldValue.equals("")) {
            createJson(fieldValue, filename, fieldType);
        } else {
            createJson("-", filename, fieldType);
        }
        et_true.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                EditText view = (EditText) v;
                String fileValue = view.getText().toString();
                if (!hasFocus) {
                    if (TextUtils.isEmpty(fileValue))
                        createJson("-", filename, fieldType);
                    else createJson(fileValue, filename, fieldType);
                }
            }
        });
        return et_true;
    }

    /**
     * 创建EditText 自动填充可输入的的EditText
     *
     * @param filename 对应的名字
     * @param width    宽度
     * @param name     操作者（默认名字)
     * @return 返回EditText
     */
    private EditText getAutoEditText(String filename, float width, String name) {
        LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, width);
        EditText et_true = (EditText) View.inflate(MainActivity.this, R.layout.et_true, null);
        et_true.setText(name);
        et_true.setLayoutParams(LP_WW);
        int fieldType = 3;
        createJson(name, filename, fieldType);
        return et_true;
    }

    /**
     * 创建AutoCompleteTextView 自动匹配的TextView
     *
     * @param filename  对应的名字
     * @param width     宽度
     * @param fieldType 字段类型
     * @return 返回AutoCompleteTextView的view
     */
    private View getAutoCompleteTextView(final String filename, float width, final int fieldType) {
        LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, width);
        final View view = View.inflate(MainActivity.this, R.layout.auto_text, null);
        view.setLayoutParams(LP_WW);
        MyAutoCompleteTextView auto_text = (MyAutoCompleteTextView) view.findViewById(R.id.auto_text);

        final LinkedList<String> auto_content = getShareData(filename);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.auto_item, auto_content);
        auto_text.setAdapter(adapter);

        if (auto_content.size() > 0) {
            auto_text.setText(auto_content.get(0));
        }
        createJson(TextUtils.isEmpty(auto_text.getText()) ? "-" : auto_text.getText().toString(), filename, fieldType);
        auto_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    MyAutoCompleteTextView view = (MyAutoCompleteTextView) v;
                    String fieldValue = view.getText().toString();
                    if (!auto_content.contains(fieldValue) && !fieldValue.isEmpty()) {
                        auto_content.addFirst(fieldValue);
                    }
                    LinkedList<String> auto_content1 = cutData(auto_content, fieldValue);
                    String save_data = JSON.toJSONString(auto_content1, true);
                    PreferenceUtil.putString(filename, save_data, MainActivity.this);

                    if (TextUtils.isEmpty(fieldValue))
                        createJson("-", filename, fieldType);
                    else createJson(fieldValue, filename, fieldType);
                }
            }
        });
        return view;
    }

    /**
     * 创建RadioGroup 单选按钮的view
     *
     * @param filename  对应的名字
     * @param width     宽度
     * @param fieldType 字段类型
     * @return 返回RadioGroup的view
     */
    private View getRadioGroup(final String filename, float width, final int fieldType) {
        LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, width);
        View view = View.inflate(MainActivity.this, R.layout.radiogroup, null);
        view.setLayoutParams(LP_WW);
        RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
        final RadioButton rv_ok = (RadioButton) view.findViewById(R.id.rb_ok);
        String fieldValue = String.valueOf(rv_ok.isChecked());
        createJson(fieldValue, filename, fieldType);
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String fieldValue = String.valueOf(rv_ok.isChecked());
                createJson(fieldValue, filename, fieldType);
            }
        });
        return view;
    }

    private View getColorText(final String filename, String fieldValue, int width, final int fieldType) {
        LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, width);
        View view = View.inflate(MainActivity.this, R.layout.et_select_color, null);
        view.setLayoutParams(LP_WW);

        LinearLayout ll_color = (LinearLayout) view.findViewById(R.id.ll_color);
        final TextView tv_color = (TextView) view.findViewById(R.id.tv_color);
        final TextView tv_text = (TextView) view.findViewById(R.id.tv_color_text);
        System.out.println("filename:" + filename + ",status:" + status);
        if (status == 0 || status == -99) {
            if (fieldValue.equals("")) {
                fieldValue = "O";
                createJson(fieldValue, filename, fieldType);
                ll_color.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        final String[] letter = new String[1];
                        letter[0] = "红色";
                        builder.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                letter[0] = items[which];
                            }
                        });
                        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String num = letter[0];
                                String letter = getLetter(num);
                                tv_text.setText(num);
                                tv_color.setBackgroundColor(ContextCompat.getColor(MainActivity.this, getTextColor(letter)));
                                createJson(letter, filename, fieldType);
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        builder.show();
                        System.out.println(v.getId());
                    }
                });
            } else {
                tv_color.setBackgroundColor(ContextCompat.getColor(this, getTextColor(fieldValue)));
            }
        } else {
            if (!fieldValue.equals("")) {
                tv_color.setBackgroundColor(ContextCompat.getColor(this, getTextColor(fieldValue)));
            }
        }
        return view;
    }

    private View getSpecificationsText(final String fieldName, String fieldValue, int width, final int fieldType) {
        LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, width);
        View view = View.inflate(MainActivity.this, R.layout.et_select_specifications, null);
        LinearLayout ll_specifications = (LinearLayout) view.findViewById(R.id.ll_specifications);
        final TextView tv_specifications = (TextView) view.findViewById(R.id.tv_specifications);
        view.setLayoutParams(LP_WW);

        if (fieldValue.equals("")) {
            fieldValue = "无";
        }
        createJson(fieldValue, fieldName, fieldType);

        tv_specifications.setText(fieldValue);

        ll_specifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                final String[] letter = new String[1];

                // builder.setItems(items, new DialogInterface.OnClickListener()//列表对话框
                builder.setSingleChoiceItems(mSpecifications, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        letter[0] = mSpecifications[which];
                    }
                });
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String specifications = letter[0];
                        tv_specifications.setText(specifications);
                        createJson(specifications, fieldName, fieldType);
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });
        return view;

    }

    private View getYaZhuangOrHuaXian(final String filename, float width, final int fieldType) {
        LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, width);
        View view = View.inflate(MainActivity.this, R.layout.yazhuang_huaxian, null);
        view.setLayoutParams(LP_WW);
        RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
        RadioButton rb_none = (RadioButton) view.findViewById(R.id.rb_none);
        createJson(rb_none.getText().toString(), filename, fieldType);
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String fieldValue = "无";
                switch (checkedId) {
                    case R.id.rb_none: {
                        fieldValue = "无";
                        break;
                    }
                    case R.id.rb_yaZhuang: {
                        fieldValue = "冲压";
                        break;
                    }
                    case R.id.rb_huaXian: {
                        fieldValue = "划线";
                        break;
                    }
                }
                createJson(fieldValue, filename, fieldType);
            }
        });
        return view;
    }

    private View getYaZhuangOrHuaXianSort(final String filename, float width, final int fieldType) {
        LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, width);
        View view = View.inflate(MainActivity.this, R.layout.yazhuang_huaxian_sort, null);
        view.setLayoutParams(LP_WW);
        RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
        RadioButton rb_none = (RadioButton) view.findViewById(R.id.rb_none);
        createJson(rb_none.getText().toString(), filename, fieldType);
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String fieldValue = "无";
                switch (checkedId) {
                    case R.id.rb_none: {
                        fieldValue = "无";
                        break;
                    }
                    case R.id.rb_yaZhuang: {
                        fieldValue = "冲长划短";
                        break;
                    }
                    case R.id.rb_huaXian: {
                        fieldValue = "冲短划长";
                        break;
                    }
                }
                createJson(fieldValue, filename, fieldType);
            }
        });
        return view;
    }

    private View getHandWorkMould(final String filename, float width, final int fieldType) {
        LinearLayout.LayoutParams LP_WW = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, width);
        View view = View.inflate(MainActivity.this, R.layout.handwork_mould, null);
        view.setLayoutParams(LP_WW);
        RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
        final RadioButton rv_ok = (RadioButton) view.findViewById(R.id.rb_ok);
        String fieldValue = String.valueOf(rv_ok.isChecked());
        createJson(fieldValue, filename, fieldType);
        rg_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String fieldValue = String.valueOf(rv_ok.isChecked());
                createJson(fieldValue, filename, fieldType);
            }
        });
        return view;
    }

    private String getLetter(String item) {
        String letter = null;
        switch (item) {
            case "红色":
                letter = "A";
                break;
            case "橙色":
                letter = "B";
                break;
            case "黄色":
                letter = "C";
                break;
            case "绿色":
                letter = "D";
                break;
            case "蓝色":
                letter = "E";
                break;
            case "紫色":
                letter = "F";
                break;
            case "粉红色":
                letter = "G";
                break;
            case "浅黄色":
                letter = "H";
                break;
            case "天蓝色":
                letter = "J";
                break;
            case "乳白色":
                letter = "K";
                break;
            case "苹果绿":
                letter = "L";
                break;
            case "浅紫色":
                letter = "M";
                break;
            case "无颜色":
                letter = "O";
                break;
        }
        return letter;

    }

    private int getTextColor(String fieldValue) {
        int color;
        switch (fieldValue) {
            case "A":
                color = R.color.red;
                break;
            case "B":
                color = R.color.orange;
                break;
            case "C":
                color = R.color.yellow;
                break;
            case "D":
                color = R.color.green;
                break;
            case "E":
                color = R.color.blue;
                break;
            case "F":
                color = R.color.purple;
                break;
            case "G":
                color = R.color.pink;
                break;
            case "H":
                color = R.color.buff;
                break;
            case "J":
                color = R.color.skyBlue;
                break;
            case "K":
                color = R.color.white;
                break;
            case "L":
                color = R.color.appleGreen;
                break;
            case "M":
                color = R.color.cream;
                break;
            case "O":
                color = R.color.trans;
                break;
            default:
                color = R.color.trans;
                break;
        }
        return color;
    }

    private LinkedList<String> cutData(LinkedList<String> auto_content, String fieldValue) {
        int position = 0;
        if (auto_content.size() > 5) {
            auto_content.removeLast();
        }
        for (int i = 0; i < auto_content.size(); i++) {
            if (auto_content.get(i).equals(fieldValue)) {
                position = i;
            }
        }
        auto_content.remove(position);
        auto_content.addFirst(fieldValue);
        return auto_content;
    }

    private void createJson(String fileValue, String filename, int fieldType) {
        RowData rowData = new RowData();
        rowData.setFieldName(filename);
        rowData.setFieldValue(fileValue);
        rowData.setFieldType(fieldType);
        JSONObject json_text = (JSONObject) JSON.toJSON(rowData);
        if (isExistJson(filename) != -1) {
            json.set(isExistJson(filename), json_text);
        } else {
            json.add(json_text);
        }
    }

    private int isExistJson(String filename) {
        int num = -1;
        for (int j = 0; j < json.size(); j++) {
            if (json.get(j).toString().contains(filename)) {
                num = j;
                break;
            }
        }
        return num;
    }

    private LinkedList<String> getShareData(String filename) {
        String msg = PreferenceUtil.getString(filename, MainActivity.this);
        JSONArray jsonArr = JSON.parseArray(msg);

        LinkedList<String> list_data = new LinkedList<>();
        if (jsonArr != null) {
            for (int i = 0; i < jsonArr.size(); i++) {
                list_data.add(jsonArr.get(i).toString());
            }
        }
        return list_data;

    }

    public void ok(View v) {
        if (status == 1 && !select_state) {
            Toast.makeText(MainActivity.this, R.string.press_select, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NetUtil.checkNetworkConnection(MainActivity.this)) {
            Toast.makeText(MainActivity.this, R.string.Net_error, Toast.LENGTH_SHORT).show();
            return;
        }
        ll_total.requestFocus();

        System.out.println(json.toString());

        String cardNum = et_search.getText().toString();

        if (json.toString().contains("-") || cardNum.isEmpty()) {
            Toast.makeText(MainActivity.this, R.string.content_null, Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject inputOrder = new JSONObject();

        inputOrder.put("username", UserName);
        inputOrder.put("cardNum", cardNum);
        inputOrder.put("orderId", orderId);
        inputOrder.put("data", json.toString());
        inputOrder.put("isGuanShu", isGuanShu);

        new UploadDataAsyncTask().execute(inputOrder);
    }

    public void no(View v) {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void finish(View v) {
        if (status == 1 && !select_state) {
            Toast.makeText(MainActivity.this, R.string.press_select, Toast.LENGTH_SHORT).show();
            return;
        }
        if (!NetUtil.checkNetworkConnection(MainActivity.this)) {
            Toast.makeText(MainActivity.this, R.string.Net_error, Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject object = new JSONObject();
        object.put("orderId", orderId);
    }

    public void select(View v) {
        if (!NetUtil.checkNetworkConnection(MainActivity.this)) {
            Toast.makeText(MainActivity.this, R.string.Net_error, Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject getOrderInfo = new JSONObject();
        String cardNum = et_search.getText().toString();
        getOrderInfo.put("cardNum", cardNum);
        getOrderInfo.put("stationId", stationId);
        new GetOrderInfoAsyncTask().execute(getOrderInfo);
    }

    public void switchGuanShuState(int isGuanShu) {
        if (!NetUtil.checkNetworkConnection(MainActivity.this)) {
            Toast.makeText(MainActivity.this, R.string.Net_error, Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject guanShuJons = new JSONObject();
        guanShuJons.put("orderId", orderId);
        guanShuJons.put("stationId", stationId);
        guanShuJons.put("isGuanShu", isGuanShu);
        new OrderIsGuanShuTask().execute(guanShuJons);
    }

    public void changeViewWithGuanShuState(int isGuanShu) {
        guanShuSelectGroup.setOnCheckedChangeListener(null);
        if (isGuanShu == 1) {
            bt_finish.setVisibility(View.VISIBLE);
            guanShuYes.setChecked(true);
        } else if(isGuanShu == 0) {
//            bt_search.setVisibility(View.GONE);
            bt_finish.setVisibility(View.GONE);
            guanShuNo.setChecked(true);
        }
        guanShuSelectGroup.setOnCheckedChangeListener(onCheckedChangeListener);
    }

    public void getUserInfo() {
        System.out.println("getUserInfo");
        if (!NetUtil.checkNetworkConnection(MainActivity.this)) {
            Toast.makeText(MainActivity.this, R.string.Net_error, Toast.LENGTH_SHORT).show();
            return;
        }
        JSONObject object = new JSONObject();
        object.put("username", UserName);
        new GetUserInfoTask().execute(object);
    }

    class OrderIsGuanShuTask extends AsyncTask<JSONObject, String, String> {

        @Override
        protected void onPreExecute() {
            Point size = new Point();
            MainActivity.this.getWindowManager().getDefaultDisplay().getSize(size);
            int width = (int) (size.x * 0.25);
            int height = (int) (size.y * 0.15);
            String text = "查询数据中...";
            String type = "Load";
            dialog = new MyDialog(MainActivity.this, width, height, text, type);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject jsonObject = params[0];
            return HttpUtil.httpPost(HttpUtil.defaultHost + HttpUtil.orderIsGuanShuURL, jsonObject);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
            if (s.isEmpty()) {
                Toast.makeText(MainActivity.this, R.string.servlet_error, Toast.LENGTH_SHORT).show();
                return;
            }
            SelectData selectData = JSON.parseObject(s, SelectData.class);
            if (selectData.getStatus() == 0) {
                Toast.makeText(MainActivity.this, R.string.select_success, Toast.LENGTH_SHORT).show();
                ll_head.removeAllViews();
                ll_content.removeAllViews();
                select_state = true;
                String head = selectData.getData().getProductLineStructJson();
                String content = selectData.getData().getStationStructJson();
                String head_name = selectData.getData().getProductLineShort();
                orderId = selectData.getData().getOrderId();
                isGuanShu = selectData.getData().getIsGuanshu();
                json = new JSONArray();
                createView(head, content, head_name);
            } else {
                Toast.makeText(MainActivity.this, selectData.getMessage(), Toast.LENGTH_SHORT).show();
            }
            changeViewWithGuanShuState(isGuanShu);

        }
    }

    class GetOrderInfoAsyncTask extends AsyncTask<JSONObject, String, String> {
        @Override
        protected void onPreExecute() {
            Point size = new Point();
            MainActivity.this.getWindowManager().getDefaultDisplay().getSize(size);
            int width = (int) (size.x * 0.25);
            int height = (int) (size.y * 0.15);
            String text = "查询数据中...";
            String type = "Load";
            dialog = new MyDialog(MainActivity.this, width, height, text, type);
            dialog.show();
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject jsonObject = params[0];
            return HttpUtil.httpPost(HttpUtil.defaultHost + HttpUtil.getOrderInfoURL, jsonObject);
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            super.onPostExecute(s);
            if (s.isEmpty()) {
                Toast.makeText(MainActivity.this, R.string.servlet_error, Toast.LENGTH_SHORT).show();
                return;
            }
            SelectData selectData = JSON.parseObject(s, SelectData.class);
            if (selectData.getStatus() == 0) {
                Toast.makeText(MainActivity.this, R.string.select_success, Toast.LENGTH_SHORT).show();
                ll_head.removeAllViews();
                ll_content.removeAllViews();
                select_state = true;
                String head = selectData.getData().getProductLineStructJson();
                String content = selectData.getData().getStationStructJson();
                String head_name = selectData.getData().getProductLineShort();
                orderId = selectData.getData().getOrderId();
                isGuanShu = selectData.getData().getIsGuanshu();
                json = new JSONArray();
                createView(head, content, head_name);
            } else {
                Toast.makeText(MainActivity.this, selectData.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class GetUserInfoTask extends AsyncTask<JSONObject, String, String> {
        @Override
        protected void onPreExecute() {
            Point size = new Point();
            MainActivity.this.getWindowManager().getDefaultDisplay().getSize(size);
            int width = (int) (size.x * 0.25);
            int height = (int) (size.y * 0.15);
            String text = "查询数据中...";
            String type = "Load";
            dialog = new MyDialog(MainActivity.this, width, height, text, type);
            dialog.show();
            super.onPreExecute();

        }


        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject jsonObject = params[0];
            return HttpUtil.httpPost(HttpUtil.defaultHost + HttpUtil.getUserInfoURL, jsonObject);
        }

        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            super.onPostExecute(s);
            if (s.isEmpty()) {
                Toast.makeText(MainActivity.this, R.string.servlet_error, Toast.LENGTH_SHORT).show();
                return;
            }
            LoginBean userData = JSON.parseObject(s, LoginBean.class);
            if (userData.getStatus() == 0) {
                Toast.makeText(MainActivity.this, R.string.select_success, Toast.LENGTH_SHORT).show();
                OtherData userInfo = JSON.parseObject(userData.getData(), OtherData.class);
                stationId = userInfo.getStationId();
                System.out.println("stationId:" + stationId);
            } else {
                Toast.makeText(MainActivity.this, userData.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    class UploadDataAsyncTask extends AsyncTask<JSONObject, String, String> {

        @Override
        protected void onPreExecute() {
            Point size = new Point();
            MainActivity.this.getWindowManager().getDefaultDisplay().getSize(size);
            int width = (int) (size.x * 0.25);
            int height = (int) (size.y * 0.15);
            String text = "上传数据中...";
            String type = "Load";
            dialog = new MyDialog(MainActivity.this, width, height, text, type);
            dialog.show();
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject json_data = params[0];

            return HttpUtil.httpPost(HttpUtil.defaultHost + HttpUtil.inputOrderURL, json_data);
        }


        @Override
        protected void onPostExecute(String s) {
            dialog.dismiss();
            super.onPostExecute(s);
            if (s.isEmpty()) {
                Toast.makeText(MainActivity.this, R.string.servlet_error, Toast.LENGTH_SHORT).show();
                return;
            }
            LoginBean inputData = JSON.parseObject(s, LoginBean.class);
            if (inputData.getStatus() == -1) {
                Toast.makeText(MainActivity.this, inputData.getMessage(), Toast.LENGTH_SHORT).show();

            } else {
                select_state = false;
                Toast.makeText(MainActivity.this, R.string.upload_success, Toast.LENGTH_SHORT).show();
                ll_head.removeAllViews();
                ll_content.removeAllViews();
                String content;
                String head_name;
                String head;
                if (status == 0 || status == -99) {
                    XlData xlData = JSON.parseObject(inputData.getData(), XlData.class);
                    head = xlData.getProductLineStructJson();
                    content = xlData.getStationStructJson();
                    head_name = xlData.getProductLineShort();
                    orderId = xlData.getOrderId();
                    isGuanShu = xlData.getIsGuanShu();
                    changeViewWithGuanShuState(isGuanShu);
                } else {
                    head = data_head;
                    content = confirm_content.get("confirm_content");
                    head_name = confirm_content.get("confirm_head_name");
                }
                et_search.setText("");
                json = new JSONArray();
                createView(head, content, head_name);
            }
        }


    }

    class GetUpdateInfoTask extends AsyncTask<JSONObject, String, String> {

        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject object = params[0];
            return HttpUtil.httpPost(HttpUtil.defaultHost + HttpUtil.getNewestVersionURL, object);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.isEmpty()) {
                Toast.makeText(MainActivity.this, R.string.servlet_error, Toast.LENGTH_SHORT).show();
                return;
            }
            System.out.println("json:" + s);
            LoginBean data = JSON.parseObject(s, LoginBean.class);
            if (data.getStatus() == 0) {
                Version version = JSON.parseObject(data.getData(), Version.class);
                AutoUpdate update = new AutoUpdate(MainActivity.this);
                if (version.getVersionNumber() > update.getVersionName("com.lfeng.pipingfactory")) {
                    update.showUpDate(HttpUtil.defaultHost + version.getAppPathUrl(), version.getVersionNumberName());
                }
            }
        }
    }
}
