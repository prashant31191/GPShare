package com.devtechdesign.gpshare.data.db;

import com.devtechdesign.gpshare.data.db.RouteDatabase.Journals;
import com.dtd.dbeagen.db.GPShareDatabase;

public class Sql { 
	
//	public static final String VW_JOURNALS = "SELECT   *"
//			                            + "FROM journals_table_name " +
//			                              "LEFT OUTER JOIN myplaces_table_name ON journals_table_name.PlaceId = myplaces_table_name.PlaceId " +
//			                              "LEFT OUTER JOIN trip_table_name ON journals_table_name.TripId = trip_table_name.TripId " +
//			                              "LEFT OUTER JOIN activity_table_name ON journals_table_name.ActivityId = activity_table_name.ActivityId";
	
	public static final String VW_JOURNALS = "SELECT     j._id, " +
														"j.y, " +
														"j.x, " +
														"j.TripId, " +
														"j.PlaceId, " +
														"j.Text, " +
														"j.CreateDate, " +
														"j.Name, " +
														"j.ActivityId," +
														"j.JournalId, " +
														"a.ActivityName, " +
														"t.TripName, " +
														"p.PlaceName " + 	
											 "FROM         			" + GPShareDatabase.Journals.JOURNALS_TABLE_NAME + " j LEFT OUTER JOIN\r\n" + 
											 "                      " + GPShareDatabase.MyPlaces.MYPLACES_TABLE_NAME + " p ON j.PlaceId = p._id LEFT OUTER JOIN\r\n" + 
											 "                      " + GPShareDatabase.Trip.TRIP_TABLE_NAME		 + " t ON j.TripId = t._id LEFT OUTER JOIN\r\n" + 
											 "                      " + GPShareDatabase.Activity.ACTIVITY_TABLE_NAME   + " a ON j.ActivityId = a._id";

	public static String vwGetJournalsViaQuery(String _id) {
	 
		return "SELECT     j._id, " +
				"j.y, " +
				"j.x, " +
				"j.TripId, " +
				"j.PlaceId, " +
				"j.Text, " +
				"j.CreateDate, " +
				"j.Name, " +
				"j.ActivityId," +
				"j.JournalId, " +
				"a.ActivityName, " +
				"t.TripName, " +
				"p.PlaceName " + 	
	 "FROM         			" + GPShareDatabase.Journals.JOURNALS_TABLE_NAME + " j LEFT OUTER JOIN\r\n" + 
	 "                      " + GPShareDatabase.MyPlaces.MYPLACES_TABLE_NAME + " p ON j.PlaceId = p._id LEFT OUTER JOIN\r\n" + 
	 "                      " + GPShareDatabase.Trip.TRIP_TABLE_NAME		 + " t ON j.TripId = t._id LEFT OUTER JOIN\r\n" + 
	 "                      " + GPShareDatabase.Activity.ACTIVITY_TABLE_NAME   + " a ON j.ActivityId = a._id where j." + Journals._ID + " = " + _id;
	}
}
