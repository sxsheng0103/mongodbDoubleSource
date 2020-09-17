package com.pangu.crawler.ocr;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.framework.exception.ClientException;
import com.pangu.crawler.framework.utils.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Base64Utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class LianZhongDama {

    private static final Logger logger = LoggerFactory.getLogger(LianZhongDama.class);

    private static final String yzmUrl ="https://gec.10010.com/wtd-api/yktdx/getsms";

    public static String parseQrcode(String filePath) {
        String res = "";
        String BOUNDARY = "---------------------------68163001211748"; //boundary就是request头和上传文件内容的分隔符
        String str = "http://v1-http-api.jsdama.com/api.php?mod=php&act=upload";
        Map<String, String> paramMap = getParamMap();
        try {
            URL url = new URL(str);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("content-type", "multipart/form-data; boundary=" + BOUNDARY);
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);

            OutputStream out = new DataOutputStream(connection.getOutputStream());
            // 普通参数
            if (paramMap != null) {
                StringBuffer strBuf = new StringBuffer();
                Iterator<Entry<String, String>> iter = paramMap.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry<String, String> entry = iter.next();
                    String inputName = entry.getKey();
                    String inputValue = entry.getValue();
                    strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                    strBuf.append("Content-Disposition: form-data; name=\""
                            + inputName + "\"\r\n\r\n");
                    strBuf.append(inputValue);
                }
                out.write(strBuf.toString().getBytes());
            }

            // 图片文件
            if (filePath != null) {
                File file = new File(filePath);
                String filename = file.getName();
                String contentType = "image/jpeg";//这里看情况设置
                StringBuffer strBuf = new StringBuffer();
                strBuf.append("\r\n").append("--").append(BOUNDARY).append("\r\n");
                strBuf.append("Content-Disposition: form-data; name=\""
                        + "upload" + "\"; filename=\"" + filename + "\"\r\n");
                strBuf.append("Content-Type:" + contentType + "\r\n\r\n");
                out.write(strBuf.toString().getBytes());
                DataInputStream in = new DataInputStream(
                        new FileInputStream(file));
                int bytes = 0;
                byte[] bufferOut = new byte[1024];
                while ((bytes = in.read(bufferOut)) != -1) {
                    out.write(bufferOut, 0, bytes);
                }
                in.close();
            }
            byte[] endData = ("\r\n--" + BOUNDARY + "--\r\n").getBytes();
            out.write(endData);
            out.flush();
            out.close();

            //读取URLConnection的响应
            InputStream in = connection.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            while (true) {
                int rc = in.read(buf);
                if (rc <= 0) {
                    break;
                } else {
                    bout.write(buf, 0, rc);
                }
            }
            in.close();
            //结果输出
            res = new String(bout.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    public static String parseYzm(String filePath) {
        String res = "";
        try {
            File file = new File(filePath);
            InputStream in = new FileInputStream(file);
            byte[] bytes = new byte[(int) file.length()];
            in.read(bytes);
            String base64Str = Base64Utils.encodeToString(bytes);

            URL url = new URL("http://39.105.160.146:8909/api/receive/receiveImg");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方式
            connection.setRequestMethod("POST");
            // 设置是否向HttpURLConnection输出
            connection.setDoOutput(true);
            // 设置是否从httpUrlConnection读入
            connection.setDoInput(true);
            // 设置是否使用缓存
            connection.setUseCaches(false);
            //设置参数类型是json格式
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.connect();
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("area", "zhejiang");
            paramMap.put("base64Code", base64Str);
            paramMap.put("taxNo", "111111");
            paramMap.put("captchaType", 1038);
            paramMap.put("captchaMinLength", 4);
            paramMap.put("captchaMaxLength", 4);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(JSON.toJSONString(paramMap));
            writer.close();
            //读取URLConnection的响应
            InputStream ins = connection.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            while (true) {
                int rc = ins.read(buf);
                if (rc <= 0) {
                    break;
                } else {
                    bout.write(buf, 0, rc);
                }
            }
            ins.close();
            //结果输出
            res = new String(bout.toByteArray());
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    
    /**
     * 直接填base64Code
     */
    public static String parseYzmByBase64Str(String base64Str,String area,
    		String taxNo,String captchaType,String captchaMinLength, String captchaMaxLength) {
    	String res = "";
    	try {

    		URL url = new URL("http://39.105.160.146:8909/api/receive/receiveImg");
    		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    		// 设置请求方式
    		connection.setRequestMethod("POST");
    		// 设置是否向HttpURLConnection输出
    		connection.setDoOutput(true);
    		// 设置是否从httpUrlConnection读入
    		connection.setDoInput(true);
    		// 设置是否使用缓存
    		connection.setUseCaches(false);
    		//设置参数类型是json格式
    		connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
    		connection.connect();
    		Map<String, Object> paramMap = new HashMap<>();
    		paramMap.put("area", area);
    		paramMap.put("base64Code", base64Str);
    		paramMap.put("taxNo", taxNo);
    		paramMap.put("captchaType", captchaType);
    		paramMap.put("captchaMinLength", captchaMinLength);
    		paramMap.put("captchaMaxLength", captchaMaxLength);

    		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
    		writer.write(JSON.toJSONString(paramMap));
    		writer.close();
    		//读取URLConnection的响应
    		InputStream ins = connection.getInputStream();
    		ByteArrayOutputStream bout = new ByteArrayOutputStream();
    		byte[] buf = new byte[1024];
    		while (true) {
    			int rc = ins.read(buf);
    			if (rc <= 0) {
    				break;
    			} else {
    				bout.write(buf, 0, rc);
    			}
    		}
    		ins.close();
    		//结果输出
    		res = new String(bout.toByteArray());
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return res;
    }

    public static String parseYzmByBase64(String base64Str) {
        String res = "";
        try {
            URL url = new URL("http://39.105.160.146:8909/api/receive/receiveImg");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            // 设置请求方式
            connection.setRequestMethod("POST");
            // 设置是否向HttpURLConnection输出
            connection.setDoOutput(true);
            // 设置是否从httpUrlConnection读入
            connection.setDoInput(true);
            // 设置是否使用缓存
            connection.setUseCaches(false);
            //设置参数类型是json格式
            connection.setRequestProperty("Content-Type", "application/json;charset=utf-8");
            connection.connect();
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("area", "zhejiang");
            paramMap.put("base64Code", base64Str);
            paramMap.put("taxNo", "111111");
            paramMap.put("captchaType", 1038);
            paramMap.put("captchaMinLength", 4);
            paramMap.put("captchaMaxLength", 4);

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
            writer.write(JSON.toJSONString(paramMap));
            writer.close();
            //读取URLConnection的响应
            InputStream ins = connection.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            while (true) {
                int rc = ins.read(buf);
                if (rc <= 0) {
                    break;
                } else {
                    bout.write(buf, 0, rc);
                }
            }
            ins.close();
            //结果输出
            res = new String(bout.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 获取短信验证码
     * @param telno
     * @return
     * @throws Exception
     */
    public static String phoneYzm(String telno) throws Exception {
    	return phoneYzm(telno, 1000L);
    }
    public static String phoneYzm(String telno,Long queryInterval) throws Exception {
        final long MAX_INTERVAL_TIME = 3 * 60 * 1000L;
        long time = System.currentTimeMillis();
        logger.info("yzm start : " + telno);
        final String template = "{\"phoneNum\":\"%s\",\"cid\":\"%s\",\"lsh\":\"%s\",\"telno\":\"%s\"}";
        final String data = String.format(template, "13X", "CID","LSH", telno);
        int i = 0;
        while (true) {
            i++;
            try {
                Thread.sleep(queryInterval);
            } catch (InterruptedException e) {
                // NOTHING
            }
            logger.info("{} - yzm request", i);
            String yzmResponse = requestAction(yzmUrl, data, false);
            logger.info("{} - yzm response : {}", i, yzmResponse);
            JSONObject yzmResponseObject;
            try {
                yzmResponseObject = JSONObject.parseObject(yzmResponse);
            } catch (Exception e) {
                throw new Exception("parse yzm response failed!");
            }
            String code = yzmResponseObject.getString("code");
            if (!"success".equalsIgnoreCase(code)) {
                logger.info("{} - yzm response not success!", i);
            } else {
                JSONObject yzmData = yzmResponseObject.getJSONObject("data");
                String yzm = yzmData.getString("vCode");
                long receiveTime = yzmData.getLongValue("serviceDate");
                long transferTime = yzmData.getLongValue("transferTime");
                if (System.currentTimeMillis() - time >= MAX_INTERVAL_TIME) {
                    throw new Exception("yzm expired!");
                }
                if (Math.abs(transferTime - receiveTime) < MAX_INTERVAL_TIME) {
                    logger.info("{} - yzm success : {}", i, yzm);
                    return yzm;
                }
            }
        }
    }

    private static String requestAction(String urlString, String data, boolean localSkip) {
        String response = "";
            HttpURLConnection connection = null;
            try {
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setReadTimeout(5 * 1000);
                connection.setConnectTimeout(3 * 1000);
                connection.setRequestProperty("Content-Encoding", "utf-8");
                connection.setRequestProperty("Content-Type", "application/json");
                byte[] request = data.getBytes(StandardCharsets.UTF_8);
                String contentLength = String.valueOf(request.length);
                connection.setRequestProperty("Content-Length", contentLength);
                try (OutputStream outputStream = connection.getOutputStream()) {
                    outputStream.write(request);
                    outputStream.flush();
                    int responseCode = connection.getResponseCode();
                    if (responseCode == 200) {
                        response = readResponse(connection.getInputStream());
                    } else {
                        response = readResponse(connection.getErrorStream());
                    }
                }
                logger.info("{} request success!", urlString);
            } catch (Exception e) {
                logger.error("{} request error!", urlString);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        return response;
    }

    private static String readResponse(InputStream inputStream) throws Exception {
        byte[] responseBytes = new byte[0];
        while (true) {
            byte[] readedBytes = new byte[1024];
            int readedCount = inputStream.read(readedBytes);
            if (readedCount <= 0) {
                break;
            }
            byte[] newBytes = new byte[responseBytes.length + readedCount];
            System.arraycopy(responseBytes, 0, newBytes, 0, responseBytes.length);
            System.arraycopy(readedBytes, 0, newBytes, responseBytes.length, readedCount);
            responseBytes = newBytes;
        }
        StringBuilder readResponse = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(responseBytes), StandardCharsets.UTF_8))) {
            String line;
            for (line = br.readLine(); line != null; line = br.readLine()) {
                readResponse.append(line).append("\r\n");
            }
        }
        return readResponse.toString();
    }



    /**
     * 参数信息
     * @return
     */
    private static Map<String, String> getParamMap() {
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("user_name", "leo1988");
        paramMap.put("user_pw", "Admin@123");
        paramMap.put("yzm_minlen", "4");
        paramMap.put("yzm_maxlen", "4");
        paramMap.put("yzmtype_mark", "1038");
        paramMap.put("zztool_token", "");

        return paramMap;
    }

}
