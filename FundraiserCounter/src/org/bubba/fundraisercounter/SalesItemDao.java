package org.bubba.fundraisercounter;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;

public class SalesItemDao implements Serializable
{
	private static final String LIST_OF_SALES_ITEMS = "listOfSalesItems";
	private static final long serialVersionUID = 197245L;
	private ArrayList<SalesItem> list;

	public SalesItemDao()
	{
		list = new ArrayList<SalesItem>();
	}
	
	public ArrayList<SalesItem> readFile(Context context)
	{
		ArrayList<SalesItem> list;
		try
		{
			FileInputStream fis = context.openFileInput(LIST_OF_SALES_ITEMS);
	    	ObjectInputStream in = new ObjectInputStream(fis);
	    	list = (ArrayList<SalesItem>) in.readObject();
	    	in.close();
	    	fis.close();
		}
		catch (Exception e)
		{
			try
			{
				list = new ArrayList<SalesItem>();
				list.add(new SalesItem("Long press to delete"));
				
				FileOutputStream fos = context.openFileOutput(LIST_OF_SALES_ITEMS, Context.MODE_PRIVATE);
				ObjectOutputStream out = new ObjectOutputStream(fos);
				out.writeObject(list);
				out.close();
				fos.close();
			}
			catch (Exception e2)
			{
				e2.printStackTrace();
				list = new ArrayList<SalesItem>();
			}
		}
		return list;
	}
	
	public void writeFile(ArrayList<SalesItem> list, Context context)
	{
		try
		{
			FileOutputStream fos = context.openFileOutput(LIST_OF_SALES_ITEMS, Context.MODE_PRIVATE);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(list);
			out.close();
			fos.close();
		}
		catch (Exception e2)
		{
			e2.printStackTrace();
		}
	}
	
	public ArrayList<SalesItem> getList()
	{
		return list;
	}

	public void setList(ArrayList<SalesItem> list)
	{
		this.list = list;
	}

	public void remove(String nameFinal, Context context)
	{
		ArrayList<SalesItem> salesItemList = readFile(context);
		ArrayList<SalesItem> newSalesItemList = new ArrayList<SalesItem>();
		SalesItem saleItem;
		
		for (Iterator<SalesItem> iter = salesItemList.iterator(); iter.hasNext();)
		{
			saleItem = iter.next();
			if(!nameFinal.equals(saleItem.getName()))
			{
				newSalesItemList.add(saleItem);
			}
		}
		writeFile(newSalesItemList, context);
	}
}