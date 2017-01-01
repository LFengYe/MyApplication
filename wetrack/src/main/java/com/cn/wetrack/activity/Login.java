/*author：time：2014-12-19 为登陆界面添加切换主机滚轮控件*
 * */
package com.cn.wetrack.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.PaintDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.wetrack.R;
import com.cn.wetrack.entity.Account;
import com.cn.wetrack.entity.Alarm;
import com.cn.wetrack.entity.Host;
import com.cn.wetrack.entity.SResponse;
import com.cn.wetrack.util.HttpRequestClient;
import com.cn.wetrack.util.SProtocol;
import com.cn.wetrack.util.UtilTool;
import com.cn.wetrack.widgets.CustomProgressDialog;
import com.cn.wetrack.widgets.Myscrollview;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 登录
 */
@SuppressLint({"WorldReadableFiles", "WorldWriteableFiles", "HandlerLeak"})
public class Login extends Activity implements OnClickListener {
    private SWApplication glob;
    private Handler handler;
    private EditText userName;//用户名
    private EditText password; //密码
    private CheckBox savePsw;//保存密码
    private CheckBox autoLogin;//自动登录
    private Button loginButton;
//    private TextView changeServer;
    private EditText serverAddress;
    private EditText serverPort;
    private LinearLayout hostSwitch;// 服务器切换

    private PopupWindow host_popwindow;//切换主机悬浮窗
    private ViewGroup myview = null, mainview = null;//获取布局
    private CustomProgressDialog progressDialog = null;
    private Myscrollview myscrollview = null;//自定义滚轮控件
    private Button pop_okbtn, pop_canclebtn;//悬浮框里的确定取消按钮
    private TextView[] textview = new TextView[100];//这里最好换成list
    private String[] hostNames = null;//主机名数组
    private LinearLayout hostset_linear;
    private float size = 0, othersize = 0;
    private Timer mytimer = new Timer();//定时器
    private Boolean Istimeout = true, Ispoptimeout = true;
    private int position = 0, oldposition = 0, SizePosition = 0, ColorPosition = 0;
    private Boolean up = false, down = false, move = false;
    private int ErrorCount = 0;
    private int Errorposition = 0;
    private boolean popwindowisshow = false;//悬浮窗是否显示
    private List<Host> hosts;//服务器列表
    private Handler pophandler;
    private int i;
    private List<Alarm> alarms;
    Configuration config;

