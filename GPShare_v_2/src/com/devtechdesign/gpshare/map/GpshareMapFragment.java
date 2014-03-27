package com.devtechdesign.gpshare.map;

import com.devtechdesign.gpshare.PlacesAutoCompleteAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.app.ActionBar.OnNavigationListener;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Activity;
import android.app.ActivityManager;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.animations.ExpandAnimation;
import com.devtechdesign.gpshare.animations.ExpandAnimationV2;
import com.devtechdesign.gpshare.data.db.DatabaseControl;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.journals.DialogJournalNoLoc;
import com.devtechdesign.gpshare.journals.JournalMapCreater;
import com.devtechdesign.gpshare.layouts.vwJournal;
import com.devtechdesign.gpshare.services.GPXService;
import com.devtechdesign.gpshare.transformers.ScalePageTransformer;
import com.dtd.dbeagen.db.elements.aJournals;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.GoogleMap.SnapshotReadyCallback;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.CancelableCallback;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import dev.tech.util.GLibTrans;

/**
 * @author Darth
 * 
 */
public class GpshareMapFragment extends Fragment implements OnItemClickListener {

	/**
	 * Prevents other map objects from getting added to the map while
	 */
	private boolean addingJournal;
	private Context context;
	private GoogleMap mMap;
	private LatLng onTapLocation;
	GoogleMapOptions options = new GoogleMapOptions();
	MapUtil mapUtil;
	public OnNavigationListener listenerType;
	private Button slideButton;
	private LinearLayout lytRoutes;
	public MapRoute mRoute;
	public MapLayer mapLayer;
	List<String> groupList;
	List<String> childList;
	ExpandableListView expListView;
	private RelativeLayout bottomContentLayout;
	private RelativeLayout lytMain;
	private Marker currentLocationMarker;
	private LocationManager locationManager;
	private MapImageLayer imageLayer;
	private Button btnPlayFlight;
	private Activity act;

	/**
	 * Represents the journal which location is being specified by map click or
	 * current location
	 */
	private DialogJournalNoLoc journalNew;
	private ImageButton btnOptions;
	private Activity activity;
	private ViewGroup vGroup;
	private ViewPager mPager;
	private MapEditorPagerAdapter mPagerAdapter;
	private IMapAction mCallback;
	private AutoCompleteTextView txtSearch;

	public interface IMapAction {
		public void relocateJournalMapClick(LatLng latLng);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = activity;
		// This makes sure that the container activity has implemented
		// the callback interface. If not, it throws an exception
		try {
			mCallback = (IMapAction) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnHeadlineSelectedListener");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (vGroup == null) {
			vGroup = (ViewGroup) inflater.inflate(R.layout.map_frag, container, false);
			mMap = ((MapFragment) activity.getFragmentManager().findFragmentById(R.id.map)).getMap();

			options.mapType(GoogleMap.MAP_TYPE_SATELLITE).rotateGesturesEnabled(true).tiltGesturesEnabled(true).compassEnabled(true);
			mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);

			txtSearch = (AutoCompleteTextView) vGroup.findViewById(R.id.txtSearch);
			txtSearch.setAdapter(new PlacesAutoCompleteAdapter(vGroup.getContext(), R.layout.auto_complete_list_item));
			txtSearch.setOnItemClickListener(this);

			mPager = (ViewPager) vGroup.findViewById(R.id.pagerMapEditor);
			mPager.setPageTransformer(true, new ScalePageTransformer(mPager));

			startOrContinueTracking();
			Globals.setCurrentmMap(mMap);
			mRoute = new MapRoute(mMap);
			mapUtil = new MapUtil(mMap);

		} else {
			ViewGroup parent = (ViewGroup) vGroup.getParent();
			parent.removeAllViews();
		}

