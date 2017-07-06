package com.cxgps.vehicle.base;


import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.cxgps.vehicle.AppConfig;
import com.cxgps.vehicle.AppContext;
import com.cxgps.vehicle.R;
import com.cxgps.vehicle.bean.SimpleBackPage;
import com.cxgps.vehicle.bean.UserInfo;
import com.cxgps.vehicle.utils.DateUtils;
import com.cxgps.vehicle.utils.DialogHelper;
import com.cxgps.vehicle.utils.DoubleClickExitHelper;
import com.cxgps.vehicle.utils.UIHelper;

import butterknife.Bind;


/**
 * Created by taosong on 16/6/16.
 */
public class BaseMonitorActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener{


    private DoubleClickExitHelper mDoubleClickExit;

    private ImageView mHeaderIcon;

    private TextView mHeaderName;

    private TextView mHeaderTime;


    @Bind(R.id.tab_road_title)
    TextView roadClick;

    @Bind(R.id.tab_map_title)
    TextView maptypeClick;

    @Bind(R.id.tab_map_location)
    TextView mylocationClick;


    @Bind(R.id.leftClickBtn)
    ImageView mLeftClickBtn;

    @Bind(R.id.rightClickBtn)
    ImageView mRightClickBtn;

    @Bind(R.id.map_add_level)
    protected TextView mAddLevel;

    @Bind(R.id.map_reduce_level)
    protected TextView mReducLevel;

    @Bind(R.id.content)
    protected FrameLayout mContent;

    protected int   locationType = AppConfig.USER_LOGIN_TAG;


    @Override
    protected int getLayoutId() {
        return R.layout.app_main;
    }

    @Override
    protected boolean hasActionBar() {
        return false;
    }

