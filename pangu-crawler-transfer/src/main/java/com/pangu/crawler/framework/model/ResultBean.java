package com.pangu.crawler.framework.model;

import lombok.Data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

import org.springframework.util.Base64Utils;

@Data
public class ResultBean {

    private int code;

    private String body;
    
    /*
	 * 000 成功
	 * 100以上为失败
	 * 
	 */
	
	final static HashMap<Integer, String> errorCode=new HashMap<Integer, String>(){{
		
		put(1, "该lsh对应的查询正在排队中,请稍候在试");
		put(2, "该lsh对应的查询正在处理中,请稍候在试");
		put(3, "该lsh对应的查询正在处理中,请稍候在试");
		put(4, "该lsh对应的查询正在处理中,请稍候在试");
		put(5, "该lsh对应的查询正在处理中,请稍候在试");
		
		
		put(100, "登陆失败");
		put(101, "异步登陆时发生异步错误");  //在框架中被BusinessException抛出的异常替代
		put(102, "异步登陆超时");
		put(103, "获取cookies时发生异常");
		put(104, "没有获取到cookies");
		put(105, "没有获取到需要登陆的账号");
		put(106, "该lsh没有对应的登陆");
		
		put(200, "异步查询中发生异常");
		put(201, "异步查询中发生业务异常");//在框架中被BusinessException抛出的异常替代
		
		put(500, "系统异常");
		put(501, "请求参数错误");
		
		put(600, "该lsh查询不到请求");
		put(601, "异步查询调取结果失败");
		
		put(700, "没有查出相对应的数据");
		put(701, "该nsrsbh没有进行中的登陆");
		put(702, "处理异步登陆交互时发生异常");
		put(703, "处理失败");
		
		
	}};

	public static ResultBean  SUCCESS( String body) {
		return new ResultBean(0, body);
	}
	
	public static ResultBean  SUCCESS(JSONObject body) {
		return new ResultBean(0, body.toJSONString());
	}
	public static ResultBean  SUCCESS(JSON body) {
		return new ResultBean(0, body.toJSONString());
	}
	
	public static ResultBean  SUCCESS( JSONArray body) {
		return new ResultBean(0, body.toJSONString());
	}
	
	public static ResultBean FAIL(int code) {
		return new ResultBean(code, errorCode.get(code));
	}
	
	public static ResultBean FAIL(int code,String body) {
		return new ResultBean(code, body);
	}
	
	public static ResultBean ParamsFAIL(String message) {
		return new ResultBean(501, message);
	}

	public ResultBean(int code, String body) {
		super();
		this.code = code;
		this.body = body;
	}
	
	@Override
	public String toString() {
		JSONObject o=new JSONObject();
		o.put("body",Base64Utils.encodeToString(body.getBytes()) );
		o.put("code", code);
		return o.toJSONString();
	}
	
	
}
