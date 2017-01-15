package com.subzero.maternalshop.dialog;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.subzero.common.dialog.BaseDialog;
import com.subzero.common.dialog.StatusBarUtils;
import com.subzero.maternalshop.R;

public class ContentDialog extends BaseDialog
{
	private OnDialogClickListener onDialogClickListener;
	private TextView tvContent;
	public ContentDialog(Context context) {
		super(context,R.style.dialog_common);
		this.context = context;
		initBaseDialogTheme();
		initDialog();
	}
	public ContentDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		initBaseDialogTheme();
	}
	
	public void setContent(String content){
		tvContent.setText(content+"");
	}
	private final class MyOnClickListener implements View.OnClickListener
	{
		@Override
		public void onClick(View v)
		{
			if(R.id.tv_submit == v.getId()){
				if(onDialogClickListener!=null){
					onDialogClickListener.onDialogClick(Gravity.CENTER);
				}else{
					dismiss();
				}
			}
		}
	}
	public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener){
		this.onDialogClickListener = onDialogClickListener;
	}
	public interface OnDialogClickListener{
		/**Gravity.LEFT  |  Gravity.RIGHT  |  Gravity.CENTER*/
		public void onDialogClick(int gravity);
	}
	@Override
	public void initDialog()
	{
		dm = getContext().getResources().getDisplayMetrics();
		maxHeight = dm.heightPixels - StatusBarUtils.getHeight(context);
		rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_content, null);
		tvContent = (TextView) rootView.findViewById(R.id.tv_content);
		rootView.findViewById(R.id.tv_submit).setOnClickListener(new MyOnClickListener());
		setContentView(rootView);
		setCancelable(true);
	}
}
