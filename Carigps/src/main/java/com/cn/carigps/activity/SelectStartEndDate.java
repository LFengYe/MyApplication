package com.cn.carigps.activity;

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
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.cn.carigps.R;
import com.cn.carigps.entity.MileageOilReport;
import com.cn.carigps.entity.SResponse;
import com.cn.carigps.widgets.CustomProgressDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class SelectStartEndDate extends Activity {
    MyApplication glob;// 全局控制类
    private SResponse response;
    private String Startdate = "2015-01-25";
    private String Starttime = "00:00:00";
    private String Enddate = "2015-01-31";
    private String Endtime = "00:00:00";
    private List<MileageOilReport> Oils = new ArrayList<MileageOilReport>();
    private Button query;
    private ImageButton back;
    private Intent getIntent;
    private RelativeLayout Relative_selectdate, Relative_selecttime,
            Relative_selectenddate, Relative_selectendtime;
    private TextView dateTextview, timeTextview, enddateTextview,endtimeTextview;
    private ViewGroup datePopview, timePopview;// popwindow的布局
    private PopupWindow Datepopwindow = null, Timepopwindow = null;// 两个popwindow
    private DatePicker Datepicker = null;// 日期控件
    private TimePicker Timepicker;// 时间选择器
    private Button selectdate_ok, selectdate_cancel, selecttime_ok,
            selecttime_cancel;// 两个popwindow的确定、取消按钮
    private Calendar systemdate = Calendar.getInstance();
    private CustomProgressDialog progressDialog = null;
    private Boolean Start = true, isshow = false, Date = false;
    private String St, Et;
    private Calendar startcalendar = Calendar.getInstance(), endcalendar = Calendar.getInstance();
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Toast.makeText(SelectStartEndDate.this,
                            R.string.History_nodata, Toast.LENGTH_LONG)
                            .show();
                    break;

                default:
                    break;
            }
        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.selectoilreportdate);

        getIntent = getIntent();
        datePopview = (ViewGroup) getLayoutInflater().inflate(
                R.layout.selectdate_datepopwindow, null);
        timePopview = (ViewGroup) getLayoutInflater().inflate(
                R.layout.selectoilreportdate_timepopwindow, null);
        glob = (MyApplication) getApplicationContext();
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        dateTextview = (TextView) this
                .findViewById(R.id.selectOilReport_startdatetext);
        timeTextview = (TextView) this
                .findViewById(R.id.selectOilReport_starttimetext);
        enddateTextview = (TextView) this
                .findViewById(R.id.selectOilReport_enddatetext);
        endtimeTextview = (TextView) this
                .findViewById(R.id.selectOilReport_endtimetext);
        Datepicker = (DatePicker) datePopview
                .findViewById(R.id.selectdate_datepicker);
        Timepicker = (TimePicker) timePopview
                .findViewById(R.id.selectoilreporttime_timpiker);
        Timepicker.setIs24HourView(true);
        InitDate();
        back = (ImageButton) this.findViewById(R.id.selectOilReportdate_back);
        back.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        query = (Button) this.findViewById(R.id.selectOilReport_query);
        query.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                checkTime();

            }

        });
        // 选择开始日期
        Datepopwindow = new PopupWindow(datePopview, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        Datepopwindow.setFocusable(true);
        Datepopwindow.setBackgroundDrawable(new PaintDrawable());
        Relative_selectdate = (RelativeLayout) this
                .findViewById(R.id.selectOilReport_chosestartdateRelativelayout);
        Relative_selectdate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Start = true;
                Datepopwindow.showAtLocation(SelectStartEndDate.this
                                .findViewById(R.id.selectOilReport_query),
                        Gravity.CENTER, 0, 0);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.3f;
                getWindow().setAttributes(lp);
                isshow = true;
                Date = true;
            }
        });
        Datepopwindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
                isshow = false;

            }
        });
        // 选择开始时间
        Timepopwindow = new PopupWindow(timePopview, LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        Timepopwindow.setFocusable(true);
        Timepopwindow.setBackgroundDrawable(new PaintDrawable());
        Relative_selecttime = (RelativeLayout) this
                .findViewById(R.id.selectOilReport_chosestarttimeRelativelayout);
        Relative_selecttime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Start = true;
                Timepopwindow.showAtLocation(SelectStartEndDate.this
                                .findViewById(R.id.selectOilReport_query),
                        Gravity.CENTER, 0, 0);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.3f;
                getWindow().setAttributes(lp);
                isshow = true;
                Date = false;

            }
        });
        Timepopwindow.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 1f;
                getWindow().setAttributes(lp);
                isshow = false;

            }
        });
        // 选择结束日期
        Relative_selectenddate = (RelativeLayout) this
                .findViewById(R.id.selectOilReport_choseenddateRelativelayout);
        Relative_selectenddate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Start = false;
                Datepopwindow.showAtLocation(SelectStartEndDate.this
                                .findViewById(R.id.selectOilReport_query),
                        Gravity.CENTER, 0, 0);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.3f;
                getWindow().setAttributes(lp);
                isshow = true;
                Date = true;

            }
        });
        // 选择结束时间
        Relative_selectendtime = (RelativeLayout) this
                .findViewById(R.id.selectOilReport_choseendtimeRelativelayout);
        Relative_selectendtime.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Start = false;
                Timepopwindow.showAtLocation(SelectStartEndDate.this
                                .findViewById(R.id.selectOilReport_query),
                        Gravity.CENTER, 0, 0);
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.alpha = 0.3f;
                getWindow().setAttributes(lp);
                isshow = true;
                Date = false;

            }
        });
        selectdate_ok = (Button) datePopview.findViewById(R.id.selectdate_ok);
        selectdate_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//				if(glob.)
                if (Start) {
                    Startdate = "" + Datepicker.getYear() + "-"
                            + (Datepicker.getMonth() + 1) + "-"
                            + Datepicker.getDayOfMonth();
                    // Log.d("day", "date "+date);
                    dateTextview.setText(Startdate);
                } else {
                    Enddate = "" + Datepicker.getYear() + "-"
                            + (Datepicker.getMonth() + 1) + "-"
                            + Datepicker.getDayOfMonth();
                    // Log.d("day", "date "+date);
                    enddateTextview.setText(Enddate);
                }
                Datepopwindow.dismiss();

            }
        });
        selectdate_cancel = (Button) datePopview
                .findViewById(R.id.selectdate_cancel);
        selectdate_cancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Datepopwindow.dismiss();

            }
        });
        selecttime_ok = (Button) timePopview
                .findViewById(R.id.selectoilreporttime_ok);
        selecttime_ok.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (Start) {
                    Starttime = "" + Timepicker.getCurrentHour() + ":"
                            + Timepicker.getCurrentMinute() + ":00";
                    timeTextview.setText(Starttime);
                } else {
                    Endtime = "" + Timepicker.getCurrentHour() + ":"
                            + Timepicker.getCurrentMinute() + ":00";
                    endtimeTextview.setText(Endtime);
                }
                Timepopwindow.dismiss();

            }
        });
        selecttime_cancel = (Button) timePopview
                .findViewById(R.id.selectoilreporttime_cancel);
        selecttime_cancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Timepopwindow.dismiss();

            }
        });

    }

    public void InitDate() {
        Startdate = "" + systemdate.get(Calendar.YEAR) + "-"
                + (systemdate.get(Calendar.MONTH) + 1) + "-"
                + systemdate.get(Calendar.DATE);
        dateTextview.setText(Startdate);
//        Starttime = "" + systemdate.get(Calendar.HOUR_OF_DAY) + ":"
//                + systemdate.get(Calendar.MINUTE) + ":"
//                + systemdate.get(Calendar.SECOND);
        timeTextview.setText(Starttime);
        Enddate = "" + systemdate.get(Calendar.YEAR) + "-"
                + (systemdate.get(Calendar.MONTH) + 1) + "-"
                + systemdate.get(Calendar.DATE);
        enddateTextview.setText(Enddate);
        Endtime = "" + systemdate.get(Calendar.HOUR_OF_DAY) + ":"
                + systemdate.get(Calendar.MINUTE) + ":"
                + systemdate.get(Calendar.SECOND);
        endtimeTextview.setText(Endtime);
    }

    private void checkTime() {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat s1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            St = Startdate + " " + Starttime;
            Et = Enddate + " " + Endtime;
            long day, difference;
            if (s1.parse(Et).getTime() > s1.parse(St).getTime()) {
                startcalendar.setTime(s1.parse(St));
                endcalendar.setTime(s1.parse(Et));
                difference = endcalendar.getTimeInMillis() - startcalendar.getTimeInMillis();
                day = difference / 86400000;//天数
                if (day <= 31) {
//                    Intent intent = new Intent();
                    if (glob.menuIndex == 2) {
                        getIntent.setClass(SelectStartEndDate.this, MileageReport.class);
                    }
                    if (glob.menuIndex == 3) {
                        getIntent.setClass(SelectStartEndDate.this, OilReportShow.class);
                    }
                    getIntent.putExtra("startTime", St);
                    getIntent.putExtra("endTime", Et);
                    startActivity(getIntent);
                } else {
                    Toast.makeText(SelectStartEndDate.this,
                            R.string.SelectOil_error, Toast.LENGTH_LONG)
                            .show();
                }
            } else {
                Toast.makeText(SelectStartEndDate.this,
                        R.string.History_dateTip1, Toast.LENGTH_LONG)
                        .show();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
