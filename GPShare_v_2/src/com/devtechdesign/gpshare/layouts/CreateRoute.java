package com.devtechdesign.gpshare.layouts;

import java.util.ArrayList;
import java.util.List;
import com.devtechdesign.gpshare.GPContextHolder;
import com.devtechdesign.gpshare.GPShare;
import com.devtechdesign.gpshare.R;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.map.MapFragAct;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import dev.tech.util.GLibTrans;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CreateRoute extends LinearLayout {

	ArrayList<LatLng> aryCoords = new ArrayList<LatLng>();
	private TextView coordsTextView;
	private LinearLayout lLayout;
	private LinearLayout lytCoords;
	private TextView txtCoords;
	MapFragAct mAct;
	private Button btnSave;
	private Button btnUndo;
	private double totalDistance;
	private double lastDistance;
	private LatLng lastLoc;
	private Button btnCancel;

	private GoogleMap mMap;
	PolylineOptions rectOptionsLocToFront;
	Polyline layerPolyLineLocToFront;
	PolylineOptions rectOptions;
	private Polyline layerPolyLine;

	public CreateRoute(Context context) {
		super(context);
		mAct = GPShare.mapFragAct;
		mMap = mAct.getmMap();
		LayoutInflater li = LayoutInflater.from(context);

		lLayout = (LinearLayout) li.inflate(R.layout.lyt_create_route, this);
		txtCoords = (TextView) findViewById(R.id.txtCoords);
		btnSave = (Button) findViewById(R.id.btnSave);
		btnUndo = (Button) findViewById(R.id.btnUndo);
		btnCancel = (Button) findViewById(R.id.btnCancel);

		btnSave.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				GPShare.transact.createNewRoute(buildXyString(), GPContextHolder.getMainAct().getGprefs().LoadPreferences("currentPlace"), "false", "", null,
						GLibTrans.getDateTime(), "false", GLibTrans.getDateTime(), String.valueOf(totalDistance));

				Toast.makeText(getContext(), "New Route Has Been Saved", 2).show();
			}
		});

		btnCancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				removeCreateRouteOb();
			}
		});

		btnUndo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (aryCoords.size() > 0) {
					aryCoords.remove(aryCoords.size() - 1);
					rebuildPolyLine(aryCoords);
					if (aryCoords.size() > 1) {
						totalDistance -= lastDistance;
					} else {
						totalDistance = 0;
					}
					setTextCoordsTextView();
				}
			}
		});

		mAct.getmMap().setOnMapClickListener(createRouteMapOnclick);
	}

	private void removeCreateRouteOb() {
		removePolygon();
		removeAllMarkers();
		mAct.getmMap().setOnMapClickListener(null);
		removePolyLineLocToFront();
	}

	OnMapClickListener createRouteMapOnclick = new OnMapClickListener() {

		@Override
		public void onMapClick(LatLng point) {
			addNewPoint(point);
			aryCoords.add(point);

			if (aryCoords.size() > 1) {
				double cDist = findCoordinateDistance(lastLoc.latitude, lastLoc.longitude, point.latitude, point.longitude);
				lastDistance = cDist;
				totalDistance += cDist;
			}

			lastLoc = point;
			setTextCoordsTextView();
		}
	};

	// /Xystring Definition
	// /x,y,datetime,altitude,speed
	private String buildXyString() {
		String xyString = null;
		for (LatLng ltLng : aryCoords) {
			xyString += String.valueOf(ltLng.latitude) + "," + String.valueOf(ltLng.longitude) + "," + GLibTrans.getDateTime() + ",,,";
		}
		return xyString;
	}

	public void setTextCoordsTextView() {
		txtCoords.setText("Distance: " + Transactions.convertMetersPrefUom(totalDistance, GPShare.getUOM()) + GPShare.UNIT_OF_MEASURE_TXT_DIST + " \n");
	}

	protected Double findCoordinateDistance(Double startLat, Double startLong, Double endLat, Double endLong) {
		Double distance = null;

		String startingLoc = "";
		Location start = new Location(startingLoc);
		String endingLoc = null;
		Location end = new Location(endingLoc);

		start.setLatitude(startLat);
		start.setLongitude(startLong);

		end.setLatitude(endLat);
		end.setLongitude(endLong);
		distance = Double.valueOf(start.distanceTo(end));

		return distance;
	}

	public void initRoute(LatLng newPoint) {
		//
		rectOptions = new PolylineOptions();
		rectOptions.color(Color.RED);
		rectOptions.width(5);
		rectOptions.add(newPoint);
		layerPolyLine = mMap.addPolyline(rectOptions);
	}

	public void addDistancePolyLine(LatLng newPoint) {

		if (mAct.mRoute.getCurrentLatLong() != null) {
			if (layerPolyLineLocToFront != null) {
				layerPolyLineLocToFront.remove();
			}
			rectOptionsLocToFront = new PolylineOptions();
			rectOptionsLocToFront.color(Color.RED);
			rectOptionsLocToFront.width(1);
			rectOptionsLocToFront.add(newPoint);

			rectOptionsLocToFront.add(mAct.mRoute.getCurrentLatLong());

			layerPolyLineLocToFront = mMap.addPolyline(rectOptionsLocToFront);
		}
	}

	public void addNewPoint(LatLng newPoint) {

		if (rectOptions == null) {
			initRoute(newPoint);
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
		if (layerPolyLine != null)
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
		if (layerPolyLineLocToFront != null)
			layerPolyLineLocToFront.remove();
	}
}
