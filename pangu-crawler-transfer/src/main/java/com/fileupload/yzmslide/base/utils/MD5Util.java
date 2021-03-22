package com.fileupload.yzmslide.base.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Util {
    public static final String MD5 = "MD5";
    public static final String ENCODING = "UTF-8";

    /**
     * 生成MD5
     *
     * @param plainText
     * @return
     */
    public static String getMd5(String plainText) {
        try {
            MessageDigest md = MessageDigest.getInstance(MD5);
            md.update(plainText.getBytes(ENCODING));
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            return buf.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
