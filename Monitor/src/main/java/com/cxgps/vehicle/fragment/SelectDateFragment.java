package com.cxgps.vehicle.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.cxgps.vehicle.AppConfig;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.api.remote.VehicleApi;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.HistoryInfo;
import com.cxgps.vehicle.bean.ResponseBean;
import com.cxgps.vehicle.ui.SimpleBackActivity;
import com.cxgps.vehicle.ui.TrackplayActivity;
import com.cxgps.vehicle.utils.DateUtils;
import com.cxgps.vehicle.utils.UIHelper;
import com.cxgps.vehicle.widget.BirthDateDialog;
import com.kymjs.rxvolley.client.HttpCallback;

import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by taosong on 16/6/8.
 */
public class SelectDateFragment extends BaseFragment {

    private static final long ONE_SECOND = 1000;
    private static final long ONE_MINUTE = ONE_SECOND * 60;
    private static final long ONE_HOUR = ONE_MINUTE * 60;
    private static final long ONE_DAY = ONE_HOUR * 24;

    @Bind(R.id.start_time)
    TextView   mStartTime;


    @Bind(R.id.end_time)
    TextView mEndTime;

    @Bind(R.id.start_toplay)
    TextView  mToplay;

    private  StringBuilder startTimeStr;

    private StringBuilder endTimeStr;


    private Date startDate;

