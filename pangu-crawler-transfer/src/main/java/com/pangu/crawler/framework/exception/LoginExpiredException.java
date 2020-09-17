package com.pangu.crawler.framework.exception;

public class LoginExpiredException extends PanicException {

    public LoginExpiredException(String trace, String nsrsbh) {
        super(trace, nsrsbh, null);
    }

    public LoginExpiredException(String trace, String nsrsbh, Exception cause) {
        super(trace, nsrsbh, cause);
    }
}