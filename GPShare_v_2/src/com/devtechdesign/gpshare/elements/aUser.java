package com.devtechdesign.gpshare.elements;

public class aUser {

	private String	encryptedUserName;
	private String	routeCount;
	private String	placeCount;
	private String	totalDistance	= "0.0";
	private String	totalTime		= "0.0";
	private String	totalImages;
	private String	firstLastName;
	private String	personId;
	private String	decryptedPassword;
	private String	encryptedPassword;
	private String	encryptedUsername;
	private String	profilePicUrl;
	private String	phoneNumber;

	public String getRouteCount() {
		return routeCount;
	}

	public void setRouteCount(String routeCount) {
		this.routeCount = routeCount;
	}

	public String getPlaceCount() {
		return placeCount;
	}

	public void setPlaceCount(String placeCount) {
		this.placeCount = placeCount;
	}

	public String getTotalDistance() {
		return totalDistance;
	}

	public void setTotalDistance(String totalDistance) {
		this.totalDistance = totalDistance;
	}

	public String getTotalImageCount() {
		return totalImageCount;
	}

	public void setTotalImageCount(String totalImageCount) {
		this.totalImageCount = totalImageCount;
	}

	public String getTotalFriendcount() {
		return totalFriendcount;
	}

	public void setTotalFriendcount(String totalFriendcount) {
		this.totalFriendcount = totalFriendcount;
	}

	private String	totalImageCount;
	private String	totalFriendcount;

	public String getEncryptedUserName() {
		return encryptedUserName;
	}

	public void setEncryptedUserName(String encryptedUserName) {
		this.encryptedUserName = encryptedUserName;
	}

	public String getFirstLastName() {
		return firstLastName;
	}

	public void setFirstLastName(String firstLastName) {
		this.firstLastName = firstLastName;
	}

	public String getPersonId() {
		return personId;
	}

	public void setPersonId(String personId) {
		this.personId = personId;
	}

	public String getDecryptedPassword() {
		return decryptedPassword;
	}

	public void setDecryptedPassword(String decryptedPassword) {
		this.decryptedPassword = decryptedPassword;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public void setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
	}

	public String getEncryptedUsername() {
		return encryptedUsername;
	}

	public void setEncryptedUsername(String encryptedUsername) {
		this.encryptedUsername = encryptedUsername;
	}

	public String getDecryptedUsername() {
		return decryptedUsername;
	}

	public void setDecryptedUsername(String decryptedUsername) {
		this.decryptedUsername = decryptedUsername;
	}

	public String getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(String totalTime) {
		this.totalTime = totalTime;
	}

	public String getTotalImages() {
		return totalImages;
	}

	public void setTotalImages(String totalImages) {
		this.totalImages = totalImages;
	}

	private String	decryptedUsername;

}
