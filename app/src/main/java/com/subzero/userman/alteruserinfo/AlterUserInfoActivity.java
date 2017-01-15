package com.subzero.userman.alteruserinfo;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.subzero.common.listener.CommonClickListener;
import com.subzero.common.listener.clickcore.CommonClickType;
import com.subzero.common.utils.ToastUtil;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.activity.BaseActivity;
import com.subzero.maternalshop.config.App;
import com.subzero.maternalshop.config.User;
import com.subzero.userman.UserManActivity;
/**修改用户信息
 * 包含：
 * 	昵称、年龄、
 * 启动者：
 * 	个人中心：UserManActivity
 * */
public class AlterUserInfoActivity extends BaseActivity
{
	private String alterType;
	private EditText editText;
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alter_user_info);
		alterType = getIntent().getStringExtra("alterType");
		initView();
	}
	@Override
	public void initView()
	{
		findViewById(R.id.iv_back).setOnClickListener(new CommonClickListener(context, CommonClickType.back));
		findViewById(R.id.bt_submit).setOnClickListener(new MyOnClickListener());
		editText = (EditText) findViewById(R.id.et);
		TextView textView = (TextView) findViewById(R.id.tv_title);
		if(App.alterAge.equalsIgnoreCase(alterType)){
			/*修改年龄*/
			editText.setInputType(InputType.TYPE_CLASS_NUMBER);
			editText.setHint("请输入年龄");
			textView.setText("年龄");
			if(User.getAge(context)!=null){
				editText.setText(User.getAge(context));
			}
		}else if(App.alterNickName.equalsIgnoreCase(alterType)){
			/*修改昵称*/
			editText.setHint("请输入昵称");
			textView.setText("昵称");
			if(User.getGender(context)!=null){
				editText.setText(User.getNickName(context));
			}
		}
	}
	private final class MyOnClickListener implements OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.bt_submit == v.getId()){
				Intent data = new Intent(context, UserManActivity.class);
				 String ageNickName = editText.getText().toString();
				if(App.alterAge.equalsIgnoreCase(alterType)){
					 if(TextUtils.isEmpty(ageNickName)){
						 ToastUtil.shortAtCenter(context, "请输入年龄！");
						 return ;
					 }
					User.setAge(context,ageNickName);
					data.putExtra("age", ageNickName);
					setResult(RESULT_OK, data);
				}else if(App.alterNickName.equalsIgnoreCase(alterType)){
					 if(TextUtils.isEmpty(ageNickName)){
						 ToastUtil.shortAtCenter(context, "请输入昵称！");
						 return ;
					 }
					data.putExtra("nickName", ageNickName);
					setResult(RESULT_OK, data);
					User.setNickName(context, editText.getText().toString());
				}
				finish();
			}
		}
	}		
}
