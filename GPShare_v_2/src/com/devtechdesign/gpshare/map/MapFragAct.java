package com.devtechdesign.gpshare.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.layouts.CreateRoute;
import com.devtechdesign.gpshare.services.GPXService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import dev.tech.util.GLibTrans;

public class MapFragAct extends Activity {
	private GoogleMap mMap;
	private LatLng onTapLocation;
	GoogleMapOptions options = new GoogleMapOptions();
	MapUtil mapUtil;
	public OnNavigationListener listenerType;
	private LinearLayout lytRoutes;
	public MapRoute mRoute;
	public MapLayer mapLayer;
	List<String> groupList;
	List<String> childList;
	Map<String, List<String>> drawerListItems;
	private RelativeLayout lytMain;
	private Marker currentLocationMarker;
	private LocationManager locationManager;
	private MapImageLayer imageLayer;
	private Button btnPlayFlight; 
	
	@SuppressWarnings("deprecation")
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setTheme(android.R.style.Theme_Holo);
		setContentView(R.layout.map_frag);
		GPShare.mapFragAct = this;

		mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();

		options.mapType(GoogleMap.MAP_TYPE_SATELLITE).rotateGesturesEnabled(true).tiltGesturesEnabled(true).compassEnabled(true);
		mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

		String mapDataId = getIntent().getStringExtra("mapDataId");
		String mapOnlineDataId = getIntent().getStringExtra("mapOnlineDataId");
		String currentLocationExtras = getIntent().getStringExtra("currentLocation");
		String profileImgUrl = getIntent().getStringExtra("profileImgUrl");

		btnPlayFlight.setVisibility(View.GONE);
		// btnPlayFlight.setOnClickListener(btnPlayFlightOnclick);
		lytMain = (RelativeLayout) findViewById(R.id.lytMain);
 
		mapLayer = new MapLayer(this);

		createGroupList();
		createCollection();

		mMap.setOnMapClickListener(new OnMapClickListener() {

			@Override
			public void onMapClick(LatLng point) {
				onTapLocation = point;
				flightPlanARy.add(point);
				btnPlayFlight.setText("Flight Points" + flightPlanARy.size());

			}

		});

		startOrContinueTracking();

		mRoute = new MapRoute(mMap);//
		mRoute.zoomToRealTimeTrackingLocation(currentLocationExtras, profileImgUrl);

		getAndDisplayOnlineRoute(mapOnlineDataId);
		getAndDisplayLocalRoute(mapDataId);
		addNewRouteCurrentOb();

