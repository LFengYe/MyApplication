package com.cn.carigps.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.cn.carigps.R;
import com.cn.carigps.entity.Location;
import com.cn.carigps.entity.Vehicle;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 二级下拉适配器
 */
public class ExpandAdapter extends BaseExpandableListAdapter {

	private Context mContext;
	private LayoutInflater mInflater = null;
	private ArrayList<String> motorcadeNameArray = null;
	private List<List<Vehicle>> vehicleListData = null;
	private Map<String, Location> vehNoLocationMap = null;
	private GroupBtnListener groupBtnListener = null;
	private int method;
	private boolean isQuery;

	// 参数vehicleListData车队树数据 motorcadeNameArray车队名数组
	public ExpandAdapter(Context ctx, List<List<Vehicle>> vehicleListData, GroupBtnListener groupBtnListener,
			ArrayList<String> motorcadeNameArray, Map<String, Location> vehNoLocationMap, int method, boolean isQuery) {
		mContext = ctx;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		this.motorcadeNameArray = motorcadeNameArray;
		this.vehicleListData = vehicleListData;
		this.vehNoLocationMap = vehNoLocationMap;
		this.groupBtnListener = groupBtnListener;
		this.method = method;
		this.isQuery = isQuery;
	}

	public void setData(List<List<Vehicle>> vehicleListData) {
		this.vehicleListData = vehicleListData;
	}

	@Override
	public int getGroupCount() {
		// Log.d("size", "size:"+vehicleListData.size());
		return this.vehicleListData.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return this.vehicleListData.get(groupPosition).size();
	}

	@Override
	public List<Vehicle> getGroup(int groupPosition) {
		return this.vehicleListData.get(groupPosition);
	}

	@Override
	public Vehicle getChild(int groupPosition, int childPosition) {
		return this.vehicleListData.get(groupPosition).get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public View getGroupView(final int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.group_item_layout, null);
		}

		GroupViewHolder holder = new GroupViewHolder();
		holder.mGroupName = (TextView) convertView
				.findViewById(R.id.group_name);
		holder.mGroupName.setText(motorcadeNameArray.get(groupPosition));
		holder.mGroupCount = (TextView) convertView
				.findViewById(R.id.group_count);
		holder.mGroupCount.setText("[" + vehicleListData.get(groupPosition).size() + "]");
		holder.mQueryTeam = (TextView) convertView.findViewById(R.id.query_team);
		holder.mQueryTeam.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				groupBtnListener.searchGroup(groupPosition);
			}
		});
		if (isQuery) {
			holder.mQueryTeam.setVisibility(View.VISIBLE);
		}

		return convertView;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.child_item_layout, null);
		}
		ChildViewHolder holder = new ChildViewHolder();
		holder.mChildName = (TextView) convertView.findViewById(R.id.item_name);
		holder.mChildName.setText(getChild(groupPosition, childPosition)
				.getVehNoF());
		holder.mChildState = (TextView) convertView
				.findViewById(R.id.child_state);
		holder.mChildState.setVisibility(View.VISIBLE);

		if (method == 3) {
			holder.mChildState.setText(mContext
					.getString(R.string.vehicle_offline));
		} else if (method == 1) {
			float speed = vehNoLocationMap.get(
					getChild(groupPosition, childPosition).getVehNoF())
					.getVelocity();
			if (speed < 2) {
				holder.mChildState.setText(mContext
						.getString(R.string.vehicle_static));
			} else {
				holder.mChildState.setText(speed + "km/h");
			}
		} else if (method == 0) {
            Vehicle vehicle = getChild(groupPosition, childPosition);
            if (vehicle.getCurStatus() == 3) {
                holder.mChildState.setText(mContext.getString(R.string.vehicle_offline));
            } else {
				Location location = vehNoLocationMap.get(vehicle.getVehNoF());
				if (location != null) {
					Float speed = Float.valueOf(vehNoLocationMap.get(vehicle.getVehNoF()).getVelocity());
					if (speed < 2) {
						holder.mChildState.setText(mContext.getString(R.string.vehicle_static));
					} else {
						holder.mChildState.setText(speed + "km/h");
					}
				} else {
					holder.mChildState.setText(mContext.getString(R.string.vehicle_offline));
				}
            }
        }


		holder.mChildIsoverdue = (TextView) convertView
				.findViewById(R.id.child_IsOverdue);
		
		if (getChild(groupPosition, childPosition).getIsOverdue()) {
			holder.mChildState.setVisibility(View.INVISIBLE);
			holder.mChildIsoverdue.setText(mContext
					.getString(R.string.vehicle_overdue));
		} else {
			holder.mChildIsoverdue.setText(mContext
					.getString(R.string.vehicle_availability));
		}
		return convertView;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		/* 很重要：实现ChildView点击事件，必须返回true */
		if (method == 4) {
			Toast.makeText(mContext, R.string.vehicle_overdue, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}

	private class GroupViewHolder {
		TextView mGroupName;
		TextView mGroupCount;
		TextView mQueryTeam;
	}

	private class ChildViewHolder {
		TextView mChildName;
		TextView mChildState;
		TextView mChildIsoverdue;
	}

	public interface GroupBtnListener {
		void searchGroup(int position);
	}
}
