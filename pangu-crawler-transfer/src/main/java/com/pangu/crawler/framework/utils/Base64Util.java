package com.pangu.crawler.framework.utils;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

public class Base64Util {

	private static final String ENCODING = "UTF-8";

	public static String encode(String input) {
		try {
			input = new String (Base64.encodeBase64(input.getBytes(ENCODING)),ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			input = "";
		}
		return input;
	}
	public static String encode(byte[] input) {
		String ouput="";
		try {
			ouput = new String (Base64.encodeBase64(input),ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			ouput = "";
		}
		return ouput;
	}

	public static String decode(String input) {
		String ouput = "";
		try {
			byte[] decodeBase64 = Base64.decodeBase64(input.getBytes(ENCODING));
			ouput = new String(decodeBase64, ENCODING);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ouput;
	}
}
