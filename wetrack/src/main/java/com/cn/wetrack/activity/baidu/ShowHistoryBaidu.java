package com.cn.wetrack.activity.baidu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;
import com.baidu.mapapi.utils.CoordinateConverter.CoordType;
import com.cn.wetrack.R;
import com.cn.wetrack.activity.SWApplication;
import com.cn.wetrack.activity.VehicleList;
import com.cn.wetrack.entity.History;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;

/**
 * 历史轨迹
 */
public class ShowHistoryBaidu extends Activity {
	private SWApplication glob;
	private MapView mapview;// 百度地图控件
	private BaiduMap mBaiDuMap = null;// 管理地图
	private ImageButton back;// 返回按钮
	private Button reverse, speed;// 快进，快退按钮
	private Button playspeed, playandpause;// 播放速度，播放暂停按钮
	private ImageButton layerbutton;// 图层按钮
	private List<LatLng> points = new ArrayList<LatLng>();// 所有点集合
	private List<LatLng> temppoints = new ArrayList<LatLng>();// 临时停留点集合
	private BitmapDescriptor startbit, endbit, currentbit;
	private SeekBar seekbar = null;//进度条
	private int Index = 0;//当前的索引值
	private Thread PlayThread = null;//播放线程
	private Timer Playhistory = null;
	private Marker currentmarker=null;//当前的标记
	private Boolean isplaying=false;//是否在播放
	private int PlaySpeed=500;//播放速度
	private Intent intent=new Intent();
	private Boolean mapmode=true;//地图的状态，true表示地图模式，false表示卫星模式
	 // 将google地图、soso地图、aliyun地图、mapabc地图和amap地图// 所用坐标转换成百度坐标
//	private  CoordinateConverter converter  = new CoordinateConverter();
	private Boolean IsFirstInActivity=true;
	private DisplayMetrics metric;//屏幕管理
	private int width,height;//屏幕宽度，高度
	private TextView firsttime,secondtime,timeandangle;
	private SimpleDateFormat dateformat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");// 字符转换
	private Calendar starcalendar = Calendar.getInstance();// 开始的时间
	private Calendar endcalendar = Calendar.getInstance();// 结束的时间
	private java.util.Date date = null;// 中间变量
//	private MappointUtil maputil=new MappointUtil();
//LatLng
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		SDKInitializer.initialize(getApplicationContext());
		/*获取屏幕参数*/
		metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		width = metric.widthPixels; // 屏幕宽度（像素）
		height = metric.heightPixels; // 屏幕高度（像素）
		setContentView(R.layout.showhistory_baidu);
		glob = (SWApplication) getApplicationContext();
		glob.sp = getSharedPreferences("UserInfo", 0);
		PlayThread=new PlayHistoryThread();
		mapview = (MapView) this.findViewById(R.id.showhistory_baidumap);
		//设置是否显示缩放控件
		mapview.showZoomControls(false);
		seekbar = (SeekBar) this.findViewById(R.id.showhistory_seekbar);
		firsttime=(TextView)this.findViewById(R.id.showhistory_firsttime);
		secondtime=(TextView)this.findViewById(R.id.showhistory_secondtime);
		timeandangle=(TextView)this.findViewById(R.id.showhistory_timeandangle);
		timeandangle.setSingleLine(true);
		seekbar.setMax(glob.historys.size() - 1);
			try {
				date = dateformat.parse(glob.historys.get(0).getTime());
				starcalendar.setTime(date);
				date = dateformat.parse(glob.historys.get(glob.historys.size()-1).getTime());
				endcalendar.setTime(date);
				firsttime.setText("00:00");
				secondtime.setText(GetTime());
			} catch (java.text.ParseException e) {
				e.printStackTrace();
			}
			
		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				Index=seekbar.getProgress();
				AddCurrentmarker();
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {

			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {

			}
		});
		mBaiDuMap = mapview.getMap();
		/*获取位图**/
		startbit = BitmapDescriptorFactory
				.fromResource(R.drawable.line_start);
		endbit = BitmapDescriptorFactory
				.fromResource(R.drawable.line_end);
		currentbit = BitmapDescriptorFactory
				.fromResource(R.drawable.history_car);
		/*添加点集**/
		for (History history : glob.historys) {
//			converter.from(CoordType.GPS);
			LatLng sourceLatLng=new LatLng(history.getLatitude(),
					history.getLongitude());
			// sourceLatLng待转换坐标  
//			converter.coord(sourceLatLng);
//			LatLng desLatLng = converter.convert();
			points.add(sourceLatLng);
		}
		if(points.size()>=2)
		Addallline();
		AddStartmarker();
		AddEndmarker();
		// 返回
		back = (ImageButton) this.findViewById(R.id.showhistory_backRe);
		back.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		// 切换车辆
		playspeed = (Button) this.findViewById(R.id.showhistory_vehicle);
		playspeed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				intent.setClass(ShowHistoryBaidu.this, VehicleList.class);
				startActivity(intent);
				finish();

			}
		});
		// 图层按钮
		layerbutton = (ImageButton) this
				.findViewById(R.id.showhistory_layerButton);
		layerbutton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(mapmode){
					mBaiDuMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
					mapmode=false;
				}
				else{
					mBaiDuMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
					mapmode=true;
				}

			}
		});
		// 播放按钮
		playandpause = (Button) this
				.findViewById(R.id.showhistory_playandpause);
		playandpause.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(!isplaying){
					playandpause.setBackgroundResource(R.drawable.showhistory_pause);
					isplaying=true;
					playhandler.post(PlayThread);
					
				}
				else{
					playandpause.setBackgroundResource(R.drawable.showhistory_playandpause_small);
					isplaying=false;
				}

			}
		});
		// 快退
		reverse = (Button) this.findViewById(R.id.showhistory_reversebtn);
		reverse.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if(PlaySpeed<1000){
					PlaySpeed=PlaySpeed+100;
				}
				

			}
		});
		// 快进
		speed = (Button) this.findViewById(R.id.showhistory_speedbtn);
		speed.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				if(PlaySpeed>100){
					PlaySpeed=PlaySpeed-100;
				}
				

			}
		});

	}

	public void Addallline() {
		OverlayOptions ooPolyline = new PolylineOptions().width(5)
				.color(0xff00ff00).points(points);
		mBaiDuMap.addOverlay(ooPolyline);
		mBaiDuMap.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom((points
				.get(0)),13));
	}

	public void AddStartmarker() {
		mBaiDuMap.addOverlay(new MarkerOptions().position(points.get(0)).icon(
				startbit));
	}

	public void AddCurrentmarker(){
		/**
		 * 不必每次都移动镜头，只需要在marker移动到屏幕外面去了在移动镜头
		 * */
		if(currentmarker==null){
			currentmarker=(Marker)mBaiDuMap.addOverlay(new MarkerOptions().position(points.get(Index)).icon(
					currentbit));
			currentmarker.setAnchor(0.5f, 0.5f);
			currentmarker.setRotate(360-Integer.parseInt(glob.historys.get(Index).getAngle()));
		}
		else{
			currentmarker.setPosition(points.get(Index));
			currentmarker.setAnchor(0.5f, 0.5f);
			currentmarker.setRotate(360-Integer.parseInt(glob.historys.get(Index).getAngle()));
		}
		String s="";
		s += glob.historys.get(Index).getTime() + "  " +
				getResources().getString(R.string.speed) + glob.historys.get(Index).getVelocity() + "km/h" + "  " +
				getResources().getString(R.string.car_mileage) + ":" + glob.historys.get(Index).getMiles() + "km";
		timeandangle.setText(s);
		if(!IsInScreen(points.get(Index))){
			mBaiDuMap.setMapStatus(MapStatusUpdateFactory
					.newLatLng(points.get(Index)));
			}
	}

	public void AddEndmarker() {
		mBaiDuMap.addOverlay(new MarkerOptions().position(
				points.get(glob.historys.size() - 1)).icon(endbit));
	}

	public class PlayHistoryThread extends Thread {
		@Override
		public void run() {
			Message msg = new Message();
			msg.what = 1;
			playhandler.sendMessage(msg);
		// super.run();
		}
	}

	/** 返回鍵 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
		if (keyCode == KeyEvent.KEYCODE_BACK ){
			finish();
//		}
			
		}
		return true;
	}

	private Handler playhandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				if (Index < (glob.historys.size() - 1)) {
					seekbar.setProgress(Index);
					Index++;
					AddCurrentmarker();
//					SystemClock.sleep(550);
				}
				else{
					currentmarker.remove();
					currentmarker=null;
					isplaying=false;
					Index=0;
					
				}
				if(isplaying){
				playhandler.postDelayed(PlayThread, PlaySpeed);
				}
				break;
			case 2:
				break;
			}
		}
	};
	protected void onDestroy() {
		isplaying=false;
		playhandler.removeCallbacks(PlayThread);
		mapview.onDestroy();
		super.onDestroy();
		
	};
	@Override
	protected void onPause() {
		isplaying=false;
//		}
		mapview.onPause();
		super.onPause();
	}
	@Override
	protected void onResume() {
		if(IsFirstInActivity){
			IsFirstInActivity=false;
		}
		else{
			isplaying=true;
			playhandler.post(PlayThread);
		
		}
		mapview.onResume();
		super.onResume();
	}
	@Override
	protected void onStop() {
		super.onStop();
	}
	@Override
	protected void onStart() {
		super.onStart();
	}
	/**
	 * 保存数据
	 * */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}
	/**
	 * 判断marker是否在屏幕内
	 * */
	public Boolean IsInScreen(LatLng Mappoint){
		Point point=mBaiDuMap.getProjection().toScreenLocation(Mappoint);
		if(point.x<0||point.y<0||point.x>width||point.y>height){
			return false;
		}
		else{
		return true;
		}
	}
	public String getOrientation(int orientation) {
		String orientation_zh = "";
	    
	    if (orientation == 0) {
	        orientation_zh += getResources().getString(R.string.Direction_north);
	    }
	    if (orientation > 0 && orientation < 45) {
	        orientation_zh += getResources().getString(R.string.Direction_northeastbynorth);
	    }
	    if (orientation == 45) {
	        orientation_zh += getResources().getString(R.string.Direction_northeast);
	    }
	    if (orientation > 45 && orientation < 90) {
	        orientation_zh += getResources().getString(R.string.Direction_northeastbyesat);
	    }
	    if (orientation == 90) {
	        orientation_zh += getResources().getString(R.string.Direction_east);
	    }
	    if (orientation > 90 && orientation < 135) {
	        orientation_zh += getResources().getString(R.string.Direction_southeastbyeast);
	    }
	    if (orientation == 135) {
	        orientation_zh += getResources().getString(R.string.Direction_southeast);
	    }
	    if (orientation > 135 && orientation < 180) {
	        orientation_zh += getResources().getString(R.string.Direction_southeastbysouth);
	    }
	    if (orientation == 180) {
	        orientation_zh += getResources().getString(R.string.Direction_south);
	    }
	    if (orientation > 180 && orientation < 225) {
	        orientation_zh += getResources().getString(R.string.Direction_southbysouthwest);
	    }
	    if (orientation == 225) {
	        orientation_zh += getResources().getString(R.string.Direction_southwest);
	    }
	    if (orientation > 225 && orientation < 270) {
	        orientation_zh +=getResources().getString(R.string.Direction_southwestbywest);
	    }
	    if (orientation == 270) {
	        orientation_zh += getResources().getString(R.string.Direction_west);
	    }
	    if (orientation > 270 && orientation < 315) {
	        orientation_zh += getResources().getString(R.string.Direction_northwestbywest);
	    }
	    if (orientation == 315) {
	        orientation_zh += getResources().getString(R.string.Direction_northwest);
	    }
	    if (orientation > 315 && orientation < 360) {
	        orientation_zh += getResources().getString(R.string.Direction_northbynorthwest);
	    }
	    return orientation_zh;
	}
	public String GetTime(){
		String time = "";
		long difference=0,hour=0,minute=0;
		difference=endcalendar.getTimeInMillis()-starcalendar.getTimeInMillis();
		hour=difference/(3600*1000);
		difference=difference%(3600*1000);
		minute=difference/(60*1000);
		time=""+hour+":"+minute;
		return time;
	}

}