		mapUtil = new MapUtil(mMap);
	}

	int flightPointCounter = 0;
	ArrayList<LatLng> flightPlanARy = new ArrayList<LatLng>();
	OnClickListener btnPlayFlightOnclick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			flightPointCounter = 0;
			animateCameraTo(0);

		}

	};

	public void animateCameraTo(int i) {
		if (flightPlanARy.size() > 0) {
			LatLng cLoc = flightPlanARy.get(i);

			CameraPosition camPosition = mMap.getCameraPosition().builder().target(onTapLocation).tilt(65).zoom(15f).build();

			if (!((Math.floor(camPosition.target.latitude * 100) / 100) == (Math.floor(cLoc.latitude * 100) / 100) && (Math.floor(camPosition.target.longitude * 100) / 100) == (Math
					.floor(cLoc.longitude * 100) / 100))) {

				mMap.getUiSettings().setScrollGesturesEnabled(false);
				mMap.animateCamera(CameraUpdateFactory.newLatLng(cLoc), new CancelableCallback() {

					@Override
					public void onFinish() {
						mMap.getUiSettings().setScrollGesturesEnabled(true);
						flightPointCounter++;
						animateCameraTo(flightPointCounter);

					}

					@Override
					public void onCancel() {
						mMap.getUiSettings().setAllGesturesEnabled(true);

					}
				});
			}
		}
	}

	public void createImageLayerbyPlace(View v, String placeName) {
		ArrayList<Image> imgs = Transactions.getImageaFromPhoneDb(placeName);
		if (imgs.size() > 0) {
			if (imageLayer != null)
				mMap.clear();
			imageLayer = new MapImageLayer(v.getContext(), mMap, imgs);
			imageLayer.zoomToIndex(0);
		} else {
			Toast.makeText(v.getContext(), "No Images for " + placeName, 2).show();
		}
	}

	private void startOrContinueTracking() {
		if (isMyServiceRunning() == true) {
			registerReceiver(broadcastReceiver, new IntentFilter(GPXService.BROADCAST_ACTION));
		} else {
			zoomToLastKnownLocationStartTracking();
		}
	}

	private void updateCurrentLocationMarker(LatLng latLong, String dateTime) {
		removeCLocationmarker();
		currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLong).title("Current Location").snippet(dateTime).anchor(0.5f, 0.5f)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location)));
	}

	public void removeCLocationmarker() {
		if (currentLocationMarker != null)
			currentLocationMarker.remove();
	}

	private class ImagesPlacesAdapter extends ArrayAdapter<String> {

		HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

		public ImagesPlacesAdapter(Context context, int textViewResourceId, String[] objects) {
			super(context, textViewResourceId, objects);
			for (int i = 0; i < objects.length; ++i) {
				mIdMap.put(objects[i], i);
			}
		}

		@Override
		public long getItemId(int position) {
			String item = getItem(position);
			return mIdMap.get(item);
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

	}

	protected void zoomToLastKnownLocationStartTracking() {
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		String best = locationManager.getBestProvider(criteria, true);
		Location loc = locationManager.getLastKnownLocation(best);

		locationManager.requestLocationUpdates(best, 2000, 10, locationListener);

		updateCurrentLocationMarker(new LatLng(loc.getLatitude(), loc.getLongitude()), GLibTrans.getDateTime());
	}

	public Bitmap rotateDrawable(float angle) {
		Bitmap arrowBitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.current_location);
		// Create blank bitmap of equal size
		Bitmap canvasBitmap = arrowBitmap.copy(Bitmap.Config.ARGB_8888, true);
		canvasBitmap.eraseColor(0x00000000);

		// Create canvas
		Canvas canvas = new Canvas(canvasBitmap);

		// Create rotation matrix
		Matrix rotateMatrix = new Matrix();
		rotateMatrix.setRotate(angle, canvas.getWidth() / 2, canvas.getHeight() / 2);

		// Draw bitmap onto canvas using matrix
		canvas.drawBitmap(arrowBitmap, rotateMatrix, null);

		return canvasBitmap;
	}

	public void getAndDisplayOnlineRoute(String mapOnlineDataId) {
		MapRoute onlineRoute = new MapRoute(mMap);
		onlineRoute.dispLayoutOnlineRoute(mapOnlineDataId);
	}

	public void getAndDisplayLocalRoute(String routeId) {
		MapRoute newRoute = new MapRoute(mMap);
		newRoute.getAndDisplayRoute(routeId);
	}

	public void addNewRouteCurrentOb() {
		MapRoute newRoute = new MapRoute(mMap);
		newRoute.getAndDisplayRouteVyxyString(Globals.getCurrentRoute());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			GPShare.mapFragAct = null;
			finish();
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			updateGpsUI(intent);
		}
	};

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.rad.gpsservice.GPXService".equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	private void updateGpsUI(Intent intent) {

		Bundle bund = intent.getExtras();

		// String pointCount = bund.getString("pointCount");
		String lat = bund.getString("latitude");
		String longi = bund.getString("longitude");
		String dateTime = bund.getString("dateTime");

		// float angle = Float.valueOf(bund.getString("angle"));
		// this.rotateDrawable(angle);
		// bund.getString("speed");
		// String elapsedTime = bund.getString("elapsedTime");
		// String topSpeed = bund.getString("topSpeed");
		// bund.getString("accuracy");

		if (!lat.equals("") && !longi.equals("")) {
			updateCurrentLocationMarker(new LatLng(Double.valueOf(lat), Double.valueOf(longi)), dateTime);
			mRoute.updateRouteLine(new LatLng(Double.valueOf(lat), Double.valueOf(longi)));
		}

	}

	private final LocationListener locationListener = new LocationListener() {
		public void onLocationChanged(Location location) {

			Double locLat = location.getLatitude();
			Double locLongi = location.getLongitude();

			updateCurrentLocationMarker(new LatLng(locLat, locLongi), GLibTrans.getDateTime());
		}

		public void onProviderDisabled(String provider) {

		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	public void getShowHistory(View v) {
		showRouteControls();
	}

	public void zoomToRealTimeFriendLocation(String currentLocationExtras, String profileImgUrl) {
		mRoute.zoomToRealTimeTrackingLocation(currentLocationExtras, profileImgUrl);
	}

	private void showRouteControls() {
		if (lytRoutes.getVisibility() == View.GONE) {
			lytRoutes.setVisibility(View.VISIBLE);
		} else {
			lytRoutes.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {
		// unregister listener
		super.onPause();
	}

	@Override
	protected void onDestroy() {

		super.onDestroy();
	}

	public GoogleMap getmMap() {
		return mMap;
	}

	public void setmMap(GoogleMap mMap) {
		this.mMap = mMap;
	}

	private void createGroupList() {
		groupList = new ArrayList<String>();
		groupList.add("Add");
		groupList.add("History");
		groupList.add("Images");
	}

	private void createCollection() {

		String[] add = { "Route" };
		String[] History = { "Routes" };
		drawerListItems = new LinkedHashMap<String, List<String>>();

		for (String group : groupList) {
			if (group.equals("Add")) {
				loadChild(add);
			} else if (group.equals("History")) {
				loadChild(History);
			} else if (group.equals("Images")) {
				loadChild(Transactions.getImagePlacesNotNull(""));
			}

			drawerListItems.put(group, childList);

		}
	}

	private void loadChild(String[] actionItemsForList) {
		childList = new ArrayList<String>();
		for (String model : actionItemsForList)
			childList.add(model);
	}

	// Convert pixel to dip
	public int getDipsFromPixel(float pixels) {
		// Get the screen's density scale
		final float scale = getResources().getDisplayMetrics().density;
		// Convert the dps to pixels, based on density scale
		return (int) (pixels * scale + 0.5f);
	}

	public MapRoute getMapRoute() {
		return mRoute;
	}

	public MapLayer getMapLayer() {
		return mapLayer;
	}

}
