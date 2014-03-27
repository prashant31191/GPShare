package com.devtechdesign.gpshare;

import com.devtechdesign.gpshare.Images.ImageGallery;
import com.devtechdesign.gpshare.map.MapFragAct;
import com.devtechdesign.gpshare.utility.GPsharePrefs;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;

import dev.tech.auth.DTDLogin;
import dev.tech.gpsharelibs.DTD;
import dev.tech.gpsharelibs.IDevTech;

public class SplashScreen extends Activity implements IDevTech {
	protected boolean alive = true;
	protected int splashTime = 1500;
	public String mapDataId, queryString;
	public String placeMarkType = "currentRoute";
	GPsharePrefs gPrefs;
	final int GalleryAct = 2;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (!determineAct()) {
			
			initializeGPShareApp();
		}
	}

	private void initializeGPShareApp() {
      
		gPrefs = new GPsharePrefs(getApplicationContext(), DTD.AuthPreferences);

		try {

			Uri uri = getIntent().getData();

			String RID = uri.getQueryParameter("RID");
			String PMID = uri.getQueryParameter("PMID");

			if (RID.equals("0")) {
				placeMarkType = "placeMark";
				mapDataId = PMID;
			} else {
				placeMarkType = "route";
				mapDataId = RID;
			}

			if (RID.equals("0") && PMID.equals("0")) {
				placeMarkType = "notification";
			}

		} catch (Exception e) {
			System.out.println("exception: " + e);
		}

		setContentView(R.layout.splash);

		// thread for displaying the SplashScreen
		Thread splashTread = new Thread() {
			@Override
			public void run() {
				try {
					int waited = 0;

					while (alive && (waited < splashTime)) {
						sleep(200);

						if (alive) {
							waited += 200;
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				} finally {

					String personId = gPrefs.LoadPreferences("personId");
					DTD.setPersonId(personId);
					
					//set for testing
					DTD.setPersonId("192");
					DTD.setFirstLastName(gPrefs.LoadPreferences("firstLastName"));

					if (DTD.getPersonId().equals("")) {
						startActivityForResult(new Intent(SplashScreen.this, DTDLogin.class), 1);
						finish();
						return;
					}

					if (placeMarkType.equals("currentRoute")) {
						Intent mainIntent = new Intent(SplashScreen.this, MainActivity.class);
						startActivityForResult(mainIntent, 1);
						finish();
					}

					if (placeMarkType.equals("route") || placeMarkType.equals("placeMark")) {
						// finish();
						Intent mapIntent = new Intent(SplashScreen.this, MapFragAct.class);
						mapIntent.putExtra("placeMarkType", placeMarkType);
						mapIntent.putExtra("mapOnlineDataId", mapDataId);
						startActivity(mapIntent);
					}

					// if (placeMarkType.equals("notification")) {
					// if (!personId.equals("")) {
					// Intent notificationIntent = new Intent(SplashScreen.this,
					// NotificationList.class);
					// startActivity(notificationIntent);
					// } else {
					//
					// Intent loginIntent = new Intent(SplashScreen.this,
					// LoginGPShare.class);
					// loginIntent.putExtra("placeMarkType", "currentPlace");
					// loginIntent.putExtra("mapDataId", "");
					// startActivity(loginIntent);
					// finish();
					// }
					// }

					try {
						stop();
					} catch (UnsupportedOperationException e) {
						try {
							sleep(1);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}

					finish();
				}
			}
		};

		splashTread.start();
	}

	private boolean determineAct() {
		Bundle bund = getIntent().getExtras();
		if (bund != null) {
			switch (bund.getInt("act")) {
			case GalleryAct:
				startActivity(new Intent(this, ImageGallery.class));
				finish();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			alive = false;
		}
		return true;
	}
}
