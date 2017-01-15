package com.subzero.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class TouchEventScrollView extends ScrollView
{
	private float xDistance, yDistance, xLast, yLast;  
	public TouchEventScrollView(Context context) {  
		super(context);  
	}  
	public TouchEventScrollView(Context context, AttributeSet attrs) {  
		super(context, attrs);  
	}  
	public TouchEventScrollView(Context context, AttributeSet attrs, int defStyle) {  
		super(context, attrs, defStyle);  
	}  
	@Override  
	public boolean onInterceptTouchEvent(MotionEvent ev) {  
		switch (ev.getAction()) {  
		case MotionEvent.ACTION_DOWN:  
			xDistance = yDistance = 0f;  
			xLast = ev.getX();  
			yLast = ev.getY();  
			break;  
		case MotionEvent.ACTION_MOVE:  
			final float curX = ev.getX();  
			final float curY = ev.getY();  

			xDistance += Math.abs(curX - xLast);  
			yDistance += Math.abs(curY - yLast);  
			xLast = curX;  
			yLast = curY;  

			/** 
			 * X轴滑动距离大于Y轴滑动距离，也就是用户横向滑动时，返回false，ScrollView不处理这次事件， 
			 * 让子控件中的TouchEvent去处理，所以横向滑动的事件交由ViewPager处理， 
			 * ScrollView只处理纵向滑动事件 
			 */  
			if (xDistance > yDistance) {  
				return false;  
			}  
		}  
		return super.onInterceptTouchEvent(ev);  
	}  
}  
