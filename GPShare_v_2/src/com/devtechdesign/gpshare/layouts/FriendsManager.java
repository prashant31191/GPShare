package com.devtechdesign.gpshare.layouts;

import java.util.ArrayList;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.adapters.FriendListAdapter;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.elements.friendList;
import com.devtechdesign.gpshare.utility.SoapInterface;

import dev.tech.gpsharelibs.DTD;

public class FriendsManager extends Fragment implements SoapInterface {

	private Context context;

	public FriendsManager() {
	}

	ArrayList<friendList> myFriends;
	TextView txtMessage;
	public ProgressBar pbLoadingData;
	LinearLayout lytProgress;
	public ListView lvFriends;
	private ViewGroup root;

	private LinearLayout lytFriends;
	private View vGroup;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (vGroup == null) {

			try {
				vGroup = (ViewGroup) inflater.inflate(R.layout.lst_friends, null, false);
				lvFriends = (ListView) vGroup.findViewById(R.id.lvFriends);
				lvFriends.setCacheColorHint(Color.TRANSPARENT);
				lvFriends.setOnItemClickListener(lvRoutesClickListener);
				lytProgress = (LinearLayout) lytFriends.findViewById(R.id.lytProgress);

			} catch (NullPointerException e) {
				e.printStackTrace();
				System.out.println("System.e.onCreateView.null: " + e.getMessage());
			}
		} else {
			ViewGroup parent = (ViewGroup) vGroup.getParent();
			parent.removeAllViews();
		}
		return vGroup;
	}

	public LinearLayout getView() {
		return lytFriends;
	}

	public void GTrack(String event) {
		GPContextHolder.getMainAct().GoogleA.recordClick(event);
	}

	public void loadFriends() {
		new LoadFriends().execute("");
	}

	public OnItemClickListener lvRoutesClickListener = new OnItemClickListener() {

		public void onItemClick(AdapterView<?> arg0, View v, int position, long arg3) {
			friendList friend = myFriends.get(position);
			RelativeLayout profileLayout = (RelativeLayout) v;

			ImageView tempImage = (ImageView) profileLayout.getChildAt(1);

			friendList currentFriend = (friendList) tempImage.getTag();
			// Intent
			// friendlistAct
			// = new
			// Intent(v.getContext(),
			// PlaceList.class);
			// friendlistAct.putExtra("friendId",
			// friend.getuserId());
			// startActivity(friendlistAct);

			GTrack("lstFriends");
		}
	};

	public class LoadFriends extends AsyncTask<String, Void, Void> {

		@SuppressWarnings("static-access")
		protected void onPreExecute() {

		}

		// automatically done on worker thread (separate from UI thread)
		protected Void doInBackground(final String... args) {
			try {
				myFriends = Transactions.getFriends(DTD.getPersonId(), "true");
			} catch (NullPointerException e) {
				e.printStackTrace();
			}
			return null;
		}

		// can use UI thread here
		protected void onPostExecute(final Void unused) {
			try {
				lytProgress.setVisibility(View.GONE);
				lvFriends.setAdapter(new FriendListAdapter(GPContextHolder.getMainAct(), myFriends));

			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}

	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}
}