package com.devtechdesign.gpshare.layouts;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.GPShareDataModel;
import com.devtechdesign.gpshare.data.db.DatabaseControl;
import com.devtechdesign.gpshare.places.PlacesManager.IPlaceManagerAction;
import com.dtd.dbeagen.db.elements.aJournals;
import com.dtd.dbeagen.db.elements.aMyPlaces;
import com.dtd.dbeagen.db.elements.aTrip;

import dev.tech.util.GLibTrans;

@SuppressLint("ValidFragment")
public class FrgEditJournals extends Fragment {

	LinearLayout statsLayout;
	private Button btnSave;
	private EditText txtTrip;
	private EditText txtPlace;
	private EditText txtActivity;
	private EditText txtJournalText;
	private Button btnCancel;
	private vwJournal vwJournal;
	private String actId;
	private String placeId;
	private String tripId;
	private Object TextView;
	private android.widget.TextView txtDateTime;
	private LocationManager location;
	private aJournals currentJournal;
	private static LinearLayout lytTopControls;
	public static ToggleButton toggleTracking;
	public static TextView txtStartTracking;
	public static LinearLayout lytHolder;

	public FrgEditJournals(vwJournal avwJournal) {
		this.vwJournal = avwJournal; 
	}

	public FrgEditJournals(Context applicationContext, vwJournal j) {
		if (vwJournal == null)
			vwJournal = new vwJournal();
		vwJournal = j;
	}

	@Override
	public View onCreateView(LayoutInflater li, ViewGroup vGroup, Bundle savedInstanceState) {

		vGroup = (ViewGroup) li.inflate(R.layout.layout_journals, null, false);
		btnSave = (Button) vGroup.findViewById(R.id.btnSave);
		txtTrip = (EditText) vGroup.findViewById(R.id.txtTrip);
		txtPlace = (EditText) vGroup.findViewById(R.id.txtPlace);
		txtActivity = (EditText) vGroup.findViewById(R.id.txtActivity);
		txtJournalText = (EditText) vGroup.findViewById(R.id.txtJournalText);
		btnCancel = (Button) vGroup.findViewById(R.id.btnCancel);
		txtDateTime = (TextView) vGroup.findViewById(R.id.txtDateTime);

		setTextControls();

		startListeningForLoc();

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				saveJournal();
			}
		});

		if (vwJournal != null)
			setjournalControls();

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mCallback.onCancelJournalEditButton();
			}
		});
		txtTrip.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mCallback.setTripFrag();
				}
			}
		});
		txtPlace.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mCallback.setPlacesFrag();
				}
			}
		});

		return vGroup;
	}

	private void startListeningForLoc() {
		location = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setBearingAccuracy(Criteria.ACCURACY_FINE);
		criteria.setHorizontalAccuracy(Criteria.ACCURACY_FINE);
		criteria.setSpeedAccuracy(Criteria.ACCURACY_FINE);
		criteria.setVerticalAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setSpeedRequired(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setSpeedAccuracy(Criteria.ACCURACY_FINE);

		String best = location.getBestProvider(criteria, true);
		location.requestLocationUpdates(best, 2000, 10, locationListener);

	}

	private void setTextControls() {
		if (vwJournal == null || vwJournal.journal == null) {
			txtDateTime.setText(GLibTrans.getDateTime());
		}
	}

	// Your created method
	public static void onBackPressed() {
		lytTopControls.setVisibility(View.VISIBLE);
	}

	private void setjournalControls() {
		if (vwJournal.trip != null) {
			txtTrip.setText(vwJournal.trip.getTripName());
		}
	}

	public void setTextPlace(String placeName) {
		vwJournal.placeName = placeName;
	}

	public void revalidateControls() {
		if (vwJournal.journal.Text != null)
			this.txtJournalText.setText(vwJournal.journal.Text);
		if (vwJournal.journal.CreateDate == null)
			vwJournal.journal.CreateDate = GLibTrans.getDateTime();
		this.txtDateTime.setText(vwJournal.journal.CreateDate);
		this.txtTrip.setText(vwJournal.trip.TripName);
		this.txtPlace.setText(vwJournal.place.PlaceName);
	}

	protected void saveJournal() {
		if (txtTrip.getText().toString().equals("")
				&& (txtPlace.getText().toString().equals("") && txtActivity.getText().toString().equals("") && (txtJournalText.getText().toString().equals("")))) {
			Toast.makeText(activity, "No data entered. No Journal was saved.", 2).show();
		} else {

			vwJournal.journal.Text = this.txtJournalText.getText().toString();
			vwJournal.journal.CreateDate = txtDateTime.getText().toString();

			if (vwJournal.journal.x == null) {
				vwJournal.journal.setx(Globals.getLat());
				vwJournal.journal.sety(Globals.getLongi());
			}
 
			vwJournal.journal.setText(vwJournal.journal.Text);
			vwJournal.journal.setCreateDate(vwJournal.journal.CreateDate);
			vwJournal.journal.setDirty("true");

			if (vwJournal.journal._id == null) {
				DatabaseControl.getInstance().journalsDbc.insertNewJournals(vwJournal.journal);
			} else {
				DatabaseControl.getInstance().journalsDbc.updateJournals(vwJournal.journal);
				GPShareDataModel.getInstance().journals.add(vwJournal.journal);
			}

			Toast.makeText(activity, "Journal saved successfully", 2).show();
			mCallback.onButtonJournalsEditButtonSave();
		}
	}

	@Override
	public void onResume() {
		revalidateControls();
		super.onResume();
	}

	public void setJournal(vwJournal j) {
		vwJournal = j;
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {

			Double locLat = location.getLatitude();
			Double locLongi = location.getLongitude();
			String angle = String.valueOf(location.getBearing());
			String lat = String.valueOf(locLat);
			String longi = String.valueOf(locLongi);
			Globals.setLat(lat);
			Globals.setLongi(longi);
		}

		public void onProviderDisabled(String provider) {

		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
	private Activity activity;
	private IJournalsEditAction mCallback;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		try {
			mCallback = (IJournalsEditAction) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		} 
	}

	public interface IJournalsEditAction {
		public void onCancelJournalEditButton();

		public void setPlacesFrag();

		public void setTripFrag();

		public void onButtonJournalsEditButtonSave();
	}

	public void setTrip(aTrip trip) {
		this.vwJournal.trip = trip;
		this.vwJournal.journal.TripId = trip._id;
	}

	public void setJournal(aJournals j) {

	}

	public void setPlace(aMyPlaces place) {
		this.vwJournal.place = place;
		this.vwJournal.journal.PlaceId = place._id;
	}
}