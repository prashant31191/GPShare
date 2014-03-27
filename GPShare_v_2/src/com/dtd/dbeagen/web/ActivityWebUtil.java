package com.dtd.dbeagen.web;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.dtd.dbeagen.db.elements.aActivity;

public class ActivityWebUtil {

	private static String ACTIVITY = "GetActivity";

	public static ArrayList<aActivity> jsonToActivity(JSONObject json) {
		ArrayList<aActivity> aActivityList = new ArrayList<aActivity>();
		JSONArray activityAry;
		try {
			activityAry = json.getJSONArray(ACTIVITY+"Result");
			aActivity activity = new aActivity();
		for (int i = 0; i < activityAry.length(); i++) {
				Object ob = activityAry.get(i);
	activity.setAcitvityId(((JSONObject) ob).getString("AcitvityId"));
	activity.setAcivityName(((JSONObject) ob).getString("AcivityName"));
	activity.setCreateDate(((JSONObject) ob).getString("CreateDate"));
	activity.setUpdateDate(((JSONObject) ob).getString("UpdateDate"));
	activity.setDirty(((JSONObject) ob).getString("Dirty"));
	aActivityList.add(activity);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return aActivityList;
	}

 	private static String nullCheck(String string) {
		if (string == null)
			return "0";
		return string;
	}


public static JSONObject activityToJson(aActivity Activity) {

		JSONObject jsonActivity = new JSONObject();
		try {jsonActivity.put("AcitvityId",Activity.getAcitvityId());
	jsonActivity.put("AcivityName",Activity.getAcivityName());
	jsonActivity.put("CreateDate",Activity.getCreateDate());
	jsonActivity.put("UpdateDate",Activity.getUpdateDate());
	jsonActivity.put("Dirty",Activity.getDirty());
	} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonActivity;
	}

}