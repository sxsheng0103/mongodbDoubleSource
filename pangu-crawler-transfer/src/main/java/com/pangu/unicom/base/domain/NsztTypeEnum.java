package com.pangu.unicom.base.domain;

public enum NsztTypeEnum implements BaseEnum {
    // 集团
    JT("jt", "集团"),
    // 集团附属
    JTFS("jtfs", "集团附属"),
    // 省分公司
    SF("sf", "省分公司"),
    // 地市公司
    DS("ds", "地市公司"),
    // 区县公司
    QX("qx", "区县公司");

    private String code;

    private String name;

    NsztTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "NsztTypeEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
