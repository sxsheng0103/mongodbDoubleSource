package com.pangu.unicom.base.domain;

public enum LoginCookieEnum {

    APPKEY("09db1bca25524beabc3198a1d33e13b9","我的授权-appKey"),
    SECRET("4696b0603b98f491b808e5aa","我的授权-安全密钥"),
    URL("http://180.168.162.219:8004/remote/callServer","请求地址");

    private String code;
    private String msg;

    LoginCookieEnum(String code,String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getCode(){
        return this.code;
    }

    public String getMsg(){
        return this.msg;
    }

}
