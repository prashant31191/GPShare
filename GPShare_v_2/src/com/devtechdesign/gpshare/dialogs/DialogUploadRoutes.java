package com.devtechdesign.gpshare.dialogs;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.Globals;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.data.db.RouteDatabase.AllRoutes;
import com.devtechdesign.gpshare.elements.Route;
import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.facebook.BaseRequestListener;
import com.devtechdesign.gpshare.facebook.FaceBookLogin;
import com.devtechdesign.gpshare.utility.FBUtility;
import com.devtechdesign.gpshare.utility.GPsharePrefs;
import com.devtechdesign.gpshare.utility.SoapInterface;
import com.devtechdesign.gpshare.utility.Utility;
import com.devtechdesign.gpshare.utility.WebTransactions;

import dev.tech.auth.DTDLogin;

public class DialogUploadRoutes extends Dialog implements SoapInterface {
	Context mContext;
	Dialog routeTagDialog;
	String currentPersonId = "";
	Boolean facebookTokenValidated = false;
	String routeId, routeKey, currentImgPath;
	Transactions TransactionSet;

	aRoute currentRoute;
	private GPsharePrefs GPSharePrefs;

	public DialogUploadRoutes(Context context, aRoute currentRoute) {
		super(context);
		this.currentRoute = currentRoute;
		mContext = context;
		GPSharePrefs = new GPsharePrefs(mContext, SharedPref);
		TransactionSet = new Transactions(mContext);

		routeKey = currentRoute.getRouteKey();
		routeTagDialog = this;
		routeId = currentRoute.getRouteId();
		currentPersonId = GPSharePrefs.LoadPreferences("personId");

		if (routeId != null) {
			if (!routeId.equals("")) {
				facebookGeotag();
			} else {
				ifRouteIdNullOrBlank();// if route is null or blank than it is
										// not uploaded; If Route is not
										// uploaded that we don't have a route
										// id for the route id posted to
										// faceboo/gpshare text;
			}
		} else {
			ifRouteIdNullOrBlank();
		}
	}

