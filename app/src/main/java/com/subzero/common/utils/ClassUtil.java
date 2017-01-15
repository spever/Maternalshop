package com.subzero.common.utils;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;
public class ClassUtil 
{
	private static final String TAG = "ClassUtil";
	/**获取 JavaBean的成员变量的 个数  成员修饰符 必须是 public
	 * @param bean 例如 MessageType.class
	 * @return  成员变量的 个数*/
	public static int getFieldsCount(Class<?> bean)
	{
		Field[] fields = bean.getFields();
		if(fields!=null){
			return fields.length;
		}
		return 0;
	}
	/**获取 JavaBean的成员变量的 个数
	 * @param bean 例如 MessageType.class
	 * @return  成员变量的 个数*/
	public static List<String> getFieldsList(Class<?> bean)
	{
		List<String> list = new ArrayList<String>();
		Field[] fields = bean.getFields();
		if(fields!=null){
			for (int i = 0; i < fields.length; i++)
			{
				list.add(fields[i].getName());
			}
		}
		return list;
	}
	public static Class<?> forName(String className){
		if(TextUtils.isEmpty(className)){
			return null;
		}
		try
		{
			return Class.forName(className);
		} catch (ClassNotFoundException e){
			Log.e(TAG, "有异常："+e);
		}
		return null;
	}
}
