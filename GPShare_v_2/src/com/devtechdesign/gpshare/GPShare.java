package com.devtechdesign.gpshare;

import com.devtechdesign.gpshare.data.db.RouteDataBaseHelper;
import com.devtechdesign.gpshare.data.db.Transactions;
import com.devtechdesign.gpshare.map.MapFragAct;

import dev.tech.gpsharelibs.EGInterface;
import dev.tech.util.GLibTrans;
import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

public class GPShare extends Application implements EGInterface {

	private static RouteDataBaseHelper dbHelper;
	public static Transactions transact;
	public static int UNIT_OF_MEASURE;
	public static String UNIT_OF_MEASURE_TXT_DIST;
	public static String UNIT_OF_MEASURE_TXT_SPEED;
	public static Intent mapIntent;
	private static Activity activity;
	private static String UOM;
	public static MapFragAct mapFragAct;

	@Override
	public void onCreate() {
		super.onCreate();
		dbHelper = new RouteDataBaseHelper(this);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		// dbHelper.close();
	}

	public static SQLiteDatabase getDB() {
		return dbHelper.getWritableDatabase();
	}

	public static int loadUnitOfMeasure(String uom) {
		if (!uom.equals("")) {
			if (uom.equals("0")) {
				UNIT_OF_MEASURE_TXT_DIST = "mi";
				UNIT_OF_MEASURE_TXT_SPEED = "mph";

				setUOM(String.valueOf(STANDARD_UOM));
				return STANDARD_UOM;
			} else {
				UNIT_OF_MEASURE_TXT_DIST = "km";
				UNIT_OF_MEASURE_TXT_SPEED = "km/h";
				setUOM(String.valueOf(METRIC_UOM));
				return METRIC_UOM;
			}
		} else {
			UNIT_OF_MEASURE_TXT_DIST = "km";
			UNIT_OF_MEASURE_TXT_SPEED = "km/h";
			setUOM(String.valueOf(METRIC_UOM));
			return STANDARD_UOM;
		}
	}

	public void GTrack(String event) {
		GPContextHolder.getMainAct().GoogleA.recordClick(event);
	}

	public static String getUOM() {
		return UOM;
	}

	public static void setUOM(String uOM) {
		UOM = uOM;
	}
}
