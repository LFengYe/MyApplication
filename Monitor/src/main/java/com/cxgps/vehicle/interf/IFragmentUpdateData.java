package com.cxgps.vehicle.interf;

import com.cxgps.vehicle.bean.HistoryInfo;

/**
 * Created by taosong on 16/8/4.
 */
public interface IFragmentUpdateData {

    public void fragmentData(int progress, int playstate, HistoryInfo bean);
}
