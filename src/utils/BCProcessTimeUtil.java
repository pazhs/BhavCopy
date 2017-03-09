package utils;

import org.joda.time.DateTime;
import org.joda.time.Period;

import constant.BCNumConstant;
import constant.BCStrNumConstant;
import constant.BCStrProcConstant;
import logger.LoggerUtil;


public class BCProcessTimeUtil {
	
	public static void end(final DateTime startTime, final DateTime endTime, final boolean bFromMethod, final String strMethodName) {

		// find the hour, minute, second and millisecond component
		Period p = new Period(startTime, endTime);
		
		int hours = p.getHours();
		int minutes = p.getMinutes();
		int seconds = p.getSeconds();
		int milliSeconds = p.getMillis();

		StringBuilder sbHour = null;
		StringBuilder sbMin = null;
		StringBuilder sbSeconds = null;
		StringBuilder sbMilli = null;

		if (hours < BCNumConstant.INT_TEN) {
			sbHour = new StringBuilder(BCStrNumConstant.STR_ZERO).append(String.valueOf(hours));
		} else {
			sbHour = new StringBuilder(String.valueOf(hours));
		}

		if (minutes < BCNumConstant.INT_TEN) {
			sbMin = new StringBuilder(BCStrNumConstant.STR_ZERO).append(String.valueOf(minutes));
		} else {
			sbMin = new StringBuilder(String.valueOf(minutes));
		}
		
		if(seconds < BCNumConstant.INT_TEN) {
			sbSeconds = new StringBuilder(BCStrNumConstant.STR_ZERO).append(String.valueOf(seconds));
		} else {
			sbSeconds = new StringBuilder(String.valueOf(seconds));
		}
		if (milliSeconds < BCNumConstant.INT_TEN) {
			sbMilli = new StringBuilder(BCStrNumConstant.STR_ZERO).append(BCStrNumConstant.STR_ZERO).append(String.valueOf(milliSeconds));
		} else if (milliSeconds < BCNumConstant.INT_HUNDRED) {
			sbMilli = new StringBuilder(BCStrNumConstant.STR_ZERO).append(String.valueOf(milliSeconds));
		} else {
			sbMilli = new StringBuilder(String.valueOf(milliSeconds));
		}
		
		if( !bFromMethod ) {
			LoggerUtil.info(String.format(BCStrProcConstant.PROC_ELAPSED_TIME, sbHour.toString(), sbMin.toString(), sbSeconds.toString(),
					sbMilli.toString()));
		} else {
		    LoggerUtil.info(String.format(BCStrProcConstant.METHOD_ELAPSED_TIME, strMethodName, sbHour.toString(), sbMin.toString(), 
					sbSeconds.toString(), sbMilli.toString()));
		}
	}
}
