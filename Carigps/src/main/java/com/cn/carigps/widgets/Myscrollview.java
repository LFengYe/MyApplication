package com.cn.carigps.widgets;


import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import java.util.Timer;

public class Myscrollview extends ScrollView {
//	private int position=0,oldposition=0,returnposition=0;
//	private int direction=0;
	private float size=0;
	public Boolean up=true,down=false,move=false;//公有状态信息
	private Timer mytimer=new Timer();
	private Boolean isfirst=true;
	public Myscrollview(Context context)  
    {  
        super(context);  
    }  
  
    public Myscrollview(Context context, AttributeSet attrs)  
    {  
        super(context, attrs);  
          
    }  
      
    public Myscrollview(Context context, AttributeSet attrs, int defStyle)  
    {  
        super(context, attrs, defStyle);  
    }  
      
    @Override  
    public boolean onInterceptTouchEvent(MotionEvent event)   //这个方法如果返回 true 的话 两个手指移动，启动一个按下的手指的移动不能被传播出去。  
    {  
        super.onInterceptTouchEvent(event);  
        return false;  
    }  
      
    @Override  
    public boolean onTouchEvent(MotionEvent event)//这个方法如果 true 则整个Activity 的 onTouchEvent() 不会被系统回调  
    {  
        super.onTouchEvent(event); 
        if(event.getActionMasked()==MotionEvent.ACTION_UP){
        	up=true;
        	down=false;
        	move=false;
        }
        if(event.getAction()==MotionEvent.ACTION_DOWN){
        	down=true;
        	up=false;
        	move=false;
//        	if(isfirst){
//        		mytimer.schedule(new TimerTask() {
//					@Override
//					public void run() {
//						if(oldposition==Myscrollview.this.getScrollY()&&up){
//							direction=0;
//						}
//						oldposition=Myscrollview.this.getScrollY();
//					}
//				}, 100,100);
//        		isfirst=false;
 //       	}
        	up=false;
        }
        if(event.getAction()==MotionEvent.ACTION_MOVE){
        	down=true;
        	move=true;
        	up=false;
        	//Log.d("dir", "direction:"+direction);
        }
        return true;          
    } 
    @Override
    public void computeScroll() {

    	super.computeScroll();
    }
      @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
    	super.onScrollChanged(l, t, oldl, oldt);
        	//position=this.getScrollY();
        	//size=t%70;
        	/*if(oldt<t){
            	direction=2;
            	position=t/70;
            	}
            	 if(oldt>t){
            		direction=-2;
            		position=t/70;
            	}
        	returnposition=position;*/
    }
//      public int Getoldposition(){
//    	  return oldposition;
//      }
      public int Getposition(){  
    	  return Myscrollview.this.getScrollY();
    	  
      }
      public float Getsize(){
    	  return (float) (size*0.1);
      }
//      public int Getstate(){
//    	 return direction; 
//      }
      public void scrolloto(int position){
    	  this.scrollTo(0, position);
      }
      public void Smoothscrollto(int position){
    	  this.smoothScrollTo(0, position);
      }

}
