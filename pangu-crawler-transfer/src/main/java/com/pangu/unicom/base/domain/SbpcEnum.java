package com.pangu.unicom.base.domain;

public enum SbpcEnum implements BaseEnum {
    // 月报
    MONTH("month", "月报"),
    // 季报
    QUARTER("quarter", "季报"),
    // 半年报
    HALF_YEAR("halfYear", "半年报"),
    // 年报
    YEAR("year", "年报"),
    // 按次
    TIMES("times", "按次"),
    // 不定期
    UNSCHEDULE("unschedule", "不定期");

    private String code;

    private String name;

    SbpcEnum(String code, String name) {
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
        return "SbpcEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
