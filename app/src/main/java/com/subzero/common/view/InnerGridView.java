package com.subzero.common.view;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ScrollView;
public class InnerGridView extends GridView 
{
	boolean expanded = true;
	public InnerGridView(Context context) {
		super(context);
		expanded = true;
	}
	public InnerGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		expanded = true;
	}
	public InnerGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		expanded = true;
	}
	public boolean isExpanded() {
		return expanded;
	}
	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
	{
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}
	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}
	/**让父窗体ScrollView滚到顶部*/
	public void scroll2Top(final ScrollView scrollView)
	{
		post(new Runnable() {
			@Override
			public void run(){
				scrollView.scrollTo(0,0);
			}
		});
	}
}