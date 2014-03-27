package com.devtechdesign.gpshare.places;

import java.util.ArrayList;

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
import android.widget.TextView; 
import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.GPShareDataModel;
import com.devtechdesign.gpshare.data.db.DatabaseControl;
import com.devtechdesign.gpshare.dialogs.DialogAddPlace;
import com.devtechdesign.gpshare.lists.IValidate;
import com.devtechdesign.gpshare.lists.ListManager;
import com.dtd.dbeagen.db.elements.aMyPlaces;

/**
 * PLaces manager is the base class for the places chooser used to choose a
 * place when right the record button is clicked. it is also the class that is
 * used to manage all places from anywhere in the application via the manager
 * list base class
 * 
 * @author Matthew Roberts
 * 
 */
public class PlacesManager extends ListManager {

	private IPlaceManagerAction mCallback;

	public PlacesManager() {

	}

	public interface IPlaceManagerAction {
		public void onPlaceClick(aMyPlaces place);

		public void onDeletePlace(aMyPlaces place);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		
		GPShareDataModel.getInstance().places = DatabaseControl.getInstance().myplacesDbc.getLocalMyPlaces();
		lstManagedItems.setAdapter(new PlaceManagerAdapter(GPContextHolder.getMainAct(), R.layout.places_manager_item, GPShareDataModel.getInstance().places));
		super.setTxtLabelViewForManager(R.string.places);
		
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
		DialogAddPlace d = new DialogAddPlace();
		d.setValidator(new ViewDataUpdate());
		d.show(fm, "fet");
		super.add();
	}

	public class PlaceManagerAdapter extends ArrayAdapter<aMyPlaces> {

		ArrayList<aMyPlaces> items;
		private Button btnOptions;

		public PlaceManagerAdapter(Context context, int resource, ArrayList<aMyPlaces> places) {
			super(context, resource, places);
			items = places;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			LayoutInflater li = null;

			if (v == null) {
				li = LayoutInflater.from(getContext());
				v = li.inflate(R.layout.places_manager_item, null);
				btnOptions = (Button) v.findViewById(R.id.btnOptions);
			}

			final aMyPlaces place = items.get(position);

			if (place != null) {
				TextView text = (TextView) v.findViewById(R.id.txtPlace);
				text.setText(place.getPlaceName());
				v.setTag(place);
				btnOptions.setTag(place);
				btnOptions.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						showOptionsDialog(v);
					}
				});

				v.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						onItemClick(place);
					}
				});
			}
			return v;
		}

		protected void showOptionsDialog(final View v) {

			AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
			builder.setItems(R.array.place_options, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int item) {

					aMyPlaces currentPlace;
					switch (item) {
					case 0:
						currentPlace = (aMyPlaces) v.getTag();
						GPShareDataModel.getInstance().places.remove(currentPlace);
						DatabaseControl.getInstance().myplacesDbc.deleteMyPlacesById(currentPlace.get_id());
						lstManagedItems.invalidateViews();
						dialog.dismiss(); 
						return;
					}
				}
			});

			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

	protected void onItemClick(aMyPlaces place) {
		this.mCallback.onPlaceClick(place);
	}

	public class ViewDataUpdate implements IValidate {

		@Override
		public void onComplete(String place) {
			lstManagedItems.invalidateViews();
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (IPlaceManagerAction) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}
}
