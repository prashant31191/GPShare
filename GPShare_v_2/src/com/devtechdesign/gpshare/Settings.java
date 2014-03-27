package com.devtechdesign.gpshare;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.devtechdesign.gpshare.data.db.DatabaseControl;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.utility.WebTransactions;

import dev.tech.auth.DTDLogin;
import dev.tech.gpsharelibs.DTD;

public class Settings extends Activity implements MySoapInterface {

	private static SharedPreferences sharedPreferences;
	private static Editor editor;
	private String internetBool;
	public CheckBox cbInternet;
	public Button btnChangeProfilePic, btnSyncOnlineData;
	private CheckBox cbPublicRank;
	private Button btnDbViewer;
	private String unitofmeasure;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		sharedPreferences = getSharedPreferences(SharedPref, 0);
		editor = sharedPreferences.edit();

		internetBool = LoadPreferences("useInternet");
		unitofmeasure = LoadPreferences("unitofmeasure");

		Button btnDone = (Button) findViewById(R.id.btnDone);
		cbInternet = (CheckBox) findViewById(R.id.cbInternet);
		btnChangeProfilePic = (Button) findViewById(R.id.btnChangeProfilePic);
		cbPublicRank = (CheckBox) findViewById(R.id.cbPublicRank);
		btnSyncOnlineData = (Button) findViewById(R.id.btnSyncOnlineData);
		btnDbViewer = (Button) findViewById(R.id.btnDbViewer);

		if (!DTD.getPersonId().equals("192")) {
			btnSyncOnlineData.setVisibility(View.GONE);
			btnDbViewer.setVisibility(View.GONE);
		}

		setSettingsStatus();

		btnChangeProfilePic.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {

				if (!LoadPreferences("personId").equals("")) {
					Intent GalleryIntent = new Intent(getApplicationContext(), ProfilePicSelection.class);
					startActivity(GalleryIntent);
					finish();
				} else {
					Intent GalleryIntent = new Intent(getApplicationContext(), DTDLogin.class);
					startActivity(GalleryIntent);
					Toast.makeText(v.getContext(), "You must login or register through GPShare", 2).show();
					finish();
				}
			}
		});

		cbInternet.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;

				if (cb.isChecked()) {

					savePreferences("useInternet", "true");
					createToast("Internet has been Enabled");
				} else {
					savePreferences("useInternet", "false");
					createToast("Internet has been Disabled");
				}
			}
		});

		btnDone.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// startActivity(new Intent(Settings.this, MainControl.class));
				GPContextHolder.getMainAct().stopRestartService();
				GPShare.loadUnitOfMeasure(LoadPreferences("unitofmeasure"));
				GPContextHolder.getFrgStrackingStats().setUomText();
				finish();
			}
		});
	}

	protected void setSettingsStatus() {
		if (internetBool.equals("true")) {
			cbInternet.setChecked(true);
		} else {
			cbInternet.setChecked(false);
		}
		setPublicProfileCb();
	}

	protected void createToast(String message) {
		Toast.makeText(this, message, 1).show();
	}

	/*
	 * in if you need username or password stored as global variable you can use
	 * LoadPreferences(key),where key = "mail" if you need retrieve username and
	 * key = "pass" if you need retrieve password
	 */
	private String LoadPreferences(String key) {
		String value = sharedPreferences.getString(key, "");
		return value;
	}

	private void savePreferences(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public void syncWithOnlineData(View v) {
		new SyncWithOnlineData().execute("");
	}

	public void updatePublicProfileBool(View v) {

		new CheckPublicProfile().execute();//

	}

	public String currentRankStatus() {
		if (LoadPreferences("publicProfile").equals("") || LoadPreferences("publicProfile").equals("false")) {
			return "true";
		} else {
			return "false";
		}
	}

	public void setPublicProfileCb() {
		if (LoadPreferences("publicProfile").equals("") || LoadPreferences("publicProfile").equals("true")) {
			this.cbPublicRank.setChecked(true);
		} else {
			this.cbPublicRank.setChecked(false);
		}
	}

	private class SyncWithOnlineData extends AsyncTask<String, Void, Void> {

		@Override
		protected Void doInBackground(String... params) {

			// Transactions.syncWithOnlinePictures();
			DatabaseControl.getInstance().gpsDbc.syncWithOnlinePictures();
			// Transactions.syncWithOnlineRouteData();
			// Transactions.syncWithOnlinePlaces();
			// CameraView.syncOldpicsIntoThumbnails();
			return null;
		}

		@Override
		public void onPostExecute(Void result) {
			System.out.println("FINISHED SYNCING WITH ONLINE DATA!!!!!");
		}

		@Override
		protected void onProgressUpdate(Void... cPair) {

			System.out.println("reporting progress while loading listview");
		}
	}

	public void btnUomClick(View v) {
		final CharSequence[] items = { "Imperial", "Metric" };

		AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this);// ERROR
																				// ShowDialog
																				// cannot
																				// be
																				// resolved
																				// to
																				// a
																				// type
		builder.setTitle("Set Unit of Measure");
		builder.setSingleChoiceItems(items, determineUnitsOfMeasure(), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {

				switch (item) {
				case 0:
					savePreferences("unitofmeasure", "0");
					GPShare.loadUnitOfMeasure("0");
					Toast.makeText(Settings.this, "UOM Saved as Imperial", Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				case 1:
					savePreferences("unitofmeasure", "");
					GPShare.loadUnitOfMeasure("");
					Toast.makeText(Settings.this, "UOM Saved as metric", Toast.LENGTH_SHORT).show();
					dialog.dismiss();
					return;
				}
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private int determineUnitsOfMeasure() {
		String uom = LoadPreferences("unitofmeasure");
		if (uom.equals("")) {
			return 1;
		} else {
			return 0;
		}

	}

	public void onPostExecute(Void result) {
		// TODO Auto-generated method stub
		System.out.println("FINISHED SYNCING WITH ONLINE DATA!!!!!");
	}

	public class CheckPublicProfile extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return WebTransactions.updatePublicProfileBool(LoadPreferences("personId"), currentRankStatus());
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				if (LoadPreferences("publicProfile").equals("") || LoadPreferences("publicProfile").equals("true")) {
					savePreferences("publicProfile", "false");
				} else {
					savePreferences("publicProfile", "true");
				}
				Toast.makeText(getApplicationContext(), "Your rank setting has been updated", 2).show();
				setPublicProfileCb();
			}
			super.onPostExecute(result);
		}
	}
}