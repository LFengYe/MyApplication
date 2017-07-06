package com.cxgps.vehicle.interf;

import com.cxgps.vehicle.bean.CarLocationBean;

/**
 * Created by taosong on 16/7/26.
 */
public interface IZuiZongInter {

    public  boolean changeRoad();

    public  boolean changeMaptype();

    public  void   loadLocation(CarLocationBean  locationBean);
}
