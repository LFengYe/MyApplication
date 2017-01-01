package com.cn.wetrack.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cn.wetrack.R;
import com.cn.wetrack.activity.baidu.ShowHistoryBaidu;
import com.cn.wetrack.entity.History;
import com.cn.wetrack.entity.SResponse;
import com.cn.wetrack.util.AppUtils;
import com.cn.wetrack.util.HttpRequestClient;
import com.cn.wetrack.util.SProtocol;
import com.cn.wetrack.widgets.CustomProgressDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 轨迹时间选择
 */
@SuppressLint({"HandlerLeak", "SimpleDateFormat"})
public class SelectDate extends Activity {
    private int Switch = 0;
    private String date = "", endTime = "23:59:59", startTime = "00:00:00";
    private ImageView todayimg, yesterdayimg, TheDayBeforeYesterdayimg,
            AnHourageimg, customimg;
    private TextView todaytext, yesterdaytext, TheDayBeforeYesterdaytext,
            AnHouragetext, customtext, datetext, timetext;
    private LinearLayout todaylinear, yesterdaylinear, TDBYesterdaylinear,
            AnHourAgolinear, Customlinear;
    private RelativeLayout chosedateRe, chosetimeRe;
    private Button query;// 查询
    private ImageButton dateBackButton;//返回
    private SWApplication glob;
    private CustomProgressDialog progressDialog = null;
    private Calendar systemday = Calendar.getInstance();
    private PopupWindow Datepopwindow = null, Timepopwindow = null;//两个popwindow
    private ViewGroup mainview = null, selectdateview = null,
            selecttimeview = null;//几个布局
    private DatePicker Datepicker = null;// 日期控件
    private TimePicker Timepicker_start, Timepicker_end;//两个时间选择器
    private Button selectdate_ok, selectdate_cancel, selecttime_ok,
            selecttime_cancel;//两个popwindow的确定、取消按钮
    private Intent intent = new Intent();//轨迹回放意图

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            progressDialog.dismiss();
            switch (msg.what) {
                case 0:
                    Toast.makeText(SelectDate.this, R.string.History_nodata,
                            Toast.LENGTH_LONG).show();
                    break;
                case 1: {
                    if (glob.mapType == 0) {
                        if (AppUtils.IsGooglePlayAvailable(SelectDate.this)) {
                            intent.setClass(SelectDate.this, ShowHistory.class);
                            startActivity(intent);
                        }
                    }
                    if (glob.mapType == 1) {
                        intent.setClass(SelectDate.this, ShowHistoryBaidu.class);
                        startActivity(intent);
                    }
                    break;
                }
                case -1: {
                    Toast.makeText(SelectDate.this, (String) msg.obj,Toast.LENGTH_LONG).show();
                }
                default:
                    break;
            }
        }
    };

    // public void

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.date);
        glob = (SWApplication) getApplicationContext();
        mainview = (ViewGroup) getLayoutInflater().inflate(R.layout.date, null);
        datetext = (TextView) this.findViewById(R.id.selectdate_datetext);
        timetext = (TextView) this.findViewById(R.id.selectdate_timetext);
        selectdateview = (ViewGroup) getLayoutInflater().inflate(
                R.layout.selectdate_datepopwindow, null);
        selecttimeview = (ViewGroup) getLayoutInflater().inflate(
                R.layout.selectdate_timepopwindow, null);
        date = "" + systemday.get(Calendar.YEAR) + "-"
                + (systemday.get(Calendar.MONTH) + 1) + "-"
                + systemday.get(Calendar.DATE);
        datetext.setText(date);
        query = (Button) this.findViewById(R.id.selectdate_query);
        query.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                checkTime();
            }
        });
        // InitDateWheel();
        Datepicker = (DatePicker) selectdateview
                .findViewById(R.id.selectdate_datepicker);
        Timepicker_start = (TimePicker) selecttimeview
                .findViewById(R.id.selecttime_start);
        Timepicker_start.setIs24HourView(true);
        Timepicker_end = (TimePicker) selecttimeview
                .findViewById(R.id.selecttime_end);
        Timepicker_end.setIs24HourView(true);
        selectdate_ok = (Button) selectdateview
                .findViewById(R.id.selectdate_ok);
        selectdate_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                date = "" + Datepicker.getYear() + "-"
                        + (Datepicker.getMonth() + 1) + "-"
                        + Datepicker.getDayOfMonth();
                // Log.d("day", "date "+date);
                datetext.setText(date);
                Datepopwindow.dismiss();

            }
        });
        selectdate_cancel = (Button) selectdateview
                .findViewById(R.id.selectdate_cancel);
        selectdate_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Datepopwindow.dismiss();

            }
        });
        // 日期选择
        chosedateRe = (RelativeLayout) this
                .findViewById(R.id.selectdate_chosedateRelativelayout);
        Datepopwindow = new PopupWindow(selectdateview,
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        Datepopwindow.setFocusable(true);
        Datepopwindow.setBackgroundDrawable(new PaintDrawable());
        chosedateRe.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Datepopwindow.showAtLocation(
                        SelectDate.this.findViewById(R.id.selectdate_customimg),
                        Gravity.CENTER, 0, 0);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.3f;
                getWindow().setAttributes(lp);

            }
        });
        Datepopwindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
            }
        });
        chosedateRe.setClickable(false);
        // 时间选择
        selecttime_ok = (Button) selecttimeview.findViewById(R.id.selecttime_ok);
        selecttime_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startTime = "" + Timepicker_start.getCurrentHour() + ":" + Timepicker_start.getCurrentMinute() + ":" + "00";
                endTime = "" + Timepicker_end.getCurrentHour() + ":" + Timepicker_end.getCurrentMinute() + ":" + "00";
