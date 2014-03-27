package com.dtd.dbeagen.db.transactions;
import com.dtd.dbeagen.*;
import com.dtd.dbeagen.db.elements.*;
import java.util.ArrayList;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import com.dtd.dbeagen.db.GPShareDatabase.Journals;
import com.dtd.dbeagen.db.GPShareDatabaseHelper;public class JournalsDBTransactions extends GPShareDatabaseHelper {

	public JournalsDBTransactions(Context context) {
		super(context);
	}
/**
	 * @param new Journals
	 * @return
	 */public  long insertNewJournals(aJournals journals) {

		long rowId = mDb.insert(Journals.JOURNALS_TABLE_NAME , null, getContentValuesJournals(journals));
		return rowId;

}/**
	 * @param newJournals
	 */
	public  void updateJournals(aJournals newJournals) {

		ContentValues newJournalsContentValues = getContentValuesJournals(newJournals);
		mDb.update(Journals.JOURNALS_TABLE_NAME, newJournalsContentValues, Journals._ID + " = " + newJournals .get_id(), null);
	}

	/**
	 * @param JournalsId
	 */
	public  void deleteJournalsById(String JournalsId) {
		mDb.delete(Journals.JOURNALS_TABLE_NAME, Journals._ID + " = '" + JournalsId + "'", null);
	}/**
	 * @param JournalsId
	 * @return ArrayList<aJournals>
	 */
	public  ArrayList<aJournals> getLocalJournalsById(String JournalsId) {
		Cursor mCursor = mDb.query(Journals.JOURNALS_TABLE_NAME, null, Journals._ID + " = '" + JournalsId + "'", null, null, null, null);

		return getJournalsFromCursor(mCursor);
	}/**
	 * @return returns dirty journals for syncing to the server
	 */
	public ArrayList<aJournals> getDirtyJournals() {
		Cursor mCursor = mDb.query(Journals.JOURNALS_TABLE_NAME, null, Journals.JOURNALS_DIRTY + " = 'true'", null, null, null, null);
		return getJournalsFromCursor(mCursor);
	}/**
	 * @param syncs
	 *            Id in android database with the Id on the server.
	 *            online ID matches to the key column of the database what ever the name might be
    */	public void syncIds(String androidJournalsId, String onlineId) {
		ContentValues journalsContent = new ContentValues();
		journalsContent.put(Journals.JOURNALS_JOURNALID, onlineId);
		journalsContent.put(Journals.JOURNALS_DIRTY, "false");
		mDb.update(Journals.JOURNALS_TABLE_NAME, journalsContent, Journals._ID + " = " + androidJournalsId, null);
	}public  ArrayList<aJournals> getLocalJournals() {
		Cursor mCursor = mDb.query(Journals.JOURNALS_TABLE_NAME, null, null, null, null, null, null);

		return getJournalsFromCursor(mCursor);
	}

public  ContentValues getContentValuesJournals(aJournals newjournals){
ContentValues JournalsToAdd = new ContentValues();
JournalsToAdd.put(Journals.JOURNALS_JOURNALID, newjournals.getJournalId());
JournalsToAdd.put(Journals.JOURNALS_ACTIVITYID, newjournals.getActivityId());
JournalsToAdd.put(Journals.JOURNALS_NAME, newjournals.getName());
JournalsToAdd.put(Journals.JOURNALS_CREATEDATE, newjournals.getCreateDate());
JournalsToAdd.put(Journals.JOURNALS_UPDATEDATE, newjournals.getUpdateDate());
JournalsToAdd.put(Journals.JOURNALS_PLACEID, newjournals.getPlaceId());
JournalsToAdd.put(Journals.JOURNALS_TEXT, newjournals.getText());
JournalsToAdd.put(Journals.JOURNALS_TRIPID, newjournals.getTripId());
JournalsToAdd.put(Journals.JOURNALS_X, newjournals.getx());
JournalsToAdd.put(Journals.JOURNALS_Y, newjournals.gety());
JournalsToAdd.put(Journals.JOURNALS_DIRTY, newjournals.getDirty());
JournalsToAdd.put(Journals.JOURNALS_PERSONID, newjournals.getPersonId());
return JournalsToAdd;

}
private  ArrayList<aJournals> getJournalsFromCursor(Cursor mCursor) {
int cursorCount = mCursor.getCount();
ArrayList<aJournals> JournalsList = new ArrayList<aJournals>();
	if (cursorCount > 0)
			if (mCursor.moveToFirst()) {

				for (int i = 0; i < cursorCount; i++) {
aJournals newJournals = new aJournals();
newJournals.setJournalId(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_JOURNALID)));
newJournals.setActivityId(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_ACTIVITYID)));
newJournals.setName(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_NAME)));
newJournals.setCreateDate(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_CREATEDATE)));
newJournals.setUpdateDate(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_UPDATEDATE)));
newJournals.setPlaceId(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_PLACEID)));
newJournals.setText(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_TEXT)));
newJournals.setTripId(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_TRIPID)));
newJournals.setx(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_X)));
newJournals.sety(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_Y)));
newJournals.setDirty(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_DIRTY)));
boolean dirty = getBoolean(mCursor.getColumnIndex(Journals.JOURNALS_DIRTY), mCursor);newJournals.setDirty(String.valueOf(dirty));newJournals.setPersonId(mCursor.getString(mCursor.getColumnIndex(Journals.JOURNALS_PERSONID)));
JournalsList.add(newJournals);
					mCursor.moveToNext();}

			}

		return JournalsList;
	}
public  boolean getBoolean(int columnIndex, Cursor cursor) {
		if (cursor.isNull(columnIndex) || cursor.getShort(columnIndex) == 0) {
			return false;
		} else {
			return true;
		}
	}}
