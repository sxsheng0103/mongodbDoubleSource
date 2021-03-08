package com.pangu.unicom.base.domain;


public enum BusinessEnum implements BaseEnum {
    // 申报初始化数据获取
    SBCSH("sbcsh", "申报初始化数据获取", "shenbao", "chushihua_result.mustache"),
    // 申报清册
    SBQC("sbqc", "申报清册", "sbqc", "sbqc_result.mustache"),
    // 完税凭证下载
    WSZMCX("wszmcx", "完税证明查询", "wszmcx", "wszmcx_result.mustache"),
    // 申报提交
    SBTJ("sbtj", "申报提交", "shenbao", "shenbao_result.mustache"),
    // 申报作废
    SBZF("sbzf", "申报作废", "sbzf", "sbzf_result.mustache"),
    // 申报缴款
    SBJK("sbjk", "申报缴款", "jiaokuan", "jiaokuan_result.mustache"),
    // 申报历史查询
    SBLSCX("sblscx", "申报历史查询", "chaxun", null),
    // 申报更正
    SBGZ("sbgz", "申报更正", "sbgz", "sbgz_result.mustache"),
    // 其它业务
    OTHER("other", "其它业务", "other", "shenbao_result.mustache");

    private String code;

    private String name;

    private String sourceName;

    private String templatePath;

    BusinessEnum(String code, String name, String sourceName, String templatePath) {
        this.code = code;
        this.name = name;
        this.sourceName = sourceName;
    }

    @Override
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getTemplatePath() {
        return templatePath;
    }

    @Override
    public String toString() {
        return "BusinessEnum{" +
                "code='" + code + '\'' +
                ", name='" + name + '\'' +
                ", sourceName='" + sourceName + '\'' +
                ", templatePath='" + templatePath + '\'' +
                '}';
    }
}
