package service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import constant.BCNumConstant;
import controller.BhavCopy;
import logger.LoggerWritingUtil;

public class Service {
	
	public static Scanner reader = new Scanner(System.in);  // Reading from System.in
	
	private BCExchangeService exchServiceObj = null;
	
	
	private String strClassName = Service.class.getSimpleName();
	
	public Service() throws ClassNotFoundException, SQLException{
		exchServiceObj = new BCExchangeService();
	}
	
	public boolean closeDBConnection(){
		
		exchServiceObj.closeDBConnection();
		return true;
	}

	public boolean execute() throws Exception {
		
		String strMethodName = Thread.currentThread().getStackTrace()[BCNumConstant.INT_ONE].getMethodName();

		LoggerWritingUtil.writeLoggerInfo(true, strMethodName, strClassName);
		
		try{
			// obtain the date of the bhavcopy of both BSE and NSE
			exchServiceObj.obtainDate();
			
			// delete the unwanted data in the download paths
			exchServiceObj.deleteUnWantedFiles();
			
			// download bhavcopy
			exchServiceObj.downloadBC();
			
			// get the faceValue of the stocks from "tab_stock_info"
			exchServiceObj.obtainSIDate();
			
			// after downloading, extract data from the bhavcopy
			exchServiceObj.readBC();
			
			// store the bhavcopy data into the database
			exchServiceObj.saveBCData();
			
			// after saving the data into the database, take the backup of the database
			// backupDBData();

			LoggerWritingUtil.writeLoggerInfo(false, strMethodName, strClassName);
			
		} finally{
			reader.close();
		}
		return true;
	}
}
