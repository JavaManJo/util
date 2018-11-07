package com.zpq.web.util;

import com.zpq.web.common.LogicCode;
import com.zpq.web.exception.LogicException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

	public static final String yyyy = "yyyy";
	public static final String yyyy_MM = "yyyy-MM";
	public static final String yyyy_MM_dd = "yyyy-MM-dd";
	public static final String yyyy_MM_dd_HHmmss = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 解析
	 * @param dateStr
	 * @param pattern
	 * @return
	 */
	public static Date parse(String dateStr, String pattern) {
		try {
			Date result = DateUtils.parseDate(dateStr, pattern);
			return result;
		} catch (ParseException e) {
			throw new LogicException(LogicCode.unknown, "时间["+dateStr+"]格式["+pattern+"]错误");
		}
	}

	/**
	 * 格式化
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String format(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	/**
	 * 格式化
	 * 
	 * @param date
	 * @return
	 */
	public static String format_yyyyMMdd(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM_dd);
		return sdf.format(date);
	}

	/**
	 * 格式化
	 * 
	 * @param date
	 * @return
	 */
	public static String format_yyyyMM(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM);
		return sdf.format(date);
	}
	
	/**
	 * 格式化
	 * 
	 * @param date
	 * @return
	 */
	public static String format_yyyy(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat(yyyy);
		return sdf.format(date);
	}

	/**
	 * 计算时间天数差
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static int calcDays(Date start, Date end) {
		int days = (int) ((end.getTime() - start.getTime()) / DateUtils.MILLIS_PER_DAY);
		return days;
	}

	/**
	 * 计算相差年数
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static int calcYear(Date start, Date end) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(start);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(end);
		return c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
	}

	/**
	 * 是否同一年
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameYear(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);

		return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR);
	}
	
	/**
	 * 是否同一月
	 * 
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameMonth(Date date1, Date date2) {
		Calendar c1 = Calendar.getInstance();
		c1.setTime(date1);
		Calendar c2 = Calendar.getInstance();
		c2.setTime(date2);

		if(c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR)) {
			return false;
		}
		return c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH);
	}

	/**
	 * 计算时间月数差
	 * 
	 * @param start
	 * @param end
	 * @return if start >= end return >=0 else return <0;
	 */
	public static int calcMonths(Date start, Date end) {

		System.err.println("---" + DateUtils.format_yyyyMMdd(start));
		System.err.println("---" + DateUtils.format_yyyyMMdd(end));

		int ratio = 1;
		if (end.before(start)) {
			Date tmp = end;
			end = start;
			start = tmp;
			ratio = -1;
		}

		Calendar startCal = Calendar.getInstance();
		startCal.setTime(start);
		Calendar endCal = Calendar.getInstance();
		endCal.setTime(end);

		int startYear = startCal.get(Calendar.YEAR);
		int endYear = endCal.get(Calendar.YEAR);

		int startMonth = startCal.get(Calendar.MONTH);
		int endMonth = endCal.get(Calendar.MONTH);

		int startDay = startCal.get(Calendar.DAY_OF_MONTH);
		int endDay = startCal.get(Calendar.DAY_OF_MONTH);

		int offsetMonth = 0;
		int offsetYear = endYear - startYear;
		// 2017-05-22---2018-03-28 2017-05-22 2018-05-15
		if (startMonth > endMonth || (startMonth == endMonth && startDay < endDay)) {
			offsetYear -= 1;
			offsetMonth = 12 - startMonth + endMonth;
		} else {
			offsetMonth = endMonth - startMonth;
		}

		return ratio * (offsetYear * 12 + offsetMonth);
	}
	
	/**
	 * 获取指定时间的0点
	 * @param time
	 * @return
	 */
	public static Date getFirstTime(Date time) {
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(time);
		
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		
		return calendar.getTime();
	}
}
