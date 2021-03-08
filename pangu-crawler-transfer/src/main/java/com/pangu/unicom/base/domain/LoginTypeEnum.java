package com.pangu.unicom.base.domain;

public enum LoginTypeEnum implements BaseEnum {
    // CA
    CA("0", "CA"),
    // CA+密码
    CA_PASSWORD("1", "CA+密码"),
    // CA+账号+密码
    CA_ACCOUNT_PASSWORD("2", "CA+账号+密码"),
    // 账号+密码
    ACCOUNT_PASSWORD("3", "账号+密码"),
    // 账号+密码+短信验证
    ACCOUNT_PASSWORD_TELNO("4", "账号+密码+短信验证"),
    // 账号+密码+办税人员信息(姓名/身份证号)
    ACCOUNT_PASSWORD_BSRY("5", "账号+密码+办税人员信息(姓名/身份证号)"),
    // 账号+密码+短信验证+办税人员信息(姓名/身份证号)
    ACCOUNT_PASSWORD_TELNO_BSRY("6", "账号+密码+短信验证+办税人员信息(姓名/身份证号)");

    private String code;

    private String desc;

    LoginTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "LoginEnum{" +
                "code='" + code + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}
