package com.devtechdesign.gpshare.map;

import java.util.ArrayList;
import java.util.List;


import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;

import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.data.db.RouteDatabase.AllRoutes;
import com.devtechdesign.gpshare.elements.aRoute;
import com.devtechdesign.gpshare.utility.Utility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapRoute {

	private Polyline polyline;
	private GoogleMap mMap;
	private Object pushpin;
	private Marker currentLocationMarker;
	private LatLng currentLatLong;

	MapRoute(GoogleMap mMap) {
		this.mMap = mMap;
	}

	@SuppressWarnings("unchecked")
	public void getAndDisplayRoute(final String mapDataId) {
		if (mapDataId != null && !mapDataId.equals("")) {

			new AsyncTask<PolylineOptions, Object, PolylineOptions>() {

				private String[] routeData;

				@Override
				protected PolylineOptions doInBackground(PolylineOptions... params) {
					try {
						ArrayList<aRoute> currentRouteAry = GPShare.transact.getARouteDynParam(AllRoutes.ALLROUTES_SegmentKey + " = '" + mapDataId + "'");
						PolylineOptions rectOptions = null;
						if (currentRouteAry.size() > 0) {
							String xyFileString = GPShare.transact.concatenateXyStrings(currentRouteAry);
							if (!xyFileString.equals("")) {
								String tempData[] = xyFileString.replace("null", "").split(",");
								if (tempData.length > 0) {
									routeData = new String[tempData.length + 1 / 5 * 2];

									rectOptions = new PolylineOptions();
									rectOptions.color(Color.GREEN);

									Object[] ob = new Object[2];
									ob[0] = tempData[0];
									ob[1] = tempData[1];

									publishProgress(ob);

									for (int i = 0; i < tempData.length + 1 / 5 * 2 - 1; i++) {

										routeData[i] = tempData[i].toString();
										routeData[i + 1] = tempData[i + 1].toString();
										rectOptions.add(new LatLng(Double.parseDouble(routeData[i]), Double.parseDouble(routeData[i + 1])));
										i += 4;
									}
								}
							}
						}
						return rectOptions;
					} catch (NullPointerException e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(PolylineOptions rectOptions) {

					if (rectOptions != null)
						polyline = mMap.addPolyline(rectOptions);

					super.onPostExecute(rectOptions);
				}

				protected void onProgressUpdate(Object... values) {
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(values[0].toString()), Double.parseDouble(values[1].toString())), 12.0f));
					super.onProgressUpdate(values);
				}
			}.execute(null, null, null);
		}
	}

	@SuppressWarnings("unchecked")
	public void getAndDisplayRouteVyxyString(final aRoute cRoute) {

		new AsyncTask<PolylineOptions, Object, PolylineOptions>() {

			private String[] routeData;

			@Override
			protected PolylineOptions doInBackground(PolylineOptions... params) {
				try {
					PolylineOptions rectOptions = null;
					if (cRoute != null) {
						String xyFileString = cRoute.getXyString();

						if (!xyFileString.equals("")) {
							String tempData[] = xyFileString.replace("null", "").split(",");
							if (tempData.length > 0) {
								routeData = new String[tempData.length + 1 / 5 * 2];

								rectOptions = new PolylineOptions();
								rectOptions.color(Color.GREEN);

								Object[] ob = new Object[2];
								ob[0] = tempData[0];
								ob[1] = tempData[1];

								publishProgress(ob);

								for (int i = 0; i < tempData.length + 1 / 5 * 2 - 1; i++) {

									routeData[i] = tempData[i].toString();
									routeData[i + 1] = tempData[i + 1].toString();
									rectOptions.add(new LatLng(Double.parseDouble(routeData[i]), Double.parseDouble(routeData[i + 1])));
									i += 4;
								}
							}
						}
					}
					return rectOptions;
				} catch (NullPointerException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(PolylineOptions rectOptions) {

				if (rectOptions != null)
					polyline = mMap.addPolyline(rectOptions);

				super.onPostExecute(rectOptions);
			}

			protected void onProgressUpdate(Object... values) {
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(values[0].toString()), Double.parseDouble(values[1].toString())), 12.0f));
				super.onProgressUpdate(values);
			}
		}.execute(null, null, null);
	}

	public void updateRouteLine(LatLng newPoint) {
		if (newPoint != null && polyline != null) {
			currentLatLong = newPoint;
			List<LatLng> points = polyline.getPoints();
			points.add(newPoint);
			polyline.setPoints(points);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void zoomToRealTimeTrackingLocation(String currentLocationExtras, String profileImgUrl) {
		if (currentLocationExtras != null) {
			String[] currentLocation = currentLocationExtras.split(",");
			String xx = currentLocation[0];
			String yy = currentLocation[1];

			Double x = Double.valueOf(xx);
			Double y = Double.valueOf(yy);

			final LatLng latLong = new LatLng(x, y);
			float cameraZoomLvl;

			if (!(mMap.getCameraPosition().zoom == 15f)) {
				cameraZoomLvl = mMap.getCameraPosition().zoom;
			} else {
				cameraZoomLvl = 15f;
			}

			CameraPosition currentPlace = new CameraPosition.Builder().target(latLong).tilt(20f).zoom(6).build();
			mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
			addOnlineBmPlaceMark(profileImgUrl, latLong);
		}
	}

	private Marker currentLocMark;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addOnlineBmPlaceMark(final String url, final LatLng latLong) {
		new AsyncTask<Bitmap, Bitmap, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Bitmap... arg0) {
				try {
					return Utility.getOnlineBitmap(Transactions.correctUrl(url));
				} catch (NullPointerException e) {
					return null;
				}
			}

			@Override
			protected void onPostExecute(Bitmap profileImage) {

				if (currentLocMark != null)
					currentLocMark.remove();

				if (profileImage != null) {
					currentLocMark = mMap.addMarker(new MarkerOptions().position(latLong).title("Matt Roberts").snippet("Matt's Current Location")
							.icon(BitmapDescriptorFactory.fromBitmap(profileImage)));
				}
				super.onPostExecute(profileImage);
			}
		}.execute();
		// GTrack("zoomToCurrentLocation");
	}

	public LatLng getCurrentLatLong() {
		return currentLatLong;
	}

	public void setCurrentLatLong(LatLng currentLatLong) {
		this.currentLatLong = currentLatLong;
	}

	public void dispLayoutOnlineRoute(final String mapOnlineDataId) {
		if (mapOnlineDataId != null && !mapOnlineDataId.equals("")) {

			new AsyncTask<PolylineOptions, Object, PolylineOptions>() {

				private String[] routeData;

				@Override
				protected PolylineOptions doInBackground(PolylineOptions... params) {
					try {
						ArrayList<aRoute> selectedRouteAry = Transactions.getARouteById(mapOnlineDataId, "");
						PolylineOptions rectOptions = null;
						if (selectedRouteAry.size() > 0) {
							String xyFileString = GPShare.transact.concatenateXyStrings(selectedRouteAry);

							String tempData[] = xyFileString.replace("null", "").split(",");
							if (tempData.length > 0) {
								routeData = new String[tempData.length + 1 / 5 * 2];

								rectOptions = new PolylineOptions();
								rectOptions.color(Color.GREEN);

								Object[] ob = new Object[2];
								ob[0] = tempData[0];
								ob[1] = tempData[1];

								publishProgress(ob);

								for (int i = 0; i < tempData.length + 1 / 5 * 2 - 1; i++) {

									routeData[i] = tempData[i].toString();
									routeData[i + 1] = tempData[i + 1].toString();
									rectOptions.add(new LatLng(Double.parseDouble(routeData[i]), Double.parseDouble(routeData[i + 1])));
									i += 4;
								}
							}
						}
						return rectOptions;

					} catch (NullPointerException e) {
						return null;
					}
				}

				@Override
				protected void onPostExecute(PolylineOptions rectOptions) {

					if (rectOptions != null)
						polyline = mMap.addPolyline(rectOptions);

					super.onPostExecute(rectOptions);
				}

				protected void onProgressUpdate(Object... values) {
					mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(Double.parseDouble(values[0].toString()), Double.parseDouble(values[1].toString())), 12.0f));
					super.onProgressUpdate(values);
				}
			}.execute(null, null, null);
		}
	}
}
