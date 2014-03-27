package com.devtechdesign.gpshare.trips;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.MainActivity;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.GPShareDataModel;
import com.devtechdesign.gpshare.data.db.DatabaseControl;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.dialogs.DialogAddPlace;
import com.devtechdesign.gpshare.dialogs.DialogAddTrip;
import com.devtechdesign.gpshare.journals.JournalsManager.IJournalAction;
import com.devtechdesign.gpshare.lists.IValidate;
import com.devtechdesign.gpshare.lists.ListManager;
import com.dtd.dbeagen.db.elements.aMyPlaces;
import com.dtd.dbeagen.db.elements.aTrip;

/**
 * PLaces manager is the base class for the places chooser used to choose a
 * place when right the record button is clicked. it is also the class that is
 * used to manage all places from anywhere in the application via the manager
 * list base class
 * 
 * @author Matthew Roberts
 * 
 */
public class TripsManager extends ListManager {

	private ITripManagerAction mCallback;

	public TripsManager() {

	}

	public interface ITripManagerAction {
		public void onTripClick(aTrip trip);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {

		GPShareDataModel.getInstance().trips = DatabaseControl.getInstance().tripDbc.getLocalTrip();
		super.setTxtLabelViewForManager(R.string.trips);
		lstManagedItems.setAdapter(new TripManagerAdapter(GPContextHolder.getMainAct(), R.layout.places_manager_item, GPShareDataModel.getInstance().trips));

		super.onStart();
	}

	@Override
	public void onResume() {

		if (lstManagedItems != null) {
			lstManagedItems.invalidateViews();
		}
		super.onResume();
	}

	@Override
	protected void add() {
		android.support.v4.app.FragmentManager fm = GPContextHolder.getMainAct().getSupportFragmentManager();
		DialogAddTrip d = new DialogAddTrip();
		d.setValidator(new ViewDataUpdate());
		d.show(fm, "fet");
		super.add();
	}

	public class TripManagerAdapter extends ArrayAdapter<aTrip> {

		List<aTrip> trips;

		public TripManagerAdapter(Context context, int resource, ArrayList<aTrip> objects) {
			super(context, resource, objects);
			trips = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			LayoutInflater li = null;
			Button btnOptions;
			if (v == null) {
				li = LayoutInflater.from(getContext());
				v = li.inflate(R.layout.places_manager_item, null);
			}

			final aTrip currentTrip = trips.get(position);

			if (currentTrip != null) {
				TextView text = (TextView) v.findViewById(R.id.txtPlace);
				text.setText(currentTrip.getTripName());
				btnOptions = (Button) v.findViewById(R.id.btnOptions);
				btnOptions.setTag(currentTrip); 
				btnOptions.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						showOptionsDialog(v);
					}
				});
				v.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						onItemClick(currentTrip);
					}
				});
			}
			return v;
		}
	}

	protected void showOptionsDialog(final View v) {

		AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		builder.setItems(R.array.place_options, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int item) {

				aTrip currentTrip;
				switch (item) {
				case 0:
					currentTrip = (aTrip) v.getTag();
					GPShareDataModel.getInstance().trips.remove(currentTrip);
					DatabaseControl.getInstance().tripDbc.deleteTripById(currentTrip.get_id());
					lstManagedItems.invalidateViews();
					dialog.dismiss();
					return;
				}
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	public class ViewDataUpdate implements IValidate {

		@Override
		public void onComplete(String place) {
			lstManagedItems.invalidateViews();
		}
	}

	protected void onItemClick(aTrip trip) {
		this.mCallback.onTripClick(trip);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (ITripManagerAction) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}
}
