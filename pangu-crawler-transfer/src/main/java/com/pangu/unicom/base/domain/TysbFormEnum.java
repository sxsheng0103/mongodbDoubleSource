package com.pangu.unicom.base.domain;

public enum TysbFormEnum implements BaseForm {
    // 通用申报表
    tysbb("tysbb");

    private String code;

    private String showName;

    TysbFormEnum(String code) {
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
        return "TysbFormEnum{" +
                "code='" + code + '\'' +
                ", showName='" + showName + '\'' +
                '}';
    }
}
