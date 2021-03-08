package com.pangu.unicom.base.domain;

public enum ZzsyjskbFormEnum  implements BaseForm {
	yjskb("yjskb");
	
	private String code;

	private String showName;

	ZzsyjskbFormEnum(String code) {
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
