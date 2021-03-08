package com.pangu.unicom.base.domain;

public enum QtxfsFormEnum implements BaseForm {
    // 其他应税消费品消费税纳税申报表
    qtysxfpxfsnssbb("qtysxfpxfsnssbb"),
    // 附表1:本期准予扣除税额计算表（其他）
    fb1bqzykcsejsb("fb1bqzykcsejsb"),
    // 附表2:准予扣除消费税凭证明细表（其他）
    fb2zykcxfspzmxb("fb2zykcxfspzmxb"),
    // 附表3:本期代收代缴税额计算表（其他）
    fb3bqdsdjsejsb("fb3bqdsdjsejsb"),
    // 附表4:生产经营情况表（其他）
    fb4scjyqkb("fb4scjyqkb"),
    // 本期减（免）税额明细表
    bqjmsemxb("bqjmsemxb"),
    // 消费税补充申报表
    xfsbcsbb("xfsbcsbb");

    private String code;

    private String showName;

    QtxfsFormEnum(String code) {
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
        return "QtxfsFormEnum{" +
                "code='" + code + '\'' +
                ", showName='" + showName + '\'' +
                '}';
    }
}
