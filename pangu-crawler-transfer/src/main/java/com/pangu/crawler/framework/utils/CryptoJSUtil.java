package com.pangu.crawler.framework.utils;

import java.io.File;
import java.io.FileReader;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.apache.commons.io.FileUtils;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import com.alibaba.fastjson.JSONObject;
import com.gargoylesoftware.htmlunit.javascript.host.crypto.Crypto;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class CryptoJSUtil implements AutoCloseable{
	Context cx;
	Scriptable scope;
	
	public CryptoJSUtil() throws Exception {
		cx = Context.enter();
		scope = cx.initStandardObjects();
		try{
			cx.evaluateString(scope, FileUtils.readFileToString(ResourceUtils.getFile("classpath:js/chongqing/crypto.js")), null, 1, null);
		} catch (Exception e) {
			log.error("CryptoJSUtil发生异常:",e);
			throw new Exception(e.getMessage());
		}
	}
	public String encryptPassword(String password,String lol) throws Exception {
		Object o=cx.evaluateString(scope,"CryptoJS.AES.encrypt(CryptoJS.enc.Utf8.parse('Aa123456'), CryptoJS.enc.Utf8.parse('4488f9c77ff5bc54'),{mode: CryptoJS.mode.ECB,padding: CryptoJS.pad.Pkcs7}).toString()", "aaaabbbb", 1, null);
		return (String) o;
	}
	@Override
	public void close() throws Exception {
		cx.exit();
	}

//	public String encryptPassword(String password,String lol) throws Exception {
//				 String k=  execute("CryptoJS.enc.Utf8.parse",new String[] {password});
//				 String v=execute("CryptoJS.enc.Utf8.parse",new String[] {lol});
//				 JSONObject j=new JSONObject();
//				 j.put("mode", "CryptoJS.mode.ECB");
//				 j.put("padding", "CryptoJS.pad.Pkcs7");
//				 String data=execute("CryptoJS.AES.encrypt",new Object[] {k,v,j.toJSONString()});
//				return data;
//		//		return  (String) nashorn.eval("CryptoJS.AES.encrypt(('"+password+"'), CryptoJS.enc.Utf8.parse('"+lol+"'))");
//	}
//
//	public String execute(String methodName,Object[] args) throws Exception {
//		
//
//		Scriptable cj=null;
//		for (String s : methodName.split("\\.")) {
//			if(cj==null) {
//				cj=(Scriptable) scope.get("CryptoJS", scope);
//			}else {
//				cj=(Scriptable) cj.get(s, cj);
//			}
//			
//		}
//		
////		Object encodeObj = ((Scriptable) ((Scriptable) ((Scriptable) scope.get("CryptoJS", scope)).get("enc", scope)).get("Utf8", scope)).get("parse", scope);
//		
//		Function encodeFun = (Function)cj;
//		Object encodeResult = encodeFun.call(cx, scope, scope, args);
//		return Context.toString(encodeResult);
//	}

}
