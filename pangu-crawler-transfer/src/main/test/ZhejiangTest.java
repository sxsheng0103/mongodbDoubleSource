import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.PanguCrawlerBootStrap;
import com.pangu.crawler.business.service.liaoning.LiaoningService;
import com.pangu.crawler.business.service.liaoning.help.LiaoningQueryServiceHelper;
import com.pangu.crawler.business.service.zhejiang.ZhejiangService;
import com.pangu.crawler.framework.cookie.CookieOperation;
import com.pangu.crawler.framework.host.HostBean;
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

/**
 * @Description: 江西接口测试
 * @Author 张智星
 * @Date 2019/12/23
 * @Version V1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PanguCrawlerBootStrap.class)
public class ZhejiangTest {

    @Autowired
    private ZhejiangService zhejiangService;

    @Autowired
    private LiaoningQueryServiceHelper liaoningQueryServiceHelper;

    @Before
    public void init(){
        HostBean.instance.initial("default", new HashMap<String, String>(){{
            put("zhejiang", "https://etax.zhejiang.chinatax.gov.cn");

        }});

    }

    @Test
    public void loginTest() throws Exception {

    }

    @Test
    public void queryRegister()throws Exception{
    }

    @Test
    public void queryTaxsDetermine()throws Exception{
    }

    @Test
    public void queryHistoricalData() throws Exception {
        JSONObject json = new JSONObject();
        json.put("skssqq","2020-01-01");
        json.put("skssqz","2020-12-30");
        json.put("sz","10101");
        zhejiangService.queryHistoricalData(new ServiceFirstArg("dddd","91330000842918432C",false,CookieOperation.NONE),json);
    }



}
