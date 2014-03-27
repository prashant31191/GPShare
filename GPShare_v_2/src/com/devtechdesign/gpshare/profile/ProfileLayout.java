package com.devtechdesign.gpshare.profile;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabContentFactory;
import android.widget.TabHost.TabSpec;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.elements.aUser;
import com.devtechdesign.gpshare.utility.GPsharePrefs;
import com.devtechdesign.gpshare.utility.SoapInterface;
import com.devtechdesign.gpshare.Images.AsyncImgDl;

import dev.tech.gpsharelibs.DTD;
import dev.tech.gpsharelibs.IDevTech;

public class ProfileLayout extends RelativeLayout implements SoapInterface, IDevTech {

	public ProfileLayout(Context context) {
		super(context);
		setupView(context);
	}

	static GPsharePrefs GPSharePrefs;
	public RelativeLayout lLayout;
	private ImageView ivProfileImage;
	private TextView txtName;
	private TextView txtTotalDistance;
	private TextView txtUploadedImages;
	private TextView txtTotalPlaces;
	private TextView txtTotalFriends;
	private TextView txtTotalRoutes;
	private TabHost mTabHost;
	private ProgressBar progressBar1;
	private ProgressBar progressBar2;
	private ProgressBar progressBar3;
	private ProgressBar progressBar4;
	private ProgressBar progressBar5;
	private ImageButton imgBtnMinimizeProfile;
	private LinearLayout lytProfile;
	private Profile profile;
	private Context context;

	private void setupView(Context context) {

		this.context = context;
		if (lLayout == null) {
			try {

				String personId = DTD.getPersonId();
				String firstLast = DTD.getFirstLastName();
				DTD.getImgProfileUrl();

				LayoutInflater li = LayoutInflater.from(context);
				lLayout = (RelativeLayout) li.inflate(R.layout.profile_layout, null, false);
				ivProfileImage = (ImageView) lLayout.findViewById(R.id.imgProfile);
				txtName = (TextView) lLayout.findViewById(R.id.txtName);
				txtTotalDistance = (TextView) lLayout.findViewById(R.id.txtTotalDistance);
				txtUploadedImages = (TextView) lLayout.findViewById(R.id.txtUploadedImages);
				txtTotalPlaces = (TextView) lLayout.findViewById(R.id.txtTotalPlaces);
				txtTotalFriends = (TextView) lLayout.findViewById(R.id.txtTotalFriends);
				txtTotalRoutes = (TextView) lLayout.findViewById(R.id.txtTotalRoutes);
				imgBtnMinimizeProfile = (ImageButton) lLayout.findViewById(R.id.imgBtnMinimizeProfile);
				// imgBtnMinimizeProfile.setOnClickListener(imgBtnMinimizeProfileClick);
				setmTabHost((TabHost) lLayout.findViewById(android.R.id.tabhost));
				lytProfile = (LinearLayout) lLayout.findViewById(R.id.lytProfile);

				progressBar1 = (ProgressBar) lLayout.findViewById(R.id.progressBar1);
				progressBar2 = (ProgressBar) lLayout.findViewById(R.id.progressBar2);
				progressBar3 = (ProgressBar) lLayout.findViewById(R.id.progressBar3);
				progressBar4 = (ProgressBar) lLayout.findViewById(R.id.progressBar4);
				progressBar5 = (ProgressBar) lLayout.findViewById(R.id.progressBar5);

				getmTabHost().setup();

				getmTabHost().getTabWidget().setDividerDrawable(R.drawable.tab_divider);

				// routeLyt = new RouteLytLocal(context.getApplicationContext(),
				// (Activity) context);

				// ProfileImagesLayout fPLayout = new
				// ProfileImagesLayout(context.getApplicationContext(), null);

				// GPContextHolder.setProfileImagesLayout(fPLayout);
				// GPContextHolder.setRouteLyt(routeLyt);

				// setupTab(routeLyt, "Routes");
				// setupTab(fPLayout, "Images");

				// loadProfileImage(imgUrl);

				txtName.setText(firstLast);

				new GetProfileAsyncTask().execute(personId);

				getmTabHost().setup();

				getmTabHost().getTabWidget().setDividerDrawable(R.drawable.tab_divider);

			} catch (NullPointerException e) {

			}
		}
	}

