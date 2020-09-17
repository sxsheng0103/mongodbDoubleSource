package com.pangu.crawler.business.controller;

import com.alibaba.fastjson.JSONObject;

public interface ShenbaoInterface {
	
	String ShenbaoZuoFei(String trace, String nsrsbh, JSONObject jsonObject) throws Exception;

}
