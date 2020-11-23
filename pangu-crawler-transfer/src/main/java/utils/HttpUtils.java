package utils;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HttpUtils {


    public static void main(String[] args) throws IOException, TranscoderException {
        String input = "钢琴";
        // 需要爬取商品信息的网站地址
        String url = "https://list.tmall.com/search_product.htm?q=" + input;
//        getpicByname(input,"https://www.iconfont.cn/collections/detail?spm=a313x.7781069.1998910419.d9df05512&cid=29");
        //29,28,27,20,19,22664,19238,17895,1695716880, 16472, 15640, 13720, 12507,11607, 9402, 7077, 5673, 4491, 3287, 2706, 2257, 1584,1312, 918, 748, 599, 317, 58, 34,
        String num = " 33, 31";

        for(String n:num.trim().split(",")){
            geticond(n);
        }
    }
    public static  void geticond(String num) throws IOException, TranscoderException {
        System.setProperty("webdriver.chrome.driver", "C://chromedriver.exe");
        ChromeOptions options = new ChromeOptions();

		options.addArguments("disable-extensions"); // disabling extensions
		options.addArguments("disable-dev-shm-usage"); // overcome limited resource problems
		options.addArguments("no-sandbox");
        options.addArguments("start-maximized");
        // 加载驱动
        WebDriver driver = new ChromeDriver(options);

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);

        driver.get("https://www.iconfont.cn/collections/detail?spm=a313x.7781069.1998910419.d9df05512&cid="+num);
        /*driver.get("https://www.iconfont.cn/collections/index?spm=a313x.7781069.1998910419.da2e3581b&type=1&page=4");
        ArrayList<String> ss= new ArrayList<String>();
        for(WebElement e: driver.findElements(By.cssSelector("div[class='block-collection']>a"))){
              ss.add(e.getAttribute("href").substring(e.getAttribute("href").lastIndexOf("?cid=")+5));
        }*/
//driver.get("https://www.iconfont.cn/collections/index?spm=a313x.7781069.1998910419.da2e3581b&type=1")
        // 循环liList的数据（具体获取的数据值还得看doc的页面源代码来获取，可能稍有变动）
        List<WebElement> eles = driver.findElements(By.cssSelector("li[class^='J_icon_id']"));
        for (WebElement item : eles) {
            String svg = item.findElement(By.tagName("svg")).getAttribute("outerHTML");
            String title = item.findElement(By.cssSelector("span[class='icon-name']")).getAttribute("title");
            if(new File(addr+File.separator+num+"").isDirectory()){

            }else{
                new File(addr+File.separator+num+"").mkdirs();
            }
            SvgToPngUtil.transferSVGTOPNG(addr+File.separator+num+""+File.separator+title+".png",svg);
        }

    }
    public static  void getpicid(String input,String url) throws IOException {

        // 动态模拟请求数据
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
// 模拟浏览器浏览（user-agent的值可以通过浏览器浏览，查看发出请求的头文件获取）
        httpGet.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        CloseableHttpResponse response = null;
        try{
            response = httpclient.execute(httpGet);
        }catch (Exception e){
            try{
                response = httpclient.execute(new HttpGet(url.replace("https","http")));
            }catch (Exception e1){
                response = httpclient.execute(new HttpPost(url.replace("https","http")));
            }

        }

        // 获取响应状态码
        int statusCode = response.getStatusLine().getStatusCode();
        try {
            HttpEntity entity = response.getEntity();
            // 如果状态响应码为200，则获取html实体内容或者json文件
            if(statusCode == 200){
                String html = EntityUtils.toString(entity, Consts.UTF_8);
                // 提取HTML得到商品信息结果
                Document doc = null;
                // doc获取整个页面的所有数据
                doc = Jsoup.parse(html);
                //输出doc可以看到所获取到的页面源代码
//      System.out.println(doc);
                // 通过浏览器查看商品页面的源代码，找到信息所在的div标签，再对其进行一步一步地解析
                Elements imgList = doc.select("img");
                // 循环liList的数据（具体获取的数据值还得看doc的页面源代码来获取，可能稍有变动）
                for (Element item : imgList) {
                    String src = item.attr("src");
                    HttpPost post = new HttpPost("http:"+src);
                        response = httpclient.execute(post, HttpClientContext.create());
                        String content = EntityUtils.toString(response.getEntity());
//                    FileUtils.write(new File(addr+src.substring(0,src.lastIndexOf("/")).substring(src.substring(0,src.lastIndexOf("/")).lastIndexOf("/")+1)+File.separator+src.substring(src.lastIndexOf("/")+1)),content);
                    FileOutputStream out = null;
                    HttpURLConnection conn = null;
                    try {
                        URL url1 = new URL("http:"+src);
                        conn = (HttpURLConnection) url1.openConnection();
                        conn.setRequestMethod("GET");
                        conn.setReadTimeout(5 * 1000);
                        InputStream inputStream = conn.getInputStream();
                        // imgUrl.replaceAll("\\", "_").replaceAll("http:", "");
                        String fileName = addr+url.substring(url.lastIndexOf("?id=")+4,url.indexOf("&"))+File.separator+src.substring(src.lastIndexOf("/")+1);
                        if(new File(addr+url.substring(url.lastIndexOf("?id=")+4,url.indexOf("&"))).isDirectory()){

                        }else{
                            new File(addr+url.substring(url.lastIndexOf("?id=")+4,url.indexOf("&"))).mkdirs();
                        }
                        try{
                            out = new FileOutputStream(new File(fileName));
                        }catch (Exception ee){
                            ee.printStackTrace();
                        }


                        byte[] arr = new byte[1024];
                        int len = 0;
                        while ((len = inputStream.read(arr)) != -1) {
                            out.write(arr, 0, len);
                        }
                        System.out.println("=====处理完成====");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // 消耗掉实体
                EntityUtils.consume(response.getEntity());
            } else {
                // 消耗掉实体
                EntityUtils.consume(response.getEntity());
            }
        } finally {
            response.close();
        }
    }

  private static final String addr = "D:\\gangqin\\icon\\";

    public static  void getpicByname(String input,String url) throws IOException {

        // 动态模拟请求数据
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
// 模拟浏览器浏览（user-agent的值可以通过浏览器浏览，查看发出请求的头文件获取）
        httpGet.setHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
        CloseableHttpResponse response = httpclient.execute(httpGet);
        // 获取响应状态码
        int statusCode = response.getStatusLine().getStatusCode();
        try {
            HttpEntity entity = response.getEntity();
            // 如果状态响应码为200，则获取html实体内容或者json文件
            if(statusCode == 200){
                String html = EntityUtils.toString(entity, Consts.UTF_8);
                // 提取HTML得到商品信息结果
                Document doc = null;
                // doc获取整个页面的所有数据
                doc = Jsoup.parse(html);
                //输出doc可以看到所获取到的页面源代码
//      System.out.println(doc);
                // 通过浏览器查看商品页面的源代码，找到信息所在的div标签，再对其进行一步一步地解析
                Elements imgList = doc.select("div[class='product']");
                // 循环liList的数据（具体获取的数据值还得看doc的页面源代码来获取，可能稍有变动）
                for (Element item : imgList) {
                    // 商品ID
                    String a=item.selectFirst("a").attr("href");
                    //获取商品主图
                    getpicid("","https:"+a);
                }
                // 消耗掉实体
                EntityUtils.consume(response.getEntity());
            } else {
                // 消耗掉实体
                EntityUtils.consume(response.getEntity());
            }
        } finally {
            response.close();
        }
    }

}
