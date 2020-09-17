import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.PanguCrawlerBootStrap;
import com.pangu.crawler.business.service.xinjiang.XinjiangLogin;
import com.pangu.crawler.business.service.xinjiang.XinjiangService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description: 新疆接口测试
 * @Author 张智星
 * @Date 2019/12/23
 * @Version V1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PanguCrawlerBootStrap.class)
public class XinjiangTest {

    @Autowired
    private XinjiangLogin xinjiangLogin;

    @Autowired
    private XinjiangService xinjiangService;

    @Test
    public void loginTest() throws Exception {
        String trace ="1321321321111";
        String nsrsbh ="91650000726988580W";
        JSONObject json = new JSONObject();
        json.put("userName","91650000726988580W");
        json.put("phone","15683450911");
        xinjiangLogin.login(trace,nsrsbh,json);
    }

}
