package com.pangu.crawler.framework.utils;

import org.apache.commons.net.util.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * <RSA加密解密工具类>
 * <额外依赖 commons-net-3.3.jar,日志用的log4j，如果是其他的日志框架可以更改>
 * @author wzh
 * @version 2018-12-16 18:20
 * @see [相关类/方法] (可选)
 **/
public class RsaUtils
{

    private static Logger log = LoggerFactory.getLogger(RsaUtils.class);

    /**
     * 块加密大小
     */
    private static final int CACHE_SIZE = 1024;

    /**
     * 加密算法RSA
     */
    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 签名算法
     */
    public static final String SIGNATURE_ALGORITHM = "MD5withRSA";

    /**
     * 获取公钥的key
     */
    private static final String PUBLIC_KEY = "RsaPublicKey";

    /**
     * 获取私钥的key
     */
    private static final String PRIVATE_KEY = "RsaPrivateKey";

    /**
     * RSA最大加密明文大小
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;

    /**
     * RSA最大解密密文大小
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

    /**
     * Base64字符串解码为二进制数据
     * @param base64
     * @return 二进制数据
     * @throws Exception
     */
    public static byte[] decodeBase64(String base64)
            throws Exception
    {
        return Base64.decodeBase64(base64.getBytes());
    }

    /**
     * 二进制数据编码为Base64字符串
     * @param bytes
     * @return Base64字符串
     * @throws Exception
     */
    public static String encodeBase64(byte[] bytes)
            throws Exception
        {
        return new String(Base64.encodeBase64(bytes));
    }

    /**
     * 生成秘钥对
     * @return 返回公钥和私钥的Map集合
     * @throws Exception
     */
    public static Map<String, Object> initKeyPair()
            throws Exception
    {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(KEY_ALGORITHM);
        keyPairGen.initialize(CACHE_SIZE);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey)keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<String, Object>(2);
        // 公钥
        keyMap.put(PUBLIC_KEY, publicKey);
        // 私钥
        keyMap.put(PRIVATE_KEY, privateKey);

