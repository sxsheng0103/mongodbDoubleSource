//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.pangu.unicom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.*;

public class HttpsImportPickData {
    private static final Logger logger = LogManager.getLogger("import server request");
    public HttpsImportPickData() {
    }

    public static String requestActionPost(String urlString, String data,String secritString,String nsrsbh) {
        String response = "";
        logger.info(data);
        logger.info(String.format("url[%s],nsrsbh[%s]",urlString,nsrsbh));
        HttpURLConnection connection = null;
        try {
            SSLContext sslcontext = SSLContext.getInstance("SSL", "SunJSSE");
            sslcontext.init((KeyManager[])null, new TrustManager[]{new MyX509TrustManager()}, new SecureRandom());
            URL url = new URL(urlString);
            HostnameVerifier ignoreHostnameVerifier = new HostnameVerifier() {
                public boolean verify(String s, SSLSession sslsession) {
                    logger.warn("WARNING: Hostname is not matched for cert.");
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(ignoreHostnameVerifier);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslcontext.getSocketFactory());
            connection = (HttpURLConnection)url.openConnection();
            connection.setDoOutput(true);
            connection.setReadTimeout(30000);
            connection.setConnectTimeout(10000);
            connection.setRequestProperty("Authorization", secritString);
            connection.setRequestProperty("Content-Encoding", "utf-8");
            connection.setRequestProperty("Content-Type", "application/json");
            byte[] request = data.getBytes(StandardCharsets.UTF_8);
            String contentLength = String.valueOf(request.length);
            connection.setRequestProperty("Content-Length", contentLength);
            OutputStream outputStream = connection.getOutputStream();
            Throwable var11 = null;

            try {
                outputStream.write(request);
                outputStream.flush();
                int responseCode = connection.getResponseCode();
                logger.info("resultcode:"+responseCode);
                if (responseCode == 200) {
                    response = readResponse(connection.getInputStream());
                } else {
                    response = readResponse(connection.getErrorStream());
                }
            } catch (Throwable var29) {
                var11 = var29;
                throw var29;
            } finally {
                if (outputStream != null) {
                    if (var11 != null) {
                        try {
                            outputStream.close();
                        } catch (Throwable var28) {
                            var11.addSuppressed(var28);
                        }
                    } else {
                        outputStream.close();
                    }
                }

            }

            logger.warn("request success!");
        } catch (Exception var31) {
            var31.printStackTrace();
            logger.error("请求异常"+var31.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

        }

        logger.info(String.format("[%s] response : [%s]", new Object[]{urlString, response}));
        return response;
    }

    public static String getconnbyget(String url,String secritString) throws Exception {
        StringBuilder result = new StringBuilder();

        try {
            URL u = new URL(url);
            HttpsURLConnection huconn = (HttpsURLConnection)u.openConnection();
            huconn.setRequestProperty("Authorization", secritString);
            huconn.connect();
            BufferedReader in = new BufferedReader(new InputStreamReader(huconn.getInputStream(), "UTF-8"));

            String line;
            while((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (IOException var7) {
            throw new Exception("密钥超时或错误,请更新密钥!" + var7.getMessage());
        }

        return result.toString();
    }

    private static String readResponse(InputStream inputStream) throws Exception {
        byte[] responseBytes = new byte[0];

        while(true) {
            byte[] readedBytes = new byte[1024];
            int readedCount = inputStream.read(readedBytes);
            if (readedCount <= 0) {
                StringBuilder readResponse = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(responseBytes), StandardCharsets.UTF_8));
                Throwable var17 = null;

                try {
                    for(String line = br.readLine(); line != null; line = br.readLine()) {
                        readResponse.append(line).append("\r\n");
                    }
                } catch (Throwable var13) {
                    var17 = var13;
                    throw var13;
                } finally {
                    if (br != null) {
                        if (var17 != null) {
                            try {
                                br.close();
                            } catch (Throwable var12) {
                                var17.addSuppressed(var12);
                            }
                        } else {
                            br.close();
                        }
                    }

                }

                return readResponse.toString();
            }

            byte[] newBytes = new byte[responseBytes.length + readedCount];
            System.arraycopy(responseBytes, 0, newBytes, 0, responseBytes.length);
            System.arraycopy(readedBytes, 0, newBytes, responseBytes.length, readedCount);
            responseBytes = newBytes;
        }
    }

    static class MyX509TrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }
}
