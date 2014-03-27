package com.devtechdesign.gpshare.map;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout.LayoutParams;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.elements.Image;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import dev.tech.images.Util;

public class MapImageLayer {

	private Polyline polyline;
	private GoogleMap mMap;
	private Object pushpin;
	private Marker currentLocationMarker;
	private LatLng currentLatLong;
	private ArrayList<Image> imgs;
	private ArrayList<Marker> imgMarkers;
	static Context context;

	MapImageLayer(Context context, GoogleMap mMap, ArrayList<Image> imgs) {
		this.mMap = mMap;
		this.imgs = imgs;
		MapImageLayer.context = context;
		imgMarkers = new ArrayList<Marker>();

		new GetLocalPicAsyncTaskWithSize().execute();
	}

	public class GetLocalPicAsyncTaskWithSize extends AsyncTask<Void, Object, Void> {

		@Override
		protected Void doInBackground(Void... params) {

			for (Image i : imgs) {
				Bitmap bm = Util.decodeFileImg(new File(i.getimgPath()), 50);
				Object[] ob = new Object[2];
				ob[0] = i;
				ob[1] = bm;
				this.publishProgress(ob);
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Object... ob) {
			Image img = (Image) ob[0];
			Bitmap bm = (Bitmap) ob[1];
			addImageToMap(img, bm);
			super.onProgressUpdate(ob);
		}
	}

	public void zoomToIndex(int i) {
		if (imgs.size() > 0) {
			Image img = imgs.get(i);
			CameraPosition currentPlace = new CameraPosition.Builder().target(new LatLng(Double.parseDouble(img.getimgX()), Double.parseDouble(img.getimgY()))).tilt(20f).zoom(13)
					.build();
			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));

		}
	}

	public void addImageToMap(Image img, Bitmap bm) {
		if (img != null) {
			if (bm != null) {
				Marker mrk = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.valueOf(img.getimgX()), Double.valueOf(img.getimgY()))).title(img.getimgPlace())
						.snippet(img.getimgCreateDate()).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(getMarkerView(bm)))));
				imgMarkers.add(mrk);
			}
		}
	}

	public void removeAllPlacemarks() {
		for (Marker m : imgMarkers) {
			m.remove();
		}
		imgMarkers.clear();
	}

	private View getMarkerView(Bitmap bm) {
		View view = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.map_image, null);
		ImageView imgMapImage = (ImageView) view.findViewById(R.id.imgMapImage);
		imgMapImage.setImageBitmap(bm);

		return view;
	}

	// Convert a view to bitmap
	public static Bitmap createDrawableFromView(View view) {

		DisplayMetrics displayMetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		view.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		view.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
		view.buildDrawingCache();
		Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);
		view.draw(canvas);

		return bitmap;
	}
}
