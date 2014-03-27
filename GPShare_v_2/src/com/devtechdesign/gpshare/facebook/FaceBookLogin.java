package com.devtechdesign.gpshare.facebook;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.facebook.SessionEvents.AuthListener;
import com.devtechdesign.gpshare.facebook.SessionEvents.LogoutListener;
import com.devtechdesign.gpshare.utility.Utility;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.Facebook;

import dev.tech.fb.LoginButton;

public class FaceBookLogin extends Activity {

	// /DEV: 465900176759803
	// /Prod: 273395502707692
	Facebook facebook = new Facebook("273395502707692");

	public static final String APP_ID = "273395502707692";
	public LoginButton mLoginButton;
	final static int AUTHORIZE_ACTIVITY_RESULT_CODE = 0;
	final static int PICK_EXISTING_PHOTO_RESULT_CODE = 1;
	String[] permissions = { "offline_access", "email", "publish_stream", "user_photos", "publish_checkins", "photo_upload" };
	private String currentImgPath;
	private TextView mText;
	private ImageView mUserPic;
	private Handler mHandler;
	ProgressDialog dialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facebook_login);

		mHandler = new Handler();

		mText = (TextView) findViewById(R.id.txt);
		mUserPic = (ImageView) findViewById(R.id.user_pic);

		// Create the Facebook Object using the app id.

		// Instantiate the asynrunner object for asynchronous api calls.
		Utility.mAsyncRunner = new AsyncFacebookRunner(Utility.mFacebook);

		mLoginButton = (LoginButton) findViewById(R.id.login);

		// restore session if one exists
		SessionStore.restore(Utility.mFacebook, this);
		SessionEvents.addAuthListener(new FbAPIsAuthListener());
		SessionEvents.addLogoutListener(new FbAPIsLogoutListener());

		/*
		 * Source Tag: login_tag
		 */
		System.out.println("authorizeCallback");
		mLoginButton.init(this, AUTHORIZE_ACTIVITY_RESULT_CODE, Utility.mFacebook, permissions);
		System.out.println("authorizeCallback");
		if (Utility.mFacebook.isSessionValid()) {
			requestUserData();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (Utility.mFacebook != null && !Utility.mFacebook.isSessionValid()) {
			mText.setText("You are logged out! ");
			mUserPic.setImageBitmap(null);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		/*
		 * if this is the activity result from authorization flow, do a call
		 * back to authorizeCallback Source Tag: login_tag
		 */
		case AUTHORIZE_ACTIVITY_RESULT_CODE: {

			Utility.mFacebook.authorizeCallback(requestCode, resultCode, data);

			break;
		}
		}
	}

	/*
	 * The Callback for notifying the application when authorization succeeds or
	 * fails.
	 */

	public class FbAPIsAuthListener implements AuthListener {

		@Override
		public void onAuthSucceed() {
			mText.setText("Login Succeeded!");
			requestUserData();
			System.out.println("login succeeded!");
		}

		@Override
		public void onAuthFail(String error) {
			mText.setText("Login Failed: " + error);
		}
	}

	/*
	 * The Callback for notifying the application when log out starts and
	 * finishes.
	 */
	public class FbAPIsLogoutListener implements LogoutListener {
		@Override
		public void onLogoutBegin() {
			mText.setText("Logging out...");
		}

		@Override
		public void onLogoutFinish() {
			mText.setText("You have logged out! ");
			mUserPic.setImageBitmap(null);
		}
	}

	/*
	 * Request user name, and picture to show on the main screen.
	 */
	public void requestUserData() {
		mText.setText("Fetching user name, profile pic...");
		Bundle params = new Bundle();
		params.putString("fields", "name, picture,email");
		Utility.mAsyncRunner.request("me", params, new UserRequestListener());
	}

	/*
	 * Callback for fetching current user's name, picture, uid.
	 */
	public class UserRequestListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(response);

				final String picURL = jsonObject.getString("picture");
				final String name = jsonObject.getString("name");
				Utility.userUID = jsonObject.getString("id");

				mHandler.post(new Runnable() {
					@Override
					public void run() {
						mText.setText("Welcome " + name + "!");
						mUserPic.setImageBitmap(Utility.getOnlineBitmap(picURL));
						finish();
					}
				});

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}
}
