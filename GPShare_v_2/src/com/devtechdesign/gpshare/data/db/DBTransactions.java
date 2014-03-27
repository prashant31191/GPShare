package com.devtechdesign.gpshare.data.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.devtechdesign.gpshare.data.db.RouteDatabase.AllRoutes;
import com.devtechdesign.gpshare.data.db.RouteDatabase.Places;
import com.devtechdesign.gpshare.elements.aPlace;
import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.utility.SoapInterface;

public class DBTransactions extends RouteDataBaseHelper implements SoapInterface {

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

	static Transactions TransactionSet;
	private Context context;

	public DBTransactions(Context context) {
		super(context);
		this.context = context;
		TransactionSet = new Transactions(context);
	}

	public static void insertNewPlace(aPlace placeOb) {

		ContentValues routeRecordToAdd = new ContentValues();
		routeRecordToAdd.put(Places.PLACES_ID, placeOb.getPlaceId());
		routeRecordToAdd.put("places_name", placeOb.getPlaceName());
		routeRecordToAdd.put("places_current_place", true);
		mDb.insert(Places.PLACES_TABLE_NAME, null, routeRecordToAdd);
	}

	public static void updateRoute(aRoute cRoute) {
		ContentValues routeRecordToUpdate = new ContentValues();
		routeRecordToUpdate.put(RouteDatabase.AllRoutes.ALLROUTES_NAME, cRoute.getRouteName());
		routeRecordToUpdate.put(RouteDatabase.AllRoutes.ALLROUTES_PLACE, cRoute.getPlaceName());
		mDb.update(RouteDatabase.AllRoutes.ALLROUTESS_TABLE_NAME, routeRecordToUpdate, RouteDatabase.AllRoutes.ALLROUTES_KEY + " =?", new String[] { cRoute.getRouteKey() });
	}

	public static void deleteRoute(String routeKey) {
		mDb.delete(RouteDatabase.AllRoutes.ALLROUTESS_TABLE_NAME, RouteDatabase.AllRoutes.ALLROUTES_KEY + " = '" + routeKey + "'", null);
	}

	public static boolean UpdateRouteNullKeyCheck() {
		boolean response = false;

		try {

			Cursor mCursor = mDb.query(RouteDatabase.AllRoutes.ALLROUTESS_TABLE_NAME, new String[] { AllRoutes.ALLROUTES_SegmentKey, AllRoutes.ALLROUTES_KEY },
					AllRoutes.ALLROUTES_SegmentKey + " is null", null, null, null, null);

			int routeCount = mCursor.getCount();
			if (routeCount > 0)
				response = true;
			else
				response = false;

			if ((routeCount > 0) && (mCursor.moveToFirst())) {
				// code for binding all of the coordinates to a listview

				for (int i = 0; i < routeCount; i++) {
					System.out.println("SelectRouteNullKeyCheck: mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_KEY)); "
							+ mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_KEY)));

					System.out.println("SelectRouteNullKeyCheck: mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_SegmentKey)); "
							+ mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_SegmentKey)));
					UpdateNullSegmentKey(mCursor.getString(mCursor.getColumnIndex(AllRoutes.ALLROUTES_KEY)));
					mCursor.moveToNext();
				}
			}
			mCursor.close();
		} catch (NullPointerException e) {

		}
		return response;
	}

	public static void UpdateNullSegmentKey(String newSegKeyValue) {
		ContentValues args = new ContentValues();
		args.put(AllRoutes.ALLROUTES_SegmentKey, newSegKeyValue);
		mDb.update(AllRoutes.ALLROUTESS_TABLE_NAME, args, AllRoutes.ALLROUTES_KEY + " = '" + newSegKeyValue + "'", null);

	}
}
