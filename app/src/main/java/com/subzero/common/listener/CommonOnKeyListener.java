package com.subzero.common.listener;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
public class CommonOnKeyListener implements OnKeyListener
{
	private Activity activity;
	private OnKeyType onKeyType;
	public CommonOnKeyListener(Activity activity, OnKeyType onKeyType) {
		this.onKeyType = onKeyType;
		this.activity = activity;
	}
	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK){
			dialog.dismiss();
			if(OnKeyType.dismissKillActivity == onKeyType){
				activity.finish();
			}
		}
		return true;
	}
	public enum OnKeyType{
		dismissKillActivity,
		dismissNotKillActivity
	}
}