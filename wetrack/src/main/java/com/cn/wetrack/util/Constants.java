package com.cn.wetrack.util;

import android.widget.LinearLayout.LayoutParams;

/**
 * Created by fuyzh on 16/7/22.
 */
public class Constants {
    /**表格字体初始大小 */
    public static final int TEXTSIZE_INIT = 22;
    /**表格字体大小最大值 */
    public static final int TEXTSIZE_MAX = 36;
    /**表格字体大小最小值 */
    public static final int TEXTSIZE_MIN = 16;
    /**
     * 登录与退出按钮点击响应
     */
    public static final int BTN_LAND_CLICKED = 0x33;
    public static final int BTN_EXIT_CLICKED = 0x34;

    /** 填满父控件布局参数 */
    public static LayoutParams FILL_FILL_LAYOUTPARAMS = new LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    /** 水平方向填满父控件布局*/
    public static LayoutParams HORIZONTAL_FILL_LAYOUTPATAMS = new LayoutParams(
            LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
    /** 垂直方向填满父控件布局*/
    public static LayoutParams VERTICAL_FILL_LAYOUTPARAMS = new LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
    /** 自适应内容布局*/
    public static LayoutParams WRAP_WRAP_LAYOUTPARAMS = new LayoutParams(
            LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
}
