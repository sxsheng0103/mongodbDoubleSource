/*
package com.pangu.crawler.framework.cookie;

import com.pangu.crawler.framework.cache.CacheLoader;
import com.pangu.crawler.framework.cache.CacheTimeoutException;
import com.pangu.crawler.framework.exception.LoginExpiredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@Component
public class CookieLoader {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String PREFIX = "crawlar_login_cookie_";

    @Autowired
    private CacheLoader cacheLoader;

    public Map<CookieKey, List<String>> loadCookies(@NotNull String trace, @NotNull String nsrsbh) {
        logger.info("[{}] - load cookies start! trace = {}, nsrsbh = {}", trace, trace, nsrsbh);
        if (cacheLoader == null) {
            logger.info("[{}] - cache loader is null when load cookies, and return null! nsrsbh = {}", trace, nsrsbh);
            return null;
        } else {
            Map<CookieKey, List<String>> cookies;
            String value;
            try {
                value = cacheLoader.load(trace, PREFIX + nsrsbh);
            } catch (CacheTimeoutException e) {
                throw new LoginExpiredException(trace, nsrsbh, e);
            }
            logger.info("[{}] - load cache value is {}! ", trace, value);
            try {
                cookies = CookieHelper.parseCacheLoadedCookie(trace, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            logger.info("[{}] - load cookies end! trace = {}, nsrsbh = {}, cookies = {}", trace, trace, nsrsbh, cookies);
            return cookies;
        }
    }

    public boolean saveCookies(@NotNull String trace, @NotNull String nsrsbh,
                               @NotNull Map<CookieKey, List<String>> cookies) {
        logger.info("[{}] - save cookies start! trace = {}, nsrsbh = {}, cookies = {}", trace, trace, nsrsbh, cookies);
        if (cacheLoader == null) {
            logger.info("[{}] - cache loader is null when save cookies, and return true! nsrsbh = {}", trace, nsrsbh);
            return true;
        } else {
            String cookieValue = CookieHelper.sequenceForCache(trace, cookies);
            boolean result = cacheLoader.save(trace, PREFIX + nsrsbh, cookieValue);
            logger.info("[{}] - save cookies end! trace = {}, nsrsbh = {}, result = {}, cookieValue = {}",
                    trace, trace, nsrsbh, result, cookieValue);
            return result;
        }
    }
}
*/
