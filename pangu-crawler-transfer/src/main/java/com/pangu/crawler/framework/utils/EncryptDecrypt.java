package com.pangu.crawler.framework.utils;

import com.google.common.base.Charsets;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.UUID;

public class EncryptDecrypt {

    private static final String KEY_ALGORITHM = "AES";

    private static final String CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";

    private static String confuse(String s) {
        if (s == null) {
            return null;
        }
        if (s.isEmpty()) {
            return "";
        }
        // 生成一个32位长度的UUID。
        String uuid = UUID.randomUUID().toString().replace("-", "");
        // 前后各加16位。
        StringBuffer sb = new StringBuffer();
        sb.append(uuid.substring(0, 16));
        sb.append(s);
        sb.append(uuid.substring(16, 32));
        return sb.toString();
    }

    private static String deconfuse(String s) throws Exception {
        if (s == null) {
            return null;
        }
        // 因为是做反向混淆，所以基于混淆的做法，此处不能小于32长度。
        if (s.length() <= 32) {
            throw new Exception("deconfuse error because of s.length <= 32! s = " + s);
        }
        // 截去前后各16位。
        String result = s.substring(16, s.length() - 16);
        return result;
    }

    private static String byte2hex(byte[] bs) {
        return String.valueOf(Hex.encodeHex(bs));
    }

    private static byte[] hex2byte(String s) throws Exception {
        return Hex.decodeHex(s.toCharArray());
    }

    public static String desEncrypt(String s, String password) throws Exception {
        String confused = confuse(s);
        byte[] bs = desEncrypt(confused.getBytes(Charsets.UTF_8), password);
        String hexEncode = byte2hex(bs);
        return hexEncode;
    }

    private static byte[] desEncrypt(byte[] src, String password) throws Exception {
        Key skeySpec = new SecretKeySpec(password.getBytes(Charsets.UTF_8), KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        byte[] ivByte = new byte[cipher.getBlockSize()];
        for (int i = 0; i < ivByte.length; i++) {
            ivByte[i] = (byte) (i + 17);
        }
        IvParameterSpec ivParams = new IvParameterSpec(ivByte);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParams);
        return cipher.doFinal(src);
    }

    public static String desDecrypt(String s, String password) throws Exception {
        byte[] hexDecode = hex2byte(s);
        byte[] bs = desDecrypt(hexDecode, password);
        String deconfused = deconfuse(new String(bs, Charsets.UTF_8));
        return deconfused;
    }

    private static byte[] desDecrypt(byte[] src, String password) throws Exception {
        Key key = new SecretKeySpec(password.getBytes(Charsets.UTF_8), KEY_ALGORITHM);
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
        byte[] ivByte = new byte[cipher.getBlockSize()];
        for (int i = 0; i < ivByte.length; i++) {
            ivByte[i] = (byte) (i + 17);
        }
        IvParameterSpec ivParamsSpec = new IvParameterSpec(ivByte);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParamsSpec);
        return cipher.doFinal(src);
    }
}
