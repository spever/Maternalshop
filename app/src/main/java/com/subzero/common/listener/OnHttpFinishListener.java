package com.subzero.common.listener;
import com.lidroid.xutils.exception.HttpException;
public interface OnHttpFinishListener
{
	public void onFailure(HttpException error, String msg);
	public void onSuccess(String result);
}
