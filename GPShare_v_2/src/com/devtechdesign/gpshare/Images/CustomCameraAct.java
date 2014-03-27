package com.devtechdesign.gpshare.Images;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.Size;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.FileObserver;
import android.view.Display;
import android.view.OrientationEventListener;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.MainActivity;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.db.RouteDatabase;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.dialogs.DialogTrackingNotOnForCameraCoords;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.map.MapFragAct;
import com.devtechdesign.gpshare.utility.CommonFunctions;
import com.devtechdesign.gpshare.utility.DateNow;
import com.devtechdesign.gpshare.utility.GPsharePrefs;
import com.devtechdesign.gpshare.utility.SoapInterface;

import dev.tech.images.Util;
import dev.tech.util.GLibTrans;

@SuppressLint("NewApi")
public class CustomCameraAct extends Activity implements SoapInterface, SurfaceHolder.Callback {
	static final int FOTO_MODE = 0;
	Camera mCamera;
	boolean mPreviewRunning = false;
	private Context mContext = this;
	boolean bIsAutoFocused = false;
	static String filename = "";
	static File mDataFile;
	String calledactivity;
	GPsharePrefs prefs;
	Bitmap bmp, itembmp;
	static Bitmap mutableBitmap;
	static File imageFileName = null;
	private int exposureCompensation = 0;
	private double exoisureStep = 0.0;
	private String flashMode;
	private Button btnSetFlashMode;
	private FileObserver observer;
	OrientationEventListener myOrientationEventListener;
	int orientation = 0;
	int orientationForSaving = 0;
	private final String imgFolder = "/GPShare/Images";
	private final String uploadFolder = "/GPShare/Upload";
	private final String thumbNailFolder = "/GPShare/Photos/Thumbs";
	private final String Geocam = "/GPShare";
	private final String GpxFolder = "/GPShare/Gpx";
	
