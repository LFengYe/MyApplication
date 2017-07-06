package com.cxgps.vehicle.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxgps.vehicle.R;
import com.cxgps.vehicle.base.BaseFragment;
import com.cxgps.vehicle.bean.CarLocationBean;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by taosong on 16/7/21.
 */
public class CarDetailFragment extends BaseFragment {


    @Bind(R.id.ivNews)
    ImageView  mIvImg;

    @Bind(R.id.tv_title)
    TextView  mTitle;

    @Bind(R.id.tv_software_des)
    TextView  mDesc;



    @Bind(R.id.item_cars_sim)
    TextView  mSim;


    @Bind(R.id.item_cars_time)
    TextView  mTime;


    @Bind(R.id.item_cars_state)
    TextView  mState;


    @Bind(R.id.item_cars_sot)
    TextView  mSot;


    @Bind(R.id.item_cars_ele)
    TextView  mEle;


    @Bind(R.id.item_cars_mile)
    TextView  mMile;


    @Bind(R.id.item_cars_gps)
    TextView  mGps;

    @Bind(R.id.item_cars_address)
    TextView  mAddress;


    private CarLocationBean carLocation = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.main_title_cardetail));

        Bundle  bundle = getArguments();
        try {
            carLocation = (CarLocationBean) bundle.getSerializable("carLocation");

        }catch (Exception e){
            e.printStackTrace();
            carLocation = null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cars_detail, container,
                false);
        ButterKnife.bind(this, view);
        initView(view);
        initData();
        return view;
    }
    @Override
    public void initView(View view) {
        super.initView(view);
    }

    @Override
    public void initData() {
        super.initData();

        if (carLocation!= null){
            mIvImg.setBackgroundResource( R.mipmap.chedui_car);
            mTitle.setText(String.format(getString(R.string.car_detail_number), carLocation.getCarNumber()));
            mDesc.setText(String.format(getString(R.string.car_detail_systemno),carLocation.getSystemNo()));

            mSim.setText(String.format(getString(R.string.car_detail_sim),carLocation.getSimID()));
            mTime.setText(String.format(getString(R.string.car_detail_time),carLocation.getNowDate(),carLocation.getCarAcc()));
            mState.setText(String.format(getString(R.string.car_detail_state),carLocation.getCarStateType(),carLocation.getStoptime()));

            mSot.setText(String.format(getString(R.string.car_detail_sot),carLocation.getCarSpeed(),carLocation.getCarOil(),carLocation.getCarTempure()));
            mEle.setText(String.format(getString(R.string.car_detail_ele),carLocation.getCarVoltage(),carLocation.getCarElec()));
            mMile.setText(String.format(getString(R.string.car_detail_mile),carLocation.getNowMile(),carLocation.getCountMile()));
            mGps.setText(String.format(getString(R.string.car_detail_gps),carLocation.getLat(),carLocation.getLng()));

            mAddress.setText(String.format(getString(R.string.car_detail_address),carLocation.getCarAddress()));
        }

    }
}
