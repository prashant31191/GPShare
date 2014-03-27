package com.devtechdesign.gpshare.utility;

import android.content.Context;

import com.google.android.apps.analytics.GoogleAnalyticsTracker; 
import dev.tech.gpsharelibs.DTD;

public class GAnal {

	public GoogleAnalyticsTracker tracker;
	String personId = "";

	public GAnal(Context context) {

		try {
			personId = DTD.getPersonId();
		} catch (ClassCastException e) {

		}

		tracker = GoogleAnalyticsTracker.getInstance();
		tracker.startNewSession("UA-30319861-1", 20, context);
		tracker.dispatch();
	}

	public void recordClick(String event) {
		if (!personId.equals("192")) {
			tracker.trackEvent("Clicks", // Category
					event + " " + personId, // Action/
					"clicked", // Label
					1); // Value
		}
	}

	public void recordPageView(String event) {
		if (!personId.equals("192")) {
			tracker.trackEvent("PageLoad", // Category
					event + " " + personId, // Action/
					"Started", // Label
					1); // Value
		}
	}
}
