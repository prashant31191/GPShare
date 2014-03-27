package com.devtechdesign.gpshare.layouts;

import com.dtd.dbeagen.db.elements.aActivity;
import com.dtd.dbeagen.db.elements.aJournals;
import com.dtd.dbeagen.db.elements.aMyPlaces;
import com.dtd.dbeagen.db.elements.aTrip;

public class vwJournal {

	public String placeName;
	public String tripName;
	public String activityName;
	public aJournals journal;
	public vwJournal vwJournal;
	public aActivity activity;
	public aMyPlaces place;
	public aTrip trip;

	public vwJournal() {
		activity = new aActivity();
		place = new aMyPlaces();
		trip = new aTrip();
		journal = new aJournals();
	}
}
