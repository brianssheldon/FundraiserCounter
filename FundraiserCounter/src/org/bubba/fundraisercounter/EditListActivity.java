package org.bubba.fundraisercounter;

import java.math.BigDecimal;
import java.util.ArrayList;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EditListActivity extends Activity
{
	SalesItemDao salesItemDao = new SalesItemDao();
	
    /** Called when the activity is first created. */
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        drawScreen();
    }

	private void drawScreen() 
	{
		setContentView(R.layout.salesitemlistupdate);
        
        ArrayList<SalesItem> list = salesItemDao.readFile(this);
        
        LinearLayout ll = (LinearLayout) findViewById(R.id.updateSalesItemListList);
        
        for (Iterator<SalesItem> iterator = list.iterator(); iterator.hasNext();)
		{
			SalesItem saleItem = (SalesItem) iterator.next();
			populateRow(ll, saleItem);
		}
        
        populateRow(ll, new SalesItem());
        
        makeSaveButtonListener();
        makeExitButtonListener();
	}
	
	void populateRow(LinearLayout ll, SalesItem saleItem)
	{
		LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout rl = (RelativeLayout) linflater.inflate(R.layout.salesitemupdaterow, null);
		
		TextView tvDesc = (TextView) rl.findViewById(R.id.salesitemupdaterowdesc);
		String name = saleItem.getName();
		if(name == null) name = "";
		tvDesc.setText(name);

		TextView tvPrice = (TextView) rl.findViewById(R.id.salesitemupdaterowprice);
		BigDecimal cost = saleItem.getCost();
		if(cost == null) cost = new BigDecimal("0.00");
		tvPrice.setText("" + cost.setScale(2, BigDecimal.ROUND_HALF_UP));
		
		final String nameFinal = name;
        tvDesc.setLongClickable(true);
        tvDesc.setOnLongClickListener(new OnLongClickListener()
		{
			public boolean onLongClick(View v)
			{
				final View vv = v;
				
		        new AlertDialog.Builder(v.getContext())
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle("Delete Item?")
		        .setMessage("Do you want to delete\n\n" + nameFinal + "?")
		        .setPositiveButton("Delete", new DialogInterface.OnClickListener() 
		        {
		            public void onClick(DialogInterface dialog, int which)
		            {	// they have clicked on the description so remove this sales item
		            	salesItemDao.remove(nameFinal, vv.getContext());
		            	
		            	drawScreen();
		            }
		        })
		        .setNegativeButton("cancel", null)
		        .show();
				return true;
			}
		});
		
		ll.addView(rl);
	}

	void makeSaveButtonListener()
	{
		Button saveButton = (Button)findViewById(R.id.savesalesitemlist);
        saveButton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v)
            {
        		SalesItemDao saleItemDao = new SalesItemDao();
                ArrayList<SalesItem> list = new ArrayList<SalesItem>();
                LinearLayout ll = (LinearLayout) findViewById(R.id.updateSalesItemListList);
                int x = ll.getChildCount();
                
                for (int i = 0; i < x; i++)
				{
                	RelativeLayout rl = (RelativeLayout) ll.getChildAt(i);
					EditText descView = (EditText)rl.getChildAt(0);
					String name = descView.getText().toString();
					if(name == null || "".equals(name))continue;
					
					EditText priceView = (EditText)rl.getChildAt(1);
					String price = priceView.getText().toString();
					if(price == null || "".equals(price)) continue;
					
					SalesItem salesItem = new SalesItem();
					salesItem.setName(name);
					salesItem.setCost(new BigDecimal(price));
					list.add(salesItem);
				}
                
                saleItemDao.writeFile(list, v.getContext());
                
        		ll.removeAllViews();	// remove all views
                
                for (Iterator<SalesItem> iterator = list.iterator(); iterator.hasNext();)
        		{
        			SalesItem saleItem = (SalesItem) iterator.next();
        			populateRow(ll, saleItem);
        		}
                populateRow(ll, new SalesItem());
                
                mergeSalesItemsList(v, saleItemDao);
            }

			private void mergeSalesItemsList(View v, SalesItemDao salesItemDao)
			{
				ArrayList<SalesItemsSold> gsList = FundraiserCounterActivity.readGsFilex(v.getContext());
                ArrayList<SalesItem> salesItemsList = salesItemDao.readFile(v.getContext());
        		
                if(gsList.size() < 1 || salesItemsList.size() < 1) return;
                
                SalesItemsSold anItemSold;
                ArrayList<SalesItem> listOfSalesItems;
                SalesItem anItem;
                SalesItem salesItem;
                
                for (Iterator<SalesItemsSold> iterator = gsList.iterator(); iterator.hasNext();)
                {
					anItemSold = iterator.next();
					listOfSalesItems = anItemSold.getItemsSoldList();
					
					for (Iterator<SalesItem> iter2 = listOfSalesItems.iterator(); iter2.hasNext();)
					{
						anItem = iter2.next();
					
						for (Iterator<SalesItem> iter3 = salesItemsList.iterator(); iter3.hasNext();) 
						{
							salesItem = iter3.next();
							
							if(anItem.getName().equals(salesItem.getName()))
							{
								if(!anItem.getCost().equals(salesItem.getCost()))
								{
									anItem.setCost(salesItem.getCost());
								}
							}
						}
					}
					
					for (Iterator<SalesItem> iter3 = salesItemsList.iterator(); iter3.hasNext();) 
					{
						salesItem = iter3.next();
						boolean found = false;
						
						for (Iterator<SalesItem> iter2 = listOfSalesItems.iterator(); iter2.hasNext();)
						{
							anItem = iter2.next();
							
							if(anItem.getName().equals(salesItem.getName()))
							{
								found = true;
								break;
							}
						}
						
						if(!found)
						{
							SalesItem newSalesItem = new SalesItem(salesItem.getName(),
														  salesItem.getCost(),
														  salesItem.getQuantity()); 
							listOfSalesItems.add(newSalesItem);
						}
					}
				}

                FundraiserCounterActivity.saveGsFilex(v.getContext(), gsList);
        		salesItemDao.writeFile(salesItemsList, v.getContext());
			}
        });
	}

	private void makeExitButtonListener()
	{
		Button exitButton = (Button)findViewById(R.id.exitSalesItemList);
        exitButton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v)
            {
		    	Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        });
	}
}
