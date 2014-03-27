package com.devtechdesign.gpshare.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List; 
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.db.RouteDatabase;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.data.db.RouteDatabase.AllRoutes;
import com.devtechdesign.gpshare.data.db.RouteDatabase.Images;
import com.devtechdesign.gpshare.data.db.RouteDatabase.Places;
import com.devtechdesign.gpshare.data.db.RouteDatabase.Routes;
import com.devtechdesign.gpshare.dialogs.ShareAct;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.elements.Route;
import com.devtechdesign.gpshare.elements.aPlace;
import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.facebook.BaseRequestListener;
import com.devtechdesign.gpshare.facebook.SessionEvents.AuthListener;
import com.devtechdesign.gpshare.facebook.SessionEvents.LogoutListener;
import com.devtechdesign.gpshare.services.GPXService;
import com.devtechdesign.gpshare.utility.DateNow;
import com.devtechdesign.gpshare.utility.GPsharePrefs;
import com.devtechdesign.gpshare.utility.SoapInterface;
import com.devtechdesign.gpshare.utility.Utility;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.devtechdesign.gpshare.Images.ImageUrls;

import dev.tech.images.Util;

public class ImageMapGallery extends MapActivity implements SoapInterface {

	public ArrayList<aRoute> aryRoutes;
	private MapController mapController;
	List<Overlay> mapOverlays;
	public boolean markSample, getRouteBool;
	private MapView mapView;
	public ArrayList<Image> imgList = new ArrayList<Image>();
	private List<Overlay> overlay;
	private Drawable pushpin;
	public Context mContext;
	public String currentCoords, currentRoute, pmid, rid, currentCoordinates, placeMarkType, mapDataId, currentMapPlace;
	public RelativeLayout galleryLayout;
	public Cursor contacts;
	public ArrayList<aPlace> placeListArray = new ArrayList<aPlace>();
	public ImageUrls ImageUrlTrans = new ImageUrls();
	public ProgressBar pbDefaultM, progressBarImages;
	public TextView txtCurrentPlace, txtImagesLoading, txtTopSpeed;
	public String[] routeListStringArray;
	public LinearLayout lytStats;
	public Button btnImages, btnShare;
	public SimpleAdapter lvRoutesAdapter;
	public Vibrator vibrator;
	private UncaughtExceptionHandler defaultUEH;
	String[] permissions = { "offline_access", "email", "read_stream", "publish_stream", "user_photos", "publish_checkins", "photo_upload" };
	public Activity act;
	public View thisView;
	ImageMapGallery mapAct;
	public GPShare GPShare;
	public boolean routeIsDrawing = false;
	private ProgressBar progressLoadingImages;
	private boolean mapActive = false;
	private Drawable defaultMarker;

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_gallery);
		mapActive = true;
		defaultMarker = getResources().getDrawable(R.drawable.image_pushpin);
		GPsharePrefs gPrefs = new GPsharePrefs(this, SharedPref);
		pushpin = this.getResources().getDrawable(R.drawable.image_pushpin);
		mapView = (MapView) findViewById(R.id.mapView);

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		mapView.setBuiltInZoomControls(false);
		mapView.setSatellite(false);
		mapView.setClickable(true);
		mapView.setSatellite(!mapView.isSatellite());
		mapView.displayZoomControls(false);
		mapController = mapView.getController();
		mapController.setZoom(12);
		mapOverlays = mapView.getOverlays();

		overlay = mapView.getOverlays();
		overlay.clear();
		pushpin = this.getResources().getDrawable(R.drawable.image_pushpin);
		ArrayList<Image> imgs = Transactions.getImageaFromPhoneDb("");
		addEachImageToMapPer(imgs);
		mapView.invalidate();
		mapController = mapView.getController();

	}

	public void zoomIn(View v) {
		int zLevel = mapView.getZoomLevel() + 1;
		mapController.setZoom(zLevel);
	}

	public void zoomOut(View v) {
		int zLevel = mapView.getZoomLevel() - 1;
		mapController.setZoom(zLevel);
	}

	public void toggleMap(View v) {

		if (mapView.isStreetView()) {
			mapView.setStreetView(false);
			mapView.setSatellite(true);
			mapView.invalidate();
			return;
		}
		// if(mapView.isTraffic())
		// {
		// mapView.setStreetView(true);
		// }
		if (mapView.isSatellite()) {
			mapView.setStreetView(true);
			mapView.setSatellite(false);
			mapView.invalidate();
			return;
		}
	}

	private void addEachImageToMapPer(ArrayList<Image> imgs) {
		mapView.getOverlays().clear();
		// add all images to the map per place
		for (int i = 0; i < imgs.size(); i++) {
			Image img = imgs.get(i);
			new GetLocalPicAsyncTaskWithSize().execute(img);
		}
	}

	@Override
	protected void onDestroy() {
		System.out.println("ondestroy");
		mapActive = false;
		super.onDestroy();
	}

	public class GetLocalPicAsyncTaskWithSize extends AsyncTask<Object, Void, Bitmap> {
		Image img;

		@Override
		protected Bitmap doInBackground(Object... params) {
			if (mapActive) {
				if (params != null) {
					this.img = (Image) params[0];

					return Util.decodeFileImg(new File(img.getimgPath()), 20);
				}
			} else {
				System.out.println("mapActive: " + mapActive);
				return null;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Bitmap markerImage) {
			if (markerImage != null) {
				Double x = Double.parseDouble(img.getimgX());
				Double y = Double.parseDouble(img.getimgY());

				GeoPoint point = new GeoPoint((int) (x * 1E6), (int) (y * 1E6));

				// ImageOverlay imageOverlay = new
				// ImageOverlay(getApplicationContext(), point, markerImage);
				// // imageOverlay.addOverlay(new OverlayItem(point,
				// // currentCoordinates, currentCoordinates));
				// overlay.add(imageOverlay);

				mapView.invalidate();
			}
			super.onPostExecute(markerImage);
		}
	}

	// private void addPlaceMark(GeoPoint point, int selectedImgIndex, String
	// zoomType, String zoomBoolean, String removeOverlayBooleen,
	// String imgPath, Image img) {
	// // currentLocationOverlay = new CurrentLocationOverlay(pushpin,
	// // getApplicationContext(), mapController, img.getimageId());
	// // currentLocationOverlay.addOverlay(new OverlayItem(point, "Kathleen",
	// // "yea"), DateNow.getCurrentDateTime(), "", point,
	// // selectedImgIndex, imgPath, "", contacts, "");
	// // overlay.add(currentLocationOverlay);
	//
	// imageOverlay = new ImageOverlay(getApplicationContext(), point,
	// img.getimgPath());
	// // currentLocationOverlay.addOverlay(new OverlayItem(point, "Kathleen",
	// // "yea"), DateNow.getCurrentDateTime(), "", point,
	// // selectedImgIndex, imgPath, "", contacts, "");
	// overlay.add(imageOverlay);
	// }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}
}