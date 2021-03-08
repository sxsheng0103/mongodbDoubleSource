package com.pangu.unicom.base.domain;

public enum NullFormEnum implements BaseForm {
    ;

    @Override
    public String getShowName() {
        return null;
    }

    @Override
    public void setShowName(String showName) {

    }

    @Override
    public String getCode() {
        return null;
    }
}
