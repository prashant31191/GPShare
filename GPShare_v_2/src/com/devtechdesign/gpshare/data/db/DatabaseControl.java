package com.devtechdesign.gpshare.data.db;

import android.content.Context;

import com.dtd.dbeagen.db.transactions.JournalsDBTransactions;
import com.dtd.dbeagen.db.transactions.MyPlacesDBTransactions;
import com.dtd.dbeagen.db.transactions.PlaceMarkDBTransactions;
import com.dtd.dbeagen.db.transactions.TripDBTransactions;

public class DatabaseControl {

	public JournalsDBTransactions journalsDbc;
	public TripDBTransactions tripDbc;
	public GPShareDbTrans gpsDbc;
	public MyPlacesDBTransactions myplacesDbc;
	public PlaceMarkDBTransactions pmDbc;
	
	private static DatabaseControl instance = new DatabaseControl();
	private Context context;

	public static DatabaseControl getInstance() {
		return instance;
	}

	private DatabaseControl() {

	}

	public void setUpConnections(Context context) {
		journalsDbc = new JournalsDBTransactions(context);
		tripDbc = new TripDBTransactions(context);
		myplacesDbc = new MyPlacesDBTransactions(context);
		gpsDbc = new GPShareDbTrans(context);
		pmDbc = new PlaceMarkDBTransactions(context);
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		setUpConnections(context);
		this.context = context;
	}
}
