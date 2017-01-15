package com.subzero.common.view;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.subzero.maternalshop.R;
public class CountLayout extends LinearLayout
{
	private int layoutBackgroundRes = R.drawable.subzero_countlayout_shape_gry;
	private int colorLine1 = Color.parseColor("#DADADA");
	private int colorLine2 = Color.parseColor("#DADADA");
	private int imgLeftResId = R.drawable.subzero_countlayout_selector_img_left;
	private int imgRightResId = R.drawable.subzero_countlayout_selector_img_right;
	private int editTextBackground = R.drawable.subzero_countlayout_edit_gry;
	private int editTextWidth = 36;
	private int editTextColor = Color.parseColor("#333333");
	private int editTextHintColor = Color.parseColor("#666666");
	/**加减号 小图标的 内边距*/
	private int imagePaddingLeft = 0;
	/**加减号 小图标的 内边距*/
	private int imagePaddingTop = 0;
	/**加减号 小图标的 内边距*/
	private int imagePaddingRight = 0;
	/**加减号 小图标的 内边距*/
	private int imagePaddingBottom = 0;
	private final int imagePaddingLeftDefault = 0;
	private final int imagePaddingRightDefault = 0;
	private final int imagePaddingTopDefault = 0;
	private final int imagePaddingBottomDefault = 0;
	private ImageView ivLeft;
	private EditText editText;
	private ImageView ivRight;
	private float editTextSize = 12;
	private String editTextHint = "0";
	private String editTextText = "";
	private int count;
	private OnCountClickListener onCountClickListener;
	public CountLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		initCountLayout(context, attrs);
	}
	private void initCountLayout(Context context, AttributeSet attrs)
	{
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		editTextWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, editTextWidth, metrics);
		editTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, editTextSize, metrics);
		//
		imagePaddingLeft = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, editTextWidth, metrics);
		imagePaddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, editTextWidth, metrics);
		imagePaddingRight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, editTextWidth, metrics);
		imagePaddingBottom = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, editTextWidth, metrics);
		
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CountLayout);
		layoutBackgroundRes = array.getResourceId(R.styleable.CountLayout_bg_layout, layoutBackgroundRes);
		colorLine1 = array.getColor(R.styleable.CountLayout_color_line1, colorLine1);
		colorLine2 = array.getColor(R.styleable.CountLayout_color_line2, colorLine2);
		editTextWidth = (int) array.getDimension(R.styleable.CountLayout_edittext_width, editTextWidth);
		editTextSize = array.getDimension(R.styleable.CountLayout_edittext_size, editTextSize);
		editTextColor = array.getColor(R.styleable.CountLayout_edittext_textcolor, editTextColor);
		editTextHintColor = array.getColor(R.styleable.CountLayout_edittext_hintcolor, editTextHintColor);
		editTextHint= array.getString(R.styleable.CountLayout_edittext_hint);
		imagePaddingBottom = (int) array.getDimension(R.styleable.CountLayout_image_paddingBottom, imagePaddingBottomDefault);
		imagePaddingLeft = (int) array.getDimension(R.styleable.CountLayout_image_paddingLeft, imagePaddingLeftDefault);
		imagePaddingRight = (int) array.getDimension(R.styleable.CountLayout_image_paddingRight, imagePaddingRightDefault);
		imagePaddingTop = (int) array.getDimension(R.styleable.CountLayout_image_paddingTop, imagePaddingTopDefault);
		if(editTextHint == null){
			editTextHint = "0";
		}
		//
		array.recycle();
		ivLeft = (ivLeft==null) ? new ImageView(context):ivLeft;
		ivRight = (ivRight==null) ? new ImageView(context):ivRight;
		editText = (editText==null)? new EditText(context):editText;
		View line1 = new View(context);
		View line2 = new View(context);
		ivLeft.setImageResource(imgLeftResId);
		ivLeft.setPadding(imagePaddingLeft, imagePaddingTop, imagePaddingRight, imagePaddingBottom);
		ivLeft.setClickable(true);
		ivLeft.setOnClickListener(new MyOnClickListener(ClickType.left));
		ivRight.setPadding(imagePaddingLeft, imagePaddingTop, imagePaddingRight, imagePaddingBottom);
		ivRight.setImageResource(imgRightResId);
		ivRight.setClickable(true);
		ivRight.setOnClickListener(new MyOnClickListener(ClickType.right));
		/*设置 编辑框的 属性*/
		initEditTextAttrs();
		line1.setBackgroundColor(colorLine1);
		line2.setBackgroundColor(colorLine1);
		setOrientation(LinearLayout.HORIZONTAL);
		setGravity(Gravity.CENTER_VERTICAL);
		addView(ivLeft);
		addView(line1,1,getViewHeight(ivLeft)+imagePaddingTop+imagePaddingBottom);
		addView(editText);
		addView(line2,1,getViewHeight(ivLeft)+imagePaddingTop+imagePaddingBottom);
		addView(ivRight);
		try{
			setBackgroundResource(layoutBackgroundRes);
		}catch(Exception e){}
	}
	/**设置 编辑框的 属性*/
	private void initEditTextAttrs()
	{
		editText.setWidth(editTextWidth);
		//editText.setTextSize(editTextSize);
		editText.setText(editTextText);
		editText.setFocusable(false);
		editText.setFocusableInTouchMode(false);
		editText.setSingleLine(true);
		editText.setPadding(3, 0, 3, 0);
		editText.setInputType(InputType.TYPE_CLASS_NUMBER);
		editText.setGravity(Gravity.CENTER);
		editText.setTextColor(editTextColor);
		editText.setHintTextColor(editTextHintColor);
		editText.setHint(editTextHint);
		try{
			editText.setBackgroundResource(editTextBackground);
		}catch(Exception e){}
	}
	
	public int getCount(){
		return count;
	}
	private final class MyOnClickListener implements OnClickListener
	{
		private ClickType clickType;
		public MyOnClickListener(ClickType clickType) {
			this.clickType = clickType;
		}
		@Override
		public void onClick(View v)
		{
			String text = editText.getText().toString();
			if(TextUtils.isEmpty(text)){
				text = "0";
				editText.setText(text);
			}
			count = Integer.parseInt(text);
			if((ClickType.left == clickType) && (count>1)){
				count --;
				if(onCountClickListener!=null){
					onCountClickListener.onCountClick(count, false);
				}
			}else if(ClickType.right == clickType){
				count ++;
				if(onCountClickListener!=null){
					onCountClickListener.onCountClick(count, true);
				}
			} 
			if(count>0){
				editText.setText(count+"");
			}else{
				editText.setText("");
			}
		}
	}
	public void setOnCountClickListener(OnCountClickListener onCountClickListener){
		this.onCountClickListener = onCountClickListener;
	}
	
	public interface OnCountClickListener{
		/**@count 数值
		 * @isAdd 做加法*/
		public void onCountClick(int count, boolean isAdd);
	}
	private enum ClickType{
		left,
		right
	}
	/**
	 * 获得这个View的宽度
	 * 测量这个view，最后通过getMeasuredWidth()获取宽度.
	 * @param view 要测量的view
	 * @return 测量过的view的宽度
	 */
	@SuppressWarnings("unused")
	private int getViewWidth(View view) {
		measureView(view);
		return view.getMeasuredWidth();
	}
	/**
	 * 获得这个View的高度
	 * 测量这个view，最后通过getMeasuredHeight()获取高度.
	 * @param view 要测量的view
	 * @return 测量过的view的高度
	 */
	private int getViewHeight(View view) {
		measureView(view);
		return view.getMeasuredHeight();
	}
	/**
	 * 测量这个view
	 * 最后通过getMeasuredWidth()获取宽度和高度.
	 * @param view 要测量的view
	 * @return 测量过的view
	 */
	private void measureView(View view) {
		ViewGroup.LayoutParams p = view.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		view.measure(childWidthSpec, childHeightSpec);
	}
	public EditText getEditText(){
		return editText;
	}
}
