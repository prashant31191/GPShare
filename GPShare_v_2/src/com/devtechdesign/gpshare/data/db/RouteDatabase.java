package com.devtechdesign.gpshare.data.db;

import android.provider.BaseColumns;

public final class RouteDatabase {

	private RouteDatabase() {
	}

	public final class Routes implements BaseColumns {

		private Routes() {
		}

		public static final String	ROUTE_NAME			= "route_name";
		public static final String	ROUTE_KEY			= "route_key";
		public static final String	ROUTE_PLACE_ID		= "route_place_id";
		public static final String	ROUTES_TABLE_NAME	= "table_routes";
		public static final String	ROUTE_X				= "route_x";
		public static final String	ROUTE_Y				= "route_y";
		public static final String	ROUTE_ID			= "route_id";
		public static final String	ROUTE_FILE_PATH		= "route_file_path";

	}

	public final class Tags implements BaseColumns {

		private Tags() {
		}

		public static final String	Tags_Id					= "tag_id";
		public static final String	Tags_tagger_userName	= "tag_tagger_username";

	}

	public final class AllRoutes implements BaseColumns {

		private AllRoutes() {
		}

		public static final String	ALLROUTES_ACTIVITY			= "allroutes_activity";
		public static final String	ALLROUTES_NAME				= "allroutes_name";
		public static final String	ALLROUTES_PLACE				= "allroutes_place";
		public static final String	ALLROUTES_KEY				= "allroutes_key";
		public static final String	ALLROUTESS_TABLE_NAME		= "table_allroutes";
		public static final String	ALLROUTES_X					= "allroutes_x";
		public static final String	ALLROUTES_Y					= "allroutes_y";
		public static final String	ALLROUTES_ID				= "allroutes_id";
		public static final String	ALLROUTES_CREATE_DATE		= "allroutes_createDate";
		// allroutes_xyString is a comma delimited string containing the
		// following fields x,y,datetime,elevation,speed
		public static final String	ALLROUTES_XYString			= "allroutes_xy_string";
		public static final String	ALLROUTES_Uploaded			= "allroutes_uploaded";
		public static final String	ALLROUTES_startDateTime		= "allroutes_startDateTime";
		public static final String	ALLROUTES_endDateTime		= "allroutes_endDateTime";
		public static final String	ALLROUTES_distance			= "allroutes_distance";
		public static final String	ALLROUTES_pointCount		= "allroutes_pointCount";
		public static final String	ALLROUTES_topSpeed			= "allroutes_topSpeed";
		public static final String	ALLROUTES_totalRise			= "allroutes_totalRise";
		public static final String	ALLROUTES_totalFall			= "allroutes_totalFall";
		public static final String	ALLROUTES_currentTrackBool	= "allroutes_currentTrackBool";
		public static final String	ALLROUTES_ElapsedTime		= "allroutes_elapsedTime";
		public static final String	DISTANCE_SUM				= "totalDistance";
		public static final String	PLACE_COUNT					= "totalPlaces";
		public static final String	IMAGE_COUNT					= "imageCount";
		public static final String	ROUTE_COUNT					= "routeCount";

		// Columns Added Post Production
		public static final String	ALLROUTES_TaggerUserName	= "allroutes_taggerUsername";
		public static final String	ALLROUTES_SegmentKey		= "allroutes_segmentKey";

		// views
		public static final String	COUNT_ROUTES				= "(select Distinct count(*) from " + ALLROUTESS_TABLE_NAME + " where "
																		+ ALLROUTES_distance + " <> '') as " + ROUTE_COUNT;

		public static final String	SUM_ROUTE_DISTANCE			= "(select sum(" + ALLROUTES_distance + ") from " + ALLROUTESS_TABLE_NAME
																		+ ") as " + DISTANCE_SUM;

		public static final String	COUNT_PLACES				= "(select count(" + Places.PLACES_NAME + ") from "
																		+ Places.PLACES_TABLE_NAME + ") as " + PLACE_COUNT;

