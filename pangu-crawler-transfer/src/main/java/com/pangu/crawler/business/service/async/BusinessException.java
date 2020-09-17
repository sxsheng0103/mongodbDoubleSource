package com.pangu.crawler.business.service.async;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BusinessException extends RuntimeException {

	public BusinessException(String info) {
		super(info);
	}
}
