package com.pangu.crawler.framework.exception;

public class NotFoundException extends PanicException {
    public NotFoundException(String trace, String nsrsbh, Exception cause) {
        super(trace, nsrsbh, cause);
    }
}
