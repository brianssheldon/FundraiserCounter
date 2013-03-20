package org.bubba.fundraisercounter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;

public class FundraiserUtil
{
	public static String getSalesItemsTotalsForEmail(ArrayList<SalesItemsSold> arrayList)
	{
		StringBuffer sb = new StringBuffer(100);
		sb.append("\n\nSales Item Totals\n\n");
		BigDecimal grandTotal = new BigDecimal("0.00").setScale(2);
		int grandTotalQuantity = 0;
		ArrayList<SalesItem> gtList = new ArrayList<SalesItem>();

    	int namelen = 20;
    	int quantitylen = 5;
    	int saletotallen = 8;
    	
		for (Iterator<SalesItemsSold> iter = arrayList.iterator(); iter.hasNext();)
		{
        	SalesItemsSold gscs = (SalesItemsSold) iter.next();
        	
        	for (Iterator<SalesItem> iter2 = gscs.getItemsSoldList().iterator(); iter2.hasNext();)
			{
				SalesItem gsc = iter2.next();
				boolean found = false;
				
				for(Iterator<SalesItem> iter3 = gtList.iterator(); iter3.hasNext();)
				{
					SalesItem gtSalesItem = iter3.next();
					if(gtSalesItem.getName().equals(gsc.getName()))
					{
						gtSalesItem.setQuantity(gtSalesItem.getQuantity() + gsc.getQuantity());
						found = true;
						break;
					}
				}
				
				if(!found)
				{
					SalesItem newSaleItem = new SalesItem();
					newSaleItem.setName(gsc.getName());
					newSaleItem.setQuantity(gsc.getQuantity());
					newSaleItem.setCost(gsc.getCost());
					gtList.add(newSaleItem);
				}
			}
		}

		for(Iterator<SalesItem> iter3 = gtList.iterator(); iter3.hasNext();)
		{
			SalesItem gtSalesItem = iter3.next();

	    	namelen = whichIsLarger(gtSalesItem.getName().length(), namelen);
	    	quantitylen = whichIsLarger(("" + gtSalesItem.getQuantity()).length(), quantitylen);
	    	saletotallen = whichIsLarger(gtSalesItem.getTotal().toString().length(), saletotallen);
		}
		for(Iterator<SalesItem> iter3 = gtList.iterator(); iter3.hasNext();)
		{
			SalesItem gtSalesItem = iter3.next();
			sb.append(padWithSpaces(gtSalesItem.getName(), namelen) + " ");
			sb.append(padWithSpaces("" + gtSalesItem.getQuantity(), quantitylen) + " ");
			sb.append(padWithSpaces("" + gtSalesItem.getTotal(), saletotallen) + " ");
			sb.append("\n");
			
			grandTotal = grandTotal.add(gtSalesItem.getTotal());
			grandTotalQuantity += gtSalesItem.getQuantity();
		}
		
		sb.append("\n");
		sb.append(padWithSpaces("Total", 15) + " ");
		sb.append(padWithSpaces("" + grandTotalQuantity, 3) + " ");
		sb.append(padWithSpaces(grandTotal.toString(), 6) + " ");
		sb.append("\n");
		
		return sb.toString();
	}

	public static int whichIsLarger(int length, int namelen)
	{
		if(length > namelen) return length;
		return namelen;
	}

	private static String padWithSpaces(String name, int i)
	{
		return (name + "                                         ").substring(0, i);
	}
}