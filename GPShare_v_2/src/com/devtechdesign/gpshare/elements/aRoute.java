package com.devtechdesign.gpshare.elements;

import android.widget.LinearLayout;

public class aRoute {

	private String			routeId;
	private String			XyString;
	private String			milliSeconds;
	private String			routeKey;
	private String			distance	= "0.0";
	private String			segmentKey;
	private String			description;
	private String			placeName;
	private String			routeName;
	private String			elapsedTime	= "0";
	private String			imgUrl		= "";
	private String			x;
	private String			y;
	private String			createDate;
	private String			pointCount	= "0";
	private String			largeUrl;
	private String			topSpeed;
	private String			screenShotPath;
	private String			placeId;
	private String			personId;
	private LinearLayout	lytSharePanel;
	private int				dataType;
	private boolean			isLytSharevisible;

	public boolean isCurrentlyTracking() {
		return currentlyTracking;
	}

	public void setCurrentlyTracking(boolean currentlyTracking) {
		this.currentlyTracking = currentlyTracking;
	}

	private boolean	currentlyTracking;

	public String getLargeUrl() {
		return largeUrl;
	}

	public void setLargeUrl(String largeUrl) {
		this.largeUrl = largeUrl;
	}

	public aRoute(String[] routeProps) {
	}

	public String getRouteId() {
		return routeId;
	}

	public void setRouteId(String routeId) {
		this.routeId = routeId;
	}

	public String getXyString() {
		return XyString;
	}

	public void setXyString(String xyString) {
		XyString = xyString;
	}

	public String getMilliSeconds() {
		return milliSeconds;
	}

	public void setMilliSeconds(String milliSeconds) {
		this.milliSeconds = milliSeconds;
	}

	public String getRouteKey() {
		return routeKey;
	}

	public void setRouteKey(String routeKey) {
		this.routeKey = routeKey;
	}

	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}

	public String getSegmentKey() {
		return segmentKey;
	}

	public void setSegmentKey(String segmentKey) {
		this.segmentKey = segmentKey;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getPlaceName() {
		return placeName;
	}

	public void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	public String getRouteName() {
		return routeName;
	}

	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}

	public String getElapsedTime() {
		return elapsedTime;
	}

	public void setElapsedTime(String elapsedTime) {
		this.elapsedTime = elapsedTime;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public String getPointCount() {
		return pointCount;
	}

	public void setPointCount(String pointCount) {
		this.pointCount = pointCount;
	}

	public String getTopSpeed() {
		return topSpeed;
	}

	public void setTopSpeed(String topSpeed) {
		this.topSpeed = topSpeed;
	}

	public String getScreenShotPath() {
		return screenShotPath;
	}

	public void setScreenShotPath(String screenShotPath) {
		this.screenShotPath = screenShotPath;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public LinearLayout getLytSharePanel() {
		return lytSharePanel;
	}

	public void setLytSharePanel(LinearLayout lytSharePanel) {
		this.lytSharePanel = lytSharePanel;
	}

	public boolean isLytSharevisible() {
		return isLytSharevisible;
	}

	public void setLytSharevisible(boolean isLytSharevisible) {
		this.isLytSharevisible = isLytSharevisible;
	}

	public int getDataType() {
		return dataType;
	}

	public void setDataType(int dataType) {
		this.dataType = dataType;
	}
}
