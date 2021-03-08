package com.pangu.unicom.base.domain;

public enum ZzsybnsrFormEnum implements BaseForm {
    // 增值税一般纳税申报主表
    zzssyyybnsrzb("zzssyyybnsrzb"),
    // 增值税纳税申报表附表一（本期销售情况明细表）
    zzssyyybnsr01bqxsqkmxb("zzssyyybnsr01bqxsqkmxb"),
    // 增值税纳税申报表附表二（本期进项税额明细表）
    zzssyyybnsr02bqjxsemxb("zzssyyybnsr02bqjxsemxb"),
    // 增值税纳税申报表附表三（服务、不动产和无形资产扣除项目明细）
    zzssyyybnsr03ysfwkcxmmx("zzssyyybnsr03ysfwkcxmmx"),
    // 增值税纳税申报表附表四（税额抵减情况表）
    zzssyyybnsr04bqjxsemxb("zzssyyybnsr04bqjxsemxb"),
    // 减免税申报明细
    zzsjmssbmxb("zzsjmssbmxb"),
    // 电信企业分支机构增值税汇总纳税信息传递单
    dxqyfzjgzzshznsxxcdd("dxqyfzjgzzshznsxxcdd"),
    // 其他扣税凭证明细
    qtkspzmxb("qtkspzmxb"),
    // 汇总纳税企业增值税分配表
    hznsqyzzsfpb("hznsqyzzsfpb"),
    // 代扣代缴税收通用缴款书抵扣清单
    dkdjsstyjksdkqd("dkdjsstyjksdkqd"),
    // 应税服务扣除项目清单
    ysfwkcxmqd("ysfwkcxmqd"),
    // 电力企业增值税销项税额和进项税额传递单
    dlqyzzsxxsehjxsecdd("dlqyzzsxxsehjxsecdd"),
    //汇总纳税企业通用传递单
    hznsqytycdd("hznsqytycdd");

    private String code;

    private String showName;

    ZzsybnsrFormEnum(String code) {
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
        return "ZzsybnsrFormEnum{" +
                "code='" + code + '\'' +
                ", showName='" + showName + '\'' +
                '}';
    }
}
