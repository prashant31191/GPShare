package com.devtechdesign.gpshare.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.data.db.RouteDatabase.*;

public class RouteDataBaseHelper extends SQLiteOpenHelper {

	private static final String			DATABASE_NAME		= "place_mark.db";

	// Verison 33 05/07/2012
	private static final int			DATABASE_VERSION	= 60;
	public static RouteDataBaseHelper	DBHelper;
	public static SQLiteDatabase		mDb;

	public RouteDataBaseHelper(Context context) {
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
			db.execSQL("CREATE TABLE " + Path.PathS_TABLE_NAME + " (" + Path._ID + " INTEGER, " + Path.Path_PLACE_ID + " TEXT, "
					+ Path.Path_ID + " TEXT, " + Path.Path_NAME + " TEXT, " + Path.Path_KEY + " TEXT, " + Path.Path_X + " TEXT, "
					+ Path.Path_Y + " TEXT, " + Path.Path_DATETIME + " TEXT, " + Path.Path_SPEED + " TEXT, " + Path.Path_ALTITUDE
					+ " TEXT, " + " PRIMARY KEY(" + Path.Path_KEY + "));");

		} catch (SQLiteException e) {
			System.out.println("xxxDatebase Exception Caught: " + e.getMessage());
		}

		try {
			db.execSQL("CREATE TABLE " + AllRoutes.ALLROUTESS_TABLE_NAME + " (" + AllRoutes._ID + " INTEGER, " + AllRoutes.ALLROUTES_PLACE
					+ " TEXT, " + AllRoutes.ALLROUTES_ID + " TEXT, " + AllRoutes.ALLROUTES_NAME + " TEXT, " + AllRoutes.ALLROUTES_KEY
					+ " TEXT, " + AllRoutes.ALLROUTES_X + " TEXT, " + AllRoutes.ALLROUTES_Y + " TEXT, " + AllRoutes.ALLROUTES_XYString
					+ " TEXT, " + AllRoutes.ALLROUTES_CREATE_DATE + " TEXT, " + AllRoutes.ALLROUTES_Uploaded + " TEXT, "

					+ " PRIMARY KEY(" + AllRoutes.ALLROUTES_KEY + "));");

		} catch (SQLiteException e) {
			e.printStackTrace();

		}

		try {
			db.execSQL("CREATE TABLE " + Routes.ROUTES_TABLE_NAME + " (" + Routes._ID + " INTEGER, " + Routes.ROUTE_PLACE_ID + " TEXT, "
					+ Routes.ROUTE_FILE_PATH + " TEXT, " + Routes.ROUTE_ID + " TEXT, " + Routes.ROUTE_NAME + " TEXT, " + Routes.ROUTE_KEY
					+ " TEXT, " + Routes.ROUTE_X + " TEXT, " + Routes.ROUTE_Y + " TEXT, " + " PRIMARY KEY(" + Routes.ROUTE_KEY + "));");

		} catch (SQLiteException e) {
			System.out.println("xxxDatebase Exception Caught: " + e.getMessage());
		}

		try {
			db.execSQL("CREATE TABLE " + Images.IMAGE_TABLE_NAME + " (" + Images._ID + " INTEGER, " + Images.IMAGE_ID + " TEXT, "
					+ Images.IMAGE_PATH + " TEXT, " + Images.IMAGE_PLACE_NAME + " TEXT, " + Images.IMAGE_CREATEDATE + " TEXT, "
					+ Images.IMAGE_X + " TEXT, " + Images.IMAGE_Y + " TEXT, " + " PRIMARY KEY(" + Images.IMAGE_CREATEDATE + "));");

		} catch (SQLiteException e) {
			System.out.println("xxxDatebase Exception Caught: " + e.getMessage());
		}

		try {
			db.execSQL("CREATE TABLE " + User_Settings.USER_SETTINGS_TABLE_NAME + " (" + User_Settings._ID + " INTEGER, "
					+ User_Settings.USER_SETTINGS_IDENT + " INTEGER, " + User_Settings.USER_SETTINGS_TRACKING_ON + " BOOLEAN, "
					+ " PRIMARY KEY(" + User_Settings.USER_SETTINGS_IDENT + "));");

		} catch (SQLiteException e) {
			System.out.println("xxxDatebase Exception Caught: " + e.getMessage());
		}

		System.out.println("XXXXXXXXXXXXXXXXXXXXdatabase helperHit222 !!!!!!!!!!!!!!!!!!!!!!!");

		try {
			// create places table
			db.execSQL("CREATE TABLE " + Places.PLACES_TABLE_NAME + " (" + Places._ID + " INTEGER, " + Places.PLACES_ID + " INTEGER, "
					+ Places.PLACES_NAME + " TEXT, " + Places.PLACES_CURRENT_PLACE + " BIT," + " PRIMARY KEY(" + Places.PLACES_NAME + "));");

			System.out.println("XXXXXXXXXXXXXXXXXXXXdatabase helper: " + Places.PLACES_TABLE_NAME + "!!!!!!!!!!!!!!!!!!!!!!!");
		} catch (SQLiteException e) {
			System.out.println("xxxDatebase Exception Caught: " + e.getMessage());
		}

		// //////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////
		try {// 03/26/2012 places_route_count
			db.execSQL("ALTER TABLE " + Places.PLACES_TABLE_NAME + " ADD COLUMN " + Places.PLACES_Route_Count + " INTEGER");
		} catch (SQLiteException e) {
			System.out.println("xxxDatabase Column Already Altererd: " + e.getMessage());
		}
		// //////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////
		try {// 02/28/2012
			db.execSQL("ALTER TABLE " + AllRoutes.ALLROUTESS_TABLE_NAME + " ADD COLUMN " + AllRoutes.ALLROUTES_TaggerUserName + " TEXT");
		} catch (SQLiteException e) {
			System.out.println("xxxDatabase Column Already Altererd: " + e.getMessage());
		}

