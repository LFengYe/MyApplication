package com.cn.carigps.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.cn.carigps.R;
/**
 * Created by fuyzh on 16/7/24.
 */
public class About extends Activity {

    private TextView aboutInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        String about = "Carigps是一款专注于车辆定位，监控管理，远程控制的车辆定位管理软件。随时随地，如影随形！\n" +
                "\n" +
                "主要功能：\n" +
                "\n" +
                "实时定位：随时随地，如影随行，掌握爱车的当前状态。\n" +
                "轨迹回放：根据时间查询车辆的历史轨迹，关注车辆行驶数据。\n" +
                "油量统计：即时了解车辆的油耗情况，加油时间，加油量，偷油量以及加油地点。\n" +
                "里程统计：统计某个时间段内的车辆行驶里程，行驶速度。\n" +
                "报警信息：获取车辆的报警信息，观察车辆动态信息。\n" +
                "关闭油路：远程对车辆进行断油或者断电操作，使车辆停止运行。\n" +
                "恢复油路：远程对车辆进行恢复油路或者电路，使车辆恢复运行。\n" +
                "公告信息：获取公司发布的相关内容和信息。\n" +
                "应用设置：根据个人爱好选择地图，刷新时间等。\n" +
                "退出系统：退出监控系统。\n" +
                "\n" +
                "Using the CARIGPS fleet tracking management system you can track your vehicles in real-time, anytime, using any internet-connected device. There is no need for proprietary software or maps - everything is accessed over the web. Our system uses GPS satellites to locate your vehicles, and cellular technology to ensure that their positions, routes and logs are constantly updated.\n" +
                "\n" +
                "The main functions:\n" +
                "\n" +
                "Monitoring: Real time tracking is made possible by the reporting frequency of the real time GPS tracking device. Fleet updates on movement and able to monitor vehicles on the ground.\n" +
                "\n" +
                "History Playback: GPS system allows you to look back at a vehicle's location and driver performance over time. For each real time GPS tracker, you can use the map interface to review a vehicle's history through an animated, interactive route replay that breaks each day does into detailed trip segments. \n" +
                "\n" +
                "Fuel Consumption: Instantly aware of vehicle fuel consumption used, refueling time, and refueling capacity, fuel steal capacity, refueling locations.\n" +
                "\n" +
                "Total Mileage: statistics on a certain vehicle mileage, speed in a period of time\n" +
                "\n" +
                "Alarm message: Receive vehicle alert information, to observe vehicle dynamic information. \n" +
                "\n" +
                "Lock: Remote oil cut-off vehicle or power operation, so that the vehicle stopped running.\n" +
                "\n" +
                "UnLock: Use remote control to restore vehicle oil circuit or electric circuit, so that vehicles resume back to normal \n" +
                "\n" +
                "Offcial News: Receive relevant content and information posted by the company.\n" +
                "\n" +
                "Settings: According to personal preferences to choose its map and refresh time.\n" +
                "\n" +
                "Exit System: Exit from Tracking & Monitoring system.\n" +
                "\n";
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
