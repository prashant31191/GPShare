package com.devtechdesign.gpshare.utility;

public interface SoapInterface {

	public String		GPSHARE_DOMAIN								= "http://www.gpshare.com";
	String				GPSHARE_DIR_ROUTES							= "/map/gpshare/routes/";
	String				NAMESPACE									= "http://tempuri.org/";
	String				MSRNameSpace								= "http://msrmaps.com/";
	String				MSRMapsUrl									= "http://msrmaps.com/TerraService2.asmx?WSDL";
	// String AUTHENTICATIONURL =
	// "http://www.devtechdesign.com/Map/LoginSvc.1.svc?wsdl";
	String				PLACES_URL									= "http://www.devtechdesign.com/Map/Places4.svc?wsdl";
	String				GPShare_Svc_Url								= "http://www.devtechdesign.com/Map/Services/GPShareSvc.svc?wsdl";
	String				USGSImage_Svc_Url							= "http://raster.nationalmap.gov/ArcGIS/services/Orthoimagery/USGS_EDC_Ortho_HRO/ImageServer?wsdl";
	String				PROPERTY_REG_ID								= "registration_id";
	String				PERSON_ID									= "personId";
	// String AUTHENTICATIONURL =
	// "http://www.devtechdesign.com/GEO_TRACKER/Services/ACCOUNTSSvc.svc?wsdl";
	// String LASTKNOWNLOCATIONURL =
	// "http://www.devtechdesign.com/GEO_TRACKER/Services/GetLastKnownLocationSvc.1.svc?wsdl";
	// String TRANSACTIONURL =
	// "http://www.devtechdesign.com/GEO_TRACKER/Services/TRANSACTIONSvc.svc?wsdl";
	// String GETFRIENDS_URL =
	// "http://www.devtechdesign.com/GEO_TRACKER/Services/GetFriendsSvc.svc?wsdl";
	// String FB_SERVICE_URL =
	// "http://www.devtechdesign.com/Map/Services/TaggedImgUpdateUrl.svc?wsdl";

	// String APLACEMARK_SERVICE_URL =
	// "http://www.devtechdesign.com/Map/Services/MPlaceMarkService.svc?wsdl";
	// String GetNotificationsTags_URL =
	// "http://www.devtechdesign.com/Map/services/GetFriendsSvc.svc?wsdl";
	// String GetARoute_Url =
	// "http://www.devtechdesign.com/map/services/ARouteService.svc";
	// String Email_URL =
	// "http://www.devtechdesign.com/Map/services/EmailSvc.svc?wsdl";
	// String Account_Url =
	// "http://www.devtechdesign.com/Map/services/AccountSvc.svc?wsdl";
	// String URL = "http://www.devtechdesign.com/Map/LoginSvc.1.svc?wsdl";
	String				EXTRA_MESSAGE								= "message";
	String				SENDER_ID									= "847884147146";
	/**
	 * Intent used to display a message in the screen.
	 */
	static final String	DISPLAY_MESSAGE_ACTION						= "com.google.android.gcm.demo.app.DISPLAY_MESSAGE";

	String				SCREEN_SHOT_FOLDER							= "/GPShare/Photos/ScreenShots";
	String				getRoutesByPersonIdAction					= "http://tempuri.org/GPShareSvc/getRoutesByPersonId";
	String				writeFacebookGraphRouteFile					= "http://tempuri.org/GPShareSvc/writeFacebookGraphRouteFile";
	String				updatePublicProfileBoolAction				= "http://tempuri.org/GPShareSvc/updatePublicProfile";
	String				getRoutesByPlaceIdAction					= "http://tempuri.org/GPShareSvc/getRoutesByPlaceId";
	String				getRoutesAndPlaceMarksPlaceIdAction			= "http://tempuri.org/GPShareSvc/getRoutesAndPlaceMarksPlaceId";
	String				getRoutesAndPlaceMarksPersonIdAction		= "http://tempuri.org/GPShareSvc/getRoutesAndPlaceMarksPersonId";
	String				getRoutesByPersonIdRangeAction				= "http://tempuri.org/GPShareSvc/getRoutesByPersonIdRange";

