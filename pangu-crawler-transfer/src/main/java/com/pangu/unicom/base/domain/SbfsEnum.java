package com.pangu.unicom.base.domain;

public enum SbfsEnum implements BaseEnum{
    // 电子税局网页申报
    ETAX_WEB("0", "电子税局网页申报"),
    // 电子税局客户端申报
    ETAX_CLIENT("1", "电子税局客户端申报"),
    // 税局大厅申报
    TAX_HALL("2", "税局大厅申报"),
    // 电子税局申报后税局大厅改报
    ETAX_AND_HALL("3", "电子税局申报后税局大厅改报");

    private String code;

    private String name;

    SbfsEnum(String code, String name) {
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
        return "SbfsEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
