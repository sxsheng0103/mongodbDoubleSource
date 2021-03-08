package com.pangu.unicom.base.domain;

public enum FjsFormItemEnum implements BaseForm {

    // 城市维护建设税
    cswhjss("cswhjss"),

    // 教育费附加
    jyffj("jyffj"),

    // 地方教育附加
    dfjyfj("dfjyfj"),

    // 水利建设专项收入
    sljszxsy("sljszxsy"),

    // 城市维护建设税消费税附加
    cswhjssxfsfj("cswhjssxfsfj"),

    // 教育费附加消费税附加
    jyffjxfsfj("jyffjxfsfj"),

    // 地方教育附加消费税附加
    dfjyfjxfsfj("dfjyfjxfsfj"),

    // 水利建设专项收入消费税附加
    sljszxsyxfsfj("sljszxsyxfsfj"),

    // 合计行
    hj("hj");


    private String code;

    private String showName;

    FjsFormItemEnum(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getShowName() {
        return this.showName;
    }

    @Override
    public void setShowName(String showName) {
        this.showName = showName;
    }

    @Override
    public String toString() {
        return "FjsFormItemEnum{" +
                "code='" + code + '\'' +
                ", showName='" + showName + '\'' +
                '}';
    }
}
