package com.devtechdesign.gpshare;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class LeftMenuSliderFragment extends Fragment {

	private SlidingPaneLayout sliderPane;
	private Button btnPlaces;
	private Button btnJournals;
	private BtnJournalsSelectedListener callBackBtnJournals;
	private Button btnMap;
	private Button btnTrips;
	private Button btnImages;
	private View btnSettings;
	private Button btnTrack;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.frag_left_main, container, false);
		btnJournals = (Button) rootView.findViewById(R.id.btnJournals);
		btnMap = (Button) rootView.findViewById(R.id.btnMap);
		btnTrips = (Button) rootView.findViewById(R.id.btnTrips);
		btnImages = (Button) rootView.findViewById(R.id.btnImages);
		btnSettings = (Button) rootView.findViewById(R.id.btnSettings);
		btnTrack = (Button) rootView.findViewById(R.id.btnTrack);
		btnTrack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callBackBtnJournals.onJournalsButtonSelected(GPShareConstants.TRACK);
			}

		});
		btnMap.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callBackBtnJournals.onJournalsButtonSelected(GPShareConstants.MAP);
			}

		});
		btnJournals.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callBackBtnJournals.onJournalsButtonSelected(GPShareConstants.JOURNALS);
			}

		});
		btnTrips.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callBackBtnJournals.onJournalsButtonSelected(GPShareConstants.TRIP);
			}

		});
		btnImages.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callBackBtnJournals.onJournalsButtonSelected(GPShareConstants.IMAGES);
			}

		});
		btnSettings.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callBackBtnJournals.onJournalsButtonSelected(GPShareConstants.SETTINGS);
			}

		});
		return rootView;
	}

	// Container Activity must implement this interface
	public interface BtnJournalsSelectedListener {
		public void onJournalsButtonSelected(int i);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			callBackBtnJournals = (BtnJournalsSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}
}