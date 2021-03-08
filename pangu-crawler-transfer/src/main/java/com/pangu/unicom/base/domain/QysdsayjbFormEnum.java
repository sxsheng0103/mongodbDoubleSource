package com.pangu.unicom.base.domain;

public enum QysdsayjbFormEnum implements BaseForm {
    // 企业所得税月（季）报预缴纳税申报主表
    a200000Ywbd("a200000Ywbd"),
    // 免税收入、减计收入、所得减免等优惠明细表
    a201010Ywbd("a201010Ywbd"),
    // 固定资产加速折旧(扣除)优惠明细表
    a201020Ywbd("a201020Ywbd"),
    // 减免所得税优惠明细表
    a201030Ywbd("a201030Ywbd"),
    // 汇总纳税分支机构所得税分配表
    a202000Ywbd("a202000Ywbd"),
    // 居民企业参股外国企业信息报告表
    jmqycgwgqyxxbgb("jmqycgwgqyxxbgb"),
    // 技术成果投资入股企业所得税递延纳税备案表
    jscgtzrgqysdsdynsbab("jscgtzrgqysdsdynsbab");

    private String code;

    private String showName;

    QysdsayjbFormEnum(String code) {
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

