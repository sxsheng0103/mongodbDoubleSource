package com.pangu.crawler.business.service.async;

import java.util.ArrayList;
import java.util.HashMap;

import org.openqa.selenium.WebDriver;

public class AsyncConfig {
	public static HashMap<String,WebDriver> DZZZDLMap=new HashMap<String,WebDriver>();
	
	public final static ArrayList<String> CurrentMonthDataSign=new ArrayList<String>() {{
	    	add("queryThisMonthQingce");
	    	add("queryTemporaryHistoricalData");
	    	add("queryQianShuiInfo");
	    }};
	    
	    public final static ArrayList<String> LOG_IN_WITH_BROWSER_OF_NSRDQ=new ArrayList<String>() {{
//	    	add("shanghai");
//	    	add("fujian");
//	    	add("chongqing");
	    }};

}
