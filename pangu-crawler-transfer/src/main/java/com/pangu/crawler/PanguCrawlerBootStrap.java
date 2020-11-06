package com.pangu.crawler;

/*import com.pangu.crawler.framework.cookie.CookieExpiredChecker;
import com.pangu.crawler.framework.host.HostBean;
import com.pangu.crawler.framework.service.ServiceFirstArg;*/
import com.pangu.crawler.transfer.com.mongo.configuration.EnableMongoMultiDataSource;
import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.mongo.MongoDataAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

//
//@EnableAspectJAutoProxy
@SpringBootApplication (exclude = {MultipartAutoConfiguration.class,MongoAutoConfiguration.class,MongoDataAutoConfiguration.class})

@EnableAsync(proxyTargetClass=true)
//@EnableSwagger2
@EnableMongoMultiDataSource
//@EnableMongoAuditing
@EnableScheduling
@ComponentScan(value = "com.pangu.crawler")
public class PanguCrawlerBootStrap {

    private static final Logger logger = LoggerFactory.getLogger(PanguCrawlerBootStrap.class);

    public static void main(String[] args) {
    	/*HostBean.instance.initial("default", new HashMap<String, String>(){{
    		put("shandong", "https://etax.shandong.chinatax.gov.cn");
            put("zhejiang", "https://etax.zhejiang.chinatax.gov.cn");
            put("beijing", "https://etax.beijing.chinatax.gov.cn");
            put("liaoning","https://etax.liaoning.chinatax.gov.cn");
            put("jiangxi","https://etax.jiangxi.chinatax.gov.cn");
            put("hebei", "https://etax.hebei.chinatax.gov.cn");
            put("gansu", "https://etax.gansu.chinatax.gov.cn");
            put("yunnan", "https://etax.yunnan.chinatax.gov.cn");
            put("hainan", "https://etax.hainan.chinatax.gov.cn");
            put("shanxi", "https://etax.shanxi.chinatax.gov.cn");
            put("anhui", "https://etax.anhui.chinatax.gov.cn");
            put("chongqing", "https://etax.chongqing.chinatax.gov.cn");
            put("dalian", "https://etax.dalian.chinatax.gov.cn");
            put("shandong_lssj", "http://taxcloud.shandong.chinatax.gov.cn");
            put("shaanxi", "https://etax.shaanxi.chinatax.gov.cn");
            put("xinjiang", "https://etax.xinjiang.chinatax.gov.cn");
        }});*/
        SpringApplication.run(PanguCrawlerBootStrap.class, args);
        logger.info("                                                                                                                                           \n" +
                "               ___              _ _           _   _            ______             _   _               _____ _             _   _             \n"+
                "              / _ \\            | (_)         | | (_)           | ___ \\           | | (_)             /  ___| |           | | (_)            \n" +
                "             / /_\\ \\_ __  _ __ | |_  ___ __ _| |_ _  ___  _ __ | |_/ / ___   ___ | |_ _ _ __   __ _  \\ `--.| |_ __ _ _ __| |_ _ _ __   __ _ \n" +
                "             |  _  | '_ \\| '_ \\| | |/ __/ _` | __| |/ _ \\| '_ \\| ___ \\/ _ \\ / _ \\| __| | '_ \\ / _` |  `--. \\ __/ _` | '__| __| | '_ \\ / _` |\n" +
                "             | | | | |_) | |_) | | | (_| (_| | |_| | (_) | | | | |_/ / (_) | (_) | |_| | | | | (_| | /\\__/ / || (_| | |  | |_| | | | | (_| |\n" +
                "             \\_| |_/ .__/| .__/|_|_|\\___\\__,_|\\__|_|\\___/|_| |_\\____/ \\___/ \\___/ \\__|_|_| |_|\\__, | \\____/ \\__\\__,_|_|   \\__|_|_| |_|\\__, |\n" +
                "                   | |   | |                                                                   __/ |                                   __/ |\n" +
                "                   |_|   |_|                                                                  |___/                                   |___/ ");
    }

   /* @Bean
    public CookieExpiredChecker cookieExpiredChecker() {
        return new CookieExpiredChecker() {
            @Override
            public boolean preCheck(ApplicationContext context, ServiceFirstArg firstArg) {
                return true;
            }
            @Override
            public boolean postCheck(ApplicationContext context, ServiceFirstArg firstArg, Object resp) {
                return true;
            }
        };
    }*/

    /*@Bean
    public Docket controllerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(new ApiInfoBuilder().title("申报查询接口").version("版本号:1.0").build())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.pangu.crawler"))
                .paths(PathSelectors.any())
                .build();
    }
*/

    /**
     * http重定向到https
     * @return
     */
    @Bean
    public TomcatServletWebServerFactory servletContainer() {
        TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint constraint = new SecurityConstraint();
                constraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                constraint.addCollection(collection);
                context.addConstraint(constraint);
            }
        };
        tomcat.addAdditionalTomcatConnectors(httpConnector());
        return tomcat;
    }
    @Value("${http.port}")
    private Integer httprt;
    @Value("${server.port}")
    private Integer htsport;
    @Bean
    public Connector httpConnector() {
        Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
        connector.setScheme("http");
        //Connector监听的http的端口号
        connector.setPort(httprt);
        connector.setSecure(false);
        //监听到http的端口号后转向到的https的端口号
        connector.setRedirectPort(htsport);
        return connector;
    }
}
