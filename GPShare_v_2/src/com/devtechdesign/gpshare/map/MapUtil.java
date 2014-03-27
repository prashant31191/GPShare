package com.devtechdesign.gpshare.map;

import java.io.File;
import java.util.ArrayList;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.elements.Image;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import dev.tech.images.Util;

public class MapUtil {
	private GoogleMap mMap;

	MapUtil(GoogleMap mMap) {
		this.mMap = mMap;

	}

	public void addEachImageToMapPer() {
		// mMap.clear();
		// add all images to the map per place
		mMap.clear();
		ArrayList<Image> imgs = Transactions.getImageaFromPhoneDb("");
		for (int i = 0; i < imgs.size(); i++) {
			Image img = imgs.get(i);
			System.out.println("image loo0p");
			new GetLocalPicAsyncTaskWithSize().execute(img);
		}
	}

	public class GetLocalPicAsyncTaskWithSize extends AsyncTask<Object, Void, Bitmap> {
		Image img;

		@Override
		protected Bitmap doInBackground(Object... params) {

			if (params != null) {
				this.img = (Image) params[0];

				return Util.decodeFileImg(new File(img.getimgPath()), 20);
			}

			return null;
		}

		@Override
		protected void onPostExecute(Bitmap markerImage) {
			if (markerImage != null) {
				Double x = Double.parseDouble(img.getimgX());
				Double y = Double.parseDouble(img.getimgY());

				LatLng coordinate = new LatLng(x, y);
				// ImageOverlay imageOverlay = new
				// ImageOverlay(getApplicationContext(), point, markerImage);
				// // imageOverlay.addOverlay(new OverlayItem(point,
				// // currentCoordinates, currentCoordinates));
				// overlay.add(imageOverlay);
				mMap.addMarker(new MarkerOptions().position(coordinate).title("").snippet("Your Location").icon(BitmapDescriptorFactory.fromBitmap(markerImage)));

			}
			super.onPostExecute(markerImage);
		}
	}
}
