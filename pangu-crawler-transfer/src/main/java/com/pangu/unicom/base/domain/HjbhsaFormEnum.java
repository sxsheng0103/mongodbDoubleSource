package com.pangu.unicom.base.domain;

public enum HjbhsaFormEnum implements BaseForm{
	zb("zb"),
	jmmxb("jmmxb"),
	swrw("swrw"),
	dqwrw("dqwrw");

	private String code;

	private String showName;


	HjbhsaFormEnum(String code) {
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
		return "FjsxgmFormEnum{" +
				"code='" + code + '\'' +
				", showName='" + showName + '\'' +
				'}';
	}


}
