
package com.pangu.crawler.business.service.async.timingjob;

import com.pangu.crawler.transfer.controller.TransferTest;
import com.pangu.crawler.transfer.utils.TempUserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class backRuleFiles{
	@Autowired
	TransferTest transfertest;
	@Scheduled(cron = "0 0/30 9-20 * * ? ")//fixedRate = 5000
	public void backRuleFiles() {
		try {
			if(TempUserInfo.activeConfigtype.equals("prod")){
				transfertest.TransferServiceDispatch();
			}
		} catch (Exception e) {
			log.error("查询发生未知异常:", e);
		}
	}

}

