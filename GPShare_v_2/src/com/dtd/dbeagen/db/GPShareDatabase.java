package com.dtd.dbeagen.db;

import android.provider.BaseColumns;

public final class GPShareDatabase {
	private GPShareDatabase() {
	}

	public final class Activity implements BaseColumns {

		public Activity() {
		}

		public static final String ACTIVITY_TABLE_NAME = "activity_table_name";
		public static final String ACTIVITY_ACITVITYID = "acitvityid";
		public static final String ACTIVITY_ACIVITYNAME = "acivityname";
		public static final String ACTIVITY_CREATEDATE = "createdate";
		public static final String ACTIVITY_UPDATEDATE = "updatedate";
		public static final String ACTIVITY_DIRTY = "dirty";
	}

	public final class Journals implements BaseColumns {

		public Journals() {
		}

		public static final String JOURNALS_TABLE_NAME = "journals_table_name";
		public static final String JOURNALS_JOURNALID = "journalid";
		public static final String JOURNALS_ACTIVITYID = "activityid";
		public static final String JOURNALS_NAME = "name";
		public static final String JOURNALS_CREATEDATE = "createdate";
		public static final String JOURNALS_UPDATEDATE = "updatedate";
		public static final String JOURNALS_PLACEID = "placeid";
		public static final String JOURNALS_TEXT = "text";
		public static final String JOURNALS_TRIPID = "tripid";
		public static final String JOURNALS_X = "x";
		public static final String JOURNALS_Y = "y";
		public static final String JOURNALS_DIRTY = "dirty";
		public static final String JOURNALS_PERSONID = "personid";
	}

	public final class MyPlaces implements BaseColumns {

		public MyPlaces() {
		}

		public static final String MYPLACES_TABLE_NAME = "myplaces_table_name";
		public static final String MYPLACES_PLACEID = "placeid";
		public static final String MYPLACES_PERSONID = "personid";
		public static final String MYPLACES_USERNAME = "username";
		public static final String MYPLACES_PLACENAME = "placename";
		public static final String MYPLACES_CREATEDATE = "createdate";
		public static final String MYPLACES_UPDATEDATE = "updatedate";
		public static final String MYPLACES_STATEID = "stateid";
		public static final String MYPLACES_COUNTRYID = "countryid";
		public static final String MYPLACES_DATE = "date";
		public static final String MYPLACES_IMGURL = "imgurl";
		public static final String MYPLACES_X = "x";
		public static final String MYPLACES_Y = "y";
		public static final String MYPLACES_CAPTION = "caption";
		public static final String MYPLACES_DIRTY = "dirty";
	}

	public final class PlaceMark implements BaseColumns {

		public PlaceMark() {
		}

		public static final String PLACEMARK_TABLE_NAME = "placemark_table_name";
		public static final String PLACEMARK_IDENTKEY = "identkey";
		public static final String PLACEMARK_USERNAME = "username";
		public static final String PLACEMARK_PLACEID = "placeid";
		public static final String PLACEMARK_PLACEMARKTYPE = "placemarktype";
		public static final String PLACEMARK_LONGITUDE = "longitude";
		public static final String PLACEMARK_LATITUDE = "latitude";
		public static final String PLACEMARK_X = "x";
		public static final String PLACEMARK_Y = "y";
		public static final String PLACEMARK_SUBTITLE = "subtitle";
		public static final String PLACEMARK_MYPLACE = "myplace";
		public static final String PLACEMARK_PICURL = "picurl";
		public static final String PLACEMARK_ALBUMID = "albumid";
		public static final String PLACEMARK_VIDEOID = "videoid";
		public static final String PLACEMARK_CREATEDATE = "createdate";
		public static final String PLACEMARK_UPDATEDATE = "updatedate";
		public static final String PLACEMARK_PLACENAME = "placename";
		public static final String PLACEMARK_PERSONID = "personid";
		public static final String PLACEMARK_FACEBOOKUPLOAD = "facebookupload";
		public static final String PLACEMARK_LARGEURL = "largeurl";
		public static final String PLACEMARK__ID = "_id";
		public static final String PLACEMARK_DIRTY = "dirty";
	}

	public final class Trip implements BaseColumns {

		public Trip() {
		}

		public static final String TRIP_TABLE_NAME = "trip_table_name";
		public static final String TRIP_TRIPID = "tripid";
		public static final String TRIP_TRIPNAME = "tripname";
		public static final String TRIP_UPDATEDATE = "updatedate";
		public static final String TRIP_CREATEDATE = "createdate";
		public static final String TRIP_DIRTY = "dirty";
	}

}