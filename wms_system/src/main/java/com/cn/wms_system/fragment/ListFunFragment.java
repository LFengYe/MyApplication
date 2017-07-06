package com.cn.wms_system.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.alibaba.fastjson.JSONObject;
import com.cn.wms_system_new.R;


public class ListFunFragment extends Fragment {

	private JSONObject menuJson;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_list_fun, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Button button1 = (Button) getActivity().findViewById(R.id.button1);
		button1.setText(R.string.stocking_manager);
		
		Button button2 = (Button) getActivity().findViewById(R.id.button2);
		button2.setText(R.string.picking_manager);
		
		Button button3 = (Button) getActivity().findViewById(R.id.button3);
		button3.setText(R.string.distribution_manager);
		
		Button button4 = (Button) getActivity().findViewById(R.id.button4);
		button4.setText(R.string.pick_return);
		
		Button button5 = (Button) getActivity().findViewById(R.id.button5);
		button5.setText(R.string.stock_manager);
		
		Button button6 = (Button) getActivity().findViewById(R.id.button6);
		button6.setText(R.string.system_setting);
		
		Button button7 = (Button) getActivity().findViewById(R.id.button7);
		button7.setText(R.string.delivery_receipt);
	}

	public JSONObject getMenuJson() {
		return menuJson;
	}

	public void setMenuJson(JSONObject menuJson) {
		this.menuJson = menuJson;
	}
}
