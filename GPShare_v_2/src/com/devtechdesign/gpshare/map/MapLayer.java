package com.devtechdesign.gpshare.map;

import java.util.ArrayList;
import java.util.List;
import android.graphics.Color;

import com.devtechdesign.gpshare.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapLayer {

	private GoogleMap mMap;
	private MapFragAct mFrag;
	PolylineOptions rectOptionsLocToFront;
	Polyline layerPolyLineLocToFront;

	MapLayer(MapFragAct mFrag) {
		this.mFrag = mFrag;
		mMap = mFrag.getmMap();
	}

	PolylineOptions rectOptions;
	private Polyline layerPolyLine;

	public void buildRoute(LatLng newPoint) {
		//
		rectOptions = new PolylineOptions();
		rectOptions.color(Color.RED);
		rectOptions.width(5);
		rectOptions.add(newPoint);
		layerPolyLine = mMap.addPolyline(rectOptions);
	}

	public void addDistancePolyLine(LatLng newPoint) {

		if (mFrag.mRoute.getCurrentLatLong() != null) {
			if (layerPolyLineLocToFront != null) {
				layerPolyLineLocToFront.remove();
			}
			rectOptionsLocToFront = new PolylineOptions();
			rectOptionsLocToFront.color(Color.RED);
			rectOptionsLocToFront.width(1);
			rectOptionsLocToFront.add(newPoint);
			rectOptionsLocToFront.add(mFrag.mRoute.getCurrentLatLong());
			layerPolyLineLocToFront = mMap.addPolyline(rectOptionsLocToFront);
		}
	}

	public void addNewPoint(LatLng newPoint) {

		if (rectOptions == null) {
			buildRoute(newPoint);
		}
		List<LatLng> points = layerPolyLine.getPoints();
		points.add(newPoint);
		layerPolyLine.setPoints(points);
		addNewPointToLayerRoute(newPoint);
		addDistancePolyLine(newPoint);
	}

	ArrayList<Marker> markers = new ArrayList<Marker>();

	public void addNewPointToLayerRoute(LatLng latLong) {

		Marker newMarker = mMap.addMarker(new MarkerOptions().position(latLong).icon(BitmapDescriptorFactory.fromResource(R.drawable.small_endpoint_pushpin)));
		markers.add(newMarker);
	}

	public void rebuildPolyLine(List<LatLng> aryCoords) {

		layerPolyLine.remove();
		removeLastmarker();
		rectOptions = new PolylineOptions();
		rectOptions.color(Color.RED);
		rectOptions.width(5);

		for (LatLng coord : aryCoords) {
			rectOptions.add(coord);
		}

		layerPolyLine = mMap.addPolyline(rectOptions);
	}

	public void removePolygon() {
		layerPolyLine.remove();
	}

	private void removeLastmarker() {
		Marker lastMarker = markers.get(markers.size() - 1);
		markers.remove(lastMarker);
		lastMarker.remove();
	}

	public void removeAllMarkers() {
		for (Marker m : markers) {
			m.remove();
		}
	}

	public void removePolyLineLocToFront() {
		layerPolyLineLocToFront.remove();
	}
}
