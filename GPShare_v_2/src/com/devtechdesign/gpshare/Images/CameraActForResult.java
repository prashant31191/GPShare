package com.devtechdesign.gpshare.Images;

import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.GPShareDataModel;
import com.devtechdesign.gpshare.data.db.DatabaseControl;
import com.devtechdesign.gpshare.data.db.RouteDatabase;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.utility.CommonFunctions;
import com.dtd.dbeagen.db.GPShareDatabase.PlaceMark;
import com.dtd.dbeagen.db.elements.aPlaceMark;

import dev.tech.images.Util;
import dev.tech.util.GLibTrans;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.view.Menu;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.widget.ImageView;

public class CameraActForResult extends Activity {
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private ImageView image;
	private final String imgFolder = "/GPShare/Images";
	private final String uploadFolder = "/GPShare/Upload";
	private final String thumbNailFolder = "/GPShare/Photos/Thumbs";
	private final String Geocam = "/GPShare";
	private final String GpxFolder = "/GPShare/Gpx";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera_act_for_result);
		createDirectories();
		startCam();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera_act_for_result, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
			Bitmap photo = (Bitmap) data.getExtras().get("data");
			// image.setImageBitmap(photo);

			saveBitmapToFile(photo);
			// Uri uri = getImageUri(getApplicationContext(), photo);
			// System.out.println("getImageUri: " + uri.toString());
			startCam();
		}
	}

	public Uri getImageUri(Context inContext, Bitmap inImage) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();
		inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
		System.out.println("path: " + path);
		return Uri.parse(path);
	}

	public void startCam() {
		Intent cameraI = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		startActivityForResult(cameraI, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	public void createDirectories() {

		String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
		File imageFolder = new File(extStorageDirectory + imgFolder);
		File folderUpload = new File(extStorageDirectory + uploadFolder);
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

	private void saveBitmapToFile(Bitmap bm) {
		File imageFileFolder = new File(Environment.getExternalStorageDirectory(), "/GPShare/Photos");
		File imageFile = new File(imageFileFolder, getDateFileName());
		FileOutputStream out2 = null;
		try {
			out2 = new FileOutputStream(imageFile.getPath());

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		bm.compress(Bitmap.CompressFormat.JPEG, 90, out2);

		new SaveThumbPhotoTask().execute(imageFile.getPath());
	}

	private static String getDateFileName() {
		Calendar c = Calendar.getInstance();
		String date = fromInt(c.get(Calendar.MONTH)) + fromInt(c.get(Calendar.DAY_OF_MONTH)) + fromInt(c.get(Calendar.YEAR)) + fromInt(c.get(Calendar.HOUR_OF_DAY))
				+ fromInt(c.get(Calendar.MINUTE)) + fromInt(c.get(Calendar.SECOND));
		return date.toString() + ".jpg";
	}

	public static String fromInt(int val) {
		return String.valueOf(val);
	}

	class SaveThumbPhotoTask extends AsyncTask<String, String, String> {
		private Bitmap thumbBitmap;

		@Override
		protected void onPostExecute(String result) {

			super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(String... params) {
			String imageFilePath = String.valueOf(params[0]);
			File file = new File(imageFilePath);
			Bitmap thumbBitmap = Util.decodeFileImg(file, 100);
			saveThumbnail(file.getName(), thumbBitmap);
			insertNewImage(imageFilePath, imageFilePath);
			return null;
		}
	}

	public static void saveThumbnail(String fileName, Bitmap bitmap) {
		ByteArrayOutputStream bytes = new ByteArrayOutputStream();

		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
		// you can create a new file name "test.jpg" in sdcard folder.

		File f = new File(Environment.getExternalStorageDirectory() + "/GPShare/Photos/Thumbs/" + fileName);
		try {

			f.createNewFile();
			// write the bytes in file
			FileOutputStream fo = new FileOutputStream(f);
			fo.write(bytes.toByteArray());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected void insertNewImage(String imagePath, String imgId) {
		String currentX;
		String currentY;

		if (Globals.getcurrentCoords() != null) {
			String currentCoordinates = Globals.getcurrentCoords().toString();
			currentX = CommonFunctions.getXyValuesFromCoords(currentCoordinates)[0].toString();
			currentY = CommonFunctions.getXyValuesFromCoords(currentCoordinates)[1].toString();
		} else {
			currentX = "0.0";
			currentY = "0.0";
		}

		aPlaceMark newImage = new aPlaceMark();
		newImage.setx(currentX);
		newImage.sety(currentY);
		newImage.setLargeUrl(imagePath);
		String thumbUrl = imagePath.replace("/Photos/", "/Photos/Thumbs/");
		newImage.setpicUrl(thumbUrl);
		newImage.setPlaceId("");
		newImage.setCreateDate(GLibTrans.getDateTime());

		DatabaseControl.getInstance().pmDbc.insertNewPlaceMark(newImage);
		GPShareDataModel.getInstance().placeMarks.addFirst(newImage);

	}
}
