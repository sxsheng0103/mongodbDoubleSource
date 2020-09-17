package com.pangu.crawler.business.dao.mongoDB;

import org.springframework.util.Base64Utils;

public class PasswordEncryption {
	private static String KEY="D4987738C5068D423BAB459D44057BB2";
	
	public static String encrypt(String password) throws Exception {
//		return Aes.aesEncrypt(password,KEY);
		return Base64Utils.encodeToString(password.getBytes());
	}
	
	public static String decrypt(String password) throws Exception {
//		return Aes.aesDecrypt(password,KEY);
		return new String(Base64Utils.decodeFromString(password),"UTF-8");
	}
	
//	public static void main(String[] args) throws Exception {
//		System.out.println(PasswordEncryption.encrypt("ssssss"));
//		System.out.println(PasswordEncryption.decrypt("c3Nzc3Nz"));
//	}

}
