package com.pangu.crawler.framework.exception;

public class BuShenbaoException extends PanicException {
    public BuShenbaoException(String trace, String nsrsbh) {
        super(trace, nsrsbh, null);
    }
}