//				Log.d("time", "stime "+startTime);
//				Log.d("time", "etime "+endTime);
                timetext.setText(startTime + "-" + endTime);
                Timepopwindow.dismiss();

            }
        });
        selecttime_cancel = (Button) selecttimeview.findViewById(R.id.selecttime_cancel);
        selecttime_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Timepopwindow.dismiss();

            }
        });
        Timepopwindow = new PopupWindow(selecttimeview,
                LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        chosetimeRe = (RelativeLayout) this
                .findViewById(R.id.selectdate_chosetimeRelativelayout);
        Timepopwindow.setFocusable(true);
        Timepopwindow.setBackgroundDrawable(new PaintDrawable());
        chosetimeRe.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Timepopwindow.showAtLocation(
                        SelectDate.this.findViewById(R.id.selectdate_customimg),
                        Gravity.CENTER, 0, 0);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.3f;
                getWindow().setAttributes(lp);

            }
        });
        chosetimeRe.setClickable(false);
        Timepopwindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);

            }
        });
        dateBackButton = (ImageButton) this.findViewById(R.id.selectdate_back);
        dateBackButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 选定今天
        todayimg = (ImageView) this.findViewById(R.id.selectdate_todayimg);
        todaytext = (TextView) this.findViewById(R.id.selectdate_todaytext);
        todaylinear = (LinearLayout) this
                .findViewById(R.id.selectdate_Todaylinear);
        todaylinear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                date = "" + systemday.get(Calendar.YEAR) + "-"
                        + (systemday.get(Calendar.MONTH) + 1) + "-"
                        + systemday.get(Calendar.DATE);
                // Log.d("date", "day"+date);
                if (Switch == 0) {

                } else {
                    switch (Switch) {
                        case 0:
                            todayimg.setBackgroundResource(R.drawable.selectdate_normal);
                            todaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 1:
                            yesterdayimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            yesterdaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 2:
                            TheDayBeforeYesterdayimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            TheDayBeforeYesterdaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 3:
                            AnHourageimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            AnHouragetext.setTextColor(0xffc9c9c9);
                            break;
                        case 4:
                            customimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            customtext.setTextColor(0xffc9c9c9);
                            break;
                        default:
                            break;

                    }
                    Switch = 0;
                    chosetimeRe.setClickable(false);
                    chosedateRe.setClickable(false);
                    todayimg.setBackgroundResource(R.drawable.selectdate_selected);
                    todaytext.setTextColor(0xff3fa9fd);

                }
            }
        });
        // 选定昨天
        yesterdayimg = (ImageView) this
                .findViewById(R.id.selectdate_yesterdayimg);
        yesterdaytext = (TextView) this
                .findViewById(R.id.selectdate_yesterdaytext);
        yesterdaylinear = (LinearLayout) this
                .findViewById(R.id.selectdate_Yesterdaylinear);
        yesterdaylinear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                date = "" + systemday.get(Calendar.YEAR) + "-"
                        + (systemday.get(Calendar.MONTH) + 1) + "-"
                        + (systemday.get(Calendar.DATE) - 1);
                // Log.d("day", "date "+date);
                if (Switch == 1) {

                } else {
                    switch (Switch) {
                        case 0:
                            todayimg.setBackgroundResource(R.drawable.selectdate_normal);
                            todaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 1:
                            yesterdayimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            yesterdaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 2:
                            TheDayBeforeYesterdayimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            TheDayBeforeYesterdaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 3:
                            AnHourageimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            AnHouragetext.setTextColor(0xffc9c9c9);
                            break;
                        case 4:
                            customimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            customtext.setTextColor(0xffc9c9c9);
                            break;
                        default:
                            break;

                    }
                    Switch = 1;
                    chosetimeRe.setClickable(false);
                    chosedateRe.setClickable(false);
                    yesterdayimg
                            .setBackgroundResource(R.drawable.selectdate_selected);
                    yesterdaytext.setTextColor(0xff3fa9fd);
                }
            }
        });
        // 选定前天
        TheDayBeforeYesterdayimg = (ImageView) this
                .findViewById(R.id.selectdate_TDByesterdayimg);
        TheDayBeforeYesterdaytext = (TextView) this
                .findViewById(R.id.selectdate_TDByesterdaytext);
        TDBYesterdaylinear = (LinearLayout) this
                .findViewById(R.id.selectdate_TDBYesterdaylinear);
        TDBYesterdaylinear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                date = "" + systemday.get(Calendar.YEAR) + "-"
                        + (systemday.get(Calendar.MONTH) + 1) + "-"
                        + (systemday.get(Calendar.DATE) - 2);
                // Log.d("day", "date "+date);
                if (Switch == 2) {

                } else {
                    switch (Switch) {
                        case 0:
                            todayimg.setBackgroundResource(R.drawable.selectdate_normal);
                            todaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 1:
                            yesterdayimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            yesterdaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 2:
                            TheDayBeforeYesterdayimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            TheDayBeforeYesterdaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 3:
                            AnHourageimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            AnHouragetext.setTextColor(0xffc9c9c9);
                            break;
                        case 4:
                            customimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            customtext.setTextColor(0xffc9c9c9);
                            break;
                        default:
                            break;

                    }
                    Switch = 2;
                    chosetimeRe.setClickable(false);
                    chosedateRe.setClickable(false);
                    TheDayBeforeYesterdayimg
                            .setBackgroundResource(R.drawable.selectdate_selected);
                    TheDayBeforeYesterdaytext.setTextColor(0xff3fa9fd);
                }
            }
        });
        // 选定一个小时前
        AnHourageimg = (ImageView) this
                .findViewById(R.id.selectdate_AnHouragoimg);
        AnHouragetext = (TextView) this
                .findViewById(R.id.selectdate_AnHouragotext);
        AnHourAgolinear = (LinearLayout) this
                .findViewById(R.id.selectdate_AnHourAgolinear);
        AnHourAgolinear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                date = "" + systemday.get(Calendar.YEAR) + "-"
                        + (systemday.get(Calendar.MONTH) + 1) + "-"
                        + systemday.get(Calendar.DATE);
                startTime = "" + (systemday.get(Calendar.HOUR) - 1) + ":"
                        + systemday.get(Calendar.MINUTE) + ":"
                        + systemday.get(Calendar.SECOND);
                startTime = "" + (systemday.get(Calendar.HOUR)) + ":"
                        + systemday.get(Calendar.MINUTE) + ":"
                        + systemday.get(Calendar.SECOND);
                if (Switch == 3) {

                } else {
                    switch (Switch) {
                        case 0:
                            todayimg.setBackgroundResource(R.drawable.selectdate_normal);
                            todaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 1:
                            yesterdayimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            yesterdaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 2:
                            TheDayBeforeYesterdayimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            TheDayBeforeYesterdaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 3:
                            AnHourageimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            AnHouragetext.setTextColor(0xffc9c9c9);
                            break;
                        case 4:
                            customimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            customtext.setTextColor(0xffc9c9c9);
                            break;
                        default:
                            break;

                    }
                    Switch = 3;
                    chosetimeRe.setClickable(false);
                    chosedateRe.setClickable(false);
                    AnHourageimg
                            .setBackgroundResource(R.drawable.selectdate_selected);
                    AnHouragetext.setTextColor(0xff3fa9fd);
                }
            }
        });
        // 自定义
        customimg = (ImageView) this.findViewById(R.id.selectdate_customimg);
        customtext = (TextView) this.findViewById(R.id.selectdate_customtext);
        Customlinear = (LinearLayout) this
                .findViewById(R.id.selectdate_Customlinear);
        Customlinear.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Switch == 4) {

                } else {
                    switch (Switch) {
                        case 0:
                            todayimg.setBackgroundResource(R.drawable.selectdate_normal);
                            todaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 1:
                            yesterdayimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            yesterdaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 2:
                            TheDayBeforeYesterdayimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            TheDayBeforeYesterdaytext.setTextColor(0xffc9c9c9);
                            break;
                        case 3:
                            AnHourageimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            AnHouragetext.setTextColor(0xffc9c9c9);
                            break;
                        case 4:
                            customimg
                                    .setBackgroundResource(R.drawable.selectdate_normal);
                            customtext.setTextColor(0xffc9c9c9);
                            break;
                        default:
                            break;

                    }
                    Switch = 4;
                    chosetimeRe.setClickable(true);
                    chosedateRe.setClickable(true);
                    customimg
                            .setBackgroundResource(R.drawable.selectdate_selected);
                    customtext.setTextColor(0xff3fa9fd);
                }
            }
        });

    }

    /**
     * 时间检查
     */
    private void checkTime() {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = s.format(new Date());
        try {
            // 日期判断
            if (s.parse(date).getTime() <= s.parse(currentDate).getTime()) {
                // 时间判断
                String st = currentDate + " " + startTime;
                String et = currentDate + " " + endTime;
                if (s1.parse(et).getTime() > s1.parse(st).getTime()) {
                    progressDialog = new CustomProgressDialog(SelectDate.this);
                    progressDialog.setMessage(getResources().getString(
                            R.string.History_query_ing));
                    progressDialog.show();
                    new GetHistoryThread().start();
                } else {
                    Toast.makeText(SelectDate.this, R.string.History_dateTip1,
                            Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(SelectDate.this, R.string.History_dateTip2,
                        Toast.LENGTH_LONG).show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取历史数据
     */
    class GetHistoryThread extends Thread {
        public void run() {
            /* 消息 */
            String msg = null;
            Message message = new Message();
            SResponse response = HttpRequestClient.getVehHistoryData(
                    glob.curVehicle.getSystemNo(), date, startTime, endTime, 0);
            if (response.getCode() != SProtocol.SUCCESS) {

                msg = SProtocol.getFailMessage(response.getCode(),
                        response.getMessage());

                message.obj = msg;
                message.what = -1;
            } else {
                @SuppressWarnings("unchecked")
                List<History> historys = (List<History>) response.getResult();
                glob.historys = historys;

                if (historys.size() < 2) {
                    message.what = 0;
                } else {
                    message.what = 1;
                }
            }
            handler.sendMessage(message);
        }
    }
}