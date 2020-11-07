package com.pangu.crawler.sbptpicture.service;

import com.pangu.crawler.framework.utils.StringUtils;
import com.pangu.crawler.sbptpicture.mongo.AsyncQueryBusinessPictureEntity;
import com.pangu.crawler.sbptpicture.mongo.AsyncQueryBusinessPictureOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReportService {

	@Autowired
    AsyncQueryBusinessPictureOperation asyncQueryBusinessPictureOperation;

	/**
	 * @param params
	 * @return
	 * @throws Exception
	 * @查询规则数据
	 */
	public List<AsyncQueryBusinessPictureEntity> queryHistoricalData(Map<String, String> params) throws Exception {
		Criteria cxyj =  new Criteria();
		if(StringUtils.isNotEmpty(params.get("jglx"))){
			cxyj.and("jglx").is(params.get("jglx"));
		}
		if(StringUtils.isNotEmpty(params.get("releationid"))){
			cxyj.and("releationid").is(params.get("releationid"));
		}
		if(StringUtils.isNotEmpty(params.get("name"))){
			cxyj.and("name").is(params.get("name"));
		}
		if(StringUtils.isNotEmpty(params.get("id"))){
			cxyj.and("_id").is(params.get("id"));
		}
		if(StringUtils.isNotEmpty(params.get("lsh"))){
			cxyj.and("lsh").is(params.get("lsh"));
		}
		List<AsyncQueryBusinessPictureEntity> data = asyncQueryBusinessPictureOperation.findByParam(cxyj);
		return data;
	}

	/**
	 * @param params
	 * @return
	 * @throws Exception
	 * @description 修改规则文件服务
	 */
	public Map<String, Object> savereportcation(Map<String, String> params) throws Exception {
		return asyncQueryBusinessPictureOperation.save(params);
	}

}