        return keyMap;
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * @throws Exception 加密过程中的异常信息
     */
    public static String encrypt(String str, String publicKey) throws Exception {
        // base64编码的公钥
        byte[] decoded = Base64.decodeBase64(publicKey);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        // RSA加密
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);
        return Base64.encodeBase64String(cipher.doFinal(str.getBytes(StandardCharsets.UTF_8)));
    }


    /**
     * 获取私钥
     * @param keyMap 秘钥对Map
     * @return 私钥字符串
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return encodeBase64(key.getEncoded());
    }

    /**
     * 获取公钥字符串
     * @param keyMap 秘钥对Map
     * @return 公钥字符串
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return encodeBase64(key.getEncoded());
    }

    /**
     * 使用私钥生成数字签名
     * @param data 使用私钥加密的数据
     * @param privateKey 是哟啊字符串
     * @return 数字签名
     * @throws Exception
     */
    public static String sign(byte[] data, String privateKey) throws Exception {
        // 获取byte数组
        byte[] keyBytes = decodeBase64(privateKey);
        // 构造PKCS8EncodedKeySpec对象
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        // 指定的加密算法
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 取私钥匙对象
        PrivateKey privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        // 用私钥对信息生成数字签名
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateK);
        signature.update(data);
        return encodeBase64(signature.sign());
    }

    /**
     * 校验数字签名
     * @param data 私钥加密的数据
     * @param publicKey 公钥字符串
     * @param sign 私钥生成的签名
     * @return 校验成功返回true 失败返回false
     * @throws Exception
     */
    public static boolean verify(byte[] data, String publicKey, String sign) throws Exception {
        // 获取byte数组
        byte[] keyBytes = decodeBase64(publicKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        // 构造X509EncodedKeySpec对象
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        // 指定的加密算法
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        // 取公钥匙对象
        PublicKey publicK = keyFactory.generatePublic(keySpec);
        signature.initVerify(publicK);
        signature.update(data);

        // 验证签名是否正常
        return signature.verify(decodeBase64(sign));
    }

    /**
     * 私钥加密
     * @param data 需要加密的数据
     * @param privateKey 私钥
     * @return 加密后的数据
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String privateKey) throws Exception {
        byte[] keyBytes = decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 公钥加密
     * @param data 需要加密的数据
     * @param publicKey 公钥字符串
     * @return 加密后的数据
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String publicKey) throws Exception {
        byte[] keyBytes = decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        // 对数据加密
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicK);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    /**
     * 私钥解密
     * @param encryptedData 公钥加密的数据
     * @param privateKey 私钥字符串
     * @return 私钥解密的数据
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData, String privateKey) throws Exception {
        byte[] keyBytes = decodeBase64(privateKey);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateK = keyFactory.generatePrivate(pkcs8KeySpec);
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥解密
     * @param encryptedData 私钥加密的数据
     * @param publicKey 公钥字符串
     * @return 公钥解密的数据
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String publicKey) throws Exception {
        byte[] keyBytes = decodeBase64(publicKey);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicK = keyFactory.generatePublic(x509KeySpec);
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicK);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    /**
     * 公钥加密方法
     * @param data 需加密的字符串
     * @param PUBLICKEY 公钥字符串
     * @return 加密后的字符串
     */
    public static String encryptedDataByPublic(String data, String PUBLICKEY) {
        try {
            data = encodeBase64(encryptByPublicKey(data.getBytes(), PUBLICKEY));
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage(),e);
        }
        return data;
    }

    /**
     * 私钥解密方法
     * @param data 公钥加密的字符串
     * @param PRIVATEKEY 私钥字符串
     * @return 私钥解密的字符串
     */
    public static String decryptDataByPrivate(String data, String PRIVATEKEY) {
        String temp = "";
        try {
            byte[] rs = decodeBase64(data);
            //以utf-8的方式生成字符串
            temp = new String(decryptByPrivateKey(hexStringToByteArray(data), PRIVATEKEY),"UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return temp;
    }

    public static final String hexStringToByteArray(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toLowerCase());
        }
        return sb.toString();
    }


    /**
     * 16进制表示的字符串转换为字节数组
     *
     * @param s 16进制表示的字符串
     * @return byte[] 字节数组
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] b = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            // 两位一组，表示一个字节,把这样表示的16进制字符串，还原成一个字节
            b[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
                    .digit(s.charAt(i + 1), 16));
        }
        return b;
    }

    private static void demo() {
        try {
            Map<String, Object> keyMap = RsaUtils.initKeyPair();
            String publicKey = RsaUtils.getPublicKey(keyMap);
            String privateKey = RsaUtils.getPrivateKey(keyMap);
            System.out.println("公钥：" + publicKey);
            System.out.println("私钥：" + privateKey);
            String source = "我是需要私钥加密的字符串！";
            System.out.println("签名验证逻辑，私钥加密--公钥解密，需要加密的字符串：" + source);
            byte[] data = source.getBytes();
            byte[] encodedData = RsaUtils.encryptByPrivateKey(data, privateKey);
            System.out.println("私钥加密后：" + new String(encodedData));
            String sign = RsaUtils.sign(encodedData, privateKey);
            System.out.println("签名:" + sign);
            boolean status = RsaUtils.verify(encodedData, publicKey, sign);
            System.out.println("验证结果:" + status);
            byte[] decodedData = RsaUtils.decryptByPublicKey(encodedData, publicKey);
            String target = new String(decodedData);
            System.out.println("公钥解密私钥加密的数据:" + target);

            System.out.println("---------公钥加密----私钥解密----------");
            // 这里尽量长一点，复制了一段歌词
            String msg = "月溅星河，长路漫漫，风烟残尽，独影阑珊；谁叫我身手不凡，谁让我爱恨两难，到后来，" +
                    "肝肠寸断。幻世当空，恩怨休怀，舍悟离迷，六尘不改；且怒且悲且狂哉，是人是鬼是妖怪，不过是，" +
                    "心有魔债。叫一声佛祖，回头无岸，跪一人为师，生死无关；善恶浮世真假界,尘缘散聚不分明，难断！" +
                    "我要这铁棒有何用，我有这变化又如何；还是不安，还是氐惆，金箍当头，欲说还休。我要这铁棒醉舞魔，" +
                    "我有这变化乱迷浊；踏碎灵霄，放肆桀骜，世恶道险，终究难逃。";
            String ecodeMsg = RsaUtils.encryptedDataByPublic(msg,publicKey);
            System.out.println("加密后的歌词：" + ecodeMsg);
            String decodeMsg = RsaUtils.decryptDataByPrivate(ecodeMsg,privateKey);
            System.out.println("解密后的歌词：" + decodeMsg);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


//    public static void main(String[] args) {
//        String enryptdata = "97df98739e0d93553343eeefd732676670d77b359085a9baf388966b9a9746a91c95b6dbc71b51c56b0b2cc9944760de4362bc7605fdb37a111d5014c2c40ee06522ec061060a9ac07e6ed2ccdd0fb353d0885d4d8def6a02fee1bc92fe4e5d6fa6c2e4d0ac09820d751b5f3bcd9fbe68e448ca82bc21ff18f886d70266a41df";
//        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzQmjC23th97MBVg6MnMPeONzHcdlD+1w53DBqofjZlQTDiTO6LTwuaaPPu4GKwnuRne74IuCwutYOTt86oZP2U0oh5CxOcp8WY9LSIcG+VM5RpxKS+KnmINALkdjOLBYjLS1DUsS7w3XSU6VX/83KbQCKC42YZFaM41PGDLQJCQIDAQAB";
//        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALNCaMLbe2H3swFWDoycw9443Mdx2UP7XDncMGqh+NmVBMOJM7otPC5po8+7gYrCe5Gd7vgi4LC61g5O3zqhk/ZTSiHkLE5ynxZj0tIhwb5UzlGnEpL4qeYg0AuR2M4sFiMtLUNSxLvDddJTpVf/zcptAIoLjZhkVozjU8YMtAkJAgMBAAECgYBeaNdr6NRuoFoylfRLsjSmUSRWFmvrFfNYtdL6FhxN2+IKabRIlZJgj1qrCYIMZqlV4+v728Ki0YPzPS7sXjTiYLJ+TSAAWZywFr83YvpG/AznSbkwu1CL/kiwfxoCHTcNK0ccYSEons9Ef90o+njN0IuHNParXgkbUkREOEqjUQJBAOKWmmJgX+rJTZ/3O2zZByNDWOe+cFCST8tgl+48zRtPmPScUM8BRLIsgZJRVQ13/KOoYdyBFkHR3Ak294vND2MCQQDKhxhRAZ5qqhJQk02XBYmqnckkMbuPA4HuZTm62Z1FKNcK0SZuTXcCKCcFBFGRZgegv9ir57BKxgIlNCqHFN+jAkEA3GZHhzddNSBXvCicifTUh41jtg88anLW4Ol0/wvvKgiTrCM8Sw5dxr2ZzzwDchVrDbmkbvq1LpK159SqlzaAJwJAdxZI7hkNlZq7ejWjghH3iSOOioHTz5w4Yn2THJOpLml9SRcCigly2QnTJ9gKPnzIL3WfajWqK82SpV2vRA66EQJBANmm8w8NorldSMm2vRXKbeZAWrddOx2f3XZ1zt7OHQAzLBdx8dAN62rehYJDiQpwaJpqhtwdzhOlzMcys3ABZjk=";
//        //byte[] decodedData;
//        try {
//            byte[] encodedata = RsaUtils.encryptByPublicKey("123456".getBytes(), publicKey);
//            System.out.println(bytesToHexString(encodedata));
//            String decodedData = RsaUtils.decryptDataByPrivate(bytesToHexString(encodedata), privateKey);
//            String target = new String(decodedData);
//            System.out.println("公钥解密私钥加密的数据:" + target);
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
////        demo();
//
//    }


    private static BigInteger n = null;
    private static BigInteger e = null;

    /**
     * 使用Base64加密用户名(su的获取)
     * @param account
     * @return
     */
    @SuppressWarnings("deprecation")
    public static String encodeAccount(String account){
        return new String(Base64.encodeBase64(account.getBytes()));
    }

    /**
     * 使用RSAEncrypt对用户密码进行加密(sp的获取)
     * @param pwd
     * @param nStr
     * @param eStr
     * @return
     */
    public static String RSAEncrypt(String pwd, String nStr, String eStr){
        n = new BigInteger(nStr,16);
        e = new BigInteger(eStr,16);

        BigInteger r = RSADoPublic(pkcs1pad2(pwd,(n.bitLength()+7)>>3));
        String sp = r.toString(16);
        if((sp.length()&1) != 0 )
            sp = "0" + sp;
        return sp;
    }

    private static BigInteger RSADoPublic(BigInteger x){
        return x.modPow(e, n);
    }

    private static BigInteger pkcs1pad2(String s, int n){
        if(n < s.length() + 11) { // TODO: fix for utf-8
            System.err.println("Message too long for RSA");
            return null;
        }
        byte[] ba = new byte[n];
        int i = s.length()-1;
        while(i >= 0 && n > 0) {
            int c = s.codePointAt(i--);
            if(c < 128) { // encode using utf-8
                ba[--n] = new Byte(String.valueOf(c));
            }
            else if((c > 127) && (c < 2048)) {
                ba[--n] = new Byte(String.valueOf((c & 63) | 128));
                ba[--n] = new Byte(String.valueOf((c >> 6) | 192));
            }
            else {
                ba[--n] = new Byte(String.valueOf((c & 63) | 128));
                ba[--n] = new Byte(String.valueOf(((c >> 6) & 63) | 128));
                ba[--n] = new Byte(String.valueOf((c >> 12) | 224));
            }
        }
        ba[--n] = new Byte("0");

        byte[] temp = new byte[1];
        Random rdm = new Random(47L);

        while(n > 2) { // random non-zero pad
            temp[0] = new Byte("0");
            while(temp[0] == 0)
                rdm.nextBytes(temp);
            ba[--n] = temp[0];
        }
        ba[--n] = 2;
        ba[--n] = 0;

        return new BigInteger(ba);
    }
    public static final String bytesToHexString(byte[] bArray) {
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp.toLowerCase());
        }
        return sb.toString();
    }

}

