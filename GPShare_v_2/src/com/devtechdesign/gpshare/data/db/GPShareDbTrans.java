package com.devtechdesign.gpshare.data.db;

import java.util.ArrayList;
import java.util.LinkedList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.devtechdesign.gpshare.data.web.WebTrans;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.layouts.vwJournal;
import com.dtd.dbeagen.db.GPShareDatabase.Activity;
import com.dtd.dbeagen.db.GPShareDatabase.Journals;
import com.dtd.dbeagen.db.GPShareDatabase.MyPlaces;
import com.dtd.dbeagen.db.GPShareDatabase.PlaceMark;
import com.dtd.dbeagen.db.GPShareDatabase.Trip;
import com.dtd.dbeagen.db.GPShareDatabaseHelper;
import com.dtd.dbeagen.db.elements.aJournals;
import com.dtd.dbeagen.db.elements.aPlaceMark;
import com.dtd.dbeagen.db.elements.aTrip;

public class GPShareDbTrans extends GPShareDatabaseHelper {

	private String rowValues;

	public GPShareDbTrans(Context context) {
		super(context);
	}

	public ArrayList<vwJournal> getJournalsViaQuery() {

		Cursor mCursor = mDb.rawQuery(Sql.VW_JOURNALS, null);

		return getJournalDataFromCursor(mCursor);
	}

	public ArrayList<vwJournal> getJournalsViaQuery(String _id) {

		Cursor mCursor = mDb.rawQuery(Sql.vwGetJournalsViaQuery(_id), null);
		return getJournalDataFromCursor(mCursor);
	}

	public ArrayList<vwJournal> getJournalDataFromCursor(Cursor mCursor) {
		int count = mCursor.getCount();
		ArrayList<vwJournal> jouralList = new ArrayList<vwJournal>();

		if ((count > 0) && (mCursor.moveToFirst())) {

			for (int i = 0; i < count; i++) {
				vwJournal j = new vwJournal();
				j.activity.setAcivityName(mCursor.getString(mCursor.getColumnIndex(Activity.ACTIVITY_ACIVITYNAME)));
				j.journal.set_id(mCursor.getString(mCursor.getColumnIndex(Journals._ID)));
				j.journal.setActivityId(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_ACTIVITYID)));
				j.journal.setName(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_NAME)));
				j.journal.setCreateDate(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_CREATEDATE)));
				j.journal.setPlaceId(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_PLACEID)));
				j.journal.setText(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_TEXT)));
				j.journal.setTripId(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_TRIPID)));
				j.journal.setx(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_X)));
				j.journal.sety(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_Y)));
				j.activity.AcivityName = (mCursor.getString(mCursor.getColumnIndex(Activity.ACTIVITY_ACIVITYNAME)));
				j.place.PlaceName = (mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_PLACENAME)));
				j.trip.TripName = (mCursor.getString(mCursor.getColumnIndex(Trip.TRIP_TRIPNAME)));

				jouralList.add(j);
				mCursor.moveToNext();
			}
		}
		return jouralList;
	}

	public boolean syncWithOnlinePictures() {
		boolean response = false;
		ArrayList<aPlaceMark> imgBackups = WebTrans.getImageBackUps();

		for (aPlaceMark p : imgBackups) {
			DatabaseControl.getInstance().pmDbc.insertNewPlaceMark(p);
		}
		return response;
	}

	public LinkedList<aPlaceMark> getImages() {
		Cursor mCursor = mDb.query(PlaceMark.PLACEMARK_TABLE_NAME, null, null, null, null, null, PlaceMark._ID + " desc");
		ArrayList<aPlaceMark> imagesAry = DatabaseControl.getInstance().pmDbc.getPlaceMarkFromCursor(mCursor);
		LinkedList<aPlaceMark> imageList = new LinkedList<aPlaceMark>();
		for (aPlaceMark pm : imagesAry) {
			imageList.add(pm);
		}
		return imageList;
	}
}
