package com.qixiaoyi.cariyou;

import android.app.Application;
import android.content.Context;

import io.realm.DynamicRealm;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmMigration;

/**
 * --------------------------------------------------
 * 版       权 ：易成勇
 * <p>
 * 作       者： 易成勇
 * <p>
 * 文件名：CariyouAPP
 * <p>
 * 创 建 日 期 ： 2017/3/1 0001  12:46
 * <p>
 * 描      述 ：
 * <p>
 * <p>
 * 修 订 历 史:
 * <p>
 * --------------------------------------------------
 */

public class CariyouAPP extends Application {
    public static Context mAppContext;
    @Override
    public void onCreate()
    {

        super.onCreate();
        mAppContext = this;
        // 配置Realm数据库
        RealmConfiguration configuration = new RealmConfiguration
                .Builder(this)
                .deleteRealmIfMigrationNeeded()
                .schemaVersion(6)
                .migration(new RealmMigration()
                {

                    @Override
                    public void migrate(DynamicRealm realm, long oldVersion, long newVersion)
                    {

                    }
                }).build();

        Realm.setDefaultConfiguration(configuration);

    }


    public static Context getContext()
    {

        return mAppContext;
    }
}