	String				getImageBackUpsAction						= "http://tempuri.org/GPShareSvc/getImageBackUps";
	String				updateSettingsAction						= "http://tempuri.org/GPShareSvc/updateSettings";
	String				MSRMapsAction								= "http://msrmaps.com/GetAreaFromPt";
	String				backupPhoneImagesAction						= "http://tempuri.org/GPShareSvc/backupPhoneImages";
	String				LOGIN2_SOAP_ACTION							= "http://tempuri.org/GPShareSvc/doLogin";
	String				getDistanceRanks_action						= "http://tempuri.org/GPShareSvc/getDistanceRanks";
	String				getUsersAction								= "http://tempuri.org/GPShareSvc/getUsers";
	String				doesUserExistAction							= "http://tempuri.org/GPShareSvc/doesUserExist";
	String				AcknowledgeTagAction						= "http://tempuri.org/GPShareSvc/acknowledgeTag";
	String				Deteleroute_Action							= "http://tempuri.org/GPShareSvc/deleteRoute";
	String				Account_Action								= "http://tempuri.org/GPShareSvc/AccountsMethod";
	String				Email_Action								= "http://tempuri.org/GPShareSvc/sendEmailTag";
	String				GetRoute_Action								= "http://tempuri.org/GPShareSvc/GetARoutes";
	String				APLACEMARK_ACTION							= "http://tempuri.org/GPShareSvc/getPlaceAPlaceMarksDataString";
	String				LOGIN_SOAP_ACTION							= "http://tempuri.org/GPShareSvc/LoginUser";
	String				GET_PLACES_SOAP_ACTION						= "http://tempuri.org/GPShareSvc/GetPlaces";
	String				REGISTER_SOAP_ACTION						= "http://tempuri.org/GPShareSvc/RegisterUser";
	String				UPDATELOCATION_SOAP_ACTION					= "http://tempuri.org/GPShareSvc/UpdateLocation";
	String				LASTKNOWNLOCATION_SOAP_ACTION				= "http://tempuri.org/GPShareSvc/GetLastKnownLocationMethod";
	String				FriendSearch_SOAP_ACTION					= "http://tempuri.org/GPShareSvc/FriendSearch";
	String				FRIENDREQUEST_SOAP_ACTION					= "http://tempuri.org/GPShareSvc/RequestFriend";
	String				GETFRIENDS_SOAP_ACTION						= "http://tempuri.org/GPShareSvc/GetFriendsData";
	String				GETPROFILE_STATS_SOAP_ACTION				= "http://tempuri.org/GPShareSvc/getProfileStats";
	// String ACCEPT_FRIEND_SOAP_ACTION =
	// "http://tempuri.org/ACCOUNTSSvc/ExceptFriend";sendemail

	String				GETPROFILE_STATS_ACTION						= "http://tempuri.org/GPShareSvc/getProfileStats";
	String				TAGGED_IMG_RECORD_INSERT_ACTION				= "http://tempuri.org/GPShareSvc/insertFbImage";
	String				TAGGED_IMG_RECORD_UPDATE_ACTION				= "http://tempuri.org/GPShareSvc/updateFbImageTag";
	String				INSERT_ROUTE_ACTION							= "http://tempuri.org/GPShareSvc/insertRoute";
	String				Get_Tags_Action								= "http://tempuri.org/GPShareSvc/GetTags";
	String				Insert_Tag_Action							= "http://tempuri.org/GPShareSvc/insertTag";
	String				Insert_ErrorLog_Action						= "http://tempuri.org/GPShareSvc/insertErrorLog";
	String				Insert_CreateUser_Action					= "http://tempuri.org/GPShareSvc/createUser";
	String				registUserGcmAction							= "http://tempuri.org/GPShareSvc/registerUserGcm";
	String				sendNotificationAction						= "http://tempuri.org/GPShareSvc/sendNotification";
	String				getPWFromUserName_action					= "http://tempuri.org/GPShareSvc/getPWFromUserName";
	String				updateFbIdforExistUserAction				= "http://tempuri.org/GPShareSvc/updateFbIdforExistUser";
	String				registerGcmAction							= "http://tempuri.org/GPShareSvc/registerGcm";

