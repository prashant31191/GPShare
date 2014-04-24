package com.devtechdesign.gpshare.journals;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.layouts.vwJournal;
import com.devtechdesign.gpshare.map.GpshareMapFragment;
import com.dtd.dbeagen.db.elements.aJournals;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class DialogJournalNoLoc {

	private GpshareMapFragment gpshareMapFragment;
	private GoogleMap mMap;
	private Marker journalMarker;
	private vwJournal currentJournal;
	private aJournals journal;

	public DialogJournalNoLoc(GpshareMapFragment gpshareMapFragment) {
		this.gpshareMapFragment = gpshareMapFragment;
		mMap = gpshareMapFragment.getmMap();
	}

	public void addJournal() {
		gpshareMapFragment.setAddingJournal(true);
		if (gpshareMapFragment.getCurrentLocation() != null) {
			gpshareMapFragment.removeCurrentLocationMarker();
			gpshareMapFragment.addJournalMarker();
		} else {
			showDialog();
		}
	}

	public void setJournal(vwJournal currentJournal) {
		this.currentJournal = currentJournal;
		this.journal = currentJournal.journal;
	}

	@SuppressWarnings("deprecation")
	public void showDialog() {
		AlertDialog alertDialog = new AlertDialog.Builder(GPContextHolder.getMainAct()).create();
		alertDialog.setTitle("Current GPS Coordinates unknown.");
		alertDialog.setMessage("Please Tap on map to specify location");
		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				Globals.getCurrentmMap().setOnMapClickListener(journalMapTapListener);
				gpshareMapFragment.setupViewEditingMapJournalLoc();
				dialog.dismiss();
			}
		});
		// Set the Icon for the Dialog
		alertDialog.setIcon(R.drawable.icon);
		alertDialog.show();
	}

	private LatLng journalLoc;
	OnMapClickListener journalMapTapListener = new OnMapClickListener() {

		@Override
		public void onMapClick(LatLng point) {
			journal.setx(String.valueOf(point.latitude));
			journal.sety(String.valueOf(point.longitude));
			setJournalLoc(point);
			gpshareMapFragment.removeJournalPin();
			gpshareMapFragment.addJournalMarker(currentJournal);
		}
	};

	public LatLng getJournalLoc() {
		return journalLoc;
	}

	public void setJournalLoc(LatLng journalLoc) {
		this.journalLoc = journalLoc;
	}
}
