package com.devtechdesign.gpshare.route;

import java.io.IOException;
import java.util.ArrayList;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import org.xmlpull.v1.XmlPullParserException;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.db.DBTransactions;
import com.devtechdesign.gpshare.data.db.RouteDatabase;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.data.db.RouteDatabase.Places;
import com.devtechdesign.gpshare.elements.aPlace;
import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.utility.SoapInterface;

public class Save_Place extends Activity implements SoapInterface {

	private EditText txtRouteName, txtPlaceName;
	private SharedPreferences sharedPreferences;
	private Editor editor;
	private static String CurrentUser = Globals.getCurrentUser();
	private static ArrayList<aPlace> myPlaces = new ArrayList<aPlace>();
	private static ProgressDialog mProgress;
	private boolean alreadyClearedTxtBox = false;
	private boolean alreadyRnClearedTxtBox = false;
	// private static Handler handler;
	private static Thread dataBaseThread;
	public static int runnableType, placeCount;
	public static String routeName;

	Transactions TransactionSet;
	private ListView lvPlaceList;
	private String[] placeList;
	private LinearLayout lytPlaceList;
	private String currentPlace;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.save_place);

		// intialize global variables
		Globals.setcurrentPlaceId("0");
		Globals.setRouteFolder("");
		Globals.setnewPlaceName("");

		// Transactions.getCurrentAddress();

		new Handler();

		TransactionSet = new Transactions(getApplicationContext());

		sharedPreferences = getSharedPreferences(SharedPref, 0);
		editor = sharedPreferences.edit();

		Bundle extras = getIntent().getExtras();

		routeName = extras.getString("routeName");
		currentPlace = extras.getString("currentPlace");

		txtPlaceName = (EditText) findViewById(R.id.txtPlaceName);
		txtRouteName = (EditText) findViewById(R.id.txtRouteName);

		lytPlaceList = (LinearLayout) findViewById(R.id.lytPlaceList);

		if (currentPlace.equals("")) {

			if (currentPlace == null && currentPlace.equals("")) {
				txtPlaceName.setText("Type Place Name");
			} else {
				txtPlaceName.setText(currentPlace);
			}
		} else {
			txtPlaceName.setText(currentPlace);
		}

		if (routeName == null || routeName.equals("")) {

			routeName = "Track " + String.valueOf(Transactions.getRouteCount());

			savePreferences("currentRouteName", routeName);
		}
		txtRouteName.setText(routeName);

		lvPlaceList = (ListView) findViewById(R.id.lvPlaceList);

		txtRouteName.clearFocus();
		txtPlaceName.clearFocus();
		lvPlaceList.requestFocus();

		if (placeList.length > 0) {
			// lvPlaceList.setAdapter(new PlaceListAdapter(this,
			// getPlacesFromPhoneDb()));

			lvPlaceList.setOnItemClickListener(placeListClickListener);
		} else {
			lytPlaceList.setVisibility(View.GONE);
		}

		Button btnSave = (Button) findViewById(R.id.btnSave);

		btnSave.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				String newPlaceName = ((TextView) txtPlaceName).getText().toString();
				if (!newPlaceName.equals("")) {
					newPlaceName = newPlaceName.substring(0, 1).toUpperCase() + newPlaceName.substring(1, newPlaceName.length()).replace("'", "");
				}

				if (newPlaceName.equals("") || newPlaceName.equals("Type Place Name")) {
					Toast.makeText(view.getContext(), "Please Type a new Place Name", 1).show();
				} else {
					DBTransactions.insertNewPlace(new aPlace(new String[] { "", newPlaceName, "" }));
					// set global txt file name variable for the current route.
					// This is accessable throughou the application.
					savePreferences("currentPlace", newPlaceName);
					aRoute newRoute = new aRoute(null);
					newRoute.setPlaceName(newPlaceName);
					newRoute.setSegmentKey(LoadPreferences("currentRouteSegmentDateKey"));
					newRoute.setRouteName(txtRouteName.getText().toString());
					savePreferences("currentRouteName", txtRouteName.getText().toString());
					Transactions.updateCurrentRoutePlace(newRoute);

					finish();

				}
			}
		});

		txtRouteName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (alreadyRnClearedTxtBox == false) {
					txtRouteName.setText("");
					alreadyRnClearedTxtBox = true;
				}
			}
		});

		txtPlaceName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (alreadyClearedTxtBox == false) {
					txtPlaceName.setText("");
					alreadyClearedTxtBox = true;
				}
			}
		});

		Button btnQuestion = (Button) findViewById(R.id.btnQuestion);
		btnQuestion.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {

				helpDialog();
			}
		});
	}

	private void deleteDatabaseRecords() {
		// DatabaseController.deleteDbContent();
		savePreferences("currentCoords", "");

		finish();
	}

	public String getCurrentCity() {
		String currentPlace = null;
		try {
			String[] xyary = Globals.getcurrentCoords().split(",");
			try {
				currentPlace = Transactions.getCityStateInfo(xyary[0], xyary[1]);
			} catch (ArrayIndexOutOfBoundsException e) {
				currentPlace = "";
			}
		} catch (NullPointerException e) {
		}
		return currentPlace;
	}

	public String getCityFromString(String address) {
		String[] addressAry = address.split(",");
		return addressAry[3];
	}

	public OnItemClickListener placeListClickListener = new OnItemClickListener() {
		public void onClick(View v) {
			// code to be
			// written to
			// handle the
			// click event
		}

		public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

			String selectedPlace = placeList[position];
			txtPlaceName.setText(selectedPlace);

		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		boolean disableEvent = false;

		System.out.println("********onKeyDown Called: " + keyCode + " " + KeyEvent.KEYCODE_BACK + "*********");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (!LoadPreferences("currentPlace").equals("")) {

				finish();
			} else {
				Toast.makeText(this, "You Must Create a Place!", 4).show();
				disableEvent = true;
			}
		}

		return disableEvent;
	}

	public void confirmationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("You have an existing route").setCancelable(false)

		.setPositiveButton("Start New Route", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				deleteDatabaseRecords();

			}
		})

		.setNegativeButton("Rename Current Route", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				// renameRouteAndRedirectToMain();

			}
		});

		AlertDialog alert = builder.create();
		alert.show();

	}

	private String LoadPreferences(String key) {
		return sharedPreferences.getString(key, "");
	}

	private static void showToast(String massage) {
		// Toast.makeText(this, massage, Toast.LENGTH_LONG).show();
	}

	protected static void fetchPlaces() {

		SoapObject request = new SoapObject(NAMESPACE, GETPLACE_METHOD_NAME);
		request.addProperty("userName", CurrentUser);
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url);
		envelope.setOutputSoapObject(request);

		try {
			androidHttpTransport.call(GET_PLACES_SOAP_ACTION, envelope);
			SoapObject b = (SoapObject) envelope.getResponse();

			int propertyCount = b.getPropertyCount();

			for (int i = 0; i < propertyCount; i++) {
				SoapObject input = (SoapObject) b.getProperty(i);
				PropertyInfo h = new PropertyInfo();
				String[] properties = new String[input.getPropertyCount()];

				for (int s = 0; s < input.getPropertyCount(); s++) {

					input.getPropertyInfo(s, h);
					System.out.println(h.toString());
					properties[s] = h.toString();

					System.out.println("****************" + h.toString() + "*************************");

				}
				myPlaces.add(new aPlace(properties));
				// new String [myPlaces.size()];
			}
		} catch (SoapFault e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			showToast("Cannot connect to the internet right now. List of places may not be up to date.");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			showToast("Cannot connect to the internet right now. List of places may not be up to date.");

		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void savePreferences(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	private static void insertAllPlaces() {
		for (int r = 0; r < myPlaces.size(); r++) {

			aPlace placeListOb = myPlaces.get(r);

			DBTransactions.insertNewPlace(placeListOb);
		}
	}

	protected void startInsertRunnable() {
		// show progress dialog
		// mProgress = ProgressDialog.show(this,
		// "Syncing Data...","Retrieving Online Data...");
		// mProgress = (ProgressDialog)onCreateDialog(1);
		// dataBaseThread = new fetchValueThread();
		// dataBaseThread.start();
	}

	// Save the thread
	@Override
	public Object onRetainNonConfigurationInstance() {
		return dataBaseThread;
	}

	// dismiss dialog if activity is destroyed
	@Override
	protected void onDestroy() {
		if (mProgress != null && mProgress.isShowing()) {
			mProgress.dismiss();
			mProgress = null;
		}
		super.onDestroy();
	}

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	//
	// MenuInflater inflater = getMenuInflater();
	// inflater.inflate(R.layout.save_route_menu, menu);
	//
	// // addCurrentLocation();
	//
	// return true;
	// }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection

		// DatabaseController.deleteDbRouteContent();

		return true;
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		ProgressDialog dialog = new ProgressDialog(this);

		switch (id) {
		case 1:
			((ProgressDialog) dialog).setIndeterminate(true);
			dialog.setCancelable(false);
			((ProgressDialog) dialog).setProgressStyle(ProgressDialog.STYLE_SPINNER);
			dialog.setMessage("Recording...");
			Message dialogStopListener = null;
			dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Stop", dialogStopListener);

			dialog.show();

			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	public void helpDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(
				"Assign your route to a place. Ex. Seattle. This allows you to organize routes under the category Places. "
						+ "You can have multiple routes per place. As you take Geotagged Images, they will also be categorized under the selected place. "
						+ "Routes and Pics are viewable through the map. After recording a route or taking a picture, click the map button on the main screen. Navigate to "
						+ "a place via the Places button on the top of the Map Screen. After clicking a place, you should see a list of routes on the left of the screen, "
						+ "and your images will be viewable through a scrollable gallery on the bottom of the map screen. Changing a place while a route is in progress can be achieved through the Main Screen's Menu")
				.setCancelable(false)

				.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dialog.dismiss();
					}
				});

		AlertDialog alert = builder.create();
		alert.show();
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}
}
