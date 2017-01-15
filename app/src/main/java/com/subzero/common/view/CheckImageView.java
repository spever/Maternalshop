package com.subzero.common.view;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import com.subzero.common.view.checkview.CheckClickType;
import com.subzero.maternalshop.R;
/**
 * 类似于CheckBox
 * */
@SuppressLint("ClickableViewAccessibility")
public class CheckImageView extends ImageView
{
	private boolean isChecked = false;
	private int imageResNormal = R.drawable.subzero_check_image_view_normal;
	private int imageResSelected = R.drawable.subzero_check_image_view_selected;
	/**保持点击后的 状态*/
	private boolean isKeepClickEffect = true;
	private OnCheckedChangeListener onCheckedChangeListener;
	private OnCheckedClickListener onCheckedClickListener;
	private Context context;
	private Dialog dialog;
	private CheckClickType checkClickType;
	public CheckImageView(Context context) {
		super(context);
		setImageResource(imageResNormal);
	}
	public CheckImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setImageResource(imageResNormal);
		initAttrs(context,attrs);
		initCheckTextView();
		setCheckTextViewAttrs(false);
	}
	public CheckImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setImageResource(imageResNormal);
		initAttrs(context,attrs);
		initCheckTextView();
		setCheckTextViewAttrs(false);
	}
	private void initAttrs(Context context, AttributeSet attrs)
	{
		TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CheckImageView);
		//
		isChecked = array.getBoolean(R.styleable.CheckImageView_civ_isChecked, isChecked);
		isKeepClickEffect  = array.getBoolean(R.styleable.CheckImageView_civ_isKeepClickEffect, isKeepClickEffect);
		imageResNormal = array.getResourceId(R.styleable.CheckImageView_civ_imageResNormal, imageResNormal);
		imageResSelected = array.getResourceId(R.styleable.CheckImageView_civ_imageResSelected, imageResSelected);
		//
		array.recycle();
	}
	private void setCheckTextViewAttrs(boolean isChecked)
	{
		if(isChecked){
			setImageResource(imageResSelected);
		}else{
			setImageResource(imageResNormal);
		}
	}
	private void initCheckTextView(){
		setOnClickListener(new MyOnClickListener());
	}
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
			}
			if((dialog != null) && (checkClickType!=null) && (CheckClickType.dismiss == checkClickType) ){
				dialog.dismiss();
			}
		}
	}
	/**状态改变监听*/
	public interface OnCheckedChangeListener{
		public void onCheckChange(View v, boolean isChecked);
	}
	/**状态改变监听*/
	public interface OnCheckedClickListener{
		public void onCheckClick(View v);
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
	public void setOnCheckedClickFinish(Context context, CheckClickType checkClickType){
		this.context = context;
		this.checkClickType = checkClickType;
	}
	public void setOnCheckedClickFinish(Dialog dialog, CheckClickType checkClickType){
		this.dialog = dialog;
		this.checkClickType = checkClickType;
	}
}
