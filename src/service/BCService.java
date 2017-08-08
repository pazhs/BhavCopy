package service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import constant.BCNumConstant;
import controller.BhavCopy;
import logger.LoggerWritingUtil;

public class BCService {
	
	public static Scanner reader = new Scanner(System.in);  // Reading from System.in
	
	private BCBSEService bseServiceObj = new BCBSEService();
	private BCNSEService nseServiceObj = new BCNSEService();
	
	
	private String strClassName = BhavCopy.class.getSimpleName();

	public boolean execute() {
		
		String strMethodName = Thread.currentThread().getStackTrace()[BCNumConstant.INT_ONE].getMethodName();

		LoggerWritingUtil.writeLoggerInfo(true, strMethodName, strClassName);
		
		try{
			// obtain the date of the bhavcopy of both BSE and NSE
			obtainDate();
			
			// download bhavcopy
			downloadBC();
			
			// after downloading, extract data from the bhavcopy
			readBC();
			
			// store the bhavcopy data into the database
			saveBCData();
			
			// after saving the data into the database, take the backup of the database
			backupDBData();

			LoggerWritingUtil.writeLoggerInfo(false, strMethodName, strClassName);
			
		} finally{
			reader.close();
		}

		return true;
	}
	
	private boolean obtainDate() {
		
		// this sets the bhavcopy dates for both BSE and NSE
		bseServiceObj.obtainDate();
		
		return true;
	}

	private boolean downloadBC() {
		
		String strMethodName = Thread.currentThread().getStackTrace()[BCNumConstant.INT_ONE].getMethodName();

		LoggerWritingUtil.writeLoggerInfo(true, strMethodName, strClassName);
		
		// once the date has been obtained, fromDate is always set and 
		// check whether the toDate is not null
		bseServiceObj.downloadBC();
		nseServiceObj.downloadBC();
		
		LoggerWritingUtil.writeLoggerInfo(false, strMethodName, strClassName);
		return true;
	}

	private boolean readBC() {
		
		String strMethodName = Thread.currentThread().getStackTrace()[BCNumConstant.INT_ONE].getMethodName();

		LoggerWritingUtil.writeLoggerInfo(true, strMethodName, strClassName);
		
		
		LoggerWritingUtil.writeLoggerInfo(false, strMethodName, strClassName);
		return true;
	}

	private boolean saveBCData() {
		
		String strMethodName = Thread.currentThread().getStackTrace()[BCNumConstant.INT_ONE].getMethodName();

		LoggerWritingUtil.writeLoggerInfo(true, strMethodName, strClassName);
		
		
		LoggerWritingUtil.writeLoggerInfo(false, strMethodName, strClassName);
		return true;
	}

	private boolean backupDBData() {
		String strMethodName = Thread.currentThread().getStackTrace()[BCNumConstant.INT_ONE].getMethodName();

		LoggerWritingUtil.writeLoggerInfo(true, strMethodName, strClassName);
		
		
		LoggerWritingUtil.writeLoggerInfo(false, strMethodName, strClassName);
		
		return true;
	}

}
