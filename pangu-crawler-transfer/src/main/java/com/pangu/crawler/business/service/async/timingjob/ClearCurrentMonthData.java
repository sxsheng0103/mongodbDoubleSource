/*
package com.pangu.crawler.business.service.async.timingjob;

import java.time.LocalDate;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.pangu.crawler.business.dao.mongoDB.operation.CurrentMonthDataOperation;

import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class ClearCurrentMonthData {
	
	@Autowired
	CurrentMonthDataOperation currentmonthdataoperation;
	
	@Scheduled(cron = "0 0 1 ? * 7")
//	@Scheduled(cron = "5 * * * * ? ")
	public void ClearCurrentMonthData() {
		log.info("清理数据任务开始执行");
		try {
			LocalDate date=LocalDate.now();
			ArrayList<String> lshs=currentmonthdataoperation.ClearCurrentMonthData(date.getYear()+"-"+(date.getMonthValue()));
			log.info("清理数据任务执行完成[{}]",lshs);
		} catch (Exception e) {
			log.error("清理数据任务发生异常",e);
		}
		
	}
	
}
*/