	String				getProfileStatsMethodName					= "getProfileStats";
	String				updatePublicProfileBoolMethodName			= "updatePublicProfile";
	String				updateFbIdforExistUserMethodName			= "updateFbIdforExistUser";
	String				getPWFromUserName_methodName				= "getPWFromUserName";
	String				getRoutesByPersonIdMethodName				= "getRoutesByPersonId";
	String				writeFacebookGraphRouteFileMethodName		= "writeFacebookGraphRouteFile";
	String				getRoutesByPlaceIdMethodName				= "getRoutesByPlaceId";
	String				getRoutesAndPlaceMarksPlaceIdMethodName		= "getRoutesAndPlaceMarksPlaceId";
	String				getRoutesByPersonIdRangeMethodName			= "getRoutesByPersonIdRange";
	String				getRoutesAndPlaceMarksPersonIdMethodName	= "getRoutesAndPlaceMarksPersonId";
	String				getImageBackUpsMethodName					= "getImageBackUps";
	String				backupPhoneImagesMethodName					= "backupPhoneImages";
	String				updateSettingsMethodName					= "updateSettingsMethod";
	String				getDistanceRanksMethodName					= "getDistanceRanks";
	String				MRSgetTileByLatLonMethodName				= "GetAreaFromPt";
	String				LOGIN2_METHOD_NAME2							= "doLogin";
	String				getUsersMethodName							= "getUsers";
	String				doesUserExist_Method_Name					= "doesUserExist";
	String				AcknoledgeTag_METHOD_NAME					= "acknowledgeTag";
	String				CreateUser_METHOD_NAME						= "createUser";
	String				registerUserGcmMethodName					= "registerUserGcm";
	String				sendNotificationMethodName					= "sendNotification";
	String				registerGcmMethodName						= "registerGcm";

	String				DeleteRouteMethod							= "deleteRoute";
	String				Insert_Tag_Method							= "insertTag";
	String				Insert_ErrorLog_Method						= "insertErrorLog";
	String				Account_Method_Name							= "AccountsMethod";
	String				Email_Method_Name							= "sendEmailTag";
	String				ARouteMethod_Name							= "GetARoutes";
	String				APLACEMARK_METHOD_NAME						= "getPlaceAPlaceMarksDataString";
	String				Tags_MethodName								= "GetTags";
	String				TAGGED_IMG_RECORD_INSERT_METHOD_NAME		= "insertFbImage";
	String				INSERT_ROUTE_METHOD_NAME					= "insertRoute";
	String				TAGGED_IMG_RECORD_UPDATE_METHOD_NAME		= "updateFbImageTag";
	String				ACCEPT_FRIEND_METHOD_NAME					= "ExceptFriend";
	String				LOGIN_METHOD_NAME							= "LoginUser";
	String				GETPLACE_METHOD_NAME						= "GetPlaces";
	String				REGISTER_METHOD_NAME						= "RegisterUser";
	String				UPDATELOCATION_METHOD_NAME					= "UpdateLocation";
	String				LASTKNOWNLOCATION_METHOD_NAME				= "GetLastKnownLocationMethod";
	String				FriendSearch_METHOD_NAME					= "FriendSearch";
	String				FRIENDREQUEST_METHOD_NAME					= "RequestFriend";
	String				GETFRIENDS_METHOD_NAME						= "GetFriendsData";

	String				SharedPref									= "myPrefs";
	int					SPIN_BAR_TYPE								= 1;
	int					ONLINE_IMG									= 0;
	int					OFFLINE_IMG									= 1;
	int					PROFILE_IMG									= 2;
	int					ONLINE_DATA									= 0;
	int					OFFLINE_DATA								= 1;
	int					HISTORY_MAIN								= 0;
	int					TRACK_MAIN									= 1;
	int					FRIENDS_MAIN								= 2;
	int					CAMERA_MAIN									= 3;

	String				regExpn										= "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
																			+ "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
																			+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
																			+ "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
																			+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
																			+ "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

	void uncaughtException(Thread thread, Throwable ex);

}
