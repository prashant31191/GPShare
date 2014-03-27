package com.devtechdesign.gpshare.dialogs;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.db.RouteDatabase;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.utility.GPsharePrefs;
import com.devtechdesign.gpshare.utility.SoapInterface;

public class ShareAct extends Activity implements SoapInterface {
	aRoute					currentARoute;
	Context					context;
	boolean					fbShare;
	Button					btnLocationRefresh, btnZoomIn, btnZoomOut;
	TextView				txtPlaceName;
	private GPShare			GPShare;
	private GPsharePrefs	GPSharePrefs;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.share_act);
		GPShare = (GPShare) getApplicationContext();
		context = this;
		GPSharePrefs = new GPsharePrefs(context, SharedPref);
		currentARoute = Globals.getCurrentRoute();
		GPContextHolder.setShareAct(this);
	}

	public void btnShareFacebook(View v) {
		fbShare = true;
		if (!currentARoute.getImgUrl().equals("")) {

			String imgPath = currentARoute.getImgUrl();

			new DialogUploadFacebookImage(this, currentARoute.getImgUrl(), imgPath);

			GTrack("GalleryImageClick");

		} else {

			if (currentARoute.getRouteId() == null)
				new SyncDataWithOnlineDB().execute("");
			if (currentARoute.getRouteId() != null && !currentARoute.getRouteId().equals("")) {
				new DialogUploadRoutes(this, currentARoute);
			} else {
				new SyncDataWithOnlineDB().execute("");
			}
		}
	}

	public void GTrack(String event) {
		GPContextHolder.getMainAct().GoogleA.recordClick(event);
	}

	public void btnShareEmailText(View v) {

		fbShare = false;

		if (!currentARoute.getImgUrl().equals("")) {

			String imgPath = currentARoute.getImgUrl();
			ArrayList<Uri> imageUris = new ArrayList<Uri>();
			imageUris.add(Uri.fromFile(new File(imgPath)));
			Intent shareIntent = new Intent();
			shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
			shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris);
			shareIntent.setType("image/*");

			startActivity(Intent.createChooser(shareIntent, "Share Geotagged images to.."));
		} else {
			if (currentARoute.getRouteId() != null && !currentARoute.getRouteId().equals("")) {
				initiateSocialMedSharing();
			} else {
				new SyncDataWithOnlineDB().execute("");
			}
		}
	}

	private void initiateSocialMedSharing() {
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "I just shared a route with you using GPShare. Click to view: "
				+ GPSHARE_DOMAIN + "/Map/?RID=" + currentARoute.getRouteId() + "&PMID=0");
		startActivity(Intent.createChooser(sharingIntent, "Share your Route using"));
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

	private class SyncDataWithOnlineDB extends AsyncTask<String, Void, Void> {
		private final ProgressDialog	dialog	= new ProgressDialog(ShareAct.this);

		// can use UI thread here

		protected void onPreExecute() {
			Globals.setpersonId(GPSharePrefs.LoadPreferences("personId"));

			dialog.setMessage("Uploading Data...");
			dialog.show();
		}

		// automatically done on worker thread (separate from UI thread) //
		protected Void doInBackground(final String... args) {

			try {
				Transactions.initiateRouteOnlineDatabaseSync();
			} catch (NullPointerException e) {
			}
			return null;
		}

		// can use UI thread here
		protected void onPostExecute(final Void unused) {

			if (dialog != null && dialog.isShowing()) {
				dialog.dismiss();
			}

			System.out.println("currentARoute.getRouteKey() : " + currentARoute.getRouteKey());
			currentARoute = Transactions.getARouteDynParam(
					RouteDatabase.AllRoutes.ALLROUTES_KEY + " ='" + currentARoute.getRouteKey() + "'").get(0);
			System.out.println("currentARoute.getRouteKey() : " + currentARoute.getRouteId());
			if (currentARoute.getRouteId() != null && !currentARoute.getRouteId().equals("")) {
				if (fbShare) {
					new DialogUploadRoutes(context, currentARoute);
				} else {
					initiateSocialMedSharing();
				}
			}
		}

		@Override
		protected void onCancelled() {
			// TODO Auto-generated method stub//
			super.onCancelled();
		}
	}
}
