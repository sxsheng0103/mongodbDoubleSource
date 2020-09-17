package com.pangu.crawler.framework.model;

public enum JsonObjectKey {

    SKSSQQ("skssqq","税款所属期起"),
    SKSSQZ("skssqz","税款所属期止"),
    SZ("sz","税种"),
    SIGN("sign","签名信息"),
    NSRDQ("nsrdq","纳税人地区"),
    NSRSBH("nsrsbh","纳税人识别号"),
    USER_NAME("username","登录用户名"),
    PASSWORD("password","登录密码"),
    PHONE("phone","登录电话号码"),
    SWJG("swjg","税务机关名称"),


    FPLB("fplb","发票类型"),
    KPRQQ("kprqq","开票日期起"),
    KPRQZ("kprqz","开票日期止");

    private String code;
    private String comment;
    JsonObjectKey(String code, String comment) {
        this.code = code;
        this.comment = comment;
    }

    public String getCode(){
        return this.code;
    }

    public String getComment(){
        return this.comment;
    }
}
