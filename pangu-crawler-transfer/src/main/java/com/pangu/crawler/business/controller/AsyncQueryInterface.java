package com.pangu.crawler.business.controller;
import org.openqa.selenium.WebDriver;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.framework.model.ResultBean;

public interface AsyncQueryInterface {
	JSON queryResultTransform(String nsrsbh,String sign,JSONObject jsonObject,String body)throws Exception;
	
	ResultBean asyncLoginInteraction(String nsrsbh,String trace,WebDriver webDriver,JSONObject jsonObject)throws Exception;

	boolean checkCookieEffective(String nsrsbh,String trace,JSONObject jsonObject)throws Exception;
}
