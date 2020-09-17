import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.PanguCrawlerBootStrap;
import com.pangu.crawler.business.service.neimeng.NeimengLogin;
import com.pangu.crawler.business.service.shaanxi.ShaanxiLogin;
import com.pangu.crawler.business.service.shaanxi.ShaanxiService;
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
public class NeimengTest {

    @Autowired
    private NeimengLogin neimengLogin;
    @Autowired
    private ShaanxiService shaanxiService;

    @Test
    public void loginTest() throws Exception {
        String trace ="1111";
        String nsrsbh ="91610600730419487C";
        JSONObject json = new JSONObject();
        json.put("userName","18647612623");
        json.put("passwd","yzchaha5159");
        json.put("gsNsrmc","中国联合网络通信有限公司赤峰市分公司");
        neimengLogin.login(trace,nsrsbh,json);
        //shaanxiService.getRegister(trace,nsrsbh);
    }

    @Test
    public void getImgCode() throws Exception {
        //System.out.println(shaanxiLogin.getYzmImageCode("dddd","dddd"));;
    }

}
