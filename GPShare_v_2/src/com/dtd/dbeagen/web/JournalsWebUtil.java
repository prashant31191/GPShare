package com.dtd.dbeagen.web;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dtd.dbeagen.db.elements.aJournals;

public class JournalsWebUtil {

	private static String JOURNALS = "GetJournals";

	public static ArrayList<aJournals> jsonToJournals(JSONObject json) {
		ArrayList<aJournals> aJournalsList = new ArrayList<aJournals>();
		JSONArray journalsAry;
		try {
			journalsAry = json.getJSONArray(JOURNALS+"Result");
			aJournals journals = new aJournals();
		for (int i = 0; i < journalsAry.length(); i++) {
				Object ob = journalsAry.get(i);
	journals.setJournalId(((JSONObject) ob).getString("JournalId"));
	journals.setActivityId(((JSONObject) ob).getString("ActivityId"));
	journals.setName(((JSONObject) ob).getString("Name"));
	journals.setCreateDate(((JSONObject) ob).getString("CreateDate"));
	journals.setUpdateDate(((JSONObject) ob).getString("UpdateDate"));
	journals.setPlaceId(((JSONObject) ob).getString("PlaceId"));
	journals.setText(((JSONObject) ob).getString("Text"));
	journals.setTripId(((JSONObject) ob).getString("TripId"));
	journals.setx(((JSONObject) ob).getString("x"));
	journals.sety(((JSONObject) ob).getString("y"));
	journals.setDirty(((JSONObject) ob).getString("Dirty"));
	journals.setPersonId(((JSONObject) ob).getString("PersonId"));
	aJournalsList.add(journals);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return aJournalsList;
	}

 	private static String nullCheck(String string) {
		if (string == null)
			return "0";
		return string;
	}


public static JSONObject journalsToJson(aJournals Journals) {

		JSONObject jsonJournals = new JSONObject();
		try {jsonJournals.put("JournalId",Journals.getJournalId());
	jsonJournals.put("ActivityId",Journals.getActivityId());
	jsonJournals.put("Name",Journals.getName());
	jsonJournals.put("CreateDate",Journals.getCreateDate());
	jsonJournals.put("UpdateDate",Journals.getUpdateDate());
	jsonJournals.put("PlaceId",Journals.getPlaceId());
	jsonJournals.put("Text",Journals.getText());
	jsonJournals.put("TripId",Journals.getTripId());
	jsonJournals.put("x",Journals.getx());
	jsonJournals.put("y",Journals.gety());
	jsonJournals.put("Dirty",Journals.getDirty());
	jsonJournals.put("PersonId",Journals.getPersonId());
	} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonJournals;
	}

}