package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import bean.StockInfoBean;
import bean.StockPriceBean;
import constant.BCNumConstant;
import controller.BhavCopy;
import dao.BhavCopyDao;
import logger.LoggerUtil;
import logger.LoggerWritingUtil;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import utils.MiscUtil;


public class BCExchangeService {
	
	private String strClassName = BhavCopy.class.getSimpleName();
	
	private BhavCopyDao bcDao = null;
	
	public static Calendar m_fromCalendar = null;
	public static Calendar m_toCalendar = null;
	
	public Map<String, Double> m_siMap = null;
	
	public static final String m_bseBCPath = "C:\\Users\\sundar\\StockData\\bse\\";
	public static final String m_nseBCPath = "C:\\Users\\sundar\\StockData\\nse\\";
	public static final File m_bseBCFilePath = new File(m_bseBCPath);
	public static final File m_nseBCFilePath = new File(m_nseBCPath);
	
	// First Key - Date of the BhavCopy, 
	// - second key - ISIN of the stock
	private Map<java.sql.Date, Map<String, StockPriceBean>> m_bseStockPriceMap = null;
	private Map<java.sql.Date, Map<String, StockPriceBean>> m_nseStockPriceMap = null;
	
	public BCExchangeService() throws ClassNotFoundException, SQLException{
		m_bseStockPriceMap = new LinkedHashMap<java.sql.Date, Map<String, StockPriceBean>>();
		m_nseStockPriceMap = new LinkedHashMap<java.sql.Date, Map<String, StockPriceBean>>();
		
		bcDao = new BhavCopyDao();
	}
	
	public void closeDBConnection() {
		bcDao.closeConnection();
	}
	
	
	public boolean deleteUnWantedFiles() throws IOException{
		
		try{
			FileUtils.cleanDirectory(m_bseBCFilePath);
			FileUtils.cleanDirectory(m_nseBCFilePath);
		}finally{
			
		}
		return true;
	}
	
	public boolean downloadBC() throws Exception{
		String strMethodName = Thread.currentThread().getStackTrace()[BCNumConstant.INT_ONE].getMethodName();
		try{
			LoggerWritingUtil.writeLoggerInfo(true, strMethodName, strClassName);
			// download bhavcopies from BSE
			downloadBSEBhavCopy();
			// download BhavCopies from NSE
			downloadNSEBhavCopy();
		}finally{
			LoggerWritingUtil.writeLoggerInfo(false, strMethodName, strClassName);
		}
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
		System.out.println("Enter from date, in the format(dd/mm/yyyy) : ");

		System.out.println();
		bcDate = Service.reader.next();
		
		// validate the obtained dates
		validateDate(bcDate);
		
		return true;
	}
	
	public boolean obtainSIDate() throws SQLException{
		
		try{
			m_siMap = bcDao.obtainSIDate();
		}finally{
			
		}
		return true;
	}
	
	public boolean readBC() throws IOException, ParseException{
		try{
			readBSEBC();
			readNSEBC();
		}finally{
			
		}
		return true;
	}
	
	public boolean saveBCData() throws SQLException{
		try{
			bcDao.saveBCData(m_bseStockPriceMap, true);
			bcDao.saveBCData(m_nseStockPriceMap, false);
		}finally{
			
		}
		return true;
	}
	
	/**
	 * 
	 * @param bcDate
	 * @return
	 */
	public boolean validateDate(final String bcDate) {
		
		Date fromDate = null;
		Date toDate = null;
		
		SimpleDateFormat formatter = null;
		
		try {
			toDate = new Date();
			// today's date
			m_toCalendar = Calendar.getInstance();
			
			// convert to date
			formatter = new SimpleDateFormat("dd/MM/yyyy");
			formatter.setLenient(false);
			fromDate = formatter.parse(bcDate);
			
			m_fromCalendar = Calendar.getInstance();
			m_fromCalendar.setTime(fromDate);
			
			if(fromDate.compareTo(toDate) > 0) {
				m_fromCalendar = Calendar.getInstance();
			}
		} catch (ParseException ex) {
			m_fromCalendar = Calendar.getInstance();
		}

		return true;
	}
	
