package com.subzero.maternalshop.activity;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.subzero.common.helpers.AppRunHelper;
import com.subzero.common.listener.OnFragmentSelectedListener;
import com.subzero.common.utils.ObjUtil;
import com.subzero.common.utils.SDCardUtil;
import com.subzero.common.utils.doubleclick.ClickUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.Cache;
import com.subzero.maternalshop.fragment.CartFragment;
import com.subzero.maternalshop.fragment.HomepageFragment;
import com.subzero.maternalshop.fragment.MineFragment;
import com.subzero.maternalshop.fragment.SortFragment;
import com.subzero.userman.LoginActivity;
/**
 * 登录页面
 * 启动项：
 * 	欢迎页面：WelcomeActivity
 * 	登录页面：LoginActivity
 * 	主页：HomepageFragment
 * 	分类：SortFragment
 * 	购物车：CartFragment
 * 	我的：MineFragment
 * */
public class IndexActivity extends BaseFragmentActivity
{
	private RadioGroup radioGroup;
	private List<Integer> listIdRadioButton;
	private List<String> listFmName;
	private List<OnFragmentSelectedListener> listOnFragmentSelectedListener;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.actvitiy_index);
		initView();
		new AppRunHelper(context,"13146008029","123456").startCheckRunAuthority();
		if(savedInstanceState == null){
			initFragment();
		}
	}
	@Override
	protected void onResume()
	{
		super.onResume();
		Cache.saveActivityLifeCycle("onResume", false);
		initView();
	}
	@Override
	protected void onPause()
	{
		super.onPause();
		Cache.saveActivityLifeCycle("\nonPause", true);
	}
	@Override
	protected void onStop()
	{
		super.onStop();
		Cache.saveActivityLifeCycle("\nonStop", true);
	}
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		Cache.saveActivityLifeCycle("\nonDestroy", true);
	}
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		Cache.saveActivityLifeCycle("/nonSaveInstanceState", true);
	}
	@Override
	public void initView()
	{
		listIdRadioButton = (listIdRadioButton == null) ? new ArrayList<Integer>() : listIdRadioButton;
		listFmName = (listFmName==null) ? new ArrayList<String>():listFmName;
		listOnFragmentSelectedListener = (listOnFragmentSelectedListener==null)?new ArrayList<OnFragmentSelectedListener>():listOnFragmentSelectedListener;
		radioGroup = (radioGroup==null)?(RadioGroup) findViewById(R.id.rg_activtiy_index_foot_navi):radioGroup;
		if(ObjUtil.isListEmpty(listIdRadioButton)){
			listIdRadioButton.add(R.id.rb_activity_index_foot_navi_1);
			listIdRadioButton.add(R.id.rb_activity_index_foot_navi_2);
			listIdRadioButton.add(R.id.rb_activity_index_foot_navi_3);
			listIdRadioButton.add(R.id.rb_activity_index_foot_navi_4);
		}
		radioGroup.setOnCheckedChangeListener(new MyOnCheckedChangeListener());
	}
	private final class MyOnCheckedChangeListener implements OnCheckedChangeListener
	{
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId)
		{
			if(checkedId>=R.id.rb_activity_index_foot_navi_1){
				checkedId = checkedId - R.id.rb_activity_index_foot_navi_1;
			}
			switchFootNavi(checkedId);
		}
	}
	public void switchFootNavi(int index)
	{
		for (int i = 0; i < 4; i++)
		{
			RadioButton radioButton = (RadioButton) findViewById(listIdRadioButton.get(i));
			if(i == index){
				radioButton.setPressed(true);
				radioButton.setChecked(true);
			}else{
				radioButton.setPressed(false);
				radioButton.setChecked(false);
			}
			/*配置接口回调*/
			if((!ObjUtil.isListEmpty(listOnFragmentSelectedListener)) && (listOnFragmentSelectedListener.get(i)!=null)){
				listOnFragmentSelectedListener.get(i).onFragmentSelected(index);
			}
		}
		switchFragment(index);
	}
	private void switchFragment(int k)
	{
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		String logMain = "切换 默认首页  manager = "+(manager==null)+" transaction = "+(transaction==null)+"\nlistFmName = "+listFmName+"\n";
		String logSub = "";
		for (int i = 0; i < listFmName.size(); i++) 
		{
			if(i!=k){//隐藏的
				logSub += "\n隐藏 "+listFmName.get(i);
				transaction.hide(manager.findFragmentByTag(listFmName.get(i)));
			}else if(i==k){//显示的
				logSub += "\n显示 "+listFmName.get(i);
				transaction.show(manager.findFragmentByTag(listFmName.get(k)));
			}
		}
		if(ObjUtil.isListEmpty(listFmName)){
			Intent intent = new Intent(context, LoginActivity.class);
			intent.putExtra(App.keyModuleName, IndexActivity.class.getSimpleName());
			startActivity(intent);
			finish();
			logMain+="重启App\n";
			return ;
		}
		String log = logMain+logSub;
		SDCardUtil.saveFile(log.getBytes(), Cache.cacheDir+"log.java",false);
		transaction.commit();
	}
	private void initFragment()
	{
		FragmentManager manager = getSupportFragmentManager();
		FragmentTransaction transaction = manager.beginTransaction();
		/*首页*/
		HomepageFragment homepageFragment = new HomepageFragment();
		listFmName.add("homepageFragment");
		transaction.add(R.id.ll_body, homepageFragment, "homepageFragment").show(homepageFragment);
		/*分类*/
		SortFragment sortFragment = new SortFragment();
		listFmName.add("sortFragment");
		transaction.add(R.id.ll_body, sortFragment, "sortFragment").hide(sortFragment);
		/*购物车*/
		CartFragment cartFragment = new CartFragment();
		listFmName.add("cartFragment");
		transaction.add(R.id.ll_body, cartFragment, "cartFragment").hide(cartFragment);
		/*我的*/
		MineFragment mineFragment = new MineFragment();
		listFmName.add("mineFragment");
		transaction.add(R.id.ll_body, mineFragment, "mineFragment").hide(mineFragment);
		
		transaction.commit();
	}
	public void addOnFragmentSelectedListener(OnFragmentSelectedListener onFragmentSelectedListener){
		listOnFragmentSelectedListener = (listOnFragmentSelectedListener==null)?new ArrayList<OnFragmentSelectedListener>():listOnFragmentSelectedListener;
		listOnFragmentSelectedListener.add(onFragmentSelectedListener);
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		 /*在这里，我们通过碎片管理器中的Tag，就是每个碎片的名称，来获取对应的fragment*/
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(listFmName.get(2));
        /*然后在碎片中调用重写的onActivityResult方法*/
        fragment.onActivityResult(requestCode, resultCode, data);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) 
	{
		if (keyCode == KeyEvent.KEYCODE_BACK) 
		{
			ClickUtil.getInstance(this).doDoubleClick(1500, "再按一次返回键退出");
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
