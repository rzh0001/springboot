package com.smily.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.common.base.Optional;

/**
 * 
 * @author Smily
 * @since 2016-07-25
 */
public class DateTimeUtils {

	protected static SimpleDateFormat dateSdf = new SimpleDateFormat("yyyyMMdd");
	protected static SimpleDateFormat monthSdf = new SimpleDateFormat("yyyyMM");
	protected static SimpleDateFormat datetimeSdf = new SimpleDateFormat("yyyyMMddhhmmss");

	public static String formatDate(Date date) {
		return dateSdf.format(date);
	}

	public static String formatMonth(Date date) {
		return monthSdf.format(date);
	}

	public static String formatDateTime(Date date) {
		return datetimeSdf.format(date);
	}

	public static Date parseDateString(String date) throws ParseException {
		return dateSdf.parse(date);
	}

	public static Date parseMonthString(String date) throws ParseException {
		return monthSdf.parse(date);
	}

	public static String getLastMonth(Date date) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, -1);

		return formatMonth(cal.getTime());
	}

	public static String getTheLastDayOfMonth(Date date) {
		Optional<Date> _date = Optional.of(date);
		Calendar cal = Calendar.getInstance();
		cal.setTime(_date.get());
		cal.add(Calendar.MONTH, 1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		cal.add(Calendar.DATE, -1);
		return formatDate(cal.getTime());
	}
}
