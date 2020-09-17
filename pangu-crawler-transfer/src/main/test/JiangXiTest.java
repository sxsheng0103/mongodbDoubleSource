import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import com.pangu.crawler.PanguCrawlerBootStrap;
import com.pangu.crawler.business.service.jiangxi.JiangxiLogin;
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
public class JiangXiTest {

    @Autowired
    private JiangxiLogin jiangxiLogin;

    @Test
    public void loginTest() throws Exception {
        String trace ="1111";
        String nsrsbh ="913601007165061386";
        JSONObject json = new JSONObject();
        json.put("userName","913601007165061386");
        json.put("passwd","jxltnc1386");
        json.put("phone","18679141050");
        json.put("swjg","国家税务总局南昌市税务局第一税务分局");
        jiangxiLogin.login(trace,nsrsbh,json);
    }



}
