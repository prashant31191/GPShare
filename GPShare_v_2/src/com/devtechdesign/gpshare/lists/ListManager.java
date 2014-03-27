package com.devtechdesign.gpshare.lists;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.devtechdesign.gpshare.R;

public class ListManager extends Fragment {

	protected Context context;
	protected ViewGroup vGroup;
	protected Button btnAdd;
	protected TextView txtPlaceManager;
	protected ListView lstManagedItems;
	protected LinearLayout lytExpandablePopup;

	public ListManager() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (vGroup == null) {
			vGroup = (ViewGroup) inflater.inflate(R.layout.places_manager, null, false);
			btnAdd = (Button) vGroup.findViewById(R.id.btnAdd);
			txtPlaceManager = (TextView) vGroup.findViewById(R.id.txtPlaceManager);
			lstManagedItems = (ListView) vGroup.findViewById(R.id.lstManagedItems);
			btnAdd.setOnClickListener(new OnClickListener() {
			 
				@Override
				public void onClick(View arg0) {
					add();
				}
			});
		} else {
			ViewGroup parent = (ViewGroup) vGroup.getParent();
			parent.removeAllViews();
		}
		return vGroup;
	}

	@Override
	public void onPause() { 
		super.onPause();
	}

	@Override
	public View getView() {
		return super.getView();
	}

	@Override
	public void onResume() { 
		super.onResume();
	}

	@Override
	public void onAttach(Activity activity) {
		if (lstManagedItems != null)
			lstManagedItems.invalidateViews();
		super.onAttach(activity);
	}

	protected void add() {
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public TextView getTxtPlaceManager() {
		return txtPlaceManager;
	}
 
	public void setTxtLabelViewForManager(int resourceId) {
		txtPlaceManager.setText(getText(resourceId));
	}
}
