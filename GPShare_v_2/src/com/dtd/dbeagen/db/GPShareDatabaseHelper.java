package com.dtd.dbeagen.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.dtd.dbeagen.db.GPShareDatabase.*;
import com.dtd.dbeagen.db.GPShareDatabaseHelper;

public class GPShareDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "GPShare.db";

	// Verison 33 05/07/2012
	private static final int DATABASE_VERSION = 5;
	public GPShareDatabaseHelper DBHelper;
	public SQLiteDatabase mDb;

	public GPShareDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);

		createDBInstance(context);
	}

	public void createDBInstance(Context pContext) {
		if (DBHelper == null) {// This will be your DB Handler Class
			// m_ObjDataBase = DBHelper.openAndCreateDataBase(); // Initialze
			// the DB Note: openAndCreateDataBase is a utility method created by
			// you to do everything an return the DB object
			mDb = getWritableDatabase();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {

		try {
			db.execSQL("CREATE TABLE " + Activity.ACTIVITY_TABLE_NAME + " (" + Activity._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + Activity.ACTIVITY_ACITVITYID
					+ " TEXT ," + Activity.ACTIVITY_ACIVITYNAME + " TEXT ," + Activity.ACTIVITY_CREATEDATE + " TEXT ," + Activity.ACTIVITY_UPDATEDATE + " TEXT ,"
					+ Activity.ACTIVITY_DIRTY + " TEXT );");
		} catch (SQLiteException e) {
			e.printStackTrace();

		}
		try {
			db.execSQL("CREATE TABLE " + Journals.JOURNALS_TABLE_NAME + " (" + Journals._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + Journals.JOURNALS_JOURNALID
					+ " TEXT ," + Journals.JOURNALS_ACTIVITYID + " TEXT ," + Journals.JOURNALS_NAME + " TEXT ," + Journals.JOURNALS_CREATEDATE + " TEXT ,"
					+ Journals.JOURNALS_UPDATEDATE + " TEXT ," + Journals.JOURNALS_PLACEID + " TEXT ," + Journals.JOURNALS_TEXT + " TEXT ," + Journals.JOURNALS_TRIPID + " TEXT ,"
					+ Journals.JOURNALS_X + " TEXT ," + Journals.JOURNALS_Y + " TEXT ," + Journals.JOURNALS_DIRTY + " TEXT ," + Journals.JOURNALS_PERSONID + " TEXT );");
		} catch (SQLiteException e) {
			e.printStackTrace();

		}
		try {
			db.execSQL("CREATE TABLE " + MyPlaces.MYPLACES_TABLE_NAME + " (" + MyPlaces._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + MyPlaces.MYPLACES_PLACEID
					+ " TEXT ," + MyPlaces.MYPLACES_PERSONID + " TEXT ," + MyPlaces.MYPLACES_USERNAME + " TEXT ," + MyPlaces.MYPLACES_PLACENAME + " TEXT ,"
					+ MyPlaces.MYPLACES_CREATEDATE + " TEXT ," + MyPlaces.MYPLACES_UPDATEDATE + " TEXT ," + MyPlaces.MYPLACES_STATEID + " TEXT ," + MyPlaces.MYPLACES_COUNTRYID
					+ " TEXT ," + MyPlaces.MYPLACES_DATE + " TEXT ," + MyPlaces.MYPLACES_IMGURL + " TEXT ," + MyPlaces.MYPLACES_X + " TEXT ," + MyPlaces.MYPLACES_Y + " TEXT ,"
					+ MyPlaces.MYPLACES_CAPTION + " TEXT ," + MyPlaces.MYPLACES_DIRTY + " TEXT );");
		} catch (SQLiteException e) {
			e.printStackTrace();

		}
		try {
			db.execSQL("CREATE TABLE " + PlaceMark.PLACEMARK_TABLE_NAME + " (" + PlaceMark._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + PlaceMark.PLACEMARK_IDENTKEY
					+ " TEXT ," + PlaceMark.PLACEMARK_USERNAME + " TEXT ," + PlaceMark.PLACEMARK_PLACEID + " TEXT ," + PlaceMark.PLACEMARK_PLACEMARKTYPE + " TEXT ,"
					+ PlaceMark.PLACEMARK_LONGITUDE + " TEXT ," + PlaceMark.PLACEMARK_LATITUDE + " TEXT ," + PlaceMark.PLACEMARK_X + " TEXT ," + PlaceMark.PLACEMARK_Y + " TEXT ,"
					+ PlaceMark.PLACEMARK_SUBTITLE + " TEXT ," + PlaceMark.PLACEMARK_MYPLACE + " TEXT ," + PlaceMark.PLACEMARK_PICURL + " TEXT ," + PlaceMark.PLACEMARK_ALBUMID
					+ " TEXT ," + PlaceMark.PLACEMARK_VIDEOID + " TEXT ," + PlaceMark.PLACEMARK_CREATEDATE + " TEXT ," + PlaceMark.PLACEMARK_UPDATEDATE + " TEXT ,"
					+ PlaceMark.PLACEMARK_PLACENAME + " TEXT ," + PlaceMark.PLACEMARK_PERSONID + " TEXT ," + PlaceMark.PLACEMARK_FACEBOOKUPLOAD + " TEXT ,"
					+ PlaceMark.PLACEMARK_LARGEURL + " TEXT ," + PlaceMark.PLACEMARK_DIRTY + " TEXT );");
		} catch (SQLiteException e) {
			e.printStackTrace();

		}
		try {
			db.execSQL("CREATE TABLE " + Trip.TRIP_TABLE_NAME + " (" + Trip._ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " + Trip.TRIP_TRIPID + " TEXT ,"
					+ Trip.TRIP_TRIPNAME + " TEXT ," + Trip.TRIP_UPDATEDATE + " TEXT ," + Trip.TRIP_CREATEDATE + " TEXT ," + Trip.TRIP_DIRTY + " TEXT );");
		} catch (SQLiteException e) {
			e.printStackTrace();

		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onCreate(db);

	}
}