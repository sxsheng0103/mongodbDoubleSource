package com.pangu.unicom.base.domain;

/**
 * 残疾人就业保障金
 */
public enum CbjFormEnum implements BaseForm {

    // 残疾人就业保障金申报表
    cjrjybzjsbb("cjrjybzjsbb");

    private String code;

    private String showName;

    CbjFormEnum(String code) {
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
        return "CbjFormEnum{" +
                "code='" + code + '\'' +
                ", showName='" + showName + '\'' +
                '}';
    }
}
