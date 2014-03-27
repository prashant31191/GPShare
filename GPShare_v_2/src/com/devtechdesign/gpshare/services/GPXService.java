package com.devtechdesign.gpshare.services;

import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Date;
import java.text.DecimalFormat; 
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast; 
import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.MainActivity;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.utility.SoapInterface;
import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;
import dev.tech.util.GLibTrans;

public class GPXService extends Service implements SoapInterface {

	public static DecimalFormat twoDForm = new DecimalFormat("#.#");
	private LocationManager location = null;
	private NotificationManager notifier = null;
	public static int pointCount = 0;
	private boolean initialGpxBool = true;
	private Handler handler, realTimeHandler;
	Intent broadCastInent;
	public static final String BROADCAST_ACTION = "com.devtechdesign.gpshare.services.GPXService";

	private int updateRate = 20;
	private int realTimeInterval = 0;
	private static String currentRouteDateKey;
	private Date datetime;
	private static String lat = "";
	private static String longi = "";
	// private String altitude = "Locating...";
	private static String speed = "0.0";
	private String distance = "0.0";
	private Double previousLat = 0.0;
	private static Double previousLongi = 0.0;
	private static Double totalDistance = 0.0;
	private Double totalSpeed = 0.0;
	private Double averageSpeed = 0.0;

	// private Double totalRise = 0.0;
	// private Double totalFall = 0.0;
	private Double previousAltitude = 0.0;
	private Double highestElevation = 0.0;
	private static String accuracy = "0.00";
	private static float topSpeed = 0;
	private static int updateCount = 0;
	private Handler timerHandler = new Handler();
	protected static int elapsedTime = 0;
	Location tempLocation;
	boolean normalTracking = false;
	boolean realTimeTracking = false;

	private UncaughtExceptionHandler defaultUEH;

	public static final String ACTION_FOREGROUND = "com.devtechdesign.gpshare.services.GPXService.FOREGROUND";
	static final String ACTION_BACKGROUND = "com.devtechdesign.gpshare.services.GPXService.BACKGROUND";

