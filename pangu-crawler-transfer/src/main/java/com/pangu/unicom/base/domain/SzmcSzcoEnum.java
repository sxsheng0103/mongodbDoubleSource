package com.pangu.unicom.base.domain;

public enum SzmcSzcoEnum {

    zzs("增值税一般纳税人","zzsybnsr"),
    fjs("附加税","fjs"),
    yhs("印花税","yhs"),
    cwbb("企业会计准则","cwbbqykjzz"),
    tysb("通用申报","tysb"),
    cbj("残疾人就业保障金缴费申报","cjrjybzj"),
    whsyjsf("文化事业建设费申报","whsyjsf"),
    sds("企业所得税","qysds");

    private String name;

    private String code;

    SzmcSzcoEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
