package com.devtechdesign.gpshare.sync;

import java.util.ArrayList;


import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.devtechdesign.gpshare.AsyncWebRequestExecutor;
import com.devtechdesign.gpshare.GPSBaseRequestListener;
import com.devtechdesign.gpshare.GPShareError;
import com.devtechdesign.gpshare.GpshareWeb;
import com.devtechdesign.gpshare.Util;
import com.devtechdesign.gpshare.utility.Utility;
import com.dtd.dbeagen.db.elements.aJournals;
import com.dtd.dbeagen.db.transactions.JournalsDBTransactions;
import com.dtd.dbeagen.web.JournalsWebUtil;

public class WebSyncUtil {

	private static String JOURNALS = "GetJournals";
	private static String PUT_JOURNALS = "PutJournals";
	private JournalsDBTransactions dbt;
	private ArrayList<aJournals> dirtyJournals;
	private int i = 0;
	private Context context;

	public WebSyncUtil(Context context) {
		this.context = context;
		this.dbt = new JournalsDBTransactions(context);

	}

	/**
	 * @param params
	 *            : Bundle: PERSON_ID, END_RANGE, START_RANGE
	 * @param listener
	 */
	public void getOnlineJournals(Bundle params, GPSBaseRequestListener listener) {

		Utility.gpsAyncRunner = new AsyncWebRequestExecutor(new GpshareWeb());
		Utility.gpsAyncRunner.request(JOURNALS, params, JOURNALS, listener, null);

	}

	/**
	 * @param params
	 *            : Bundle: JSON_STRING
	 * @param listener
	 */
	public void putOnlineJournals(Bundle params, GPSBaseRequestListener listener) {

		Utility.gpsAyncRunner = new AsyncWebRequestExecutor(new GpshareWeb());
		Utility.gpsAyncRunner.request(JOURNALS, params, PUT_JOURNALS, listener, null);

	}

	public void sync(ArrayList<aJournals> dirtyJournals) {
		this.dirtyJournals = dirtyJournals;
		uploadNext();
	}

	private void uploadNext() {
		if (i < dirtyJournals.size()) {
			Bundle params = new Bundle();
			params.putString("json", JournalsWebUtil.journalsToJson(dirtyJournals.get(i)).toString());
			putOnlineJournals(params, new SyncJournalsRquestListener());
		} else {
			Log.i("GPShare", "Journal  Syncing Complete: " + dbt.getDirtyJournals().size() + " dirty remaining in database");
		}
	}

	public class SyncJournalsRquestListener extends GPSBaseRequestListener {

		@Override
		public void onComplete(final String response, Object state) {

			JSONObject json;
			try {
				json = Util.parseJson(response);
				String JournalId = json.get("OnlineId").toString();
				if (JournalId != null && !JournalId.equals(""))
					dbt.syncIds(json.get("_id").toString(), JournalId);
				i++;
				uploadNext();
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (GPShareError e) {
				e.printStackTrace();
			}
		}
	}
}
