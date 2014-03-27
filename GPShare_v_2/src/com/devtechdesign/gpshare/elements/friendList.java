package com.devtechdesign.gpshare.elements;

import java.util.StringTokenizer;

public class friendList {

	private String	friendFirstName;
	private String	friendLastName;
	private String	friendCurrentLocation;
	private String	friendFirstLast;
	private String	friendPreviousLocation;
	private String	friendShipConfirmed;
	private String	friendUserName;
	private String	userCurrentLocation;
	private String	userFirstLast;
	private String	userFirstName;
	private String	userLastName;
	private String	userPreviousLocation;
	private String	userUserName;
	private String	profileImgUrl;
	private String	friendPhoneNumber;
	private String	friendId;
	private String	userId;
	private String	friendProfilePic;

	private boolean	checked	= false;
	private String	FriendsGcmRegId;

	public friendList() {

	}

	public friendList(String createDate, String facebookId, String firstName, String friendFaceBookId, String friendFirstName2,
			String friendId2, String friendLastName, String friendsGcmRegId, String friendsPhoneNumber, String friendsProfilePic,
			String lastName, String personId, String profilePic, String friendShipConfirmed2, String userFirstLast) {

		this.userFirstName = firstName;
		this.friendId = friendId2;
		this.friendFirstName = friendFirstName2;
		this.friendFirstLast = friendFirstName + " " + friendLastName;
		this.setFriendsGcmRegId(friendsGcmRegId);
		this.friendPhoneNumber = friendsPhoneNumber;
		this.friendProfilePic = friendsProfilePic;
		this.userId = personId;
		this.profileImgUrl = profilePic;
		this.userLastName = lastName;
		this.friendShipConfirmed = friendShipConfirmed2;
		this.userFirstLast = userFirstLast;
	}

	public String getuserId() {
		return userId;
	}

	public String getfriendId() {
		return friendId;
	}

	public String getfriendPhoneNumber() {
		return friendPhoneNumber;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public void toggleChecked() {
		checked = !checked;
	}

	public String getProfielImgUrl() {
		return profileImgUrl;
	}

	public String getuserFirstLast() {
		return userFirstLast;
	}

	public String getuserFirstName() {
		return userFirstName;
	}

	public String getuserLastName() {
		return userLastName;
	}

	public String getuserPreviousLocation() {
		return userPreviousLocation;
	}

	public String getuserUserName() {
		return userUserName;
	}

	public String getfriendFirstName() {
		return friendFirstName;
	}

	public String getfriendLastName() {
		return friendLastName;
	}

	public String getfriendCurrentLocation() {
		return friendCurrentLocation;
	}

	public String getfriendFirstLast() {
		return friendFirstLast;
	}

	public String getfriendPreviousLocation() {
		return friendPreviousLocation;
	}

	public String getfriendShipConfirmed() {
		return friendShipConfirmed;
	}

	public String getuserCurrentLocation() {
		return userCurrentLocation;
	}

	public String getfriendUserName() {
		return friendUserName;
	}

	public String getFriendProfilePic() {
		return friendProfilePic;
	}

	public void setFriendProfilePic(String friendProfilePic) {
		this.friendProfilePic = friendProfilePic;
	}

	public String getFriendsGcmRegId() {
		return FriendsGcmRegId;
	}

	public void setFriendsGcmRegId(String friendsGcmRegId) {
		FriendsGcmRegId = friendsGcmRegId;
	}
}
