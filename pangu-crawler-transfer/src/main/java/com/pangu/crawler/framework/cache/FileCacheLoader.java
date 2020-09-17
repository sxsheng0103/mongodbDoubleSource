//package com.pangu.crawler.framework.cache;
//
//import com.pangu.crawler.framework.config.FileCacheConfig;
//import com.pangu.crawler.framework.utils.CharsetEnum;
//import com.pangu.crawler.framework.utils.EncryptDecrypt;
//import org.apache.commons.io.FileUtils;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import javax.validation.constraints.NotNull;
//import java.io.File;
//import java.util.List;
//
//@Component
//public class FileCacheLoader implements CacheLoader, InitializingBean {
//
//    private static final Logger logger = LoggerFactory.getLogger(FileCacheLoader.class);
//
//    @Autowired
//    private FileCacheConfig fileCacheConfig;
//
//    @Override
//    public String load(@NotNull String trace, @NotNull String key) throws CacheTimeoutException {
//        logger.info("[{}] - cache load start! trace = {}, key = {}", trace, trace, key);
//        String value;
//        File file = new File(String.format(
//                fileCacheConfig.getPath() + File.separator + "sb_cookies" + File.separator + "cookie_%s", key));
//        try {
//            List<String> values = FileUtils.readLines(file, CharsetEnum.UTF8.getCharset());
//            //if (values == null) {
//            //    throw new Exception("file read values is empty! file = " + file);
//            //}
//            if (values.size() != 1) {
//                throw new Exception("file read values size not equal 1! file = " + file + ", values = " + values);
//            }
//            value = values.get(0);
//        } catch (Exception e) {
//            logger.error("[{}] - cache load fail, result is null! trace = {}, key = {}", trace, trace, key, e);
//            return null;
//        }
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
//                decryptedValue = EncryptDecrypt.desDecrypt(value, fileCacheConfig.getPasswordForDataCache());
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
//            encryptedValue = EncryptDecrypt.desEncrypt(value, fileCacheConfig.getPasswordForDataCache());
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        if (logger.isDebugEnabled()) {
//            logger.debug("[{}] - when cache save, cached encrypted value is {}! key = {}",
//                    trace, encryptedValue, key);
//        }
//        File file = new File(String.format(
//                fileCacheConfig.getPath() + File.separator + "sb_cookies" + File.separator + "cookie_%s", key));
//        try {
//            FileUtils.write(file, encryptedValue, CharsetEnum.UTF8.getCharset());
//        } catch (Exception e) {
//            logger.error("[{}] - cache save fail!", trace, e);
//            return false;
//        }
//        logger.info("[{}] - cache save success!", trace);
//        return true;
//    }
//
//    @Override
//    public void afterPropertiesSet() throws Exception {
//        // TODO
//    }
//}
