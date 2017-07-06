package com.cxgps.vehicle.bean;

import com.cxgps.vehicle.R;
import com.cxgps.vehicle.fragment.BDSearchAddressFragment;
import com.cxgps.vehicle.fragment.CarDetailFragment;
import com.cxgps.vehicle.fragment.CarsListFragment;
import com.cxgps.vehicle.fragment.FeedBackFragment;
import com.cxgps.vehicle.fragment.MapAndLanFragment;
import com.cxgps.vehicle.fragment.MyInformationFragment;
import com.cxgps.vehicle.fragment.NavigationFragment;
import com.cxgps.vehicle.fragment.NoticesMsgFragment;
import com.cxgps.vehicle.fragment.SelectDateFragment;
import com.cxgps.vehicle.fragment.SettingsFragment;

/**
 * Created by taosong on 16/12/22.
 */

public enum  SimpleBackPage {

    NAVIGATION(1, R.string.main_slide_navi, NavigationFragment.class),
    CARSLIST(2, R.string.main_slide_cars,CarsListFragment.class)

    ,MY_INFORMATION(3, R.string.main_title_persion, MyInformationFragment.class),
    SETTING(4, R.string.main_slide_settings,SettingsFragment.class),
    FEEDBACK(5, R.string.main_slide_adverise,FeedBackFragment.class),
    CARDETAIL(6, R.string.main_title_cardetail, CarDetailFragment.class),
    SELECTDATE(7, R.string.main_select_time, SelectDateFragment.class),
    SEARCHADDRESSLIST(8,R.string.app_name, BDSearchAddressFragment.class),
    NOTICESMSG(9,R.string.main_slide_notice, NoticesMsgFragment.class),
    SWITCHMAP(10,R.string.user_map_title, MapAndLanFragment.class),
    SWITCHLAN(11,R.string.user_language_title, MapAndLanFragment.class);

    private int title;
    private Class<?> clz;
    private int value;

    private SimpleBackPage(int value, int title, Class<?> clz) {
        this.value = value;
        this.title = title;
        this.clz = clz;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public Class<?> getClz() {
        return clz;
    }

    public int getTitle() {
        return title;
    }

    public void setClz(Class<?> clz) {
        this.clz = clz;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public static SimpleBackPage getPageByValue(int val) {
        for (SimpleBackPage p : values()) {
            if (p.getValue() == val)
                return p;
        }
        return null;
    }
}
