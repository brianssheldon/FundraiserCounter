package org.bubba.fundraisercounter;

import java.io.Serializable;
import java.util.ArrayList;

public class SalesItemsSold implements Serializable
{
	private static final long serialVersionUID = 123L;
	private String name;
	private ArrayList<SalesItem> itemsSoldList;
	private String address1;
	private String address2;
	private String phone;
	
	public SalesItemsSold()
	{
		itemsSoldList = new ArrayList<SalesItem>();
		name = "";
		address1 = "";
		address2 = "";
		phone = "";
	}

	public SalesItemsSold(String name, ArrayList<SalesItem> itemsSoldList)
	{
		super();
		this.name = name;
		this.itemsSoldList = itemsSoldList;
		address1 = "";
		address2 = "";
		phone = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<SalesItem> getItemsSoldList() {
		return itemsSoldList;
	}

	public void setItemsSoldList(ArrayList<SalesItem> itemsSoldList) {
		this.itemsSoldList = itemsSoldList;
	}

	public String getAddress1() {
		return address1;
	}

	public void setAddress1(String address1) {
		this.address1 = address1;
	}

	public String getAddress2() {
		return address2;
	}

	public void setAddress2(String address2) {
		this.address2 = address2;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}