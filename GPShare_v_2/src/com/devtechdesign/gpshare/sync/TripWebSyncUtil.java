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
import com.dtd.dbeagen.db.elements.aTrip;
import com.dtd.dbeagen.db.transactions.TripDBTransactions;
import com.dtd.dbeagen.web.TripWebUtil;

public class TripWebSyncUtil {

	private static String Trip = "GetTrip";
	private static String PUT_Trip = "PutTrip";
	private TripDBTransactions dbt;
	private ArrayList<aTrip> dirtyTrip;
	private int i = 0;
	private Context context;

	public TripWebSyncUtil(Context context) {
		this.context = context;
		this.dbt = new TripDBTransactions(context);

	}

	/**
	 * @param params
	 *            : Bundle: PERSON_ID, END_RANGE, START_RANGE
	 * @param listener
	 */
	public void getOnlineTrip(Bundle params, GPSBaseRequestListener listener) {

		Utility.gpsAyncRunner = new AsyncWebRequestExecutor(new GpshareWeb());
		Utility.gpsAyncRunner.request(Trip, params, Trip, listener, null);

	}

	/**
	 * @param params
	 *            : Bundle: JSON_STRING
	 * @param listener
	 */
	public void putOnlineTrip(Bundle params, GPSBaseRequestListener listener) {

		Utility.gpsAyncRunner = new AsyncWebRequestExecutor(new GpshareWeb());
		Utility.gpsAyncRunner.request(Trip, params, PUT_Trip, listener, null);

	}

	public void sync(ArrayList<aTrip> dirtyTrip) {
		this.dirtyTrip = dirtyTrip;
		uploadNext();
	}

	private void uploadNext() {
		if (i < dirtyTrip.size()) {
			Bundle params = new Bundle();
			params.putString("json", TripWebUtil.tripToJson(dirtyTrip.get(i)).toString());
			putOnlineTrip(params, new SyncTripRquestListener());
		} else {
			Log.i("GPShare", "Journal  Syncing Complete: " + dbt.getDirtyTrip().size() + " dirty remaining in database");
		}
	}

	public class SyncTripRquestListener extends GPSBaseRequestListener {

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
