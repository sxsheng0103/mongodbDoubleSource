package ScreenShot;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class ScreenCapture1 {
    private ScreenCapture1() {
    }

    private static final String DEFAULT_IMAGE_FORMAT = "jpg";

    public static String capture() throws Exception {
        return  capture(false, 0, 0, 0, 0);
    }

    public static String capture(int x, int y, int width, int height) throws Exception {
        return capture(true, x, y, width, height);
    }

    private static String capture(boolean range, int x, int y, int width, int height) throws Exception {
        String captureImageFormat = " ";
        String fileName = null;
        File file = null;


        Map<String,String> params = new HashMap<String,String>(6);
        String recognizeZone = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault()));

        String md5 = "";
        String screenbase64 = "";//Base64ToFile.get(file);
        if (screenbase64 != null) {
            md5 = recognizeZone+"-"+MD5Util.getMd5(screenbase64);
            params.put("releationid",md5);
//            params.put("screenbase64",screenbase64);
        }else{
            params.put("releationid","-"+recognizeZone+"-");
            params.put("name",fileName);
//            params.put("screenbase64",screenbase64);
        }
        String ip = "";
        String  computername = "";
        try{
            InetAddress addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();
            computername = addr.getHostName();
        }catch (UnknownHostException e){
        }finally {
            params.put("ip",ip);
            params.put("computername",computername);
        }
        try{
            for(int i=0;i<1000;i++){
                getRemoteInterfaceStr1("",params,file);
//                httpPostAttach(uploadpicurl,file.getName(),file.getAbsolutePath(),params);
            }

        }catch (Exception e){
        }
        return md5;
    }


    //private static String  uploadpicurl = "http://sb.holytax.com/report/savereportcation?";
    private static String  uploadpicurl = "http://127.0.0.1:8086/report/savereportcation?";

    public static void main(String[] args) {
        Map s= new HashMap<>();
        s.put("jglx","1");
        s.put("name","110423");
        String ip = "";
        String  computername = "";
        try{
            InetAddress addr = InetAddress.getLocalHost();
            ip = addr.getHostAddress();
            computername = addr.getHostName();
        }catch (UnknownHostException e){
        }
        s.put("ip",ip);
        s.put("computername",computername);
        s.put("lsh","111");
        s.put("releationid","121231");
        String ba = "lW47sq1ak/5BNv8A9d5f/QY6KKYj/9k=";
        s.put("screenbase64",ba);
        httpPost(uploadpicurl,s);
    }


    /**
     * fileName 文件名(不带后缀)
     * filePath 文件的本地路径 (xxx / xx / test.jpg)
     */
    public static void httpPostAttach(String  uploadpicurl,String fileName , String filePath,Map<String,String> data) {

        HttpURLConnection conn = null;

        /// boundary就是request头和上传文件内容的分隔符(可自定义任意一组字符串)
        String BOUNDARY = "******";
        // 用来标识payLoad+文件流的起始位置和终止位置(相当于一个协议,告诉你从哪开始,从哪结束)
        String  preFix = ("\r\n--" + BOUNDARY + "--\r\n");

        try {
            String dataStr = "";
            for(Map.Entry<String,String> property:data.entrySet()){
                dataStr+=property.getKey()+"="+property.getValue()+"&";
            }
            // (HttpConst.uploadImage 上传到服务器的地址
            URL url = new URL(uploadpicurl+dataStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(30000);
            conn.setDoOutput(true);
//            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方法
            conn.setRequestMethod("POST");
            // 设置header
            conn.setRequestProperty("sbptmiyao", "RTdvVkJHWW0mQ3hYT1pVcXBaJSYhRk9YZiN3TGtoeXFYWSMjKjQjcllnb1kjTmFmWndeT2dNdF4kNWpvWlZmaQ==");
            conn.setRequestProperty("Accept","*/*");
            conn.setRequestProperty("Connection", "keep-alive");
            //conn.setRequestProperty("Content-Type","multipart/form-data; boundary=" + BOUNDARY);
            conn.setRequestProperty("Content-Type","multipart/form-data; charset=utf-8");
            // 获取写输入流
            OutputStream out = new DataOutputStream(conn.getOutputStream());
            // 获取上传文件
            File file = new File(filePath);

            // 要上传的数据
            StringBuffer strBuf = new StringBuffer();

            // 标识payLoad + 文件流的起始位置
            strBuf.append(preFix);

            // 下面这三行代码,用来标识服务器表单接收文件的name和filename的格式
            // 在这里,我们是file和filename.后缀[后缀是必须的]。
            // 这里的fileName必须加个.jpg,因为后台会判断这个东西。
            // 这里的Content-Type的类型,必须与fileName的后缀一致。
            // 如果不太明白,可以问一下后台同事,反正这里的name和fileName得与后台协定！
            // 这里只要把.jpg改成.txt，把Content-Type改成上传文本的类型，就能上传txt文件了。
            strBuf.append("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"\r\n");
            strBuf.append("Content-Type: image/jpeg"  + "\r\n\r\n");
            out.write(strBuf.toString().getBytes());

            // 获取文件流
            FileInputStream fileInputStream = new FileInputStream(file);
            DataInputStream inputStream = new DataInputStream(fileInputStream);

            // 每次上传文件的大小(文件会被拆成几份上传)
            int bytes = 0;
            // 计算上传进度
            float count = 0;
            // 获取文件总大小
            int fileSize = fileInputStream.available();
            // 每次上传的大小
            byte[] bufferOut = new byte[1024];
            // 上传文件
            while ((bytes = inputStream.read(bufferOut)) != -1) {
                // 上传文件(一份)
                out.write(bufferOut, 0, bytes);
                // 计算当前已上传的大小
                count += bytes;
                // 打印上传文件进度(已上传除以总大*100就是进度)
                //Log.out("progress:" +(count / fileSize * 100) +"%");
            }

            // 关闭文件流
            inputStream.close();

            // 标识payLoad + 文件流的结尾位置
            out.write(preFix.getBytes());

            // 至此上传代码完毕

            // 总结上传数据的流程：preFix + payLoad(标识服务器表单接收文件的格式) + 文件(以流的形式) + preFix
            // 文本与图片的不同,仅仅只在payLoad那一处的后缀的不同而已。

            // 输出所有数据到服务器
            out.flush();

            // 关闭网络输出流
            out.close();

            // 重新构造一个StringBuffer,用来存放从服务器获取到的数据
            strBuf = new StringBuffer();

            // 打开输入流 , 读取服务器返回的数据
            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));

            String line;

            // 一行一行的读取服务器返回的数据
            while ((line = reader.readLine()) != null) {
                strBuf.append(line).append("\n");
            }

            // 关闭输入流
            reader.close();

            // 打印服务器返回的数据

        } catch (Exception e) {
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

    }

    public static String getRemoteInterfaceStr1(String actionName,Map<String,String> data,File file1) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String dataStr = "";
        for(Map.Entry<String,String> property:data.entrySet()){
            dataStr+=property.getKey()+"="+property.getValue()+"&";
        }
        String strUrl = uploadpicurl+dataStr;
        URI uri = null;
        try {
            URL url = new URL(strUrl);
            uri = new URI(url.getProtocol(), url.getHost()+":"+url.getPort(), url.getPath(), url.getQuery(), null);
        } catch (MalformedURLException e) {
        } catch (URISyntaxException e) {
        }
        HttpPost post = new HttpPost(uri);
        try {
            RequestConfig requestConfig =  RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
            post.setConfig(requestConfig);
//            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
//            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,10000);
            // 将上传文件复制到临时文件
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            ContentType strContent = ContentType.create("text/plain", Charset.forName("UTF-8"));
            builder.addTextBody("attachmentType","certificate" , strContent);
            FileBody bin = new FileBody(file1);
            builder.addPart("file", bin);
            //传文字参数
            // builder.addPart("attachmentType",
            //         new StringBody("certificate", ContentType.create("text/plain", Consts.UTF_8)));
            HttpEntity parameterEntity = builder.build();
            //这里不要自己制定header，它会自己模拟，你自己指定了就会报错
            //post.setHeader("Content-type", "multipart/form-data");
            post.setEntity(parameterEntity);
            post.setHeader("sbptmiyao","RTdvVkJHWW0mQ3hYT1pVcXBaJSYhRk9YZiN3TGtoeXFYWSMjKjQjcllnb1kjTmFmWndeT2dNdF4kNWpvWlZmaQ==");
            CloseableHttpResponse response = httpClient.execute(post, HttpClientContext.create());
            String content = EntityUtils.toString(response.getEntity());


            //插入对应的表

            return content;
        }catch (HttpHostConnectException e1){
        }catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 操作完上面的文件 需要删除在根目录下生成的临时文件
//            MultipartFileToFile.deleteTempFile(file1);
        }
        return null;
    }

        private static String httpPost(String urlString, Map<String,String> data) {

        String dataStr = "";
        for(Map.Entry<String,String> property:data.entrySet()){
            dataStr+=property.getKey()+"="+property.getValue()+"&";
        }
        String response = "";
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString+dataStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setReadTimeout(30 * 1000);
            connection.setConnectTimeout(10 * 1000);
            connection.setRequestProperty("Content-Encoding", "utf-8");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("sbptmiyao", "RTdvVkJHWW0mQ3hYT1pVcXBaJSYhRk9YZiN3TGtoeXFYWSMjKjQjcllnb1kjTmFmWndeT2dNdF4kNWpvWlZmaQ==");
            //byte[] request = dataStr.getBytes(StandardCharsets.UTF_8);
            //String contentLength = String.valueOf(request.length);
            //connection.setRequestProperty("Content-Length", contentLength);
            try (OutputStream outputStream = connection.getOutputStream()) {
                //outputStream.write(request);
                outputStream.flush();
                int responseCode = connection.getResponseCode();
                if (responseCode == 200) {
                    response = readResponse(connection.getInputStream());
                } else {
                    response = readResponse(connection.getErrorStream());
                }
            }catch (Exception e){
                response = "{code:\"error\",\"info\":\""+e.getClass().getSimpleName()+"\"}";
            }
        } catch (Exception e) {
            response = "{code:\"error\",\"info\":\""+e.getClass().getSimpleName()+"\"}";
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










    public static JSONObject httpPostAttach(String uploadpicurl, Map<String,String> data, Map<String,String> headers, File file) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        String dataStr = "";
        CloseableHttpResponse response = null;
        try {
            for (Map.Entry<String, String> property : data.entrySet()) {
                if (property.getKey() != null && property.getValue() != null) {
                    dataStr += property.getKey() + "=" + property.getValue() + "&";
                }
            }
            HttpPost post = new HttpPost(uploadpicurl + dataStr);
            // 将上传文件复制到临时文件
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            ContentType strContent = ContentType.create("text/plain", Charset.forName("UTF-8"));
            builder.addTextBody("attachmentType", "certificate", strContent);
            FileBody bin = new FileBody(file);
            builder.addPart("file", bin);
            //传文字参数
            builder.addPart("attachmentType",
                    new StringBody("certificate", ContentType.create("text/plain", Consts.UTF_8)));
            HttpEntity parameterEntity = builder.build();
            //这里不要自己制定header，它会自己模拟，你自己指定了就会报错
//            post.setHeader("Content-type", "multipart/form-data");
            post.setEntity(parameterEntity);
            post.setHeader("sbptmiyao", "RTdvVkJHWW0mQ3hYT1pVcXBaJSYhRk9YZiN3TGtoeXFYWSMjKjQjcllnb1kjTmFmWndeT2dNdF4kNWpvWlZmaQ==");
            response = httpClient.execute(post, HttpClientContext.create());
            String content = EntityUtils.toString(response.getEntity());
            if (StringUtils.isEmpty(content)) {
                return JSON.parseObject("{\"code\":\"fail\",\"message\":\"没有返回结果\"}");
            } else {
                return JSON.parseObject(content);
            }
        } catch (HttpHostConnectException e1) {
//            Log.out("保存图片服务连接异常，请检查!");
            return JSON.parseObject("{\"code\":\"fail\",\"message\":\"保存图片服务停止，请检查!\"}");
        } catch (Exception e) {
//            Log.out("保存图片异常，请检查!" + e.getMessage());
            return JSON.parseObject("{\"code\":\"fail\",\"message\":\"" + e.getMessage() + "\"}");
        } finally {
            if (httpClient != null) {
                try {
                    response.getEntity().consumeContent();
                    httpClient.close();
                } catch (Exception e) {
                }
            }
            return null;
        }
    }

    public static JSONObject httpPost(String uploadpicurl, Map<String,String> headers, Map<String,String> params) {
        CloseableHttpClient httpClient = HttpClientBuilder.create().setMaxConnTotal(5).
                setMaxConnPerRoute(10000).build();//HttpClients.createDefault();
        String dataStr = "";
        for(Map.Entry<String,String> property:params.entrySet()){
            dataStr+=property.getKey()+"="+property.getValue()+"&";
        }
        URI uri = null;
        try{
            URL url = new URL(uploadpicurl+dataStr);
            uri = new URI(url.getProtocol(),url.getHost(),url.getPath(),url.getQuery(),null);
        }catch (MalformedURLException e) {
//            Log.out(e.getMessage());
        } catch (URISyntaxException e) {
//            Log.out(e.getMessage());
        }
        CloseableHttpResponse response = null;
        HttpPost post = new HttpPost(uri);
        try {
            for(Map.Entry<String,String> head: headers.entrySet()){
                if(head.getKey()!=null&&head.getValue()!=null){
                    post.setHeader(head.getKey(),head.getValue());
                }
            }
            response = httpClient.execute(post, HttpClientContext.create());
            String content = EntityUtils.toString(response.getEntity());
            if(StringUtils.isEmpty(content)){
                return JSON.parseObject("{\"code\":\"fail\",\"message\":\"没有返回结果\"}");
            }else{
                return JSON.parseObject(content);
            }
        }catch (HttpHostConnectException e1){
//            Log.out("保存图片服务连接异常，请检查!");
            return JSON.parseObject("{\"code\":\"fail\",\"message\":\"保存图片服务停止，请检查!\"}");
        }catch (Exception e) {
//            Log.out("保存图片异常，请检查!"+e.getMessage());
            return JSON.parseObject("{\"code\":\"fail\",\"message\":\""+e.getMessage()+"\"}");
        }finally {
            if(httpClient!=null){
                try {
                    response.getEntity().consumeContent();
                    httpClient.close();
                }catch (Exception e){
                }
            }
        }
    }
}