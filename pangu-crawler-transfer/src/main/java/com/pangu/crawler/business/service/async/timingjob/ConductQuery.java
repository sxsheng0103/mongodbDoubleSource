/*
package com.pangu.crawler.business.service.async.timingjob;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.controller.DelareRelevantControllers;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryRegisterRequestEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryErrorOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryFaPiaoXinXiOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryHistoricalDataInfoOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryJiaoKuanXinXiOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryRegisterRequestOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryUserInfoOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.CurrentMonthDataOperation;
import com.pangu.crawler.business.service.async.AsyncConfig;
import com.pangu.crawler.business.service.async.BusinessException;
import com.pangu.crawler.business.service.async.help.AsyncQueryServiceHelper;
import com.pangu.crawler.framework.utils.AppUtil;
import com.pangu.crawler.framework.utils.TraceHelp;

import aj.org.objectweb.asm.Type;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import java.util.ArrayList;
import java.util.Base64;

//@Component
@Slf4j
public class ConductQuery {
    @Autowired
    AsyncQueryRegisterRequestOperation asyncqueryregisterrequestoperation;
    @Autowired
    AsyncQueryServiceHelper asyncqueryservicehelper;
    @Autowired
    DelareRelevantControllers delarerelevantcontrollers;

    @Autowired
    AsyncQueryHistoricalDataInfoOperation asyncqueryhistoricaldatainfooperation;

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

    @Scheduled(fixedRate = 5000)
    public void ConductQuery() {
        String lsh = "";
        try {
            AsyncQueryRegisterRequestEntity data = asyncqueryregisterrequestoperation.getOneByState(4);
            if (data == null) {
                return;
            }
            lsh = data.getLsh();
            data.setState(5);
            asyncqueryregisterrequestoperation.save(data);
            JSONObject json = asyncqueryservicehelper.entity2JSONObject(data);

            String nsrdq = json.getString("nsrdq");
            String trace = TraceHelp.uuid();
            String nsrsbh = json.getString("nsrsbh");
            String sign = json.getString("sign");

            try {
            	JSON returnJson = delarerelevantcontrollers.ordinaryCircumstances(sign, nsrdq, trace, nsrsbh, json);
            	
//            	JSON transformOver = appUtil.getAsyncQueryInterface("AsyncQuery_" + nsrdq).queryResultTransform(nsrsbh, sign, json, body);
            	
            	if(sign.equals("queryTaxsDetermine")){
            		JSONObject o=new JSONObject();
        			o.put("customerid", json.getString("customerid"));
        			o.put("nsrsbh", nsrsbh);
        			o.put("sfzrdxx", returnJson.toJSONString());
            		asyncqueryuserinfooperation.saveOrUpdate(o);
            	}else if(sign.equals("queryRegister")){
            		JSONObject o=new JSONObject();
        			o.put("customerid", json.getString("customerid"));
        			o.put("nsrsbh", nsrsbh);
        			o.put("djxx",  returnJson.toJSONString());
            		asyncqueryuserinfooperation.saveOrUpdate(o);
            	}else if (AsyncConfig.CurrentMonthDataSign.contains(sign)) {
            		currentmonthdataoperation.save(lsh, returnJson);
            	}else if (sign.equals("queryHistoricalData")||sign.equals("queryHistoricalDataList")) {
            		setInfo((JSONArray)returnJson, nsrsbh, json.getString("customerid"));
            		for (int i = 0; i < ((JSONArray)returnJson).size(); i++) {
            			JSONObject o=((JSONArray)returnJson).getJSONObject(i);
            			o.put("nsrdq", nsrdq);
            			if(o.containsKey("json")) {
            				o.put("json", Base64Utils.encodeToString(o.getString("json").getBytes()));
            			}
            			if(o.containsKey("html")) {
            				o.put("html", Base64Utils.encodeToString(o.getString("html").getBytes()));
            			}
            			if(o.containsKey("pdf")) {
            				o.put("pdf", Base64Utils.encodeToString(o.getString("pdf").getBytes()));
            			}
            			
            		}
            		asyncqueryhistoricaldatainfooperation.saveHistoricalDataList((JSONArray) returnJson);
            	}else if (sign.equals("queryFapiaoInfo")) {
            		setInfo((JSONArray)returnJson, nsrsbh, json.getString("customerid"));
            		asyncqueryfapiaoxinxioperation.save((JSONArray)returnJson);
            	}else if (sign.equals("queryJiaoKuanXinXi")) {
            		setInfo((JSONArray)returnJson, nsrsbh, json.getString("customerid"));
            		asyncqueryjiaokuanxinxioperation.save((JSONArray)returnJson);
            	}
            	
            	data.setState(6);
            	asyncqueryregisterrequestoperation.save(data);
            	return;

            } catch (BusinessException e) {
                data.setState(201);
                data.setErrorInfo(e.getMessage());
                asyncqueryregisterrequestoperation.save(data);
            } catch (Exception e) {
                log.error("查询发生异常:", e);
                data.setState(200);
                asyncqueryregisterrequestoperation.save(data);
                AsyncQueryErrorOperation.saveInfo(lsh,"ConductQuery_1", null, e);
            }
        } catch (Exception e) {
            log.error("查询发生未知异常:", e);
            AsyncQueryErrorOperation.saveInfo(lsh,"ConductQuery_2", null, e);
        }
    }
    public void setInfo(JSONArray returnJson,String nsrsbh,String customerid) {
    	for (int i = 0; i < returnJson.size(); i++) {
    		JSONObject o=returnJson.getJSONObject(i);
    		o.put("nsrsbh", nsrsbh);
    		o.put("customerid", customerid);
		}
    }
    
//    public JSONObject objectToJson(JSONArray returnJson,Object type) {
//    	JSONArray outData=new JSONArray();
//    	for (int i = 0; i < returnJson.size(); i++) {
//    		Object o=returnJson.get(i);
//    		if(o instanceof JSONObject) {
//    			js
//    			outData.add(o);
//    		}else {
//    			outData.add(o);
//			}
//    		
//		}
//    	
//    	return outData;
//    }
}*/
