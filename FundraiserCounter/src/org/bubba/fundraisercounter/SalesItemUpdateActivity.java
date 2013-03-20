package org.bubba.fundraisercounter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SalesItemUpdateActivity extends Activity
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		populateScreen();
	}

	private void populateScreen()
	{
		setContentView(R.layout.salesitemupdate);

		Intent sender = getIntent();
		final int id = sender.getExtras().getInt("id");
		
		final ArrayList<SalesItemsSold> list = FundraiserCounterActivity.readGsFilex(this);
		
		if(list == null || id < 0 || id > list.size())
		{
			return;
		}
		
		SalesItemsSold gscs = list.get(id);
		
		TextView titleView = (TextView) findViewById(R.id.salesitemfortextview);
		String gsName = gscs.getName();
		final String gsNameFinal = gsName;
		titleView.setText("Sales Items for  " + gsName);
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.mylayoutxxyz);
		if (ll == null)
		{
			return;
		}
		
		Button emailButton = (Button) findViewById(R.id.salesitemUpdateEmailButton);
        emailButton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v)
            {
		        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		        emailIntent.setType("plain/text"); 
		        
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Sales List as of "
						+ (new Date()).toString()); 
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "List of Items\n"
						+ getSalesItemListEmailString(id));
		        startActivity(emailIntent); 
            }
        });

		ArrayList<SalesItem> gsList = gscs.getItemsSoldList();
		int i = 0;
		int totalQuantity = 0;
		BigDecimal totalCost = BigDecimal.ZERO;
		
		for (Iterator<SalesItem> iterator = gsList.iterator(); iterator.hasNext();)
		{
			final SalesItem saleItem = (SalesItem) iterator.next();
			LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    
			RelativeLayout rl = (RelativeLayout) linflater.inflate(R.layout.salesitemlistupdaterow, null);
			
			TextView tvDesc = (TextView) rl.findViewById(R.id.salesitemrowdesc);
			tvDesc.setText(saleItem.getName());
			
	        tvDesc.setLongClickable(true);
	        tvDesc.setOnLongClickListener(new OnLongClickListener()
			{
				public boolean onLongClick(View v)
				{
					final View vv = v;
					
			        new AlertDialog.Builder(v.getContext())
			        .setIcon(android.R.drawable.ic_dialog_alert)
			        .setTitle("Delete Item?")
			        .setMessage("Do you want to delete\n\n" + saleItem.getName() + "?")
			        .setPositiveButton("Delete", new DialogInterface.OnClickListener() 
			        {
			            public void onClick(DialogInterface dialog, int which)
			            {	// they have clicked on the description so remove this item
			            	removeItemFromList(list, gsNameFinal, saleItem.getName(), vv.getContext());
			            	populateScreen();
			            }
			        })
			        .setNegativeButton("cancel", null)
			        .show();
					return true;
				}
			});

			TextView tvPrice = (TextView) rl.findViewById(R.id.salesitemrowprice);
			tvPrice.setText("" + saleItem.getCost().setScale(2)); 

			TextView tvQuantity = (TextView) rl.findViewById(R.id.salesitemrowquantity);
			int quantity = saleItem.getQuantity();
			totalQuantity += quantity;
			totalCost = totalCost.add(saleItem.getTotal());
			
			tvQuantity.setText("" + quantity);
			UpdateSalesItemLocator ucl = new UpdateSalesItemLocator(id, i, gsList);
			tvQuantity.setTag(ucl);

			TextView tvTotal = (TextView) rl.findViewById(R.id.salesitemrowtotalcost);
			tvTotal.setText(saleItem.getCost().multiply(new BigDecimal(saleItem.getQuantity())).setScale(2).toString()); 

			Button plusSign = (Button) rl.findViewById(R.id.salesitemrowplus);
			plusSign.setOnClickListener(new View.OnClickListener() 
	        {public void onClick(View v){(new UpdateSalesItemTotals()).updateRow(v, 1);}});
			
			Button plusMinus = (Button) rl.findViewById(R.id.salesitemrowminus);
			plusMinus.setOnClickListener(new View.OnClickListener()
			{public void onClick(View v){(new UpdateSalesItemTotals()).updateRow(v, -1);}});
			
			ll.addView(rl);
			
			LayoutInflater x = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			TextView tvLine = (TextView) x.inflate(R.layout.thelineb, null);
			ll.addView(tvLine);
			i += 1;
		}
		
		writeTotalLine(id, ll, i, totalQuantity, totalCost);
	}
	
	private void removeItemFromList(
			ArrayList<SalesItemsSold> list,
			String gsNameFinal, String itemName, Context context)
	{
		ArrayList<SalesItemsSold> newList = new ArrayList<SalesItemsSold>();
		SalesItemsSold salesItemsSold;
		
		for (Iterator<SalesItemsSold> iter = list.iterator(); iter.hasNext();)
		{
			salesItemsSold = iter.next();
			if(gsNameFinal.equals(salesItemsSold.getName()))
			{
				ArrayList<SalesItem> salesItemsSoldList = salesItemsSold.getItemsSoldList();
				ArrayList<SalesItem> newSalesItemsSoldList = new ArrayList<SalesItem>();
				SalesItem saleItem;
				
				for (Iterator<SalesItem> iter2 = salesItemsSoldList.iterator(); iter2.hasNext();)
				{
					saleItem = iter2.next();
					if(!saleItem.getName().equals(itemName))
					{
						newSalesItemsSoldList.add(saleItem);						
					}
				}
				salesItemsSold.setItemsSoldList(newSalesItemsSoldList);
			}
			newList.add(salesItemsSold);
		}
		FundraiserCounterActivity.saveGsFilex(context, newList);
	}
	
	void writeTotalLine(int id, LinearLayout ll, int i, int totalQuantity, BigDecimal totalCost)
	{
		LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
		RelativeLayout rl = (RelativeLayout) linflater.inflate(R.layout.salesitemstotalrow, null);

		TextView tvDesc = (TextView) rl.findViewById(R.id.salesitemrowdesc);
		tvDesc.setText("Total");

		TextView tvPrice = (TextView) rl.findViewById(R.id.salesitemrowprice);
		tvPrice.setText("    ");//3.50"); 

		TextView tvQuantity = (TextView) rl.findViewById(R.id.salesitemrowquantity);
		tvQuantity.setText("" + totalQuantity);

		TextView tvTotal = (TextView) rl.findViewById(R.id.salesitemrowtotalcost);
		tvTotal.setText(totalCost.setScale(2).toString()); 
		
		ll.addView(rl);
	}

	void getLineDivider(LinearLayout ll)
	{
		View view = new View(this);
		view.setBackgroundColor(0xFFFFFFFF);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, 2);
		view.setLayoutParams(params);
		ll.addView(view);
	}
	
	public class UpdateSalesItemTotals
	{
		public void updateRow(View v, int i)
		{
        	RelativeLayout rl = (RelativeLayout) v.getParent();
	    	TextView tvQuantity = (TextView) rl.findViewById(R.id.salesitemrowquantity);
	    	int q = (Integer.valueOf(tvQuantity.getText().toString())) + i;
			tvQuantity.setText("" + q);
			
			TextView tvPrice = (TextView) rl.findViewById(R.id.salesitemrowprice);
	    	BigDecimal p = new BigDecimal(tvPrice.getText().toString());
	
			TextView tvTotal = (TextView) rl.findViewById(R.id.salesitemrowtotalcost);
			tvTotal.setText(p.multiply(new BigDecimal(q)).setScale(2).toString());
			
			UpdateSalesItemLocator ucl = (UpdateSalesItemLocator) tvQuantity.getTag();
			
			ArrayList<SalesItemsSold> list = FundraiserCounterActivity.readGsFilex(v.getContext());
			SalesItemsSold gscs = list.get(ucl.personRow);
			SalesItem row = gscs.getItemsSoldList().get(ucl.getSalesItemRow());
			row.setQuantity(q);
    		FundraiserCounterActivity.saveGsFilex(v.getContext(), list);
    		
    		LinearLayout ll = (LinearLayout)v.getParent().getParent();
    		
    		RelativeLayout rlTotalRow = (RelativeLayout)ll.findViewById(R.id.rlsalesitemtotalrow);
    		TextView tvTotalQuantity = (TextView) rlTotalRow.findViewById(R.id.salesitemrowquantity);
    		TextView tvTotalTotal = (TextView) rlTotalRow.findViewById(R.id.salesitemrowtotalcost);
	    	
    		tvTotalQuantity.setText("" + (Integer.parseInt(tvTotalQuantity.getText().toString()) + i));
    		
    		BigDecimal bd = new BigDecimal(tvTotalTotal.getText().toString());
    		if(i == 1) bd = bd.add(row.getCost());
    		if(i == -1) bd = bd.subtract(row.getCost());
    		tvTotalTotal.setText(bd.toString());
		}
	}
	
	public class UpdateSalesItemLocator
	{
		private int personRow = -1;
		private int salesItemRow = -1;
		private ArrayList<SalesItem> gsList;
		
		public UpdateSalesItemLocator(int personRow, int salesitemRow, ArrayList<SalesItem> gsList)
		{
			super();
			this.personRow = personRow;
			this.salesItemRow = salesitemRow;
			this.gsList = gsList;
		}

		public int getPersonRow() {
			return personRow;
		}

		public void setPersonRow(int personRow) {
			this.personRow = personRow;
		}

		public int getSalesItemRow() {
			return salesItemRow;
		}

		public void setSalesItemRow(int salesItemRow) {
			this.salesItemRow = salesItemRow;
		}

		public ArrayList<SalesItem> getGsList() {
			return gsList;
		}

		public void setGsList(ArrayList<SalesItem> gsList) {
			this.gsList = gsList;
		}
	}

	protected String getSalesItemListEmailString(int id)
	{
		StringBuffer sb = new StringBuffer(100);
		
		ArrayList<SalesItemsSold> arrayList = FundraiserCounterActivity.readGsFilex(this);
		
    	SalesItemsSold gscs = arrayList.get(id);
    	
    	BigDecimal personTotal = new BigDecimal("0.00").setScale(2);
    	sb.append(gscs.getName() + "\n");

    	int namelen = 20;
    	int quantitylen = 5;
    	int costlen = 8; 
    	int saletotallen = 8;
    	
    	for (Iterator<SalesItem> iter2 = gscs.getItemsSoldList().iterator(); iter2.hasNext();)
		{
			SalesItem gsc = iter2.next();
			namelen = FundraiserUtil.whichIsLarger(gsc.getName().length(), namelen);
			quantitylen = FundraiserUtil.whichIsLarger(gsc.getQuantity(), quantitylen);
			costlen = FundraiserUtil.whichIsLarger(("" + gsc.getCost()).toString().length(), costlen);
			saletotallen = FundraiserUtil.whichIsLarger(gsc.getTotal().toString().length(), namelen);
		}
    	
    	for (Iterator<SalesItem> iter2 = gscs.getItemsSoldList().iterator(); iter2.hasNext();)
		{
			SalesItem gsc = iter2.next();
			sb.append(gsc.toStringBuffer(namelen, quantitylen, costlen, saletotallen));
			BigDecimal saleTotal = gsc.getTotal();
			personTotal = personTotal.add(saleTotal);
		}
    	sb.append("    total = " + personTotal.toString());
		
		return sb.toString();
	}
}
