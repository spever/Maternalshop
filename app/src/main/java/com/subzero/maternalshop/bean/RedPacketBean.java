package com.subzero.maternalshop.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class RedPacketBean implements Parcelable
{
	public String bonus_id;
	public String type_id;
	public String min_goods_amount;
	public String bonus_money_formated;
	public String status;
	public String supplier_id;
	public String supplier;
	public String type_money;
	public String type_name;
	public String use_enddate;
	public String use_startdate;
	/**已经失效*/
	public boolean isLosed;
	public boolean canShowLine;
	public boolean canShowTopLine;
	public boolean isSelected;
	@Override
	public int describeContents(){
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags)
	{
		dest.writeString(bonus_id);  
		dest.writeString(bonus_money_formated);  
		dest.writeString(supplier_id);
		dest.writeString(type_id);
		dest.writeString(type_money);
		dest.writeString(type_name);
	}
	public static final Creator<RedPacketBean> CREATOR  = new Creator<RedPacketBean>() {
		//实现从source中创建出类的实例的功能  
		@Override  
		public RedPacketBean createFromParcel(Parcel source) {  
			RedPacketBean bean  = new RedPacketBean();  
			bean.bonus_id = source.readString();  
			bean.bonus_money_formated= source.readString();  
			bean.supplier_id = source.readString();  
			bean.type_id = source.readString();
			bean.type_money = source.readString();
			bean.type_name = source.readString();
			return bean;  
		}  
		//创建一个类型为T，长度为size的数组  
		@Override  
		public RedPacketBean[] newArray(int size) {  
			return new RedPacketBean[size];  
		}  
	};
	@Override
	public String toString()
	{
		return "RedPacketBean [bonus_id=" + bonus_id + ", type_id=" + type_id + ", supplier_id=" + supplier_id + ", supplier=" + supplier + ", type_money=" + type_money + ", type_name=" + type_name
				+ "]";
	}
	
}