	private ImageView ivImagePreview;
	private File imageFileFolder;

	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);

		getWindow().setFormat(PixelFormat.TRANSLUCENT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		prefs = new GPsharePrefs(getApplicationContext(), SharedPref);
		setContentView(R.layout.camera);

		createDirectories();

		mSurfaceView = (SurfaceView) findViewById(R.id.surface_camera);
		mSurfaceHolder = mSurfaceView.getHolder();
		mSurfaceHolder.addCallback(this);
		mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		File f = new File(Environment.getExternalStorageDirectory() + "/capp/");
		btnSetFlashMode = (Button) findViewById(R.id.btnSetFlashMode);
		ivImagePreview = (ImageView) findViewById(R.id.ivImagePreview);

		imageFileFolder = new File(Environment.getExternalStorageDirectory(), "/GPShare/Photos");

		if (!imageFileFolder.exists()) {
			String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
			new File(extStorageDirectory + "/GPShare/Photos/").mkdirs();
		}

		myOrientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
			@Override
			public void onOrientationChanged(int arg0) {

				// determineOrientation(arg0);

				// System.out.println("onOrientationChanged changed arg0; " +
				// arg0);
				determineOrientation(arg0);
			}
		};

		if (myOrientationEventListener.canDetectOrientation()) {
			// Toast.makeText(this, "Can DetectOrientation: " ,
			// Toast.LENGTH_LONG).show();
			myOrientationEventListener.enable();
		}

		try {
			if (!f.exists() || f.isFile())
				f.mkdir();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void determineOrientation(int or) {
		if (or > 240 && or < 313) {
			orientation = 0;
		} else {
			orientation = 90;
		}
		if (cameraParams != null) {
			// cameraParams.setRotation(0);

			// mCamera.setParameters(cameraParams);
		}

	}

	private void checkForRunningGpsService() {
		if (!GPContextHolder.getMainAct().isGpsServiceRunning(getApplicationContext())) {
			new DialogTrackingNotOnForCameraCoords(CustomCameraAct.this);
		}
	}

	@SuppressLint("NewApi")
	public void setProperties() {
		String eC = prefs.LoadPreferences("exposureCompensation");
		if (eC.equals("")) {
			exposureCompensation = 0;
		} else {
			exposureCompensation = Integer.valueOf(eC);
		}
	}

	@SuppressLint("NewApi")
	public void setFlashMode(View v) {
		flashMode = prefs.LoadPreferences("flashMode");
		if (flashMode.equals("")) {
			prefs.savePreferences("flashMode", "off");
		} else if (flashMode.equals("on")) {
			prefs.savePreferences("flashMode", "");
		} else if (flashMode.equals("off")) {
			prefs.savePreferences("flashMode", "on");
		}
		initializeFlashMode();
		mCamera.setParameters(cameraParams);
	}

	@SuppressLint("NewApi")
	public void initializeFlashMode() {
		flashMode = prefs.LoadPreferences("flashMode");
		if (flashMode.equals("")) {
			cameraParams.setFlashMode(Parameters.FLASH_MODE_AUTO);
			btnSetFlashMode.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ic_flash_auto));
		} else if (flashMode.equals("on")) {
			cameraParams.setFlashMode(Parameters.FLASH_MODE_ON);
			btnSetFlashMode.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ic_flash_on));
		} else if (flashMode.equals("off")) {
			cameraParams.setFlashMode(Parameters.FLASH_MODE_OFF);
			btnSetFlashMode.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.ic_flash_off));
		}

		mCamera.setParameters(cameraParams);
	}

	@SuppressLint("NewApi")
	public void increaseExposure(View v) {
		exposureCompensation += 1;
		prefs.savePreferences("exposureCompensation", String.valueOf(exposureCompensation));
		cameraParams.setExposureCompensation(exposureCompensation);
		mCamera.setParameters(cameraParams);
	}

	@SuppressLint("NewApi")
	public void decreaseExposure(View v) {
		exposureCompensation -= 1;
		prefs.savePreferences("exposureCompensation", String.valueOf(exposureCompensation));
		cameraParams.setExposureCompensation(exposureCompensation);
		mCamera.setParameters(cameraParams);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
		public void onPictureTaken(byte[] imageData, Camera c) {
			if (imageData == null) {

			} else {

				saveImage(imageData);
				mCamera.startPreview();
			}
		}
	};

	private void saveImage(byte[] imageData) {
		String fileName = getDateFileName();

		imageFileName = new File(imageFileFolder, fileName);

		// Bitmap bm = convertBytesToBitmap(jpeg[0]);

		// rotateAndSaveImg(bm);

		try {
			FileOutputStream fos = new FileOutputStream(imageFileName.getPath());

			fos.write(imageData);
			fos.close();

			MainActivity.glogalDataChanged = true;

			if (orientation == 90) {
				Bitmap bm2 = rotateBitmap(imageFileName.getPath());
				saveBitmapToFile(bm2);
			}

			Toast.makeText(getApplicationContext(), "Image Saved!", 1).show();
			// saveRotatedBitmapImg(rotateBitmap(imageFileName.getPath()));
			new SaveThumbPhotoTask().execute(fileName);

		} catch (java.io.IOException e) {
			e.printStackTrace();
		}
	}

	protected void onResume() {

		super.onResume();
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	protected void onStop() {

		super.onStop();
	}

	public void surfaceCreated(SurfaceHolder holder) {

		mCamera = Camera.open();

		Parameters params = mCamera.getParameters();
		params.setRotation(90);
		Size sizes = params.getPictureSize();

		checkForRunningGpsService();

	}

	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

		if (mPreviewRunning) {
			mCamera.stopPreview();
		}

		try {
			mCamera.setPreviewDisplay(holder);
			setParameters();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mCamera.startPreview();
		mPreviewRunning = true;
	}

	public void surfaceDestroyed(SurfaceHolder holder) {

		mCamera.stopPreview();
		mPreviewRunning = false;
		mCamera.release();
	}

	Camera.Parameters cameraParams;

	@SuppressWarnings("deprecation")
	@SuppressLint("NewApi")
	private void setParameters() {
		try {
			setProperties();
			cameraParams = mCamera.getParameters();
			cameraParams.set("jpeg-quality", 100);
			cameraParams.setPictureFormat(PixelFormat.JPEG);
			cameraParams.setExposureCompensation(exposureCompensation);

			initializeFlashMode();

			mCamera.setParameters(cameraParams);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onBackPressed() {
		if (GPContextHolder.getProfileImagesLayout() != null)
			GPContextHolder.getProfileImagesLayout().resetImages();

		super.onBackPressed();
	}

	private SurfaceView mSurfaceView;
	private SurfaceHolder mSurfaceHolder;
	private String imagePath;

	public void capture(View v) {

		try {

			AutoFocusCallBackImpl autoFocusCallBack = new AutoFocusCallBackImpl();
			mCamera.autoFocus(autoFocusCallBack);
			GTrack("cameraPictureTaken");
		} catch (Exception exe) {

		}

	}

	public static void GTrack(String event) {
		GPContextHolder.getMainAct().GoogleA.recordClick(event);
	}

	private class AutoFocusCallBackImpl implements Camera.AutoFocusCallback {

		@SuppressLint("NewApi")
		public void onAutoFocus(boolean success, Camera camera) {

			try {
				if (success)
					mCamera.takePicture(null, mPictureCallback, mPictureCallback);
			} catch (Exception e) {

			}
		}
	}

	public static String savePhoto(Bitmap bmp, String filePath) {

		File imageFileFolder = new File(Environment.getExternalStorageDirectory(), "/GPShare/Photos/" + filePath + "/");

		FileOutputStream out = null;
		imageFileName = new File(imageFileFolder, getDateFileName());

		try {
			out = new FileOutputStream(imageFileName);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, out);
			out.flush();
			out.close();
			// scanPhoto(imageFileName.toString());
			out = null;
		} catch (Exception e) {
			e.printStackTrace();
		}

		GTrack("savePhoto");
		return imageFileName.getName();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		myOrientationEventListener.disable();
		super.onDestroy();
	}

	private Bitmap rotateBitmap(Bitmap tempBitmap) {

		// BitmapFactory.Options options = new BitmapFactory.Options();
		// options.inSampleSize = 2; // 1/4 of the original image
		//
		// Bitmap tempBitmap = BitmapFactory.decodeFile(imagePath, options);
		int tempW = tempBitmap.getWidth();
		int tempH = tempBitmap.getHeight();

		Matrix mtx = new Matrix();
		mtx.postRotate(orientation);
		return Bitmap.createBitmap(tempBitmap, 0, 0, tempW, tempH, mtx, true);

	}

	private void changeExfDataOrientation(String impPath) {
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(impPath);

			String exifOrientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION);
			System.out.println("exifOrientation: " + exifOrientation);
			exif.setAttribute(ExifInterface.TAG_ORIENTATION, "0");

			exif.saveAttributes();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Bitmap convertBytesToBitmap(byte[] byteAry) {

		return BitmapFactory.decodeByteArray(byteAry, 0, byteAry.length);
	}

	private void saveBitmapToFile(Bitmap bm) {

		FileOutputStream out2 = null;
		try {
			out2 = new FileOutputStream(imageFileName.getPath());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bm.compress(Bitmap.CompressFormat.JPEG, 90, out2);
	}

	private Bitmap rotateBitmap(String imagePath) {

		Bitmap src = Util.decodeFileImg(new File(imagePath), 500);

		// //
		// // BitmapFactory.Options options = new BitmapFactory.Options();
		// // options.inSampleSize = 2; // 1/4 of the original image
		//
		// Bitmap tempBitmap = BitmapFactory.decodeFile(imagePath, options);
		int tempW = src.getWidth();
		int tempH = src.getHeight();

		Matrix mtx = new Matrix();
		mtx.postRotate(orientation);
		return Bitmap.createBitmap(src, 0, 0, tempW, tempH, mtx, true);

	}

	private static String getDateFileName() {
		Calendar c = Calendar.getInstance();
		String date = fromInt(c.get(Calendar.MONTH)) + fromInt(c.get(Calendar.DAY_OF_MONTH)) + fromInt(c.get(Calendar.YEAR)) + fromInt(c.get(Calendar.HOUR_OF_DAY))
				+ fromInt(c.get(Calendar.MINUTE)) + fromInt(c.get(Calendar.SECOND));
		return date.toString() + ".jpg";
	}

	class SaveThumbPhotoTask extends AsyncTask<String, String, String> {
		private Bitmap thumbBitmap;

		@Override
		protected String doInBackground(String... fileName) {

			// Bitmap bitmap = BitmapFactory.decodeByteArray(jpeg[0] , 0,
			// jpeg[0].length);
			// bitmap.compress(Bitmap.CompressFormat.PNG, 70, fos);

			thumbBitmap = Util.decodeFileImg(new File(imageFileName.getPath()), 100);
			saveThumbnail(fileName[0], thumbBitmap);
			insertNewImage(imageFileName.getPath(), imageFileName.getPath());
			return calledactivity;
		}

		@Override
		protected void onPostExecute(String result) {

			Toast.makeText(getApplicationContext(), "Image Saved!", Toast.LENGTH_LONG).show();

			if (thumbBitmap != null) {
				ivImagePreview.setImageBitmap(thumbBitmap);
				ivImagePreview.setTag(imageFileName.getPath());
				ivImagePreview.setOnClickListener(ivOnclick);
			}
			super.onPostExecute(result);
		}
	}

	private void goToMapImageGallery() {

		Intent fullGalleryPagerIntent = new Intent(getApplicationContext(), FullScreenGalleryPager.class);
		fullGalleryPagerIntent.putExtra("dataType", "offline");
		fullGalleryPagerIntent.putExtra("currentImgPath", imageFileName.getAbsolutePath());
		startActivity(fullGalleryPagerIntent);
	}

	OnClickListener ivOnclick = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			goToMapImageGallery();
		}
	};

	@SuppressWarnings("static-access")
	protected void insertNewImage(String imagePath, String imgId) {
		System.out.println("props: " + prefs);
		String currentPlace = prefs.LoadPreferences("currentPlace");
		System.out.println("props: " + currentPlace);
		String currentDate = DateNow.getCurrentDateTime();

		String currentCoordinates = Globals.getcurrentCoords().toString();
		String currentX = CommonFunctions.getXyValuesFromCoords(currentCoordinates)[0].toString();
		String currentY = CommonFunctions.getXyValuesFromCoords(currentCoordinates)[1].toString();

		ContentValues imageRecord = new ContentValues();
		imageRecord.put("image_path", imagePath);
		imageRecord.put("image_place_name", currentPlace);
		imageRecord.put("image_path", imagePath);
		imageRecord.put("image_createdate", GLibTrans.getDateTime());
		imageRecord.put("image_x", currentX);
		imageRecord.put("image_y", currentY);
		imageRecord.put("image_id", currentY);

		Transactions.mDb.insert(RouteDatabase.Images.IMAGE_TABLE_NAME, null, imageRecord);

	}

	public static String fromInt(int val) {
		return String.valueOf(val);
	}

	/**
	 * Cook Dinner Kathleen!!! Save Thumbnail for quick retrieval in galleries
	 */
	public static void saveThumbnail(String fileName, Bitmap bitmap) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();

		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		System.out.println("props: " + "HITTTTTTTTTTTTTTTTTTTT");
		// you can create a new file name "test.jpg" in sdcard folder.

		File f = new File(Environment.getExternalStorageDirectory() + "/GPShare/Photos/Thumbs/" + fileName);
		System.out.println("props: " + "HITTTTTTTTTTTTTTTTTTTT");
		try {

			f.createNewFile();
			// write the bytes in file
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean StoreByteImage(Context mContext, byte[] imageData, int quality, String expName) {

		FileOutputStream fileOutputStream = null;

		try {
			filename = "img_" + System.currentTimeMillis() + ".jpg";

			mDataFile = new File(Environment.getExternalStorageDirectory() + "/capp/", filename);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;

			Bitmap myImage = BitmapFactory.decodeByteArray(imageData, 0, imageData.length, options);

			fileOutputStream = new FileOutputStream(mDataFile);

			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

			myImage.compress(CompressFormat.JPEG, quality, bos);

			bos.flush();
			bos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();

		} catch (IOException e) {
			e.printStackTrace();
		}

		return true;
	}

	public static void syncOldpicsIntoThumbnails() {

		List<String> fileList = new ArrayList<String>();
		File root = new File(Environment.getExternalStorageDirectory() + "/GPShare/Photos/");

		File[] fileNames = root.listFiles();
		fileList.clear();
		for (File file : fileNames) {
			Bitmap thumbBitmap = Util.decodeFileImg(new File(file.getPath()), 100);

			try {
				saveThumbnail(file.getName(), thumbBitmap);

			} catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
	}

	public void cancel(View v) {
		if (GPContextHolder.getProfileImagesLayout() != null)
			GPContextHolder.getProfileImagesLayout().resetImages();
		finish();
	}

	public void createDirectories() {

		String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		File imageFolder = new File(extStorageDirectory + imgFolder);
		File folderUpload = new File(extStorageDirectory + uploadFolder);
		File GeoCamFolder = new File(extStorageDirectory + Geocam);
		File thumbNailFlder = new File(extStorageDirectory + thumbNailFolder);
		File gpxFlder = new File(extStorageDirectory + GpxFolder);

		File imageFileFolderThumbs = new File(Environment.getExternalStorageDirectory(), "/GPShare/Photos/Thumbs");
		// screenShotFlder.mkdir();

		if (!imageFileFolderThumbs.exists()) {
			imageFileFolderThumbs.mkdir();
			thumbNailFlder.mkdir();
			imageFolder.mkdir();
			folderUpload.mkdir();
			folderUpload.mkdir();
			gpxFlder.mkdir();
		}

		File imageFileFolder = new File(Environment.getExternalStorageDirectory(), "/GPShare/Photos");

		if (!imageFileFolder.exists()) {
			new File(extStorageDirectory + "/GPShare/Photos/").mkdirs();
		}

		File screenShotFileFolder = new File(Environment.getExternalStorageDirectory(), "/GPShare/Photos/ScreenShots");

		if (!screenShotFileFolder.exists()) {
			new File(extStorageDirectory + "/GPShare/Photos/ScreenShots").mkdirs();
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}
}