	private boolean downloadBSEBhavCopy() throws Exception{
		
		Calendar bseFromCalendar = null;		
		
		StringBuilder bseBCURLFileName = null;
		int nDate = -1;
		int nMonth = -1;
		
		StringBuilder sbFileName = null;
		StringBuilder sb = null;
		StringBuilder sbDate = null;
		
		
		try{
			bseFromCalendar = Calendar.getInstance();
			bseFromCalendar.setTime(m_fromCalendar.getTime());
			// to download bhavcopies of different dates.
			while(bseFromCalendar.compareTo(m_toCalendar) <= 0){
				sbFileName = new StringBuilder("C:\\Users\\sundar\\StockData\\bse\\");
				
				sb = new StringBuilder("EQ_ISINCODE_");
				sbDate = new StringBuilder();
				
				bseBCURLFileName = new StringBuilder("http://www.bseindia.com/download/BhavCopy/Equity/");
						
						nDate = bseFromCalendar.get(Calendar.DAY_OF_MONTH);
						
						if(nDate < 10){
							sb.append("0");
							sbDate.append("0");
						}
						
						sb.append(nDate);
						
						sbDate.append(nDate).append("-");
						
						nMonth = bseFromCalendar.get(Calendar.MONTH) + 1;
						
						if(nMonth < 10){
							sb.append("0");
							sbDate.append("0");
						}
						
						sb.append(nMonth)
						.append(bseFromCalendar.get(Calendar.YEAR) % 2000)
						.append(".zip");
						
						sbDate.append(nMonth).append("-")
						.append(bseFromCalendar.get(Calendar.YEAR));
						
						bseBCURLFileName.append(sb);
						sbFileName.append(sb);
						
						try{
							FileUtils.copyURLToFile(new URL(bseBCURLFileName.toString()), 
									new File(sbFileName.toString()));
						}catch(IOException ex){
							// continue the while loop
							LoggerUtil.error(String.format("could not find \"%s\" \n"
									+ "for the date \"%s\"", bseBCURLFileName.toString(), sbDate.toString()));
						}finally{
							bseFromCalendar.add(Calendar.DAY_OF_MONTH, 1);;
						}
			}
		}finally{
			
		}
		return true;
	}
	
	private boolean downloadNSEBhavCopy() throws Exception{
		
		Calendar nseFromCalendar = null;		
		
		StringBuilder nseBCURLFileName = null;
		int nDate = -1;
		int nMonth = -1;
		
		String strMonth = null;
		
		StringBuilder sbFileName = null;
		StringBuilder sbURLFileName = null;
		StringBuilder sbDate = null;
		
		try{
			nseFromCalendar = Calendar.getInstance();
			nseFromCalendar.setTime(m_fromCalendar.getTime());
			
			while(nseFromCalendar.compareTo(m_toCalendar) <= 0){
				sbFileName = new StringBuilder("C:\\Users\\sundar\\StockData\\nse\\");
				
				sbDate = new StringBuilder();
				
				nseBCURLFileName = new StringBuilder("https://www.nseindia.com/content/historical/EQUITIES/")
						.append(nseFromCalendar.get(Calendar.YEAR)).append("/");
				
				nMonth = nseFromCalendar.get(Calendar.MONTH) + 1;
				
				strMonth = MiscUtil.getMonthString(nMonth);

				nseBCURLFileName.append(strMonth).append("/");
				
				sbURLFileName = new StringBuilder("cm");
				
				nDate = nseFromCalendar.get(Calendar.DAY_OF_MONTH);
				
				if(nDate < 10){
					sbURLFileName.append("0");
					sbDate.append("0");
				}
				
				sbURLFileName.append(nDate);
				sbDate.append(nDate).append("-");
				
				if(nMonth < 10){
					sbDate.append("0");
				}
				
				sbDate.append(nMonth).append("-");
				sbDate.append(nseFromCalendar.get(Calendar.YEAR));
				
				
				sbURLFileName.append(strMonth);
				sbURLFileName.append(nseFromCalendar.get(Calendar.YEAR));
				sbURLFileName.append("bhav.csv.zip");
				
				nseBCURLFileName.append(sbURLFileName);
				sbFileName.append(sbURLFileName);
				
				try{
					FileUtils.copyURLToFile(new URL(nseBCURLFileName.toString()), 
							new File(sbFileName.toString()));
				}catch(IOException ex){
					// continue the while loop
					LoggerUtil.error(String.format("could not find \"%s\" \n"
							+ "for the date \"%s\"", nseBCURLFileName.toString(), sbDate.toString()));
				}finally{
					nseFromCalendar.add(Calendar.DAY_OF_MONTH, 1);;
				}
			}
		}finally{
			
		}
		return true;
	}
	
