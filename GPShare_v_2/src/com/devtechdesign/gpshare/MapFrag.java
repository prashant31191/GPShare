package com.devtechdesign.gpshare;

import com.devtechdesign.gpshare.map.GpshareMapFragment;
 
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapFrag extends Fragment {
	static GpshareMapFragment mapFrag;

	@Override
	public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		return mapFrag.getView();
	}

}
