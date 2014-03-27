package com.devtechdesign.gpshare.map;

import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.db.DatabaseControl;
import com.devtechdesign.gpshare.journals.JournalsManager.IJournalAction;
import com.devtechdesign.gpshare.layouts.vwJournal;
import com.dtd.dbeagen.db.elements.aJournals;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class MapFragEditJournalLoc extends Fragment {

	private SlidingPaneLayout sliderPane;
	private Button btnPlaces;
	private Button btnSave;
	private IEditJournalMapLoc mCallback;
	private vwJournal currentJournal;
	private Button btnCancel;

	public interface IEditJournalMapLoc {
		public void saveComplete();
	}

	public MapFragEditJournalLoc() {

	}

	public void setJournal(vwJournal vwJournal) {
		this.currentJournal = vwJournal;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.edit_journal_location, container, false);
		btnSave = (Button) rootView.findViewById(R.id.btnSave);
		btnCancel = (Button) rootView.findViewById(R.id.btnCancel);
		btnSave.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				DatabaseControl.getInstance().journalsDbc.updateJournals(currentJournal.journal);
				mCallback.saveComplete();
				Toast.makeText(v.getContext(), "Location Saved Successfully!", 2).show();
			}
		});
		btnCancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) { 
				mCallback.saveComplete();
				Toast.makeText(v.getContext(), "Saved cancelled", 2).show();
			}
		});
		return rootView;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			mCallback = (IEditJournalMapLoc) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}
}