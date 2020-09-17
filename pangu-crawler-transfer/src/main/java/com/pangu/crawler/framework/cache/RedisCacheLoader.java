//package com.pangu.crawler.framework.cache;
//
//import com.pangu.crawler.framework.config.RedisCacheConfig;
//import com.pangu.crawler.framework.utils.EncryptDecrypt;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Primary;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//
//import javax.validation.constraints.NotNull;
//import java.util.concurrent.TimeUnit;
//
//@Component
//@Primary
//public class RedisCacheLoader implements CacheLoader {
//
//    private static final Logger logger = LoggerFactory.getLogger(RedisCacheLoader.class);
//
//    @Autowired
//    private RedisCacheConfig redisCacheConfig;
//
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @Override
//    public String load(@NotNull String trace, @NotNull String key) throws CacheTimeoutException {
//        logger.info("[{}] - cache load start! trace = {}, key = {}", trace, trace, key);
//        String value = stringRedisTemplate.opsForValue().get(key);
//        if (logger.isDebugEnabled()) {
//            logger.debug("[{}] - load value is {}! key = {}", trace, value, key);
//        }
//        if (value != null && !value.isEmpty()) {
//            logger.info("[{}] - load value success! key = {}", trace, key);
//            if (value.startsWith("*")) {
//                logger.info("[{}] - value start with * ! key = {}", trace, key);
//                return value;
//            }
//            String decryptedValue;
//            try {
//                decryptedValue = EncryptDecrypt.desDecrypt(value, redisCacheConfig.getPasswordForDataCache());
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
//            encryptedValue = EncryptDecrypt.desEncrypt(value, redisCacheConfig.getPasswordForDataCache());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        if (logger.isDebugEnabled()) {
//            logger.debug("[{}] - when cache save, cached encrypted value is {}! key = {}",
//                    trace, encryptedValue, key);
//        }
//        stringRedisTemplate.opsForValue().set(key, encryptedValue, redisCacheConfig.getTimeoutOfDataCache(),
//                TimeUnit.MINUTES);
//        logger.info("[{}] - cache save success!", trace);
//        return true;
//    }
//}
