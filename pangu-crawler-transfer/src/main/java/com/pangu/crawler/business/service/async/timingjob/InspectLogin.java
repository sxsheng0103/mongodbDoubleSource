/*
package com.pangu.crawler.business.service.async.timingjob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.pangu.crawler.business.dao.mongoDB.entity.AsyncQueryRegisterRequestEntity;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryErrorOperation;
import com.pangu.crawler.business.dao.mongoDB.operation.AsyncQueryRegisterRequestOperation;
import com.pangu.crawler.business.service.async.BusinessException;
import com.pangu.crawler.business.service.async.help.AsyncQueryServiceHelper;
import com.pangu.crawler.framework.utils.AppUtil;
import com.pangu.crawler.framework.utils.TraceHelp;

import lombok.extern.slf4j.Slf4j;

//@Component
@Slf4j
public class InspectLogin {
	@Autowired
	AsyncQueryRegisterRequestOperation asyncqueryregisterrequestoperation;
	@Autowired
	AsyncQueryServiceHelper asyncqueryservicehelper;

	@Autowired
	private AppUtil appUtil;

	@Scheduled(fixedRate = 5000)
	public void inspectLogin() {
		String lsh="";

		try {
			AsyncQueryRegisterRequestEntity data = asyncqueryregisterrequestoperation.getOneByState(1);
			if (data == null) {
				return;
			}
			lsh = data.getLsh();
			JSONObject json=asyncqueryservicehelper.entity2JSONObject(data);

			String nsrsbh=json.getString("nsrsbh");
			String nsrdq=json.getString("nsrdq");
			String trace = TraceHelp.uuid();
			try {
				if(appUtil.containsAsyncQueryInterface("AsyncQuery_"+nsrdq)) {
					boolean loginok=appUtil.getAsyncQueryInterface("AsyncQuery_"+nsrdq).checkCookieEffective(nsrsbh, trace, json);
					if(loginok) {
						data.setState(4);
						asyncqueryregisterrequestoperation.save(data);
						return;
					}
				}
				
				data.setState(2);
				asyncqueryregisterrequestoperation.save(data);
				return;
			} catch (Exception e) {
				log.error("处理InspectLogin发生异常:",e);
				data.setState(2);
				asyncqueryregisterrequestoperation.save(data);
				//AsyncQueryErrorOperation.saveInfo(lsh, "InspectLogin_2",null, e);
				return;
			}

		} catch (Exception e) {
			log.error("查询发生未知异常:", e);
			AsyncQueryErrorOperation.saveInfo(lsh, "InspectLogin_1",null, e);
		}
	}

}
*/