		// //////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////
		try {// 03/27/2012
			db.execSQL("ALTER TABLE " + AllRoutes.ALLROUTESS_TABLE_NAME + " ADD COLUMN " + AllRoutes.ALLROUTES_ElapsedTime + " INTEGER");
		} catch (SQLiteException e) {
			System.out.println("xxxDatabase Column Already Altererd: " + e.getMessage());
		}

		// //////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////
		try {// 03/20/2012
			db.execSQL("ALTER TABLE " + AllRoutes.ALLROUTESS_TABLE_NAME + " ADD COLUMN " + AllRoutes.ALLROUTES_startDateTime + " TEXT");
			db.execSQL("ALTER TABLE " + AllRoutes.ALLROUTESS_TABLE_NAME + " ADD COLUMN " + AllRoutes.ALLROUTES_endDateTime + " TEXT");
			db.execSQL("ALTER TABLE " + AllRoutes.ALLROUTESS_TABLE_NAME + " ADD COLUMN " + AllRoutes.ALLROUTES_distance + " TEXT");
			db.execSQL("ALTER TABLE " + AllRoutes.ALLROUTESS_TABLE_NAME + " ADD COLUMN " + AllRoutes.ALLROUTES_pointCount + " TEXT");
			db.execSQL("ALTER TABLE " + AllRoutes.ALLROUTESS_TABLE_NAME + " ADD COLUMN " + AllRoutes.ALLROUTES_topSpeed + " TEXT");
			db.execSQL("ALTER TABLE " + AllRoutes.ALLROUTESS_TABLE_NAME + " ADD COLUMN " + AllRoutes.ALLROUTES_totalRise + " TEXT");
			db.execSQL("ALTER TABLE " + AllRoutes.ALLROUTESS_TABLE_NAME + " ADD COLUMN " + AllRoutes.ALLROUTES_totalFall + " TEXT");

		} catch (SQLiteException e) {
			System.out.println("xxxDatabase Column Already Altererd: " + e.getMessage());
		}

		// //////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////
		try {// 03/21/2012
			db.execSQL("ALTER TABLE " + AllRoutes.ALLROUTESS_TABLE_NAME + " ADD COLUMN " + AllRoutes.ALLROUTES_currentTrackBool + " TEXT");
		} catch (SQLiteException e) {

		}

		// //////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////
		try {// 10/28/2012
			db.execSQL("ALTER TABLE " + AllRoutes.ALLROUTESS_TABLE_NAME + " ADD COLUMN " + AllRoutes.ALLROUTES_SegmentKey + " TEXT");

		} catch (SQLiteException e) {
		}

		// //////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////
		try {// 04/07/2013
			db.execSQL("ALTER TABLE " + Images.IMAGE_TABLE_NAME + " ADD COLUMN " + Images.IMAGE_EARTH_GALLERY + " BOOLEAN");
		} catch (SQLiteException e) {
		}

		try {
			db.execSQL("CREATE TABLE " + Journals.JOURNAL_TABLE + " (" + Journals._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
					+ Journals.JOURNAL_ONLINE_ID + " INTEGER" + Journals.JOURNAL_ACTIVITY_ID + " TEXT, " + Journals.JOURNAL_NAME
					+ " TEXT, " + Journals.JOURNAL_CREATE_DATE + " TEXT, " + Journals.JOURNAL_PLACE_ID + " TEXT, " + Journals.JOURNAL_TEXT
					+ " TEXT, " + Journals.JOURNAL_TRIP_ID + " TEXT, " + Journals.JOURNAL_UPDATE_DATE + " TEXT, " + Journals.JOURNAL_DIRTY
					+ " BOOLEAN, " + Journals.JOURNAL_X + " TEXT, " + Journals.JOURNAL_Y + " TEXT);");
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		// try {
		// db.execSQL("CREATE TABLE " + aTrip.A_TRIP_TABLE + " (" + aTrip._ID +
		// " INTEGER PRIMARY KEY AUTOINCREMENT, "
		// + Journals.JOURNAL_ONLINE_ID + " INTEGER" + aTrip.A_TRIP_CREATE_DATE
		// + " TEXT, " + aTrip.A_TRIP_DIRTY + " BOOLEAN, "
		// + aTrip.A_TRIP_ONLINE_ID + " INTEGER, " + aTrip.a + " TEXT, " +
		// aTrip.JOURNAL_TEXT + " TEXT, " + aTrip.JOURNAL_TRIP_ID
		// + " TEXT, " + aTrip.JOURNAL_UPDATE_DATE + " TEXT, " +
		// aTrip.JOURNAL_DIRTY + " BOOLEAN, " + aTrip.JOURNAL_X + " TEXT, "
		// + Journals.JOURNAL_Y + " TEXT);");
		// } catch (SQLiteException e) {
		// e.printStackTrace();
		// }

		// //////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////
		try {// 12/24/2013
			db.execSQL("ALTER TABLE " + Journals.JOURNAL_TABLE + " ADD COLUMN " + Journals.JOURNAL_ACTIVITY_ID + " TEXT");
		} catch (SQLiteException e) {
			e.printStackTrace();
		}

		// //////////////////////////////////////////////////////
		// //////////////////////////////////////////////////////
		try {// 12/26/2013
			db.execSQL("ALTER TABLE " + Journals.JOURNAL_TABLE + " ADD COLUMN " + Journals.JOURNAL_ID + " TEXT");
		} catch (SQLiteException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

		onCreate(db);
		Globals.setnewVersion("true");

	}
}
