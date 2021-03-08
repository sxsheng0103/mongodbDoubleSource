package com.pangu.unicom.base.domain;

public enum WhsyjsfFormEnum implements BaseForm {
    // 文化事业建设费申报主表
    whsyjsfzb("whsyjsfzb"),
    // 文化事业建设费应税服务(扣除/减除)项目清单
    ysfwkcxmqd("ysfwkcxmqd");

    private String code;

    private String showName;

    WhsyjsfFormEnum(String code) {
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
        return "WhsyjsfFormEnum{" +
                "code='" + code + '\'' +
                ", showName='" + showName + '\'' +
                '}';
    }
}
