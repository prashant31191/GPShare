package com.devtechdesign.gpshare.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.Images.ImageConversionFactory;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.elements.friendList;
import com.devtechdesign.gpshare.utility.Utility;

public class FriendListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	ArrayList<friendList> myFriends;
	private Context context;

	public FriendListAdapter(Context context, ArrayList<friendList> myFriends) {

		this.context = context;
		this.myFriends = myFriends;

		if (Utility.model == null) {
			Utility.model = new ImageConversionFactory();
		}

		Utility.model.setListener(this);
		mInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return myFriends.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		friendList friend = (friendList) this.getItem(position);

		friend = myFriends.get(position);
		// The child views in each row.

		TextView textView;
		ImageView profile_pic;

		// Create a new row view
		if (convertView == null) {

			convertView = mInflater.inflate(R.layout.friendlistrow, null);
			// Find the child views.
			textView = (TextView) convertView.findViewById(R.id.rowTextView);
			profile_pic = (ImageView) convertView.findViewById(R.id.imgProfile);
			// Optimization: Tag the row with it's child views, so we don't have
			// call findViewById() later when we reuse the row.

			convertView.setTag(new FriendHolder(textView, profile_pic));

		}

		// Reuse existing row view
		else {
			// Because we use a ViewHolder, we avoid having to call
			// findViewById().
			FriendHolder viewHolder = (FriendHolder) convertView.getTag();

			textView = viewHolder.getTextView();
			profile_pic = viewHolder.getImage();
		}
		textView.setTextColor(Color.GRAY);
		textView.setText(friend.getfriendFirstLast());
		String imgUrl = friend.getFriendProfilePic();

		imgUrl = Transactions.correctUrl(imgUrl);

		profile_pic.setImageBitmap(Utility.model.getImage(friend.getfriendId(), imgUrl));
		profile_pic.setTag(friend);
		return convertView;
	}

	/** Holds child views for one row. */
	public class FriendHolder {

		private TextView textView;
		private ImageView imgProfile;

		public FriendHolder() {
		}

		public FriendHolder(TextView textView, ImageView imgProfile) {

			this.textView = textView;
			this.imgProfile = imgProfile;
		}

		public ImageView getImage() {
			return imgProfile;
		}

		public void setImage(ImageView imgProfile) {
			this.imgProfile = imgProfile;
		}

		public TextView getTextView() {
			return textView;
		}

		public void setTextView(TextView textView) {
			this.textView = textView;
		}
	}
}