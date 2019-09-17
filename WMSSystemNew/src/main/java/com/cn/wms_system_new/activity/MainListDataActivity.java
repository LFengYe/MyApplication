package com.cn.wms_system_new.activity;

import android.app.Activity;
import android.app.AlertDialog;
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
import com.cn.wms_system_new.dialog.CustomerDialog;
import com.cn.wms_system_new.service.BootBroadcastReceiver;
import com.cn.wms_system_new.service.MyHandler;
import com.cn.wms_system_new.service.MyThread;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by LFeng on 2017/8/13.
 */

public class MainListDataActivity extends Activity {
    public static final int REQUEST_REMARK = 1;
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
     * 主表模型
     */
    private String[] titleVale;
    private JSONObject titles;
    private JSONArray mainObjects;
}
