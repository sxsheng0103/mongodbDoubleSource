package com.pangu.unicom.base.domain;

/**
 * @Author wangjx
 * @Date 2020/4/3 17:24
 * @Version 1.0
 */
public enum ZzsxgmhdzsFormEnum implements BaseForm {
    // 增值税适用于小规模纳税人
    zzssyyxgmnsr("zzssyyxgmnsr"),
    // 增值税纳税申报表（适用于增值税小规模纳税人）附列资料
    zzsxgmflzl("zzsxgmflzl"),
    // 增值税减免税申报明细表
    zzsjmssbmxb("zzsjmssbmxb"),
    // 销售不动产情况表
    zzsxgmxsbdcqkb("zzsxgmxsbdcqkb"),
    // 应税服务扣除项目清单
    zzsxgmkcxmqd("zzsxgmkcxmqd");

    private String code;

    private String showName;
    ZzsxgmhdzsFormEnum(String code) {
        this.code = code;
    }

    @Override
    public String getShowName() {
        return this.showName;
    }

    @Override
    public String getCode() {
        return this.code;
    }
    @Override
    public void setShowName(String showName) {
        this.showName = showName;
    }

    @Override
    public String toString() {
        return "ZzsxgmFormEnum{" +
                "code='" + code + '\'' +
                ", showName='" + showName + '\'' +
                '}';
    }
}
