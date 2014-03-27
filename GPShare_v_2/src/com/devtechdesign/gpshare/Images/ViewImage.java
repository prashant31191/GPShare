package com.devtechdesign.gpshare.Images;

import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.MainActivity;
import com.devtechdesign.gpshare.data.db.Transactions;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class ViewImage extends Activity implements com.devtechdesign.gpshare.utility.SoapInterface {
	private String imgId;
	private Button btnSave, btnBack;
	private static SharedPreferences sharedPreferences;
	String[] fileNames;
	com.devtechdesign.gpshare.utility.GPsharePrefs Prefs;
	private ImageView ivProfileImage;
	private ImageView ivCurrentProfileImage;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.update_profile_image);

		inititializePrefs();
		// setUiControls();
		Globals.setpersonId(Prefs.LoadPreferences("personId"));
		setImgId();

		fileNames = new String[1];

		Uri imgUri = Uri.withAppendedPath(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, imgId);
		fileNames[0] = getFileName(imgUri);

		setProfileImg(imgUri);

		btnSave.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				{
					Intent myIntent = new Intent(getApplicationContext(), UploadService.class);
					myIntent.putExtra("fileNames", fileNames);
					myIntent.putExtra("userName", Prefs.LoadPreferences("email"));
					startService(myIntent);
					Prefs.savePreferences("imgProfileUrl", GPSHARE_DOMAIN + "/map/images/profile/" + "P" + Prefs.LoadPreferences("personId") + "_.jpg");

					Intent mainAct = new Intent(getApplicationContext(), MainActivity.class);
					startActivity(mainAct);
					finish();
				}
			}
		});

		btnBack.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				{
					finish();
				}
			}
		});
	}

	private void setProfileImg(Uri imgUri) {
		try {

			Bitmap currentProfileImage = Transactions.LoadImage(Transactions.GPSHARE_DOMAIN + "/map/images/profile/" + "P" + Prefs.LoadPreferences("personId") + "_.jpg",
					bmOptions());
			ivCurrentProfileImage.setImageBitmap(currentProfileImage);
		} catch (NullPointerException e) {

		}
		System.out.println("Img URI: " + imgUri);
		ivProfileImage.setImageURI(imgUri);
	}

	private void setImgId() {
		Intent i = getIntent();
		Bundle extras = i.getExtras();
		imgId = extras.getString("imageId");
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

	public String getFileName(Uri uri) {
		String[] projection = { MediaStore.Images.Media.DATA };
		Cursor cur = getContentResolver().query(uri, projection, null, null, null);
		cur.moveToFirst();
		String path = cur.getString(cur.getColumnIndex(MediaStore.Images.Media.DATA));

		return path;
	}

	public void inititializePrefs() {
		sharedPreferences = getSharedPreferences(SharedPref, 0);
		sharedPreferences.edit();

		Prefs = new com.devtechdesign.gpshare.utility.GPsharePrefs(getApplicationContext(), SharedPref);
	}

	private BitmapFactory.Options bmOptions() {
		BitmapFactory.Options bmOptions;
		bmOptions = new BitmapFactory.Options();
		bmOptions.inSampleSize = 1;
		return bmOptions;
	}

	// private void setUiControls() {
	// btnBack = (Button) findViewById(R.id.btnNotRightNow);
	// btnSave = (Button) findViewById(R.id.btnSave);
	// ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
	// ivCurrentProfileImage = (ImageView)
	// findViewById(R.id.ivCurrentProfileImage);
	// }
}
