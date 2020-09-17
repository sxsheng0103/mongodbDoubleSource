//package com.pangu.crawler.framework.cache;
//
//import com.pangu.crawler.framework.config.HttpCacheConfig;
//import com.pangu.crawler.framework.config.HttpConfig;
//import com.pangu.crawler.framework.utils.EncryptDecrypt;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.client.SimpleClientHttpRequestFactory;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import javax.validation.constraints.NotNull;
//
//@Component
//public class HttpCacheLoader implements CacheLoader, InitializingBean {
//
//    private static final Logger logger = LoggerFactory.getLogger(HttpCacheLoader.class);
//
//    @Autowired
//    private HttpCacheConfig httpCacheConfig;
//
//    @Autowired
//    private HttpConfig httpConfig;
//
//    private RestTemplate restTemplate;
//
//    @Override
//    public String load(@NotNull String trace, @NotNull String key) throws CacheTimeoutException {
//        logger.info("[{}] - cache load start! trace = {}, key = {}", trace, trace, key);
//        String value = restTemplate.getForObject(httpCacheConfig.getHost() + urlSuffix(), String.class, key);
//        if (logger.isDebugEnabled()) {
//            logger.debug("[{}] - load value is {}! key = {}", trace, value, key);
//        }
//        if (value != null && value.startsWith("[success]")) {
//            logger.info("[{}] - load value success! key = {}", trace, key);
//            value = value.substring("[success]".length());
//            long createTime;
//            try {
//                createTime = Long.parseLong(value.substring(value.indexOf("[") + 1, value.indexOf("]")));
//            } catch (Exception e) {
//                throw new CacheTimeoutException("create time parse failed : " + value, e);
//            }
//            long currentTime = System.currentTimeMillis();
//            long timeout = httpCacheConfig.getTimeoutOfDataCache() * 60000L;
//            if (currentTime - createTime > timeout) {
//                throw new CacheTimeoutException("cookie timeout : current = " + currentTime
//                        + ", create = " + createTime + ", timeout = " + timeout);
//            }
//            String decryptedValue;
//            try {
//                value = value.substring(value.indexOf("]") + 1);
//                decryptedValue = EncryptDecrypt.desDecrypt(value, httpCacheConfig.getPasswordForDataCache());
//            } catch (Exception e) {
//                throw new CacheTimeoutException("decrypt failed : " + value, e);
//            }
//            if (logger.isDebugEnabled()) {
//                logger.debug("[{}] - cache load success! trace = {}, key = {}, result = {}",
//                        trace, trace, key, decryptedValue);
//            } else {
//                logger.info("[{}] - cache load success! trace = {}, key = {}", trace, trace, key);
//            }
//            return decryptedValue;
//        } else {
//            logger.info("[{}] - cache load fail, result is null! trace = {}, key = {}", trace, trace, key);
//            return null;
//        }
//    }
//
//    @Override
//    public boolean save(@NotNull String trace, @NotNull String key, @NotNull String value) {
//        if (logger.isDebugEnabled()) {
//            logger.debug("[{}] - cache save start! trace = {}, key = {}, value = {}", trace, trace, key, value);
//        } else {
//            logger.info("[{}] - cache save start! trace = {}, key = {}", trace, trace, key);
//        }
//        String encryptedValue;
//        try {
//            encryptedValue = EncryptDecrypt.desEncrypt(value, httpCacheConfig.getPasswordForDataCache());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        if (logger.isDebugEnabled()) {
//            logger.debug("[{}] - when cache save, encrypted value is {}! key = {}", trace, encryptedValue, key);
//        }
//        String cachedValue = String.format("[%d]%s", System.currentTimeMillis(), encryptedValue);
//        if (logger.isDebugEnabled()) {
//            logger.debug("[{}] - when cache save, cached value is {}! key = {}", trace, cachedValue, key);
//        }
//        String result = restTemplate.postForObject(httpCacheConfig.getHost() + urlSuffix(),
//                cachedValue, String.class, key);
//        if (result.startsWith("[success]")) {
//            logger.info("[{}] - cache save success! result = {}", trace, result);
//            return true;
//        } else {
//            logger.error("[{}] - cache save fail! result = {}", trace, result);
//            return false;
//        }
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
//        requestFactory.setConnectTimeout(httpConfig.getConnectTimeout() * 1000);
//        requestFactory.setReadTimeout(httpConfig.getReadTimeout() * 1000);
//        restTemplate = new RestTemplate(requestFactory);
//    }
//}
