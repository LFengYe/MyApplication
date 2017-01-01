package com.cn.wetrack.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

import java.util.Timer;
import java.util.TimerTask;

public class MyHScrollview extends HorizontalScrollView {

	private int position=0,oldposition=0,returnposition=0;
	private float size=0;
	public Boolean up=true,down=false,move=false;//公有状态信息
	private Timer mytimer=new Timer();
	private Boolean isfirst=true;
	public MyHScrollview(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public MyHScrollview(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	public MyHScrollview(Context context) {
		super(context);
	}
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		if(event.getActionMasked()==MotionEvent.ACTION_UP){
        	up=true;
        	down=false;
        	move=false;
        }
        if(event.getAction()==MotionEvent.ACTION_DOWN){
        	down=true;
        	up=false;
        	move=false;
        	up=false;
        	if(isfirst){
    		mytimer.schedule(new TimerTask() {
				@Override
				public void run() {
					if(oldposition==MyHScrollview.this.getScrollY()&&up){
						if(MyHScrollview.this.getScrollX()>=80){
			    			MyHScrollview.this.smoothScrollTo(160, 0);
			        	}
			        	else{
			        		MyHScrollview.this.smoothScrollTo(0, 0);
			        	}
					}
					oldposition=MyHScrollview.this.getScrollY();
				}
			}, 100,100);
    		isfirst=false;
       	}
        }
        if(event.getAction()==MotionEvent.ACTION_MOVE){
        	down=true;
        	move=true;
        	up=false;
        	//Log.d("dir", "direction:"+direction);
        }
		return super.dispatchTouchEvent(event);
	}
//	@Override  
//    public boolean onInterceptTouchEvent(MotionEvent event)   //这个方法如果返回 true 的话 两个手指移动，启动一个按下的手指的移动不能被传播出去。  
//    {  
//        super.onInterceptTouchEvent(event);  
//        return false;  
//    }  
//      
    @Override
    public void computeScroll() {

    	super.computeScroll();
    }
//      @Override
//    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
//    	super.onScrollChanged(l, t, oldl, oldt);
//    	if(oldl==l&&up){
//    		if(this.getScrollX()>=80){
//    			MyHScrollview.this.smoothScrollTo(160, 0);
//        	}
//        	else{
//        		MyHScrollview.this.smoothScrollTo(0, 0);
//        		MyHScrollview.this.gets
//        	}
//    	}
//    }
//      public int Getoldposition(){
//    	  return oldposition;
//      }
      public int Getposition(){  
    	  return MyHScrollview.this.getScrollX();
    	  
      }
//      public float Getsize(){
//    	  return (float) (size*0.1);
//      }
////      public int Getstate(){
////    	 return direction; 
////      }
      public void scrolloto(int position){
    	  this.scrollTo(position, 0);
      }
      public void Smoothscrollto(int position){
    	  this.smoothScrollTo(position, 0);
      }

}
