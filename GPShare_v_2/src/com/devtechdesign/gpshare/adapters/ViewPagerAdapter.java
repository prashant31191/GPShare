package com.devtechdesign.gpshare.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.devtechdesign.gpshare.layouts.FriendsManager;
import com.devtechdesign.gpshare.layouts.FrgTrackingStats;
import com.devtechdesign.gpshare.profile.ProfileLayout;

public class ViewPagerAdapter extends FragmentPagerAdapter {
	private Activity act;
	private Fragment profileLayout;
	private FriendsManager friendsFragment;

	// public ProfileLayout getprofileLayout() {
	// return (ProfileLayout) profileLayout;
	// }

	public ViewPagerAdapter(Activity act, Context context, FragmentManager fm) {
		super(fm);
		this.act = act;

	}

	// @Override
	// public Fragment getItem(int position) {
	// Fragment f = new Fragment();
	// switch (position) {
	// case 0:
	// f = ProfileLayout.newInstance(act);
	// profileLayout = f;
	// GPContextHolder.setProfileLayout((ProfileLayout) f);
	// break;
	// case 1:
	// f = FrgTrackingStats.newInstance(act);
	// break;
	// case 2:
	// f = FrgFriends.newInstance(act);
	// setFriendsFragment((FrgFriends) f);
	// break;
	// case 3:
	// f = FrgRanks.newInstance(act);
	// break;
	// }
	// return f;
	// }

	@Override
	public int getCount() {
		return 4;
	}

	public FriendsManager getFriendsFragment() {
		return friendsFragment;
	}

	public void setFriendsFragment(FriendsManager friendsFragment) {
		this.friendsFragment = friendsFragment;
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}