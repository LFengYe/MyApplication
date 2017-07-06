package com.cxgps.vehicle.interf;

/**
 * Created by taosong on 16/7/14.
 */
public interface IMonitorInter {


    public  boolean changeRoad();

    public  boolean changeMaptype();

    public  void changeLocation(int type);

    public  void changeBefore();

    public  void changeAfter();

    public  boolean changeAdd();

    public  boolean changeReduct();

    public void reflushLocation(String systemNo);

}
