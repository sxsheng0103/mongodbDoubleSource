package com.pangu.unicom.base.domain;

public enum CztdsysfcsFormEnum implements BaseForm {
	 // 利润表
	cztdsysGrid("cztdsysGrid"),
    // 现金流量表
	fcsGrid("fcsGrid"),
    // 资产负债表
	jmsmxGrid("jmsmxGrid");
	
	 private String code;

    private String showName;

    CztdsysfcsFormEnum(String code) {
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
        return "CwbbFormEnum{" +
                "code='" + code + '\'' +
                ", showName='" + showName + '\'' +
                '}';
    }
}
