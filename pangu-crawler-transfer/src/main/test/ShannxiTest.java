import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.PanguCrawlerBootStrap;
import com.pangu.crawler.business.controller.shaanxi.ShaanxiController;
import com.pangu.crawler.business.service.shaanxi.ShaanxiLogin;
import com.pangu.crawler.business.service.shaanxi.ShaanxiService;
import com.pangu.crawler.framework.model.JsonObjectKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description: 江西接口测试
 * @Author 张智星
 * @Date 2019/12/23
 * @Version V1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PanguCrawlerBootStrap.class)
public class ShannxiTest {

    @Autowired
    private ShaanxiController shaanxiController;

    @Test
    public void queryRegister() throws Exception {
        String trace ="1111";
        String nsrsbh ="91610600730419487C";
        JSONObject json = new JSONObject();
        json.put(JsonObjectKey.USER_NAME.getCode(),"91610600730419487C");
        json.put(JsonObjectKey.PASSWORD.getCode(),"Aa11223344");
        shaanxiController.login(trace,nsrsbh,json);
        shaanxiController.queryRegister(trace,nsrsbh,json);
    }


}