	private boolean readBSEBC() throws IOException, ParseException{
		String strBCFilePath = "C:\\Users\\sundar\\StockData\\bse\\";
		File bCFilePath = null;
		String[] extensions = {"zip"};
		String[] csvFileExt = {"CSV"};
		
		Collection<File> bcZipFileSet = null;
		Collection<File> bcCsvFileSet = null;
		
		String source = null;
		StringBuilder destination = null;
		
		int zipStartIndex = -1;
		BufferedReader br = null;
		
		String line = "";
		String cvsSplitBy = ",";
		String [] dataArr = null;
		int header = 1;
		
		String strIsin = null;
		double fOpen = 0.0; 
		double fHigh = 0.0; 
		double fLow = 0.0; 
		double fClose = 0.0; 
		double fPrevClose = 0.0; 
		int nVloume = 0;
		int nNumberOfTrades = 0;
		double fTurnOver = 0.0;
		double fFaceValue = 0.0;
		String strEquity = null;
		java.sql.Date tradingDate = null;
		java.sql.Date prevTradingDate = null;
		String strTradingDate = null;
		SimpleDateFormat sdf = null;
		
		Map<String, StockPriceBean> stockPriceLocalMap = null;
		StockPriceBean spBean = null;
		
		try {
			bCFilePath = new File(strBCFilePath);
			bcZipFileSet = FileUtils.listFiles(bCFilePath, extensions, true);

			// from the ZIP file set, read the bhavcopy
			for (File bseZipFile : bcZipFileSet) {
				
				zipStartIndex = -1;
				
				try {
					source = bseZipFile.getAbsolutePath();
					ZipFile zipFile = new ZipFile(source);
					if (zipFile.isEncrypted()) {
						zipFile.setPassword("");
					}
					
					zipFile.extractAll(strBCFilePath);
				} catch (ZipException e) {

				}
			}
			
			// extract data from the bhavcopy
			bcCsvFileSet = FileUtils.listFiles(bCFilePath, csvFileExt, true);
			
			for (File bseCsvFile : bcCsvFileSet) {
				br = new BufferedReader(new FileReader(bseCsvFile.getAbsolutePath()));
				header = 1;
				
				tradingDate = null;
				
				// isin is the key
				stockPriceLocalMap = new LinkedHashMap<String, StockPriceBean>();
				
				while ((line = br.readLine()) != null) {

					dataArr = line.split(cvsSplitBy);
					
					strEquity = dataArr[3].trim();

					if (header++ == 1 || strEquity.compareToIgnoreCase("Q") != 0) {
						// skip the header
						continue;
					}

					strIsin = dataArr[14].trim();
					fOpen = Double.parseDouble(dataArr[4].trim());
					fHigh = Double.parseDouble(dataArr[5].trim());
					fLow = Double.parseDouble(dataArr[6].trim());;
					fClose = Double.parseDouble(dataArr[7].trim());
					fPrevClose = Double.parseDouble(dataArr[9].trim());
					nNumberOfTrades = Integer.parseInt(dataArr[10].trim());
					nVloume = Integer.parseInt(dataArr[11].trim());
					fTurnOver =  Double.parseDouble(dataArr[12].trim());
					strTradingDate = dataArr[15].trim();
					fFaceValue = 0.0;
					
					sdf = new SimpleDateFormat("dd-MMM-yy");
					
					if(tradingDate == null){
						tradingDate = new java.sql.Date(sdf.parse(strTradingDate).getTime());  
						prevTradingDate = tradingDate;
					} else if(((prevTradingDate != null && tradingDate != null) && tradingDate.compareTo(prevTradingDate) != 0)){
						// write to error log.
					}
					
					tradingDate = new java.sql.Date(sdf.parse(strTradingDate).getTime());
					spBean = new StockPriceBean();
					
					spBean.setIsin(strIsin);
					spBean.setOpen(fOpen);
					spBean.setHigh(fHigh);
					spBean.setLow(fLow);
					spBean.setClose(fClose);
					spBean.setPrevClose(fPrevClose);
					
					
					spBean.setNumberOfTrades(nNumberOfTrades);
					spBean.setVolume(nVloume);
					spBean.setNetTurnover(fTurnOver);
					spBean.setTradingDate(tradingDate);
					
					if(m_siMap.containsKey(strIsin)){
						spBean.setFaceValue(m_siMap.get(strIsin));
						stockPriceLocalMap.put(strIsin, spBean);
					}
				}
				// Map<java.sql.Date, Map<String, StockPriceBean>> m_bseStockPriceMap = null;
				m_bseStockPriceMap.put(tradingDate, stockPriceLocalMap);
			}
		}finally{
			
		}
		return true;
	}
	
