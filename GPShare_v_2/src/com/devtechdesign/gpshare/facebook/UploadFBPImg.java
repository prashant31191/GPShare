package com.devtechdesign.gpshare.facebook;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.utility.GPsharePrefs;
import com.devtechdesign.gpshare.utility.NotificationHelper;
import com.devtechdesign.gpshare.utility.SoapInterface;
import com.devtechdesign.gpshare.utility.Utility;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

public class UploadFBPImg extends AsyncTask<Integer, Integer, String> implements SoapInterface {

	private final String sent = "X";
	private int uloadStatus = 1;
	private NotificationHelper mNotificationHelper;
	private int currentIndex;
	private ArrayList<String> files;
	private Handler handler_;
	Context context;
	private char fileTypePrefix;
	private String fileType, currentUser;
	private ContentResolver contentResolver;
	private GPsharePrefs Prefs;
	private GPShare GPShare;
	private String smallUrl;
	private String largeUrl;
	private String pmId;
	private String photo_id;
	private Handler mHandler;
	String actualImageForUpload;

	public UploadFBPImg(Context myContext, ArrayList<String> myFiles, int index, Handler handler) {

		GPShare = (GPShare) myContext;
		handler_ = handler;
		context = myContext;
		files = myFiles;
		currentIndex = index;
		contentResolver = myContext.getContentResolver();
		Prefs = new GPsharePrefs(myContext, SharedPref);
		if (mHandler == null) {
			mHandler = new Handler();
		}
		mNotificationHelper = new NotificationHelper(myContext, currentIndex, files.get(currentIndex), fileType);
	}

	protected void onPreExecute() {
		mNotificationHelper.createNotification();
	}

	@Override
	protected String doInBackground(Integer... integers) {
		int progress = 0;
		actualImageForUpload = files.get(currentIndex);
		pmId = Transactions.insertFbRecord("", actualImageForUpload);

		return pmId;
	}

	protected void onProgressUpdate(Integer... progress) {
		mNotificationHelper.progressUpdate(progress[0]);
	}

	public void makeToast(final String message) {
		handler_.post(new Runnable() {

			public void run() {
				Toast.makeText(context, message, Toast.LENGTH_LONG).show();
			}
		});
	}

	protected void onPostExecute(String result) {
		Bundle params = new Bundle();
		if (new File(actualImageForUpload).exists()) {
			try {
				Uri photoUri = null;

				params.putByteArray("photo", Utility.scaleImage(context, photoUri, actualImageForUpload));

			} catch (IOException e) {
				e.printStackTrace();
			}

			if (pmId != null) {
				// params.putString("caption", "View on GPShare: " +
				// GPSHARE_DOMAIN
				// + "/GPShare?PMID=" + pmId + "&RID=0");
				Utility.mAsyncRunner.request("me/photos", params, "POST", new PhotoUploadListener(), null);
			} else {
			}

			if (result != null) {
				mNotificationHelper.completed(0);
				currentIndex++;
			} else {
				currentIndex++;
				mNotificationHelper.completed(1);
			}

			if (currentIndex < files.size()) {
				new UploadFBPImg(context, files, currentIndex, handler_).execute(0);
			}
		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

	/*
	 * callback for the photo upload
	 */
	public class PhotoUploadListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			mHandler.post(new Runnable() {

				@Override
				public void run() {

					JSONObject json;
					try {

						json = Util.parseJson(response);
						photo_id = json.getString("id");

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (FacebookError e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// getFBFriends(response);
					fetchAndUpdateSmlLrgUrl();
				}
			});
		}
	}

	public void fetchAndUpdateSmlLrgUrl() {
		String query2 = "Select src_big,src_small from photo where aid in " + "(SELECT aid FROM album WHERE owner = me()) and object_id =" + "\"" + photo_id + "\"";

		Bundle params = new Bundle();
		params.putString("method", "fql.query");
		params.putString("query", query2);
		Utility.mAsyncRunner.request(null, params, new FQLRequestListener());
	}

	public class FQLRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {

			try {
				JSONArray json = new JSONArray(response);
				if (json.length() == 0) {
					fetchAndUpdateSmlLrgUrl();
				}
				JSONObject data = (JSONObject) json.get(0);
				smallUrl = data.getString("src_small");
				largeUrl = data.getString("src_big");

				updateFaceBookRecord(pmId, smallUrl, largeUrl);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		public void onFacebookError(FacebookError error) {

		}
	}

	protected void updateFaceBookRecord(String pmid, String fbImageUrl, String largeUrl) {

		SoapObject request = new SoapObject(NAMESPACE, TAGGED_IMG_RECORD_UPDATE_METHOD_NAME);
		request.addProperty("pmid", pmid);
		request.addProperty("url", fbImageUrl);
		request.addProperty("largeUrl", largeUrl);

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url);

		try {

			androidHttpTransport.call(TAGGED_IMG_RECORD_UPDATE_ACTION, envelope);
			pmid = envelope.getResponse().toString();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
