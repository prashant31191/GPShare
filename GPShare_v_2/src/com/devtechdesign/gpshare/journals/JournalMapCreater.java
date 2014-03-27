package com.devtechdesign.gpshare.journals;

import java.util.ArrayList;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.map.GpshareMapFragment;
import com.devtechdesign.gpshare.map.MapFragAct;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class JournalMapCreater extends LinearLayout {

	ArrayList<LatLng> aryCoords = new ArrayList<LatLng>();
	private LinearLayout lLayout; 
	private Button btnSave;
	private Button btnCancel;

	private GoogleMap mMap;
	PolylineOptions rectOptionsLocToFront;
	Polyline layerPolyLineLocToFront;
	PolylineOptions rectOptions;
	private GpshareMapFragment mapFrag;

	public JournalMapCreater(Context context, final GpshareMapFragment mapFrag) {
		super(context);
		this.mapFrag = mapFrag;
		mMap = mapFrag.getmMap();

		LayoutInflater li = LayoutInflater.from(context);

		lLayout = (LinearLayout) li.inflate(R.layout.lyt_add_journal, this);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnCancel = (Button) findViewById(R.id.btnCancel);

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

			}
		});
	}

	protected aJournal initJournal() {
		LatLng journalLatLng = mapFrag.getJournalNew().getJournalLoc();

		return null;

	}
}
