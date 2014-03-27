package com.devtechdesign.gpshare.facebook;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

public class FBImgUlSvc extends Service {
	private Handler		handler;
	private int			filesToUploadCount	= 0;
	private String[]	filesToUpload;

	@Override
	public void onCreate() {
		handler = new Handler();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		Bundle extras = intent.getExtras();
		filesToUpload = extras.getStringArray("fileNames");

		try {
			uploadImages();
		} catch (Exception e) {

		}
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	private void uploadImages() {

		final ArrayList<String> listToSent = new ArrayList<String>();
		for (int u = 0; u < filesToUpload.length; u++)

			listToSent.add(filesToUpload[u]);
		filesToUploadCount++;

		if (filesToUpload.length > 0)
			createNotification(listToSent);

	}

	private void createNotification(final ArrayList<String> files) {
		handler.post(new Runnable() {

			public void run() {
				if (filesToUploadCount != 0) {
					new UploadFBPImg(getApplicationContext(), files, 0, handler).execute(0);
				}

			}
		});
	}

	public void makeToast(String message) {
		Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
	}
}
