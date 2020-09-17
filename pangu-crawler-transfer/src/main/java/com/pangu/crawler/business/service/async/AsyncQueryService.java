/*
package com.pangu.crawler.business.service.async;

import java.util.HashMap;
import java.util.UUID;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.bsd.RLoginClient;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryRegisterRequestEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryFaPiaoXinXiOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryHistoricalDataInfoOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryJiaoKuanXinXiOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryRegisterRequestOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryUserInfoOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.CurrentMonthDataOperation;
import com.pangu.crawler.business.service.async.help.AsyncQueryServiceHelper;
import com.pangu.crawler.business.service.async.timingjob.AnyncLogin;
import com.pangu.crawler.framework.model.AreaCode;
import com.pangu.crawler.framework.model.ResultBean;
import com.pangu.crawler.framework.selenium.SeleniumUtil;
import com.pangu.crawler.framework.utils.AppUtil;
import com.pangu.crawler.framework.utils.TraceHelp;

import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
public class AsyncQueryService {
	@Autowired
	AsyncQueryServiceHelper asyncqueryservicehelper;
	@Autowired
	AsyncQueryRegisterRequestOperation asyncqueryregisterrequestoperation;
	@Autowired
	AsyncQueryHistoricalDataInfoOperation  asyncqueryhistoricaldatainfooperation;
	@Autowired
	AsyncQueryUserInfoOperation asyncqueryuserinfooperation;
	@Autowired
	AsyncQueryFaPiaoXinXiOperation asyncqueryfapiaoxinxioperation;
	@Autowired
	CurrentMonthDataOperation currentmonthdataoperation;

	@Autowired
	AsyncQueryJiaoKuanXinXiOperation asyncqueryjiaokuanxinxioperation;


	@Autowired
	private AppUtil appUtil;


	public ResultBean register(String nsrsbh,String trace,String nsrdq,String sign,JSONObject jsonObject)throws Exception {
		switch (sign) {
		case "queryHistoricalData":
			if(ArrayUtils.contains(new String[] {"anhui","shandong"}, nsrdq)) {
				ParamsCheck.requestCheck(new String[] {"sbrqq#String","sbrqz#String"}, jsonObject);
				ParamsCheck.dateCheck(new String[] {"sbrqq#yyyy-MM-dd","sbrqz#yyyy-MM-dd"}, jsonObject);
			}else {
				ParamsCheck.requestCheck(new String[] {"skssqq#String","skssqz#String"}, jsonObject);
				ParamsCheck.dateCheck(new String[] {"skssqq#yyyy-MM-dd","skssqz#yyyy-MM-dd"}, jsonObject);
			}

			break;
		case "queryHistoricalDataList":
			if(ArrayUtils.contains(new String[] {"anhui","shandong"}, nsrdq)) {
				ParamsCheck.requestCheck(new String[] {"sbrqq#String","sbrqz#String"}, jsonObject);
				ParamsCheck.dateCheck(new String[] {"sbrqq#yyyy-MM-dd","sbrqz#yyyy-MM-dd"}, jsonObject);
			}else {
				ParamsCheck.requestCheck(new String[] {"skssqq#String","skssqz#String"}, jsonObject);
				ParamsCheck.dateCheck(new String[] {"skssqq#yyyy-MM-dd","skssqz#yyyy-MM-dd"}, jsonObject);
			}
			break;
		case "queryFapiaoInfo":
			ParamsCheck.requestCheck(new String[] {"kprqq#String","kprqz#String","fplx#String"}, jsonObject);
			ParamsCheck.dateCheck(new String[] {"kprqq#yyyy-MM-dd","kprqz#yyyy-MM-dd"}, jsonObject);
			break;
		case "queryJiaoKuanXinXi":
			ParamsCheck.requestCheck(new String[] {"jkfqrq#String","jkfqrz#String"}, jsonObject);
			ParamsCheck.dateCheck(new String[] {"jkfqrq#yyyy-MM-dd","jkfqrz#yyyy-MM-dd"}, jsonObject);
			break;
		case "queryTemporaryHistoricalData":
			ParamsCheck.requestCheck(new String[] {"sbyf#String"}, jsonObject);
			ParamsCheck.dateCheck(new String[] {"sbyf#yyyyMMdd"}, jsonObject);
			break;
		default:
			break;
		}

		String lsh=UUID.randomUUID().toString().replaceAll("-","");
		AsyncQueryRegisterRequestEntity original= asyncqueryservicehelper.assembleAsyncQueryRegisterRequest(lsh,jsonObject);
		asyncqueryregisterrequestoperation.save(original);

		return ResultBean.SUCCESS(lsh);
	}

	public ResultBean returnResult(String lsh,String trace,JSONObject jsonObject) throws Exception {
		AsyncQueryRegisterRequestEntity  request=asyncqueryregisterrequestoperation.findByLsh(lsh);

		if(request!=null) {
			String nsrsbh=request.getNsrsbh();
			if(request.getState()<6) {
				return ResultBean.FAIL(request.getState());
			}else if (request.getState()==6) {
				HashMap<String, String>  requestParam=request.getRequestParam();
				if(request.getSign().equals("queryRegister")) {
					return ResultBean.SUCCESS(
							new String(Base64Utils.decodeFromString(
									asyncqueryuserinfooperation.findAsyncQueryUserInfo(request.getNsrsbh(), request.getCustomerid()).getDjxx()
									))
							);
				}else if (request.getSign().equals("queryTaxsDetermine")) {
					return ResultBean.SUCCESS(
							new String(Base64Utils.decodeFromString(
									asyncqueryuserinfooperation.findAsyncQueryUserInfo(request.getNsrsbh(), request.getCustomerid()).getSfzrdxx()
									))
							);
				}else if (AsyncConfig.CurrentMonthDataSign.contains(request.getSign())) {
					return ResultBean.SUCCESS(
							new String(Base64Utils.decodeFromString(
									currentmonthdataoperation.findByLSH(request.getLsh()).getData()
									))
							);
				}else if (request.getSign().equals("queryFapiaoInfo")) {
					Criteria cxyj=null;
					if(StringUtils.isNotBlank(requestParam.get("cxlx"))&&requestParam.get("cxlx").equals("0")) {
						cxyj=asyncqueryfapiaoxinxioperation.checkAndAssemble(nsrsbh, null,
								requestParam.get("fplx"), requestParam.get("kprqq"), requestParam.get("kprqz"));
					}else {
						cxyj=asyncqueryfapiaoxinxioperation.checkAndAssemble(null,nsrsbh, 
								requestParam.get("fplx"), requestParam.get("kprqq"), requestParam.get("kprqz"));
						
					}

					JSONArray  data=asyncqueryfapiaoxinxioperation.find(cxyj);
					if(data!=null&&data.size()>0) {
						return ResultBean.SUCCESS(data.toJSONString());
					}else {
						return ResultBean.FAIL(700);
					}

				}else if (request.getSign().equals("queryJiaoKuanXinXi")) {
					Criteria cxyj=asyncqueryjiaokuanxinxioperation.checkAndAssemble(nsrsbh,requestParam.get("jkfqrq") , requestParam.get("jkfqrz"));
					JSONArray  data=asyncqueryjiaokuanxinxioperation.findList(cxyj);
					if(data!=null&&data.size()>0) {
						return ResultBean.SUCCESS(data.toJSONString());
					}else {
						return ResultBean.FAIL(700);
					}

				}else if (request.getSign().equals("queryHistoricalData")||request.getSign().equals("queryHistoricalDataList")) {
					Criteria cxyj=asyncqueryhistoricaldatainfooperation.checkAndAssemble(requestParam.get("skssqq"), requestParam.get("skssqz"), requestParam.get("sz"), 
							nsrsbh, request.getNsrdq(), requestParam.get("sbfs"), requestParam.get("bbmc"), requestParam.get("state"), 
							requestParam.get("sbrqq"),requestParam.get("sbrqz"),requestParam.get("xgrq") );
					JSONArray  data=asyncqueryhistoricaldatainfooperation.findHistoricalDataList(cxyj);
					if (request.getSign().equals("queryHistoricalDataList")) {
						for (int i = 0; i < data.size(); i++) {
							JSONObject o=data.getJSONObject(i);
							o.remove("json");
							o.remove("html");
							o.remove("pdf");
						}
					}
					if(data!=null&&data.size()>0) {
						return ResultBean.SUCCESS(data.toJSONString());
					}else {
						return ResultBean.FAIL(700);
					}
				}
				return ResultBean.FAIL(601);
			}else {
				if(request.getErrorInfo()!=null&&!request.getErrorInfo().isEmpty()) {
					return new ResultBean(request.getState(),request.getErrorInfo());
				}else {
					return ResultBean.FAIL(request.getState());
				}

			}
		}else {
			return ResultBean.FAIL(600);
		}

	}

	public ResultBean asyncLoginInteraction(String trace,JSONObject jsonObject) throws Exception {
		String lsh=jsonObject.getString("lsh");
		AsyncQueryRegisterRequestEntity  request=asyncqueryregisterrequestoperation.findByLsh(lsh);
		if(request!=null) {
			String nsrsbh=request.getNsrsbh();
			if(AsyncConfig.DZZZDLMap.containsKey(nsrsbh)) {
				try {
					ResultBean  rb=appUtil.getAsyncQueryInterface("AsyncQuery_"+request.getNsrdq()).asyncLoginInteraction(nsrsbh,trace,AsyncConfig.DZZZDLMap.get(nsrsbh), jsonObject);
					return rb;
				} catch (Exception e) {
					log.error("处理异步登陆交互时发生异常:",e);
					return ResultBean.FAIL(702); 
				}
			}else {
				return ResultBean.FAIL(701); 
			}
		}else {
			return ResultBean.FAIL(600);
		}

	}





	public ResultBean getAsyncQueryRegisterRequestList(String trace,JSONObject jsonObject) throws Exception {
		Criteria cxyj=asyncqueryregisterrequestoperation.checkAndAssemble(jsonObject);
		JSONArray data=asyncqueryregisterrequestoperation.find(cxyj);
		if(data==null||data.isEmpty()) {
			return ResultBean.FAIL(103);
		}else {
			return ResultBean.SUCCESS(data.toJSONString());
		}
	}



}
*/
