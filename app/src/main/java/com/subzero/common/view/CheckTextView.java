package com.subzero.common.view;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.subzero.common.view.checkview.CheckClickType;
import com.subzero.maternalshop.R;
/**
 * 类似于CheckBox
 * */
public class CheckTextView extends TextView
{
	private boolean isChecked = false;
	/**保持点击后的 状态*/
	private boolean isKeepClickEffect = false;
	private int backgroundResNormal = -1;
	private int backgroundResSelected = -1;
	private int textColorNormal = Color.parseColor("#000000");
	private int textColorSelected = Color.parseColor("#FFFFFF");
	private float textSizeNormal = 12;
	private float textSizeSelected = 14;
	private Context context;
	private Intent intent;
	private CheckClickType checkClickType;
	private Class<?> className;
	private OnCheckedChangeListener onCheckedChangeListener;
	private OnCheckedClickListener onCheckedClickListener;
	public CheckTextView(Context context) {
		super(context);
	}
	public CheckTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initAttrs(context,attrs);
		initCheckTextView();
		setCheckTextViewAttrs(isChecked);
	}
	public CheckTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initAttrs(context,attrs);
		initCheckTextView();
		setCheckTextViewAttrs(isChecked);
	}
	private void initAttrs(Context context, AttributeSet attrs)
	{
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		textSizeNormal = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSizeNormal, metrics);
		textSizeSelected = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSizeSelected, metrics);
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CheckTextView);
		//
		isChecked = array.getBoolean(R.styleable.CheckTextView_ctv_isChecked, isChecked);
		isKeepClickEffect  = array.getBoolean(R.styleable.CheckTextView_ctv_isKeepClickEffect, isKeepClickEffect);
		backgroundResNormal = array.getResourceId(R.styleable.CheckTextView_ctv_backgroundResNormal, backgroundResNormal);
		backgroundResSelected = array.getResourceId(R.styleable.CheckTextView_ctv_backgroundResSelected, backgroundResSelected);
		textColorNormal = array.getColor(R.styleable.CheckTextView_ctv_textColorNormal, textColorNormal);
		textColorSelected = array.getColor(R.styleable.CheckTextView_ctv_textColorSelected, textColorSelected);
		textSizeNormal = array.getDimension(R.styleable.CheckTextView_ctv_textSizeNormal, textSizeNormal);
		textSizeSelected = array.getDimension(R.styleable.CheckTextView_ctv_textSizeSelected, textSizeSelected);
		//
		array.recycle();
	}
	private void setCheckTextViewAttrs(boolean isChecked)
	{
		if(isChecked){
			if(backgroundResSelected!=-1){
				setBackgroundResource(backgroundResSelected);
			}
			setTextColor(textColorSelected);
			setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeSelected);
		}else{
			if(backgroundResNormal!=-1){
				setBackgroundResource(backgroundResNormal);
			}
			setTextColor(textColorNormal);
			setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizeNormal);
		}
	}
	private void initCheckTextView(){
		setOnClickListener(new MyOnClickListener());
	}
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		super.onTouchEvent(event);
		if(event.getAction() == MotionEvent.ACTION_DOWN){
			if(isChecked){
				setCheckTextViewAttrs(false);
			}else{
				setCheckTextViewAttrs(true);
			}
		}
		else if(event.getAction() == MotionEvent.ACTION_UP){
			if(isChecked){
				setCheckTextViewAttrs(true);
			}else{
				setCheckTextViewAttrs(false);
			}
		}
		return true;
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(onCheckedClickListener!=null){
				onCheckedClickListener.onCheckClick(v);
			}
			if(isKeepClickEffect)
			{
				isChecked = !isChecked;
				setCheckTextViewAttrs(isChecked);
				if(onCheckedChangeListener!=null){
					onCheckedChangeListener.onCheckChange(v, isChecked);
				}
			}
			if((context != null) && (checkClickType!=null) && (CheckClickType.back == checkClickType) ){
				((Activity)context).finish();
			}else if((context != null) && (checkClickType!=null) && (CheckClickType.startNoKillSelf == checkClickType) && (intent!=null)){
				((Activity)context).startActivity(intent);
			}else if((context != null) && (checkClickType!=null) && (CheckClickType.startNoKillSelf == checkClickType) && (className!=null)){
				((Activity)context).startActivity(new Intent(context, className));
			}
		}
	}
	public boolean getChecked(){
		return isChecked;
	}
	/**设置 Checked*/
	public void setChecked(boolean isChecked){
		this.isChecked = isChecked;
		setCheckTextViewAttrs(isChecked);
		if(onCheckedChangeListener!=null){
			onCheckedChangeListener.onCheckChange(this, isChecked);
		}
	}
	/**切换 Checked*/
	public void toggleChecked(){
		isChecked = !isChecked;
		setCheckTextViewAttrs(isChecked);
		if(onCheckedChangeListener!=null){
			onCheckedChangeListener.onCheckChange(this, isChecked);
		}
	}
	/**设置状态改变监听*/
	public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener){
		this.onCheckedChangeListener = onCheckedChangeListener;
	}
	/**设置点击监听*/
	public void setOnCheckedClickListener(OnCheckedClickListener onCheckedClickListener){
		this.onCheckedClickListener = onCheckedClickListener;
	}
	/**状态改变监听*/
	public interface OnCheckedChangeListener{
		public void onCheckChange(View v, boolean isChecked);
	}
	public void setOnCheckedClickFinish(Context context, CheckClickType checkClickType){
		this.context = context;
		this.checkClickType = checkClickType;
	}
	public void setOnCheckedClickStart(Context context, CheckClickType checkClickType, Intent intent){
		this.context = context;
		this.checkClickType = checkClickType;
		this.intent = intent;
	}
	public void setOnCheckedClickStart(Context context, CheckClickType checkClickType, Class<?> className){
		this.context = context;
		this.checkClickType = checkClickType;
		this.className = className;
	}
	/**状态改变监听*/
	public interface OnCheckedClickListener{
		public void onCheckClick(View v);
	}
}
