package com.devtechdesign.gpshare.journals;

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
import android.widget.TextView;
import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.GPShareDataModel;
import com.devtechdesign.gpshare.data.db.DatabaseControl;
import com.devtechdesign.gpshare.dialogs.DialogNoJournalsExist;
import com.devtechdesign.gpshare.layouts.vwJournal;
import com.devtechdesign.gpshare.lists.IValidate;
import com.devtechdesign.gpshare.lists.ListManager;
import com.dtd.dbeagen.db.elements.aJournals;

/**
 * PLaces manager is the base class for the places chooser used to choose a
 * place right the record button is clicked. it is also the class that is used
 * to manage all places from anywhere in the application via the managerlist
 * base class
 * 
 * @author Matthew Roberts
 * 
 */
public class JournalsManager extends ListManager {

	public JournalsManager() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onStart() {
		ArrayList<vwJournal> vwJournalList = DatabaseControl.getInstance().gpsDbc.getJournalsViaQuery();
		GPShareDataModel.getInstance().vwJournals = vwJournalList;
		lstManagedItems.setAdapter(new JournalsManagerAdapter(GPContextHolder.getMainAct(), R.layout.journals_manager_item, vwJournalList));
		super.setTxtLabelViewForManager(R.string.journals);

		if (vwJournalList.size() == 0) {
			android.support.v4.app.FragmentManager fm = this.getFragmentManager();
			DialogNoJournalsExist nje = new DialogNoJournalsExist();
			nje.show(fm, "fragment_edit_name");
		}
		super.onStart();
	}

	@Override
	public void onResume() {

		if (vGroup != null) {
			if (lstManagedItems != null)
				lstManagedItems.invalidateViews();
		}

		super.onResume();
	}

	@Override
	protected void add() {
		mCallback.addNewJournal();
		super.add();
	}

	public class JournalsManagerAdapter extends ArrayAdapter<vwJournal> {

		List<vwJournal> vwJournalList;

		public JournalsManagerAdapter(Context context, int resource, ArrayList<vwJournal> vwJournalList) {
			super(context, resource, vwJournalList);
			this.vwJournalList = vwJournalList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View v = convertView;
			LayoutInflater li = null;

			if (v == null) {
				li = LayoutInflater.from(getContext());
				v = li.inflate(R.layout.journals_manager_item, null);
			}

			final vwJournal vwJournal = vwJournalList.get(position);

			if (vwJournal != null) {
				TextView txText = (TextView) v.findViewById(R.id.txText);
				TextView tvDateTime = (TextView) v.findViewById(R.id.tvDateTime);
				Button btnJournalOptions = (Button) v.findViewById(R.id.btnJournalOptions);
				btnJournalOptions.setTag(vwJournal);

				tvDateTime.setText(vwJournal.journal.getCreateDate());

				String jText = vwJournal.journal.getText();

				if (jText.length() > 65)
					jText = jText.substring(0, 65) + "...";
				txText.setText(jText);
				btnJournalOptions.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						showOptionsDialog(v);
					}
				});
				v.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						mCallback.onJournalSelected((vwJournal) v.getTag());
					}
				});
			}
			return v;
		}
	}

	private vwJournal j;
	private IJournalAction mCallback;

	public void showOptionsDialog(final View v) {

		AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		builder.setItems(R.array.journal_options, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int item) {

				switch (item) {
				case 0:
					j = (vwJournal) v.getTag();
					mCallback.addJournalMarker(j);
					return;
				case 1:
					j = (vwJournal) v.getTag();
					GPContextHolder.getMainAct().setJournalEditingView(j);
					return;

				case 2:
					j = (vwJournal) v.getTag();
					mCallback.relocateJournal(j);
					lstManagedItems.invalidateViews();
					return;

				case 3:
					j = (vwJournal) v.getTag();
					DatabaseControl.getInstance().journalsDbc.deleteJournalsById(j.journal.get_id());
					GPShareDataModel.getInstance().vwJournals.remove(j);
					lstManagedItems.invalidateViews();
					return;
				}
			}
		});

		AlertDialog dialog = builder.create();
		dialog.show();
	}

	// Container Activity must implement this interface
	public interface BtnJournalsSelectedListener {
		public void onJournalsButtonSelected(int i);
	}

	public class ViewDataUpdateJournal implements IValidate {

		@Override
		public void onComplete(String place) {
			lstManagedItems.invalidateViews();
		}
	}

	// MainActivity must implement this interface
	public interface IJournalAction {
		public void relocateJournal(vwJournal j);

		/**
		 * Add Journal Marker to map, if coords are null, Dialog pops up
		 * 
		 * @param j
		 */
		public void addJournalMarker(vwJournal j);

		public void addNewJournal();

		public void onJournalSelected(vwJournal vwJournal);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (IJournalAction) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	} 
}
