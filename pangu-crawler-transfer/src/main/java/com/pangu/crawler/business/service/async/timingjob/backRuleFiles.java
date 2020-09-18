
package com.pangu.crawler.business.service.async.timingjob;

import com.pangu.crawler.transfer.controller.TransferTest;
import com.pangu.crawler.transfer.utils.TempUserInfo;
import com.pangu.crawler.transfer.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.system.ApplicationHome;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class backRuleFiles{
	@Autowired
	TransferTest transfertest;
	String path = new ApplicationHome(this.getClass()).getSource().getAbsolutePath();
	@Scheduled(cron = "0 0/30 9-20 * * ? ")//fixedRate = 5000
	public void backRuleFiles() {
		try {
			if(TempUserInfo.activeConfigtype.equals("prod")){
				log.info(TimeUtils.getCurrentDateTime(null,TimeUtils.sdf1)+"备份数据开始");
				transfertest.TransferServiceDispatch(path);
			}
		} catch (Exception e) {
			log.error("查询发生未知异常:", e);
		}
	}

}

