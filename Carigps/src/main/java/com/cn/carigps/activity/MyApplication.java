package com.cn.carigps.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;

import com.cn.carigps.entity.Account;
import com.cn.carigps.entity.Alarm;
import com.cn.carigps.entity.Geofence;
import com.cn.carigps.entity.History;
import com.cn.carigps.entity.Location;
import com.cn.carigps.entity.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 全局变量
 */
@SuppressLint("UseSparseArrays")
public class MyApplication extends MultiDexApplication {
	/** 存放共享数据 */
	public SharedPreferences sp;
	/** 定位数据刷新时间, 单位秒*/
	public int refreshTime;
	/** 地图类型:0 谷歌地图 | 1 百度地图*/
	public int mapType;
	/**切换帐号标记*/
	public boolean switchAccount=false;
	/**欢迎页面检查更新*/
//	public static boolean welcomeUpdate=false;
	/**升级取消欢迎页面退出*/
	public static boolean welcomeDismiss=false;
	/**切换帐号不自动登录操作*/
	public boolean switchAutoLogin=true;
	/**是否车牌登录*/
	public boolean vehNoFLogin=false;
	/**车队树数据*/
	public List<List<Vehicle>> vehicleListData = new ArrayList<>();
	/**系统编号车牌号哈希表*/
	public Map<String,String> systemNoVehNoMap= new HashMap<>();
	/**车牌号车辆位置哈希表*/
	public Map<String,Location> vehNoLocationMap= new HashMap<>();
	/**车牌号车辆位置临时哈希表*/
	public Map<String,Location> TempvehNoLocationMap= new HashMap<>();
	/**用户信息*/
	public Account account=null;
	/**车队名数组*/
	 public String[] motorcadeNameArray=null;
	 /**当前操作车辆*/
	 public Vehicle curVehicle=null;
	 /**当前操作菜单序号*/
	 public int menuIndex=0;
	 /**当前监控车辆位置*/
	 public Location curVLocation=null;
	 /**当前车辆地址*/
	 public String addrss=null;
	 /**今日里程*/
	 public String totalMileage="0";
	 /**查询告警列表*/
	 public List<Alarm> alarms=null;
	 /**告警序号*/
	 public int alarmsIndex=0;
	 /**我的消息*/
//	 public List<MyMessage> myMessages=null;
	 /**我的消息序号*/
//	 public int myMessageIndex=0;
	 /**我的4S店*/
//	 public List<My4SShop> my4SShops= new ArrayList<>();
	 /**点击跳转序号*/
//	 public int my4SShopsIndex=0;
	 /**我的备忘*/
//	 public List<MyRemark> myRemarks= new ArrayList<>();
	 /**我的备忘点击序号*/
//	 public int myRemarkIndex=0;
	 /**历史数据*/
	 public List<History> historys;
	 /**电子围栏的半径*/
	 public Geofence geofence;
	 /**油量查询结果列表*/
//	 public List<MileageOilReport> mileageOilReports = new ArrayList<>();
	 /**消息推送线程*/
//	 public Thread pushthread=null;
	 /**消息推送handler*/
//	 public Handler pushhandler=null;
	 /**标记活动
	  * 0,不推送消息；1,登陆；2，设置
	  * */
	 public int activity=0;
//	 public String username;

	private static MyApplication instance;
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
//		/* 全局异常处理 */
//		CrashHandler crashHandler = CrashHandler.getInstance();
//		crashHandler.init(getApplicationContext());
	}

	public void restartApp(){
		Intent intent = new Intent(instance, Login.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		instance.startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid());  //结束进程之前可以把你程序的注销或者退出代码放在这段代码之前
	}
}
