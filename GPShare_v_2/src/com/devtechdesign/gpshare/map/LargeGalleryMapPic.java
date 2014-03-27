package com.devtechdesign.gpshare.map;

import java.io.File;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.Images.CustomCameraAct;
import com.devtechdesign.gpshare.adapters.ImageGalleryViewPagerAdapter;
import com.devtechdesign.gpshare.adapters.ImagePagerAdapter;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.dialogs.ShareAct;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.utility.Utility;
import com.facebook.android.Facebook;
import com.google.android.apps.analytics.GoogleAnalyticsTracker;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import dev.tech.images.Util;

public class LargeGalleryMapPic extends MapActivity {

	ImageView imgMain;
	String x, y, mapDataId, imgPath;
	private SharedPreferences sharedPreferences, userNameSharedPreferences;
	private Editor editor;
	private MapController mapController;
	List<Overlay> mapOverlays;
	public boolean markSample, getRouteBool;
	protected ListView lvRoutes;
	private ArrayList<GeoPoint> trackPoints = new ArrayList<GeoPoint>();
	public ArrayList<Image> imgList = new ArrayList<Image>();
	private List<Overlay> overlay;
	private Drawable pushpin, routePin, bluePushPin, topSpeedPin;
	public Context mContext;
	public String currentCoords, currentRoute, pmid, rid, currentCoordinates, placeMarkType, currentMapPlace, dataType;
	public RelativeLayout galleryLayout;
	public Cursor contacts;
	public ProgressBar pbDefaultM, progressBarImages;
	public TextView tvProgress, txtCurrentPlace, txtImagesLoading;
	public String[] routeListStringArray;
	GoogleAnalyticsTracker tracker;
	public Button btnImages, btnRoutes;
	public SimpleAdapter lvRoutesAdapter;
	public Vibrator vibrator;
	public static final String APP_ID = "273395502707692";
	@SuppressWarnings("deprecation")
	Facebook facebook = new Facebook(APP_ID);
	private UncaughtExceptionHandler defaultUEH;
	private ArrayList<aRoute> localRouteAry = new ArrayList<aRoute>();
	private LocationManager location = null;
	private Bitmap bmp;
	private aRoute currentaRoute;
	private GeoPoint geoPoint;
	private TextView txtPlaceName;
	private Button btnShare;
	private LinearLayout lytMain;
	private Button btnLocationRefresh;
	private Button btnZoomIn;
	private Button btnZoomOut;
	private GPShare GPShare;
	private ArrayList<Image> imageAry;
	LinearLayout myGallery;
	private int imgIndex;
	private ViewPager _mViewPager;
	private ImageGalleryViewPagerAdapter _adapter;
	private int cIndex;
	private ImagePagerAdapter adapter;
	private ViewPager mapGalleryViewPager;
	private MapView mapView;
	private String imgFilePath;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		setContentView(R.layout.large_gallery_pic);

		// if (Utility.actionBar == null) {
		// Utility.actionBar = new GPSActionBar(new
		// MainControlActionBarListener(this));
		// Utility.actionBar.setActionBar(this);
		// } else {
		// Utility.actionBar.setActionBar(this);
		// }

		// imgMain = (ImageView) findViewById(R.id.imgMain);
		// imgMain.setScaleType(ImageView.ScaleType.CENTER_CROP);
		lytMain = (LinearLayout) findViewById(R.id.lytMain);
		mContext = getApplicationContext();
		GPShare = (GPShare) getApplicationContext();
		currentaRoute = new aRoute(null);
		btnShare = (Button) findViewById(R.id.btnShare);
		Bundle extras = getIntent().getExtras();
		imgFilePath = extras.getString("imgFilePath");
		x = extras.getString("x");
		y = extras.getString("y");
		imgPath = extras.getString("imgPath");
		mapDataId = extras.getString("mapDataId");
		dataType = extras.getString("dataType");
		myGallery = (LinearLayout) findViewById(R.id.mygallery);

		btnLocationRefresh = (Button) findViewById(R.id.btnLocationRefresh);
		btnZoomIn = (Button) findViewById(R.id.btnZoomIn);
		btnZoomOut = (Button) findViewById(R.id.btnZoomOut);
		txtPlaceName = (TextView) findViewById(R.id.txtPlaceName);

		if (dataType.equals("online")) {
			btnShare.setVisibility(View.GONE);
		}

