package com.dtd.dbeagen.db.transactions;
import com.dtd.dbeagen.*;
import com.dtd.dbeagen.db.elements.*;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.dtd.dbeagen.db.GPShareDatabase.MyPlaces;
import com.dtd.dbeagen.db.GPShareDatabaseHelper;public class MyPlacesDBTransactions extends GPShareDatabaseHelper {

	public MyPlacesDBTransactions(Context context) {
		super(context);
	}
/**
	 * @param new MyPlaces
	 * @return
	 */public  long insertNewMyPlaces(aMyPlaces myplaces) {

		long rowId = mDb.insert(MyPlaces.MYPLACES_TABLE_NAME , null, getContentValuesMyPlaces(myplaces));
		return rowId;

}/**
	 * @param newMyPlaces
	 */
	public  void updateMyPlaces(aMyPlaces newMyPlaces) {

		ContentValues newMyPlacesContentValues = getContentValuesMyPlaces(newMyPlaces);
		mDb.update(MyPlaces.MYPLACES_TABLE_NAME, newMyPlacesContentValues, MyPlaces._ID + " = " + newMyPlaces .get_id(), null);
	}

	/**
	 * @param MyPlacesId
	 */
	public  void deleteMyPlacesById(String MyPlacesId) {
		mDb.delete(MyPlaces.MYPLACES_TABLE_NAME, MyPlaces._ID + " = '" + MyPlacesId + "'", null);
	}/**
	 * @param MyPlacesId
	 * @return ArrayList<aMyPlaces>
	 */
	public  ArrayList<aMyPlaces> getLocalMyPlacesById(String MyPlacesId) {
		Cursor mCursor = mDb.query(MyPlaces.MYPLACES_TABLE_NAME, null, MyPlaces._ID + " = '" + MyPlacesId + "'", null, null, null, null);

		return getMyPlacesFromCursor(mCursor);
	}/**
	 * @return returns dirty myplaces for syncing to the server
	 */
	public ArrayList<aMyPlaces> getDirtyMyPlaces() {
		Cursor mCursor = mDb.query(MyPlaces.MYPLACES_TABLE_NAME, null, MyPlaces.MYPLACES_DIRTY + " = 'true'", null, null, null, null);
		return getMyPlacesFromCursor(mCursor);
	}/**
	 * @param syncs
	 *            Id in android database with the Id on the server.
	 *            online ID matches to the key column of the database what ever the name might be
    */	public void syncIds(String androidMyPlacesId, String onlineId) {
		ContentValues myplacesContent = new ContentValues();
		myplacesContent.put(MyPlaces.MYPLACES_PLACEID, onlineId);
		myplacesContent.put(MyPlaces.MYPLACES_DIRTY, "false");
		mDb.update(MyPlaces.MYPLACES_TABLE_NAME, myplacesContent, MyPlaces._ID + " = " + androidMyPlacesId, null);
	}public  ArrayList<aMyPlaces> getLocalMyPlaces() {
		Cursor mCursor = mDb.query(MyPlaces.MYPLACES_TABLE_NAME, null, null, null, null, null, null);

		return getMyPlacesFromCursor(mCursor);
	}

public  ContentValues getContentValuesMyPlaces(aMyPlaces newmyplaces){
ContentValues MyPlacesToAdd = new ContentValues();
MyPlacesToAdd.put(MyPlaces.MYPLACES_PLACEID, newmyplaces.getPlaceId());
MyPlacesToAdd.put(MyPlaces.MYPLACES_PERSONID, newmyplaces.getPersonId());
MyPlacesToAdd.put(MyPlaces.MYPLACES_USERNAME, newmyplaces.getUserName());
MyPlacesToAdd.put(MyPlaces.MYPLACES_PLACENAME, newmyplaces.getPlaceName());
MyPlacesToAdd.put(MyPlaces.MYPLACES_CREATEDATE, newmyplaces.getCreateDate());
MyPlacesToAdd.put(MyPlaces.MYPLACES_UPDATEDATE, newmyplaces.getUpdateDate());
MyPlacesToAdd.put(MyPlaces.MYPLACES_STATEID, newmyplaces.getStateId());
MyPlacesToAdd.put(MyPlaces.MYPLACES_COUNTRYID, newmyplaces.getCountryId());
MyPlacesToAdd.put(MyPlaces.MYPLACES_DATE, newmyplaces.getDate());
MyPlacesToAdd.put(MyPlaces.MYPLACES_IMGURL, newmyplaces.getImgUrl());
MyPlacesToAdd.put(MyPlaces.MYPLACES_X, newmyplaces.getx());
MyPlacesToAdd.put(MyPlaces.MYPLACES_Y, newmyplaces.gety());
MyPlacesToAdd.put(MyPlaces.MYPLACES_CAPTION, newmyplaces.getCaption());
MyPlacesToAdd.put(MyPlaces.MYPLACES_DIRTY, newmyplaces.getDirty());
return MyPlacesToAdd;

}
private  ArrayList<aMyPlaces> getMyPlacesFromCursor(Cursor mCursor) {
int cursorCount = mCursor.getCount();
ArrayList<aMyPlaces> MyPlacesList = new ArrayList<aMyPlaces>();
	if (cursorCount > 0)
			if (mCursor.moveToFirst()) {

				for (int i = 0; i < cursorCount; i++) {
aMyPlaces newMyPlaces = new aMyPlaces();
newMyPlaces.setPlaceId(mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_PLACEID)));
newMyPlaces.setPersonId(mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_PERSONID)));
newMyPlaces.setUserName(mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_USERNAME)));
newMyPlaces.setPlaceName(mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_PLACENAME)));
newMyPlaces.setCreateDate(mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_CREATEDATE)));
newMyPlaces.setUpdateDate(mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_UPDATEDATE)));
newMyPlaces.setStateId(mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_STATEID)));
newMyPlaces.setCountryId(mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_COUNTRYID)));
newMyPlaces.setDate(mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_DATE)));
newMyPlaces.setImgUrl(mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_IMGURL)));
newMyPlaces.setx(mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_X)));
newMyPlaces.sety(mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_Y)));
newMyPlaces.setCaption(mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_CAPTION)));
newMyPlaces.setDirty(mCursor.getString(mCursor.getColumnIndex(MyPlaces.MYPLACES_DIRTY)));
boolean dirty = getBoolean(mCursor.getColumnIndex(MyPlaces.MYPLACES_DIRTY), mCursor);newMyPlaces.setDirty(String.valueOf(dirty));MyPlacesList.add(newMyPlaces);
					mCursor.moveToNext();}

			}

		return MyPlacesList;
	}
public  boolean getBoolean(int columnIndex, Cursor cursor) {
		if (cursor.isNull(columnIndex) || cursor.getShort(columnIndex) == 0) {
			return false;
		} else {
			return true;
		}
	}}