    private Date  endDate;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_date, null);
        ButterKnife.bind(this,view);
        initView(view);
        initMyData();
        return view;
    }



    @OnClick({R.id.start_toplay, R.id.start_time, R.id.end_time})
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.start_time:
                getDate(startTimeStr.toString(),getString(R.string.set_start_date),mStartTime,0);
                break;
            case R.id.end_time:
                getDate(endTimeStr.toString(), getString(R.string.set_end_date),mEndTime,1);
                break;
            case R.id.start_toplay:
                toPlay();
                break;
        }
    }

    /**
     * 时间截取
     *
     * @param datetime
     * @param splite
     * @return
     */
    public static int[] getYMDArray(String datetime, String splite) {
        int date[] = { 0, 0, 0, 0, 0 };
        if (datetime != null && datetime.length() > 0) {
            String[] dates = datetime.split(splite);
            int position = 0;
            for (String temp : dates) {
                date[position] = Integer.valueOf(temp);
                position++;
            }
        }
        return date;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);


    }

    @Override
    public void initView(View view) {
    }


    public void initMyData() {
        startDate = new Date();
        startTimeStr = new StringBuilder(DateUtils.date2yyyyMMdd(startDate));
        startTimeStr.append(" ").append("00:00:00");

        endDate = new Date();
        endTimeStr  = new StringBuilder(DateUtils.date2yyyyMMdd(endDate));
        endTimeStr.append(" ").append("23:59:59");

        String mStartStr = getDateTitle(startTimeStr.toString(),0);
        String mEndStr =  getDateTitle(endTimeStr.toString(), 1);


        mStartTime.setText(mStartStr);
        mEndTime.setText(mEndStr);






    }


    public String getDateTitle(String time,int tag){

        if (tag == 0){

            return  String.format(getString(R.string.start_time_title), time).toString();
        }else if (tag == 1){

            return  String.format(getString(R.string.end_time_title), time).toString();
        }

        return "";
    }




    private  void toPlay(){

        if ("".equals(startTimeStr) || startDate ==null){

            AppContext.showToast(R.string.start_date_null);
            return;
        }
        if ("".equals(endTimeStr) || endDate == null){
            AppContext.showToast(R.string.end_date_null);
            return;
        }
        long  spliteTime =  Math.abs(endDate.getTime() - startDate.getTime());
        if (spliteTime < 4*ONE_DAY){


            if ( AppConfig.SELECT_DATA_MODE == AppConfig.MODE_TRACK) {
                requestHistory(spliteTime, startTimeStr.toString(), endTimeStr.toString());
            }else {

                Bundle bundle = getArguments();
                bundle.putString("startTime", startTimeStr.toString());
                bundle.putString("endTime", endTimeStr.toString());
                UIHelper.showWebDetail(getActivity(), AppConfig.REPORT, AppConfig.REPORT, bundle);

            }


        }else {
            AppContext.showToast(R.string.date_select_week);
        }


    }





    // 时间
    public void getDate(String curDate, String dialog_title,final  TextView  textView,final  int tag) {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        String dateStr = "";
        String timeStr = "";
        if (!"".equals(curDate) && curDate != null) {
            String strsf[] = curDate.split(" ");
            dateStr = strsf[0];
            timeStr = strsf[1];
        }

        int[] date = getYMDArray(dateStr, "-");
        int[] time = getYMDArray(timeStr, ":");
        BirthDateDialog birthDiolog = new BirthDateDialog(
                getActivity(),
                new BirthDateDialog.PriorityListener() {
                    @Override
                    public void refreshPriorityUI(String year, String month,
                                                  String day, String hours, String mins, String second) {
                        StringBuffer date_time_buf = new StringBuffer(year
                                + "-" + month + "-" + day + " ");
                        date_time_buf.append(hours + ":" + mins + ":" + second);

                        if (tag == 0){
                            startDate = DateUtils.string2Date(date_time_buf.toString(), "yyyy-MM-dd");
                            startTimeStr = new StringBuilder(date_time_buf);
                            textView.setText(String.format(getString(R.string.start_time_title), date_time_buf).toString());
                        }else if (tag == 1){
                            endDate = DateUtils.string2Date(date_time_buf.toString(), "yyyy-MM-dd");
                            endTimeStr = new StringBuilder(date_time_buf);
                            textView.setText(  String.format(getString(R.string.end_time_title),date_time_buf).toString());
                        }



                    }
                }, date[0], date[1], date[2], time[0], time[1], time[2], width,
                height, dialog_title);
        Window window = birthDiolog.getWindow();
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置
        window.setWindowAnimations(R.style.dialogstyle); // 添加动画
        birthDiolog.setCancelable(true);
        birthDiolog.show();
    }




    private  void requestHistory(long longtime,String startTime,String endTime){


        String systemNo = getActivity().getIntent().getBundleExtra(SimpleBackActivity.BUNDLE_KEY_ARGS).getString("systemNo") ;

        showWaitDialog(getString(R.string.data_loading));
        VehicleApi.getCarHistory(systemNo,startTime,endTime,0,historyHandler);




    }

    private HttpCallback historyHandler = new HttpCallback() {


        @Override
        public void onFailure(int errorNo, String strMsg) {
            super.onFailure(errorNo, strMsg);
            hideWaitDialog();
            AppContext.showToast(R.string.error_view_no_data);
        }

        @Override
        public void onSuccess(Map<String, String> headers, byte[] t) {
            super.onSuccess(headers, t);

            Log.i("TAG","===onSuccess====="+new String(t));
            hideWaitDialog();
            ResponseBean responseBean = JSON.parseObject(new String(t), ResponseBean.class);

            if (responseBean!=null && responseBean.isRequestFlag()){

                List<HistoryInfo> historyInfo = JSON.parseArray(responseBean.getData(),HistoryInfo.class);

                if (historyInfo.size() > 0 ){

                    showHistoryFragment(historyInfo);

                }else {

                    AppContext.showToast(R.string.error_view_no_data);

                }
            }else {

                AppContext.showToast(R.string.error_view_no_data);
            }
        }


    };


    private  void showHistoryFragment( List<HistoryInfo> data){

        long  spliteTime =  Math.abs(endDate.getTime() - startDate.getTime());
        Bundle  bundle = new Bundle();

        bundle.putLong("longtime", spliteTime);

        Intent  intent = new Intent(getActivity(), TrackplayActivity.class);
        //  bundle.putSerializable("history_data", (Serializable) data);

        AppContext.getInstance().setHistoryInfos(data);
        intent.putExtras(bundle);

        startActivity(intent);


    }





}

