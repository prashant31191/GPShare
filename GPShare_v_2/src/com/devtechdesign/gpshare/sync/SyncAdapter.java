package com.devtechdesign.gpshare.sync;

import java.util.ArrayList;

import com.devtechdesign.gpshare.AppData;
import com.dtd.dbeagen.db.elements.aJournals; 
import com.dtd.dbeagen.db.elements.aTrip;
import com.dtd.dbeagen.db.transactions.JournalsDBTransactions;
import com.dtd.dbeagen.db.transactions.TripDBTransactions;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

	private Context context;
	private WebSyncUtil jwt;
	private JournalsDBTransactions dbt;
	private TripDBTransactions tbt;

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		this.context = context;
		jwt = new WebSyncUtil(context);
		dbt = new JournalsDBTransactions(context);
		tbt = new TripDBTransactions(context);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
		if (AppData.getInstance().getJsResponder() != null)
			AppData.getInstance().getJsResponder().onPreSync();

		ArrayList<aJournals> dirtyJournals = dbt.getDirtyJournals();
		Log.i("GPShare", "Syncing: " + String.valueOf(dirtyJournals.size()) + " dirty journals");
		if (dirtyJournals.size() > 0) {
			jwt.sync(dirtyJournals);
		}
		
		ArrayList<aTrip> dirtyTrips = tbt.getDirtyTrip(); 
		
		Log.i("GPShare", "Syncing: " + String.valueOf(dirtyJournals.size()) + " dirty journals");
		if (dirtyTrips.size() > 0) {
			jwt.sync(dirtyJournals);
		}
		
	}
}
