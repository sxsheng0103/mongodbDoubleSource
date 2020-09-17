/*
package com.pangu.crawler.business.service.login;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonArray;
import com.pangu.crawler.business.dao.mongoDB.entity.NeedLoginEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.NeedLoginOperation;
import com.pangu.crawler.framework.cookie.CookieKey;
import com.pangu.crawler.framework.cookie.CookieLoader;
import com.pangu.crawler.framework.model.ResultBean;
@Service
public class LoginService {
	@Autowired
	NeedLoginOperation needloginzoperation;
	
	@Autowired
    private MongoTemplate mongoTemplate;

	@Autowired
	CookieLoader cookieLoader;
	public ResultBean getNeedLogin(String customerid,String trace,JSONObject jsonObject) {
		NeedLoginEntity  n=needloginzoperation.findNoLogin(customerid, jsonObject.getString("nsrdq"));

		if(n!=null) {
			return ResultBean.SUCCESS( needloginzoperation.ObjToJson(n).toJSONString());
		}else {
			return ResultBean.FAIL(105);
		}
	}
	public ResultBean getNewNeedLoginByNsrsbh(String customerid,String trace,JSONObject jsonObject) {
		return ResultBean.SUCCESS(needloginzoperation.getByNsrsbh(jsonObject.getString("nsrsbh")).toJSONString());
	}
	
	public ResultBean setNeedLoginErrorInfo(String customerid,String trace,JSONObject jsonObject) {
		needloginzoperation.saveErrorInfo(jsonObject.getString("lsh"), jsonObject.getString("errorInfo"),jsonObject.getIntValue("state") );
		return ResultBean.SUCCESS("记录异常成功");
	}

	public ResultBean setCookies(String customerid,String trace,JSONObject jsonObject) throws Exception {
		//		boolean jg=needloginzoperation.LSHexistence(jsonObject.getString("lsh"));
		NeedLoginEntity data=needloginzoperation.findLSH(jsonObject.getString("lsh"));
		if(data!=null) {
			String cookiesStr=new String(Base64Utils.decodeFromString(jsonObject.getString("cookies")),"UTF-8");
			String[] cgld_cookies=cookiesStr.split(";");
			Map<CookieKey, List<String>> cookies = new HashMap<>();
			

			
			if(cgld_cookies!=null&&cgld_cookies.length>0) {
				for (int i = 0; i < cgld_cookies.length; i++) {
					String[] kv=cgld_cookies[i].split("=");
					CookieKey cookieKey = new CookieKey(kv[0], data.getNsrdq(), "/", "false");
					cookies.compute(cookieKey, (ck, list) -> {
						if (list == null) {
							list = new ArrayList<>();
							list.add(kv[1]);
							return list;
						} else {
							list.add(kv[1]);
							return list;
						}
					});
				}
				cookieLoader.saveCookies(trace, data.getNsrsbh(), cookies);
				needloginzoperation.updateState(jsonObject.getString("lsh"), 3);
				return ResultBean.SUCCESS( "保存完成");
			}else {
				return ResultBean.FAIL(104);
			}


		}else {
			return ResultBean.FAIL(106);
		}
	}
	
	public ResultBean saveNeedLogin(String trace,JSONObject jsonObject) {
		needloginzoperation.saveNeedLogin(jsonObject);
		return ResultBean.SUCCESS( "保存完成");
	}
	
	public ResultBean getCookies(String nsrsbh,String trace,String customerid,JSONObject jsonObject) {
		JSONObject data=null;
		try {
			data=getCookies(nsrsbh, trace);
		} catch (Exception e) {
		}
		if(data!=null) {
			if(data.isEmpty()) {
				return ResultBean.FAIL(104);
			}else {
				return ResultBean.SUCCESS(Base64Utils.encodeToString(data.toJSONString().getBytes()));
			}

		}else {
			return ResultBean.FAIL(103);
		}

	}
	
	public JSONObject getCookies(String nsrsbh,String trace) {
		Map<CookieKey, List<String>>  data=cookieLoader.loadCookies(trace, nsrsbh);
		
		JSONObject outdata=new JSONObject();
		
		for (CookieKey c : data.keySet()) {
			outdata.put(c.getKey(), data.get(c));
		}
		
		return outdata;
	}
	

}

*/
