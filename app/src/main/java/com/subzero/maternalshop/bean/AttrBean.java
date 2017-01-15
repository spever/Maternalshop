package com.subzero.maternalshop.bean;

import java.util.List;

public class AttrBean
{
	public String titleId;
	public String titleName;
	public  List<String> listContentId;
	public  List<String> listContentName;
	public  List<String> listContentFormatPrice;
	@Override
	public String toString()
	{
		return "AttrBean [titleId=" + titleId + ", titleName=" + titleName + ", listContentId=" + listContentId + ", listContentName=" + listContentName + "]";
	}
	
}