		if (dataType.equals("local")) {
			bmp = Util.decodeFileImg(new File(imgFilePath), 500);
			imageAry = Transactions.getImageaFromPhoneDb("");

		} else if (dataType.equals("online")) {
			new DownloadLoadImage().execute("mapDataId", imgFilePath);
			// imageAry =
			// GPContextHolder.getFriendProfileImagesLayout().getImgList();
		}

		adapter = new ImagePagerAdapter(this, imageAry);

		mapGalleryViewPager = (ViewPager) findViewById(R.id.imgGalleryViewPager);
		mapGalleryViewPager.setAdapter(adapter);
		mapGalleryViewPager.setCurrentItem(adapter.findImageIndex(imgFilePath));

		mapGalleryViewPager.setOnPageChangeListener(pagerListener);

		mapView = (MapView) findViewById(R.id.mapView);
		// pbDefaultM = (ProgressBar) findViewById(R.id.pbDefault);
		// pbDefaultM.setBackgroundColor(Color.GREEN);
		tracker = GoogleAnalyticsTracker.getInstance();
		// Start the tracker in manual dispatch mode...
		tracker.startNewSession("UA-30319861-1", 20, this);

		tracker.trackPageView("/Map");
		tracker.dispatch();
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		mapView.setSatellite(false);
		mapView.setBuiltInZoomControls(false);
		mapView.setSatellite(false);
		mapView.setClickable(true);
		mapView.setSatellite(!mapView.isSatellite());
		mapView.displayZoomControls(false);
		mapController = mapView.getController();
		mapController.setZoom(12);
		txtPlaceName = (TextView) findViewById(R.id.txtPlaceName);

		overlay = mapView.getOverlays();
		pushpin = this.getResources().getDrawable(R.drawable.image_pushpin);
		Bundle zoomGalleryPicExtras = getIntent().getExtras();

		String xx = zoomGalleryPicExtras.getString("x");
		String yy = zoomGalleryPicExtras.getString("y");
		String placeName = zoomGalleryPicExtras.getString("placeName");
		txtPlaceName.setText(placeName);

