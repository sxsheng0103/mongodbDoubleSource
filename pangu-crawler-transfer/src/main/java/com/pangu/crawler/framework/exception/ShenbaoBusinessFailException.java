package com.pangu.crawler.framework.exception;

public class ShenbaoBusinessFailException extends PanicException {
    public ShenbaoBusinessFailException(String trace, String nsrsbh, Exception cause) {
        super(trace, nsrsbh, cause);
    }
}
