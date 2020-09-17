package com.pangu.crawler.framework.Sm2Util;

public class SM2KeyPair {

    /** 公钥 */
    private  String publicKey;

    /** 私钥 */
    private String privateKey;

    SM2KeyPair(String publicKey, String privateKey) {
        this.publicKey = publicKey;
        this.privateKey = privateKey;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public String getPrivateKey() {
        return privateKey;
    }

}