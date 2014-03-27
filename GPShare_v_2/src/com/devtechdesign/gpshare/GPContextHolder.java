package com.devtechdesign.gpshare;

import com.devtechdesign.gpshare.Images.ImageGallery;
import com.devtechdesign.gpshare.dialogs.ShareAct;
import com.devtechdesign.gpshare.layouts.FriendsManager;
import com.devtechdesign.gpshare.layouts.FrgTrackingStats;
import com.devtechdesign.gpshare.map.MapFragAct;
import com.devtechdesign.gpshare.profile.ProfileLayout;
import com.devtechdesign.gpshare.profile.imgs.Gallery;

import android.app.Fragment;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class GPContextHolder {

	private static Context MapContext;
	private static MainActivity mainAct;
	private static ShareAct shareAct;
	private static ImageGallery imgGalleryAct;
	private static ProfileLayout profileLayout;
	private static MapFragAct mapFragAct;
	private static Gallery friendProfileImagesLayout;
	private static Gallery profileImagesLayout;
	private static FriendsManager frgFriends;
	private static FrgTrackingStats frgStrackingStats;
	private static Fragment currentFragment;
	private static Fragment previousFragment;

	public static ShareAct getShareAct() {
		return shareAct;
	}

	public static void setShareAct(ShareAct shareAct) {
		GPContextHolder.shareAct = shareAct;
	}

	SQLiteDatabase tDb;

	public SQLiteDatabase gettDb() {
		return tDb;
	}

	public void settDb(SQLiteDatabase tDb) {
		this.tDb = tDb;
	}

	public static MainActivity getMainAct() {
		return mainAct;
	}

	public static void setMainAct(MainActivity mainActivity) {
		GPContextHolder.mainAct = mainActivity;
	}

	public Context getMapContext() {
		return MapContext;
	}

	public void setMapContext(Context mapContext) {
		MapContext = mapContext;
	}

	public static ImageGallery getImgGalleryAct() {
		return imgGalleryAct;
	}

	public static void setImgGalleryAct(ImageGallery imgGalleryAct) {
		GPContextHolder.imgGalleryAct = imgGalleryAct;
	}

	public static ProfileLayout getProfileLayout() {
		return profileLayout;
	}

	public static void setProfileLayout(ProfileLayout profileLayout) {
		GPContextHolder.profileLayout = profileLayout;
	}

	public static MapFragAct getMapFragAct() {
		return mapFragAct;
	}

	public static void setMapFragAct(MapFragAct mapFragAct) {
		GPContextHolder.mapFragAct = mapFragAct;
	}

	public static Gallery getFriendProfileImagesLayout() {
		return friendProfileImagesLayout;
	}

	public static void setFriendProfileImagesLayout(Gallery friendProfileImagesLayout) {
		GPContextHolder.friendProfileImagesLayout = friendProfileImagesLayout;
	}

	public static Gallery getProfileImagesLayout() {
		return profileImagesLayout;
	}

	public static void setProfileImagesLayout(Gallery profileImagesLayout) {
		GPContextHolder.profileImagesLayout = profileImagesLayout;
	}

	public static FriendsManager getFrgFriends() {
		return frgFriends;
	}

	public static void setFrgFriends(FriendsManager frgFriends) {
		GPContextHolder.frgFriends = frgFriends;
	}

	public static FrgTrackingStats getFrgStrackingStats() {
		return frgStrackingStats;
	}

	public static void setFrgStrackingStats(FrgTrackingStats frgStrackingStats) {
		GPContextHolder.frgStrackingStats = frgStrackingStats;
	}

	public static Fragment getCurrentFragment() {
		return currentFragment;
	}

	public static void setCurrentFragment(Fragment currentFragment) {
		GPContextHolder.currentFragment = currentFragment;
	}

	public static Fragment getPreviousFragment() {
		return previousFragment;
	}

	public static void setPreviousFragment(Fragment previousFragment) {
		GPContextHolder.previousFragment = previousFragment;
	}
}
