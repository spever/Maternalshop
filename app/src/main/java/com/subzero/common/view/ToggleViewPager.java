package com.subzero.common.view;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**可以手动设置，ViewPager可侧滑|禁侧滑*/
public class ToggleViewPager extends ViewPager 
{
	private boolean isLocked = true;
    public ToggleViewPager(Context context) {
        super(context);
        isLocked = true;
    }
    public ToggleViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        isLocked = true;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) 
    {
    	if (!isLocked) {
	        try {
	            return super.onInterceptTouchEvent(ev);
	        } catch (IllegalArgumentException e) {
	            return false;
	        }
    	}
    	return false;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isLocked) {
            return super.onTouchEvent(event);
        }
        return false;
    }
	public void toggleLock() {
		isLocked = !isLocked;
	}
	public void setLocked(boolean isLocked) {
		this.isLocked = isLocked;
	}
	public boolean isLocked() {
		return isLocked;
	}
}
