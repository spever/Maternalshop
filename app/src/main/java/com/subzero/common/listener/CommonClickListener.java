package com.subzero.common.listener;
import com.subzero.common.listener.clickcore.CommonClickType;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
public class CommonClickListener implements OnClickListener
{
	private Context context;
	private CommonClickType clickType;
	private Class<?> cls;
	private Intent intent;
	public CommonClickListener(Context context,CommonClickType commonClickType) {
		this.context = context;
		this.clickType = commonClickType;
	}
	public CommonClickListener(Context context,CommonClickType commonClickType,Class<?> cls) {
		this.context = context;
		this.clickType = commonClickType;
		this.cls = cls;
	}
	public CommonClickListener(Context context,CommonClickType commonClickType,Intent intent) {
		this.context = context;
		this.clickType = commonClickType;
		this.intent = intent;
	}
	@Override
	public void onClick(View v)
	{
		//"返回"
		if(CommonClickType.back == clickType){
			((Activity)context).finish();
		}
		/*下一步，并结束掉自己*/
		else if(CommonClickType.cls2Killed == clickType){
			Intent intent = new Intent(context, cls);
			context.startActivity(intent);
			((Activity)context).finish();
		}
		/*下一步，不结束掉自己*/
		else if(CommonClickType.cls0Killed == clickType){
			Intent intent = new Intent(context, cls);
			context.startActivity(intent);
		}
		/*意图跳转，并结束掉自己*/
		else if(CommonClickType.intent2Killed == clickType){
			context.startActivity(intent);
			((Activity)context).finish();
		}
		/*意图跳转，不结束掉自己*/
		else if(CommonClickType.intent0Killed == clickType){
			context.startActivity(intent);
			((Activity)context).finish();
		}
	}
}
