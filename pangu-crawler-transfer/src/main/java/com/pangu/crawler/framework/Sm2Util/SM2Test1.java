package com.pangu.crawler.framework.Sm2Util;
import java.io.IOException;

public class SM2Test1 {
    public static void main(String[] args) throws IOException {

        // 获取公钥私钥对
        SM2KeyPair sm2KeyPair = SM2Utils.generateKeyPair();

        // 获取公钥和私钥
        String publickey=sm2KeyPair.getPublicKey();
        String privatekey=sm2KeyPair.getPrivateKey();

        // 需要加密的信息
        String info="哈哈哈，嘿嘿嘿";
        // 将信息加密，公钥的字节码需要使用util里的hexToByte方法
        String encrypt = SM2Utils.encrypt(Util.hexToByte(publickey), info.getBytes("utf-8"));

        System.out.println("加密后信息："+encrypt);

        // 将加密后的信息解密，私钥和加密后的数据的字节码对象需要用util里的hexToByte方法
        byte[] decrypt = SM2Utils.decrypt(Util.hexToByte(privatekey), Util.hexToByte(encrypt));
        // 将字节数组转为字符串
        String newInfo=new String(decrypt,"utf-8");
        System.out.println("解密后信息:"+newInfo);


    }

}