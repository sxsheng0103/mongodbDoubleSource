package com.pangu.unicom.base.domain;

public enum JkfsEnum implements BaseEnum {
    // 自行手动缴款
    SDJK("0", "自行手动缴款"),
    // 自动扣款（三方协议）
    ZDJK_SFXY("1", "自动扣款（三方协议）"),
    // 预约扣款（三方协议）
    YYJK_SFXY("2", "预约扣款（三方协议）");

    private String code;

    private String name;

    JkfsEnum(String code, String name) {
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
        return "JkfsEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