    @Override
    public void initView() {

        mDoubleClickExit = new DoubleClickExitHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer,  toolbar, R.string.drawer_open, R.string.drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View heanderView = navigationView.getHeaderView(0);
        mHeaderIcon = (ImageView) heanderView.findViewById(R.id.header_icon);
        mHeaderName = (TextView) heanderView.findViewById(R.id.header_name);
        mHeaderTime = (TextView) heanderView.findViewById(R.id.header_time);
        heanderView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIHelper.showSimpleBack(BaseMonitorActivity.this, SimpleBackPage.MY_INFORMATION);
            }
        });



    }






    @Override
    public void initData() {

        UserInfo userInfo =  AppContext.getInstance().getLoginUser();

        String userName = "未知";
        String loginTime = DateUtils.getFormateDate();
        Bitmap headerBitmap = null;
        int loginType = 0;

        if (userInfo !=null){

            userName = userInfo.getUserName();
            loginTime = userInfo.getuLogintime();
            loginType = userInfo.getuLoginType();


        }
        if (loginType == AppConfig.USER_LOGIN_TAG){

            headerBitmap =  BitmapFactory.decodeResource(getResources(), R.mipmap.monitor_user);

        }else {

            headerBitmap =  BitmapFactory.decodeResource(getResources(), R.mipmap.monitor_car);

        }
        mHeaderName.setText(userName);
        mHeaderTime.setText(loginTime);
        mHeaderIcon.setImageBitmap(headerBitmap);



    }


    public void menuRightTop(View view){

        int viewId = view.getId();
        switch (viewId){

            case  R.id.tab_road_title:
                break;
            case R.id.tab_map_title:
                break;
            case R.id.tab_map_location:
                break;
            case R.id.leftClickBtn:
                break;
            case R.id.rightClickBtn:
                break;
            case R.id.map_add_level:
                break;
            case R.id.map_reduce_level:
                break;

        }
    }



    public  void setTrafficeState(boolean flag){

        Drawable img_on, img_off;
        Resources res = getResources();
        img_off = res.getDrawable(R.mipmap.monitor_ico_lukuang);
        img_on  = res.getDrawable(R.mipmap.monitor_ico_lukuang_pre);
        //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示

        if (flag){
            img_on.setBounds(0, 0, img_on.getMinimumWidth(), img_on.getMinimumHeight());
            roadClick.setCompoundDrawables(null, img_on, null, null); //设置左图标
        }else {
            img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
            roadClick.setCompoundDrawables(null, img_off, null, null); //设置左图标
        }



    }

    public int setCarOrRenLocation(){

        Drawable img_on, img_off;
        Resources res = getResources();
        img_off = res.getDrawable(R.mipmap.monitor_myposition1);
        img_on  = res.getDrawable(R.mipmap.monitor_carposition);
        //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示

        if (locationType == AppConfig.USER_LOGIN_TAG){

            img_on.setBounds(0, 0, img_on.getMinimumWidth(), img_on.getMinimumHeight());
            mylocationClick.setCompoundDrawables(null, img_on, null, null); //设置左图标
            locationType =  AppConfig.CARNUMBER_LOGIN_TAG;
        }else if (locationType == AppConfig.CARNUMBER_LOGIN_TAG){

            img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
            mylocationClick.setCompoundDrawables(null, img_off, null, null); //设置左图标
            locationType =  AppConfig.USER_LOGIN_TAG;
        }


        return locationType;
    }



    public void setMapyViewType(boolean mapFlag){



        Drawable img_on, img_off;
        Resources res = getResources();
        img_off = res.getDrawable(R.mipmap.monitor_ico_earth);
        img_on  = res.getDrawable(R.mipmap.monitor_ico_earthselected);
        //调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示

        if (mapFlag){
            img_on.setBounds(0, 0, img_on.getMinimumWidth(), img_on.getMinimumHeight());
            maptypeClick.setCompoundDrawables(null, img_on, null, null); //设置左图标
        }else {
            img_off.setBounds(0, 0, img_off.getMinimumWidth(), img_off.getMinimumHeight());
            maptypeClick.setCompoundDrawables(null, img_off, null, null); //设置左图标
        }



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){

            case  R.id.main_action_carslist:
                UIHelper.showSimpleBackForResult(BaseMonitorActivity.this, 1000, SimpleBackPage.CARSLIST);



                break;

            case R.id.main_action_notice:

                UIHelper.showSimpleBack(BaseMonitorActivity.this, SimpleBackPage.NOTICESMSG);
                break;
        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.navigation_item_navi) {

            int   maptype  =Integer.parseInt(AppContext.getMapWithKey());

            if (maptype ==2){

                DialogHelper.getConfirmDialog(BaseMonitorActivity.this, getString(R.string.tips_no_navia_error), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();
                    }
                }).create().show();
            }else {

                UIHelper.showSimpleBack(BaseMonitorActivity.this, SimpleBackPage.NAVIGATION);
            }

        } else

        if (id == R.id.navigation_item_ubook) {


            UIHelper.showWebDetail(BaseMonitorActivity.this, AppConfig.GUIDE, AppConfig.GUIDE);

        } else if (id == R.id.navigation_item_wzcq) {


            UIHelper.showWebDetail(BaseMonitorActivity.this, AppConfig.WEIZHANG, AppConfig.WEIZHANG);


        } else if (id == R.id.navigation_item_adverise) {

            UIHelper.showSimpleBack(BaseMonitorActivity.this, SimpleBackPage.FEEDBACK);


        } else if (id == R.id.navigation_item_about) {


            UIHelper.showWebDetail(BaseMonitorActivity.this, AppConfig.ABOUT, AppConfig.ABOUT);


        }else if (id == R.id.navigation_item_settings){

            UIHelper.showSimpleBack(BaseMonitorActivity.this, SimpleBackPage.SETTING);


        }else if(id == R.id.navigation_item_loginout){
            DialogHelper.getConfirmDialog(BaseMonitorActivity.this, getString(R.string.tips_logout_msg), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserInfo userInfo = AppContext.getInstance().getLoginUser();

                    if (userInfo != null && Boolean.valueOf(userInfo.getuLoginFlag())) {


                        userInfo.setuLoginFlag("false");

                        AppContext.getInstance().saveUserInfo(userInfo.getuLoginType(), userInfo);
                        UIHelper.showLoginActivity(BaseMonitorActivity.this);
                        finish();
                    }

                }
            }).create().show();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK){

            // 是否退出应用
            if (AppContext.get(AppConfig.KEY_DOUBLE_CLICK_EXIT, true)) {
                return mDoubleClickExit.onKeyDown(keyCode, event);
            }
        }

        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

}
