package com.pangu.unicom.base.domain;

public enum FjsxgmFormEnum implements BaseForm {
    // 城市维护建设税
    cswhjss("cswhjss"),
    // 教育费附加
    jyffj("jyffj"),
    // 地方教育附加
    dfjyfj("dfjyfj"),
    // sljszxsy
    sljszxsy("sljszxsy"),
    // 合计
    hj("hj");
    private String code;

    private String showName;


    FjsxgmFormEnum(String code) {
        this.code = code;
    }

    @Override
    public String getShowName() {
        return this.showName;
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public void setShowName(String showName) {
        this.showName = showName;
    }

    @Override
    public String toString() {
        return "FjsxgmFormEnum{" +
                "code='" + code + '\'' +
                ", showName='" + showName + '\'' +
                '}';
    }

}
