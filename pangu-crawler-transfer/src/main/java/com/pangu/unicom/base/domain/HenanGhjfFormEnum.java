package com.pangu.unicom.base.domain;

public enum HenanGhjfFormEnum implements BaseForm  {
	ghjfnssbb("ghjfnssbb")
	;

	private String code;

	private String showName;

	HenanGhjfFormEnum(String code) {
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
		return "HenanGhjfFormEnum{" +
				"code='" + code + '\'' +
				", showName='" + showName + '\'' +
				'}';
	}

}
