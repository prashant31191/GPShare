package com.devtechdesign.gpshare.utility;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;

import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.MainActivity;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import dev.tech.gpsharelibs.DTD;

public class GCMHelper implements SoapInterface {

	AsyncTask<Void, Void, Void> mRegisterTask;
	Context context;
	GPsharePrefs prefs;

	public GCMHelper(Context context) {
		this.context = context;
		prefs = new GPsharePrefs(context, SharedPref);
		registerGcm();
	}

	public static final String EXTRA_MESSAGE = "message";

	/**
	 * You can use your own project ID instead. This sender is a test CCS echo
	 * server.
	 */

	// Tag for log messages.
	static final String TAG = "GCMDemo";

	GoogleCloudMessaging gcm;
	String regid;
	MainActivity mc;

	public void registerGcm() {

		mc = (MainActivity) context;
		// Make sure the app is registered with GCM and with the server
		// prefs =
		// context.getSharedPreferences(MainControl.class.getSimpleName(),
		// Context.MODE_PRIVATE);

		// regid = prefs.getString(GPShare.transact.PROPERTY_REG_ID, null);
		regid = prefs.LoadPreferences(GPShare.transact.PROPERTY_REG_ID);

		System.out.println("registered!: " + regid);
		// If there is no registration ID, the app isn't registered.
		// Call registerBackground() to register it.

		gcm = GoogleCloudMessaging.getInstance(context);

		if (regid == null || regid.equals("")) {
			registerBackground();
		}

	}

	@SuppressWarnings("unchecked")
	public void registerBackground() {
		new AsyncTask() {

			@Override
			protected Object doInBackground(Object... arg0) {
				String msg = "";
				try {

					regid = gcm.register(SENDER_ID);
					Transactions.registerGcmRegIdOnline(regid, DTD.getPersonId());
					System.out.println("Device registered, registration id=" + regid);

					// You should send the registration ID to your server over
					// HTTP,
					// so it can use GCM/HTTP or CCS to send messages to your
					// app.

					// For this demo: we don't need to send it because the
					// device
					// will send upstream messages to a server that will echo
					// back
					// the message using the 'from' address in the message.

					// Save the regid for future use - no need to register
					// again.

					prefs.savePreferences(PROPERTY_REG_ID, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}
		}.execute(null, null, null);
	}

	@SuppressWarnings("unchecked")
	public void unRegisterReRegister() {
		new AsyncTask() {

			@Override
			protected void onPostExecute(Object result) {
				registerBackground();
				super.onPostExecute(result);
			}

			@Override
			protected Object doInBackground(Object... arg0) {
				String msg = "";
				try {

					gcm.unregister();
					prefs.savePreferences(PROPERTY_REG_ID, "");
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}
		}.execute(null, null, null);
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}
}
