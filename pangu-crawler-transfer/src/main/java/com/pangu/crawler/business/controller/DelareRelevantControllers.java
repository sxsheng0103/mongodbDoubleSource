/*
package com.pangu.crawler.business.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryCallRecordOperation;
import com.pangu.crawler.business.service.async.AsyncQueryService;
import com.pangu.crawler.business.service.async.BusinessException;
import com.pangu.crawler.business.service.async.ParamsCheck;
import com.pangu.crawler.business.service.login.LoginService;
import com.pangu.crawler.framework.exception.ClientException;
import com.pangu.crawler.framework.exception.LoginFailException;
import com.pangu.crawler.framework.model.AreaCode;
import com.pangu.crawler.framework.model.Constant;
import com.pangu.crawler.framework.model.ResultBean;
import com.pangu.crawler.framework.selenium.SeleniumUtil;
import com.pangu.crawler.framework.utils.AppUtil;
import com.pangu.crawler.framework.utils.TraceHelp;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
@Api(tags = "【申报查询对外接口】")
public class DelareRelevantControllers {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	AsyncQueryService asyncqueryservice;

	@Autowired
	private AppUtil appUtil;
	
	@Autowired
	LoginService loginservice;

	*/
/**
	 * CONTROLLER 入口
	 * @param str 任务报文对象
	 * @throws LoginFailException   如果出现其它错误或失败，则抛出该异常
	 *//*

	@ResponseBody
	@PostMapping("/loginAndQuery")
	@ApiOperation(value = "登录以及申报查询", notes = "登录以及申报查询接口")
	public String entrance(@RequestBody String str) throws LoginFailException,ClientException{
		String returnStr = "";
		String trace = TraceHelp.uuid();
		JSONObject jsonObject =JSONObject.parseObject(str);
		String sign = String.valueOf(jsonObject.get("sign"));
		String nsrdq = String.valueOf(jsonObject.get("nsrdq"));
		String nsrsbh = String.valueOf(jsonObject.get("nsrsbh"));

		returnStr= dealWithRequest(false,sign, nsrdq, trace, nsrsbh, jsonObject);
		return returnStr;
	}


	public String dealWithRequest(boolean isAsync,String sign,String nsrdq,String trace,String nsrsbh,JSONObject jsonObject) {
		JSON outData=null;
		if(nsrdq.equals(AreaCode.shanghai)||nsrdq.equals(AreaCode.fujian)||nsrdq.equals(AreaCode.qinghai)||nsrdq.equals(AreaCode.beijing)) {
			WebDriver webdriver =null;
			boolean seleniumClose=false;
			try {
				webdriver=SeleniumUtil.loadChromeDriver();
//				boolean isLogin=appUtil.getChromeController("chrome_"+nsrdq).checkCookieEffective(trace, nsrsbh, jsonObject);
//				if(!isLogin) {
					appUtil.getChromeController("chrome_"+nsrdq).login(webdriver,trace,nsrsbh,jsonObject);
					SeleniumUtil.saveCookie(trace, nsrsbh,nsrdq, webdriver);
//				}
					//				if(true) {
					SeleniumUtil.closeDriver(webdriver);
					seleniumClose=true;

					outData=ordinaryCircumstances( sign, nsrdq, trace, nsrsbh, jsonObject);
					//				}else {
					//					returnStr=seleniumCircumstances(isAsync, webdriver, sign, nsrdq, trace, nsrsbh, jsonObject);
					//				}
			}catch (Exception e){
				logger.error("{["+nsrdq+"]}:",e);
				Throwable cause = e.getCause();
				return ResultBean.FAIL(500,cause.toString()).toString();
			}finally{
				if(!seleniumClose&&webdriver!=null) {
					SeleniumUtil.closeDriver(webdriver);
				}
			}

		}else {
			try {
				appUtil.getBaseController(nsrdq).login(trace,nsrsbh,jsonObject);
			}catch (Exception e){
				logger.error("{["+nsrdq+"]}:",e);
				return ResultBean.FAIL(500,"登录失败！").toString();
			}

			try {
				outData=ordinaryCircumstances(sign, nsrdq, trace, nsrsbh, jsonObject);
			} catch (Exception e) {
				logger.error("{["+nsrdq+"]}:",e);
				return ResultBean.FAIL(500,"查询失败！").toString();
			}
		}

		return outData.toJSONString();
	}

	public JSON ordinaryCircumstances(String sign,String nsrdq,String trace,String nsrsbh,JSONObject jsonObject) throws Exception{
		JSON outData=null;
		logger.info("{["+nsrdq+"]}:"+Thread.currentThread().getId());
		if(Constant.QUERYREGISTER.equals(sign)){
			outData = appUtil.getBaseController(nsrdq).queryRegister(trace,nsrsbh,jsonObject);
		}else if(Constant.QUERYHISTORICALDATA.equals(sign)){
			outData = appUtil.getBaseController(nsrdq).queryHistoricalData(trace,nsrsbh,jsonObject);
		}else if(Constant.QUERYTAXSDETERMINE.equals(sign)){
			outData = appUtil.getBaseController(nsrdq).queryTaxsDetermine(trace,nsrsbh,jsonObject);
		}else if(Constant.QUERYFAPIAOINFO.equals(sign)){
			outData = appUtil.getBaseController(nsrdq).queryFapiaoInfo(trace,nsrsbh,jsonObject);
		}else if(Constant.QUERYHISTORICALDATALIST.equals(sign)){
			outData = appUtil.getBaseController(nsrdq).queryHistoricalDataList(trace,nsrsbh,jsonObject);
		}else if(Constant.QUERYTHISMONTHQINGCE.equals(sign)){
			outData = appUtil.getBaseController(nsrdq).queryThisMonthQingce(trace,nsrsbh,jsonObject);
		}else if(Constant.QUERYJIAOKUANXINXI.equals(sign)){
			outData = appUtil.getBaseController(nsrdq).queryJiaoKuanXinXi(trace,nsrsbh,jsonObject);
		}else if(Constant.QUERYTEMPORARYHISTORICALDATA.equals(sign)){
			outData = appUtil.getBaseController(nsrdq).queryTemporaryHistoricalData(trace,nsrsbh,jsonObject);
		}else if(Constant.QUERYQIANSHUIINFO.equals(sign)){
			outData = appUtil.getBaseController(nsrdq).queryQianShuiInfo(trace,nsrsbh,jsonObject);

		}
		return outData;
	}

	public String seleniumCircumstances(boolean isAsync,WebDriver webdriver,String sign,String nsrdq,String trace,String nsrsbh,JSONObject jsonObject) throws Exception {
		logger.info("{["+nsrdq+"]}:"+Thread.currentThread().getId());
		String returnStr="";
		if(Constant.QUERYREGISTER.equals(sign)){
			returnStr = appUtil.getChromeController(nsrdq).queryRegister(webdriver,trace,nsrsbh,jsonObject);
		}else if(Constant.QUERYHISTORICALDATA.equals(sign)){
			returnStr = appUtil.getChromeController(nsrdq).queryHistoricalData(webdriver,trace,nsrsbh,jsonObject);
		}else if(Constant.QUERYTAXSDETERMINE.equals(sign)){
			returnStr = appUtil.getChromeController(nsrdq).queryTaxsDetermine(webdriver,trace,nsrsbh,jsonObject);
		}else if(Constant.QUERYFAPIAOINFO.equals(sign)){
			returnStr = appUtil.getChromeController(nsrdq).queryFapiaoInfo(webdriver,trace,nsrsbh,jsonObject);
		}
		return returnStr;
	}

	
	
	@ResponseBody
	@PostMapping("/getCookies")
	@ApiOperation(value = "直接通过税号查询cookies接口", notes = "直接通过税号查询cookies")
	public String getCookies(@RequestBody String str) throws Exception{
		JSONObject jsonObject =JSONObject.parseObject(str);
		String nsrsbh = jsonObject.getString("nsrsbh");
		String customerid = jsonObject.getString("customerid");
		
		String trace = TraceHelp.uuid();
		ResultBean out=null;
		try {
			ParamsCheck.Customerid(customerid);
			ParamsCheck.requestCheck(new String[] {"nsrsbh#String"}, jsonObject);
			out= loginservice.getCookies(nsrsbh,trace,customerid,jsonObject);
			return out.toString();
		}catch (BusinessException e) {
			out=  ResultBean.ParamsFAIL( e.getMessage());
			return out.toString();
		}catch (Exception e) {
			log.error("getCookies发生异常",e);
			out=  ResultBean.FAIL(500);
			return out.toString();
		}finally {
			AsyncQueryCallRecordOperation.saveInfo(customerid, nsrsbh,out.getCode(), "/getCookies", out.toString());
		}
	}
	
	@ResponseBody
	@PostMapping("/getNeedLogin")
	@ApiOperation(value = "直接通过用户id 查询有没有要登陆的", notes = "直接通过用户id 查询有没有要登陆的")
	public String getNeedLogin(@RequestBody String str) throws Exception{
		JSONObject jsonObject =JSONObject.parseObject(str);
		String customerid = jsonObject.getString("customerid");
		
		String trace = TraceHelp.uuid();
		ResultBean out=null;
		try {
			ParamsCheck.Customerid(customerid);
			ParamsCheck.requestCheck(new String[] {"nsrdq#String"}, jsonObject);
			out= loginservice.getNeedLogin(customerid,trace, jsonObject);
			return out.toString();
		}catch (BusinessException e) {
			return ResultBean.ParamsFAIL( e.getMessage()).toString();
		}catch (Exception e) {
			log.error("getNeedLogin发生异常",e);
			return ResultBean.FAIL(500).toString();
		}
	}
	
	@ResponseBody
	@PostMapping("/getNewNeedLoginByNsrsbh")
	@ApiOperation(value = "直接通过用户id 查询有没有要登陆的", notes = "直接通过用户id 查询有没有要登陆的")
	public String getNewNeedLoginByNsrsbh(@RequestBody String str) throws Exception{
		JSONObject jsonObject =JSONObject.parseObject(str);
		String customerid = jsonObject.getString("customerid");
		
		String trace = TraceHelp.uuid();
		ResultBean out=null;
		try {
			ParamsCheck.Customerid(customerid);
			ParamsCheck.requestCheck(new String[] {"nsrsbh#String"}, jsonObject);
			out= loginservice.getNewNeedLoginByNsrsbh(customerid,trace, jsonObject);
			return out.toString();
		}catch (BusinessException e) {
			return ResultBean.ParamsFAIL(e.getMessage()).toString();
		}catch (Exception e) {
			log.error("getNeedLogin发生异常",e);
			return ResultBean.FAIL(500).toString();
		}
	}
	
	@ResponseBody
	@PostMapping("/setNeedLoginErrorInfo")
	@ApiOperation(value = "保存登陆异常信息", notes = "保存登陆异常信息")
	public String setNeedLoginErrorInfo(@RequestBody String str) throws Exception{
		JSONObject jsonObject =JSONObject.parseObject(str);
		String customerid = jsonObject.getString("customerid");
		
		String trace = TraceHelp.uuid();
		ResultBean out=null;
		try {
			ParamsCheck.Customerid(customerid);
			ParamsCheck.requestCheck(new String[] {"lsh#String","errorInfo#String","state#int"}, jsonObject);
			out= loginservice.setNeedLoginErrorInfo(customerid,trace, jsonObject);
			return out.toString();
		}catch (BusinessException e) {
			return ResultBean.ParamsFAIL(e.getMessage()).toString();
		}catch (Exception e) {
			log.error("setNeedLoginErrorInfo发生异常",e);
			return ResultBean.FAIL(500).toString();
		}
	}
	
	@ResponseBody
	@PostMapping("/setCookies")
	@ApiOperation(value = "设置cookies", notes = "设置cookies")
	public String setCookies(@RequestBody String str) throws Exception{
		JSONObject jsonObject =JSONObject.parseObject(str);
		String customerid = jsonObject.getString("customerid");
		
		String trace = TraceHelp.uuid();
		ResultBean out=null;
		try {
			ParamsCheck.Customerid(customerid);
			ParamsCheck.requestCheck(new String[] {"lsh#String","cookies#String"}, jsonObject);
			out= loginservice.setCookies(customerid,trace, jsonObject);
			return out.toString();
		}catch (BusinessException e) {
			return ResultBean.ParamsFAIL( e.getMessage()).toString();
		}catch (Exception e) {
			log.error("setCookies发生异常",e);
			return ResultBean.FAIL(500).toString();
		}
	}
	
	
	@ResponseBody
	@PostMapping("/saveNeedLogin")
	@ApiOperation(value = "保存需要登陆的税号", notes = "保存需要登陆的税号")
	public String saveNeedLogin(@RequestBody String str) throws Exception{
		JSONObject jsonObject =JSONObject.parseObject(str);;
		
		String trace = TraceHelp.uuid();
		ResultBean out=null;
		try {
			ParamsCheck.requestCheck(new String[] {"lsh#String","customerid#String","nsrsbh#String",
					"password#String","nsrdq#String"}, jsonObject);
			out= loginservice.saveNeedLogin(trace, jsonObject);
			return out.toString();
		}catch (BusinessException e) {
			return ResultBean.ParamsFAIL(e.getMessage()).toString();
		}catch (Exception e) {
			log.error("getNeedLogin发生异常",e);
			return ResultBean.FAIL(500).toString();
		}
	}

}*/
