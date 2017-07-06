package com.cxgps.vehicle.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cxgps.vehicle.R;
import com.cxgps.vehicle.bean.CarLocationBean;
import com.cxgps.vehicle.interf.IGMarkerInterf;

import butterknife.ButterKnife;

/**
 * Created by taosong on 17/1/3.
 */

public class GMarkerPopFragment  extends Fragment implements IGMarkerInterf {



    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_marker_pop, container,
                false);
        ButterKnife.bind(this, view);




        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void loadCarLocation(CarLocationBean locationBean) {

    }
}
