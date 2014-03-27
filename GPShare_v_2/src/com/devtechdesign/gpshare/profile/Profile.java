package com.devtechdesign.gpshare.profile;

import android.content.Context;
import android.database.Cursor;

import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.data.db.RouteDataBaseHelper;
import com.devtechdesign.gpshare.data.db.RouteDatabase;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.utility.SoapInterface;

public class Profile extends RouteDataBaseHelper implements SoapInterface {
	public aProfile userProfile;

	public Profile(Context context) {
		super(context);

		userProfile = new aProfile();

		getProfileStats();

	}

	public void getProfileStats() {
		Cursor cursor = mDb.rawQuery(RouteDatabase.AllRoutes.PROFILE_STATS, null);
		if (cursor.getCount() > 0) {
			cursor.moveToFirst();

			userProfile.setTotalDistance(String.valueOf(Transactions.convertMetersPrefUom(Double.valueOf(cursor.getString(1)), GPShare.getUOM())));

			userProfile.setTotalPlaces(cursor.getString(2));
			userProfile.setTotalPics(cursor.getString(3));
			userProfile.setTotalRoutes(cursor.getString(4));
		}
	}

	public class aProfile {

		private String totalDistance;
		private String totalPlaces;
		private String totalPics;
		private String totalRoutes;
		private String totalFriends;

		public String getTotalDistance() {
			return totalDistance;
		}

		public void setTotalDistance(String totalDistance) {
			this.totalDistance = totalDistance;
		}

		public String getTotalPlaces() {
			return totalPlaces;
		}

		public void setTotalPlaces(String totalPlaces) {
			this.totalPlaces = totalPlaces;
		}

		public String getTotalPics() {
			return totalPics;
		}

		public void setTotalPics(String totalPics) {
			this.totalPics = totalPics;
		}

		public String getTotalRoutes() {
			return totalRoutes;
		}

		public void setTotalRoutes(String totalRoutes) {
			this.totalRoutes = totalRoutes;
		}

		public String getTotalFriends() {
			return totalFriends;
		}

		public void setTotalFriends(String totalFriends) {
			this.totalFriends = totalFriends;
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}
}
