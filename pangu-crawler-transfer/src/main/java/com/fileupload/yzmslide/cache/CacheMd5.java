package com.fileupload.yzmslide.cache;

import org.apache.log4j.Logger;

import java.security.MessageDigest;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

class CacheMd5 {

    private static Logger logger = Logger.getLogger(CacheMd5.class);

    public static String md5(String s, Logger logger, String logPrefix) {
        if (logger == null) {
            logger = CacheMd5.logger;
        }
        String r = null;
        try {
            if (s == null) {
                logger.info(logPrefix + "md5 error! s is null!");
                return r;
            }
            byte[] bytes = s.getBytes(UTF_8);
            if (bytes == null) {
                logger.info(logPrefix + "md5 error! bytes is null!");
                return r;
            }
            if (bytes.length <= 0) {
                logger.info(logPrefix + "md5 error! bytes is empty!");
                return r;
            }
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] rBytes = messageDigest.digest(bytes);
            if (rBytes == null) {
                logger.info(logPrefix + "md5 error! rBytes is null!");
                return r;
            }
            if (rBytes.length <= 0) {
                logger.info(logPrefix + "md5 error! rBytes is empty!");
                return r;
            }
            r = Base64.getEncoder().encodeToString(rBytes);
        } catch (Exception e) {
            logger.error(logPrefix + "md5 error!", e);
        }
        return r;
    }
}
