package com.cn.wetrack.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cn.wetrack.R;

/**
 * 设置
 */
public class Setting extends Activity {
    private static final String TAG = "Setting";
	SWApplication glob;// 全局控制类
	private Handler handler;// 消息处理
    private TextView refreshTimeView;
    private TextView changeMapView;
	private ImageButton backButton;// 返回
    private String[] mapChangeArray;

    private int[] refreshTimes = {10, 20, 30, 60, 90, 120};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		
		/*取得全局变量*/
		glob = (SWApplication) getApplicationContext();
		glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);

		/* 返回*/
		backButton = (ImageButton) findViewById(R.id.backButton);
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

        findViewById(R.id.personl_info_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Setting.this, AccountInfo.class);
                startActivity(intent);
            }
        });

        refreshTimeView = (TextView) findViewById(R.id.location_refresh_time);

        refreshTimeView.setText(glob.sp.getInt("refreshTime", 10) + getString(R.string.second));
        findViewById(R.id.location_refresh_time_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Setting.this)
                        .setTitle(R.string.fix_time)
                        .setSingleChoiceItems(getResources().getStringArray(R.array.location_refresh_time_array),
                                0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        refreshTimeView.setText(refreshTimes[which] + getString(R.string.second));
                                        glob.refreshTime = refreshTimes[which];
                                        glob.sp.edit().putInt("refreshTime", refreshTimes[which]).commit();
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton(R.string.btn_title_cancel, null)
                        .show();
            }
        });

        changeMapView = (TextView) findViewById(R.id.change_map);
        mapChangeArray = getResources().getStringArray(R.array.change_map_array);
        changeMapView.setText(mapChangeArray[glob.sp.getInt("mapType", 0)]);
        findViewById(R.id.change_map_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(Setting.this)
                        .setTitle(R.string.change_map)
                        .setSingleChoiceItems(mapChangeArray,
                                0, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        changeMapView.setText(mapChangeArray[which]);
                                        glob.mapType = which;
                                        glob.sp.edit().putInt("mapType", which).commit();
                                        dialog.dismiss();
                                    }
                                })
                        .setNegativeButton(R.string.btn_title_cancel, null)
                        .show();
            }
        });

        findViewById(R.id.about_layout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(Setting.this, About.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
	}

	/** 返回按键 结束activity */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0)
			finish();
		return false;
	}
}