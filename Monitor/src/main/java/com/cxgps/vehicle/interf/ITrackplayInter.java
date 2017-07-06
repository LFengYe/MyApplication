package com.cxgps.vehicle.interf;

import android.os.Bundle;

/**
 * Created by taosong on 16/7/23.
 */
public interface ITrackplayInter {

    public  boolean changeRoad();

    public  boolean changeMaptype();

    public int startPlay();

    public  void historyData(Bundle bundle);

    public  void setPlayProgress(int playProgress);

    public void getSpeedProgress(int speedPro);


    public void setIFragmentUpdateListener(IFragmentUpdateData iFragmentUpdateListener);


    public  void backAndGoWithTimmer(int  flag);

}
