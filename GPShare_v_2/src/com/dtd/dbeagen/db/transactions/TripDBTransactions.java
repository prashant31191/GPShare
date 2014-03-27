package com.dtd.dbeagen.db.transactions;
import com.dtd.dbeagen.*;
import com.dtd.dbeagen.db.elements.*;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.dtd.dbeagen.db.GPShareDatabase.Trip;
import com.dtd.dbeagen.db.GPShareDatabaseHelper;public class TripDBTransactions extends GPShareDatabaseHelper {

	public TripDBTransactions(Context context) {
		super(context);
	}
/**
	 * @param new Trip
	 * @return
	 */public  long insertNewTrip(aTrip trip) {

		long rowId = mDb.insert(Trip.TRIP_TABLE_NAME , null, getContentValuesTrip(trip));
		return rowId;

}/**
	 * @param newTrip
	 */
	public  void updateTrip(aTrip newTrip) {

		ContentValues newTripContentValues = getContentValuesTrip(newTrip);
		mDb.update(Trip.TRIP_TABLE_NAME, newTripContentValues, Trip._ID + " = " + newTrip .get_id(), null);
	}

	/**
	 * @param TripId
	 */
	public  void deleteTripById(String TripId) {
		mDb.delete(Trip.TRIP_TABLE_NAME, Trip._ID + " = '" + TripId + "'", null);
	}/**
	 * @param TripId
	 * @return ArrayList<aTrip>
	 */
	public  ArrayList<aTrip> getLocalTripById(String TripId) {
		Cursor mCursor = mDb.query(Trip.TRIP_TABLE_NAME, null, Trip._ID + " = '" + TripId + "'", null, null, null, null);

		return getTripFromCursor(mCursor);
	}/**
	 * @return returns dirty trip for syncing to the server
	 */
	public ArrayList<aTrip> getDirtyTrip() {
		Cursor mCursor = mDb.query(Trip.TRIP_TABLE_NAME, null, Trip.TRIP_DIRTY + " = 'true'", null, null, null, null);
		return getTripFromCursor(mCursor);
	}/**
	 * @param syncs
	 *            Id in android database with the Id on the server.
	 *            online ID matches to the key column of the database what ever the name might be
    */	public void syncIds(String androidTripId, String onlineId) {
		ContentValues tripContent = new ContentValues();
		tripContent.put(Trip.TRIP_TRIPID, onlineId);
		tripContent.put(Trip.TRIP_DIRTY, "false");
		mDb.update(Trip.TRIP_TABLE_NAME, tripContent, Trip._ID + " = " + androidTripId, null);
	}public  ArrayList<aTrip> getLocalTrip() {
		Cursor mCursor = mDb.query(Trip.TRIP_TABLE_NAME, null, null, null, null, null, null);

		return getTripFromCursor(mCursor);
	}

public  ContentValues getContentValuesTrip(aTrip newtrip){
ContentValues TripToAdd = new ContentValues();
TripToAdd.put(Trip.TRIP_TRIPID, newtrip.getTripId());
TripToAdd.put(Trip.TRIP_TRIPNAME, newtrip.getTripName());
TripToAdd.put(Trip.TRIP_UPDATEDATE, newtrip.getUpdateDate());
TripToAdd.put(Trip.TRIP_CREATEDATE, newtrip.getCreateDate());
TripToAdd.put(Trip.TRIP_DIRTY, newtrip.getDirty());
return TripToAdd;

}
private  ArrayList<aTrip> getTripFromCursor(Cursor mCursor) {
int cursorCount = mCursor.getCount();
ArrayList<aTrip> TripList = new ArrayList<aTrip>();
	if (cursorCount > 0)
			if (mCursor.moveToFirst()) {

				for (int i = 0; i < cursorCount; i++) {
aTrip newTrip = new aTrip();
newTrip.setTripId(mCursor.getString(mCursor.getColumnIndex(Trip.TRIP_TRIPID)));
newTrip.setTripName(mCursor.getString(mCursor.getColumnIndex(Trip.TRIP_TRIPNAME)));
newTrip.setUpdateDate(mCursor.getString(mCursor.getColumnIndex(Trip.TRIP_UPDATEDATE)));
newTrip.setCreateDate(mCursor.getString(mCursor.getColumnIndex(Trip.TRIP_CREATEDATE)));
newTrip.setDirty(mCursor.getString(mCursor.getColumnIndex(Trip.TRIP_DIRTY)));
boolean dirty = getBoolean(mCursor.getColumnIndex(Trip.TRIP_DIRTY), mCursor);newTrip.setDirty(String.valueOf(dirty));TripList.add(newTrip);
					mCursor.moveToNext();}

			}

		return TripList;
	}
public  boolean getBoolean(int columnIndex, Cursor cursor) {
		if (cursor.isNull(columnIndex) || cursor.getShort(columnIndex) == 0) {
			return false;
		} else {
			return true;
		}
	}}
