package com.devtechdesign.gpshare.utility;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import dev.tech.gpsharelibs.IDevTech;

@SuppressLint("CommitPrefEdits")
public class GPsharePrefs {

	private SharedPreferences	sharedPreferences;
	private Editor				editor;

	public GPsharePrefs(Context context, String prefs) {
		sharedPreferences = context.getSharedPreferences(prefs, 0);
		editor = sharedPreferences.edit();
	}

	@SuppressLint("CommitPrefEdits")
	public String LoadPreferences(String key) {
		String value = sharedPreferences.getString(key, "");
		return value;
	}

	public void savePreferences(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}
}
