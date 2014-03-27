package com.dtd.dbeagen.db.transactions;

import com.dtd.dbeagen.*;
import com.dtd.dbeagen.db.elements.*;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.dtd.dbeagen.db.GPShareDatabase.PlaceMark;
import com.dtd.dbeagen.db.GPShareDatabaseHelper;

public class PlaceMarkDBTransactions extends GPShareDatabaseHelper {

	public PlaceMarkDBTransactions(Context context) {
		super(context);
	}

	/**
	 * @param new PlaceMark
	 * @return
	 */
	public long insertNewPlaceMark(aPlaceMark placemark) {

		long rowId = mDb.insert(PlaceMark.PLACEMARK_TABLE_NAME, null, getContentValuesPlaceMark(placemark));
		return rowId;

	}

	/**
	 * @param newPlaceMark
	 */
	public void updatePlaceMark(aPlaceMark newPlaceMark) {

		ContentValues newPlaceMarkContentValues = getContentValuesPlaceMark(newPlaceMark);
		mDb.update(PlaceMark.PLACEMARK_TABLE_NAME, newPlaceMarkContentValues, PlaceMark._ID + " = " + newPlaceMark.get_id(), null);
	}

	/**
	 * @param PlaceMarkId
	 */
	public void deletePlaceMarkById(String PlaceMarkId) {
		mDb.delete(PlaceMark.PLACEMARK_TABLE_NAME, PlaceMark._ID + " = '" + PlaceMarkId + "'", null);
	}

	/**
	 * @param PlaceMarkId
	 * @return ArrayList<aPlaceMark>
	 */
	public ArrayList<aPlaceMark> getLocalPlaceMarkById(String PlaceMarkId) {
		Cursor mCursor = mDb.query(PlaceMark.PLACEMARK_TABLE_NAME, null, PlaceMark._ID + " = '" + PlaceMarkId + "'", null, null, null, null);

		return getPlaceMarkFromCursor(mCursor);
	}

	/**
	 * @return returns dirty placemark for syncing to the server
	 */
	public ArrayList<aPlaceMark> getDirtyPlaceMark() {
		Cursor mCursor = mDb.query(PlaceMark.PLACEMARK_TABLE_NAME, null, PlaceMark.PLACEMARK_DIRTY + " = 'true'", null, null, null, null);
		return getPlaceMarkFromCursor(mCursor);
	}

	/**
	 * @param syncs
	 *            Id in android database with the Id on the server. online ID
	 *            matches to the key column of the database what ever the name
	 *            might be
	 */
	public void syncIds(String androidPlaceMarkId, String onlineId) {
		ContentValues placemarkContent = new ContentValues();
		placemarkContent.put(PlaceMark.PLACEMARK_IDENTKEY, onlineId);
		placemarkContent.put(PlaceMark.PLACEMARK_DIRTY, "false");
		mDb.update(PlaceMark.PLACEMARK_TABLE_NAME, placemarkContent, PlaceMark._ID + " = " + androidPlaceMarkId, null);
	}

	public ArrayList<aPlaceMark> getLocalPlaceMark() {
		Cursor mCursor = mDb.query(PlaceMark.PLACEMARK_TABLE_NAME, null, null, null, null, null, null);

		return getPlaceMarkFromCursor(mCursor);
	}

	public ContentValues getContentValuesPlaceMark(aPlaceMark newplacemark) {
		ContentValues PlaceMarkToAdd = new ContentValues();
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_IDENTKEY, newplacemark.getIdentKey());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_USERNAME, newplacemark.getUserName());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_PLACEID, newplacemark.getPlaceId());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_PLACEMARKTYPE, newplacemark.getPlaceMarkType());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_LONGITUDE, newplacemark.getLongitude());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_LATITUDE, newplacemark.getLatitude());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_X, newplacemark.getx());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_Y, newplacemark.gety());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_SUBTITLE, newplacemark.getSubtitle());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_MYPLACE, newplacemark.getMyPlace());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_PICURL, newplacemark.getpicUrl());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_ALBUMID, newplacemark.getAlbumID());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_VIDEOID, newplacemark.getVideoID());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_CREATEDATE, newplacemark.getCreateDate());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_UPDATEDATE, newplacemark.getUpdateDate());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_PLACENAME, newplacemark.getPlaceName());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_PERSONID, newplacemark.getPersonId());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_FACEBOOKUPLOAD, newplacemark.getFacebookUpload());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_LARGEURL, newplacemark.getLargeUrl());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK__ID, newplacemark.get_id());
		PlaceMarkToAdd.put(PlaceMark.PLACEMARK_DIRTY, newplacemark.getdirty());
		return PlaceMarkToAdd;

	}

	public ArrayList<aPlaceMark> getPlaceMarkFromCursor(Cursor mCursor) {
		int cursorCount = mCursor.getCount();
		ArrayList<aPlaceMark> PlaceMarkList = new ArrayList<aPlaceMark>();
		if (cursorCount > 0)
			if (mCursor.moveToFirst()) {

				for (int i = 0; i < cursorCount; i++) {
					aPlaceMark newPlaceMark = new aPlaceMark();
					newPlaceMark.setIdentKey(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_IDENTKEY)));
					newPlaceMark.setUserName(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_USERNAME)));
					newPlaceMark.setPlaceId(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_PLACEID)));
					newPlaceMark.setPlaceMarkType(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_PLACEMARKTYPE)));
					newPlaceMark.setLongitude(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_LONGITUDE)));
					newPlaceMark.setLatitude(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_LATITUDE)));
					newPlaceMark.setx(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_X)));
					newPlaceMark.sety(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_Y)));
					newPlaceMark.setSubtitle(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_SUBTITLE)));
					newPlaceMark.setMyPlace(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_MYPLACE)));
					newPlaceMark.setpicUrl(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_PICURL)));
					newPlaceMark.setAlbumID(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_ALBUMID)));
					newPlaceMark.setVideoID(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_VIDEOID)));
					newPlaceMark.setCreateDate(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_CREATEDATE)));
					newPlaceMark.setUpdateDate(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_UPDATEDATE)));
					newPlaceMark.setPlaceName(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_PLACENAME)));
					newPlaceMark.setPersonId(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_PERSONID)));
					newPlaceMark.setFacebookUpload(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_FACEBOOKUPLOAD)));
					newPlaceMark.setLargeUrl(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_LARGEURL)));
					newPlaceMark.set_id(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK__ID)));
					newPlaceMark.setdirty(mCursor.getString(mCursor.getColumnIndex(PlaceMark.PLACEMARK_DIRTY)));
					PlaceMarkList.add(newPlaceMark);
					mCursor.moveToNext();
				}

			}

		return PlaceMarkList;
	}

	public boolean getBoolean(int columnIndex, Cursor cursor) {
		if (cursor.isNull(columnIndex) || cursor.getShort(columnIndex) == 0) {
			return false;
		} else {
			return true;
		}
	}
}