		public static final String	COUNT_IMAGES				= "(select count(" + Images.IMAGE_ID + ") from " + Images.IMAGE_TABLE_NAME
																		+ ") as " + IMAGE_COUNT;

		public static final String	PROFILE_STATS				= "select " + ALLROUTES_PLACE + "," + SUM_ROUTE_DISTANCE + ","
																		+ COUNT_PLACES + "," + COUNT_IMAGES + "," + COUNT_ROUTES + " from "
																		+ ALLROUTESS_TABLE_NAME;
	}

	public final class Path implements BaseColumns {

		private Path() {
		}

		public static final String	Path_NAME			= "path_name";
		public static final String	Path_KEY			= "path_key";
		public static final String	Path_PLACE_ID		= "path_place_id";
		public static final String	PathS_TABLE_NAME	= "table_paths";
		public static final String	Path_X				= "path_x";
		public static final String	Path_Y				= "path_y";
		public static final String	Path_ID				= "path_id";
		public static final String	Path_DATETIME		= "path_date_time";
		public static final String	Path_SPEED			= "path_speed";
		public static final String	Path_ALTITUDE		= "path_altitude";
	}

	public final class Places implements BaseColumns {

		private Places() {
		}

		public static final String	PLACES_TABLE_NAME		= "table_places";
		public static final String	PLACES_ID				= "places_id";
		public static final String	PLACES_NAME				= "places_name";
		public static final String	PLACES_CURRENT_PLACE	= "places_current_place";
		public static final String	PLACES_Route_Count		= "places_route_count";
	}

	public final class User_Settings implements BaseColumns {

		private User_Settings() {
		}

		public static final String	USER_SETTINGS_TABLE_NAME	= "table_settings";
		public static final String	USER_SETTINGS_TRACKING_ON	= "user_settings_tracking_on";
		public static final String	USER_SETTINGS_IDENT			= "user_settings_ident";
	}

	public final class Images implements BaseColumns {

		private Images() {
		}

		public static final String	IMAGE_TABLE_NAME	= "table_images";
		public static final String	IMAGE_ID			= "image_id";
		public static final String	IMAGE_PATH			= "image_path";
		public static final String	IMAGE_PLACE_NAME	= "image_place_name";
		public static final String	IMAGE_CREATEDATE	= "image_createdate";
		public static final String	IMAGE_X				= "image_x";
		public static final String	IMAGE_Y				= "image_y";
		public static final String	IMAGE_EARTH_GALLERY	= "image_earth_gallery";

	}

	public final class Journals implements BaseColumns {

		private Journals() {
		}

		public static final String	JOURNAL_ONLINE_ID	= "journal_online_id";
		public static final String	JOURNAL_NAME		= "journal_names";
		public static final String	JOURNAL_TABLE		= "journal_table";
		public static final String	JOURNAL_ID			= "journal_id";
		public static final String	JOURNAL_PLACE_ID	= "journal_place_id";
		public static final String	JOURNAL_TRIP_ID		= "journal_trip_id";
		public static final String	JOURNAL_ACTIVITY_ID	= "journal_activity_id";
		public static final String	JOURNAL_CREATE_DATE	= "journal_create_date";
		public static final String	JOURNAL_UPDATE_DATE	= "journal_update_date";
		public static final String	JOURNAL_TEXT		= "journal_text";
		public static final String	JOURNAL_X			= "journal_x";
		public static final String	JOURNAL_Y			= "journal_y";
		public static final String	JOURNAL_DIRTY		= "journal_dirty";
	}

	public final class aTrip implements BaseColumns {

		private aTrip() {
		}

		public static final String	A_TRIP_TABLE		= "aTrip_table";
		public static final String	A_TRIP_ITEM_TYPE_ID	= "trip_item_type_id";
		public static final String	A_TRIP_ITEM_ID		= "trip_item_id";
		public static final String	A_TRIP_ONLINE_ID	= "trip_online_id";
		public static final String	A_TRIP_CREATE_DATE	= "trip_create_date";
		public static final String	A_TRIP_UPDATE_DATE	= "trip_update_date";
		public static final String	A_TRIP_DIRTY		= "trip_dirty";
	}
}
