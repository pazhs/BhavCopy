package service;

import constant.BCNumConstant;
import controller.BhavCopy;

public class BCService {
	
	public boolean execute(){
		
		String strClassName = BhavCopy.class.getSimpleName();
        String strMethodName = Thread.currentThread().getStackTrace()[BCNumConstant.INT_ONE]
                .getMethodName();
		
		return true;
	}
	
	private boolean downloadBC(){
		return true;
	}
	
	private boolean readBC(){
		return true;
	}
	
	private boolean saveBCData() {
		return true;
	}
	
	private boolean backupDBData() {
		return true;
	}

}
