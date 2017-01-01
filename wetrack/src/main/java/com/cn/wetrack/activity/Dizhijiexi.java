package com.cn.wetrack.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cn.wetrack.R;
import com.cn.wetrack.activity.baidu.MonitorLocationBaidu;
import com.cn.wetrack.entity.Location;
import com.cn.wetrack.entity.SResponse;
import com.cn.wetrack.util.AppUtils;
import com.cn.wetrack.util.HttpRequestClient;
import com.cn.wetrack.util.SProtocol;

public class Dizhijiexi extends Activity {
    private TextView CarNumber, CarLocation, Systemno, PhoneNumber,
            LocationTime, Temperature, OverDueTime, Mileage;
    private Button historyBtn, monitorBtn, monitorList;
    private SWApplication glob;
    private RelativeLayout firstrelativelayout;
    private Intent intent = new Intent();
    private ImageButton Imagebuttonback;
    //	private Runnable getLocationRunnable = null;// 刷新位置线程
    // 	private CustomProgressDialog progressDialog = null;// 进度
    private Handler addressHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle bundle = msg.getData();
            CarLocation.setText(bundle.getString("address"));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dizhijiexi);
//        progressDialog = new CustomProgressDialog(Dizhijiexi.this);
        glob = (SWApplication) getApplicationContext();
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        CarNumber = (TextView) this.findViewById(R.id.dzjx_carnumber);
        CarNumber.setText(glob.curVehicle.getVehNoF());
        CarLocation = (TextView) this.findViewById(R.id.dzjx_carlocation);
        new GetAddressThread().start();
        // Log.d("loca", "loca "+glob.curVLocation.getLocate());
        Systemno = (TextView) this.findViewById(R.id.dzjx_systemno);
        Systemno.setText(glob.curVehicle.getSystemNo());
        PhoneNumber = (TextView) this.findViewById(R.id.dzjx_PhoneNumber);
        PhoneNumber.setText(glob.curVehicle.getSimID());
        LocationTime = (TextView) this.findViewById(R.id.dzjx_locationtime);
        LocationTime.setText(glob.curVLocation.getTime());
        Temperature = (TextView) this.findViewById(R.id.dzjx_temperature);
        Temperature.setText(glob.curVLocation.getTemperature() + "℃");
        OverDueTime = (TextView) this.findViewById(R.id.dzjx_overduetime);
        OverDueTime.setText(glob.curVehicle.getYyDate());
        Mileage = (TextView) findViewById(R.id.dzjx_all_mileage);
        Mileage.setText(glob.curVLocation.getMiles() + "Km");
        firstrelativelayout = (RelativeLayout) this
                .findViewById(R.id.dzjx_firstpart);
        firstrelativelayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (glob.mapType == 0) {
                    if (AppUtils.IsGooglePlayAvailable(Dizhijiexi.this)) {
                        intent.setClass(Dizhijiexi.this, MonitorLocation.class);
                    }
                }
                if (glob.mapType == 1) {
                    intent.setClass(Dizhijiexi.this, MonitorLocationBaidu.class);
                }
                startActivity(intent);
            }
        });

        monitorBtn = (Button) findViewById(R.id.dzjx_monitor_btn);
        monitorBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (glob.mapType == 0) {
                    if (AppUtils.IsGooglePlayAvailable(Dizhijiexi.this)) {
                        intent.setClass(Dizhijiexi.this, MonitorLocation.class);
                    }
                }
                if (glob.mapType == 1) {
                    intent.setClass(Dizhijiexi.this, MonitorLocationBaidu.class);
                }
                startActivity(intent);
            }
        });
        historyBtn = (Button) this.findViewById(R.id.dzjx_histor_btn);
        historyBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                intent.setClass(Dizhijiexi.this, SelectDate.class);
                startActivity(intent);

            }
        });
        /* 返回 */
        Imagebuttonback = (ImageButton) this.findViewById(R.id.dzjx_back);
        Imagebuttonback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        monitorList = (Button) this.findViewById(R.id.dzjx_monitorList);
        monitorList.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                intent.setClass(Dizhijiexi.this, VehicleList.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 获取地址
     */
    class GetAddressThread extends Thread {
        public void run() {
            String address;
            Message message = new Message();
            Bundle bundle = new Bundle();
            Location l = glob.curVLocation;
            SResponse response = HttpRequestClient.addressTranslate(
                    l.getLongitude(), l.getLatitude());
            if (response.getCode() == SProtocol.SUCCESS) {
                address = (String) response.getResult();
                message.what = 0;
            } else {
                address = SProtocol.getFailMessage(response.getCode(),
                        response.getMessage());
                message.what = 1;
            }
            bundle.putString("address", address);
            message.setData(bundle);
            addressHandler.sendMessage(message);
        }
    }

}