		mapDataId = zoomGalleryPicExtras.getString("mapDataId");
		String imgPath = zoomGalleryPicExtras.getString("imgPath");

	}

	OnPageChangeListener pagerListener = new OnPageChangeListener() {

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageSelected(int arg0) {

			Image img = imageAry.get(arg0);
			adapter.setImgFilePath(arg0);

		}

	};

	public String getImgFilePath() {
		return imgFilePath;
	}

	public void setImgFilePath(String imgFilePath) {
		this.imgFilePath = imgFilePath;
	}

	public void goToMapGallery(View v) {

		Intent i = new Intent(getApplicationContext(), ImageMapGallery.class);
		i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		startActivity(i);
	}

	private class DownloadLoadImage extends AsyncTask<Object, Void, Bitmap> {

		@Override
		protected Bitmap doInBackground(Object... params) {

			String url = (String) params[1];
			Bitmap resizedbitmap = null;
			System.out.println("doInBackground1");
			Bitmap tempbm = Utility.getOnlineBitmap(url);
			System.out.println("doInBackground2");
			if (tempbm != null) {
				resizedbitmap = Bitmap.createScaledBitmap(tempbm, tempbm.getWidth(), tempbm.getHeight(), true);
			}
			System.out.println("doInBackground3");
			return tempbm;
		}

		@Override
		protected void onPostExecute(Bitmap result) {

			// imgMain.setImageBitmap(result);
		}
	}

	public void btnLocationRefresh(View v) {

		vibrator.vibrate(35);
		try {

			tracker.trackEvent("Clicks", // Category
					"LargGalleryRefreshLocation", // Action
					"clicked", // Label
					1);

		} catch (NullPointerException e) {
			Toast.makeText(mContext, "Your current location is unknown", 1).show();
		}
	}

	public void zoomIn(View v) {
		vibrator.vibrate(35);
		mapController.setZoom(mapView.getZoomLevel() + 1);
		tracker.trackEvent("Clicks", // Category
				"LargeGallleryPicZoomInButton", // Action
				"clicked", // Label
				1);
	}

	public void zoomOut(View v) {
		vibrator.vibrate(35);
		mapController.setZoom(mapView.getZoomLevel() - 1);
		tracker.trackEvent("Clicks", // Category
				"LargeGallleryPicZoomOutButton", // Action
				"clicked", // Label
				1);
	}

	public void btnShare(View v) {

		vibrator.vibrate(35);
		controlVisibilityToggleInVisible();
		lytMain.setDrawingCacheEnabled(true);
		lytMain.buildDrawingCache();

		Bitmap bm = lytMain.getDrawingCache();
		String imageFileName = CustomCameraAct.savePhoto(bm, "ScreenShots");
		controlVisibilityToggleVisible();
		aRoute cRoute = new aRoute(null);

		Image img = imageAry.get(mapGalleryViewPager.getCurrentItem());
		cRoute.setX(img.getimgX());
		cRoute.setY(img.getimgY());
		cRoute.setImgUrl(img.getimgPath());
		cRoute.setCreateDate(img.getimgCreateDate());
		cRoute.setPlaceId(img.getimgPlace());
		cRoute.setScreenShotPath(Environment.getExternalStorageDirectory() + "/GPShare/Photos/ScreenShots/" + imageFileName);
		Globals.setCurrentRoute(cRoute);
		btnShare.setTag(cRoute);

		GTrack("ShareImageFromGallery");
		Intent shareIntent = new Intent(v.getContext(), ShareAct.class);
		v.getContext().startActivity(shareIntent);

		GTrack("savePhoto");
	}

	public void controlVisibilityToggleInVisible() {
		btnShare.setVisibility(View.GONE);
		btnZoomIn.setVisibility(View.GONE);
		btnZoomOut.setVisibility(View.GONE);
		btnLocationRefresh.setVisibility(View.GONE);

	}

	public void controlVisibilityToggleVisible() {
		btnShare.setVisibility(View.VISIBLE);
		btnZoomIn.setVisibility(View.VISIBLE);
		btnZoomOut.setVisibility(View.VISIBLE);
		btnLocationRefresh.setVisibility(View.VISIBLE);

	}

	public void GTrack(String event) {
		GPContextHolder.getMainAct().GoogleA.recordClick(event);
	}

	private void insertPhoto(Image img) {

		// Utility.galleryModel.getLocalImage(img);
		// LinearLayout layout = new LinearLayout(getApplicationContext());
		// layout.setLayoutParams(new LayoutParams(110, 110));
		// layout.setGravity(Gravity.CENTER);
		//
		// ImageView imageView = new ImageView(getApplicationContext());
		// imageView.setLayoutParams(new LayoutParams(100, 100));
		// imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		// Bitmap mbImg = null;
		// imageView.setImageBitmap(mbImg);
		// imageView.setTag(img);
		// imageView.setOnClickListener(new OnClickListener() {
		//
		// @Override
		// public void onClick(View arg0) {
		// Image img = (Image) arg0.getTag();
		// zoomToPic(img.getimgX(), img.getimgY());
		// imgMain.setImageBitmap(ImageUrls.decodeFileImg(new
		// File(img.getImgPath()), 200));
		// }
		// });
		// layout.addView(imageView);
		// myGallery.addView(layout);
		//
		// //iq.addToQueue(img, imageView);
		// //new AsyncImgDl(mbImg, img.getimgPath(), imageView, "local");
	}

	public void notifyDataSetChanged(Image img) {

		imgIndex++;

		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setLayoutParams(new LayoutParams(110, 110));
		layout.setGravity(Gravity.CENTER);

		ImageView imageView = new ImageView(getApplicationContext());
		imageView.setLayoutParams(new LayoutParams(100, 100));
		imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		imageView.setImageBitmap(img.getBitmap());
		imageView.setTag(img);
		imageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Image img = (Image) arg0.getTag();
				// zoomToPic(img.getimgX(), img.getimgY());
				// imgMain.setImageBitmap(ImageUrls.decodeFileImg(new
				// File(img.getImgPath()), 200));
			}
		});

		layout.addView(imageView);
		myGallery.addView(layout);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	public int getcIndex() {
		return cIndex;
	}

	public void setcIndex(int cIndex) {
		this.cIndex = cIndex;
	}

	public ViewPager getMapGalleryViewPager() {
		return mapGalleryViewPager;
	}

	public void setMapGalleryViewPager(ViewPager mapGalleryViewPager) {
		this.mapGalleryViewPager = mapGalleryViewPager;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

}
