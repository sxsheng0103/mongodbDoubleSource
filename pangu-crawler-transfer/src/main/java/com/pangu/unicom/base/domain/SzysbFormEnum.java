package com.pangu.unicom.base.domain;

public enum SzysbFormEnum implements BaseForm {
    
	zb("zb"),
	fb("fb");


    private String code;

    private String showName;

    SzysbFormEnum(String code) {
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
        return "QysdsayjbFormEnum{" +
                "code='" + code + '\'' +
                ", showName='" + showName + '\'' +
                '}';
    }
}