	public RelativeLayout getView() {
		return lLayout;
	}

	// public RouteLyt getRouteLyt() {
	// return routeLyt;
	// }

	private void showProgressBars() {
		progressBar1.setVisibility(View.VISIBLE);
		progressBar2.setVisibility(View.VISIBLE);
		progressBar3.setVisibility(View.VISIBLE);
		progressBar4.setVisibility(View.VISIBLE);
		progressBar5.setVisibility(View.VISIBLE);
	}

	private void hideProgressBars() {
		progressBar1.setVisibility(View.GONE);
		progressBar2.setVisibility(View.GONE);
		progressBar3.setVisibility(View.GONE);
		progressBar4.setVisibility(View.GONE);
		progressBar5.setVisibility(View.GONE);
	}

	private void setupTab(final View view, final String tag) {

		View tabview = createTabView(getmTabHost().getContext(), tag, R.layout.tabs_bg);

		TabSpec setContent = getmTabHost().newTabSpec(tag).setIndicator(tabview).setContent(new TabContentFactory() {
			public View createTabContent(String tag) {
				return view;
			}
		});

		getmTabHost().addTab(setContent);
	}

	private static View createTabView(final Context context, final String text, int tabViewId) {
		View view = LayoutInflater.from(context).inflate(tabViewId, null);
		TextView tv = (TextView) view.findViewById(R.id.tabsText);
		tv.setText(text);//
		return view;
	}

	// OnClickListener imgBtnMinimizeProfileClick = new OnClickListener() {
	//
	// @Override
	// public void onClick(View v) {
	// ExpandAnimation expandAni = new ExpandAnimation(lytProfile, 500);
	// v.startAnimation(expandAni);
	//
	// if (MainControl.lytLayoutStatus.getVisibility() == View.GONE) {
	// MainControl.lytLayoutStatus.setVisibility(View.VISIBLE);
	// imgBtnMinimizeProfile.setImageResource(R.drawable.orange_round_nav_arrow);
	//
	// } else {
	// MainControl.lytLayoutStatus.setVisibility(View.GONE);
	// imgBtnMinimizeProfile.setImageResource(R.drawable.orange_round_nav_arrow_down);
	// }
	// }
	// };

	public void loadProfileImage(String imgUrl) {

		if (!imgUrl.equals("")) {
			String urlPrefix = imgUrl.substring(0, 4);

			if (!urlPrefix.equals("http")) {
				imgUrl = "http://" + imgUrl;
			}
		}
		Bitmap bMap = null;
		new AsyncImgDl(bMap, imgUrl, ivProfileImage, ONLINE_IMG);
	}

	private class GetProfileAsyncTask extends AsyncTask<String, Void, aUser> {

		@Override
		protected aUser doInBackground(String... params) {

			profile = new Profile(context);
			return null;
		}

		@Override
		protected void onPostExecute(aUser result) {
			hideProgressBars();

			try {

				txtTotalDistance.setText(profile.userProfile.getTotalDistance());
				txtUploadedImages.setText(profile.userProfile.getTotalPics());
				txtTotalPlaces.setText(profile.userProfile.getTotalPlaces());
				txtTotalFriends.setText("--");
				txtTotalRoutes.setText(profile.userProfile.getTotalRoutes());

			} catch (NullPointerException e) {
				setBlankValues();
			}

		}
	}

	private void setBlankValues() {
		txtTotalDistance.setText("--");
		txtUploadedImages.setText("--");
		txtTotalPlaces.setText("--");
		txtTotalFriends.setText("--");
		txtTotalRoutes.setText("--");
	}

	public TabHost getmTabHost() {
		return mTabHost;
	}

	public void setmTabHost(TabHost mTabHost) {
		this.mTabHost = mTabHost;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

	public void bindProfile() {

		new GetProfileAsyncTask().execute("");
	}
}
