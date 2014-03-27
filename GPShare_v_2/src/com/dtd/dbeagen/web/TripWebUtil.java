package com.dtd.dbeagen.web;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dtd.dbeagen.db.elements.aTrip;

public class TripWebUtil {

	private static String TRIP = "GetTrip";

	public static ArrayList<aTrip> jsonToTrip(JSONObject json) {
		ArrayList<aTrip> aTripList = new ArrayList<aTrip>();
		JSONArray tripAry;
		try {
			tripAry = json.getJSONArray(TRIP+"Result");
			aTrip trip = new aTrip();
		for (int i = 0; i < tripAry.length(); i++) {
				Object ob = tripAry.get(i);
	trip.setTripId(((JSONObject) ob).getString("TripId"));
	trip.setTripName(((JSONObject) ob).getString("TripName"));
	trip.setUpdateDate(((JSONObject) ob).getString("UpdateDate"));
	trip.setCreateDate(((JSONObject) ob).getString("CreateDate"));
	trip.setDirty(((JSONObject) ob).getString("Dirty"));
	aTripList.add(trip);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return aTripList;
	}

 	private static String nullCheck(String string) {
		if (string == null)
			return "0";
		return string;
	}


public static JSONObject tripToJson(aTrip Trip) {

		JSONObject jsonTrip = new JSONObject();
		try {jsonTrip.put("TripId",Trip.getTripId());
	jsonTrip.put("TripName",Trip.getTripName());
	jsonTrip.put("UpdateDate",Trip.getUpdateDate());
	jsonTrip.put("CreateDate",Trip.getCreateDate());
	jsonTrip.put("Dirty",Trip.getDirty());
	} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonTrip;
	}

}