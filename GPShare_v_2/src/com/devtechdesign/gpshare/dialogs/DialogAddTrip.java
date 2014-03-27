package com.devtechdesign.gpshare.dialogs;

import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.GPShareDataModel;
import com.devtechdesign.gpshare.data.db.DBTransactions;
import com.devtechdesign.gpshare.data.db.DatabaseControl;
import com.devtechdesign.gpshare.elements.aPlace;
import com.devtechdesign.gpshare.layouts.FrgEditJournals.IJournalsEditAction;
import com.devtechdesign.gpshare.lists.IValidate;
import com.devtechdesign.gpshare.trips.TripsManager.ViewDataUpdate;
import com.dtd.dbeagen.db.elements.aTrip;

import android.app.Activity;
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

public class DialogAddTrip extends DialogFragment implements OnEditorActionListener {

	private Button btnSave;
	private Button btnCancel;
	private EditText etEditPlace; 
	private ViewDataUpdate viewDataUpdate;

	public DialogAddTrip() {
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
				String tripName = etEditPlace.getText().toString();
				if (!tripName.equals("")) {
					aTrip t = new aTrip();
					t.setTripName(tripName);
					long id = DatabaseControl.getInstance().tripDbc.insertNewTrip(t);
					t._id = String.valueOf(id); 
					GPShareDataModel.getInstance().trips.add(t);
					viewDataUpdate.onComplete(null);
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

	//
	// public interface IAddTripDialogAction {
	// public void addTripOnComplete();
	// }
	//
	// @Override
	// public void onAttach(Activity activity) {
	// super.onAttach(activity);
	// try {
	// mCallback = (IAddTripDialogAction) activity;
	// } catch (ClassCastException e) {
	// throw new ClassCastException(activity.toString() +
	// " must implement OnHeadlineSelectedListener");
	// }
	// }

	public void setValidator(ViewDataUpdate viewDataUpdate) {
		this.viewDataUpdate = viewDataUpdate;
	} 
}