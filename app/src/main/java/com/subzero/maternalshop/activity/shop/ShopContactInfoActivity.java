package com.subzero.maternalshop.activity.shop;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.AppUtil;
import com.subzero.common.utils.ToastUtil;
import com.subzero.common.utils.ViewUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
/**店铺详情 宝贝分类  一级列表
 * 启动者：
 * 	默认首页：ShopDetailActivity
 * 启动项：
 * 	店铺详情 宝贝分类 二级列表：SortDetailListActivity
 * 适配器：
 * 	SortListAdapter
 * */
public class ShopContactInfoActivity extends BaseActivity
{
	private String qq;
	private String phone;
	private String email;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_shop_contact_info);
		Intent intent = getIntent();
		qq = intent.getStringExtra("qq");
		phone = intent.getStringExtra("phone");
		email = intent.getStringExtra("email");
		initView();
	}
	@Override
	public void initView()
	{
		ViewUtil.setText2TextView(findViewById(R.id.tv_qq), qq);
		ViewUtil.setText2TextView(findViewById(R.id.tv_email), email);
		ViewUtil.setText2TextView(findViewById(R.id.tv_phone), phone);
		if(TextUtils.isEmpty(qq)){
			findViewById(R.id.layout_qq).setVisibility(View.GONE);
		}else if(TextUtils.isEmpty(phone)){
			findViewById(R.id.layout_phone).setVisibility(View.GONE);
		}else if(TextUtils.isEmpty(email)){
			findViewById(R.id.layout_email).setVisibility(View.GONE);
		}
		findViewById(R.id.tv_phone).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_qq).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.tv_email).setOnClickListener(new MyOnClickListener());
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context,CommonClickType.back));
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.tv_phone == v.getId()){
				Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + phone));
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}else if(R.id.tv_email == v.getId()){
				/* 需要注意，email必须以数组形式传入*/  
				String[] emailAry = {email}; 
				Intent intent = new Intent(Intent.ACTION_SEND);  
				/* 设置邮件格式  */
				intent.setType("message/rfc822"); 
				/*接收人  */
				intent.putExtra(Intent.EXTRA_EMAIL, emailAry);
				/*抄送人 */
				//intent.putExtra(Intent.EXTRA_CC, email);  
				/*主题*/
				intent.putExtra(Intent.EXTRA_SUBJECT, "来自附近逛用户");   
				intent.putExtra(Intent.EXTRA_TEXT, "亲爱的卖家，您好！"); 
				startActivity(Intent.createChooser(intent, "请选择邮件类app")); 
			}else if(R.id.tv_qq == v.getId()){
				if(AppUtil.isQQClientAvailable(context)){
					String url="mqqwpa://im/chat?chat_type=wpa&uin="+qq;
					startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
				}else{
					ToastUtil.shortAtCenter(context, "请先安装QQ客户端");
				}
			}
		}
	}
}
