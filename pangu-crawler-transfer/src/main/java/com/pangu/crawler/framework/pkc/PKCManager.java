/*
package com.pangu.crawler.framework.pkc;

import com.pangu.crawler.framework.utils.PathUtils;

import javax.net.ssl.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class PKCManager {

    public static void register(String area, String url) throws Exception {
        File file = PathUtils.fileInPkcs(area + "-pkc");
        if (!file.exists()) {
            load(file, url);
        }
        System.setProperty("javax.net.ssl.trustStore", file.getPath());
    }

    public static void registerByPort(String area, String url,int port) throws Exception {
        File file = PathUtils.fileInPkcs(area + "-pkc");
        if (!file.exists()) {
            loadByPort(file, url,port);
        }
        System.setProperty("javax.net.ssl.trustStore", file.getPath());
    }

    private static void loadByPort(File file, String url,int port) throws Exception {
        String host = url.substring("https://".length());
        char[] passPhrase = "changeit".toCharArray();
        File cacerts = new File(System.getProperty("java.home")
                + File.separator + "lib"
                + File.separator + "security"
                + File.separator + "cacerts");
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream inputStream = new FileInputStream(cacerts)) {
            keyStore.load(inputStream, passPhrase);
        }
        SSLContext context = SSLContext.getInstance("TLS");
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        X509TrustManager defaultTrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
        SavingTrustManager savingTrustManager = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[]{savingTrustManager}, null);
        SSLSocketFactory factory = context.getSocketFactory();
        try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {
            socket.setSoTimeout(60 * 1000);
            socket.startHandshake();
        } catch (Exception e) {
            // 忽略！
        }
        X509Certificate[] chain = savingTrustManager.chain;
        if (chain == null) {
            throw new Exception("Could not obtain server certificate chain!");
        }
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        for (X509Certificate cert : chain) {
            sha1.update(cert.getEncoded());
            md5.update(cert.getEncoded());
        }
        keyStore.setCertificateEntry(host + "-1", chain[0]);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            keyStore.store(fileOutputStream, passPhrase);
        }
    }


    private static void load(File file, String url) throws Exception {
        String host = url.substring("https://".length());
        int port = 443;
        char[] passPhrase = "changeit".toCharArray();
        File cacerts = new File(System.getProperty("java.home")
                + File.separator + "lib"
                + File.separator + "security"
                + File.separator + "cacerts");
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream inputStream = new FileInputStream(cacerts)) {
            keyStore.load(inputStream, passPhrase);
        }
        SSLContext context = SSLContext.getInstance("TLS");
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        X509TrustManager defaultTrustManager = (X509TrustManager) trustManagerFactory.getTrustManagers()[0];
        SavingTrustManager savingTrustManager = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[]{savingTrustManager}, null);
        SSLSocketFactory factory = context.getSocketFactory();
        try (SSLSocket socket = (SSLSocket) factory.createSocket(host, port)) {
            socket.setSoTimeout(60 * 1000);
            socket.startHandshake();
        } catch (Exception e) {
            // 忽略！
        }
        X509Certificate[] chain = savingTrustManager.chain;
        if (chain == null) {
            throw new Exception("Could not obtain server certificate chain!");
        }
        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        for (X509Certificate cert : chain) {
            sha1.update(cert.getEncoded());
            md5.update(cert.getEncoded());
        }
        keyStore.setCertificateEntry(host + "-1", chain[0]);
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
            keyStore.store(fileOutputStream, passPhrase);
        }
    }

    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager trustManager;

        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager trustManager) {
            this.trustManager = trustManager;
        }

        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) {
            throw new UnsupportedOperationException();
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            this.chain = chain;
            this.trustManager.checkServerTrusted(chain, authType);
        }
    }
}
*/
