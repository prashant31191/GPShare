package com.devtechdesign.gpshare.layouts;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.MainActivity;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.db.DatabaseControl;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.places.PlacesManager.IPlaceManagerAction;
import com.devtechdesign.gpshare.services.GPXService;
import com.dtd.dbeagen.db.elements.aMyPlaces;
import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;

@SuppressLint("ValidFragment")
public class FrgTrackingStats extends Fragment {

	LinearLayout statsLayout;
	private static TextView txtLatitude;
	private static TextView txtSpeed;
	private static TextView txtDistance;
	private static TextView txtLongitude;
	private static TextView txtTopSpeed;
	private static TextView mycurrentLocationText;
	private ScrollView lytScrollView;
	private static TextView txtAccuracyDisplay;
	private static TextView txtCoordCount;
	private static TextView txtElapsedTimeDisplay;
	private TableLayout tblMainTableLayout;
	private TextView tvDistanceUom;
	private TextView tvSpeedUom;
	private MainActivity mainAct;
	private ITrackingActions mCallback;
	private boolean trackingBool, isPebbleTracking;
	private static TextView tvPebbleConnection;
	private static ToggleButton togglePebble;
	private static TextView tvTrackingStatus;
	public static ToggleButton toggleTracking;
	public static TextView txtStartTracking;
	public static LinearLayout lytHolder;

	public static void registerReceiver() {

		GPContextHolder.getMainAct().registerReceiver(broadcastReceiver, new IntentFilter(GPXService.BROADCAST_ACTION));
	}

	public FrgTrackingStats() {

	}

	public interface ITrackingActions {
		public void onTrackingToggle();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup statsView = (ViewGroup) inflater.inflate(R.layout.layout_stats, container, false);

		try {
			lytHolder = (LinearLayout) statsView.findViewById(R.id.lytHolder);
			tvPebbleConnection = (TextView) statsView.findViewById(R.id.tvPebbleConnection);
			txtLatitude = (TextView) statsView.findViewById(R.id.txtLatitudeDisplay);
			txtLongitude = (TextView) statsView.findViewById(R.id.txtLongitudeDisplay);
			txtSpeed = (TextView) statsView.findViewById(R.id.txtSpeedDisplay);
			txtTopSpeed = (TextView) statsView.findViewById(R.id.txtTopSpeedDisplay);
			mycurrentLocationText = (TextView) statsView.findViewById(R.id.myCurrentLocationTextDisplay);
			mycurrentLocationText.setTextColor(Color.WHITE);
			mycurrentLocationText.setTextSize(16);
			lytScrollView = (ScrollView) statsView.findViewById(R.id.scrollView1);
			txtAccuracyDisplay = (TextView) statsView.findViewById(R.id.txtAccuracyDisplay);
			txtCoordCount = (TextView) statsView.findViewById(R.id.txtCoordCountDisplay);
			txtDistance = (TextView) statsView.findViewById(R.id.txtDistanceDisplay);
			txtElapsedTimeDisplay = (TextView) statsView.findViewById(R.id.txtElapsedTimeDisplay);
			tblMainTableLayout = (TableLayout) statsView.findViewById(R.id.tblMainTableLayout);
			toggleTracking = (ToggleButton) statsView.findViewById(R.id.toggleTracking);
			toggleTracking.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mCallback.onTrackingToggle();
					startStopBtnVisibility();
				}

			});
			togglePebble = (ToggleButton) statsView.findViewById(R.id.togglePebble);
			// realTimeToggleTracking = (ToggleButton)
			// statsView.findViewById(R.id.realTimeToggleTracking);
			// realTimeToggleTracking.setVisibility(View.GONE);
			tvTrackingStatus = (TextView) statsView.findViewById(R.id.tvTrackingStatus);
			tvDistanceUom = (TextView) statsView.findViewById(R.id.tvDistanceUom);
			tvSpeedUom = (TextView) statsView.findViewById(R.id.tvSpeedUom);

			// tvGPSRealTimeTrackingService = (TextView)
			// statsView.findViewById(R.id.tvGPSRealTimeTrackingService);
			// tvGPSRealTimeTrackingService.setVisibility(View.GONE);
			setTrackingBool(); 
			setUomText();
			startStopBtnVisibility();

		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		return statsView;
	}

	private void setTrackingBool() {
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			isPebbleTracking = bundle.getBoolean("isPebbleTracking");
			trackingBool = bundle.getBoolean("trackingOnBool");
		}
	}

	public void setUomText() {
		tvDistanceUom.setText(GPShare.UNIT_OF_MEASURE_TXT_DIST);
		tvSpeedUom.setText(GPShare.UNIT_OF_MEASURE_TXT_SPEED);
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void abortBroadCast() {

		broadcastReceiver.abortBroadcast();
	}

	public void startStopBtnVisibility() {
		if (trackingBool) {
			lytHolder.setVisibility(View.VISIBLE);
			toggleTracking.setChecked(true);
			tvTrackingStatus.setText("Tracking On");
			registerReceiver();
		} else {
			toggleTracking.setChecked(false);
			tvTrackingStatus.setText("Tracking off");
			lytHolder.setVisibility(View.GONE);
		}

		if (isPebbleTracking) {
			lytHolder.setVisibility(View.VISIBLE);
			tvPebbleConnection.setText("Pebble Connected");
			togglePebble.setChecked(true);
		} else {
			togglePebble.setChecked(false);
			tvPebbleConnection.setText("Pebble Connection Off");

			if (!GPContextHolder.getMainAct().trackingOnBool) {
				lytHolder.setVisibility(View.GONE);
			}
		}
	}

	public static BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateGpsUI(intent);
		}
	};

	private static void updateGpsUI(Intent intent) {

		Bundle bund = intent.getExtras();
		String pointCount = bund.getString("pointCount");
		String lat = bund.getString("latitude");
		String longi = bund.getString("longitude");
		String distance = bund.getString("distance");
		String speed = bund.getString("speed");
		String elapsedTime = bund.getString("elapsedTime");
		String topSpeed = bund.getString("topSpeed");
		String accuracy = bund.getString("accuracy");

		txtSpeed.setText(speed);
		txtDistance.setText(distance);
		txtCoordCount.setText(pointCount);
		txtLongitude.setText(longi);
		txtLatitude.setText(lat);
		txtElapsedTimeDisplay.setText(elapsedTime);
		txtTopSpeed.setText(topSpeed);
		txtAccuracyDisplay.setText(accuracy);
		mycurrentLocationText.setText(GPContextHolder.getMainAct().getGprefs().LoadPreferences("currentPlace"));
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (ITrackingActions) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}

	public boolean isTrackingBool() {
		return trackingBool;
	}

	public void setTrackingBool(boolean trackingBool) {
		this.trackingBool = trackingBool;
	}

	public boolean isPebbleTracking() {
		return isPebbleTracking;
	}

	public void setPebbleTracking(boolean isPebbleTracking) {
		this.isPebbleTracking = isPebbleTracking;
	} 
 
}