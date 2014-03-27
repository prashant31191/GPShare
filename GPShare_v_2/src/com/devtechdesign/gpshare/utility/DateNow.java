package com.devtechdesign.gpshare.utility;

import java.util.Calendar;

public class DateNow {
	
	public static String getCurrentDateTime() {
		final Calendar c = Calendar.getInstance();

		String day_ = "" + c.get(Calendar.DATE);

		String month_ = "" + (c.get(Calendar.MONTH) + 1);

		String year_ = "" + c.get(Calendar.YEAR);

		String hour_ = "" + c.get(Calendar.HOUR);
//
		String minute_ = "" + c.get(Calendar.MINUTE);

		String second_ = "" + c.get(Calendar.SECOND);

		String miliSecond_ = "" + c.get(Calendar.MILLISECOND);

		String am_pm = "PM";
		if (c.get(Calendar.AM_PM) == 0)
			am_pm = "AM";

		StringBuffer g = new StringBuffer();
		g.append(month_).append("_").append(day_).append("_").append(year_)
				.append("SS").append(hour_).append("_").append(minute_)
				.append("_").append(second_).append("_").append(miliSecond_)
				.append(am_pm);
		return g.toString();
	}
	
	public static String getCurrentDateTimeFormatted() {
		final Calendar c = Calendar.getInstance();

		String day_ = "" + c.get(Calendar.DATE);

		String month_ = "" + (c.get(Calendar.MONTH) + 1);

		String year_ = "" + c.get(Calendar.YEAR);

		String hour_ = "" + c.get(Calendar.HOUR);

		String minute_ = "" + c.get(Calendar.MINUTE);

		String second_ = "" + c.get(Calendar.SECOND);

		String miliSecond_ = "" + c.get(Calendar.MILLISECOND);

		String am_pm = "PM";
		if (c.get(Calendar.AM_PM) == 0)
			am_pm = "AM";

		StringBuffer g = new StringBuffer();
		g.append(month_).append("/").append(day_).append("/").append(year_)
				.append(" ").append(hour_).append(":").append(minute_)
				.append(":").append(second_).append(":").append(miliSecond_).append(" ")
				.append(am_pm);
		return g.toString();
	}
}
