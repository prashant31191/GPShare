package com.devtechdesign.gpshare;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.atomic.AtomicInteger;
import android.util.Log;
import com.devtechdesign.gpshare.LeftMenuSliderFragment.BtnJournalsSelectedListener;
import com.devtechdesign.gpshare.Images.CameraActForResult;
import com.devtechdesign.gpshare.Images.CustomCameraAct;
import com.devtechdesign.gpshare.data.db.DatabaseControl;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.facebook.SessionStore;
import com.devtechdesign.gpshare.journals.JournalsManager;
import com.devtechdesign.gpshare.journals.JournalsManager.IJournalAction;
import com.devtechdesign.gpshare.layouts.FrgEditJournals;
import com.devtechdesign.gpshare.layouts.FrgTrackingStats.ITrackingActions;
import com.devtechdesign.gpshare.layouts.FriendsManager;
import com.devtechdesign.gpshare.layouts.FrgTrackingStats;
import com.devtechdesign.gpshare.layouts.vwJournal;
import com.devtechdesign.gpshare.layouts.FrgEditJournals.IJournalsEditAction;
import com.devtechdesign.gpshare.map.GpshareMapFragment;
import com.devtechdesign.gpshare.map.GpshareMapFragment.IMapAction;
import com.devtechdesign.gpshare.map.MapFragAct;
import com.devtechdesign.gpshare.map.MapFragEditJournalLoc.IEditJournalMapLoc;
import com.devtechdesign.gpshare.places.PlacesManager;
import com.devtechdesign.gpshare.places.PlacesManager.IPlaceManagerAction;
import com.devtechdesign.gpshare.profile.Profile;
import com.devtechdesign.gpshare.profile.ProfileLayout;
import com.devtechdesign.gpshare.profile.imgs.Gallery;
import com.devtechdesign.gpshare.services.GPXService;
import com.devtechdesign.gpshare.sync.SyncService;
import com.devtechdesign.gpshare.trips.TripsManager;
import com.devtechdesign.gpshare.trips.TripsManager.ITripManagerAction;
import com.devtechdesign.gpshare.utility.GAnal;
import com.devtechdesign.gpshare.utility.GPsharePrefs;
import com.devtechdesign.gpshare.utility.SoapInterface;
import com.devtechdesign.gpshare.utility.Utility;
import com.dtd.dbeagen.db.elements.aJournals;
import com.dtd.dbeagen.db.elements.aMyPlaces;
import com.dtd.dbeagen.db.elements.aTrip;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import dev.tech.auth.DTDLogin;
import dev.tech.gpsharelibs.DTD;
import dev.tech.gpsharelibs.IDevTech;
import dev.tech.util.GLibTrans;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends BaseActivity implements SoapInterface, IDevTech, FragmentChangedListener, IJournalAction, BtnJournalsSelectedListener, IMapAction,
		IEditJournalMapLoc, IPlaceManagerAction, IJournalsEditAction, ITripManagerAction, ITrackingActions {

	public MainActivity() {
		super(R.string.app_name);
	}

	// set the current user for use in this class(saving pictures and
	// r..outes)///
	public static String currentUser;
	public String CurrentLayoutView;
	public static String currentDistance;
	public String currentSpeed, currentAltitude, currentCoordCount, currentPlace;
	public int tagCount = 0;
	public String notificationCount = "0";
	public static TextView txtLayoutStatus, txtName; 
	private static Handler handler = new Handler();
	private Thread gpxThread, profileThread;
	private ProgressDialog gpxDialog;
	private ProgressBar progressBarInbox;
	private SensorManager mySensorManager;
	public GAnal GoogleA;
	long startTime;
	long countUp;
	Chronometer stopWatch;
	public boolean trackingOnBool = false;
	public static final String APP_ID = "273395502707692";

	public LinearLayout layoutGeocamDescription;
	public ScrollView lytScrollView;
	// uncaught exception handler variable
	private UncaughtExceptionHandler defaultUEH;
	public LocationManager locManager;
	public Vibrator vibrator;
	public int publicV;
	public LinearLayout lytMain;
	public ImageView ivProfileImage, ivBanner;
	private LinearLayout lytLayoutStatusLeftButton;
	public static LinearLayout lytLayoutStatus;
	public static boolean glogalDataChanged;
	AtomicInteger msgId = new AtomicInteger();
	int i = 0;
	public boolean realTimeTrackingBool = false;
	private int routeCount;
	private GPsharePrefs devTechPrefs;
	private boolean pebbleTracking;
	private static GPsharePrefs gprefs;

	public static GPsharePrefs getGprefs() {
		return gprefs;
	}

	public static void setGprefs(GPsharePrefs gprefs) {
		MainActivity.gprefs = gprefs;
	}

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] drawerItems;
	private SlidingPaneLayout sliderPane;
	private GoogleMap mMap;
	GoogleMapOptions options = new GoogleMapOptions();
	private Fragment contentFrame;
	private LinearLayout lytJournalSaveLoc;
	private Button btnSaveJournalLoc;

	private ViewPager mPager;

	private GpshareMapFragment mapFrag;
	private PagerAdapter mPagerAdapter;
	private Fragment frgJournalsManager;
	private Gallery galleryFrag;
	private FrgTrackingStats frgTrackingStats;
	private static MainActivity gpshareMainAct;
	private FrgEditJournals lytJournalEditing;
	private PlacesManager frgPlacesManager;
	private FrgEditJournals journalFrag;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_frame);

		ActionBar aBar = getActionBar();
		aBar.setTitle("");
		aBar.setDisplayHomeAsUpEnabled(true);

		MainActivity.gpshareMainAct = this;

		DatabaseControl.getInstance().setContext(gpshareMainAct.getApplicationContext());

		getSlidingMenu().setMode(SlidingMenu.LEFT_RIGHT);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

		getSlidingMenu().setSecondaryMenu(R.layout.menu_frame_two);
		getSlidingMenu().setSecondaryShadowDrawable(R.drawable.shadowright);
		getSupportFragmentManager().beginTransaction().replace(R.id.menu_frame_two, new FriendsManager()).commit();

		initPrefs();

		if (DTD.getPersonId() == null) {
			// logOut();
			// temporarily setting person ID for testing
			DTD.setPersonId("192");
		}

		GPContextHolder.setMainAct(this);

		Profile p = new Profile(this);

		// /SET Person ID For use in the entire application
		Bundle bund = getIntent().getExtras();

		Utility.mFacebook = new Facebook(APP_ID);
		SessionStore.restore(Utility.mFacebook, this);
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		LocationManager locationManager;
		String context = Context.LOCATION_SERVICE;
		locationManager = (LocationManager) getSystemService(context);
		routeCount = Transactions.getRouteCount();

		GoogleA = new GAnal(getApplicationContext());

		GoogleA.recordPageView("MainControl");

		locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		checkGpsStatus();

		handler = new Handler();

		// if the app is a new version blank the credentials out to make the
		// user sign in again.
		versionControl();
		// getRealTimeTrackingOnBool();
		getPebbleTrackingBool();

		// new GCMHelper(this);

		CurrentLayoutView = "Friends";

		// /new GetNotificationsAndSyncTask().execute("test");

		lytMain = (LinearLayout) findViewById(R.id.lytMain);

		if (!gprefs.LoadPreferences("currentRouteDateKey").equals("")) {
			// if currentRouteDateKey does not equal blank, then there is a
			// route in progress or GPShare crashed/

			// if the service isn't already running then start it because it
			// crashed, if the currentRouteDateKey isn't blank and
			trackingOnBool = true;
			if (!isGpsServiceRunning(getApplicationContext()))
				startOrContinueGpxService(gprefs.LoadPreferences("currentRouteDateKey"));

		}
		setCurrentFragTracking();
		this.setFragment(Globals.getCurrentFrag());
		// startSyncService();
	}

	public void startSyncService() {
		Intent service = new Intent(getApplicationContext(), SyncService.class);
		startService(service);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		Log.v("GPShare", String.valueOf(item.getItemId()));
		switch (item.getItemId()) {
		case R.id.action_carmera:
			this.setCameraFragment();
			return true;
		case R.id.action_map:
			this.setCurrentFragMap(null);
			toggle();
			return true;
			// case R.id.action_search:
			//
			// return true;
		case android.R.id.home:
			toggle();
			return true;
		case R.id.action_friends:
			showSecondaryMenu();
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void setCameraFragment() {
		Intent cameraIntent = new Intent(this.getApplicationContext(), CameraActForResult.class);
		startActivity(cameraIntent);
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		return super.onPrepareOptionsMenu(menu);
	}

	static final int DRAWER_DELAY = 200;

	public void startCameraActivity() {
		try {

			Globals.getcurrentCoords().toString();

		} catch (NullPointerException e) {
			Globals.setcurrentCoords("0.0,0.0");
			Toast.makeText(gpshareMainAct, "Must have at least one GPS point. Press Start!!", 1).show();
		}

		GPContextHolder.getMainAct().GoogleA.recordClick("CamButton");
		Intent i = new Intent(gpshareMainAct, CustomCameraAct.class);
		gpshareMainAct.startActivity(i);
	}

	public void goToMapFragActivity() {
		GPContextHolder.getMainAct().GoogleA.recordClick("MapButton");
		Intent showMap = new Intent(gpshareMainAct, MapFragAct.class);
		showMap.putExtra("placeMarkType", "routeFromMain");
		showMap.putExtra("mapDataId", GPContextHolder.getMainAct().getGprefs().LoadPreferences("currentRouteSegmentDateKey"));
		gpshareMainAct.startActivity(showMap);
	}

	public void goToMapFragActivity(aJournals journal) {
		GPContextHolder.getMainAct().GoogleA.recordClick("MapButton");
		Intent showMap = new Intent(gpshareMainAct, MapFragAct.class);
		showMap.putExtra("journalId", journal.getJournalId());
		gpshareMainAct.startActivity(showMap);
	}

	public void toggleRealTimeTrackging(View v) {

		if (realTimeTrackingBool) {
			gprefs.savePreferences("realTimeTrackingOnBool", "false");
			getRealTimeTrackingOnBool();
			stopRealTimeTracking();
		} else {
			gprefs.savePreferences("realTimeTrackingOnBool", "true");
			getRealTimeTrackingOnBool();
			startRealTimeTracking();
		}
	}

	protected void startRealTimeTracking() {
		realTimeTrackingBool = true;
		String currentRouteDateKey = GLibTrans.getDateTime();
		stopGpsService();
		startGpsService();
	}

	protected void stopRealTimeTracking() {
		GoogleA.recordClick("StopRealTimeTrackingButton");
		confirmStopRealTimeTracking();
	}

	private void initPrefs() {
		gprefs = new GPsharePrefs(this, SharedPref);
		devTechPrefs = new GPsharePrefs(this, DTD.AuthPreferences);
		currentUser = gprefs.LoadPreferences("email");
		GPShare.loadUnitOfMeasure(gprefs.LoadPreferences("unitofmeasure"));
		currentCoordCount = gprefs.LoadPreferences("currentCoordCount");
		currentPlace = gprefs.LoadPreferences("currentPlace");
	}

	private void setFragment(Fragment fragment) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
	}

	static FriendsManager frgFriends = null;

	static JournalsManager frgEditJournals = null;

	//
	// @SuppressLint("ValidFragment")
	// public static class FrgJournalsManagerFrag extends Fragment {
	//
	// @Override
	// public View onCreateView(final LayoutInflater inflater, ViewGroup
	// container, Bundle savedInstanceState) {
	// if (frgEditJournals == null)
	// frgEditJournals = new JournalsManager();
	// return frgEditJournals.createView();
	// }
	//
	// @Override
	// public void onResume() {
	// ArrayList<aJournals> journalList =
	// DatabaseControl.getInstance().journalsDbc.getLocalJournals();
	//
	// if (journalList.size() == 0) {
	// android.support.v4.app.FragmentManager fm =
	// gpshareMainAct.getSupportFragmentManager();
	// DialogNoJournalsExist nje = new DialogNoJournalsExist();
	// nje.show(fm, "fragment_edit_name");
	// }
	// super.onResume();
	// }
	// }

	@Override
	public void onBackPressed() {
		if (mPager.getCurrentItem() == 0) {
			// If the user is currently looking at the first step, allow the
			// system to handle the
			// Back button. This calls finish() on this activity and pops the
			// back stack.
			super.onBackPressed();
		} else {
			// Otherwise, select the previous step.
			mPager.setCurrentItem(mPager.getCurrentItem() - 1);
		}
		super.onBackPressed();

	}

	static ProfileLayout profileLayout;

	@SuppressLint("ValidFragment")
	public static class ProfileFrag extends Fragment {

		@Override
		public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			if (profileLayout == null)
				profileLayout = new ProfileLayout(gpshareMainAct);

			return profileLayout.getView();
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

	public void versionControl() {

		try {
			if (Globals.getNewVersion().equals("true")) {
				gprefs.savePreferences("email", "");
				gprefs.savePreferences("personId", "");
				Globals.setnewVersion("false");
				logOut();
			}
		} catch (NullPointerException e) {

		}
	}

	private void logOut() {

		gprefs.savePreferences("email", "");
		this.devTechPrefs.savePreferences("personId", "");
		gprefs.savePreferences("currentRouteDateKey", "");
		gprefs.savePreferences("realTimeTrackingOnBool", "");

		Utility.userUID = null;
		stopGpsService();
		Intent loginIntent = new Intent(this, DTDLogin.class);
		startActivity(loginIntent);
		finish();
	}

	public void confirmStopRealTimeTracking() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you would like to stop?").setCancelable(false)

		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {

				stopGpsService();

				realTimeTrackingBool = false;
				frgTrackingStats.startStopBtnVisibility();

				glogalDataChanged = true;

				startOrContinueRegTracking();
				// beingTaggingConfirmation(localRouteKey);

			}
		})

		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				FrgTrackingStats.toggleTracking.setChecked(true);
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	// stops and restarts gps service to continue normal
	// tracking
	// but not post realtime udpates
	private void startOrContinueRegTracking() {
		if (trackingOnBool) {
			stopGpsService();
			startGpsService();
		}
	}

	public void stopRestartService() {
		if (isGpsServiceRunning(getApplicationContext())) {
			stopGpsService();
			startGpsService();
		}
	}

	protected void startOrContinueGpxService(String currentRouteDateKey) {

		if (checkGpsStatus() == true) {
			trackingOnBool = true;
			stopGpsService();
			startGpsService();
		}
	}

	public void stopGpsService() {
		Intent intent = new Intent(GPXService.ACTION_FOREGROUND);
		intent.setClass(getApplicationContext(), GPXService.class);
		stopService(intent);
		Intent gpsService = new Intent(getApplicationContext(), GPXService.class);
		stopService(gpsService);
	}

	public void startGpsService() {

		Intent intent = new Intent(GPXService.ACTION_FOREGROUND);
		intent.setClass(getApplicationContext(), GPXService.class);

		intent.putExtra("normalTracking", trackingOnBool);
		intent.putExtra("realTimeTracking", realTimeTrackingBool);
		intent.putExtra("pebbleTracking", pebbleTracking);
		intent.putExtra("realTimeInterval", 10000);
		startService(intent);

		Intent service = new Intent(getApplicationContext(), GPXService.class);
		startService(service);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		boolean disableEvent = false;

		// back button key down
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return disableEvent;
	}

	protected boolean checkGpsStatus() {
		if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			gpsStatusDialog();
			return false;
		}
		return true;
	}

	public void getPebbleTrackingBool() {
		String rttBool = gprefs.LoadPreferences("pebbleTrackingOnBool");
		if (rttBool.equals("true")) {
			setPebbleTracking(true);
			PebbleKit.startAppOnPebble(getApplicationContext(), Constants.SPORTS_UUID);
		} else {
			setPebbleTracking(false);
		}
	}

	public void getRealTimeTrackingOnBool() {
		String rttBool = gprefs.LoadPreferences("realTimeTrackingOnBool");
		if (rttBool.equals("true")) {
			realTimeTrackingBool = true;
		} else {
			realTimeTrackingBool = false;
		}
	}

	public void stopTracking() {
		GoogleA.recordClick("StopButton");
		confirmStopTracking();

	}

	public void confirmStopTracking() {

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you would like to stop?").setCancelable(false)

		.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int id) {

				// reload route list with new route
				// reloadRouteListWithNewRouteAfterTracking();

				String currentPlace = gprefs.LoadPreferences("currentPlace");
				String localRouteKey = gprefs.LoadPreferences("currentRouteDateKey");
				String currentRouteSegmentDateKey = gprefs.LoadPreferences("currentRouteSegmentDateKey");
				stopGpsService();

				trackingOnBool = false;

				Transactions.updateCurrentRouteRecord(localRouteKey, "false");

				frgTrackingStats.abortBroadCast();
				frgTrackingStats.setTrackingBool(false);

				gprefs.savePreferences("currentRouteDateKey", "");
				gprefs.savePreferences("currentRouteName", "");
				gprefs.savePreferences("currentPlace", "");

				glogalDataChanged = true;

				stopPebbleTracking();

				frgTrackingStats.startStopBtnVisibility();

				if (currentRouteSegmentDateKey != null) {
					// Intent routeSummary = new
					// Intent(getApplicationContext(),
					// TrackSummaryAct.class);
					// routeSummary.putExtra("routeKey",
					// currentRouteSegmentDateKey);
					// startActivity(routeSummary);

					new SynchRoutes().execute("");
				}
			}
		})

		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				FrgTrackingStats.toggleTracking.setChecked(true);
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	public boolean isPebbleTracking() {
		return pebbleTracking;
	}

	public void setPebbleTracking(boolean pebbleTracking) {
		this.pebbleTracking = pebbleTracking;
	}

	public void gpsStatusDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Your GPS is Disabled!!").setCancelable(false)

		.setPositiveButton("Turn on GPS", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				showGpsOptions();

			}
		})

		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private void showGpsOptions() {
		Intent gpsOptionsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
		startActivity(gpsOptionsIntent);
	}

	public boolean isGpsServiceRunning(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.devtechdesign.gpshare.services.GPXService".equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	public void startTracking() {
		// TODO Auto-generated method stub
		// for(int i = 0; i < latLongArray.length; i++)
		if (checkGpsStatus() == true) {
			String currentRouteDateKey = GLibTrans.getDateTime();
			String firstSegRouteDateKey = GLibTrans.getDateTime();
			String currentRouteSegmentDateKey = GLibTrans.getDateTime() + "Seg";

			gprefs.savePreferences("currentRouteDateKey", currentRouteDateKey);
			gprefs.savePreferences("currentRouteSegmentDateKey", currentRouteSegmentDateKey);
			gprefs.savePreferences("firstSegRouteDateKey", firstSegRouteDateKey);

			Transactions.createNewRoute("", gprefs.LoadPreferences("currentPlace"), "false", "", "", currentRouteDateKey, "true", currentRouteSegmentDateKey, "");
			GoogleA.recordClick("StartButton." + DTD.getPersonId());

			trackingOnBool = true;

			startOrContinueGpxService(currentRouteDateKey);
			frgTrackingStats.setTrackingBool(true);
		}
	}

	private Marker journalMarker;
	private vwJournal currentJournal;

	// @SuppressWarnings("unchecked")
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	//
	// int itemId = item.getItemId();
	//
	// if (itemId == R.id.menuRenamePlace) {
	//
	// startUpdateSaveRouteAct();
	// GoogleA.recordClick("MenuRenamePlaceMenu");
	// return true;
	// }
	//
	// if (itemId == R.id.menuLogout) {
	// logOut();
	// return true;
	// }
	//
	// if (itemId == R.id.menuSettings) {
	// // final String regid =
	// // gprefs.LoadPreferences(GPShare.transact.PROPERTY_REG_ID);
	// //
	// // new AsyncTask() {
	// //
	// // @Override
	// // protected Object doInBackground(Object... arg0) {
	// //
	// // if (Globals.getcurrentCoords() != null) {
	// // String xy[] = Globals.getcurrentCoords().split(",");
	// // Transactions.sendNotification(regid, Globals.getcurrentCoords(),
	// // DTD.getPersonId(), "{\"x\":\"" + xy[0]
	// // + "\",\"y\":\"" + xy[1] + "\"}", "0", getApplicationContext());
	// // }
	// //
	// // return null;
	// // }
	// //
	// // }.execute(null, null, null);
	//
	// GoogleA.recordClick("SettingsMenu");
	// startActivityForResult(new Intent(this, Settings.class), 1);
	// return true;
	// }
	//
	// if (itemId == R.id.menuInvite) {
	//
	// GoogleA.recordClick("InviteMenu");
	// if (!currentUser.equals("")) {
	// Intent friendRequest = new Intent(this, FriendInvite.class);
	// startActivity(friendRequest);
	// } else {
	// Toast.makeText(this, "Please login or Register with GPShare", 4)
	// .show();
	// Intent loginint = new Intent(this, LoginGPShare.class);
	// startActivity(loginint);
	// }
	// return true;
	// }
	//
	// if (itemId == R.id.menuUpdates) {
	// Intent AboutListIntent = new Intent(this, AboutList.class);
	// startActivity(AboutListIntent);
	// GoogleA.recordClick("UpdatesMenu");
	// return true;
	// }
	//
	// try {
	// if (itemId == R.id.menuShareLocation)
	//
	// GoogleA.recordClick("ShareLocationMenu");
	// shareLocation();
	// return true;
	//
	// } catch (NullPointerException e) {
	// Toast.makeText(
	// this,
	// "Current location unknown. Click Start to begin Tracking!!",
	// 1).show();
	// return false;
	// }
	// }
	//
	// protected void shareLocation() {
	//
	// StringBuilder sb = new StringBuilder();
	// sb.append(Globals.getcurrentCoords().replace(" ", ""));
	// sb.deleteCharAt(sb.length() - 1);
	//
	// Intent sharingIntent = new Intent(Intent.ACTION_SEND);
	// sharingIntent.setType("text/plain");
	// sharingIntent
	// .putExtra(
	// android.content.Intent.EXTRA_TEXT,
	// "My Current location: Click to View Loacion: http://maps.google.com/maps?t=m&q=loc:"
	// + sb + " http://www.GPShare.com");
	// startActivity(Intent.createChooser(sharingIntent,
	// "Share your location using"));
	// }

	public void stopPebbleTracking() {
		setPebbleTracking(false);
		gprefs.savePreferences("pebbleTrackingOnBool", "false");
		PebbleKit.closeAppOnPebble(getApplicationContext(), Constants.SPORTS_UUID);
	}

	public void startOrContinuePebbleTracking() {
		if (pebbleTracking) { // stops and restarts gps service
			// to continue realtime tracking // but not record a route
			stopGpsService();
			startGpsService();
		}
	}

	// private void reloadRouteListWithNewRouteAfterTracking() {
	// _adapter.getprofileLayout().getRouteLyt().setUpRouteListView();
	// _adapter.getprofileLayout().getmTabHost().setCurrentTab(0);
	// }

	public void finsihedTrackingConfirmation() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to Finish Tracking?").setCancelable(false)

		.setPositiveButton("Yes Please!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				// set tracking bool for use in
				// startStopBtnVisibility()
				trackingOnBool = false;

				// update database record to reflect a non
				// tracking state
				Transactions.updateCurrentRouteRecord(gprefs.LoadPreferences("currentRouteDateKey"), "false");
				Toast.makeText(getApplicationContext(), "The Route is Now Available for Viewing on the map!!", 1).show();
				// update preferences to
				gprefs.savePreferences("currentRouteDateKey", "");
				dialog.dismiss();

			}
		})

		.setNegativeButton("NO!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				startOrContinueGpxService(gprefs.LoadPreferences("currentRouteDateKey"));
				dialog.dismiss();
			}
		});

		AlertDialog alert = builder.create();
		alert.show();
	}

	private class SynchRoutes extends AsyncTask<String, Void, Void> {

		protected Void doInBackground(final String... args) {

			try {
				Transactions.initiateRouteOnlineDatabaseSync();
			} catch (NullPointerException e) {
			}
			return null;
		}
	}

	// private class PaneListener implements
	// SlidingPaneLayout.PanelSlideListener {
	//
	// @Override
	// public void onPanelClosed(View view) {
	// System.out.println("Panel closed");
	//
	// getFragmentManager().findFragmentById(R.id.leftpane).setHasOptionsMenu(false);
	// getFragmentManager().findFragmentById(R.id.content_frame).setHasOptionsMenu(true);
	// }
	//
	// @Override
	// public void onPanelOpened(View view) {
	// System.out.println("Panel opened");
	// getFragmentManager().findFragmentById(R.id.leftpane).setHasOptionsMenu(true);
	// getFragmentManager().findFragmentById(R.id.content_frame).setHasOptionsMenu(false);
	// }
	//
	// @Override
	// public void onPanelSlide(View view, float arg1) {
	// System.out.println("Panel sliding");
	// }
	//
	// }

	// public void setTrackFrag() {
	//
	// setFragment(new TrackFrag());
	//
	// }

	// public void setJournalManagerFrag() {
	// if (journalsManagerFrag == null)
	// journalsManagerFrag = new FrgJournalsManagerFrag();
	// setFragment(journalsManagerFrag);
	// }

	OnClickListener saveButtonClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			lytJournalSaveLoc.setVisibility(View.GONE);
			mMap.setOnMapClickListener(null);
			sliderPane.openPane();
		}
	};
	private TripsManager frgTripManager;
	private vwJournal vwJournal;

	@Override
	public void onFragmentChanged(String fragment) {

	}

	@Override
	public void onJournalsButtonSelected(int i) {
		switchFrag(i);
	}

	public void switchFrag(int i) {
		switch (i) {
		case GPShareConstants.TRACK:
			setCurrentFragTracking();
			break;
		case GPShareConstants.JOURNALS:
			setCurrentFragJournalsManager();
			break;
		case GPShareConstants.MAP:
			setCurrentFragMap(null);
			break;
		case GPShareConstants.IMAGES:
			setCurrentFragImageGallery();
			break;
		case GPShareConstants.SETTINGS:
			startActivityForResult(new Intent(this, Settings.class), 1);
			return;
		case GPShareConstants.PLACES_CLICK_JOURNAL:

			break;
		}
		setFragment(Globals.getCurrentFrag());
		toggle();
	}

	private void setCurrentFragMap(Bundle args) {
		if (mapFrag == null) {
			mapFrag = new GpshareMapFragment();
			mapFrag.setArguments(args);
		}
		Globals.setCurrentFrag(mapFrag);
	}

	private void setCurrentFragTracking() {
		if (frgTrackingStats == null) {
			frgTrackingStats = new FrgTrackingStats();
			Bundle args = new Bundle();
			args.putBoolean("trackingOnBool", trackingOnBool);
			args.putBoolean("isPebbleTracking", isPebbleTracking());
			frgTrackingStats.setArguments(args);
		}
		Globals.setCurrentFrag(frgTrackingStats);
	}

	private void setCurrentFragImageGallery() {
		if (galleryFrag == null)
			galleryFrag = new Gallery(null, null, i, i);
		Globals.setCurrentFrag(galleryFrag);
	}

	public void setCurrentFragJournalsManager() {
		if (frgJournalsManager == null)
			frgJournalsManager = new JournalsManager();
		Globals.setCurrentFrag(frgJournalsManager);
	}

	public void setCurrentFragJournalsEditor() {
		if (journalFrag == null)
			journalFrag = new FrgEditJournals(null, vwJournal);
		Globals.setCurrentFrag(journalFrag);
		setFragment(journalFrag);
	}

	public void setPlacesManagerFrag() {
		if (frgPlacesManager == null)
			frgPlacesManager = new PlacesManager();
		setFragment(frgPlacesManager);
		Globals.setCurrentFrag(frgPlacesManager);
	}

	public void setTripsManagerFrag() {
		if (frgTripManager == null)
			frgTripManager = new TripsManager();
		setFragment(frgTripManager);
		Globals.setCurrentFrag(frgTripManager);
	}

	public void setJournalEditingView(vwJournal j) {
		vwJournal = j;
		if (journalFrag == null)
			journalFrag = new FrgEditJournals(this.getApplicationContext(), j);
		else
			journalFrag.setJournal(j);
		Globals.setCurrentFrag(journalFrag);
		setFragment(journalFrag);
	}

	@Override
	public void relocateJournal(vwJournal j) {
		currentJournal = j;
		Bundle args = new Bundle();
		args.putString("relocateJournalId", j.journal._id);
		setCurrentFragMap(args);
		setFragment(Globals.getCurrentFrag());
		sliderPane.closePane();
	}

	@Override
	public void onJournalSelected(vwJournal position) {

	}

	@Override
	public void relocateJournalMapClick(LatLng latLong) {
		currentJournal.journal.setx(String.valueOf(latLong.latitude));
		currentJournal.journal.sety(String.valueOf(latLong.longitude));
		DatabaseControl.getInstance().journalsDbc.updateJournals(currentJournal.journal);
	}

	@Override
	public void saveComplete() {
		mapFrag.hideMapEditor();
	}

	@Override
	public void onPlaceClick(aMyPlaces place) {
		setCurrentFragJournalsEditor();
		journalFrag.setPlace(place);
	}

	@Override
	public void onCancelJournalEditButton() {
		this.setCurrentFragJournalsManager();
		this.setFragment(Globals.getCurrentFrag());
	}

	@Override
	public void onButtonJournalsEditButtonSave() {
		this.setCurrentFragJournalsManager();
		this.setFragment(Globals.getCurrentFrag());
	}

	@Override
	public void setPlacesFrag() {
		this.setPlacesManagerFrag();
	}

	@Override
	public void setTripFrag() {
		this.setTripsManagerFrag();
	}

	@Override
	public void onTripClick(aTrip trip) {
		setCurrentFragJournalsEditor();
		journalFrag.setTrip(trip);
	}

	@Override
	public void addJournalMarker(vwJournal j) {
		Bundle args = new Bundle();
		args.putString("journalId", j.journal._id);
		setCurrentFragMap(args);
		setFragment(Globals.getCurrentFrag());
	}

	@Override
	public void addNewJournal() {
		setJournalEditingView(new vwJournal());
	}

	@Override
	public void onDeletePlace(aMyPlaces place) {

	}

	@Override
	public void onTrackingToggle() {
		toggleTracking();
	}

	public void togglePebbleTracking() {
		if (!isPebbleTracking()) {
			setPebbleTracking(true);
			if (trackingOnBool) {
				GPXService.setPebbleTracking(true);
			} else {
				startTracking();
			}
			PebbleKit.startAppOnPebble(getApplicationContext(), Constants.SPORTS_UUID);
			gprefs.savePreferences("pebbleTrackingOnBool", "true");
		} else {
			setPebbleTracking(false);
			if (trackingOnBool) {
				GPXService.setPebbleTracking(false);
			} else {
				stopTracking();
			}

			gprefs.savePreferences("pebbleTrackingOnBool", "false");
			PebbleKit.closeAppOnPebble(getApplicationContext(), Constants.SPORTS_UUID);
		}
	}

	public void toggleTracking() {
		if (trackingOnBool) {
			stopTracking();
		} else {
			startTracking();
		}
	}
}