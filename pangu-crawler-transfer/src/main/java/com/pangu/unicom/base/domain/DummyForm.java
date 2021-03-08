package com.pangu.unicom.base.domain;

public class DummyForm implements BaseForm {
    @Override
    public String getCode() {
        throw new IllegalStateException("not support!");
    }

    @Override
    public String getShowName() {
        throw new IllegalStateException("not support!");
    }

    @Override
    public void setShowName(String showName) {
        throw new IllegalStateException("not support!");
    }
}
