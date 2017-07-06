package com.cn.wms_system_new.component;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by fuyzh on 16/9/2.
 */
public class AutoUpdate {
    private final int SUCCESS = 0;// 表示下载成功
    private final int UNMOUNT = 1;// 没有内存卡
    private final int MURLEXE = 2;
    private final int FNFEXE = 3;// 没有找到文件
    private final int IOEXE = 4;// IO异常

    private Context context;
    private CustomProgress progressDialog;
    private String apkUrl;
    private String versionName;

    public AutoUpdate(Context context) {
        this.context = context;
    }

    private Handler APKHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            progressDialog.dismiss();
            if (msg.what == 0) {
                installApk((File) msg.obj);
            } else {
                Toast.makeText(context, "更新失败!", Toast.LENGTH_LONG).show();
            }
        }

    };

    public int getVersionName(String packName) {
        // 用来管理手机的APK
        PackageManager pm = context.getPackageManager();
        try {
            // 得到知道APK的清单文件
            PackageInfo info = pm.getPackageInfo(packName, PackageManager.GET_ACTIVITIES);
            return info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void showUpDate(String apkUrl, String versionName) {
        this.apkUrl = apkUrl;
        this.versionName = versionName;

        // 对话框的上下文 是Activity的class,AlertDialog是Activity的一部分
        NormalDialog dialog;
        NormalDialog.Builder builder = new NormalDialog.Builder(context);
        // 让用户禁用取消操作
        builder.setMessage("新版本是否更新？");
        builder.setTitle("更新");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }

        });
        builder.setPositiveButton("更新", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 下载APK 并且安装
                dialog.dismiss();
                progressDialog = CustomProgress.show(context, "版本更新中", false, null);
                downLoadApk();

            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void downLoadApk() {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (Environment.getExternalStorageState().equals(
                            Environment.MEDIA_MOUNTED)) {
                        URL url = new URL(apkUrl);
                        HttpURLConnection conn = (HttpURLConnection) url
                                .openConnection();
                        conn.setConnectTimeout(5000);
                        InputStream is = conn.getInputStream();
                        File file = new File(
                                Environment.getExternalStorageDirectory(),
                                "update_" + versionName + ".apk");

                        FileOutputStream fos = new FileOutputStream(file);
                        BufferedInputStream bis = new BufferedInputStream(is);
                        byte[] buffer = new byte[1024];
                        int len;
                        int total = 0;
                        while ((len = bis.read(buffer)) != -1) {
                            fos.write(buffer, 0, len);
                            total += len;
                        }
                        fos.close();
                        bis.close();
                        is.close();
                        Message msg = new Message();
                        msg.what = SUCCESS;
                        msg.obj = file;
                        APKHandler.sendMessage(msg);
                    } else {
                        Message msg = new Message();
                        msg.what = UNMOUNT;
                        APKHandler.sendMessage(msg);
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = MURLEXE;
                    APKHandler.sendMessage(msg);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = FNFEXE;
                    APKHandler.sendMessage(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = IOEXE;
                    APKHandler.sendMessage(msg);
                }
            }
        }.start();
    }

    private void installApk(File apkfile) {
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
                "application/vnd.android.package-archive");
        context.startActivity(i);
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}
