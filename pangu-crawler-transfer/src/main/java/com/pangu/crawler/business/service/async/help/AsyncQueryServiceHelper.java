/*
package com.pangu.crawler.business.service.async.help;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.PasswordEncryption;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryRegisterRequestEntity;
import com.pangu.crawler.business.service.async.BusinessException;
import com.pangu.crawler.framework.cookie.CookieKey;
import com.pangu.crawler.framework.cookie.CookieLoader;

@Service
public class AsyncQueryServiceHelper {
	
	ArrayList<String> loginMethodList=new ArrayList<String>(){{add("dzzz"); add("password");}};
	
	public AsyncQueryRegisterRequestEntity assembleAsyncQueryRegisterRequest(String lsh,JSONObject jsonObject) throws Exception {
		AsyncQueryRegisterRequestEntity original=new AsyncQueryRegisterRequestEntity();
		
		original.setNsrsbh(jsonObject.getString("nsrsbh"));
		original.setSign(jsonObject.getString("sign"));
		original.setNsrdq(jsonObject.getString("nsrdq"));
		original.setCustomerid(jsonObject.getString("customerid"));
		if(StringUtils.isNotBlank(jsonObject.getString("loginMethod"))) {
			if(loginMethodList.contains(jsonObject.getString("loginMethod"))) {
				original.setLogin_method(jsonObject.getString("loginMethod"));
			}else {
				throw new BusinessException("未知的登陆方式");
			}
			
		}else {
			original.setLogin_method("password");
		}
		original.setLsh(lsh);
		if(StringUtils.isNotBlank(jsonObject.getString("password"))) {
			original.setPassword(PasswordEncryption.encrypt(jsonObject.getString("password")));
		}
		
		HashMap<String, String> requestParam=new HashMap<String, String>();
		for (String k : jsonObject.keySet()) {
			if(
					!k.equals("sign")&&
					!k.equals("selectid")&&
					!k.equals("nsrsbh")&&
					!k.equals("nsrdq")&&
					!k.equals("password")&&
					!k.equals("isUpdate")&&
					!k.equals("loginMethod")&&
					!k.equals("customerid")
					) {
				requestParam.put(k, jsonObject.getString(k));
			}
		}
//		if(requestParam.size()>0) {
		original.setRequestParam(requestParam);
//		}
		
		original.setState(1);
		
		return original;
	}
	
	public static JSONObject entity2JSONObject(AsyncQueryRegisterRequestEntity asyncqueryregisterrequestentity) throws Exception {
		JSONObject outData=new JSONObject();
		outData.putAll(asyncqueryregisterrequestentity.getRequestParam());
		if(asyncqueryregisterrequestentity.getPassword()!=null&&!asyncqueryregisterrequestentity.getPassword().isEmpty()) {
			outData.put("password", PasswordEncryption.decrypt(asyncqueryregisterrequestentity.getPassword()));
		}
		outData.put("nsrsbh", asyncqueryregisterrequestentity.getNsrsbh());
		outData.put("sign", asyncqueryregisterrequestentity.getSign());
		outData.put("nsrdq", asyncqueryregisterrequestentity.getNsrdq());
		outData.put("customerid", asyncqueryregisterrequestentity.getCustomerid());
		return outData;
		
	}
	
	
	
	

}
*/
