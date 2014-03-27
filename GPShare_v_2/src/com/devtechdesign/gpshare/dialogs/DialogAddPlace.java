package com.devtechdesign.gpshare.dialogs;

import java.util.ArrayList;

import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.GPShareDataModel;
import com.devtechdesign.gpshare.data.db.DBTransactions;
import com.devtechdesign.gpshare.data.db.DatabaseControl;
import com.devtechdesign.gpshare.elements.aPlace;
import com.devtechdesign.gpshare.lists.IValidate;
import com.devtechdesign.gpshare.places.PlacesManager.ViewDataUpdate;
import com.dtd.dbeagen.db.elements.aMyPlaces;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.OnEditorActionListener;

public class DialogAddPlace extends DialogFragment implements OnEditorActionListener {

	private Button btnSave;
	private Button btnCancel;
	private EditText etEditPlace;
	private IValidate viewDataUpdateJournal;

	public DialogAddPlace() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.add_place, container);
		btnSave = (Button) view.findViewById(R.id.btnSave);
		btnCancel = (Button) view.findViewById(R.id.btnCancel);
		etEditPlace = (EditText) view.findViewById(R.id.etEditPlace);

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String placeName = etEditPlace.getText().toString();
				if (!placeName.equals("")) {
					aMyPlaces place = new aMyPlaces(); 
					place.setPlaceName(etEditPlace.getText().toString());
					long newId = DatabaseControl.getInstance().myplacesDbc.insertNewMyPlaces(place);
					place._id = String.valueOf(newId); 
					GPShareDataModel.getInstance().places.add(place); 
					ArrayList<aMyPlaces> places = DatabaseControl.getInstance().myplacesDbc.getLocalMyPlaces();
							
					viewDataUpdateJournal.onComplete(placeName);
					getDialog().dismiss();
				} else {
					Toast.makeText(v.getContext(), "Place not entered", 2).show();
				}
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getDialog().dismiss();
			}

		});
		getDialog().setTitle("Enter Place");

		return view;
	}

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {

		return false;
	}

	public void setValidator(IValidate viewDataUpdate) {
		this.viewDataUpdateJournal = viewDataUpdate;
	}
}