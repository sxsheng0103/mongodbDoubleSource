/*
package com.pangu.crawler.framework.service;

import com.pangu.crawler.framework.cookie.CookieOperation;
import com.pangu.crawler.framework.http.HttpManager;

public class ServiceFirstArg {

    private String trace;

    private String nsrsbh;

    private HttpManager httpManager;

    private boolean cookieExpiredCheck;

    private CookieOperation cookieOperation;

    @Deprecated
    public ServiceFirstArg(String trace, String nsrsbh) {
        this(trace, nsrsbh, true, CookieOperation.LOAD);
    }

    @Deprecated
    public ServiceFirstArg(String trace, String nsrsbh, boolean cookieExpiredCheck, CookieOperation cookieOperation) {
        this.trace = trace;
        this.nsrsbh = nsrsbh;
        this.cookieExpiredCheck = cookieExpiredCheck;
        this.cookieOperation = cookieOperation;
    }

    public static ServiceFirstArg cookieIgnore(String trace, String nsrsbh) {
        return new ServiceFirstArg(trace, nsrsbh, false, CookieOperation.NONE);
    }

    public static ServiceFirstArg cookie(String trace, String nsrsbh) {
        return new ServiceFirstArg(trace, nsrsbh, true, CookieOperation.LOAD);
    }

    public static ServiceFirstArg cookieSave(String trace, String nsrsbh) {
        return new ServiceFirstArg(trace, nsrsbh, false, CookieOperation.SAVE);
    }

    public String getTrace() {
        return trace;
    }

    public String getNsrsbh() {
        return nsrsbh;
    }

    public boolean isCookieExpiredCheck() {
        return cookieExpiredCheck;
    }

    public CookieOperation getCookieOperation() {
        return cookieOperation;
    }

    public HttpManager getHttpManager() {
        return httpManager;
    }

    public void setHttpManager(HttpManager httpManager) {
        if (this.httpManager != null) {
            throw new RuntimeException("httpManager can be set only once!");
        }
        this.httpManager = httpManager;
    }

    @Override
    public String toString() {
        return "ServiceFirstArg{" +
                "trace='" + trace + '\'' +
                ", nsrsbh='" + nsrsbh + '\'' +
                ", httpManager=" + httpManager +
                ", cookieExpiredCheck=" + cookieExpiredCheck +
                ", cookieOperation=" + cookieOperation +
                '}';
    }
}
*/
