package com.pangu.unicom.base.domain;

public enum TdzzsyzFormEnum implements BaseForm {
	
	tdzzsnssbb01("tdzzsnssbb01");
	
	private String code;

	private String showName;

	TdzzsyzFormEnum(String code) {
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
