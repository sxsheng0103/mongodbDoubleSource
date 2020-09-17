import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.PanguCrawlerBootStrap;
import com.pangu.crawler.business.service.jilin.JilinService;
import com.pangu.crawler.business.service.liaoning.LiaoningService;
import com.pangu.crawler.business.service.liaoning.help.LiaoningQueryServiceHelper;
import com.pangu.crawler.framework.cookie.CookieOperation;
import com.pangu.crawler.framework.host.HostBean;
import com.pangu.crawler.framework.http.HttpManager;
import com.pangu.crawler.framework.model.JsonObjectKey;
import com.pangu.crawler.framework.model.ResultBean;
import com.pangu.crawler.framework.service.ServiceFirstArg;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 江西接口测试
 * @Author 张智星
 * @Date 2019/12/23
 * @Version V1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PanguCrawlerBootStrap.class)
public class LiaoNingTest {

    @Autowired
    private LiaoningService liaoningService;

    @Autowired
    private LiaoningQueryServiceHelper liaoningQueryServiceHelper;

    @Before
    public void init(){
        HostBean.instance.initial("default", new HashMap<String, String>(){{
            put("shandong", "https://etax.shandong.chinatax.gov.cn");
            put("hubei", "https://etax.hubei.chinatax.gov.cn");
            //put("jilin", "https://etax.jilin.chinatax.gov.cn:10812");
            put("liaoning","https://etax.liaoning.chinatax.gov.cn");
        }});

    }

    @Test
    public void loginTest() throws Exception {
        JSONObject json = new JSONObject();
        json.put(JsonObjectKey.USER_NAME.getCode(),"91211200794840457U");
        json.put(JsonObjectKey.PASSWORD.getCode(),"abc*6688");
        ResultBean res = liaoningService.login(new ServiceFirstArg("dewrewr", "91211200794840457U", false, CookieOperation.NONE), json);
        System.out.println(res);
    }

    @Test
    public void queryRegister()throws Exception{
        liaoningService.queryRegister(new ServiceFirstArg("dewrewr", "91211200794840457U", false, CookieOperation.LOAD),null);
    }

    @Test
    public void queryTaxsDetermine()throws Exception{
        liaoningService.queryTaxsDetermine(new ServiceFirstArg("dewrewr", "91211200794840457U", false, CookieOperation.LOAD),null);
    }

    @Test
    public void queryHistoricalData() throws Exception {
        JSONObject json = new JSONObject();
        json.put(JsonObjectKey.SKSSQQ.getCode(),"2020-01-01");
        json.put(JsonObjectKey.SKSSQZ.getCode(),"2020-12-31");
        json.put(JsonObjectKey.SZ.getCode(),"");
        liaoningService.queryHistoricalData(new ServiceFirstArg("dewrewr", "91211200794840457U", false, CookieOperation.LOAD),json);
    }



}
