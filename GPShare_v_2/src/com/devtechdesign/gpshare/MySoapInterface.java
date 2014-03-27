package com.devtechdesign.gpshare;

public interface MySoapInterface {
	public static final String NAMESPACE = "http://tempuri.org/";
	//public static final String URL = "http://www.devtechdesign.com/Map/LoginSvc.1.svc?wsdl";
	//public static final String PLACESURL = "http://www.devtechdesign.com/Map/Places4.svc?wsdl";

	public static final String LOGIN_SOAP_ACTION = "http://tempuri.org/LoginSvc/doLogin";
	public static final String LOGIN_METHOD_NAME = "doLogin";
	
	public static final String REGISTER_SOAP_ACTION = "http://tempuri.org/LoginSvc/doNewUserInsert";
	public static final String REGISTER_METHOD_NAME = "doNewUserInsert";	
	
	public static final String GETPLACES_SOAP_ACTION = "http://tempuri.org/Places/GetPlaces";
	public static final String GETPLACES_METHOD_NAME = "GetPlaces";	

	public static final String SharedPref = "myPrefs";
	
	int SPIN_BAR_TYPE = 1;
	
	public static final String regExpn = 
		"^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
	    +"((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
	      +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
	      +"([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
	      +"[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
	      +"([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

}
