package org.bubba.fundraisercounter;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactActivity extends Activity
{
	ArrayList<SalesItemsSold> contactList;
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		populateScreen();
	}

	private void populateScreen()
	{
		setContentView(R.layout.contactlist);
		contactList = FundraiserCounterActivity.readGsFilex(this);
		
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.updateContactListList);

		populateRow(ll, new SalesItemsSold());
		
		for (int i = 0; i < contactList.size(); i++)
		{
			populateRow(ll, contactList.get(i));
		}

        makeSaveButtonListener();
        makeEmailButtonListener();
	}

	private void populateRow(LinearLayout ll, SalesItemsSold contact)
	{
		LayoutInflater linflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout rl = (RelativeLayout) linflater.inflate(R.layout.contactrow, null);

		final EditText tvName = (EditText) rl.findViewById(R.id.contactRowName);
		
		tvName.setLongClickable(true);
		tvName.setOnLongClickListener(new OnLongClickListener()
		{
			public boolean onLongClick(View v)
			{
				final View vv = v;
				
		        new AlertDialog.Builder(v.getContext())
		        .setIcon(android.R.drawable.ic_dialog_alert)
		        .setTitle("Delete Item?")
		        .setMessage("Do you want to delete\n\n" + tvName.getText().toString() + "?")
		        .setPositiveButton("Delete", new DialogInterface.OnClickListener() 
		        {
		            public void onClick(DialogInterface dialog, int which)
		            {	// they have clicked on the description so remove this salesitem
		            	deleteFromContact(tvName.getText().toString());
		            	populateScreen();
		            }

					private void deleteFromContact(String name)
					{
						SalesItemsSold contact = findContact(name);
						if(contact != null)
						{
							contactList.remove(contact);
							FundraiserCounterActivity.saveGsFilex(vv.getContext(), contactList);
						}
					}

					private SalesItemsSold findContact(String name) {
						for (Iterator<SalesItemsSold> iterator = contactList.iterator(); iterator.hasNext();)
						{
							SalesItemsSold contact = iterator.next();
							if(contact.getName().equals(name)) return contact;
						}
						return null;
					}
		        })
		        .setNegativeButton("cancel", null)
		        .show();
				return true;
			}
		});
		
		EditText tvAdd1 = (EditText) rl.findViewById(R.id.contactRowAddress1);
		EditText tvAdd2 = (EditText) rl.findViewById(R.id.contactRowAddress2);
		EditText tvPhoneNumber = (EditText) rl.findViewById(R.id.contactRowPhoneNumber);
		
		tvName.setText(contact.getName());
		tvAdd1.setText(contact.getAddress1());
		tvAdd2.setText(contact.getAddress2());
		tvPhoneNumber.setText(contact.getPhone());
		
		ll.addView(rl);

		LayoutInflater x = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		TextView tvLine = (TextView) x.inflate(R.layout.thelineb, null);
		tvLine.setTextSize(8);
		ll.addView(tvLine);
	}
	
	private void makeEmailButtonListener()
	{
		Button emailButton = (Button)findViewById(R.id.emailcontactlist);
        emailButton.setOnClickListener(new View.OnClickListener() 
        {
        	public void onClick(View v)
            {
		        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
		        emailIntent.setType("plain/text"); 
		        
				emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Contact List as of "
						+ (new Date()).toString()); 
				emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "List of Contacts\n\n"
						+ getContactsListEmailString());
		        startActivity(emailIntent); 
            }

			private String getContactsListEmailString()
			{
				StringBuffer emailSB = new StringBuffer();
				
				for (Iterator<SalesItemsSold> iterator = contactList.iterator(); iterator.hasNext();)
				{
					SalesItemsSold salesItemSold = iterator.next();
					emailSB.append(getLine(salesItemSold.getName()));
					emailSB.append(getLine(salesItemSold.getAddress1()));
					emailSB.append(getLine(salesItemSold.getAddress2()));
					emailSB.append(getLine(salesItemSold.getPhone()));
					emailSB.append("\n");
				}
				
				return emailSB.toString();
			}

			private String getLine(String string)
			{
				if(string == null || "".equals(string)) return "";
				
				return string + "\n";
			}
        });
	}

	void makeSaveButtonListener()
	{
		Button saveButton = (Button)findViewById(R.id.savecontactlist);
        saveButton.setOnClickListener(new View.OnClickListener() 
        {
            public void onClick(View v)
            {
                LinearLayout ll = (LinearLayout) findViewById(R.id.updateContactListList);
                int x = ll.getChildCount();
                contactList = new ArrayList<SalesItemsSold>();
                
                for (int i = 0; i < x; i++)
				{
                	if(!(ll.getChildAt(i) instanceof RelativeLayout)) continue;
                	
                	RelativeLayout rl = (RelativeLayout) ll.getChildAt(i);
					EditText nameView = (EditText)rl.getChildAt(0);
					String name = nameView.getText().toString();
					if(name == null || "".equals(name))continue;
					
					EditText add1View = (EditText)rl.getChildAt(1);
					String add1 = add1View.getText().toString();
					if(add1 == null) add1 = "";
					
					EditText add2View = (EditText)rl.getChildAt(2);
					String add2 = add2View.getText().toString();
					if(add2 == null) add2 = "";
					
					EditText phoneView = (EditText)rl.getChildAt(3);
					String phone = phoneView.getText().toString();
					if(phone == null) phone = "";
					
					SalesItemsSold contact = new SalesItemsSold();
					contact.setName(name);
					contact.setAddress1(add1);
					contact.setAddress2(add2);
					contact.setPhone(phone);
					if(contact.getItemsSoldList() == null || contact.getItemsSoldList().isEmpty())
					{
						contact.setItemsSoldList(getListOfSalesItems());
					}
					
					contactList.add(contact);
				}
                
				FundraiserCounterActivity.saveGsFilex(v.getContext(), contactList);
                
        		ll.removeAllViews();	// remove all views

        		populateRow(ll, new SalesItemsSold());
        		
        		for(Iterator<SalesItemsSold> iterator = contactList.iterator(); iterator.hasNext();)
        		{
        			populateRow(ll, iterator.next());
        		}
            }
        });
	}
	
	ArrayList<SalesItem> getListOfSalesItems()
	{
		SalesItemDao salesitemDao = new SalesItemDao();
		ArrayList<SalesItem> theList = salesitemDao.readFile(this);
		ArrayList<SalesItem> newList = new ArrayList<SalesItem>();
		
		for(Iterator<SalesItem> iter = theList.iterator(); iter.hasNext();)
		{
			SalesItem thepc = iter.next();
			SalesItem pc = new SalesItem();
			pc.setName(thepc.getName());
			pc.setQuantity(thepc.getQuantity());
			pc.setCost(thepc.getCost());
			newList.add(thepc);
		}
		return newList;
	}
}