    private Handler myhandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 2:
                    if (size > 5) {
                        textview[ColorPosition].setTextColor(0xffa0a0a0);
                        textview[SizePosition].setTextSize(10 - size);
                        textview[ColorPosition + 1].setTextColor(0xff959595);
                        textview[SizePosition + 1].setTextSize(20 - size);
                        textview[ColorPosition + 2].setTextColor(0xff000000);
                        textview[SizePosition + 2].setTextSize(30 - size);
                        textview[ColorPosition + 3].setTextColor(0xff959595);
                        textview[SizePosition + 3].setTextSize(20 + size);
                        textview[ColorPosition + 4].setTextColor(0xffa0a0a0);
                        textview[SizePosition + 4].setTextSize(10 + size);
                    } else {
                        textview[ColorPosition].setTextColor(0xffa0a0a0);
                        textview[SizePosition].setTextSize(10 - size);
                        textview[ColorPosition + 1].setTextColor(0xff959595);
                        textview[SizePosition + 1].setTextSize(20 - size);
                        textview[ColorPosition + 2].setTextColor(0xff000000);
                        textview[SizePosition + 2].setTextSize(30 - size);
                        textview[ColorPosition + 3].setTextColor(0xff959595);
                        textview[SizePosition + 3].setTextSize(20 + size);
                        textview[ColorPosition + 4].setTextColor(0xffa0a0a0);
                        textview[SizePosition + 4].setTextSize(10 + size);
                    }
                    break;
                case -2:
                    othersize = 7 - size;
                    if (size > 5) {
                        textview[ColorPosition].setTextColor(0xffa0a0a0);
                        textview[SizePosition].setTextSize(10 + othersize);
                        textview[ColorPosition + 1].setTextColor(0xff959595);
                        textview[SizePosition + 1].setTextSize(20 + othersize);
                        textview[ColorPosition + 2].setTextColor(0xff000000);
                        textview[SizePosition + 2].setTextSize(30 - othersize);
                        textview[ColorPosition + 3].setTextColor(0xff959595);
                        textview[SizePosition + 3].setTextSize(20 - othersize);
                        textview[ColorPosition + 4].setTextColor(0xffa0a0a0);
                        textview[SizePosition + 4].setTextSize(10 - othersize);
                    } else {
                        textview[ColorPosition].setTextColor(0xffa0a0a0);
                        textview[SizePosition].setTextSize(10 + othersize);
                        textview[ColorPosition + 1].setTextColor(0xff959595);
                        textview[SizePosition + 1].setTextSize(20 + othersize);
                        textview[ColorPosition + 2].setTextColor(0xff000000);
                        textview[SizePosition + 2].setTextSize(30 - othersize);
                        textview[ColorPosition + 3].setTextColor(0xff959595);
                        textview[SizePosition + 3].setTextSize(20 - othersize);
                        textview[ColorPosition + 4].setTextColor(0xffa0a0a0);
                        textview[SizePosition + 4].setTextSize(10 - othersize);
                    }
                    break;
                case 0:
                    myscrollview.scrolloto(ColorPosition * 100);
                    textview[ColorPosition].setTextColor(0xffa0a0a0);
                    textview[ColorPosition].setTextSize(10);
                    textview[ColorPosition + 1].setTextColor(0xff959595);
                    textview[ColorPosition + 1].setTextSize(20);
                    textview[ColorPosition + 2].setTextColor(0xff000000);
                    textview[ColorPosition + 2].setTextSize(30);
                    textview[ColorPosition + 3].setTextColor(0xff959595);
                    textview[ColorPosition + 3].setTextSize(20);
                    textview[ColorPosition + 4].setTextColor(0xffa0a0a0);
                    textview[ColorPosition + 4].setTextSize(10);
                    break;
            }
        }
    };

    private Handler Testtimehandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Istimeout = true;
                    break;
                case 1:
                    Ispoptimeout = true;
                    break;
                default:
                    break;
            }
        }

        ;
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        config = getResources().getConfiguration();// 获得设置对象
        glob = (SWApplication) getApplicationContext();
        myview = (ViewGroup) getLayoutInflater().inflate(R.layout.host_popwindow, null);
        mainview = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_login, null);
        hostset_linear = (LinearLayout) myview.findViewById(R.id.addhostset_linear);
        host_popwindow = new PopupWindow(myview, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        host_popwindow.setFocusable(true);
        host_popwindow.setBackgroundDrawable(new PaintDrawable());
        pop_okbtn = (Button) myview.findViewById(R.id.hostswitch_ok);
        pop_okbtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                /* 记录到本地 */
                glob.sp.edit().putString("hostName", hosts.get(ColorPosition).getName()).commit();
                glob.sp.edit().putString("hostEnName", hosts.get(ColorPosition).getEnname()).commit();
                glob.sp.edit().putString("host", "http://" + hosts.get(ColorPosition).getIp() + ":" + hosts.get(ColorPosition).getPort() + "/").commit();
				/*改变显示*/
                //hostText.setText(hosts.get(position).getName());
				/*切换地址*/
                HttpRequestClient.host = glob.sp.getString("host", HttpRequestClient.defaultHost);
                host_popwindow.dismiss();
            }
        });
        pop_canclebtn = (Button) myview.findViewById(R.id.hostswitch_cancle);
        pop_canclebtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                host_popwindow.dismiss();
            }
        });


        handler = createHandler();
        progressDialog = new CustomProgressDialog(Login.this);

		/*初始化服务器*/
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        pophandler = createpopHandler();
        String host = glob.sp.getString("host", HttpRequestClient.defaultHost);
        if (host != null) {
            HttpRequestClient.host = host;
        }

		/*初始化*/
        userName = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.psw);
        savePsw = (CheckBox) findViewById(R.id.savepsw);
        autoLogin = (CheckBox) findViewById(R.id.autologin);
        loginButton = (Button) findViewById(R.id.dologin);
        myscrollview = (Myscrollview) myview.findViewById(R.id.my_scrollview);
        loginButton.setOnClickListener(this);

		/*保存密码*/
        savePsw.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
                glob.sp.edit().putBoolean("savePsw", isChecked).commit();
            }
        });

		/*自动登录*/
        autoLogin.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
                glob.sp.edit().putBoolean("autoLogin", isChecked).commit();
            }
        });

		/*初始化配置*/
        initShareConfig();

		/*自动登录*/
        if (glob.switchAutoLogin && initCheckNetwork(this) && autoLogin.isChecked()) {
            loginButton.performClick();
        }
        if (!glob.switchAutoLogin) {
            glob.switchAutoLogin = true;
        }

        /*
        changeServer = (TextView) findViewById(R.id.change_server);
        changeServer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(Login.this);
                View dialogView = inflater.inflate(R.layout.dialog_server_input, null);
                serverAddress = (EditText) dialogView.findViewById(R.id.server_address);
                serverPort = (EditText) dialogView.findViewById(R.id.server_port);

                AlertDialog dialog = new Builder(Login.this)
                        .setTitle(R.string.change_server_title)
                        .setView(dialogView)
                        .setNegativeButton(R.string.btn_title_cancel, null)
                        .setPositiveButton(R.string.btn_title_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (TextUtils.isEmpty(serverAddress.getText())) {
                                    Toast.makeText(Login.this, R.string.server_address_nonull, Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if (TextUtils.isEmpty(serverPort.getText())) {
                                    Toast.makeText(Login.this, R.string.server_port_nonull, Toast.LENGTH_LONG).show();
                                    return;
                                }
                                String host = "http://" + serverAddress.getText() + ":" + serverPort.getText() + "/";
                                HttpRequestClient.host = host;
                                glob.sp.edit().putString("host", host);
                            }
                        })
                        .create();

                Window window = dialog.getWindow();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.alpha = 0.9f;
                window.setAttributes(lp);
                dialog.show();
            }
        });
        */

		/*服务器切换*/
        hostSwitch = (LinearLayout) findViewById(R.id.hostSwitch);
        hostSwitch.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
				/*Intent intent = new Intent(Login.this, HostSetting.class);
				startActivity(intent);*/

                if (Ispoptimeout) {
                    progressDialog.setMessage(getResources().getString(R.string.HostSetting_getting));
                    progressDialog.show();
                    Ispoptimeout = false;
                    Testtimehandler.postDelayed(new Poptimecleanthread(), 5000);
                    myscrollview.scrolloto(position * 70 + 1);
                    new GetHostThread().start();
                    popwindowisshow = true;
                }
            }
        });
    }

    /**
     * Handler
     */
    private Handler createHandler() {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
				/*隐藏*/
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

                Bundle b = message.getData();
                switch (message.what) {
                    case 0: {
                        String msg = b.getString("msg");
                        if (msg == null) {
                            Intent intent = new Intent(Login.this, Main.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
                super.handleMessage(message);
            }
        };
        return handler;
    }

    /**
     * 检测网络
     */
    private boolean initCheckNetwork(final Context context) {
        boolean flag = UtilTool.CheckNetwork(this);
        if (!flag) {
            Builder b = new Builder(context);
            b.setTitle(R.string.alert_title);
            b.setMessage(context.getString(R.string.Login_net_work_message));
            b.setNeutralButton(R.string.btn_title_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    dialog.cancel();
                }
            }).create();
            b.show();
        }
        return flag;
    }

    /**
     * 初始化配置
     */
    private void initShareConfig() {
        glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
        userName.setText(glob.sp.getString("user", ""));
        password.setText(glob.sp.getString("psw", ""));
        savePsw.setChecked(glob.sp.getBoolean("savePsw", false));
        autoLogin.setChecked(glob.sp.getBoolean("autoLogin", false));
    }

    /**
     * 点击事件
     */
    public void onClick(View v) {
		/*登录*/
        if (v.getId() == R.id.dologin) {
            if (Istimeout) {
                Istimeout = false;
                Testtimehandler.postDelayed(new Logintimecleanthread(), 5000);
			/*检查网络*/
                if (UtilTool.CheckNetwork(this)) {
				/*判空*/
                    if (!"".equals(userName.getText().toString()) && !"".equals(password.getText().toString())) {
					/*保存密码*/
                        if (savePsw.isChecked()) {
                            glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
                            SharedPreferences.Editor editor = glob.sp.edit();
                            editor.putString("user", userName.getText().toString());
                            editor.putString("psw", password.getText().toString());
                            editor.putInt("type", 1);
                            editor.commit();
                        } else {
                            glob.sp = getSharedPreferences("UserInfo", MODE_PRIVATE);
                            glob.sp.edit().putString("user", userName.getText().toString()).commit();
                            glob.sp.edit().putString("psw", password.getText().toString()).commit();
                        }

					/*进度*/
                        progressDialog.setMessage(getResources().getString(R.string.Login_requestServerTips));
                        progressDialog.show();
					/*登录 */
                        new Thread(new LoginThread()).start();
                    } else {
                        Toast.makeText(Login.this, R.string.Login_notNull, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(Login.this, R.string.Login_net_work_message, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (popwindowisshow) {
            host_popwindow.dismiss();
            popwindowisshow = false;
        } else {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                finish();
            }
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * 创建Handler
     */
    private Handler createpopHandler() {
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                progressDialog.dismiss();
                Bundle b = message.getData();
                switch (message.what) {
                    case 0: {
                        String msg = b.getString("msg");
                        if (msg == null && hosts != null && hosts.size() > 0) {
//                            String host = glob.sp.getString("host", HttpRequestClient.defaultHost);
                            if (hostNames == null) {
                                hostNames = new String[hosts.size()];
                                for (int i = 0; i < hosts.size(); i++) {
                                    System.out.println("lan:" + config.locale.getLanguage());
                                    if (config.locale.getLanguage().contains("zh")) {
                                        hostNames[i] = hosts.get(i).getName();
                                    } else {
                                        hostNames[i] = hosts.get(i).getEnname();
                                    }
                                }
                                popwindowisshow = true;
                                textview[0] = (TextView) myview.findViewById(R.id.hostset_textviewb1);
                                textview[1] = (TextView) myview.findViewById(R.id.hostset_textviewb2);
                                for (i = 2; i < hostNames.length + 2; i++) {
                                    textview[i] = new TextView(getApplicationContext());
                                    textview[i].setHeight(100);
                                    textview[i].setText(hostNames[i - 2]);
                                    textview[i].setTextSize(15);
                                    textview[i].setTextColor(0xffa0a0a0);
                                    textview[i].setGravity(Gravity.CENTER);
                                    hostset_linear.addView(textview[i], i);
                                }
                                textview[i] = (TextView) myview.findViewById(R.id.hostset_textviewb3);
                                textview[i + 1] = (TextView) myview.findViewById(R.id.hostset_textviewb4);
                                textview[2].setTextColor(0xff000000);
                                textview[2].setTextSize(30);
                                textview[3].setTextColor(0xff959595);
                                textview[3].setTextSize(20);
                                textview[4].setTextColor(0xffa0a0a0);
                                textview[4].setTextSize(10);

                                mytimer.schedule(new TimerTask() {
                                    @Override
                                    public void run() {
                                        oldposition = position;
                                        position = myscrollview.Getposition();
                                        ColorPosition = SizePosition = position / 100;
                                        size = position % 100;
                                        size = (float) (size * 0.1);
                                        up = myscrollview.up;
                                        down = myscrollview.down;
                                        move = myscrollview.move;
                                        if (size > 5) {
                                            ColorPosition++;
                                        }
                                        Message msg = new Message();
                                        if (down && move && Math.abs(oldposition - position) >= 5) {
                                            if (oldposition < position) {
                                                msg.what = 2;
                                                myhandler.sendMessage(msg);
                                            }
                                            if (oldposition > position) {
                                                msg.what = -2;
                                                myhandler.sendMessage(msg);
                                            }
                                        }
                                        if (down && move && Math.abs(oldposition - position) < 5 && oldposition != position) {//这个地方的oldposition要记录
                                            if (ErrorCount == 0) {
                                                Errorposition = oldposition;
                                            }
                                            ErrorCount++;
                                            if (ErrorCount >= 5) {
                                                if (Errorposition < position) {
                                                    msg.what = 2;
                                                    myhandler.sendMessage(msg);
                                                }
                                                if (Errorposition > position) {
                                                    msg.what = -2;
                                                    myhandler.sendMessage(msg);
                                                }
                                                ErrorCount = 0;
                                            }
                                        }
                                        if (up) {
                                            if (oldposition < position) {
                                                msg.what = 2;
                                                myhandler.sendMessage(msg);
                                            }
                                            if (oldposition > position) {
                                                msg.what = -2;
                                                myhandler.sendMessage(msg);
                                            }
                                            if (oldposition == position) {
                                                msg.what = 0;
                                                myhandler.sendMessage(msg);
                                            }
                                        }
                                        up = false;
                                        down = false;
                                        move = false;
                                    }

                                }, 20, 2);
                            }
                            host_popwindow.showAtLocation(Login.this.findViewById(R.id.image_logolin), Gravity.BOTTOM, 0, 0);
                            //myscrollview.Setpoisition(position*70+2);
                            WindowManager.LayoutParams lp = getWindow().getAttributes();
                            lp.alpha = 0.3f;
                            getWindow().setAttributes(lp);
                            host_popwindow.setOnDismissListener(new OnDismissListener() {

                                @Override
                                public void onDismiss() {
                                    popwindowisshow = false;
                                    WindowManager.LayoutParams lp = getWindow().getAttributes();
                                    lp.alpha = 1f;
                                    getWindow().setAttributes(lp);
                                }
                            });
                        } else {
                            Toast.makeText(Login.this, msg, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                }
                super.handleMessage(message);
            }
        };
        return handler;
    }

    /**
     * 登录
     */
    class LoginThread implements Runnable {
        public void run() {
            Message message = new Message();
            message.what = 0;
            Bundle b = new Bundle();

			/*消息 */
            String msg = null;

            SResponse response = HttpRequestClient.login(userName.getText().toString(), password.getText().toString());
            if (response.getCode() != SProtocol.SUCCESS) {
                msg = SProtocol.getFailMessage(response.getCode(), response.getMessage());
            } else {

                if (response.getResult() != null) {
                    glob.vehNoFLogin = false;
//					glob.username = userName.getText().toString();
                    glob.account = (Account) response.getResult();
                } else {
                    glob.vehNoFLogin = true;
                }
            }

            b.putString("msg", msg);
            message.setData(b);
            handler.sendMessage(message);
        }
    }

    /**
     * 获取服务器列表
     */
    class GetHostThread extends Thread {
        @SuppressWarnings("unchecked")
        public void run() {
            Bundle b = new Bundle();
            SResponse response = HttpRequestClient.getHostList();
            if (response.getCode() == SProtocol.SUCCESS) {
                hosts = (List<Host>) response.getResult();
            } else {
                b.putString("msg", SProtocol.getFailMessage(response.getCode(), (String) response.getResult()));
            }

            Message message = new Message();
            message.setData(b);
            message.what = 0;
            pophandler.sendMessage(message);
        }
    }

    class Logintimecleanthread extends Thread {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 0;
            Testtimehandler.sendMessage(message);
            super.run();
        }
    }

    class Poptimecleanthread extends Thread {
        @Override
        public void run() {
            Message message = new Message();
            message.what = 1;
            Testtimehandler.sendMessage(message);
            super.run();
        }
    }
}