	private static final Class<?>[] mSetForegroundSignature = new Class[] { boolean.class };
	private static final Class<?>[] mStartForegroundSignature = new Class[] { int.class, Notification.class };
	private static final Class<?>[] mStopForegroundSignature = new Class[] { boolean.class };
	private Intent localIntent;
	private NotificationManager mNM;
	private Method mSetForeground;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mSetForegroundArgs = new Object[1];
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];
	private int NOTIFICATION = 1;

	@Override
	public IBinder onBind(Intent arg0) { 

		return null;
	}

	void invokeMethod(Method method, Object[] args) {
		try {
			method.invoke(this, args);
		} catch (InvocationTargetException e) {
			// Should not happen.

		} catch (IllegalAccessException e) {
			// Should not happen.

		}
	}

	/**
	 * * This is a wrapper around the new startForeground method, using the
	 * older * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification) {
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			invokeMethod(mStartForeground, mStartForegroundArgs);

			return;
		}
		// Fall back on the old API.
		mSetForegroundArgs[0] = Boolean.TRUE;
		invokeMethod(mSetForeground, mSetForegroundArgs);
		mNM.notify(id, notification);
	}

	/**
	 * * This is a wrapper around the new stopForeground method, using the older
	 * * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
		// If we have the new stopForeground API, then use it.
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			invokeMethod(mStopForeground, mStopForegroundArgs);
			return;
		}
		// Fall back on the old API. Note to cancel BEFORE changing the
		// foreground state, since we could be killed at that point.
		mNM.cancel(id);
		mSetForegroundArgs[0] = Boolean.FALSE;

		invokeMethod(mSetForeground, mSetForegroundArgs);

	}

	private Handler _timerHandler;
	private static boolean pebbleTracking;
	private static GPXService gpsService;

	@Override
	public void onStart(Intent intent, int startId) {
		if (intent != null)
			handleCommand(intent);

	}

	private static SharedPreferences sharedPreferences;
	private static Editor editor;
	private static int segmentCount = 1;
	private static int maxSegmentCountPerRoute = 20;
	private static String currentRouteSegmentDateKey;

	private static String LoadPreferences(String key) {
		String value = sharedPreferences.getString(key, "");
		return value;
	}

	public static String intToSTwoDig(int num) {
		String output = Integer.toString(num);
		while (output.length() < 2)
			output = "0" + output;
		return output;
	}

	void handleCommand(Intent intent) {
		if (ACTION_FOREGROUND.equals(intent.getAction())) {

			normalTracking = intent.getBooleanExtra("normalTracking", false);
			realTimeTracking = intent.getBooleanExtra("realTimeTracking", false);
			realTimeInterval = intent.getIntExtra("realTimeInterval", 0);
			pebbleTracking = intent.getBooleanExtra("pebbleTracking", false);

			if (realTimeTracking) {
				realTimeHandler = new Handler();
				realTimeHandler.removeCallbacks(sendRealTimeTrackingNotification);
				realTimeHandler.postDelayed(sendRealTimeTrackingNotification, realTimeInterval);
			}

			updateRate = intent.getIntExtra("update-rate", -1);
			// currentRouteDateKey = bund.getString("currentRouteDateKey");
			Bundle bund = intent.getExtras();

			if (updateRate == -1) {
				updateRate = 6000;
			}

			Criteria criteria = new Criteria();
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setBearingAccuracy(Criteria.ACCURACY_FINE);
			criteria.setHorizontalAccuracy(Criteria.ACCURACY_FINE);
			criteria.setSpeedAccuracy(Criteria.ACCURACY_FINE);
			criteria.setVerticalAccuracy(Criteria.ACCURACY_FINE);
			criteria.setAltitudeRequired(false);
			criteria.setBearingRequired(false);
			criteria.setCostAllowed(true);
			criteria.setSpeedRequired(true);
			criteria.setPowerRequirement(Criteria.POWER_LOW);
			criteria.setAccuracy(Criteria.ACCURACY_FINE);
			criteria.setSpeedAccuracy(Criteria.ACCURACY_FINE);
			gpsService = this;

			location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

			String best = location.getBestProvider(criteria, true);
			location.requestLocationUpdates(best, 2000, 10, locationListener);

			// notifty that we've started up
			Notification notify = new Notification(android.R.drawable.stat_notify_more, "GPShare GPS Service", System.currentTimeMillis());
			notify.flags |= Notification.FLAG_ONGOING_EVENT;
			notify.icon = R.drawable.globe_logo2;

			Intent toLaunch = new Intent(getApplicationContext(), MainActivity.class);

			toLaunch.setAction("android.intent.action.MAIN");
			toLaunch.addCategory("android.intent.category.LAUNCHER");

			PendingIntent intentBack = PendingIntent.getActivity(getApplicationContext(), 0, toLaunch, PendingIntent.FLAG_UPDATE_CURRENT);

			notify.setLatestEventInfo(getApplicationContext(), "GPShare GPS Service", "Currently Tracking", intentBack);

			notifier.notify(NOTIFICATION, notify);
			startForegroundCompat(1, notify);

			startListeningForLocation();
		}
	}

	private Runnable sendUpdatesToUI = new Runnable() {
		public void run() {
			DisplayGpxServiceData();
			handler.postDelayed(this, 1000); // 2
												// seconds
		}
	};

	private Runnable sendRealTimeTrackingNotification = new Runnable() {

		@SuppressWarnings("unchecked")
		public void run() {
			DisplayGpxServiceData();
			realTimeHandler.postDelayed(this, realTimeInterval);

			new AsyncTask() {

				@Override
				protected Object doInBackground(Object... arg0) {

					if (regid != null) {
						if (personId != null) {
							if (!lat.equals("") && !longi.equals("")) {
								Transactions.sendNotification(regid, lat + "," + longi, personId, "{\"x\":\"" + lat + "\",\"y\":\"" + longi + "\"}", "0", getApplicationContext());
							}
						}
					}
					return null;
				}
			}.execute(null, null, null);
		}
	};
	private String angle;

	private String getTime(int milliSeconds) {
		int seconds = (int) (milliSeconds / 1000) % 60;
		int minutes = (int) ((milliSeconds / (1000 * 60)) % 60);
		int hours = (int) ((milliSeconds / (1000 * 60 * 60)) % 24);
		String totalTime = intToSTwoDig(hours) + ":" + intToSTwoDig(minutes) + ":" + intToSTwoDig(seconds);
		return totalTime;
	}

	private void DisplayGpxServiceData() {

		broadCastInent.putExtra("pointCount", String.valueOf(pointCount));
		broadCastInent.putExtra("speed", GLibTrans.convertMphToUom(Double.valueOf(speed), GPShare.getUOM()));
		broadCastInent.putExtra("distance", String.valueOf(Transactions.convertMetersPrefUom(totalDistance, GPShare.getUOM())));
		broadCastInent.putExtra("latitude", lat);
		broadCastInent.putExtra("longitude", longi);
		broadCastInent.putExtra("elapsedTime", getTime(elapsedTime));
		broadCastInent.putExtra("topSpeed", GLibTrans.convertMphToUom(topSpeed, GPShare.getUOM()));
		broadCastInent.putExtra("accuracy", accuracy);
		broadCastInent.putExtra("segmentCount", segmentCount);
		broadCastInent.putExtra("angle", angle);
		broadCastInent.putExtra("dateTime", GLibTrans.getDateTime());
		broadCastInent.addFlags(0);

		if (pebbleTracking)
			updatePebbleUi();
		// if(tempLocation != null)
		// {
		// updateDatabase(tempLocation);
		// }

		sendBroadcast(broadCastInent);
	}

	protected Double findCoordinateDistance(Double startLat, Double startLong, Double endLat, Double endLong) {

		Double distance = null;
		String startingLoc = "";
		Location start = new Location(startingLoc);
		String endingLoc = null;
		Location end = new Location(endingLoc);

		start.setLatitude(startLat);
		start.setLongitude(startLong);

		end.setLatitude(endLat);
		end.setLongitude(endLong);
		distance = Double.valueOf(start.distanceTo(end));

		return distance;
	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {

			Double locLat = location.getLatitude();
			Double locLongi = location.getLongitude();
			angle = String.valueOf(location.getBearing());
			lat = String.valueOf(locLat);
			longi = String.valueOf(locLongi);

			// only
			// calculate
			// values and
			// update the
			// database when
			// we are
			// noarmal
			// tracking.
			if (normalTracking) {
				tempLocation = location;
				float currentSpeed = location.getSpeed() * 2.23693629f;
				speed = String.valueOf(twoDForm.format(currentSpeed));

				if (pointCount != 0)
					averageSpeed = ((elapsedTime / 1000) / 60) / totalDistance;
				accuracy = String.valueOf((double) location.getAccuracy());
				Globals.setcurrentCoords(lat + "," + longi + ",");

				if (previousLat != 0.0 && previousLongi != 0.0 && locLat != 0.0 & locLongi != 0.0) {
					totalDistance += findCoordinateDistance(previousLat, previousLongi, locLat, locLongi);
				}

				if (currentSpeed > topSpeed) {
					topSpeed = currentSpeed;
				}

				previousLat = Double.valueOf(locLat);
				previousLongi = Double.valueOf(locLongi);

				new UpdateDataBaseWithXYStringAsync().execute(location);

			}
		}

		public void onProviderDisabled(String provider) {

		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};
	private String regid;
	private String personId;

	private static String firstSegRouteDateKey;

	protected void showToast(String toastString) {
		Toast.makeText(this, toastString, Toast.LENGTH_SHORT).show();
	}

	public void onDestroy() {
		super.onDestroy();

		if (location != null) {
			location.removeUpdates(locationListener);
			location = null;

		}

		zeroOutStaticValues();
		mNM.cancel(NOTIFICATION);

		_timerHandler.removeCallbacks(getData);
		handler.removeCallbacks(sendUpdatesToUI);

		if (realTimeTracking) {
			realTimeHandler.removeCallbacks(sendRealTimeTrackingNotification);
			realTimeHandler = null;
		}

		handler = null;
		_timerHandler = null;

		super.onDestroy();
	}

	public void startListeningForLocation() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setBearingAccuracy(Criteria.ACCURACY_FINE);
		criteria.setHorizontalAccuracy(Criteria.ACCURACY_FINE);
		criteria.setSpeedAccuracy(Criteria.ACCURACY_FINE);
		criteria.setVerticalAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setSpeedRequired(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setSpeedAccuracy(Criteria.ACCURACY_FINE);

		broadCastInent = new Intent(BROADCAST_ACTION);

		location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		String best = location.getBestProvider(criteria, true);
		location.requestLocationUpdates(best, 2000, 10, locationListener);
		// showToast("Gpx Service: Location Listner Restarted");
	}

	public static void savePreferences(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	public void onCreate() {
		super.onCreate();

		handler = new Handler();
		_timerHandler = new Handler();

		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		try {

			mStartForeground = getClass().getMethod("startForeground", mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground", mStopForegroundSignature);

		} catch (NoSuchMethodException e) {
			// Running on an older platform.
			mStartForeground = mStopForeground = null;
		}

		location = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		notifier = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

		sharedPreferences = getSharedPreferences(SharedPref, 0);
		editor = sharedPreferences.edit();

		personId = LoadPreferences(PERSON_ID);
		regid = LoadPreferences(PROPERTY_REG_ID);

		currentRouteDateKey = LoadPreferences("currentRouteDateKey");
		currentRouteSegmentDateKey = LoadPreferences("currentRouteSegmentDateKey");
		firstSegRouteDateKey = LoadPreferences("firstSegRouteDateKey");

		aRoute currentRouteElements = null;

		try {
			currentRouteElements = Transactions.getAllElementsOfRouteByKey(firstSegRouteDateKey);
		} catch (IndexOutOfBoundsException e) {
			savePreferences("currentRouteDateKey", "");
			gpsService.stopSelf();
		}

		try {
			totalDistance = Double.valueOf(currentRouteElements.getDistance());
		} catch (NullPointerException e) {
		} catch (NumberFormatException ne) {
			totalDistance = 0.0;
		}
		try {
			previousLongi = Double.valueOf(currentRouteElements.getX());
		} catch (NullPointerException e) {
		} catch (NumberFormatException ne) {
			previousLongi = 0.0;
		}
		try {
			elapsedTime = Integer.valueOf(currentRouteElements.getElapsedTime());
		} catch (NullPointerException e) {
		} catch (NumberFormatException ne) {
			elapsedTime = 0;
		}
		try {
			pointCount = Integer.valueOf(currentRouteElements.getPointCount());
		} catch (NullPointerException e) {
		} catch (NumberFormatException ne) {
			pointCount = 0;
		}

		broadCastInent = new Intent(BROADCAST_ACTION);
		handler.removeCallbacks(sendUpdatesToUI);

		handler.postDelayed(sendUpdatesToUI, 1000);
		_timerHandler.postDelayed(getData, 1000);
	}

	private static void zeroOutStaticValues() {
		lat = "0.0";
		longi = "0.0";
		totalDistance = 0.0;
		previousLongi = 0.0;
		elapsedTime = 0;
		pointCount = 0;
	}

	@SuppressWarnings("static-access")
	private static void dbUpdateRouteRecord(Location location) {
		// 02-16 15:04:25.453: I/System.out(21811): ColumnHeaders:
		// |_id|allroutes_place|allroutes_id|allroutes_name|allroutes_key|
		// allroutes_x|allroutes_y|allroutes_xy_string|allroutes_createDate|allroutes_uploaded|allroutes_taggerUsername|allroutes_elapsedTime|
		// allroutes_startDateTime|allroutes_endDateTime|allroutes_distance|allroutes_pointCount|allroutes_topSpeed|allroutes_totalRise|
		// allroutes_totalFall|allroutes_currentTrackBool|allroutes_segmentKey

		double lat = location.getLatitude();
		double lng = location.getLongitude();

		Transactions.updateCurrentRouteSeg(currentRouteDateKey, lat, lng, "", speed);
		Transactions.updateFirstSegOfRoute(lat, totalDistance, lng, String.valueOf(topSpeed), elapsedTime, pointCount, firstSegRouteDateKey);

	}

	public static void insertNewRouteAndUpdateSegKey() {

		currentRouteDateKey = GLibTrans.getDateTime();

		currentRouteSegmentDateKey = LoadPreferences("currentRouteSegmentDateKey");
		savePreferences("currentRouteDateKey", currentRouteDateKey);

		Transactions.createNewRoute("", LoadPreferences("currentPlace"), "false", "", "", currentRouteDateKey, "true", currentRouteSegmentDateKey, "");
	}

	public static void updateDatabase(Location location) {
		if (location != null) {
			// dbInsert(location);

			if (updateCount == maxSegmentCountPerRoute) {
				insertNewRouteAndUpdateSegKey();
				dbUpdateRouteRecord(location);
				updateCount = 0;
				segmentCount++;
				pointCount++;

			} else {
				dbUpdateRouteRecord(location);
				updateCount++;
				pointCount++;
			}

		}
	}

	@Override
	public boolean onUnbind(Intent intent) {

		_timerHandler.removeCallbacks(getData);
		handler.removeCallbacks(sendUpdatesToUI);
		realTimeHandler.removeCallbacks(sendRealTimeTrackingNotification);

		realTimeHandler = null;
		handler = null;
		_timerHandler = null;
		return super.onUnbind(intent);
	}

	private static int DATA_INTERVAL = 1000;

	private Runnable getData = new Runnable() {
		@Override
		public void run() {
			_timerHandler.postDelayed(getData, DATA_INTERVAL);
			// if(elapsedTime
			// >
			// 5000)
			// {
			// if(elapsedTime
			// <
			// 20000)
			// {
			// System.out.println("before intentional force close");
			// int
			// test
			// =
			// Integer.valueOf("s");
			// }
			// }
			// System.out.println("elapsedTime from service: "+
			// elapsedTime
			// );
			elapsedTime += 1000;
		}
	};

	// handler listener
	private Thread.UncaughtExceptionHandler _unCaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
		@Override
		public void uncaughtException(Thread thread, Throwable ex) {

			// here
			// I
			// do
			// logging
			// of
			// exception
			// to
			// a
			// db

			PendingIntent myActivity = PendingIntent.getActivity(getApplicationContext(), 192837, new Intent(getApplicationContext(), MainActivity.class),
					Intent.FLAG_ACTIVITY_NEW_TASK);

			AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 2000, myActivity);
			System.exit(2);

			// re-throw
			// critical
			// exception
			// further
			// to
			// the
			// os
			// (important)
			defaultUEH.uncaughtException(thread, ex);
		}
	};
	private boolean isPaceLabel = true;

	private class UpdateDataBaseWithXYStringAsync extends AsyncTask<Location, Void, Void> {

		// can use UI thread here

		protected void onPreExecute() {
			// this.dialog.setMessage("Getting Notification data...");
			// this.dialog.show();
		}

		// automatically done on worker thread (separate from UI thread)
		protected Void doInBackground(final Location... args) {

			GPXService.updateDatabase(args[0]);

			return null;
		}

		// can use UI thread here
		protected void onPostExecute(final Void unused) {
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub
			super.onCancelled();
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

	private void updatePebbleUi() {

		PebbleDictionary data = new PebbleDictionary();
		data.addString(Constants.SPORTS_TIME_KEY, getTime(elapsedTime).substring(3));
		data.addString(Constants.SPORTS_DISTANCE_KEY, String.valueOf(Transactions.convertMetersPrefUom(totalDistance, GPShare.getUOM())));
		data.addString(Constants.SPORTS_DATA_KEY, GLibTrans.convertMphToUom(Double.valueOf(speed), GPShare.getUOM()));
		data.addInt32(Constants.SPORTS_UNITS_KEY, determineUom());
		data.addString(Constants.SPORTS_LABEL_KEY,
				(isPaceLabel ? GLibTrans.convertMphToUom(Double.valueOf(speed), GPShare.getUOM()) : GLibTrans.convertMphToUom(Double.valueOf(averageSpeed), GPShare.getUOM())));
		data.addUint8(Constants.SPORTS_LABEL_KEY, (byte) (isPaceLabel ? Constants.SPORTS_DATA_SPEED : Constants.SPORTS_DATA_PACE));

		PebbleKit.sendDataToPebble(getApplicationContext(), Constants.SPORTS_UUID, data);
		isPaceLabel = !isPaceLabel;
	}

	public int determineUom() {
		if (GPShare.getUOM().equals("") || !GPShare.getUOM().equals("0")) {
			return Constants.SPORTS_UNITS_METRIC;
		} else {
			return Constants.SPORTS_UNITS_IMPERIAL;
		}
	}

	public static boolean isPebbleTracking() {
		return pebbleTracking;
	}

	public static void setPebbleTracking(boolean pebbleTracking) {
		GPXService.pebbleTracking = pebbleTracking;
	}
}
