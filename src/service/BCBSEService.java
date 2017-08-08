package service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import constant.BCNumConstant;
import controller.BhavCopy;
import logger.LoggerWritingUtil;

public class BCBSEService  extends BCExchangeService{
	
	private String strClassName = BhavCopy.class.getSimpleName();
	
	public boolean downloadBC(){
		
		String strMethodName = Thread.currentThread().getStackTrace()[BCNumConstant.INT_ONE].getMethodName();

		LoggerWritingUtil.writeLoggerInfo(true, strMethodName, strClassName);
		
		
		
		LoggerWritingUtil.writeLoggerInfo(false, strMethodName, strClassName);
		
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean obtainDate(){
		
		String bcDate;
		
		System.out.println();
		System.out.println("If something is input, then its from date");
		System.out.println();
		System.out.println("Enter from date, in the format(mm/dd/yyy) : ");

		System.out.println();
		bcDate = BCService.reader.next();
		
		// validate the obtained dates
		validateDate(bcDate);
		
		return true;
	}
	
	/**
	 * 
	 * @param bcDate
	 * @return
	 */
	private boolean validateDate(final String bcDate) {
		
		String [] dateArray =null;
		SimpleDateFormat formatter = null;
		
		Date tempDate = null;
		
		
		try {
			toDate = new Date();
			// convert to date
			formatter = new SimpleDateFormat("dd/MM/yyyy");
			formatter.setLenient(false);
			fromDate = formatter.parse(bcDate);
			
			if(fromDate.compareTo(toDate) > 0) {
				fromDate = toDate;
			}
		} catch (ParseException ex) {
			fromDate = new Date();
		}

		return true;
	}

}
