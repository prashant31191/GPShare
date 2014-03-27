package com.dtd.dbeagen.web;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dtd.dbeagen.db.elements.aMyPlaces;

public class MyPlacesWebUtil {

	private static String MYPLACES = "GetMyPlaces";

	public static ArrayList<aMyPlaces> jsonToMyPlaces(JSONObject json) {
		ArrayList<aMyPlaces> aMyPlacesList = new ArrayList<aMyPlaces>();
		JSONArray myplacesAry;
		try {
			myplacesAry = json.getJSONArray(MYPLACES+"Result");
			aMyPlaces myplaces = new aMyPlaces();
		for (int i = 0; i < myplacesAry.length(); i++) {
				Object ob = myplacesAry.get(i);
	myplaces.setPlaceId(((JSONObject) ob).getString("PlaceId"));
	myplaces.setPersonId(((JSONObject) ob).getString("PersonId"));
	myplaces.setUserName(((JSONObject) ob).getString("UserName"));
	myplaces.setPlaceName(((JSONObject) ob).getString("PlaceName"));
	myplaces.setCreateDate(((JSONObject) ob).getString("CreateDate"));
	myplaces.setUpdateDate(((JSONObject) ob).getString("UpdateDate"));
	myplaces.setStateId(((JSONObject) ob).getString("StateId"));
	myplaces.setCountryId(((JSONObject) ob).getString("CountryId"));
	myplaces.setDate(((JSONObject) ob).getString("Date"));
	myplaces.setImgUrl(((JSONObject) ob).getString("ImgUrl"));
	myplaces.setx(((JSONObject) ob).getString("x"));
	myplaces.sety(((JSONObject) ob).getString("y"));
	myplaces.setCaption(((JSONObject) ob).getString("Caption"));
	myplaces.setDirty(((JSONObject) ob).getString("Dirty"));
	aMyPlacesList.add(myplaces);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return aMyPlacesList;
	}

 	private static String nullCheck(String string) {
		if (string == null)
			return "0";
		return string;
	}


public static JSONObject myplacesToJson(aMyPlaces MyPlaces) {

		JSONObject jsonMyPlaces = new JSONObject();
		try {jsonMyPlaces.put("PlaceId",MyPlaces.getPlaceId());
	jsonMyPlaces.put("PersonId",MyPlaces.getPersonId());
	jsonMyPlaces.put("UserName",MyPlaces.getUserName());
	jsonMyPlaces.put("PlaceName",MyPlaces.getPlaceName());
	jsonMyPlaces.put("CreateDate",MyPlaces.getCreateDate());
	jsonMyPlaces.put("UpdateDate",MyPlaces.getUpdateDate());
	jsonMyPlaces.put("StateId",MyPlaces.getStateId());
	jsonMyPlaces.put("CountryId",MyPlaces.getCountryId());
	jsonMyPlaces.put("Date",MyPlaces.getDate());
	jsonMyPlaces.put("ImgUrl",MyPlaces.getImgUrl());
	jsonMyPlaces.put("x",MyPlaces.getx());
	jsonMyPlaces.put("y",MyPlaces.gety());
	jsonMyPlaces.put("Caption",MyPlaces.getCaption());
	jsonMyPlaces.put("Dirty",MyPlaces.getDirty());
	} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonMyPlaces;
	}

}