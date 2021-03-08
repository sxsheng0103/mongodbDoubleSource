package com.pangu.unicom.base.domain;

public enum CwbbFormEnum implements BaseForm {
    // 利润表
    lrbData("lrbData"),
    // 现金流量表
    xjllData("xjllData"),
    // 资产负债表
    zcfzData("zcfzData"),
    // 所有者权益表
    syzqyData("syzqyData");

    private String code;

    private String showName;

    CwbbFormEnum(String code) {
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
        return "CwbbFormEnum{" +
                "code='" + code + '\'' +
                ", showName='" + showName + '\'' +
                '}';
    }
}

