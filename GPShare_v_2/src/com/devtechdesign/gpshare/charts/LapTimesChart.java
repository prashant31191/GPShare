package com.devtechdesign.gpshare.charts;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.chart.PointStyle;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.widget.Toast;

import com.devtechdesign.gpshare.elements.aRoute;

/**
 * Average temperature demo chart.
 */
public class LapTimesChart extends AbstractDemoChart {

	private ArrayList<double[]> mphHit;
	private long totalMinutes;

	/**
	 * Returns the chart name.
	 * 
	 * @return the chart name
	 */
	public String getName() {
		return "Average temperature";
	}

	/**
	 * Returns the chart description.
	 * 
	 * @return the chart description
	 */
	public String getDesc() {
		return "The average temperature in 4 Greek islands (line chart)";
	}

	/**
	 * Executes the chart demo.
	 * 
	 * @param context
	 *            the context
	 * @return the built intent
	 */
	public Intent execute(Context context, aRoute oneRoute) {

		Intent intent;
		try {
			calculateGraphValues(oneRoute);

			String[] titles = new String[] { "Route" };
			List<double[]> x = new ArrayList<double[]>();

			double[] miniutesAry = new double[(int) totalMinutes];
			for (int i = 0; i < totalMinutes; i++) {
				miniutesAry[i] = i;
			}
			x.add(miniutesAry);

			int[] colors = new int[] { Color.GREEN };
			PointStyle[] styles = new PointStyle[] { PointStyle.POINT };
			XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
			int length = renderer.getSeriesRendererCount();
			for (int i = 0; i < length; i++) {
				((XYSeriesRenderer) renderer.getSeriesRendererAt(i)).setFillPoints(true);
			}
			setChartSettings(renderer, "Average Speed", "Time", "MPH", 0, totalMinutes, 0, 40, Color.LTGRAY, Color.LTGRAY);
			renderer.setXLabels(12);
			renderer.setYLabels(10);
			renderer.setShowGrid(true);
			renderer.setXLabelsAlign(Align.RIGHT);
			renderer.setYLabelsAlign(Align.RIGHT);
			renderer.setZoomButtonsVisible(true);
			renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
			renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
			intent = ChartFactory.getLineChartIntent(context, buildDataset(titles, x, mphHit), renderer, "Laps");

		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
			Toast.makeText(context, "Unable to Load Chart at this time!", 2).show();
			return null;
		}

		return intent;
	}

	public void calculateGraphValues(aRoute route) {

		double totalDistance;
		double oneMileIncrement;
		double previousX;
		double previousY;

		java.util.Date endTime = null;
		java.util.Date startTime = null;
		String tempData[] = route.getXyString().replace("null", "").split(",");

		java.text.DateFormat df = new java.text.SimpleDateFormat("hh:mm:ss");
		String parsedStart = tempData[2].substring(tempData[2].length() - 8);
		String parsedEnd = tempData[tempData.length - 3].substring(tempData[tempData.length - 3].length() - 8);

		try {
			startTime = df.parse(parsedStart);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			endTime = df.parse(parsedEnd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		totalMinutes = (endTime.getTime() - startTime.getTime()) / 60000;

		System.out.println("startTime: " + parsedStart);
		System.out.println("endTime: " + parsedEnd);
		System.out.println("diff: " + totalMinutes);
		String routeData[] = new String[tempData.length + 1 / 5 * 2];
		mphHit = new ArrayList<double[]>();

		double[] mphAry = new double[tempData.length / 5];

		int ii = 0;

		for (int i = 0; i < tempData.length + 1 / 5 * 2 - 1; i++) {

			mphAry[ii] = Double.valueOf(tempData[i + 4]);
			ii++;
			// double x = Double.valueOf(tempData[i].toString());
			// double y = Double.valueOf(tempData[i + 1].toString());
			//
			// Transactions.findCoordinateDistance(x,y,)

			// previousX = x;
			// previousY = y;
			System.out.println("looped time: " + tempData[i + 4]);
			i += 4;
		}
		mphHit.add(mphAry);
	}
}
