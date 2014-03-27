package com.devtechdesign.gpshare.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.MainActivity;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.utility.SoapInterface;

public class DialogTrackingNotOnForCameraCoords extends Dialog implements SoapInterface {

	Context mContext;
	Dialog routeTagDialog;
	String currentPersonId = "";
	Boolean facebookTokenValidated = false;
	String routeId, routeKey, currentImgPath;
	Transactions TransactionSet;
	private Context context;

	public DialogTrackingNotOnForCameraCoords(final Context context) {
		super(context);
		this.context = context;

		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setTitle("Tracking is not On. Images will not have GPS coordinates. Please turn on tracking.");
		alert.setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				startGpsService();
				dialog.dismiss();
				return;
			}
		});
		alert.setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {

				dialog.dismiss();
				return;
			}
		}).show();
	}

	public void startGpsService() {

		MainActivity mC = GPContextHolder.getMainAct();
		mC.startTracking();

		// Intent intent = new Intent(GPXService.ACTION_FOREGROUND);
		// intent.setClass(context.getApplicationContext(), GPXService.class);
		// intent.putExtra("normalTracking", true);
		// intent.putExtra("realTimeTracking", false);
		// intent.putExtra("realTimeInterval", 10000);
		//
		// context.getApplicationContext().startService(intent);
		// Intent service = new Intent(context.getApplicationContext(),
		// GPXService.class);
		// context.getApplicationContext().startService(service);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

}
