package com.pangu.unicom.base.domain;

public enum ShandongCpyXfsFormEnum implements BaseForm{
	ysxfpxfsnssbb("ysxfpxfsnssbb"),
	bqzykcsejsb("bqzykcsejsb"),
	bqjmsemxb("bqjmsemxb"),
	bqwtjgqkbgb("bqwtjgqkbgb");
	private String code;

	private String showName;

	ShandongCpyXfsFormEnum(String code) {
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
