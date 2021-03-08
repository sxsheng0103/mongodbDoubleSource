package com.pangu.unicom.base.domain;

public enum DfgxjjfljclfFormEnum implements BaseForm {

    dfgxjjfljclf("dfgxjjfljclf");

    private String code;

    private String showName;

    DfgxjjfljclfFormEnum(String code) {
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
