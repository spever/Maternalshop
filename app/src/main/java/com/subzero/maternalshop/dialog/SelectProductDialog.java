package com.subzero.maternalshop.dialog;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.util.LogUtils;
import com.subzero.common.dialog.BaseDialog;
import com.subzero.common.dialog.StatusBarUtils;
import com.subzero.common.view.CountLayout;
import com.subzero.common.view.FlowLayout;
import com.subzero.maternalshop.R;
import com.subzero.maternalshop.bean.AttrBean;
public class SelectProductDialog  extends BaseDialog
{
	private List<AttrBean> listAttrBean;
	private LinearLayout layoutAttr;
	private OnAttrSelectorListener onAttrSelectorListener;
	private List<List<TextView>> listTvContent;
	private EditText editText;
	/**优惠信息*/
	private TextView tvSpecial;
	private TextView tvPrice;
	private TextView tvDesc;
	/**商品 原价*/
	private String price;
	public SelectProductDialog(Context context) {
		super(context,R.style.dialog_common);
		this.context = context;
		initBaseDialogTheme();
		initDialog();
	}
	public SelectProductDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
		initBaseDialogTheme();
	}
	@Override
	public void initDialog()
	{
		listAttrBean = new ArrayList<AttrBean>();
		listTvContent= new ArrayList<List<TextView>>();
		dm = getContext().getResources().getDisplayMetrics();
		maxHeight = dm.heightPixels - StatusBarUtils.getHeight(context);
		rootView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_product, null);
		layoutAttr = (LinearLayout) rootView.findViewById(R.id.layout_attr);
		editText = ((CountLayout) rootView.findViewById(R.id.cl_product)).getEditText();
		tvSpecial  = (TextView) rootView.findViewById(R.id.tv_special);
		tvPrice = (TextView) rootView.findViewById(R.id.tv_price);
		tvDesc = (TextView) rootView.findViewById(R.id.tv_desc);
		rootView.findViewById(R.id.tv_submit).setOnClickListener(new MyOnClickListener(-1, -1));
		setContentView(rootView);
		setCancelable(true);
	}
	public EditText getEditText(){
		return editText;
	}
	/**获取优惠信息  标签*/
	public TextView getSpecialTextView(){
		return tvSpecial;
	}
	/**获取价格信息  标签*/
	public TextView getPriceTextView(){
		return tvPrice;
	}
	/**得到 原价*/
	public void setPrice(String price){
		LogUtils.e("price = "+price);
		this.price = price;
	}
	/***/
	public String getPrice(){
		return price;
	}
	public void setDataSet(List<AttrBean> list){
		LogUtils.e("setDataSet  执行了");
		this.listAttrBean = list;
		if(list==null || list.size()<=0){
			tvPrice.setText("￥"+price);
			return ;
		}
		for (int i = 0; i < list.size(); i++)
		{
			AttrBean bean = list.get(i);
			View view = LayoutInflater.from(context).inflate(R.layout.dialog_select_product_cell_1, null);
			TextView tvTitle = (TextView) view.findViewById(R.id.tv_title);
			tvTitle.setText(bean.titleName);
			FlowLayout layoutContent = (FlowLayout) view.findViewById(R.id.layout_content);
			List<String> listContentName = bean.listContentName;
			List<TextView> listTextView = new ArrayList<TextView>();
			for (int j = 0; (listContentName!=null) && (j<listContentName.size()); j++)
			{
				View attrContentView = LayoutInflater.from(context).inflate(R.layout.dialog_select_product_cell_2, layoutContent,false);
				TextView tvName = (TextView) attrContentView.findViewById(R.id.tv_name);
				tvName.setText(listContentName.get(j));
				tvName.setOnClickListener(new MyOnClickListener(i,j));
				if(j==0){
					tvName.setSelected(true);
					tvName.setTag(true);
				}else{
					tvName.setTag(false);
				}
				listTextView.add(tvName);
				layoutContent.addView(tvName);
			}
			listTvContent.add(listTextView);
			layoutAttr.addView(view);
		}
		getFormatPrice();
	}
	private final class MyOnClickListener implements View.OnClickListener
	{
		/**标题的下表*/
		private int titlePosition;
		/**内容的下表*/
		private int contentPosition;
		public MyOnClickListener(int titlePosition, int contentPosition) {
			this.titlePosition = titlePosition;
			this.contentPosition = contentPosition;
		}
		@Override
		public void onClick(View v)
		{
			StringBuilder builder = new StringBuilder();
			if((titlePosition == -1) && (contentPosition ==-1)){
				for (int i = 0; (listTvContent!=null) && (i<listTvContent.size()); i++)
				{
					List<TextView> list = listTvContent.get(i);
					for (int j = 0; (list!=null) && (j<list.size()); j++)
					{
						TextView textView = list.get(j);
						Boolean tag = (Boolean) textView.getTag();
						if(tag){
							builder.append(listAttrBean.get(i).listContentId.get(j)+"|");
						}
					}
				}
				if((onAttrSelectorListener!=null)){
					if((builder!=null) && (builder.length()>0)){
						onAttrSelectorListener.onAttrSelector(editText.getText().toString(), builder.substring(0, builder.length()-1), price);
					}else{
						onAttrSelectorListener.onAttrSelector(editText.getText().toString(), "", price);
					}
				}
				dismiss();
			}else{
				if ((listTvContent!=null) && (titlePosition<listTvContent.size()))
				{
					List<TextView> list = listTvContent.get(titlePosition);
					for (int j = 0; (list!=null) && (j<list.size()); j++)
					{
						TextView textView = list.get(j);
						if(j==contentPosition){
							textView.setSelected(true); 
							textView.setTag(true);
						}else{
							textView.setSelected(false);
							textView.setTag(false);
						}
					}
					getFormatPrice();
				}
			}
		}
	}
	private String getFormatPrice(){
		StringBuilder builder = new StringBuilder();
		builder.append("已选：");
		float formatPrice = 0;
		for (int i = 0; (listTvContent!=null) && (i<listTvContent.size()); i++)
		{
			List<TextView> list = listTvContent.get(i);
			for (int j = 0; (list!=null) && (j<list.size()); j++)
			{
				TextView textView = list.get(j);
				Boolean tag = (Boolean) textView.getTag();
				if(tag){
					//LogUtils.e("listContentFormatPrice = "+listAttrBean.get(i).listContentFormatPrice);
					String price = listAttrBean.get(i).listContentFormatPrice.get(j);
					builder.append(listAttrBean.get(i).listContentName.get(j)+"  ");
					formatPrice += Float.parseFloat(price);
				}
			}
		}
		tvDesc.setText(builder.toString());
		LogUtils.e("FormatPrice = "+builder.toString()+" formatPrice = "+formatPrice);
		if(formatPrice == 0){
			LogUtils.e("属性 价格  为零  price = "+price);
			tvPrice.setText("￥"+price);
			return price;
		}else{
			LogUtils.e("属性 价格  不为零");
			tvPrice.setText("￥"+(Float.parseFloat(price)+formatPrice));
			 return formatPrice+"";
		}
	}
	public void setOnAttrSelectorListener(OnAttrSelectorListener onAttrSelectorListener){
		this.onAttrSelectorListener = onAttrSelectorListener;
	}
	public interface OnAttrSelectorListener{
		public void onAttrSelector(String count, String spec, String price);
	}

}
