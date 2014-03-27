package com.devtechdesign.gpshare.utility;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.facebook.BaseRequestListener;

public class FBUtility implements SoapInterface {

	public static void postFbRouteToTimeLine(Context context, aRoute currentRoute) {
		Bundle params = new Bundle();

		params.putString("link", GPSHARE_DOMAIN + "/GPShare?PMID=0&RID=" + currentRoute.getRouteId());
		params.putString("route", GPSHARE_DOMAIN + GPSHARE_DIR_ROUTES + currentRoute.getRouteId() + ".html");

		Utility.mAsyncRunner.request("me/devtechdesign:track", params, "POST", new WallPostListener(), null);
		Toast.makeText(context, "Route has been shared on Facebook", 2).show();
	}

	public static class WallPostListener extends BaseRequestListener {

		@Override
		public void onComplete(final String response, final Object state) {
			System.out.println("WALL POST: response: " + response);

		}
	}

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		System.out.println("WALL POST: response: " + ex.getMessage());

	}
}
