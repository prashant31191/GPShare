package com.devtechdesign.gpshare.facebook;

import java.io.IOException;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.elements.Image;
import com.devtechdesign.gpshare.utility.SoapInterface;
import com.devtechdesign.gpshare.utility.Utility;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;

public class UploadFaceBookImage extends Activity implements SoapInterface {

	private static SharedPreferences sharedPreferences;
	private static Editor editor;
	private Handler mHandler;
	private ProgressDialog dialog;
	private String currentImgPath, graph_or_fql, userName, pmId, photo_id, smallUrl, largeUrl;

	Transactions TransactionSet;
	private String actualImageForUpload;
	private GPShare GPShare;

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		GPShare = (GPShare) getApplicationContext();

		setContentView(R.layout.upload_facebook_image);
		sharedPreferences = getSharedPreferences(SharedPref, 0);
		editor = sharedPreferences.edit();
		TransactionSet = new Transactions(getApplicationContext());
		mHandler = new Handler();

		Bundle extras = getIntent().getExtras();
		currentImgPath = extras.getString("currentImgPath");
		actualImageForUpload = extras.getString("actualImageForUpload");

		dialog = ProgressDialog.show(UploadFaceBookImage.this, "", "Uploading Image To Facebook...", true, true);

		if (actualImageForUpload != null) {
			new insertFbRecrod().execute("");
		} else {
			Toast.makeText(UploadFaceBookImage.this, "Error selecting image from the gallery.", Toast.LENGTH_SHORT).show();
		}
	}

	public class insertFbRecrod extends AsyncTask<Object, Object, Object> {

		private Uri photoUri;
		private String stringPhotoUri;

		@Override
		protected Object doInBackground(Object... arg0) {

			pmId = Transactions.insertFbRecord(LoadPreferences("userName"), currentImgPath);

			return null;
		}

		@Override
		protected void onPostExecute(Object result) {

			Bundle params = new Bundle();

			try {

				params.putByteArray("photo", Utility.scaleImage(UploadFaceBookImage.this, photoUri, actualImageForUpload));

			} catch (IOException e) {
				e.printStackTrace();
			}

			if (pmId != null) {
				params.putString("caption", "View on GPShare: " + GPSHARE_DOMAIN + "/GPShare?PMID=" + pmId + "&RID=0");
				Utility.mAsyncRunner.request("me/photos", params, "POST", new PhotoUploadListener(), null);
			} else {
				Toast.makeText(UploadFaceBookImage.this, "Image cannot be uploaded at this time.", Toast.LENGTH_SHORT).show();
				finish();
			}
			super.onPostExecute(result);
		}

	}

	/*
	 * callback for the photo upload
	 */
	public class PhotoUploadListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			System.out.println("\\\\\\\\\\\\\\before Handler!!!!!!!!!!!!");

			mHandler.post(new Runnable() {
				@Override
				public void run() {

					Toast.makeText(getApplicationContext(), "Photo Uploaded Successfully!", 2).show();
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
					fetchAndUpdateSmlLrgUrl(photo_id);

				}
			});

			// dialog.dismiss();
			// mHandler.post(new Runnable() {
			// @Override
			// public void run() {
			// new UploadPhotoResultDialog(Hackbook.this,
			// "Upload Photo executed", response)
			// .show();
			// }/
			// });
			// }
		}
	}

	public void getFBFriends(String response) {
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

		// Toast.makeText(getApplicationContext(), photo_id, 1).show();
		graph_or_fql = "fql";
		String query2 = "Select src_big,src_small from photo where aid in " + "(SELECT aid FROM album WHERE owner = me()) and object_id =" + "\"" + photo_id + "\"";
		// " +"\""+ photo_id +"\"";
		System.out.println("***************facebookphotoId: " + photo_id + "****************");

		Bundle params = new Bundle();
		params.putString("method", "fql.query");
		params.putString("query", query2);
		Utility.mAsyncRunner.request(null, params, new FQLRequestListener());
	}

	public void fetchAndUpdateSmlLrgUrl(String photo_id) {
		graph_or_fql = "fql";
		String query2 = "Select src_big,src_small from photo where aid in " + "(SELECT aid FROM album WHERE owner = me()) and object_id =" + "\"" + photo_id + "\"";
		// " +"\""+ photo_id +"\"";
		System.out.println("***************facebookphotoId: " + photo_id + "****************");

		Bundle params = new Bundle();
		params.putString("method", "fql.query");
		params.putString("query", query2);
		Utility.mAsyncRunner.request(null, params, new FQLRequestListener());
	}

	public class FQLRequestListener extends BaseRequestListener {

		public void onComplete(final String response, final Object state) {
			dialog.dismiss();
			/*
			 * Output can be a JSONArray or a JSONObject. Try JSONArray and if
			 * there's a JSONException, parse to JSONObject
			 */
			try {
				JSONArray json = new JSONArray(response);

				JSONObject data = (JSONObject) json.get(0);
				smallUrl = data.getString("src_small");
				largeUrl = data.getString("src_big");

				updateFaceBookRecord(pmId, smallUrl, largeUrl);
				GPContextHolder.getShareAct().finish();
				finish();
				// String query =
				// "select name, current_location, uid,email, pic_square from user where uid in (select uid2 from friend where uid1=me()) order by name";
				// Bundle params = new Bundle();
				// params.putString("method", "fql.query");
				// params.putString("query", query);
				//
				// params.putString("method", "fql.query");
				// params.putString("query", query);
				// Utility.mAsyncRunner.request(null, params,new
				// FriendsRequestListener());
				//
				//
				// System.out.println("XXXXXXXHITTTTTTTTTresponse: " +
				// json.toString(2) + "TTTTTTT!!!!!!!!!!!!");
			} catch (JSONException e) {

			}
		}

		public void onFacebookError(FacebookError error) {
			dialog.dismiss();
		}
	}

	private String LoadPreferences(String key) {
		String value = sharedPreferences.getString(key, "");
		return value;
	}

	/*
	 * callback after friends are fetched via me/friends or fql query.
	 */

	// public class FriendsRequestListener extends BaseRequestListener {
	//
	// @Override
	// public void onComplete(final String response, final Object state) {
	// dialog.dismiss();
	// /*
	// * Output can be a JSONArray or a JSONObject.
	// * Try JSONArray and if there's a JSONException, parse to JSONObject
	// */
	// //JSONObject json;
	//
	// try {
	//
	// JSONArray json = new JSONArray(response);
	//
	// JSONObject smallUrl2 = (JSONObject)json.get(0);
	// smallUrl = smallUrl2.getString("src_small");
	//
	// System.out.println("XXXXXXXresponse smallUrl: " + smallUrl +
	// "TTTTTTT!!!!!!!!!!!!");
	// //json = Util.parseJson(response);
	// //String src_small = json.getString("src_small");
	// // System.out.println("XXXXXXXsrc_small: " + json.getString("src_small")
	// + "TTTTTTT!!!!!!!!!!!!");
	// // System.out.println("XXXXXXXsrc_big: " + json.getString("src_big") +
	// "TTTTTTT!!!!!!!!!!!!");
	//
	//
	//
	// } catch (JSONException e) {
	//
	// System.out.println("XXXXXXXJSON Error: " + e.getMessage()+
	// "TTTTTTT!!!!!!!!!!!!");
	//
	// }
	//
	// Intent myIntent = new Intent(UploadFaceBookImage.this,
	// FriendsList.class);
	//
	//
	// myIntent.putExtra("API_RESPONSE", response);
	// myIntent.putExtra("METHOD", graph_or_fql);
	// myIntent.putExtra("userName", userName);
	// myIntent.putExtra("userName", userName);
	// myIntent.putExtra("photo_id", photo_id);
	// myIntent.putExtra("smallBigUrl", smallUrl);
	// myIntent.putExtra("pmId", pmId);
	//
	// //update server facebook url before viewing friend list
	// //updateFaceBookRecord(pmId,smallUrl);
	// UploadFaceBookImage.this.startActivity(myIntent);
	//
	// }
	//
	// public void onFacebookError(FacebookError error) {
	// //dialog.dismiss();
	// Toast.makeText(UploadFaceBookImage.this, "Facebook Error: " +
	// error.getMessage(),
	// Toast.LENGTH_SHORT).show();
	// }
	// }

	protected void updateFaceBookRecord(String pmid, String fbImageUrl, String largeUrl) {

		System.out.println("**********fbImageUrl: " + fbImageUrl);

		SoapObject request = new SoapObject(NAMESPACE, TAGGED_IMG_RECORD_UPDATE_METHOD_NAME);
		request.addProperty("pmid", pmid);
		request.addProperty("url", fbImageUrl);
		request.addProperty("largeUrl", largeUrl);
		System.out.println("**********updateFaceBookRecord properites: " + request.getProperty(0));
		System.out.println("**********updateFaceBookRecord properites: " + request.getProperty(1));

		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = true;
		envelope.setOutputSoapObject(request);
		HttpTransportSE androidHttpTransport = new HttpTransportSE(GPShare_Svc_Url);

		try {

			androidHttpTransport.call(TAGGED_IMG_RECORD_UPDATE_ACTION, envelope);
			pmid = envelope.getResponse().toString();

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("**********KSoap Error: " + e.getMessage());

		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		// TODO Auto-generated method stub

	}

}
