package com.dtd.dbeagen.web;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dtd.dbeagen.db.elements.aPlaceMark;

public class PlaceMarkWebUtil {

	private static String PLACEMARK = "GetPlaceMark";

	public static ArrayList<aPlaceMark> jsonToPlaceMark(JSONObject json) {
		ArrayList<aPlaceMark> aPlaceMarkList = new ArrayList<aPlaceMark>();
		JSONArray placemarkAry;
		try {
			placemarkAry = json.getJSONArray(PLACEMARK + "Result");
			aPlaceMark placemark = new aPlaceMark();
			for (int i = 0; i < placemarkAry.length(); i++) {
				Object ob = placemarkAry.get(i);
				placemark.setIdentKey(((JSONObject) ob).getString("IdentKey"));
				placemark.setUserName(((JSONObject) ob).getString("UserName"));
				placemark.setPlaceId(((JSONObject) ob).getString("PlaceId"));
				placemark.setPlaceMarkType(((JSONObject) ob).getString("PlaceMarkType"));
				placemark.setLongitude(((JSONObject) ob).getString("Longitude"));
				placemark.setLatitude(((JSONObject) ob).getString("Latitude"));
				placemark.setx(((JSONObject) ob).getString("x"));
				placemark.sety(((JSONObject) ob).getString("y"));
				placemark.setSubtitle(((JSONObject) ob).getString("Subtitle"));
				placemark.setMyPlace(((JSONObject) ob).getString("MyPlace"));
				placemark.setpicUrl(((JSONObject) ob).getString("picUrl"));
				placemark.setAlbumID(((JSONObject) ob).getString("AlbumID"));
				placemark.setVideoID(((JSONObject) ob).getString("VideoID"));
				placemark.setCreateDate(((JSONObject) ob).getString("CreateDate"));
				placemark.setUpdateDate(((JSONObject) ob).getString("UpdateDate"));
				placemark.setPlaceName(((JSONObject) ob).getString("PlaceName"));
				placemark.setPersonId(((JSONObject) ob).getString("PersonId"));
				placemark.setFacebookUpload(((JSONObject) ob).getString("FacebookUpload"));
				placemark.setLargeUrl(((JSONObject) ob).getString("LargeUrl"));
				placemark.set_id(((JSONObject) ob).getString("_id"));
				placemark.setdirty(((JSONObject) ob).getString("dirty"));
				aPlaceMarkList.add(placemark);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return aPlaceMarkList;
	}

	private static String nullCheck(String string) {
		if (string == null)
			return "0";
		return string;
	}

	public static JSONObject placemarkToJson(aPlaceMark PlaceMark) {

		JSONObject jsonPlaceMark = new JSONObject();
		try {
			jsonPlaceMark.put("IdentKey", PlaceMark.getIdentKey());
			jsonPlaceMark.put("UserName", PlaceMark.getUserName());
			jsonPlaceMark.put("PlaceId", PlaceMark.getPlaceId());
			jsonPlaceMark.put("PlaceMarkType", PlaceMark.getPlaceMarkType());
			jsonPlaceMark.put("Longitude", PlaceMark.getLongitude());
			jsonPlaceMark.put("Latitude", PlaceMark.getLatitude());
			jsonPlaceMark.put("x", PlaceMark.getx());
			jsonPlaceMark.put("y", PlaceMark.gety());
			jsonPlaceMark.put("Subtitle", PlaceMark.getSubtitle());
			jsonPlaceMark.put("MyPlace", PlaceMark.getMyPlace());
			jsonPlaceMark.put("picUrl", PlaceMark.getpicUrl());
			jsonPlaceMark.put("AlbumID", PlaceMark.getAlbumID());
			jsonPlaceMark.put("VideoID", PlaceMark.getVideoID());
			jsonPlaceMark.put("CreateDate", PlaceMark.getCreateDate());
			jsonPlaceMark.put("UpdateDate", PlaceMark.getUpdateDate());
			jsonPlaceMark.put("PlaceName", PlaceMark.getPlaceName());
			jsonPlaceMark.put("PersonId", PlaceMark.getPersonId());
			jsonPlaceMark.put("FacebookUpload", PlaceMark.getFacebookUpload());
			jsonPlaceMark.put("LargeUrl", PlaceMark.getLargeUrl());
			jsonPlaceMark.put("_id", nullCheck(PlaceMark.get_id()));
			jsonPlaceMark.put("dirty", PlaceMark.getdirty());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonPlaceMark;
	}
}