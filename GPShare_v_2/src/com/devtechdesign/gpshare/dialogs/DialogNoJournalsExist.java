package com.devtechdesign.gpshare.dialogs;

import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.R;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

public class DialogNoJournalsExist extends DialogFragment implements OnEditorActionListener {

	private Button btnYes;
	private Button btnNo;

	public DialogNoJournalsExist() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.no_journals_exist, container);
		btnYes = (Button) view.findViewById(R.id.btnYes);
		btnYes.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				GPContextHolder.getMainAct().setJournalEditingView(null);
				getDialog().dismiss();
			}
		});

		btnNo = (Button) view.findViewById(R.id.btnNo);
		btnNo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getDialog().dismiss();
			}
		});

		getDialog().setTitle("No Journals Exist");

		return view;
	}

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {

		return false;
	}
}