	private boolean readNSEBC() throws IOException, ParseException{
		
		String strBCFilePath = "C:\\Users\\sundar\\StockData\\nse\\";
		File bCFilePath = null;
		String[] extensions = {"zip"};
		String[] csvFileExt = {"csv"};
		
		Collection<File> bcZipFileSet = null;
		Collection<File> bcCsvFileSet = null;
		
		String source = null;
		StringBuilder destination = null;
		
		int zipStartIndex = -1;
		BufferedReader br = null;
		
		String line = "";
		String cvsSplitBy = ",";
		String [] dataArr = null;
		int header = 1;
		
		String strIsin = null;
		double fOpen = 0.0; 
		double fHigh = 0.0; 
		double fLow = 0.0; 
		double fClose = 0.0; 
		double fPrevClose = 0.0; 
		int nVloume = 0;
		int nNumberOfTrades = 0;
		double fTurnOver = 0.0;
		double fFaceValue = 0.0;
		String strEquity = null;
		java.sql.Date tradingDate = null;
		java.sql.Date prevTradingDate = null;
		String strTradingDate = null;
		SimpleDateFormat sdf = null;
		
		Map<String, StockPriceBean> stockPriceLocalMap = null;
		StockPriceBean spBean = null;
		
		try {
			bCFilePath = new File(strBCFilePath);
			bcZipFileSet = FileUtils.listFiles(bCFilePath, extensions, true);

			// from the ZIP file set, read the bhavcopy
			for (File nseZipFile : bcZipFileSet) {

				zipStartIndex = -1;

				try {
					source = nseZipFile.getAbsolutePath();
					ZipFile zipFile = new ZipFile(source);
					if (zipFile.isEncrypted()) {
						zipFile.setPassword("");
					}

					zipFile.extractAll(strBCFilePath);
				} catch (ZipException e) {

				}
			}
			
			// extract data from the bhavcopy
			bcCsvFileSet = FileUtils.listFiles(bCFilePath, csvFileExt, true);
			
			for (File nseCsvFile : bcCsvFileSet) {
				br = new BufferedReader(new FileReader(nseCsvFile.getAbsolutePath()));
				header = 1;

				tradingDate = null;

				// isin is the key
				stockPriceLocalMap = new LinkedHashMap<String, StockPriceBean>();

				while ((line = br.readLine()) != null) {
					dataArr = line.split(cvsSplitBy);

					strEquity = dataArr[3].trim();

					if (header++ == 1) {
						// skip the header
						continue;
					}

					strIsin = dataArr[12].trim();
					fOpen = Double.parseDouble(dataArr[2].trim());
					fHigh = Double.parseDouble(dataArr[3].trim());
					fLow = Double.parseDouble(dataArr[4].trim());
					fClose = Double.parseDouble(dataArr[5].trim());
					fPrevClose = Double.parseDouble(dataArr[7].trim());
					nNumberOfTrades = Integer.parseInt(dataArr[11].trim());
					nVloume = Integer.parseInt(dataArr[8].trim());
					fTurnOver = Double.parseDouble(dataArr[9].trim());
					strTradingDate = dataArr[10].trim();
					fFaceValue = 0.0;

					sdf = new SimpleDateFormat("dd-MMM-yy");

					if (tradingDate == null) {
						tradingDate = new java.sql.Date(sdf.parse(strTradingDate).getTime());
						prevTradingDate = tradingDate;
					} else if (((prevTradingDate != null && tradingDate != null)
							&& tradingDate.compareTo(prevTradingDate) != 0)) {
						// write to error log.
					}

					tradingDate = new java.sql.Date(sdf.parse(strTradingDate).getTime());
					spBean = new StockPriceBean();
					
					spBean.setIsin(strIsin);
					spBean.setOpen(fOpen);
					spBean.setHigh(fHigh);
					spBean.setLow(fLow);
					spBean.setClose(fClose);
					spBean.setPrevClose(fPrevClose);
					
					
					spBean.setNumberOfTrades(nNumberOfTrades);
					spBean.setVolume(nVloume);
					spBean.setNetTurnover(fTurnOver);
					spBean.setTradingDate(tradingDate);
					
					if(m_siMap.containsKey(strIsin)){
						spBean.setFaceValue(m_siMap.get(strIsin));
						stockPriceLocalMap.put(strIsin, spBean);
					}
				}
				// Map<java.sql.Date, Map<String, StockPriceBean>> m_bseStockPriceMap = null;
				m_nseStockPriceMap.put(tradingDate, stockPriceLocalMap);
			}
		}finally{
			
		}
		return true;
	}
}
