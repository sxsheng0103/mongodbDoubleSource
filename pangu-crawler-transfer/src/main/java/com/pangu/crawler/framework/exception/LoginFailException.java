package com.pangu.crawler.framework.exception;

public class LoginFailException extends PanicException {
    public LoginFailException(String trace, String nsrsbh, Exception cause) {
        super(trace, nsrsbh, cause);
    }
}