		getShowJournal();
		getShowJournalRelocate();
		return vGroup;
	}

	public OnMapClickListener journalLocationSet = new OnMapClickListener() {

		@Override
		public void onMapClick(LatLng point) {
			vwJournal.journal.setx(String.valueOf(point.latitude));
			vwJournal.journal.sety(String.valueOf(point.longitude));
			addJournalAndAnimate("", vwJournal);
			Globals.setLat(String.valueOf(point.latitude));
			Globals.setLongi(String.valueOf(point.longitude));
		}
	};

	public void getShowJournal() {
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			String journalId = bundle.getString("journalId");
			if (journalId != null) {
				ArrayList<vwJournal> journals = DatabaseControl.getInstance().gpsDbc.getJournalsViaQuery(journalId);
				this.addJournalMarker(journals.get(0));
			}
		}
	}

	public void getShowJournalRelocate() {
		Bundle bundle = this.getArguments();
		if (bundle != null) {
			String getShowJournalRelocate = bundle.getString("relocateJournalId");
			if (getShowJournalRelocate != null) {
				ArrayList<vwJournal> journals = DatabaseControl.getInstance().gpsDbc.getJournalsViaQuery(getShowJournalRelocate);
				this.addJournalMarker(journals.get(0));
				setupViewEditingMapJournalLoc();
			}
		}
	}

	public void viewJournal(aJournals j) {
		showJournalNoLocDialog();
	}

	public void addJournalMarker() {
		journalMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude())).title("Journal")
				.snippet("Journal").anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_journal)));
	}

	// public void addJournalMarker(vwJournal vwJournal) {
	// journalMarker = mMap.addMarker(new MarkerOptions().position(new
	// LatLng(Double.valueOf(vwJournal.journal.getx()),
	// Double.valueOf(vwJournal.journal.gety())))
	// .title("Journal").snippet("Journal").anchor(0.5f,
	// 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_journal)));
	// }

	OnMapClickListener flightPlanMapTapListener = new OnMapClickListener() {

		@Override
		public void onMapClick(LatLng point) {
			onTapLocation = point;
			flightPlanARy.add(point);
			btnPlayFlight.setText("Flight Points" + flightPlanARy.size());

		}
	};

	int flightPointCounter = 0;
	ArrayList<LatLng> flightPlanARy = new ArrayList<LatLng>();
	OnClickListener btnPlayFlightOnclick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// flightPointCounter
			// =
			// 0;
			// animateCameraTo(0);
			// captureMapScreen();
		}

	};
	private Location currentLocation;

	public void removeJournalPin() {
		if (this.journalMarker != null)
			this.journalMarker.remove();
	}

	public void animateCameraTo(int i) {
		if (flightPlanARy.size() > 0) {
			LatLng cLoc = flightPlanARy.get(i);

			mMap.getCameraPosition();
			CameraPosition camPosition = CameraPosition.builder().target(onTapLocation).tilt(65).zoom(15f).build();

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
			activity.registerReceiver(broadcastReceiver, new IntentFilter(GPXService.BROADCAST_ACTION));
		} else {
			zoomToLastKnownLocationStartTracking();
		}
	}

	private void updateCurrentLocationMarker(LatLng latLong, String dateTime) {
		removeCLocationmarker();
		currentLocationMarker = mMap.addMarker(new MarkerOptions().position(latLong).title("Current Location").snippet(dateTime).anchor(0.5f, 0.5f)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.current_location)));
		mMap.getCameraPosition();
		CameraPosition camPosition = CameraPosition.builder().target(latLong).tilt(65).zoom(15f).build();
		mMap.moveCamera(CameraUpdateFactory.newCameraPosition(camPosition));
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

		locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

		String best = locationManager.getBestProvider(criteria, true);
		setCurrentLocation(locationManager.getLastKnownLocation(best));

		locationManager.requestLocationUpdates(best, 2000, 10, locationListener);

		updateCurrentLocationMarker(new LatLng(getCurrentLocation().getLatitude(), getCurrentLocation().getLongitude()), GLibTrans.getDateTime());
	}

	public Bitmap rotateDrawable(float angle) {
		Bitmap arrowBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.current_location);
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

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (!isAddingJournal())
				updateGpsUI(intent);
		}
	};

	private boolean isMyServiceRunning() {
		ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if ("com.devtechdesign.gpshare.services.GPXService".equals(service.service.getClassName())) {
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

	private Marker journalMarker;
	private LatLng latLong;
	private vwJournal vwJournal;

	public void getShowHistory(View v) {
		showRouteControls();
		// setUpRouteListView();
	}

	public void zoomToRealTimeFriendLocation(String currentLocationExtras, String profileImgUrl) {
		mMap.clear();
		mRoute.zoomToRealTimeTrackingLocation(currentLocationExtras, profileImgUrl);
	}

	private void showRouteControls() {
		if (lytRoutes.getVisibility() == View.GONE) {
			lytRoutes.setVisibility(View.VISIBLE);
		} else {
			lytRoutes.setVisibility(View.GONE);
		}
	}

	public void displayImages(View v) {

		mapUtil.addEachImageToMapPer();

	}

	// public void setUpRouteListView() {
	// lytRoutes.removeAllViews();
	// lytRoutes.addView(new RouteLytLocal(context, act));
	//
	// }

	public GoogleMap getmMap() {
		return mMap;
	}

	public void setmMap(GoogleMap mMap) {
		this.mMap = mMap;
	}

	private void loadChild(String[] actionItemsForList) {
		childList = new ArrayList<String>();
		for (String model : actionItemsForList)
			childList.add(model);
	}

	// private void setGroupIndicatorToRight() {
	// /* Get the screen width */
	// DisplayMetrics dm = new DisplayMetrics();
	// context.getWindowManager().getDefaultDisplay().getMetrics(dm);
	// int width = dm.widthPixels;
	//
	// expListView.setIndicatorBounds(width - getDipsFromPixel(35), width -
	// getDipsFromPixel(5));
	// }

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

	public void showJournalNoLocDialog() {

		if (journalNew == null)
			journalNew = new DialogJournalNoLoc(this);

		JournalMapCreater jc = new JournalMapCreater(context, this);
		bottomContentLayout.removeAllViews();
		bottomContentLayout.addView(jc);
	}

	public void removeCurrentLocationMarker() {
		if (currentLocationMarker != null)
			currentLocationMarker.remove();
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}

	public boolean isAddingJournal() {
		return addingJournal;
	}

	public void setAddingJournal(boolean addingJournal) {
		this.addingJournal = addingJournal;
	}

	public Activity getAct() {
		return act;
	}

	public void setAct(Activity act) {
		this.act = act;
	}

	// public void captureMapScreen() {
	// SnapshotReadyCallback callback = new SnapshotReadyCallback() {
	// Bitmap bitmap;
	//
	// @Override
	// public void onSnapshotReady(Bitmap snapshot) {
	// // TODO Auto-generated method stub
	// bitmap = snapshot;
	// try {
	// FileOutputStream out = new FileOutputStream(
	// Environment.getExternalStorageDirectory()
	// + "/GPShare/" + "map"
	// + System.currentTimeMillis() + ".png");
	//
	// // above "/mnt ..... png" => is a storage path (where image
	// // will be stored) + name of image you can customize as per
	// // your Requirement
	//
	// bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// };
	//
	// mMap.snapshot(callback);
	//
	// // myMap is object of GoogleMap +> GoogleMap myMap;
	// // which is initialized in onCreate() =>
	// // myMap = ((SupportMapFragment)
	// //
	// getSupportFragmentManager().findFragmentById(R.id.map_pass_home_call)).getMap();
	// }

	public DialogJournalNoLoc getJournalNew() {
		return journalNew;
	}

	public void setJournalNew(DialogJournalNoLoc journalNew) {
		this.journalNew = journalNew;
	}

	/**
	 * Adds journal marker to the map representing an instance of a aJournals
	 * 
	 * @param j
	 */
	public void addJournalMarker(vwJournal j) {
		this.vwJournal = j;
		if (j.journal.getx() != null) {
			latLong = new LatLng(Double.valueOf(j.journal.getx()), Double.valueOf(j.journal.gety()));
			addJournalAndAnimate(j.journal.getText());
		} else {
			DialogJournalNoLoc djnl = new DialogJournalNoLoc(this);
			djnl.setJournal(j);
			djnl.showDialog();
		}
	}

	public void addJournalAndAnimate(String snippetText) {
		journalMarker = mMap.addMarker(new MarkerOptions().position(latLong).title("Journal").snippet(snippetText).anchor(0.5f, 0.5f)
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_journal)));
		animateCameraTo(latLong);
	}

	public void addJournalAndAnimate(String snippetText, vwJournal currentJournal) {
		this.vwJournal = currentJournal;

		if (mapJournalEdiLoc != null)
			mapJournalEdiLoc.setJournal(vwJournal);

		if (journalMarker != null)
			journalMarker.remove();

		LatLng latLng = new LatLng(Double.valueOf(currentJournal.journal.getx()), Double.valueOf(currentJournal.journal.gety()));
		journalMarker = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(currentJournal.journal.getx()), Double.valueOf(currentJournal.journal.gety())))
				.title("Journal").snippet(snippetText).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_journal)));

		animateCameraTo(latLng);
	}

	public void animateCameraTo(LatLng location) {

		@SuppressWarnings("static-access")
		CameraPosition camPosition = mMap.getCameraPosition().builder().target(location).tilt(65).zoom(15f).build();
		mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition), new CancelableCallback() {

			@Override
			public void onFinish() {

			}

			@Override
			public void onCancel() {

			}
		});
	}

	private MapFragEditJournalLoc mapJournalEdiLoc;

	/**
	 * ViewPager adapter to house different fragments used in editing map data
	 */
	private class MapEditorPagerAdapter extends FragmentStatePagerAdapter {
		private static final int NUM_PAGES = 1;

		public MapEditorPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 1:
				mapJournalEdiLoc = new MapFragEditJournalLoc();
				mapJournalEdiLoc.setJournal(vwJournal);
				return mapJournalEdiLoc;
			}
			mapJournalEdiLoc = new MapFragEditJournalLoc();
			mapJournalEdiLoc.setJournal(vwJournal);
			return mapJournalEdiLoc;
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}

	public void setupViewEditingMapJournalLoc() {
		mMap.setOnMapClickListener(journalLocationSet);
		if (mPagerAdapter == null)
			setMapEditorViewPagerAdapter();
		setPagerEditorIndex(1);
	}

	public void setMapEditorViewPagerAdapter() {
		mPagerAdapter = new MapEditorPagerAdapter(this.getFragmentManager());
		mPager.setAdapter(mPagerAdapter);
	}

	public void hideShowDataEditor() {
		ExpandAnimationV2 ea = new ExpandAnimationV2();
		ea.expand(mPager);
	}

	public void setPagerEditorIndex(int i) {
		hideShowDataEditor();
		mPager.setCurrentItem(i);
	}

	@Override
	public void onDetach() {
		hideMapEditor();
		super.onDetach();
	}

	public void hideMapEditor() {
		ExpandAnimationV2 ea = new ExpandAnimationV2();
		ea.collapse(mPager);
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		String str = (String) adapterView.getItemAtPosition(position);
		Toast.makeText(view.getContext(), str, Toast.LENGTH_SHORT).show();
	}
}
