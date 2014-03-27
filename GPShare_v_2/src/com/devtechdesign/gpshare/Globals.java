package com.devtechdesign.gpshare;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Bitmap;
import android.location.Location;
import android.support.v4.app.Fragment;

import com.devtechdesign.gpshare.elements.aRoute;
import com.google.android.gms.maps.GoogleMap;

public class Globals {

	private static String routeFolder;
	private static String currentUser;
	private static String coords;
	private static String currentCoords;
	private static String personId;
	private static String currentPlaceId;
	private static String newPlaceName;
	private static String[] placeList;
	private static String[] coordList;
	private static String[] imgNames;
	private static List<Bitmap> imgUrlBmps;
	private static String[] routeList;
	private static String[] imgUrlList;
	private static String routeCoordList;
	private static String[] imgUrlsByplace;
	private static String[] contactList;
	private static Location currentLocation;
	private static String universalBool = "false";
	private static String topSpeedCoords;
	private static String newVersion;
	private static String firstLastName;
	private static String currentPlace;
	private static aRoute currentRoute;
	private static String longi;
	private static String lat;
	private static Fragment frag;
	private static GoogleMap mMap;

	public static String getNewVersion() {

		return newVersion;
	}

	public static void setnewVersion(String newVersion) {

		Globals.newVersion = newVersion;
	}

	public static String gettopSpeedCoords() {

		return topSpeedCoords;
	}

	public static void settopSpeedCoords(String topSpeedCoords) {

		Globals.topSpeedCoords = topSpeedCoords;
	}

	public static String getpersonId() {

		return personId;
	}

	public static void setpersonId(String personId) {

		Globals.personId = personId;
	}

	public static String getuniversalBool() {

		return universalBool;
	}

	public static void setuniversalBool(String universalBool) {

		Globals.universalBool = universalBool;
	}

	public static Location getCurrentLocation() {

		return currentLocation;
	}

	public static void setCurrentLocation(Location currentLocation) {

		Globals.currentLocation = currentLocation;
	}

	public static String[] getcontactList() {

		return contactList;
	}

	public static void setcontactList(String[] contactList) {

		Globals.contactList = contactList;
	}

	public static String[] getImgUrlsByPlace() {

		return imgUrlsByplace;
	}

	public static void setImgUrlsByPlace(String[] imgUrlsByplace) {

		Globals.imgUrlsByplace = imgUrlsByplace;
	}

	public static String getRouteCoordList() {

		return routeCoordList;
	}

	public static void setRouteCoordList(String routeCoordList) {

		Globals.routeCoordList = routeCoordList;
	}

	public static String[] getimgUrlList() {

		return imgUrlList;
	}

	public static void setimgUrlList(String[] imgUrlList) {

		Globals.imgUrlList = imgUrlList;
	}

	public static String[] getrouteList() {

		return routeList;
	}

	public static void setrouteList(String[] routeList) {

		Globals.routeList = routeList;
	}

	public static List<Bitmap> getimgUrlBmps() {
		return imgUrlBmps;
	}

	public static void setimgUrlBmps(List<Bitmap> list) {

		Globals.imgUrlBmps = list;
	}

	public static String[] getImgNames() {
		return imgNames;
	}

	public static void setImgNames(String[] imgNames) {

		Globals.imgNames = imgNames;
	}

	public static String[] getCoordList() {
		return coordList;
	}

	public static void setCoordList(String[] coordList) {

		Globals.coordList = coordList;
	}

	public static String getnewPlaceName() {
		return newPlaceName;
	}

	public static String[] getPlaceList() {
		return placeList;
	}

	public static void setPlaceList(String[] placeList) {

		Globals.placeList = placeList;
	}

	public static void setnewPlaceName(String newPlaceName) {

		Globals.newPlaceName = newPlaceName;
	}

	public static String getcurrentPlaceId() {
		return currentPlaceId;
	}

	public static void setcurrentPlaceId(String currentPlaceId) {

		Globals.currentPlaceId = currentPlaceId;
	}

	public static String getRouteFolder() {
		return routeFolder;
	}

	public static void setRouteFolder(String routeFolder) {

		Globals.routeFolder = routeFolder;
	}

	public static String getCurrentUser() {
		return currentUser;
	}

	public static void setCurrentUser(String currentUser) {

		Globals.currentUser = currentUser;
	}

	public static String getCoords() {
		return coords;
	}

	public static void setCoords(String coords) {

		Globals.coords = coords;
	}

	public static String getcurrentCoords() {
		return currentCoords;
	}

	public static void setcurrentCoords(String currentCoords) {

		Globals.currentCoords = currentCoords;
	}

	public static String getFirstLastName() {
		return firstLastName;
	}

	public static void setFirstLastName(String firstLastName) {
		Globals.firstLastName = firstLastName;
	}

	public static String getCurrentPlace() {
		return currentPlace;
	}

	public static void setCurrentPlace(String currentPlace) {
		Globals.currentPlace = currentPlace;
	}

	public static aRoute getCurrentRoute() {
		return currentRoute;
	}

	public static void setCurrentRoute(aRoute currentRoute) {
		Globals.currentRoute = currentRoute;
	}

	public static String getLongi() {
		return longi;
	}

	public static void setLongi(String longi) {
		Globals.longi = longi;
	}

	public static String getLat() {
		return lat;
	}

	public static void setLat(String lat) {
		Globals.lat = lat;
	}

	public static Fragment getCurrentFrag() {
		return frag;
	}

	public static void setCurrentFrag(Fragment frag2) {
		Globals.frag = frag2;
	}

	public static void setCurrentmMap(GoogleMap mMap) {
		Globals.mMap = mMap;
	}

	public static GoogleMap getCurrentmMap() {
		return mMap;
	}
}
