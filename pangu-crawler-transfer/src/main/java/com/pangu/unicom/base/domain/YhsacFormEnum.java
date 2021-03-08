package com.pangu.unicom.base.domain;

public enum YhsacFormEnum implements BaseForm {

    // 印花税纳税申报（报告）表
    yhsbgb("yhsbgb");

    private String code;

    private String showName;

    YhsacFormEnum(String code) {
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
        return "YhsacFormEnum{" +
                "code='" + code + '\'' +
                ", showName='" + showName + '\'' +
                '}';
    }
}
