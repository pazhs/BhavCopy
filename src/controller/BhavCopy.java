package controller;


import java.io.IOException;
import java.sql.SQLException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.joda.time.DateTime;

import constant.BCNumConstant;
import constant.BCStrLtrConstant;
import constant.BCStrProcConstant;
import logger.LoggerUtil;
import logger.LoggerWritingUtil;
import service.BCService;
import utils.BCProcessTimeUtil;


public class BhavCopy {


	public static void main(String[] args) {
		
		String strClassName = BhavCopy.class.getSimpleName();
        String strMethodName = Thread.currentThread().getStackTrace()[BCNumConstant.INT_ONE]
                .getMethodName();
        
        Runtime rt = null;
        DateTime startTime = null;
        DateTime endTime = null;
        int exitStatus = BCNumConstant.INT_ZERO;
        
        BhavCopy mainObj = new BhavCopy();
        
		try{
			LoggerUtil.info(String.format(BCStrProcConstant.AC_PROC_SE, BCStrLtrConstant.AC_START));
            LoggerWritingUtil.writeLoggerInfo(true, strMethodName, strClassName);

            startTime = new DateTime();

            rt = Runtime.getRuntime();
            
            mainObj.execute();
            
            endTime = new DateTime();
            
            LoggerWritingUtil.writeLoggerInfo(false, strMethodName, strClassName);
            BCProcessTimeUtil.end(startTime, endTime, false, BCStrLtrConstant.STR_EMPTY);
            LoggerUtil.info(String.format(BCStrProcConstant.AC_PROC_SE, BCStrLtrConstant.AC_END));
		}catch(Exception ex) {
            exitStatus = BCNumConstant.INT_ONE;
            LoggerUtil.error(ExceptionUtils.getStackTrace(ex));
        }finally {
        	
            rt.exit(exitStatus);
        }
	}
	
	/**
	 * Execute.
	 *
	 * @return true, if successful
	 * @throws SQLException the SQL exception
	 * @throws IOException 
	 */
	public boolean execute() throws SQLException, IOException {
		
		BCService serviceObj = new BCService();
		serviceObj.execute();
		return true;
	}
}
