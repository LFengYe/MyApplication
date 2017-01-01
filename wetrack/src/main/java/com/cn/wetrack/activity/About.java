package com.cn.wetrack.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cn.wetrack.R;

/**
 * Created by fuyzh on 16/7/24.
 */
public class About extends Activity {

    private TextView aboutInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String about = "Light Year GPS\nWe Provide GPS Solution For Truck, Bus Express, Taxi Etc.. Feature; Daily Driving Record Report, Vehicle Daily Mileage Report, Fuel Consumption and Report Etc… Over Speeding Alarm, Battery Stolen Alarm, Emergency Alarm, Illegal Start Alarm, Remote Lock / Open Lock Function, Geo-Fence Report, Poi Etc...\n我们提供货车，长途巴士，德士等交通工具GPS解决方案。功能；行驶记录分析报告，行事里数分析报告，车辆油耗统计报告，超速警报，断电警报，应急警报，非法启动警报，断电功能，围栏报告，等坐标设置等...\n如有任何需求及技术上问题可以联络以下:\nSale / 销售\nBenny Leong 016-6618666\nEric Gan 016-772 6668\n\nHardware and Programmer / 硬件以及软件\nEric Gan 016-772 6668\n\nTechnicial Support / 技术支援\nSenior Technician Jason Liong 019-710 9666";
        aboutInfo = (TextView) findViewById(R.id.about_info);
        aboutInfo.setText(about);

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
