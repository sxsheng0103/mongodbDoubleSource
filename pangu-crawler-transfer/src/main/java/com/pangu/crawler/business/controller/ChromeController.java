package com.pangu.crawler.business.controller;

import org.openqa.selenium.WebDriver;

import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.framework.model.ResultBean;

public interface ChromeController{
	  /**
     * 登录并构造返回结果<br/>
	 * @throws Exception TODO
     */
	ResultBean login(WebDriver webDriver,String trace, String nsrsbh, JSONObject jsonObject) throws Exception;
    
	/**
     * 检查页面状态
	 * @throws Exception TODO
     */
    boolean loginPageCheck(WebDriver webDriver,String trace, String nsrsbh, JSONObject jsonObject) throws Exception;
    /**
     * 查询登记并构造返回结果<br/>
     * 注意：<br/>
     * @param jsonObject    任务报文对象
     * @throws Exception TODO
     */
    String queryRegister(WebDriver webDriver,String trace,String nsrsbh,JSONObject jsonObject) throws Exception;

    /**
     * 查询历史数据并构造返回结果<br/>
     * 注意：<br/>
     * @param jsonObject    任务报文对象
     * @throws Exception TODO
     */
    String queryHistoricalData(WebDriver webDriver,String trace,String nsrsbh,JSONObject jsonObject) throws Exception;

    /**
     * 查询税费种认定并构造返回结果<br/>
     * 注意：<br/>
     * @param jsonObject    任务报文对象
     * @throws Exception TODO
     */
    String queryTaxsDetermine(WebDriver webDriver,String trace,String nsrsbh,JSONObject jsonObject) throws Exception;

    /**
     *	查询 发票信息
     * @throws Exception TODO
     */
    String queryFapiaoInfo(WebDriver webDriver,String trace,String nsrsbh,JSONObject jsonObject) throws Exception;

}
