package com.devtechdesign.gpshare.data.web;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.os.Bundle;
import com.devtechdesign.gpshare.AsyncWebRequestExecutor;
import com.devtechdesign.gpshare.GPSBaseRequestListener;
import com.devtechdesign.gpshare.GpshareWeb;
import com.devtechdesign.gpshare.utility.Utility;
import com.dtd.dbeagen.db.elements.aJournals;

public class JournalWebTransactions {

	private static String JOURNALS = "GetJournals";
	private static String PUT_JOURNALS = "PutJournals";

	@SuppressWarnings("unused")
	public static ArrayList<aJournals> jsonToJournals(JSONObject json) {
		ArrayList<aJournals> journals = new ArrayList<aJournals>();
		JSONArray journalsAry;
		try {
			journalsAry = json.getJSONArray(JOURNALS + "Result");
			aJournals journal = new aJournals();
			for (int i = 0; i < journalsAry.length(); i++) {
				Object routeOb = journalsAry.get(i);
				journal.setOnline_id(((JSONObject) routeOb).getString("JournalId"));
				journal.setActivityId(((JSONObject) routeOb).getString("ActivityId"));
				journal.setName(((JSONObject) routeOb).getString("Name"));
				journal.setCreateDate(((JSONObject) routeOb).getString("CreateDate"));
				journal.setUpdateDate(((JSONObject) routeOb).getString("UpdateDate"));
				journal.setPlaceId(((JSONObject) routeOb).getString("PlaceId"));
				journal.setPlaceId(((JSONObject) routeOb).getString("TripId"));
				journal.setx(((JSONObject) routeOb).getString("x"));
				journal.sety(((JSONObject) routeOb).getString("y"));
				journal.setDirty(((JSONObject) routeOb).getString("Dirty"));
				journals.add(journal);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return journals;
	}

	private static String nullCheck(String string) {
		if (string == null)
			return "0";
		return string;
	}

	@SuppressWarnings("unused")
	public static JSONObject journalsToJson(aJournals j) {

		JSONObject journal = new JSONObject();
		try {
			journal.put("_id", nullCheck(j.get_id()));
			journal.put("Online_id", nullCheck(j.getOnline_id()));
			journal.put("Text", j.getText());
			journal.put("JournalId", j.getJournalId());
			journal.put("ActivityId", j.getActivityId());
			journal.put("Name", j.getName());
			journal.put("CreateDate", "2014-01-18 14:59:49.930");
			journal.put("UpdateDate", "2014-01-18 14:59:49.930");
			journal.put("PlaceId", j.getPlaceId());
			journal.put("Text", j.getText());
			journal.put("x", j.getx());
			journal.put("y", j.gety());
			journal.put("Dirty", j.getDirty());
			journal.put("PersonId", j.getPersonId());
			journal.put("TripId", j.getTripId());

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return journal;
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
}
