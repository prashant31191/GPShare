package com.dtd.dbeagen.db.transactions;
import com.dtd.dbeagen.*;
import com.dtd.dbeagen.db.elements.*;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.dtd.dbeagen.db.GPShareDatabase.Activity;
import com.dtd.dbeagen.db.GPShareDatabaseHelper;public class ActivityDBTransactions extends GPShareDatabaseHelper {

	public ActivityDBTransactions(Context context) {
		super(context);
	}
/**
	 * @param new Activity
	 * @return
	 */public  long insertNewActivity(aActivity activity) {

		long rowId = mDb.insert(Activity.ACTIVITY_TABLE_NAME , null, getContentValuesActivity(activity));
		return rowId;

}/**
	 * @param newActivity
	 */
	public  void updateActivity(aActivity newActivity) {

		ContentValues newActivityContentValues = getContentValuesActivity(newActivity);
		mDb.update(Activity.ACTIVITY_TABLE_NAME, newActivityContentValues, Activity._ID + " = " + newActivity .get_id(), null);
	}

	/**
	 * @param ActivityId
	 */
	public  void deleteActivityById(String ActivityId) {
		mDb.delete(Activity.ACTIVITY_TABLE_NAME, Activity._ID + " = '" + ActivityId + "'", null);
	}/**
	 * @param ActivityId
	 * @return ArrayList<aActivity>
	 */
	public  ArrayList<aActivity> getLocalActivityById(String ActivityId) {
		Cursor mCursor = mDb.query(Activity.ACTIVITY_TABLE_NAME, null, Activity._ID + " = '" + ActivityId + "'", null, null, null, null);

		return getActivityFromCursor(mCursor);
	}/**
	 * @return returns dirty activity for syncing to the server
	 */
	public ArrayList<aActivity> getDirtyActivity() {
		Cursor mCursor = mDb.query(Activity.ACTIVITY_TABLE_NAME, null, Activity.ACTIVITY_DIRTY + " = 'true'", null, null, null, null);
		return getActivityFromCursor(mCursor);
	}/**
	 * @param syncs
	 *            Id in android database with the Id on the server.
	 *            online ID matches to the key column of the database what ever the name might be
    */	public void syncIds(String androidActivityId, String onlineId) {
		ContentValues activityContent = new ContentValues();
		activityContent.put(Activity.ACTIVITY_ACITVITYID, onlineId);
		activityContent.put(Activity.ACTIVITY_DIRTY, "false");
		mDb.update(Activity.ACTIVITY_TABLE_NAME, activityContent, Activity._ID + " = " + androidActivityId, null);
	}public  ArrayList<aActivity> getLocalActivity() {
		Cursor mCursor = mDb.query(Activity.ACTIVITY_TABLE_NAME, null, null, null, null, null, null);

		return getActivityFromCursor(mCursor);
	}

public  ContentValues getContentValuesActivity(aActivity newactivity){
ContentValues ActivityToAdd = new ContentValues();
ActivityToAdd.put(Activity.ACTIVITY_ACITVITYID, newactivity.getAcitvityId());
ActivityToAdd.put(Activity.ACTIVITY_ACIVITYNAME, newactivity.getAcivityName());
ActivityToAdd.put(Activity.ACTIVITY_CREATEDATE, newactivity.getCreateDate());
ActivityToAdd.put(Activity.ACTIVITY_UPDATEDATE, newactivity.getUpdateDate());
ActivityToAdd.put(Activity.ACTIVITY_DIRTY, newactivity.getDirty());
return ActivityToAdd;

}
private  ArrayList<aActivity> getActivityFromCursor(Cursor mCursor) {
int cursorCount = mCursor.getCount();
ArrayList<aActivity> ActivityList = new ArrayList<aActivity>();
	if (cursorCount > 0)
			if (mCursor.moveToFirst()) {

				for (int i = 0; i < cursorCount; i++) {
aActivity newActivity = new aActivity();
newActivity.setAcitvityId(mCursor.getString(mCursor.getColumnIndex(Activity.ACTIVITY_ACITVITYID)));
newActivity.setAcivityName(mCursor.getString(mCursor.getColumnIndex(Activity.ACTIVITY_ACIVITYNAME)));
newActivity.setCreateDate(mCursor.getString(mCursor.getColumnIndex(Activity.ACTIVITY_CREATEDATE)));
newActivity.setUpdateDate(mCursor.getString(mCursor.getColumnIndex(Activity.ACTIVITY_UPDATEDATE)));
newActivity.setDirty(mCursor.getString(mCursor.getColumnIndex(Activity.ACTIVITY_DIRTY)));
boolean dirty = getBoolean(mCursor.getColumnIndex(Activity.ACTIVITY_DIRTY), mCursor);newActivity.setDirty(String.valueOf(dirty));ActivityList.add(newActivity);
					mCursor.moveToNext();}

			}

		return ActivityList;
	}
public  boolean getBoolean(int columnIndex, Cursor cursor) {
		if (cursor.isNull(columnIndex) || cursor.getShort(columnIndex) == 0) {
			return false;
		} else {
			return true;
		}
	}}