	protected void showUploadDialog(final ArrayList<aRoute> routesToUpload) {

		String routeCount = String.valueOf(routesToUpload.size());

		new AlertDialog.Builder(mContext)

		.setTitle("Internet required").setMessage(routeCount + " routes Not yet Uploaded. Upload now?").setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				if (!currentPersonId.equals("")) {
					syncronizeOnlinePhoneRouteDB(routesToUpload);

				} else {
					Toast.makeText(mContext, "You must be logged in to upload data! " + "Please Login or Register with GPShare.", 4).show();

					Intent loginIntent = new Intent(mContext, DTDLogin.class);
					loginIntent.putExtra("placeMarkType", "currentPlace");
					loginIntent.putExtra("mapDataId", "");
					mContext.startActivity(loginIntent);
					dialog.dismiss();
				}
				dialog.dismiss();
			}
		}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}

		}).setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface d) {
				d.dismiss();
			}
		}).show();
	}

	private void ifRouteIdNullOrBlank() {
		System.out.println("routeId is null try upload");
		ArrayList<aRoute> routeListToUPload = Transactions.getRoutesFromPhoneDb(currentPersonId, "false", "false", "false", "");

		if (routeListToUPload.size() == 0) {
			Toast.makeText(mContext, "Cannot tag a route in progress...Feature Coming Soon!.", 6).show();
		} else {
			showUploadDialog(routeListToUPload);
			Toast.makeText(mContext, "Route Must be Uploaded in Order To Tag", 6).show();
		}
	}

	private void syncronizeOnlinePhoneRouteDB(ArrayList<aRoute> routesToUpload) {
		try {

			// insert all routes into online database that are not yet uploaded
			System.out.println("routeId: " + routeId);
			Transactions.insertAllRoutesOnlineDb(routesToUpload);

			// update new found route key for current routeButtonOverlay.
			routeId = Transactions.getARouteReturnId(routeKey);
			System.out.println("routeId: " + routeId);
		} catch (NullPointerException e) {
			Toast.makeText(mContext, "There was an error updating your data. Tag data may " + "not reflect its current state. Error Message: " + e.getMessage(), 3).show();
			System.out.println("XXsyncronizeOnlinePhoneRouteDB: " + e.getMessage() + currentPersonId);
		}
	}

	public void facebookGeotag() {
		// Instantiate the asynrunner object for asynchronous api calls.

		if (!Utility.mFacebook.isSessionValid()) {

			new AlertDialog.Builder(mContext).setTitle("Login").setMessage("Log into Facebook").setPositiveButton("Login", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Intent facebookLogin = new Intent(mContext, FaceBookLogin.class);
					facebookLogin.putExtra("currentImgPath", "");
					mContext.startActivity(facebookLogin);
					facebookTokenValidated = true;
				}
			}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					Activity a = (Activity) mContext;
					a.finish();
				}

			}).setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface d) {
					routeTagDialog.dismiss();
					Activity a = (Activity) mContext;
					a.finish();
				}
			}).show();

		} else {

			postRouteToFacebookTimeLine();

			// Transactions.sendEmail("mattlroberts@live.com",
			// "mattlroberts@live.com",
			// "Matthew Roberts tagged you in a route with Geocam!! Click --> "
			// ,"http://www.10000fish.com/Geocam?PMID=0&RID=" + routeId
			// , "Matthew Roberts tagged you in a route with Geocam!!");

		}
	}

	public void postRouteToFacebookTimeLine() {

		final aRoute currentRoute = new aRoute(null);
		ArrayList<aRoute> routeSegList = Transactions.selectRoutesViaSql("Select * from " + AllRoutes.ALLROUTESS_TABLE_NAME + " where " + AllRoutes.ALLROUTES_ID + " = " + routeId);

		aRoute lastSegment = routeSegList.get(0);
		try {
			currentRoute.setElapsedTime(Transactions.convertMillisecondsToMinutes(Integer.valueOf(lastSegment.getElapsedTime())));
		} catch (NumberFormatException ne) {
			currentRoute.setElapsedTime("");
		}
		currentRoute.setRouteId(routeId);

		try {
			currentRoute.setDistance(String.valueOf(Transactions.convertMetersPrefUom(Double.valueOf(lastSegment.getDistance()), GPShare.getUOM())));
		} catch (NumberFormatException ne) {
			currentRoute.setDistance("0.0");
		}

		// if the route file was successfully written,post to facebook

		new AsyncTask<aRoute, Void, Boolean>() {

			@Override
			protected Boolean doInBackground(aRoute... arg0) {

				return WebTransactions.writeFacebookGraphRouteFile(arg0[0]);

			}

			@Override
			protected void onPostExecute(Boolean response) {
				if (response) {
					FBUtility.postFbRouteToTimeLine(mContext, currentRoute);

					Activity a = (Activity) mContext;
					a.finish();
				}
			};
		}.execute(currentRoute);
	}

	public void postRouteToWall(String descriptionMessage) {
		String totalTime = Route.gettotalTime();
		String recordCount = Route.getrecordCount();
		String averageSpeed = Route.getaverageSpeed();
		String topSpeed = Route.gettopSpeed();
		String distance = Route.getdistance();
		String currentPlace = Globals.getCurrentPlace();

		Bundle params = new Bundle();
		params.putString("caption", "Click To View!");

		// Transactions.domainName + "/GPShare?PMID=0&RID=" + routeId
		params.putString("link", GPSHARE_DOMAIN + "/GPShare?PMID=0&RID=" + routeId);
		params.putString("description", "Place: " + currentPlace + " Distance: " + distance + " Elapsed Time: " + totalTime);
		// + "</center>" + "<center>TopSpeed: " + topSpeed + "</center>");

		params.putString("picture", "http://" + GPSHARE_DOMAIN + "/map/Images/Site/globe_logo2.png");
		// Globals.getFirstLastName() + " uploaded a route with GPShare"
		params.putString("message", descriptionMessage);// ("message",
														// "".getBytes());

		Utility.mAsyncRunner.request("me/feed", params, "POST", new WallPostListener(), null);
	}

	public class WallPostListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {

			routeTagDialog.dismiss();
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

}
