package com.subzero.maternalshop.activity;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.view.CircleIndicator;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.adapter.pageradapter.WelcomeAdapter;
import com.subzero.maternalshop.config.User;
import com.subzero.userman.LoginActivity;
/**
 * 欢迎页面
 * 启动者：
 * 	程序入口：MainActivity
 * 启动项：
 * 	默认页面：IndexActivity
 * */
public class WelcomeActivity extends BaseActivity
{
	protected ViewPager viewPager;
	protected CircleIndicator circleIndicator;
	private Context context;
	private int resIds[];
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_welcome);
		context = this;
		initView();
	}
	public void initView()
	{
		viewPager = (ViewPager) findViewById(R.id.vp);
		circleIndicator = (CircleIndicator) findViewById(R.id.ci);
		boolean online = User.getIsOnline(context);
		if(online){/*有默认账户*/
			findViewById(R.id.bt_start).setOnClickListener(new CommonClickListener(context, CommonClickType.cls2Killed, IndexActivity.class));
		}else{/*没有默认账户*/
			findViewById(R.id.bt_start).setOnClickListener(new CommonClickListener(context, CommonClickType.cls2Killed, LoginActivity.class));
		}
		List<ImageView> list = new ArrayList<ImageView>();
		resIds = new int[]{R.drawable.logo_loading,R.drawable.logo_splash_2,R.drawable.logo_splash_3};
		for (int i = 0; i < resIds.length; i++)
		{
			Options opts = new Options();
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = 1;   //width，hight设为原来的十分一
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resIds[i], opts);
			ImageView imageView = new ImageView(context);
			imageView.setImageBitmap(bitmap);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			list.add(imageView);
		}
		WelcomeAdapter adapter = new WelcomeAdapter(list);
		viewPager.setAdapter(adapter);
		circleIndicator.setViewPager(viewPager);
		circleIndicator.setOnPageChangeListener(new MyOnPageChangeListener());
	}
	protected class MyOnPageChangeListener implements ViewPager.OnPageChangeListener
	{
		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){	
			if(position == resIds.length-1){
				findViewById(R.id.bt_start).setVisibility(View.VISIBLE);
				if(positionOffset>0.25){
					findViewById(R.id.bt_start).setVisibility(View.GONE);	
				}
			}else {
				findViewById(R.id.bt_start).setVisibility(View.GONE);
			}
		}
		@Override
		public void onPageSelected(int position)
		{
			
		}
		@Override
		public void onPageScrollStateChanged(int state){    }

	}
}
