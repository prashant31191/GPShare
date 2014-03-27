package com.devtechdesign.gpshare.elements;

public class friendRoute {
	// (String userName, String placeName, String createDate, String
	// xyStringSvc)

	private String[] properties;
	private String placeName;
	private String userName;
	private String createDate;
	private String xyStringSvc;
	private String routeKey;
	private String allroutes_x;
	private String allroutes_y;
	private String allroutes_distance;
	private String allroutes_pointCount;
	private String allroutes_topSpeed;
	private String allroutes_totalRise;
	private String allroutes_totalFall;
	private String allroute_elapsedTime;
	private String routeId;

	public friendRoute(String[] myProperties) {
		properties = myProperties;
		initialize(properties);
	}

	private void initialize(String[] myProperties) {
		String[] temp = myProperties;
		for (int a = 0; a < myProperties.length; a++) {

			// System.out.println(" Property Index: " + a + " Property: " +
			// myProperties[a]);
		}
		// System.out.println("XXXRoute LIst: " +temp[0] +"," +temp[1] +"," +
		// temp[2] +"," + temp[3] +" !!!!!!!!");

		userName = temp[0];
		placeName = temp[1];
		createDate = temp[2];
		xyStringSvc = temp[3];
		routeKey = temp[4];
		allroutes_x = temp[5];
		allroutes_y = temp[6];
		allroutes_distance = temp[7];
		allroutes_pointCount = temp[8];
		allroutes_topSpeed = temp[9];
		allroutes_totalRise = temp[10];
		allroutes_totalFall = temp[11];
		allroute_elapsedTime = temp[12];
		setRouteId(temp[13]);
	}

	public String getrouteKey() {
		return routeKey;
	}

	public String getplaceName() {
		return placeName;
	}

	public String getPlaceName() {
		return placeName;
	}

	public String getUserName() {
		return userName;
	}

	public String getCreateDate() {
		return createDate;
	}

	public String getXyString() {
		return xyStringSvc;
	}

	public String getallroutes_x() {

		return allroutes_x;
	}

	public String getallroutes_y() {

		return allroutes_y;
	}

	public String getallroutes_distance() {

		return allroutes_distance;
	}

	public String getallroutes_pointCount() {

		return allroutes_pointCount;
	}

	public String getallroutes_topSpeed() {

		return allroutes_topSpeed;
	}

	public String getallroutes_totalRise() {

		return allroutes_totalRise;
	}

	public String getallroutes_totalFall() {

		return allroutes_totalFall;
	}

	public String getallroute_elapsedTime() {

		return allroute_elapsedTime;
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}
}
