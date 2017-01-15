package com.subzero.common.view;
import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ScrollView;
public class InnerListView extends ListView 
{
    boolean expanded = true;
    public InnerListView(Context context) {
        super(context);
        expanded = true;
    }

    public InnerListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        expanded = true;
    }

    public InnerListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        expanded = true;
    }
    public boolean isExpanded() {
        return expanded;
    }
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) 
    {
        if (isExpanded()) {
            int expandSpec = MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST);
            super.onMeasure(widthMeasureSpec, expandSpec);
            ViewGroup.LayoutParams params = getLayoutParams();
            params.height = getMeasuredHeight();
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
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