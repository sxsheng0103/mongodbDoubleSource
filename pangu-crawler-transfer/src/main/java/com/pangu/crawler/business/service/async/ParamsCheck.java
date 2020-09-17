package com.pangu.crawler.business.service.async;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;

public class ParamsCheck {
	
	static SimpleDateFormat yyyy_MM_dd=new SimpleDateFormat("yyyy-MM-dd");
	
	static SimpleDateFormat yyyy_MM_dd_HH_mm_ss=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	static Pattern yyyyMMdd=Pattern.compile("^[0-9]{6}$");
	
	public static void requestCheck(String[] mustFill,JSONObject jsonObject) throws Exception {
		for (String string : mustFill) {
			String name=string.split("#")[0];
			String type=string.split("#")[1];
			if(!jsonObject.containsKey(name)) {
				throw new BusinessException("请求参数:"+name+"没找到");
			}
			
			try {
				if(type.equals("int")) {
					if(jsonObject.getInteger(name)==null) {
						throw new BusinessException("请求参数:"+name+"不能为空");
					}
				}else if (type.equals("String")) {
					if(StringUtils.isBlank(jsonObject.getString(name))||jsonObject.getString(name).equals("null")) {
						throw new BusinessException("请求参数:"+name+"不能为空");
					}
				}else if (type.equals("boolean")) {
					if(jsonObject.getBoolean(name)==null) {
						throw new BusinessException("请求参数:"+name+"不能为空");
					}
				}
			} catch (Exception e) {
				if(e.getMessage().startsWith("请求参数")) {
					throw new BusinessException(e.getMessage());
				}
				throw new BusinessException("请求参数:"+name+"格式错误应该为"+type);
			}
		}
		
	}
	
	public static void dateCheck(String[] mustFill,JSONObject jsonObject) throws Exception {
	
		for (String string : mustFill) {
			String name=string.split("#")[0];
			String type=string.split("#")[1];
			
			String strdate=jsonObject.getString(name);
			try {
				if(type.equals("yyyy-MM-dd")) {
					yyyy_MM_dd.parse(strdate).getTime();
					
				}else if(type.equals("yyyy-MM-dd HH:mm:ss")) {
					yyyy_MM_dd_HH_mm_ss.parse(strdate).getTime();
				}else if(type.equals("yyyyMMdd")) {
					if(!yyyyMMdd.matcher(strdate).matches()) {
						throw new Exception();
					}
				}
			} catch (Exception e) {
				throw new BusinessException("请求参数:"+name+"格式错误应该为"+type);
			}
		}
	}
	
	public static void Customerid(String customerid) throws Exception {
		if(StringUtils.isBlank(customerid)) {
			throw new BusinessException("customerid 为空");
		}
	}
	
//	public static void main(String[] args) throws Exception {
//		JSONObject a=new JSONObject();
//		a.put("aaa",null);
//		ParamsCheck.requestCheck(new String[] {"aaa#int"}, a);
//	}

}
